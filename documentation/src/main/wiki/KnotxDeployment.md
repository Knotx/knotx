# Deploying Knot.x with custom modules
Thanks to the modular architecture of Knot.x, there are multiple approaches how to deploy Knot.x for 
the production usage. However, the easiest approach is to use Knot.x as one **fat** jar together with 
jar files specific for your target implementation (such as custom [[Adapters|Adapter]], [[Knots|Knot]] 
or Handlebars helpers) all available in classpath.

## Recommended Knot.x deployment
For this example purpose, assume you have folder created on your host machine where Knot.x 
is going to be run, let's assume it's `KNOTX_HOME`. Performing simple following steps is recommended way
to deploy Knot.x with custom modules:

- Create subfolder `KNOTX_HOME/app` and put there **knotx-standalone-X.Y.Z-fat.jar**.
- If you have custom Handlebars helpers, you can put it as JAR file here.
- If you have project specific [[Adapters|Adapter]] or [[Knots|Knot]], you can put their jar files here.
If you don't use external libraries, you don't have to use **fat** jar, all Knot.x dependencies 
(e.g. `knotx-common` and `knotx-adapter-api`) will be taken from **knotx-standalone-X.Y.Z-fat.jar**.
- Create your own configuration JSON (any location on the host) basing e.g. on `knotx-standalone.json` 
from the [latest release](https://github.com/Cognifide/knotx/releases). In this example, 
created file is named `knotx-custom.json` and is placed in `KNOTX_HOME`.
- Create your own [Logback logger configuration](http://logback.qos.ch/manual/configuration.html) 
(any location on the host) basing e.g. on `logback.xml` 
from the [latest release](https://github.com/Cognifide/knotx/releases).

At this step `KNOTX_HOME` should contain:
```
- app
    - knotx-standalone-X.Y.Z-fat.jar
    - custom-modules.jar
- knotx-custom.json
- logback.xml
```

To start Knot.x with custom modules, use following command

```
java -Dlogback.configurationFile=logback.xml -cp "app/*" com.cognifide.knotx.launcher.LogbackLauncher -conf knotx-custom.json
```

## Deployment options
Knot.x consists of multiple verticles. Configuration file enables using 
[vert.x Deployment Options](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html)
when configuring Knot.x instance.

To deploy e.g. `KnotxServerVerticle` with advanced options use following properties in your configuration JSON file:
```json
{
    ... 
  "com.cognifide.knotx.server.KnotxServerVerticle": {
    "config": {...},
    
    "instances": 2,
    "worker" : false,
    "multiThreaded": false,
    "isolationGroup": "null",
    "ha": false,
    "extraClasspath": [],
    "isolatedClasses": []
  }
    ...
}
```
- **instances** - number of verticle instances.
- **worker** - deploy verticle as a worker.
- **multiThreaded** - deploy verticle as a multi-threaded worker.
- **isolationGroup** - array of isolation group.
- **ha** - deploy verticle as highly available.
- **extraClasspath** - extra classpath to be used when deploying the verticle.
- **isolatedClasses** - array of isolated classes.
