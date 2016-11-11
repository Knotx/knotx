## Deployment options
To deploy verticle with advanced options use following properties:
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

Read more about [vert.x Deployment Options](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html)

### Recommended Knot.x deployment
Thanks to the modular architecture of Knot.x, there are multiple approaches how to deploy Knot.x for the production usage. However, the easiest approach is to use Knot.x as one **fat** jar together with jar files specific for your target implementation (such as adapter services, Handlebars helpers) all available in classpath.
For instance:

- Assuming you have folder created on your host machine where Knot.x is going to be run, let's assume it's `KNOTX_HOME`
- Create subfolder `$KNOTX_HOME/lib` and put there **knotx-standalone-X.Y.Z-fat.jar**
- If you have custom Handlebars helpers [[See how to implement custom Handlebars helpers|TemplatingEngine#extending-handlebars-with-custom-helpers]], you can put it as JAR file here
- If you have project specific Verticles, e.g. adapter services, you can put their jar files here (**DO NOT PUT ANOTHER FAT JARS**)
- Create your own configuration JSON (any location on the host)
- Create your own Logback logger configuration (any location on the host)
- Start Knot.x using following command
```
java -Dlogback.configurationFile=/path/to/your/logback.xml -cp "lib/*" com.cognifide.knotx.launcher.LogbackLauncher -conf /path/to/your/setup.json
```


# TODO
Describe how to configure knot.x as a whole, recommended approach for deployment - as it's now, logging config etc.
