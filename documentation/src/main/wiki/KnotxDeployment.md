# Deploying Knot.x with custom modules
Thanks to the modular architecture of Knot.x, there are many ways to deploy Knot.x for 
the production usage. However, the easiest approach is to use Knot.x as one **fat** jar together with 
jar files specific for your target implementation (such as custom [[Adapters|Adapter]], [[Knots|Knot]] 
or Handlebars helpers). These jar files should be all available in the classpath.

## Recommended Knot.x deployment
For the purpose of this example let's assume you have `KNOTX_HOME` folder created on the host machine where Knot.x 
is going to be run. Following simple steps is a recommended way
to deploy Knot.x with custom modules:

- Create a subfolder `KNOTX_HOME/app` and put **knotx-standalone-X.Y.Z-fat.jar** in `KNOTX_HOME` folder.

- If you have custom Handlebars helpers, you can put them in that folder as well (as JAR files).

- If you have project specific [[Adapters|Adapter]] or [[Knots|Knot]], you can put their jar files in the same folder.
You don't need to embed Knot.x dependencies (e.g. `knotx-common` and `knotx-adapter-api`) in your custom jar files.
They will be taken from **knotx-standalone-X.Y.Z-fat.jar**.

- Create your own configuration JSON (any location on the host). Use `knotx-standalone.json` 
from the [latest release](https://github.com/Cognifide/knotx/releases) as a reference. In this example, 
created file is named `knotx-starter.json` and is placed in `KNOTX_HOME`.

- Create your own [Logback logger configuration](http://logback.qos.ch/manual/configuration.html) 
(any location on the host) basing e.g. on `logback.xml` 
from the [latest release](https://github.com/Cognifide/knotx/releases).

At this step `KNOTX_HOME` should contain:
```
- app
    - knotx-standalone-X.Y.Z-fat.jar
    - custom-modules.jar
- knotx-starter.json
- logback.xml
```

To start Knot.x with custom modules, use following command

```
java -Dlogback.configurationFile=logback.xml -cp "app/*" io.knotx.launcher.LogbackLauncher -conf knotx-starter.json
```

### Vert.x metrics
For monitoring purposes you might want to enable Vert.x metrics in order to monitor how Knot.x performs. 
Currently, it's possible to enable JMX metrics, so you can use any JMX tool, like Jconsole to inspect all the metrics Vert.x collects.

In order to enable it, add following JVM property when starting Knot.x
```
java -Dcom.sun.management.jmxremote -Dvertx.metrics.options.jmxEnabled=true -Dvertx.metrics.options.jmxDomain=knotx ...
```
Vert.x collects:
- Vert.x elements metrics (such as amount of verticles, worker pool sizes, etc.)
- Event bus metrics (such as bytes sent/received, messages delivered, etc. )
- HTTP Clients metrics (such as bytes sent/received, connections, amount of requests, per http code responses, etc. )
- HTTP Server metrics (such as bytes sent/received, connections, amount of requests, etc.)

For a detailed description about available metrics please check [Vert.x The Metrics](http://vertx.io/docs/vertx-dropwizard-metrics/java/#_the_metrics) page

| ! Warning |
|:------ |
| **We don’t recommend that you try to gather metrics from your production environment. JMX’s RPC API is fragile and bonkers. For development purposes and browsing, though, it can be very useful.** | 

## How to configure ?
As mentioned above, the knotx-starter.json is the main configuration file describing what Knot.x modules (verticles) need to be started as part of Knot.x.

`knotx-standalone.json` configuration available on Github looks like below
```json
{
  "modules": [
    "knotx:io.knotx.KnotxServer",
    "knotx:io.knotx.HttpRepositoryConnector",
    "knotx:io.knotx.FilesystemRepositoryConnector",
    "knotx:io.knotx.FragmentSplitter",
    "knotx:io.knotx.ServiceKnot",
    "knotx:io.knotx.ActionKnot",
    "knotx:io.knotx.HandlebarsKnot",
    "knotx:io.knotx.HttpServiceAdapter"
  ]
}
```
As you see, it simply have list of modules (verticles) that Knot.x should start. Out of the box, no other configuration is required as each verticle 
is shipped with default config.

However, at the production environment you must alter the configuration parameters such as port of HTTP server, or HTTP headers that are 
being passed, or finally addresses to the client services that are going to be used for rendering dynamic content.

Thanks to the Knot.x capabilities you can provide your configurations that modifies defaults. There are two ways:
- in your `knotx-starter.json` file add `config` section where you can put configuration for each Verticle you want to modify - but only elements that 
you need to change. Follow the guide of each Verticle to see the supported parameters.
- through JVM properties, you can provide single values for desired fields (e.g. http port) or even whole json objects from external JSON file. 
Any parameter provided through system properties will always override default and starter values.
- in some scenarios, it's also possible to create your own module that uses Knot.x core Verticle, where you can build the configuration from scratch. 
Such module might be also overridden using starter JSON and/or JVM properties.

### How to configure though starter JSON ?
In your project specific `knots-starter.json` add `config` object. Inside, put field with configuration object for each module you want to change configuration for.
For instance, you want to modify configuration of KnotxServer module, you can do it as follows:
```json
{
  "modules": [
    "knotx:io.knotx.KnotxServer",
    "knotx:io.knotx.HttpRepositoryConnector",
    "knotx:io.knotx.FilesystemRepositoryConnector",
    "knotx:io.knotx.FragmentSplitter",
    "knotx:io.knotx.ServiceKnot",
    "knotx:io.knotx.ActionKnot",
    "knotx:io.knotx.HandlebarsKnot",
    "knotx:io.knotx.HttpServiceAdapter"
  ],
  "config": {
    "knotx:io.knotx.KnotxServer" : {
      "options": {
        "config": {
          "httpPort": 9999
        },
        "instances": 2
      }
    }
  }
}
```
Important things to remember:
- `options` field which maps exactly to a [vert.x Deployment Options](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html) object. 
It means, that you can specify here deployment options such as how many instances of that module should be deployed, etc.
- Inside `options` you can supply `config` object where you're actualy can provide configuration to the verticle represented by the module. 
See Knot.x Verticle documentation to see what's available on each Verticle. 

If you start Knot.x with the configuration as above, it will start all modules listed in the config, but the KnotxServer will be deployed as:
- Two instances
- HTTP Port it listens is 9999

### How to configure through JVM properties ?
In some cases, you might want to provide configuration parameters through JVM properties, e.g. you can have same config used on all environments, 
but you wanted to specify HTTP port of the server to be different on each host. 
In such case, you can simply provide env specific port as JVM property, e.g.  
```
-Dio.knotx.KnotxServer.options.config.httpPort=9999
```

Additionally, if you wanted to change more than one value, you can create separate JSON file with all parameters you want to modify, and inject that config using JVM property.

For instance, you want to have routing of KnotxServer defined in a separate JSON file, let call it `my-routing.json` that might looks like below:
```json
{
  "GET": [
    {
      "path": "/content/custom-path/.*",
      "address": "knotx.knot.service",
      "onTransition": {
        "next": {
          "address": "knotx.knot.handlebars"
        }
      }
    }
  ]
}
```
Then, you can use that config to override default routing as below:
```
-Dio.knotx.KnotxServer.options.config.routing=file:my-routing.json
```

### How to configure your own module ?
The last option to change Verticle configuration, is to redefine Knotx module by creating your own descriptor. 
The descriptor is simply a JSON file that must be available in classpath (e.g. in JAR file of your custom Verticle implementation, 
or inside folder where all JAR files you put on your installation).

Name of the descriptor file is important as it's a module name used in starter JSON.

For instance, you want to create KnotxServer configuration from scratch, ignoring default config available. 
All you have to do is, to create module descriptor file, e.g. `my.KnotxServer.json` with the content as below
```json
{
  "main": "io.knotx.server.KnotxServerVerticle",
  "options": {
    "config": {
      
    }
  }
}
```
Where:
- `main` is the fully qualified class name of the Verticle the module represents
- `options` object, where you can specify deployment options, and inside it a `config` object that should have Verticle specific configuration

Next step, is to use your new module in `knotx-starter.json`.
```json
{
  "modules": [
    "knotx:my.KnotxServer",
    "......"
  ],
  "config": {
    "knotx:my.KnotxServer": {
      "options": {
        "config": {
        
        }
      }
    }
  }
}
```
Finally, you can still do override of that config as described above, through starter JSON as in the example, or through JVM properties:

Single value:
```
-Dmy.KnotxServer.options.config.httpPort=9999
```

Or, whole JSON Object from external file
```
-Dmy.KnotxServer.options.config.routing=file:/path/to/my-routing.json
```
