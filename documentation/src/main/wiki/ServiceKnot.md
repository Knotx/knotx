# Service Knot
Service Knot is a [[Knot|Knot]] implementation responsible for asynchronous Adapter calls to fetch the
data that will be later used to compose page final markup with [[Handlebars Knot|HandlebarsKnot]].

## How does it work?
Service Knot filters Fragments containing `services` in `data-knots-types` attribute (see
[[Knot Election Rule|Knot]]). Then for every Fragment it calls configured Adapters. At the end
it collects responses from those Adapters and expose them in [[Knot Context|Knot]]. Let's describe
how Adapters are invoked with following example.

Adapters calls are defined both on template and Knot configuration layers:

First Service Knot collects `data-knotx-service-{NAMESPACE}={ADAPTERNAME}` attributes which define accordingly:
 - namespace under which Adapter response will be available,
 - name of the Adapter tha will be called during snippet processing.

Additionally with every Adapter `data-knotx-params-{NAMESPACE}={JSON DATA}` attribute can be defined
which specifies parameters for Adapter call. An example `script` definition can look like:

```html
<script data-knotx-knots="services,handlebars"
  data-knotx-service="first-service"
  data-knotx-service-second="second-service"
  data-knotx-params-second='{"path":"/overridden/path"}'
  type="text/knotx-snippet">
```
Service Knot will call two Adapters with names: `first-service` and `second-service`.

Now we need to combine the service name with Adapter service address. This link is configured within
Service Knot configuration in `services` part. See example below:
```
"services": [
  {
    "name" : "first-service",
    "address" : "knotx.adapter.service.http",
    "params": {
      "path": "/service/mock/first.json"
    },
    "cacheKey": "first"
  },
  {
    "name" : "second-service",
    "address" : "knotx.adapter.service.http",
    "params": {
      "path": "/service/mock/second.json"
    }
  }
]
```
The configuration contains also params attribute which defines default parameter value which is passed
to Adapter. It can be overridden at template layer like in the example above. When `second-service`
Adapter will be called it will get `path` parameter from `params` with overridden value `{'path':'/overridden/path'}`
instead of default `"path": "/service/mock/second.json"`.

Now all Adapter calls are ready to perform. Knot.x fully uses asynchronous programming principles so
those calls have also asynchronous natures. It is visualized on diagram below.

[[assets/knotx-modules-advanced-request-flow.png|alt=Knot.x Request Flow]]

### Adapter Calls Caching
Template might consists of more than one Adapter call. It's also possible that there are multiple
fragments on the page, each using same Adapter call. Knot.x does caching results of Adapter calls
to avoid multiple calls for the same data.
Caching is performed within page request scope, this means another request will not get cached data.

## How to configure?
For all configuration fields and their defaults consult [ServiceKnotOptions](https://github.com/Cognifide/knotx/blob/master/documentation/src/main/cheatsheet/cheatsheets.adoc#serviceknotoptions)

In general, it:
- Listens on event bus address `knotx.knot.service` on messages to process
- It communicates with the [[Service Adapter|ServiceAdapter]] on event bus address `mock-service-adapter` for processing GET requests to the services
- It defines service adapter configuration

Detailed description of each configuration option is described in the next subsection.

### Vert.x Event Bus delivery options

While HTTP request processing, Service Knot calls Adapter / Adapters using 
[Vert.x Event Bus](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/EventBus.html). The `config` field can contain 
[Vert.x Delivery Options](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/DeliveryOptions.html) related to the event 
bus. It can be used to control the low level aspects of the event bus communication like timeouts, headers, message 
codec names.

The `deliveryOptions` need to be added in the following place, of the Service Knot configuration to define the 
timeout for the Adapter response.
```
{
  "options": {
    "config": {
      "deliveryOptions": {
        "timeout": 15000,
         ...
      },
      ...
```
