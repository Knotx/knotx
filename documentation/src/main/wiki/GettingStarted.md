# Getting started

## Requirements

To run Knot.x you only need Java 8.

To build it you also need Maven (version 3.3.1 or higher).

## Modules
The Knot.x project has two main Maven modules: **knotx-core** and **knotx-example**.

### Core
The *core* module contains the Knot.x [verticles](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) without any example data or mock endpoints. See the [Configuration section](#configuration-1) for instructions on how to deploy the Knot.x core module.

#### knotx-api
A module that defines the communication model used to send event-related messages via the Event Bus.

#### knotx-launcher
A module that initialises system properties for Knot.x core. Currently only logger.

#### knotx-repository
A module that contains the **repository** [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) implementation. The repository is responsible for fetching templates.

#### knotx-server
A module that contains the **server** [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) implementation. Server is responsible for communication between Knot.x and the World. It handles initial request, dispatches them to `repository` and `templating-engine` verticles using [Event Bus](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/EventBus.html) and finally sends responses to the client.

#### knotx-standalone
A module that contains the **host** [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) which starts `server`, `repository` and `template-engine` verticles. It enables one to quickly set up a standalone Knot.x core application.

#### knotx-template-engine
A module that contains the **template-engine** [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) implementation. Templating Engine is responsible for processing template snippets, calling external services for dynamic data and producing final markup with injected data.

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
- Knot.x Repository in `knotx-core/knotx-repository/target`
- Knot.x Template Engine in `knotx-core/knotx-engine/target`
- Knot.x Http Server in `knotx-core/knotx-server/target`

And in example application:
- Mocks verticles in `knotx-example/knotx-mocks/target`
- Sample app in `knotx-example/knotx-example-monolith/target`

### Executing fat jar

To run sample app, execute the following commands:
```
$cd knotx-example/knotx-example-monolith
java -jar target/knotx-example-monolith-XXX.jar -conf src/main/resources/application.json
```

This will run the server with sample data. You can supply your own configuration using the `-conf` parameter. Sample pages are available at:
```
http://localhost:8092/content/local/simple.html
http://localhost:8092/content/remote/simple.html
```
## Configuration

The Knot.x Sample application consists of multiple verticles, each requiring a dedicated configuration entry.
Here's how JSON configuration files look:
**1. application.json**
```json
{
  "repository": {
    "config" : {
        ...
    }
  },
  "templateEngine": {
    "config" : {
        ...
    }
  },
  "knotxServer": {
    "config" : {
        ...
    }
  },  
  "mockRepo": {
    "config" : {
        ...
    }
  },
  "mockService": {
    "config" : {
        ...
    }
  }
}
```
This is the main configuration supplying config entries for each verticle started by the Sample application.

**1.1. repository** section
```json
{
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
          "port": 3001
        }
      ]
    }
  },
  ...
}
```
This section configures the Knot.x Repository Verticle listening for requests on Vert.x event bus. The config node consists of:
- **service.name** - the name/address of the event bus to which Repository Verticle subscribes on,
- **repositories** - an array of definitions of all repositories used that provide HTML Templates.

There are two sample repositories defined - `local` and `remote`. Each of them defines a `path` - a regular expression that indicates which resources will be taken from this repository. 
The first one matched will handle the request or, if no repository is matched, **Knot.x** will return a `404 Not found` response for the given request.

- **Local repositories**
```json
{
    "type": "local",
    "path": "/content/local/.*",
    "catalogue": ""    
}
```
If you need to take files from a local machine, this is the kind of repository you want to use. It's perfect for mocking data. 
The second parameter to define is `catalogue` - it determines where to take the resources from. If it's left empty, they will be taken from the classpath. It may be treated like a prefix to the requested resources.

- **Remote repositories**
This kind of repository connects with an external server to fetch templates. To specify where the remote instance is, please configure the `domain` and `port` parameters.
```json
{
    "type": "remote",
    "path": "/content/.*",
    "domain": "localhost",
    "port": 3001   
}
```

**1.2. templateEngine** section
```json
  ...
  "templateEngine": {
    "config": {
      "service.name": "template-engine",
      "template.debug": true,
      "client.options": {
        "maxPoolSize" : 1000,
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
This section configures the Knot.x Template Engine Verticle listening for requests on Vert.x event bus. The config node consists of:
- **service.name** - name/address of the event bus to which the Repository Verticle subscribes,
- **template.debug** - boolean flag to enable/disable rendering HTML comment entities around dynamic snippets,
- **client.options** - contains json representation of [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) configuration for [HttpClient](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClient.html), 
- **services** - an array of definitions of all service endpoints used by dynamic snippets.

There are two groups of services defined. Each one will be handled by a different server, i.e. all service requests which match the regular expression:
- `/service/mock/.*` will by handled by `localhost:3000`
- `/service/.*` will be handled by `localhost:8080`

The first matched service will handle the request or, if there's no service matched, the corresponding template's script block will be empty. Please note that in the near future it will be improved to define fallbacks in the template for cases when the service does not respond or cannot be matched.

**1.3. knotxServer** section
```json
{
  ...
  "knotxServer": {
    "config": {
      "http.port": 8092,
      "preserved.headers": [
        "User-Agent",
        "X-Solr-Core-Key",
        "X-Language-Code"
      ],
      "dependencies" : {
        "repository.address" : "template-repository",
        "engine.address": "template-engine"
      }
    }
  },
  ...
}
```
This section configures the Knot.x HTTP server. The config node consists of:
- **http.port** - an HTTP port on which the server listens for requests,
- **preserved.headers** - an array of HTTP Headers that will be forwarded to service calls,
- **dependencies** - Vert.x Event Bus addresses to Knot.x verticles used by Knot.x
    - **repository.address** - event bus address of Knot.x Repository verticle. This is the same value as **service.name** in the **repository** section of the `application.json`
    - **engine.address** - event bus address of Knot.x Template Engine verticle. This is the same value as **service.name** in the **templateEngine** section of the `application.json`    

**mockRepo** section
```json
{
    ... 
    "mockRepo": {
        "mock.data.root": "mock/repository",
        "http.port": 3001
    },     
    ...
}
```
This section configures the Remote repository mock used by the example application. It consists of:
- **mock.data.root** - a path (relative to `knotx-mocks/src/main/resources`) where mocked HTML responses are located on local storage,
- **http.port** - HTTP Port the mock service is listening to.

**mockService** section
```json
{
    ... 
    "mockService": {
        "mock.data.root": "mock/service",
        "http.port": 3000
    },   
    ...
}
```
This section configures the Services mock used by the example application. It consists of:
- **mock.data.root** - relative (to knotx-mocks/src/main/resources) path where mocked JSON responses are located on local storage
- **http.port** - HTTP Port the mock service is listening to.

Please mind that this an example that depicts a valid setup of the Sample monolith application and it is not fit for use in production environments.
To learn how to configure Knot.x for use in production, see the [Production](#configuration-1) section.
