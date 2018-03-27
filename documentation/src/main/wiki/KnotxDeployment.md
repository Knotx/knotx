# Deploying Knot.x with custom modules
Thanks to the modular architecture of Knot.x, there are many ways to deploy Knot.x for
the production usage. However, the easiest approach is to use Knot.x as one **fat** jar together with
jar files specific for your target implementation (such as custom [[Adapters|Adapter]], [[Knots|Knot]]
or Handlebars helpers). These jar files should be all available in the classpath.

## Recommended Knot.x deployment
For the purpose of this example let's assume you have `KNOTX_HOME` folder created on the host machine where Knot.x
is going to be run. Following simple steps is a recommended way
to deploy Knot.x with custom modules:

- Create a subfolder `KNOTX_HOME/app` and put **knotx-standalone-X.Y.Z-fat.jar** in that folder.

- If you have custom Handlebars helpers, you can put them in that folder as well (as JAR files).

- If you have project specific [[Adapters|Adapter]] or [[Knots|Knot]], you can put their jar files in the same folder.
You don't need to embed Knot.x dependencies (e.g. `knotx-common` and `knotx-adapter-api`) in your custom jar files.
They will be taken from **knotx-standalone-X.Y.Z-fat.jar**.

- Create your own configuration JSON (any location on the host). Use `knotx-standalone.json`
from the [latest release](https://github.com/Cognifide/knotx/releases) as a reference. In this example,
created file is named `knotx-starter.json` and is placed in `KNOTX_HOME`.

- Create your own logger configuration. See [[Knot.x Logging|Logging]] on how to do it.

At this step `KNOTX_HOME` should contain:
```
- app
  - custom-modules.jar
- config
  - knotx-starter.json
  - logback.xml
- knotx-standalone-X.Y.Z-fat.jar
```

To start Knot.x with custom modules, use following command

```
java -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory -Dlogback.configurationFile=config/logback.xml -jar knotx-standalone-X.Y.Z-fat.jar -conf config/knotx-starter.json -cp "app/*" 
```

The execution of Knot.x using a launcher as above it uses a following exit codes as specified in [Vert.x documentation|http://vertx.io/docs/vertx-core/java/#_launcher_and_exit_code].
Additionally, Knot.x adds following exit codes:
- `30` - If the configuration is missing or it's empty

### Vert.x metrics
You might want to enable Vert.x metrics in order to monitor how Knot.x performs.
Currently, it's possible to enable JMX metrics, so you can use any JMX tool, like JConsole, to inspect all the metrics Vert.x collects.

In order to enable it, add following JVM property when starting Knot.x
```
java -Dcom.sun.management.jmxremote -Dvertx.metrics.options.jmxEnabled=true -Dvertx.metrics.options.jmxDomain=knotx ...
```
Vert.x collects:
- Vert.x elements metrics (such as amount of verticles, worker pool sizes, etc.)
- Event bus metrics (such as bytes sent/received, messages delivered, etc.)
- HTTP Clients metrics (such as bytes sent/received, connections, amount of requests, per http code responses, etc.)
- HTTP Server metrics (such as bytes sent/received, connections, amount of requests, etc.)

For a detailed description of available metrics please check [Vert.x The Metrics](http://vertx.io/docs/vertx-dropwizard-metrics/java/#_the_metrics) page

| ! Warning |
|:------ |
| **We don’t recommend gathering metrics from your production environment. JMX’s RPC API is fragile and bonkers. However for development purposes and troubleshooting it can be very useful.** |

## How to configure ?
As mentioned above, the `knotx-starter.json` is the main configuration file describing what Knot.x modules need to be started as part of Knot.x.

`knotx-standalone.json` configuration available on GitHub looks like below
```json
{
  "modules": [
    "server=io.knotx.server.KnotxServerVerticle",
    "httpRepo=io.knotx.repository.http.HttpRepositoryConnectorVerticle",
    "fsRepo=io.knotx.repository.fs.FilesystemRepositoryConnectorVerticle",
    "splitter=io.knotx.splitter.FragmentSplitterVerticle",
    "assembler=io.knotx.assembler.FragmentAssemblerVerticle",
    "hbsKnot=io.knotx.knot.templating.HandlebarsKnotVerticle",
    "serviceKnot=io.knotx.knot.service.ServiceKnotVerticle",
    "actionKnot=io.knotx.knot.action.ActionKnotVerticle",
    "serviceAdapter=io.knotx.adapter.service.http.HttpServiceAdapterVerticle"
  ]
}
```
As you see, it simply have list of modules that Knot.x should start in the form of:
`<alias>=<verticle.class.name>`.

In order to reconfigure Knot.x to your needs, e.g. to set port of HTTP server, or HTTP headers that are
being passed, or addresses of the client services used for rendering dynamic content you can use your configuration json file.

Just add `config` section for each module (using **alias** as the reference) that needs default configuration to be modified. 
You only need to specify elements that should be changed. Follow the guide of each Verticle to see the supported parameters.

Some of the verticles, might respect System properties to alter some configurations. E.g. see the [[Servcer|Server]] 

### How to configure Knot.x in starter JSON ?
In your project specific `knots-starter.json` add `config` object. For each module you want to configure put a field with configuration object.
For instance, if you want to modify configuration of KnotxServer module, you can do it as follows:
```json
{
  "modules": [
    "server=io.knotx.server.KnotxServerVerticle",
    "httpRepo=io.knotx.repository.http.HttpRepositoryConnectorVerticle",
    "fsRepo=io.knotx.repository.fs.FilesystemRepositoryConnectorVerticle",
    "splitter=io.knotx.splitter.FragmentSplitterVerticle",
    "assembler=io.knotx.assembler.FragmentAssemblerVerticle",
    "hbsKnot=io.knotx.knot.templating.HandlebarsKnotVerticle",
    "serviceKnot=io.knotx.knot.service.ServiceKnotVerticle",
    "actionKnot=io.knotx.knot.action.ActionKnotVerticle",
    "serviceAdapter=io.knotx.adapter.service.http.HttpServiceAdapterVerticle"
  ],
  "config": {
    "server" : {
      "options": {
        "config": {
          "serverOptions": {
            "port": 9999
          }
        },
        "instances": 2
      }
    }
  }
}
```
Important things to remember:
- `options` field maps exactly to a [vert.x Deployment Options](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html) object.
It means, that you can specify here deployment options such as how many instances of that module should be deployed, etc.
- Inside `options` you can supply `config` object where you override configuration for the verticle provided by the module.
See Knot.x Verticle documentation to see what's available on each Verticle.

If you start Knot.x with the configuration as above, it will start all modules listed in the config, but the `server` will be deployed as:
- Two KnotxServer Verticle instances
- Both will listen on port 9999 (vert.x will do load balancing for you)


### What happens when config refers to non-existing module?
Let's assume that you work over a new `com.acme.MyCustomModuleVerticle` verticle and it will be implemented inside `custom-module.jar`. 
As mentioned above, you should do 2 things to start using it within Knot.x instance. 
You need to add it to the list of Knot.x modules in the main config file:

```json
{
  "modules": [
    "server=io.knotx.server.KnotxServerVerticle",
    ...
    "myFancyModule=com.acme.MyCustomModuleVerticle"
  ]
}
```

And add the `custom-module.jar` to the classpath. But what will happen, if you actually forgot to add `custom-module.jar` to the classpath?
Knot.x will start the instance with following error:

```
2018-01-01 15:11:22.003 [vert.x-eventloop-thread-0] ERROR i.k.launcher.KnotxStarterVerticle - Can't deploy myFancyModule=java:com.acme.MyCustomModuleVerticle: {}
...
```

and the list of deployment status at the end shows:
```
2018-02-21 15:28:46.419 [vert.x-eventloop-thread-0] INFO  i.k.launcher.KnotxStarterVerticle - Knot.x STARTED

		Failed deploying customKnot [java:com.acme.MyCustomModuleVerticle]
		Deployed fsRepo [java:io.knotx.repository.fs.FilesystemRepositoryConnectorVerticle] [ef35d8e8-887b-4f8b-bd2e-31943d8ff706]
		Deployed assembler [java:io.knotx.assembler.FragmentAssemblerVerticle] [0f5d74f4-2233-4937-b11c-d20d3e5451b1]
		...
```

Your Verticle instance not started as the `com.acme.MyCustomModuleVerticle` class didn't exist.
