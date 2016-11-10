# View Knot
View Knot is [[Knot|Knot]] implementation responsible for asynchronous Adapter calls and a handlebars 
template processing.

##How does it work?

### Handlebars template evaluation
At the heart of the Knot.x View Knot lies [Handlebars.js](http://handlebarsjs.com/). Knot.x utilizes 
its Java port - [Handlebars.java](https://github.com/jknack/handlebars.java) to compile and evaluate 
templates.

View Knot retrieves all [[dynamic fragments|Splitter]] from [[Knot Context|Knot]]. Then for each fragment
it evaluate Handlebars template fragment using fragment context. In this way data from other [[Knots|Knot]]
can be applied to Handlebars snippets.
Handlebars template evaluation process uses also responses from Adapter calls. Those calls are described
in the next section.

### Adapters calls
Besides handlebars template evaluation View Knot is responsible for asynchronous Adapters calls. Then
it collects responses from those Adapters and expose them to evaluation process. Let's describe how
Adapters are invoked with following example.

Adapters calls are defined both on template and Knot configuration layers:

First Knot View collects `data-service-{NAMESPACE}={ADAPTERNAME}` attributes which defines Adapter name
and namespace with which Adapter response will be available. Additionally with every Adapter attribute
can be defined `data-params-{NAMESPACE}={JSON DATA}` attribute which specifies parameters for Adapter call.
So an example `script` definition can look like:
```html
<script data-api-type="templating"
  data-service="first-service"
  data-service-second="second-service"
  data-params-second="{'path':'/overridden/path'}"
  type="text/x-handlebars-template">
```
View Knot will call two Adapters with names: first-service, second-service.

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
to Adapter. It can be overridden at template layer what in the example is configured for
*second-service* Adapter.

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
The `second-service` Adapter call has *second* namespace defined in template so its results are 
wrapped with appropriate JSON node. Each Adapter call contains a `_result` part with the Adapter response 
and a `_response` part with the Adapter response status code.

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
Caching is performed within one request only. It means second request will not get cached data.

## How to configure?

This section configures the Knot.x View Knot responsible for rendering page consists of Handlebars template using data from corresponding services. The config node consists of:
- **address** - event bus address of the verticle it listens on,
- **template.debug** - boolean flag to enable/disable rendering HTML comment entities around dynamic snippets,
- **client.options** - contains json representation of [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) configuration for [HttpClient](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClient.html), 
- **services** - an array of definitions of all service endpoints used by dynamic snippets.

There are two groups of services defined. Each one will be handled by a different server, i.e. all service requests which match the regular expression:
- `/service/mock/.*` will by handled by `localhost:3000`
- `/service/.*` will be handled by `localhost:8080`

The first matched service will handle the request or, if there's no service matched, the corresponding template's script block will be empty. Please note that in the near future it will be improved to define fallbacks in the template for cases when the service does not respond or cannot be matched.


## How to extend?

#TODO Finish configuration

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