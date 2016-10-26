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

#### knotx-engine-view
A module that contains the **engine-view** [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) implementation. View Engine is responsible for processing template snippets, calling external services (only GET requests) for dynamic data and producing final markup with injected data.
See [[View Engine|ViewEngine]] to learn more.

### Example
The *example* module contains the Knot.x application, example template repositories and mock services. Internally, it starts five verticles (Knot.x Repository, Knot.x View Engine, Knot.x Server, Services Mocks and Mocked Remote repository). This module is a perfect fit for those getting started with Knot.x. 

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
- Knot.x View Engine in `knotx-core/knotx-engine-view/target`
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
      "com.cognifide.knotx.server.KnotxServerVerticle": {
         "config" : {
            ...
         }
      },
      "com.cognifide.knotx.repository.HttpRepositoryVerticle": {
        "config" : {
            ...
        }
      },
      "com.cognifide.knotx.repository.FilesystemRepositoryVerticle": {
        "config" : {
            ...
        }
      },      
      "com.cognifide.knotx.knot.view.ViewKnotVerticle": {
        "config" : {
            ...
        }
      },
      "com.cognifide.knotx.adapter.service.http.HttpServiceAdapterVerticle" :{
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
      },
      "com.cognifide.knotx.mocks.MockServiceAdapterVerticle": {
        "config" : {
            ...
        }
      }
  }
}
```
The configuration consists of **verticles** object containing set of configurations for verticles that are going to be deployed(started). Each object in a set should have name of class name of the **Verticle** that are going to be deployed.


#### 1.5 MockRepo section
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

#### 1.6 MockService section
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