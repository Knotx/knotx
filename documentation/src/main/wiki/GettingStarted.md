# Getting started

## Requirements

To run Knot.x you only need Java 8.

To build it you also need Maven (version 3.3.1 or higher).

## Modules
The Knot.x project has two main Maven modules: **knotx-core** and **knotx-example**.

### Core
The *core* module contains the Knot.x [verticles](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) without any example data or mock endpoints. See the [Configuration section](#configuration-1) for instructions on how to deploy the Knot.x core module.

#### knotx-common
A module that defines:

- the communication model used to send event-related messages via the Event Bus.
- Knotx Starter Verticle supporting verticles definition in the given configuration JSON and corresponding Vert.x launcher

#### knotx-repository
A module that contains submodules for specialized repository [verticles](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) imlementations. 

- **knotx-repository-http** - Http Repository verticle
- **knotx-repository-filesystem** - Local filesystem Repository verticle

#### knotx-server
A module that contains the **server** [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) implementation. Server is responsible for communication between Knot.x and the World. It handles initial request, dispatches them to `repository` and `templating-engine` verticles using [Event Bus](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/EventBus.html) and finally sends responses to the client.

#### knotx-standalone
A module that contains JSON configuration to start Knot.x as standalone system. It means only following verticles to be started: `server`, `repository` and `template-engine`. It enables one to quickly set up a standalone Knot.x core application.

#### knotx-template-engine
A module that contains the **template-engine** [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) implementation. Templating Engine is responsible for processing template snippets, calling external services for dynamic data and producing final markup with injected data.
See [[Templating Engine|TemplatingEngine]] to learn more.

### Example
The *example* module contains the Knot.x application, example template repositories and mock services. Internally, it starts five verticles (Knot.x Repository, Knot.x Template Engine, Knot.x Server, Services Mocks and Mocked Remote repository). This module is a perfect fit for those getting started with Knot.x. 

#### knotx-example-monolith
A module that can be used to set up a Knot.x instance with mocked external services (from `knotx-mocks`) on a single Vert.x instance using one command. Please mind that this an example that depicts a valid setup of Sample monolith application and is not fit for use in production environments.
We recommend to start playing with Knot.x using this module. To learn how to configure Knot.x for use in production, see the [Production](#configuration-1) section.
More information about setup can be found in [Getting started](#executing-fat-jar) section.

#### knotx-mocks
A module that contains simple mocks of `templates repository` and `micorservices endpoints` implemented as [verticles](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html). 
When started, *mock remote repository* listens on port `3001` and *mock service* listens on port `3000`.

## Building

To build it, simply checkout the project, navigate to the project root and run the following command:

```
mvn clean install
```
This will create executable fat JAR files for each Knot.x:
- Knot.x Http Repository in `knotx-core/knotx-repository/knotx-repository-http/target`
- Knot.x Filesystem Repository in `knotx-core/knotx-repository/knotx-repository-filesystem/target`
- Knot.x Template Engine in `knotx-core/knotx-engine/target`
- Knot.x Http Server in `knotx-core/knotx-server/target`

And in example application:
- Mocks verticles in `knotx-example/knotx-mocks/target`
- Sample app (bundled with all required verticles) in `knotx-example/knotx-example-monolith/target`

### Executing fat jar

To run sample app, execute the following commands:
```
$cd knotx-example/knotx-example-monolith
java -jar target/knotx-example-monolith-XXX.jar -conf src/main/resources/knotx-example-monolith.json
```

This will run the server with sample data. You can supply your own configuration using the `-conf` parameter. Sample pages are available at:
```
http://localhost:8092/content/local/simple.html
http://localhost:8092/content/local/multiple-forms.html
http://localhost:8092/content/remote/simple.html
http://localhost:8092/content/remote/multiple-forms.html
```
## Configuration

The Knot.x Sample application consists of multiple verticles, each requiring a dedicated configuration entry.
Here's how JSON configuration files look:
### 1. knotx-example-monolith.json
```json
{
  "verticles" : {  
      "com.cognifide.knotx.repository.RepositoryVerticle": {
        "config" : {
            ...
        }
      },
      "com.cognifide.knotx.engine.TemplateEngineVerticle": {
        "config" : {
            ...
        }
      },
      "com.cognifide.knotx.server.KnotxServerVerticle": {
        "config" : {
            ...
        }
      },  
      "com.cognifide.knotx.mocks.MockRemoteRepositoryVerticle": {
        "config" : {
            ...
        }
      },
      "com.cognifide.knotx.mocks.MockServiceVerticle": {
        "config" : {
            ...
        }
      }
  }
}
```
The configuration consists of **verticles** object containing set of configurations for verticles that are going to be deployed(started). Each object in a set should have name of class name of the **Verticle** that are going to be deployed.

#### 1.1. httpRepository section
```json
{
  "com.cognifide.knotx.repository.RepositoryVerticle": {
    "config": {
      "address": "knotx.core.repository.http",
      "configuration": {
        "client.options": {
          "maxPoolSize": 1000,
          "keepAlive": false
        },
        "client.destination" : {
          "domain": "localhost",
          "port": 3001
        }
      }
    }
  },
  ...
}
```
This section configures the Knot.x HTTP Repository Verticle that fetches a template from external server via HTTP protocol.
The config node consists of:

- **address** - the event bus address on which Http Repository Verticle listens for template requests.
- **configuration** - it's a configuration section specific to the Http protocol used by verticle, it contains:
-- **client.options** - HTTP Client options used when communicating with the destination repository. See [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) to get all options supported.
-- **client.destination** - Allows to specify **domain** and **port** of the HTTP repository endpoint.

#### 1.2. localRepository section
```json
{
  "localRepository": {
    "config": {
      "address": "knotx.core.repository.filesystem",
      "configuration": {
        "catalogue": ""
      }
    }
  },
  ...
}
```
If you need to take files from a local machine, this is the kind of repository you want to use. It's perfect for mocking data.
The config node consists of:

- **address** - the event bus address on which file system Repository Verticle listens for template requests.
- **configuration** - it's a configuration section specific to the file system.
-- **catalogue** - it determines where to take the resources from. If it's left empty, they will be taken from the classpath. It may be treated like a prefix to the requested resources.

#### 1.3. engine section
```json
  ...
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
          "port": 3000
        },
        {
          "path": "/service/.*",
          "domain": "localhost",
          "port": 8080
        }
      ]
    }
  },
  ...,
```
This section configures the Knot.x Template Engine Verticle responsible for rendering page consists of Handlebars template using data from corresponding services. The config node consists of:
- **address** - event bus address of the verticle it listens on,
- **template.debug** - boolean flag to enable/disable rendering HTML comment entities around dynamic snippets,
- **client.options** - contains json representation of [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) configuration for [HttpClient](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClient.html), 
- **services** - an array of definitions of all service endpoints used by dynamic snippets.

There are two groups of services defined. Each one will be handled by a different server, i.e. all service requests which match the regular expression:
- `/service/mock/.*` will by handled by `localhost:3000`
- `/service/.*` will be handled by `localhost:8080`

The first matched service will handle the request or, if there's no service matched, the corresponding template's script block will be empty. Please note that in the near future it will be improved to define fallbacks in the template for cases when the service does not respond or cannot be matched.

#### 1.4. server section
```json
{
  ...
  "com.cognifide.knotx.server.KnotxServerVerticle": {
    "config": {
      "http.port": 8092,
      "preserved.headers": [
        "User-Agent",
        "X-Solr-Core-Key",
        "X-Language-Code",
        "X-Requested-With"
      ],
      "repositories": [
        {
          "path" : "/content/local/.*",
          "address" : "knotx.core.repository.filesystem"
        },
        {
          "path" : "/content/.*",
          "address" : "knotx.core.repository.http"
        }
      ],
      "engine" : {
        "address": "knotx.core.engine"
      }
    }
  },
  ...
}
```
This section configures the Knot.x HTTP server. The config node consists of:

- **http.port** - an HTTP port on which the server listens for requests,
- **preserved.headers** - an array of HTTP Headers that will be forwarded to service calls,
- **repositories** - configuration of repositories. It's a array of mappings what paths are supported by what repository verticles (by specifing its event bus addresses). The order of mappings is important as they are evaluated from top to down on each request. The first one matched will handle the request or, if no repository is matched, **Knot.x** will return a `404 Not found` response for the given request.
- **engine** - configuration about templating engine dependency. You can configure event bus **address** of engine verticle here.

#### 1.5 mockRepo section
```json
{
    ... 
  "com.cognifide.knotx.mocks.MockRemoteRepositoryVerticle": {
    "config": {
      "mock.data.root": "mock/repository",
      "http.port": 3001
    }
  },   
    ...
}
```
This section configures the Remote repository mock used by the example application. It consists of:
- **mock.data.root** - a path (relative to `knotx-mocks/src/main/resources`) where mocked HTML responses are located on local storage,
- **http.port** - HTTP Port the mock service is listening to.

#### 1.6 mockService section
```json
{
  ..., 
  "com.cognifide.knotx.mocks.MockServiceVerticle": {
    "config": {
      "mock.data.root": "mock/service",
      "http.port": 3000
    }
  }
    ...
}
```
This section configures the Services mock used by the example application. It consists of:
- **mock.data.root** - relative (to knotx-mocks/src/main/resources) path where mocked JSON responses are located on local storage
- **http.port** - HTTP Port the mock service is listening to.

Please mind that this an example that depicts a valid setup of the Sample monolith application and it is not fit for use in production environments.
To learn how to configure Knot.x for use in production, see the [Production](#configuration-1) section.

## Deployment options
To deploy verticle with advanced options use following properties:
```json
{
    ... 
  "com.cognifide.knotx.mocks.MockServiceVerticle": {
    "config": {
      "mock.data.root": "mock/service",
      "http.port": 3000
    },
    "worker" : false,
    "multiThreaded": false,
    "isolationGroup": "null",
    "ha": false,
    "extraClasspath": [],
    "instances": 2,
    "isolatedClasses": []
  }
    ...
}
```
- **worker** - deploy verticle as a worker.
- **multiThreaded** - deploy verticle as a multi-threaded worker.
- **isolationGroup** - array of isolation group.
- **ha** - deploy verticle as highly available.
- **extraClasspath** - extra classpath to be used when deploying the verticle.
- **instances** - number of verticle instances.
- **isolatedClasses** - array of isolated classes.

Read more about [vert.x Deployment Options](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html)