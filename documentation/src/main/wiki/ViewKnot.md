# View Knot
View Knot is a [[Knot|Knot]] implementation responsible for asynchronous Adapter calls and handlebars 
template processing.

##How does it work?

### Handlebars template evaluation
At the heart of the Knot.x View Knot lies [Handlebars.js](http://handlebarsjs.com/). Knot.x utilizes 
its Java port - [Handlebars.java](https://github.com/jknack/handlebars.java) to compile and evaluate 
templates.

View Knot retrieves all [[dynamic fragments|Splitter]] from [[Knot Context|Knot]]. Then for each fragment
it evaluates Handlebars template fragment using fragment context. In this way data from other [[Knots|Knot]]
can be applied to Handlebars snippets.
Handlebars template evaluation process uses also responses from Adapter calls. Those calls are described
in the next section.

### Adapters calls
Besides handlebars template evaluation View Knot is responsible for asynchronous Adapters calls. Then
it collects responses from those Adapters and expose them to evaluation process. Let's describe how
Adapters are invoked with following example.

Adapters calls are defined both on template and Knot configuration layers:

First Knot View collects `data-service-{NAMESPACE}={ADAPTERNAME}` attributes which define accordingly:
 - namespace under which Adapter response will be available,
 - name of the Adapter tha will be called during snippet processing. 

Additionally with every Adapter `data-params-{NAMESPACE}={JSON DATA}` attribute can be defined 
which specifies parameters for Adapter call. An example `script` definition can look like:

```html
<script data-api-type="templating"
  data-service="first-service"
  data-service-second="second-service"
  data-params-second="{'path':'/overridden/path'}"
  type="text/x-handlebars-template">
```
View Knot will call two Adapters with names: `first-service` and `second-service`.

Now we need to combine the service name with Adapter service address. This link is configured within
Knot View configuration in `services` part. See example below:
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

Example Adapters responses for Handlebars template evaluation have the following format:
```
{  
  "_result": {
    "message":"this is webservice no. 1",
    ...
  },
  "_response": { 
    "statusCode":"200"
  },
  "second": { 
    "_result": {
      "message":"this is webservice no. 2",
    ...
    },
    "_response": { 
      "statusCode":"200"
    }
  }
}
```
The `first-service` Adapter call has no namespace - its data is available directly (without namespace).
The `second-service` Adapter call has *second* namespace defined in template so its results are 
wrapped with appropriate JSON node. Each Adapter call contains a `_result` part with the Adapter response 
(JSON object or array) and a `_response` part with the Adapter response status code.

This final Adapter results format is reflected in Handlebars templates:
```html
<div class="col-md-4">
  <h2>Snippet1 - {{second._result.message}}</h2>
  <div>Snippet1 - {{second._result.body.a}}</div>
  {{#string_equals second._response.statusCode "200"}}
    <div>Success! Status code : {{second._response.statusCode}}</div>
  {{/string_equals}}
</div>
```

#### Adapter Calls Caching
Template might consists of more than one Adapter call. It's also possible that there are multiple 
fragments on the page, each using same Adapter call. Knot.x does caching results of Adapter calls 
to avoid multiple calls for the same data.
Caching is performed within page request scope, this means another request will not get cached data.

## How to configure?
View Knot is deployed as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html), 
depending on how it's deployed. You need to supply **View Knot** configuration.

JSON presented below is an example how to configure Action Knot deployed as standalone fat jar:
```json
{
  "address": "knotx.knot.view",
  "template.debug": true,
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
When deploying **View Knot** using Knot.x starter verticle, configuration presented above should 
be wrapped in the JSON `config` section:
```json
"verticles" : {
  ...,
  "com.cognifide.knotx.knot.view.ViewKnotVerticle": {
    "config": {
      "PUT YOUR CONFIG HERE"
    }
  },
  ...,
}
```
Detailed description of each configuration option is described in the next subsection.

### View Knot options

Main View Knot options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Action Knot verticle. |
| `template.debug`            | `Boolean`                           | &#10004;       | Template debug enabled option.|
| `client.options`            | `String`                            | &#10004;       | JSON representation of [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) configuration for [HttpClient](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClient.html) |
| `services`                  | `Array of ServiceMetadata`          | &#10004;       | Array of [ServiceMetadata](https://github.com/Cognifide/knotx/blob/master/knotx-core/knotx-knot-view/src/main/java/com/cognifide/knotx/knot/view/ViewKnotConfiguration.java).|

Service (Adapter) metadata options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `name`                      | `String`                            | &#10004;       | Name of [[Adapter|Adapter]] which is referenced in `data-service-{NAMESPACE}={ADAPTERNAME}`. |
| `address`                   | `String`                            | &#10004;       | Event bus address of the **Adapter** verticle. |
| `params`                    | `JSON object`                       | &#10004;       | Default params which are sent to Adapter. |
| `cacheKey`                  | `String`                            |                | Cache key which is used for Adapters calls caching. **No** means that cache key has value `{NAME}|{PARAMS}` |


## How to extend?

### Extending handlebars with custom helpers

If the list of available handlebars helpers is not enough, you can easily extend it. To do this the 
following actions should be undertaken:

1. Create a class implementing ```com.cognifide.knotx.handlebars.CustomHandlebarsHelper``` interface. 
This interface extends [com.github.jknack.handlebars.Helper](https://jknack.github.io/handlebars.java/helpers.html)
2. Register the implementation as a service in the JAR file containing the implementation
    * Create a configuration file called META-INF/services/com.cognifide.knotx.handlebars.CustomHandlebarsHelper 
    in the same project as your implementation class
    * Paste a fully qualified name of the implementation class inside the configuration file. If you're 
    providing multiple helpers in a single JAR, you can list them in new lines (one name per line is allowed) 
    * Make sure the configuration file is part of the JAR file containing the implementation class(es)
3. Run Knot.x with the JAR file in the classpath

#### Example extension

Sample application contains an example custom Handlebars helper - please take a look at the implementation of ```BoldHelper```:
* Implementation class: ```com.cognifide.knotx.example.monolith.handlebars.BoldHelper```
* service registration: ```knotx-example-monolith/src/main/resources/META-INF/services/com.cognifide.knotx.handlebars.CustomHandlebarsHelper```
