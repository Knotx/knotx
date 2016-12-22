# Service Knot
Service Knot is a [[Knot|Knot]] implementation responsible for asynchronous Adapter calls to fetch the
data that will be later used to compose page final markup with [[Handlebars Knot|HandlebarsKnot]].

##How does it work?
Service Knot retrieves [[dynamic fragments|Splitter]] from [[Knot Context|Knot]]. Then for every dynamic
fragment it calls configured Adapters. At the end it collects responses from those 
Adapters and expose them in [[Knot Context|Knot]]. Let's describe how Adapters are invoked with 
following example.

Adapters calls are defined both on template and Knot configuration layers:

First Service Knot collects `data-service-{NAMESPACE}={ADAPTERNAME}` attributes which define accordingly:
 - namespace under which Adapter response will be available,
 - name of the Adapter tha will be called during snippet processing. 

Additionally with every Adapter `data-params-{NAMESPACE}={JSON DATA}` attribute can be defined 
which specifies parameters for Adapter call. An example `script` definition can look like:

```html
<script data-knot-types="services,handlebars"
  data-service="first-service"
  data-service-second="second-service"
  data-params-second='{"path":"/overridden/path"}'
  type="text/x-handlebars-template">
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
Service Knot is deployed as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html), 
depending on how it's deployed. You need to supply **Service Knot** configuration.

JSON presented below is an example how to configure Service Knot deployed as standalone fat jar:
```json
{
  "address": "knotx.knot.service",
  "client.options": {
    "maxPoolSize": 1000,
    "keepAlive": false
  },
  "services": [
    {
      "name" : "mock",
      "address" : "mock-service-adapter",
      "config": {
        "path": "/service/mock/.*"
      }
    }
  ]
}
```
When deploying **Service Knot** using Knot.x starter verticle, configuration presented above should 
be wrapped in the JSON `config` section:
```json
"verticles" : {
  ...,
  "com.cognifide.knotx.knot.service.ServiceKnotVerticle": {
    "config": {
      "PUT YOUR CONFIG HERE"
    }
  },
  ...,
}
```
Detailed description of each configuration option is described in the next subsection.

### Service Knot options

Main Service Knot options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Service Knot verticle. |
| `client.options`            | `String`                            | &#10004;       | JSON representation of [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) configuration for [HttpClient](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClient.html) |
| `services`                  | `Array of ServiceMetadata`          | &#10004;       | Array of [ServiceMetadata](https://github.com/Cognifide/knotx/blob/master/knotx-core/knotx-knot-view/src/main/java/com/cognifide/knotx/knot/service/ServiceKnotConfiguration.java).|

Service (Adapter) metadata options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `name`                      | `String`                            | &#10004;       | Name of [[Adapter|Adapter]] which is referenced in `data-service-{NAMESPACE}={ADAPTERNAME}`. |
| `address`                   | `String`                            | &#10004;       | Event bus address of the **Adapter** verticle. |
| `params`                    | `JSON object`                       | &#10004;       | Default params which are sent to Adapter. |
| `cacheKey`                  | `String`                            |                | Cache key which is used for Adapters calls caching. **No** means that cache key has value `{NAME}|{PARAMS}` |
