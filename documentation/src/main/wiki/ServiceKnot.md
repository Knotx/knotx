# Service Knot
Service Knot is a [[Knot|Knot]] implementation responsible for asynchronous Adapter calls to fetch the
data that will be later used to compose page final markup with [[Handlebars Knot|HandlebarsKnot]].

##How does it work?
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
Service Knot is deployed using Vert.x service factory as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default configuration.

Default configuration shipped with the verticle as `io.knotx.ServiceKnot.json` file available in classpath.

```json
{
  "main": "com.cognifide.knotx.knot.service.ServiceKnotVerticle",
  "options": {
    "config": {
      "address": "knotx.knot.service",
      "services": [
        {
          "name": "mock",
          "address": "mock-service-adapter",
          "params": {
            "path": "/service/mock/.*"
          }
        }
      ]
    }
  }
}
```
In general, it:
- Listens on event bus address `knotx.knot.service` on messages to process
- It communicates with the [Service Adapter|ServiceAdapter] on event bus address `mock-service-adapter` for processing GET requests to the services
- It defines service adapter configuration

Detailed description of each configuration option is described in the next subsection.

### Service Knot options

Main Service Knot options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Service Knot verticle. |
| `services`                  | `Array of ServiceMetadata`          | &#10004;       | Array of [ServiceMetadata](https://github.com/Cognifide/knotx/blob/master/knotx-core/knotx-knot-view/src/main/java/com/cognifide/knotx/knot/service/ServiceKnotConfiguration.java).|

ServiceMetadata options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `name`                      | `String`                            | &#10004;       | Name of [[Adapter|Adapter]] which is referenced in `data-knotx-service-{NAMESPACE}={ADAPTERNAME}`. |
| `address`                   | `String`                            | &#10004;       | Event bus address of the **Adapter** verticle. |
| `params`                    | `JSON object`                       | &#10004;       | Json Object with default params which are sent to Adapter. |
| `cacheKey`                  | `String`                            |                | Cache key which is used for Adapters calls caching. **No** means that cache key has value `{NAME}|{PARAMS}` |
