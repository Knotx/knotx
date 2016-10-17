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

The *core* module contains four Knot.x verticles without any sample data. Here's how its configuration files look based on a sample **standalone.json** available in knotx-standalone module:
**standalone.json**
```json
{
  "verticles": {
    "com.cognifide.knotx.server.KnotxServerVerticle": {
      "config": {
        "http.port": 8092,
        "allowed.response.headers": [
          "User-Agent",
          "X-Solr-Core-Key",
          "X-Language-Code",
          "X-Requested-With"
        ],
        "repositories": [
          {
            "path": "/content/local/.*",
            "address": "knotx.core.repository.filesystem"
          },
          {
            "path": "/content/.*",
            "address": "knotx.core.repository.http"
          }
        ],
        "engine": {
          "address": "knotx.core.engine"
        }
      }
    },
    "com.cognifide.knotx.repository.HttpRepositoryVerticle": {
      "config": {
        "address": "knotx.core.repository.http",
        "configuration": {
          "client.options": {
            "maxPoolSize": 1000,
            "keepAlive": false,
            "tryUseCompression": true
          },
          "client.destination": {
            "domain": "localhost",
            "port": 3001
          }
        }
      }
    },
    "com.cognifide.knotx.repository.FilesystemRepositoryVerticle": {
      "config": {
        "address": "knotx.core.repository.filesystem",
        "configuration": {
          "catalogue": ""
        }
      }
    },
    "com.cognifide.knotx.engine.TemplateEngineVerticle": {
      "config": {
        "address": "knotx.core.engine",
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
    }
  }
}
```
Configuration JSON contains four config sections, one for each Knot.x verticle.
Each verticle can be configured with additional [Deployment Options](https://github.com/Cognifide/knotx/wiki/GettingStarted#deployment-options)

### Executing Knot.x core verticles as a cluster
Thanks to the modular structure of the Knot.x project, it's possible to run each Knot.x verticle on a separate JVM or host them as a cluster. An out of the box requirement to form a cluster (driven by Hazelcast) is that the network supports multicast.
For other network configurations please consult Vert.x documentation [Vert.x Hazelcast - Configuring cluster manager](http://vertx.io/docs/vertx-hazelcast/java/#_configuring_this_cluster_manager)

To run it, execute the following command:
**Host 1 - Http Repository Verticle**
```
java -jar knotx-repository-http-XXX-fat.jar -conf <path-to-http-repository-configuration.json>
```
**Host 2 - Filesystem Repository Verticle**
```
java -jar knotx-repository-filesystem-XXX-fat.jar -conf <path-to-filesystem-repository-configuration.json>
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
- **allowed.response.headers** list of headers that should be passed back to the client.
- **repositories** mapping of paths to the repository verticles that should deliver Templates
- **engine** event bus address of the Rendering engine verticle
```json
{
  "com.cognifide.knotx.server.KnotxServerVerticle": {
    "config": {
      "http.port": 8092,
      "allowed.response.headers": [
        "User-Agent",
        "X-Solr-Core-Key",
        "X-Language-Code",
        "X-Requested-With"
      ],
      "repositories": [
        {
          "path": "/content/local/.*",
          "address": "knotx.core.repository.filesystem"
        },
        {
          "path": "/content/.*",
          "address": "knotx.core.repository.http"
        }
      ],
      "engine": {
        "address": "knotx.core.engine"
      }
     }
     ...
 ```
####Verticle configuration
Each verticle requires JSON configuration of **config** object. The configuration consists of the same parameters as previous examples.
For instance, a configuration JSON for the *HTTP repository* verticle could look like this:
```json
{
  "address": "knotx.core.repository.http",
  "configuration": {
    "client.options": {
      "maxPoolSize": 1000,
      "keepAlive": false,
      "tryUseCompression": true
    },
    "client.destination" : {
      "domain": "localhost",
      "port": 3001
    }
  }
}
```
#####Preserving headers passed to microservices
Single service configuration allows to define which headers should be passed to microservices.
If **allowed.request.headers** section is not present, no headers will be forwarded to microservice. It is possible to use wildcard character (*) e.g.
```json
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
    ...
]
```

```json
"services": [
    {
      "path": "/service/mock/.*",
      "domain": "localhost",
      "port": 3000,
      "allowed.request.headers": [
        "*"
      ]
    },
    ...
]
```

### Recommended Knot.x deployment
Thanks to the modular architecture of Knot.x there are multiple approaches how to deploy Knot.x for the production usage. However, the easiest approach is to use Knot.x as one **far** jar with other jar files specific for the target implementation (such as adapter services, Handlebars helpers) available in classpath.
For instance:

- Assuming you have folder created on your host machine where Knot.x is going to be run, let's assume it's KNOTX_HOME
- Create subfolder **$KNOTX_HOME/lib** and put there **knotx-standalone-X.Y.Z-fat.jar**
- If you have custom Handlebars helpers ([See how to implement custom Handlebars helpers](TemplatingEngine#extending)), you can put it as JAR file here
- If you have project specific Verticles, e.g. Adapter services, you can put their jar files here (**DO NOT PUT ANOTHER FAT JARS**)
- Create your own configuration JSON (any location on the host)
- Create your own Logback logger configuration (any location on the host)
- Start Knot.x using following command
```
java -Dlogback.configurationFile=/path/to/your/logback.xml -cp "lib/*" com.cognifide.knotx.launcher.LogbackLauncher -conf /path/to/your/setup.json
```
