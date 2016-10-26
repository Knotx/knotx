# View Engine

At the heart of the Knot.x View Engine lies [Handlebars.js](http://handlebarsjs.com/). Knot.x utilizes its Java port - [Handlebars.java](https://github.com/jknack/handlebars.java) to compile and evaluate templates.

## Extending handlebars with custom helpers

If the list of available handlebars helpers is not enough, you can easily extend it. To do this the following actions should be undertaken:

1. Create a class implementing ```com.cognifide.knotx.handlebars.CustomHandlebarsHelper``` interface. This interface extends [com.github.jknack.handlebars.Helper](https://jknack.github.io/handlebars.java/helpers.html)
2. Register the implementation as a service in the JAR file containing the implementation
    * Create a configuration file called META-INF/services/com.cognifide.knotx.handlebars.CustomHandlebarsHelper in the same project as your implementation class
    * Paste a fully qualified name of the implementation class inside the configuration file. If you're providing multiple helpers in a single JAR, you can list them in new lines (one name per line is allowed) 
    * Make sure the configuration file is part of the JAR file containing the implementation class(es)
3. Run Knot.x with the JAR file in the classpath

### Example extension

Sample application contains an example custom Handlebars helper - please take a look at the implementation of ```BoldHelper```:
* Implementation class: ```com.cognifide.knotx.example.monolith.handlebars.BoldHelper```
* service registration: ```knotx-example-monolith/src/main/resources/META-INF/services/com.cognifide.knotx.handlebars.CustomHandlebarsHelper```

#### 1.3. Engine section
```json
  ...
  "com.cognifide.knotx.knot.view.ViewKnotVerticle": {
    "config": {
      "address": "knotx.knot.view",
      "template.debug": true,
      "client.options": {
        "maxPoolSize": 1000,
        "keepAlive": false
      },
      "services": [
        {
          "path": "/service/mock/.*",
          "domain": "localhost",
          "port": 3000,
          "allowed.request.headers": [
            "Content-Type",
            "X-*"
          ]          
        },
        {
          "path": "/service/.*",
          "domain": "localhost",
          "port": 8080,
          "allowed.request.headers": [
            "Content-Type",
            "X-*"
          ]          
        }
      ]
    }
  },
  ...,
```
This section configures the Knot.x View Knot responsible for rendering page consists of Handlebars template using data from corresponding services. The config node consists of:
- **address** - event bus address of the verticle it listens on,
- **template.debug** - boolean flag to enable/disable rendering HTML comment entities around dynamic snippets,
- **client.options** - contains json representation of [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) configuration for [HttpClient](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClient.html), 
- **services** - an array of definitions of all service endpoints used by dynamic snippets.

There are two groups of services defined. Each one will be handled by a different server, i.e. all service requests which match the regular expression:
- `/service/mock/.*` will by handled by `localhost:3000`
- `/service/.*` will be handled by `localhost:8080`

The first matched service will handle the request or, if there's no service matched, the corresponding template's script block will be empty. Please note that in the near future it will be improved to define fallbacks in the template for cases when the service does not respond or cannot be matched.