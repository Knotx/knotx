# Production

The example module is provided for testing purposes. Only the core verticles should be deployed in a production environment. (The Knot.x application consists of 3 verticles).

### Executing Knot.x core verticles as standalone fat jar

To run it, execute the following command:
```
$cd knotx-core/knotx-standalone
java -jar target/knotx-standalone-XXX-fat.jar -conf <path-to-your-configuration.json>
```

This will run the server with production settings. For more information see the [Configuration](#configuration-1) section.

#### Configuration

The *core* module contains 3 Knot.x verticle without any sample data. Here's how its configuration files look based on a sample **standalone.json** available in knotx-standalone module:
**standalone.json**
```json
{
  "server": {
    "config": {
      "http.port": 8092,
      "preserved.headers": [
        "User-Agent",
        "X-Solr-Core-Key",
        "X-Language-Code",
        "X-Requested-With"
      ],
      "dependencies": {
        "repository.address": "template-repository",
        "engine.address": "template-engine"
      }
    },
    "worker" : false,
    "multiThreaded": false,
    "isolationGroup": "null",
    "ha": false,
    "extraClasspath": [],
    "instances": 2,
    "isolatedClasses": []
  },
  "repository": {
    "config": {
      "service.name": "template-repository",
      "repositories": [
        {
          "type": "local",
          "path": "/content/local/.*",
          "catalogue": ""
        },
        {
          "type": "remote",
          "path": "/content/.*",
          "domain": "localhost",
          "port": 3001,
          "client.options": {
            "tryUseCompression": true,
            "keepAlive": false
          }
        }
      ]
    },
    "worker" : false,
    "multiThreaded": false,
    "isolationGroup": "null",
    "ha": false,
    "extraClasspath": [],
    "instances": 2,
    "isolatedClasses": []
  },
  "engine": {
    "config": {
      "service.name": "template-engine",
      "template.debug": true,
      "client.options": {
        "maxPoolSize": 1000,
        "keepAlive": false
      },
      "services": [
        {
          "path": "/service/mock/.*",
          "domain": "localhost",
          "port": 3000
        },
        {
          "path": "/service/.*",
          "domain": "localhost",
          "port": 8080
        }
      ]
    },
    "worker" : false,
    "multiThreaded": false,
    "isolationGroup": "null",
    "ha": false,
    "extraClasspath": [],
    "instances": 2,
    "isolatedClasses": []
  }
}
```
Configuration JSON contains three config sections, one for each Knot.x verticle.

### Executing Knot.x core verticles as a cluster
Thanks to the modular structure of the Knot.x project, it's possible to run each Knot.x verticle on a separate JVM or host them as a cluster. An out of the box requirement to form a cluster (driven by Hazelcast) is that the network supports multicast.
For other network configurations please consult Vert.x documentation [Vert.x Hazelcast - Configuring cluster manager](http://vertx.io/docs/vertx-hazelcast/java/#_configuring_this_cluster_manager)

To run it, execute the following command:
**Host 1 - Repository Verticle**
```
java -jar knotx-repository-XXX-fat.jar -conf <path-to-repository-configuration.json>
```
**Host 2 - Template Engine Verticle**
```
java -jar knotx-template-engine-XXX-fat.jar -conf <path-to-engine-configuration.json>
```
**Host 3 - Knot.x Server Verticle**
```
java -jar knotx-server-XXX-fat.jar -conf <path-to-server-configuration.json>
```

For production setup you might need to configure a logger according to your needs. You can start each of the verticles specifying a path to the logback logger configuration via a system property.
```
java -Dlogback.configurationFile=<full-path-to-logback-configuration-file> -jar XXX
```
Please refer to [LOGBack manual](http://logback.qos.ch/manual/index.html) for details on how to configure the logger.

### Remarks
For clustering testing purposes you can also start a separate verticle with repository and services mocks. In order to do so, run the following commands:
```
$ cd knotx-example/knotx-mocks
$ java -jar target/knotx-mocks-XXXX-far.jar -conf src/main/resources/knotx-mocks.json
```
The mocks verticle is configured as follows:
- **mock remote repository** listens on port **3001**
- **mock service** listens on port **3000**

### Configuration
####Server configuration
Knot.x server requires JSON configuration with *config* object. **Config** section allows to define:
- **http.port** property to set http port which will be used to start Knot.x server
- **preserved.headers** array property of headers which will be rewritten between Knot.x, template repository and service call
```json
{
  "server": {
    "config": {
      "http.port": 8092,
      "preserved.headers": [
        "User-Agent",
        "X-Solr-Core-Key",
        "X-Language-Code"
      ],
     ...
 ``` 
####Verticle configuration
Each verticle requires JSON configuration with *config* object. The configuration consists of the same parameters as previous examples.
For instance, a configuration JSON for the *repository* verticle could look like this:
```json
{
  "config": {
    "service.name": "template-repository",
    "repositories": [
      {
        "type": "remote",
        "path": "/content/.*",
        "domain": "localhost",
        "port": 3001,
        "client.options": {
          "tryUseCompression" : true
       }
      }
    ]
  }
}
```
