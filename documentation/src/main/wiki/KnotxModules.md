# Knot.x Module
Knot.x is composed of set of Verticles. The Vert.x provides a Service Factory functionality that allows to deploy Verticles from different sources such as classpath, Http, Maven, etc. (see [[Service Factories|http://vertx.io/docs/vertx-service-factory/java/]).
In any case the verticle need to be supplied with a corresponding configuration.
By default, Knot.x uses **java** service factory. It means during an Knot.x start the verticles is being looked up in classpath as java classes.
Each module is supplied with a default configuration that can be override on the application config file.

As mentioned in [[Knot.x Deployment|KnotxDeployment]], the list of modules must be specified in JSON file provided as `-conf` parameter when starting Knot.x application.
```json
{
  "modules": [
    "java:io.knotx.server.KnotxServerVerticle",
    "java:my.custom.ServiceVerticle"
  ]
}
```
In the example above, each module is prefixed with `java:` string. This tells Vert.x engine that Java classpath Verticle Factory should be used to resolve actual Verticle.

When Knot.x is starting with the above config, we're actually asking to deploy two verticles `io.knotx.server.KnotxServerVerticle` 
and `my.custom.ServiceVerticle` (`java:` prefix is mandatory to let the system know it need to look for them in the classpath).

During start of the Verticle, it's being supplied by default configuration, e.g. [[KnotxServerOptions|https://github.com/Cognifide/knotx/blob/feature/cleanup-knotx-configurations/knotx-server/src/main/asciidoc/dataobjects.adoc#knotxserveroptions]] for KnotxServerVerticle.
However, a default configuration can still be overridden in the application configuration file in the `config` section, e.g.:
```json
{
  "modules": [
    "java:io.knotx.server.KnotxServerVerticle"
  ],
  "config": {
    "java:io.knotx.server.KnotxServerVerticle": {
      "options": {
        "config": {
          "serverOptions": {
            "port": 6666
          }
        },
        "instances": 2
      }
    }
  }
}
```
See [[Knot.x Deployments|KnotxDeployment]] for details how to manage your configurations.

## How to create your service?
Assuming you're implementing your own Knot.x Verticle (either Knot or any kind of Adapter following the appropriate guides).
1. Create a configuration data object. You can follow the pattern used in [[KnotxerverOptions|]] that's pretty much is as follows:
```java
@DataObject(generateConverter = true, publicConverter = false)
public class MyVerticleOptions {
   public final static int DEFAULT_SAMPLE_PARAMETER = 1234;
   
   private int sampleParameter;
   
   public MyVerticleOptions() {
     init();
   }
   
   public MyVerticleOptions(MyVerticleOptions other) {
     //copy constructor impl
   }
   
   public MyVerticleOptions(JsonObject obj) {
     init();
     MyVerticleOptionsConverter.fromJson(obj, this);
   }
   
   public JsonObject toJson() {
       JsonObject json = new JsonObject();
       MyVerticleOptions.toJson(this, json);
       return json;
   }
   
   private init() {
     //Initiate a parameter value from System props if exists, otherwise use dafault value
     this.sampleParameter = Integer.getInteger("my.sampleParam", DEFAULT_SAMPLE_PARAMETER);
   }
  
   //Getter
   public int getSampleParameter() {
     return sampleParameter;
   }
   
   //Fluent setter
   public MyVerticleOptions setSampleParameter(int param) {
     this.sampleParameter = param;
     return this;
   }
}

```
- JSON converters of this class will be automatically generated (during compilation) if field names follows the [[Vert.x Data Objects|https://github.com/vert-x3/vertx-codegen#data-objects]] 
- Data object must have **Default & Copy Constructors**, **Constructor from JsonObject** and **toJson()** method
- Any defaults in the configuration need to be implemented in a data object
- If some of the configuration variables might need to be overridden through JVM system properties (e.g. -Dmy.setting=123) use a convenient Java methods, such as `Integer.getInteger("my.setting)` for integers (same methods available for Strings, Booleans, etc.)
- Use fluent setters

2. Implement your verticle that uses your configuration object
3. After building your project, put result JAR file into the Knot.x classpath ([[Knot.x Deployments|KnotxDeployment]]) and add your module name to the starter JSON
```json
{
  "modules": [
    "java:io.knotx.server.KnotxServerVerticle",
    "java:my.custom.ServiceVerticle"
  ]
}
```
4. If necessary, override the default configuration directly in starter JSON, or through JVM properties if implemented.
