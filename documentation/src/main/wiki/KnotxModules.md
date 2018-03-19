# Knot.x Module
Knot.x is composed of set of Verticles. The Vert.x provides a Service Factory functionality that which deploys a Verticle given the service(id) name. It allows to deploy such verticles from different sources such as classpath, Http, Maven, etc. (see [[Service Factories|http://vertx.io/docs/vertx-service-factory/java/]).
No matter which source of verticle was, it should be supplied with a corresponding configuration.

By default, Knot.x uses standard **java** service factory. It means that during a Knot.x initialization verticles are being looked up in classpath as java classes.
Each module is supplied with a default configuration that can be overriden on the application config file.

As mentioned in [[Knot.x Deployment|KnotxDeployment]], the list of modules must be specified in JSON file provided as `-conf` parameter when starting Knot.x application.
```json
{
  "modules": [
    "server=io.knotx.server.KnotxServerVerticle",
    "myService=my.custom.ServiceVerticle"
  ]
}
```
Each module definition has the following syntax:
`<alias>=[<factory>:]<verticle-name>`
Where:
- *<alias>* is simply a convenient name of the module, used to later in the config file to indicate what module config is going to be overriden
- *<factory>:* is optional and describes the name of vert.x service factory used to load the Verticle. If not specified it used java classpath to find a verticle for the given class name (see next)
- *<verticle-name>* is generally a verticle name, depending on the factory used it can be fully qualified class name (default), or just a descriptor name if used other factories (see [[Service Factories|http://vertx.io/docs/vertx-service-factory/java/])

When Knot.x is starting with the above configuration, we're actually asking to deploy two verticles classes `io.knotx.server.KnotxServerVerticle` 
and `my.custom.ServiceVerticle`.

When each Verticle starts, it uses its own default configuration, e.g. [KnotxServerOptions](https://github.com/Cognifide/knotx/blob/master/documentation/src/main/cheatsheet/cheatsheets.adoc#knotxserveroptions) for KnotxServerVerticle, that can be overridden in the application configuration file.
Just add `config` section, and reference the verticle by using an alias, e.g.:
```json
{
  "modules": [
    "server=io.knotx.server.KnotxServerVerticle"
  ],
  "config": {
    "server": {
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
| ! Hint |
|:------ |
| When using an alias, to supply a configuration for the verticle, you can actually deploy same verticle multiple times. Then provide different configurations for each alias, e.g. two HTTP Repository Connectors, each pointing to a different HTTP locations. | 

See [[Knot.x Deployments|KnotxDeployment]] for details how to manage your configurations.

## How to create your service?
Assuming you're implementing your own Knot.x Verticle (either Knot, RepositoryConnector or Adapter following the appropriate guides).
1. Create a configuration data object. You can follow the pattern used in [[KnotxerverOptions|]] that's pretty much as follows:
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
- JSON converters for this class will be automatically generated (during compilation) if field names follows the [[Vert.x Data Objects|https://github.com/vert-x3/vertx-codegen#data-objects]] 
- Data object must have **Default & Copy Constructors**, **Constructor from JsonObject** and **toJson()** method
- Any defaults in the configuration need to be implemented in this class.
- If some of the configuration variables might need to be overridden through JVM system properties (e.g. -Dmy.setting=123) use a convenient Java methods, such as `Integer.getInteger("my.setting)` for integers (same methods available for Strings, Booleans, etc.) (See KnotxServerOptions.DEFAULT_HTTP_PORT as an example)
- *Use fluent setters*

2. Implement your verticle that way it will use your configuration data object (initialized by the config json at start)
3. After building your project, put result JAR file into the Knot.x classpath ([[Knot.x Deployments|KnotxDeployment]]) and add your verticle to the starter JSON
```json
{
  "modules": [
    "server=io.knotx.server.KnotxServerVerticle",
    "myVerticle=my.custom.ServiceVerticle"
  ]
}
```
4. If necessary, override the default configuration directly in starter JSON, or through JVM properties if implemented.
```json
{
    ...
    "config": {
       "myVerticle": {
          "options": {
             "config": {
                 ....
             }
          }
       }
    }
}
```
