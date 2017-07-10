# Knot.x Module
Knot.x is composed of set of Verticles. To simplify deployment process and configuration of each specialized Verticle, Knot.x is shipped with it's own implementation of Vert.x Service Factory.
It means that in order to configure Knot.x user needs to provide a set of Knot.x module names that should be deployed. Please notice that module name is something different than Verticle class name.

As mentioned in [[Knot.x Deployment|KnotxDeployment]], the list of modules must be specified in JSON file provided as `-conf` parameter when starting Knot.x application.
```json
{
  "modules": [
    "knotx:io.knotx.KnotxServer",
    "knotx:my.custom.Service"
  ]
}
```
In the example above, each module is prefixed with `knotx:` string. This tells Vert.x engine that Knot.x Verticle Factory should be used to resolve actual Verticle.

When Knot.x is starting with the above config, we're actually asking to deploy two modules `io.knotx.KnotxServer` 
and `my.custom.Service` (`knotx:` prefix is mandatory to let the system know it's Knot.x module that should be deployed).

Knot.x first looks for a descriptor file on the classpath. The descriptor file name is given by the module name concatenated with the `.json` file extension. 
In our case two descriptors are going to be looked up: `io.knotx.KnotxServer.json` and `my.custom.Service.json`

The descriptor file is simply a text file which must contain a valid JSON object. At minimum the JSON must provide a `main` field which determines the actual verticle that will be deployed, e.g.:
```json
{
  "main": "io.knotx.server.KnotxServerVerticle"
}
```
The JSON can also provide an `options` field which maps exactly to a **[Deployment Options](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html)** object.
```json
{
  "main": "io.knotx.server.KnotxServerVerticle",
  "options": {
    "config": {
      "httpPort": 4555,
      "foo": "bar"
    },
    "instances": 2,
    "isolationGroup": "myGroup"
  }
}
```
When deploying a service from a service descriptor, any fields that are specified in the descriptor can be overridden:
- in the starter JSON at `config` object
```json
{
  "modules": [
    "knotx;io.knotx.KnotxServer"
  ],
  "config": {
    "knotx:io.knotx.KnotxServer": {
      "options": {
        "config": {
          "httpPort": 6666
        },
        "instances": 1
      }
    }
  }
}
```
- by JVM property (it will override also values overridden by starter JSON)
```
$ java -Dio.knotx.KnotxServer.options.config.httpPort=2000 -jar knotx-xxxx-fat.jar -conf starter.json
```
See [[Knot.x Deployments|KnotxDeployment]] for details how to supply your configurations.

##How to create your service ?
1. Assuming you're implementing your own Knot.x Verticle (either Knot or any kind of Adapter following the appropriate guides), 
you need to create module descriptor of your verticle to be available in class path. Simply create JSON file in `src/main/resource` folder on your maven module. 
E.g.: `src/main/resources/my.custom.Service.json`
2. Define verticle class and default configuration for it's implementation
```json
{
  "main": "com.example.knot.MyCustomKnot",
  "options": {
    "config": {
      "foo": "bar",
      "flag": true,
      "data": {
        "first": 333,
        "second": 122,
        "msg": "some message"
      }
    }
  }
}
```
3. After building your project, put result JAR file into the Knot.x classpath ([[Knot.x Deployments|KnotxDeployment]]) and add your module name to the starter JSON
```json
{
  "modules": [
    "knotx:io.knotx.KnotxServer",
    "knotx:my.custom.Service"
  ]
}
```
4. If necessary, override default configuration directly in starter JSON, or through JVM properties.
