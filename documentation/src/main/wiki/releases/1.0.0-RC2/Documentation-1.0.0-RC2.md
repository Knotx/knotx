# Knot.x

<p align="center">
  <img src="https://github.com/Cognifide/knotx/blob/master/icons/180x180.png?raw=true" alt="Knot.x Logo"/>
</p>

A simplified description of **Knot.x** can be `a tool which converts a static page (template) into a 
dynamic page driven by the data provided by external sources`

In short, we call **Knot.x** a **reactive multisource assembler**.

## What problems does Knot.x solve?
**Knot.x** assembles static and dynamic content from multiple sources to produce pages with dynamic data in a very performant manner.

[[assets/knotx-high-level-architecture.png|alt=High Level Architecture]]

- **Knot.x** can combine several template (page) sources thanks to its [[Repository|Repository]] feature. It allows to have one entry point to different content platforms.
- **Knot.x** can assemble dynamic page that requires data from multiple external sources (e.g. microservices) thanks to [[Service Knot|ServiceKnot]] and [Handlebars Knot|HandlebarsKnot]modules.
- With fast and scalable heart of an architecture - [Vert.x](http://vertx.io/) engine - **Knot.x** can significantly boost platform's performance. Learn more about [[Knot.x Architecture|Architecture]].
- **Knot.x** supports forms submission including multi-step forms. Find out more about this topic reading about [[Action Knot|ActionKnot]].

## What's philosophy behind Knot.x?
We care a lot about speed and that is why we built **Knot.x** on [Vert.x](http://vertx.io/), known as one of the leading frameworks for performant, event-driven applications.

### Stability and responsiveness
**Knot.x** uses asynchronous programming principles which allows it to process a large number of requests using a single thread.
Asynchronous programming is a style promoting the ability to write non-blocking code (no thread pools).
The platform stays responsive under heavy and varying load and is designed to follow [Reactive Manifesto](http://www.reactivemanifesto.org/) principles.

### Loose coupling
Relies on asynchronous message-passing to establish a boundary between system components that ensures 
loose coupling, isolation and location transparency. Base **Knot.x** component is called [[Knot|Knot]].

[[assets/knotx-modules-basic-request-flow.png|alt=Basic request flow]]

### Scalability
Various scaling options are available to suit client needs and help in cost optimization. Using a 
simple concurrency model and message bus **Knot.x** can be scaled within a single host or cluster of 
servers.

# Getting started

## Getting Binaries
Knot.x provides binaries and default JSON configuration files at [Releases](https://github.com/Cognifide/knotx/releases).
Binaries are packaged in *fat jars*. A fat jar is a standalone executable Jar file containing all 
the dependencies required to run the application. It make those jars easy to execute.

To run Knot.x you need Java 8.

## Hello world!
First download Knot.x sample app:

[knotx-example-monolith-X.X.X-fat.jar](https://github.com/Cognifide/knotx/releases/)

[knotx-example-monolith.json](https://github.com/Cognifide/knotx/releases/)


Now you can run Knot.x:

```
java -jar knotx-example-monolith-X.X.X-fat.jar -conf knotx-example-monolith.json
```

That's all. Finally you can open a browser and type an url `http://localhost:8092/content/local/simple.html`. 
You should see a page which is served from a local repository and contains example data from mock services.

The page should look like:

[[assets/knotx-example-simple.png|alt=Example simple page]]

See also [[how to run Knot.x demo|RunningTheDemo]] for more details.

## Building

To checkout the source and build:

```
$ git clone https://github.com/Cognifide/knotx.git
$ cd knotx/
$ mvn clean install
```

You should see:

```
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] Knot.x ............................................. SUCCESS [  2.747 s]
[INFO] Knot.x - Reactive microservice assembler - documentation SUCCESS [  0.091 s]
[INFO] Knot.x - Reactive microservice assembler - Core Root POM SUCCESS [  0.159 s]
[INFO] Knot.x - Reactive microservice assembler - Common .. SUCCESS [  5.713 s]
[INFO] Knot.x - Reactive microservice assembler - Repository Verticle SUCCESS [  0.071 s]
[INFO] Knot.x - Reactive microservice assembler - Specialized Filesysten Repository Verticle SUCCESS [  4.622 s
]
[INFO] Knot.x - Reactive microservice assembler - Specialized Http Repository Verticle SUCCESS [  4.670 s]
[INFO] Knot.x - Reactive microservice assembler - HTML Fragment Splitter SUCCESS [  7.849 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Server SUCCESS [  7.902 s]
[INFO] Knot.x - Reactive microservice assembler - Knot Root SUCCESS [  0.109 s]
[INFO] Knot.x - Reactive microservice assembler - Knot API  SUCCESS [  0.414 s]
[INFO] Knot.x - Reactive microservice assembler - Service Knot SUCCESS [  4.563 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter Root SUCCESS [  0.100 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter API SUCCESS [  0.405 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter Common SUCCESS [  1.866 s]
[INFO] Knot.x - Reactive microservice assembler - Action Knot Verticle SUCCESS [  6.901 s]
[INFO] Knot.x - Reactive microservice assembler - Handlebars Knot SUCCESS [  3.623 s]
[INFO] Knot.x - Sample App with Mock service .............. SUCCESS [  0.305 s]
[INFO] Knot.x - Mocked services for sample app ............ SUCCESS [  2.894 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Service Adapter SUCCESS [ 10.189 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Action Adapter SUCCESS [  9.619 s]
[INFO] Knot.x - Reactive microservice assembler - Auhtorization Knot Verticle SUCCESS [ 10.165 s]
[INFO] Knot.x - Reactive microservice assembler - Standalone Knot.x SUCCESS [  2.779 s]
[INFO] Knot.x - Sample App with Mock service .............. SUCCESS [ 11.764 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:40 min
[INFO] Finished at: 2016-11-20T15:32:38+01:00
[INFO] Final Memory: 166M/1491M
[INFO] ------------------------------------------------------------------------
```

See also [[how to run Knot.x demo|RunningTheDemo]].


# Running Knot.x Demo

## Requirements

To run Knot.x you only need Java 8.
To build it you also need Maven (version 3.3.1 or higher).

## Running the demo
To run an [Example monolith app](https://github.com/Cognifide/knotx/blob/master/knotx-example/knotx-example-monolith) do the following:

Build Knot.x with Example app:

```
$ git clone https://github.com/Cognifide/knotx.git
$ cd knotx
$ mvn clean install
```

or download [released](https://github.com/Cognifide/knotx/releases) application `knotx-example-monolith-X.Y.Z-fat.jar` and configuration file `knotx-example-monolith.json`.

Run example app:
```
$ cd knotx-example/knotx-example-monolith
$ java -jar target/knotx-example-monolith-X.Y.Z-SNAPSHOT-fat.jar -conf src/main/resources/knotx-example-monolith.json
```

You will see output similar to the following:
```
...
2016-11-20 15:22:18 [vert.x-eventloop-thread-2] INFO  c.c.k.r.HttpRepositoryVerticle - Registered <HttpRepositoryVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-3] DEBUG c.c.knotx.knot.api.AbstractKnot - Starting <com.cognifide.knotx.knot.service.ServiceKnotProxyVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-5] DEBUG c.c.knotx.knot.api.AbstractKnot - Starting <com.cognifide.knotx.knot.authorization.AuthorizationKnotVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-6] DEBUG c.c.k.s.FragmentSplitterVerticle - Starting <com.cognifide.knotx.splitter.FragmentSplitterVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-1] DEBUG c.c.knotx.server.KnotxServerVerticle - Starting <com.cognifide.knotx.server.KnotxServerVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-7] DEBUG c.c.knotx.knot.api.AbstractKnot - Starting <com.cognifide.knotx.knot.action.ActionKnotVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-3] INFO  c.c.knotx.mocks.MockServiceVerticle - Starting <MockServiceVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-5] INFO  c.c.k.r.FilesystemRepositoryVerticle - Registered <FilesystemRepositoryVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-6] INFO  c.c.k.m.MockRemoteRepositoryVerticle - Starting <MockRemoteRepositoryVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-4] INFO  c.c.k.k.t.HandlebarsKnotVerticle - Registered custom Handlebars helper: bold
2016-11-20 15:22:18 [vert.x-eventloop-thread-4] DEBUG c.c.knotx.knot.api.AbstractKnot - Starting <com.cognifide.knotx.knot.templating.HandlebarsKnotProxyVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-4] INFO  c.c.k.m.MockServiceAdapterVerticle - Starting <MockServiceAdapterVerticle>
2016-11-20 15:22:19 [vert.x-eventloop-thread-0] DEBUG c.c.k.adapter.api.AbstractAdapter - Registered <HttpServiceAdapterVerticle>
2016-11-20 15:22:19 [vert.x-eventloop-thread-2] INFO  c.c.k.m.MockActionAdapterVerticle - Starting <MockActionAdapterVerticle>
2016-11-20 15:22:19 [vert.x-eventloop-thread-1] DEBUG c.c.k.adapter.api.AbstractAdapter - Registered <HttpActionAdapterVerticle>
2016-11-20 15:22:19 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2016-11-20 15:22:19 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED 

		Deployed 126460c4-a1ce-4edb-8a27-f7bcc46a8c6d [com.cognifide.knotx.knot.authorization.AuthorizationKnotVerticle]
		Deployed d15b83dd-963b-40fb-967b-05f999cd5175 [com.cognifide.knotx.splitter.FragmentSplitterVerticle]
		Deployed 6cff9e78-979b-423d-97d9-bdf9c26209bc [com.cognifide.knotx.knot.service.ServiceKnotProxyVerticle]
		Deployed 4ec1beb0-e231-4c55-bf3e-dca9a940f1ef [com.cognifide.knotx.knot.action.ActionKnotVerticle]
		Deployed 3b0e9f44-9e89-48b8-8b8e-ba17a376e987 [com.cognifide.knotx.repository.FilesystemRepositoryConnectorVerticle]
		Deployed e96f425c-4fdc-4415-be92-c65e759e8476 [com.cognifide.knotx.knot.templating.HandlebarsKnotProxyVerticle]
		Deployed 9a3ea907-d513-4d22-b846-8395d6afe5e9 [com.cognifide.knotx.mocks.MockServiceAdapterVerticle]
		Deployed 828a05f5-3ace-4d54-89bd-f5efcaaceb33 [com.cognifide.knotx.repository.HttpRepositoryConnectorVerticle]
		Deployed e34358f2-48da-47de-b42c-d91cfc2ef2b7 [com.cognifide.knotx.mocks.MockActionAdapterVerticle]
		Deployed 3bcd0b2b-2d3f-41e0-87bf-983f3a3cb630 [com.cognifide.knotx.adapter.service.http.HttpServiceAdapterVerticle]
		Deployed 5d3e7cce-1ceb-49b9-b901-8f5fc4b5a64c [com.cognifide.knotx.mocks.MockRemoteRepositoryVerticle]
		Deployed 649b6ed0-baa7-4bc4-94b7-e71cbb44fed0 [com.cognifide.knotx.mocks.MockServiceVerticle]
		Deployed 2bd2ea06-aa9d-4afe-ba5f-776f93ebda77 [com.cognifide.knotx.adapter.action.http.HttpActionAdapterVerticle]
		Deployed 6dccd9b6-c887-4f59-a50d-8675f52dbe17 [com.cognifide.knotx.server.KnotxServerVerticle]

```

This example app simulates Vert.x based application running Knot.x core verticles:
 - [[Server|Server]],
 - [[Repositories|Repository]]: File System Repository, Http Repository,
 - [[Splitter|Splitter]],
 - [[Action Knot|ActionKnot]],
 - [[Service Knot|ServiceKnot]],
 - [[Handlebars Knot|HandlebarsKnot]],
 - [[Http Service Adapter|HttpServiceAdapter]], 
 - example Action Adapter,
 - example Authorization Knot.
 
Besides Knot.x, mock verticles are started:
 - Mock Service  -> simulates services used by View Engine feeding the Handlebars snippets
 - Mock Remote Repository -> simulates HTTP Remote repository serving HTML templates
 - Mock Service Adapter -> simulates real service adapters on event bus

With default configuration, Knot.x starts on port `8092`. You can access example Knot.x application from the following URLs:
```
http://localhost:8092/content/remote/simple.html
http://localhost:8092/content/local/simple.html
http://localhost:8092/content/local/multiple-forms.html
http://localhost:8092/content/remote/multiple-forms.html
```
- first serves HTML template from Remote Http Repository
- second serves HTML template from local storage
- third one serves HTML template with multiple forms on the page, one is AJAX based - served from local storage
- last one serves HTML template with multiple forms on the page, one is AJAX based - served from remote repository

#Architecture

A simplified description of Knot.x can be `a tool which converts a static page (template) into 
dynamic page driven by data provided by microservices`.
Page visitor requests are directed to Knot.x. Then Knot.x calls Repository for 
the template, split this template to static / dynamic fragments and process those fragments. Finally 
it calls external services if required.

The diagram below depicts Knot.x request flow at very high level point of view.

[[assets/knotx-overview.png|alt=Knot.x Overview]]

Thanks to modular nature, Knot.x can be easily extended by project-specific mechanics (see [[Knots|Knot]]).
Knot.x can easily adapt responses with different formats to required one (see e.g. [[Service Adapters|ServiceAdapter]]).
Additionally Knot.x does not concentrate on HTTP protocol so even custom protocols can be used if required.

#High Level Architecture

Knot.x is modular easily extensible and adaptable platform which assembles static and dynamic 
content from multiple sources.

Knot.x hides its internal complexity and allows to use it with very basic knowledge about Knot.x 
Core modules. Custom features can be easily added to Knot.x with two flexible extension points: [[Knots|Knot]]
and [[Adapters|Adapter]], that listen to the event bus and handle custom business logic.

Diagram below depicts high level Knot.x architecture.

[[assets/knotx-high-level-architecture.png|alt=High Level Architecture]]

Custom business logic can be encapsulated in dedicated Knots / Adapters.

Knot is module which defines custom step while [request routing](#KnotRouting). It can process custom
fragments, invoke Adapters and redirect site visitors to new site or error page. 
More information about Knots can be found in the [[dedicated section|Knot]].
 
[[Adapters|Adapter]] are used to communicate with external services. Knot.x recommends to create dedicated Adapter
every time we need to perform some business logic or adapt service response to other format.

If REST service responses can be used as is without any changes no custom Adapters will be required. 
Knot.x provides generic HTTP Adapters ([[Http Service Adapter|HttpServiceAdapter]] and ) which can communicate with services.
It is marked on diagram with arrow between Knot.x and Services Layer.

[WORKING IN PROGRESS]
#Knot.x Core Architecture

Diagram below depicts Knot.x core modules. Knot.x by default comes with Core and Core Adapters modules.
Every module can be disabled / replaced with a simple JSON configuration change.

[[assets/knotx-architecture-full.png|alt=Knot.x Core Architecture]]

Every core module is described in a dedicated section.
# Communication Flow

A diagram below depicts a very basic request flow inside Knot.x:
[[assets/knotx-modules-basic-request-flow.png|alt=Knot.x Request Flow]]

This simple request fetches template and injects dynamic data form Service layer in order to render dynamic page.

More complex processing is presented below, where request travel through several [[Knots|Knot]] before final response is ready.
[[assets/knotx-modules-advanced-request-flow.png|alt=Knot.x Request Flow]]
 
The diagram presents that a request from a user hits Server first. Server fetches a template from a 
repository, then request for the template fragments and at the end calls Knots on matched route - 
see [[Knots routing|KnotRouting]] for more details. Server calls are synchronous, it means that before a 
next call the current one must finished. The synchronous nature of a Server does not prevent non-blocking 
implementation - Server still follows the asynchronous programming principles.

[[Knots|Knot]] can communicate with services using Adapters. When using Knot.x our recommendation is to write
 custom Adapters for cases when a service response must be adapted to required format or when one service call depends on another call. 

Knots can perform their jobs both synchronously and asynchronously. Service Adapters calls in Service Knot 
are asynchronously - GET service calls are independent so there is no reason to wait for a service response 
before the next call.

#Knot Routing

A Request from a user goes first to the [[Server|#Server]]. 
Server passes the request to Knot.x modules until processing is finished and result can be returned. 
The request flow is described in [[Communication Flow|CommunicationFlow]] section.

Server uses Router from [Vert.x-Web](http://vertx.io/docs/vertx-web/java/) library to define which 
[[Knots|Knot]] should participate in request processing and in what order.

A router takes a request, finds the first matching route for that request and passes the 
request to that route. The route has a Knot associated with it, which then receives the request. 
Knot does his job and returns response to the Server. Server then can end processing or pass request 
to the next matching Knot.


Routes entries example configuration:
```
"routing": {
  "GET": [
    {
      "path": "/secure/.*",
      "address": "knotx.knot.authorization",
      ...
    },
    {
      "path": "/forms/.*",
      "address": "knotx.knot.action",
      ...
    },
    {
      "path": "/view/.*",
      "address": "knotx.knot.service",
      ...
    }
  ],
  "POST": [
    {
      "path": "/secure/.*",
      "address": "knotx.knot.authorization",
      ...
    },
    {
      "path": "/forms/.*",
      "address": "knotx.knot.action",
      ...
    }
  ]
}
```
Knot.x understands Knot as a vertex in a graph which has one input and many outputs. Those outputs are
called transitions. Example graph configuration can look like:
```
{
  "path": "/secure/.*",
  "address": "knotx.knot.authorization",
  "onTransition": {
    "view": {
      "address": "knotx.knot.service",
      "onTransition": {
        "next": {
          "address": "knotx.knot.handlebars"
        }
      }
    },
    "next": {
      "address": "knotx.knot.action"
      "onTransition": {
        "next": {
          "address": "knotx.knot.handlebars"
        }
      }
    }
  }
}
```
Knot.x uses Router mechanism to define many routes and adds transitions to make routes easily
configurable. Example request flow can be illustrated with diagram: 

[[assets/knotx-routing-graph.png|alt=Knot Routing]]

The diagram depicts which modules take part in a request processing. A user request is first seen by Server,
then Knot.x fetches a template and split the template to fragments. 

After that routing begins. Based on the request method and path the route is selected, and the request is passed to first Knot on the route.
Knot performs its business logic and returns a transition. The transition defines next Knot the request is passed to.
In some cases Knot can decide to break the route and redirect the user to a different page.

When all Knots on the route processed the request or one of Knots break the routing, Server returns a response to the user.

# Server

Server is essentially a "heart" (a main [Verticle](http://vertx.io/docs/vertx-core/java/#_verticles)) of Knot.x.
It creates HTTP Server, listening for browser requests, and is responsible for coordination of 
communication between [[Repository|Repository]], [[Splitter|Splitter]] and all deployed [[Knots|Knot]].

## How does it work?
Once the HTTP request from the browser comes to the Knot.x, it goes to the **Server** verticle.
Server performs following actions when receives HTTP request:

- Verifies if request **method** is configured in `routing` (see config below), and sends 
**Method Not Allowed** response if not matches
- Search for the **repository** address in `repositories` configuration, by matching the 
requested path with the regexp from config, and sends **Not Found** response if none is matched.
- Calls the matching **repository** address with the original request
- Calls the **splitter** address with the template got from **repository**
- Builds [[KnotContext|Knot]] communication model (that consists of original request, response from 
repository & split HTML fragments)
- Calls **[[Knots|Knot]]** according to the [routing](#routing) configuration, with the **KnotContext**
- Once the last Knot returns processed **KnotContext**, server creates HTTP Response based on data from the KnotContext
- Filters the response headers according to the `allowed.response.headers` configuration and returns to the browser.

The diagram below depicts flow of data coordinated by the **Server** based on the hypothetical 
configuration of routing (as described in next section).
[[assets/knotx-server.png|alt=Knot.x Server How it Works flow diagram]]

### Routing
Routing specifies how the system should behave for different [Knots|Knot] responses. The request flow at 
the diagram above is reflected in a `routing` JSON node in the configuration section below. This routing 
defines that all requests for HTML pages must be processed first by Knot listening on address 
`first.knot.eventbus.address`. Then based on its response there are two next steps: `go-second` and
`go-alt`:
- If returned transition is `go-second`, Server will call next `second.knot.eventbus.address`.
- If returned transition is `go-alt`, Server will call next `alternate.knot.eventbus.address`.

For the route with `go-second` transition there is one more strep after `second.knot.eventbus.address` - 
for `go-third` transition Server will call `third.knots.eventbus.address` at the end.
For the route with `go-alt` transition Server will call `alternate.knot.eventbus.address` only.
In both cases the response will be returned to the client.

For more details please see [[Routing|Routing]] and [[Communication Flow|CommunicationFlow]] sections.

## How to configure?
Server is deployed as a separate Verticle, depending on how it's deployed. 
You need to supply **Server** configuration as JSON below if deployed as Server Verticle fat jar.
```json
{
  "http.port": 8080,
  "allowed.response.headers": [ "Content-Type", "Content-Length", "Location", "Set-Cookie" ],
  "repositories" : [
    {
      "path": "/content/local/.*\.html",
      "address": "filesystem.repository.eventbus.address"
    },
    {
      "path": "/content/.*\.html",
      "address": "http.repository.eventbus.address"
    }
  ],
  "splitter": {
    "address" : "splitter.eventbus.address"
  },
  "routing": {
    "GET": {
      "path": "/.*\.html",
      "address": "first.knot.eventbus.address",
      "onTransition": {
        "go-second": {
          "address": "second.knot.eventbus.address",
          "onTransition": {
            "go-third": {
              "address": "third.knots.eventbus.address"
            }
          }
        },
        "go-alt": {
          "address": "alternate.knot.eventbus.address"
        }
      }
    },
    "POST": {
      "path": "/.*\.html",
      "address": "first.knot.eventbus.address"
    }
  }
}
```

Or, above configuration wrapped in the JSON `config` section as shown below, if deployed using Knot.x starter verticle.
```json
  "verticles": {
    ...,
    "com.cognifide.knotx.server.KnotxServerVerticle": {
      "config": {
         "PUT YOUR CONFIG HERE"
      }
    },
    ...,
  }
```

Detailed description of each configuration option is described in next section.

## Server options
Main server options available.

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `http.port`                 | `Number (int)`                      | &#10004;       | HTTP Port on which Knot.x will listen for browser requests |
| `displayExceptionDetails`   | `Boolean`                           |         | (Debuging only) Displays exception stacktrace on error page. **False** if not set.|
| `allowed.response.headers`  | `Array of String`                   |         | Array of HTTP headers that are allowed to be send in response. **No** response headers are allowed if not set. |
| `repositories`              | `Array of RepositoryEntry`          | &#10004;       | Array of repositories configurations |
| `splitter`                  | `SplitterEntry`                     | &#10004;       | **Splitter** communication options |
| `routing`                   | `Object of Method to RoutingEntry`  | &#10004;       | Set of HTTP method based routing entries, describing communication between **Knots**<br/>`"routing": {"GET": {}, "POST": {}}` |

### RepositoryEntry options

| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `path`      | `String`  | &#10004;       | Regular expression of the HTTP Request path |
| `address`   | `String`  | &#10004;       | Event bus address of the **Repository|Repository** verticle, that should deliver content for the requested path matching the regexp in `path` |

### SplitterEntry options

| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `address`  | `String`  | &#10004;       | Sets the event bus address of the **Splitter** verticle |

### RoutingEntry options
| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `path`           | `String`                               | &#10004;       | Regular expression of HTTP Request path |
| `address`        | `String`                               | &#10004;       | Event bus address of the **Knot** verticle, that should process the message, for the requested path matching the regexp in `path` |
| `onTransition`   | `Object of Strings to TransitionEntry` |        | Describes routing to addresses of other Knots based on the transition trigger returned from current Knot.<br/> `"onTransition": { "go-a": {}, "go-b": {} }` |

### KnotRouteEntry options
| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `address`      | `String`         | &#10004;       | Event bus address of the **Knot** verticle |
| `onTransition` | `KnotRouteEntry` |        | Describes routing to addresses of other Knots based on the transition trigger returned from current Knot.<br/>`"onTransition": { "go-d": {}, "go-e": {} }` |


# Repository
Knot.x gets templates from one or more repositories, processes them and serves to end users.

## How does it work?
First it is important to understand what Repository is. Repositories are not part of Knot.x itself, 
they are stores where templates live. The diagram below depicts how Knot.x uses repositories.

[[assets/knotx-overview.png|alt=Knot.x Overview]]

Mapping between incoming request and repository is defined in a Server configuration section. It specifies
which requests should go to which repository address.

```json
[
  {
    "path": "/content/local/.*",
    "address": "knotx.core.repository.filesystem"
  },
  {
    "path": "/content/.*",
    "address": "knotx.core.repository.http"
  }
]
```

Knot.x supports by default two repository types: HTTP Repository and Filesystem Repository. Both 
[[HTTP Repository|HttpRepositoryConnector]] and [[Filesystem Repository|FilesystemRepositoryConnector]] connectors 
handle template requests using the Vertx Event Bus. This
communication model allows to add custom repositories connectors easily. For more information see sections:
* [[HTTP Repository Connector|HttpRepositoryConnector]]
* [[Filesystem Repository Connector|FilesystemRepositoryConnector]]



# HTTP Repository Connector

Http Repository Connector allows to fetch templates from an external repository via HTTP protocol. 

## How does it work?
The diagram below depicts Knot.x modules and request flow in more details.

[[assets/knotx-http-repository.png|alt=Http Repository Connector]]

## How to configure?
Http Repository Connector is deployed as a separate Verticle, depending on how it's deployed. 
You need to supply **Http Repository Connector** configuration as JSON below if deployed as Http Repository Connector Verticle fat jar.
```json
"address": "knotx.core.repository.http",
"client.options": {
  "maxPoolSize": 1000,
  "keepAlive": false,
  "tryUseCompression": true
},
"client.destination" : {
  "domain": "localhost",
  "port": 3001
}
```
Or, above configuration wrapped in the JSON `config` section as shown below, if deployed using Knot.x starter verticle.
```json
  "verticles": {
    ...,
    "com.cognifide.knotx.repository.HttpRepositoryConnectorVerticle": {
      "config": {
         "PUT YOUR CONFIG HERE"
      }
    },
    ...,
  }
```

Detailed description of each configuration option is described in next section.

### Options
Main options available.

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `address`                   | `String`                            | &#10004;       | Event Bus address of Http Repository Connector Verticle |
| `client.options`            | `HttpClientOptions`                 | &#10004;       | HTTP Client options used when communicating with the destination repository. See [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) to get all options supported.|
| `client.destination`        | `JsonObject`                        | &#10004;       | Allows to specify **domain** and **port** of the HTTP Repository endpoint |

### Destination options

| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `domain`      | `String`  | &#10004;       | Http Repository domain / IP |
| `port`        | `Number`  | &#10004;       | Http Repository port number |
# Filesystem Repository Connector section

Filesystem Repository Connector allows to fetch templates from local file storage. 

## How does it work?
The diagram below depicts Knot.x modules and request flow in more details.

[[assets/knotx-filesystem-repository.png|alt=Http Repository Connector]]

## How to configure?
Filesystem Repository Connector is deployed as a separate Verticle, depending on how it's deployed. 
You need to supply **Filesystem Repository Connector** configuration as JSON below if deployed as Filesystem Repository Connector Verticle fat jar.
```json
"address": "knotx.core.repository.filesystem",
"catalogue": ""
```
Or, above configuration wrapped in the JSON `config` section as shown below, if deployed using Knot.x starter verticle.
```json
  "verticles": {
    ...,
    "com.cognifide.knotx.repository.FilesystemRepositoryConnectorVerticle": {
      "config": {
         "PUT YOUR CONFIG HERE"
      }
    },
    ...,
  }
```

Detailed description of each configuration option is described in next section.

### Options
Main options available.

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `address`                   | `String`                            | &#10004;       | Event Bus address of Filesystem Repository Connector Verticle |
| `catalogue`                 | `String`                            |                | it determines where to take the resources from. If it's left empty, they will be taken from the classpath. It may be treated like a prefix to the requested resources. |
#HTML Fragment Splitter
HTML Fragment Splitter divides HTML template into static and dynamic chunks. Those chunks (fragments) 
goes to Knot context and can be processed later by [[Knots|Knot]].

##How does it work?
HTML Fragment Splitter gets a Knot context as input and responds with the modified Knot context. It 
divides HTML using regexp `<script\s+data-api-type\s*=\s*"([A-Za-z0-9-]+)"[^>]*>.+?</script>` into
static and dynamic fragments. So all `script` tags with a `data-api-type` attribute are converted to 
dynamic fragments. **According to performance reasons Splitter requires `data-api-type` 
attribute to be the first attribute in the `script` tag.**
All HTML markup outside script tags is considered as static fragments.

Fragment contains an *identifier, a content (a template chunk) and a context*. The *identifier* has `data-api-type` 
attribute value or `_raw` for static fragments. It can be used by Knots to select required fragment / fragments 
(performance enhancement) without additional snippet content processing. The *content* contains 
script tag with its content for dynamic fragments or static HTML content for static fragments. 
The *context* can be omitted at this moment.

At the end Splitter updates the Knot context with the list of fragments and returns it to further processing.

####Example
A site visitor requests for page *example.html* page. Knot.x fetches a page template from Repository and asks 
Splitter to retrieve fragments from the template: 
```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Knot.x example</title>
</head>
<body>
  <div class="row">
    <script data-api-type="templating"
            data-service="first-service"
            type="text/x-handlebars-template">
      <div class="col-md-4">
        <h2>Snippet - {{_result.message}}</h2>
      </div>
    </script>
  </div>
</body>
</html>
```
Splitter divides page into 3 following fragments:

**Fragment 1** (identifier = "_raw")
```
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Knot.x example</title>
</head>
<body>
  <div class="row">
```
**Fragment 2** (identifier = "templating")
```
    <script data-api-type="templating"
            data-service="first-service"
            type="text/x-handlebars-template">
      <div class="col-md-4">
        <h2>Snippet - {{_result.message}}</h2>
      </div>
    </script>
```
**Fragment 3** (identifier = "_raw")
```
  </div>
</body>
</html>
```

##How to configure?
Splitter is deployed as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html), 
depending on how it's deployed. You need to supply **Splitter** configuration.

JSON presented below is an example how to configure Splitter deployed as standalone fat jar:
```json
{
  "address": "knotx.core.splitter"
}
```
When deploying **Splitter** using Knot.x starter verticle, configuration presented above should 
be wrapped in the JSON `config` section:
```json
"verticles" : {
  ...,
  "com.cognifide.knotx.splitter.FragmentSplitterVerticle": {
    "config": {
      "PUT YOUR CONFIG HERE"
    }
  },
  ...,
}
```
Detailed description of each configuration option is described in the next subsection.

### Splitter options

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Splitter verticle. |
#Knot
Knot is a module which defines custom step during [[request routing|KnotRouting]]. It can process 
markup [[fragments|Splitter]], invoke [[Adapters|Adapter]] and redirect site visitor depending on 
Adapter response.

##How does it work?
Knot gets [Knot Context](#knot-context), does its job and responds with Knot Context. This is a very simple but 
powerful contract which makes Knot easy to integrate and develop.

Knot registers to Event Bus with an unique address and listens for [Knot Context events](#knot-request). 
Each Knot is deployed as a separate verticle and is triggered during [[request routing|KnotRouting]].

To understand what Knots really do we need to know what Knot Context is.

### Knot Context
Knot Context is a model which is exchanged between [[Server|Server]] and Knot. Server forward Knot Context
to Knots and gets Knot Context back from them. So Knot Context keeps information about site visitor 
request, current processing status and site visitor response. Next we will use *client* and
*site visitor* words equivalently.

Knot Context contains:
* client request with path, headers, form attributes and parameters
* client response with body, headers and status code
* [[fragments|Splitter]]
* transition

*Client request* includes site visitor path (requested URL), HTTP headers, form attributes 
(for POST requests) and request query parameters.

*Client response* includes a body (which represents final response body), HTTP headers (which are narrowed finally
by Server) and HTTP status code.

Please see [[Splitter|Splitter]] section to find out what fragments are and how they are produced. 
Fragments contain a template fragment content and a context. Knots can process a configured fragment content, 
call required Adapters and put responses from Adapters to the fragment context (fragment context is a JSON 
object).

Transition is a text value which determines next step in [[request routing|KnotRouting]].

#### Knot Request
A table below represents an event model consumed by Knot. First rows relates to client request attributes
which are not modifiable within Knots. Next, rows are connected with client response attributes and 
transition. Those rows are modified by Knots according to required behaviour (continue routing, redirect
to another url, return an error response).

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `clientRequest.path`                 | `String`                      | &#10004;       | client request url |
| `clientRequest.method`                 | `HttpMethod`                      | &#10004;       | client request method |
| `clientRequest.headers`                 | `MultiMap`                      | &#10004;       | client request headers |
| `clientRequest.params`                 | `MultiMap`                      | &#10004;       | client request parameters |
| `clientRequest.formAttributes`                 | `MultiMap`                      |       | form attributes, relevant to POST requests |
| `clientResponse.statusCode`                 | `HttpResponseStatus`                      |   &#10004;    | `HttpResponseStatus.OK` |
| `clientResponse.headers`                 | `MultiMap`                      | &#10004;       | client response headers |
| `clientResponse.body`                 | `Buffer`                      |        | final response body, can be empty until last Handlebars Knot |
| `fragments`                 | `List<Fragment>`                      |   &#10004;    | list of Fragments created by Splitter |
| `transition`                 | `String`                      |        | empty |


#### Knot Response 
Knot responds with Knot Context. So Knot Context from a request is consumed and updated according to required behaviour.

Knots are designed to process Knot Context and finally decides what next step in routing is valid.
It is the default Knot behaviour. Knots can also beak routing and decide to return an error or redirect 
response to the client.

A table below represents Knot response values.

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `clientRequest.path`                 | `String`                      | &#10004;       | client request url |
| `clientRequest.method`                 | `HttpMethod`                      | &#10004;       | client request method |
| `clientRequest.headers`                 | `MultiMap`                      | &#10004;       | client request headers |
| `clientRequest.params`                 | `MultiMap`                      | &#10004;       | client request parameters |
| `clientRequest.formAttributes`                 | `MultiMap`                      |       | form attributes, relevant to POST requests |
| `clientResponse.statusCode`               | `HttpResponseStatus`                      |    &#10004;    | `HttpResponseStatus.OK` to process routing, other to beak routing  |
| `clientResponse.headers`                 | `MultiMap`                      | &#10004;       | client response headers, can be updated by Knot |
| `clientResponse.body`                 | `Buffer`                      |        | final response body, can be empty until last Handlebars Knot |
| `fragments`                 | `List<Fragment>`                      |   &#10004;    | list of Fragments created by Splitter |
| `transition`                 | `String`                      |        | defines next routing step (Knot), empty for redirects, errors and last routing step |

##### Example Knot Responses
Knots can decide what next routing step is valid. They can also break the routing. This section shows
example responses.

*Next Routing Step*

Knot decides that routing should be continued. It sets `transition` to `next` and then Server continues 
routing according to its [[configuration|Server]].

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `HttpResponseStatus.OK`
| `transition`| `next` 

*Redirect response*

Knot finds out that client must be redirected to an other URL.

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `HttpResponseStatus.MOVED_PERMANENTLY`
| `clientResponse.headers`| `location: /new/location.html`
| `transition`| EMPTY 

*Error response*

Knot calls Adapter Service and gets HttpResponseStatus.INTERNAL_SERVER_ERROR. Knot is not
aware how this error should be processed so it sets clientResponse.statusCode to HttpResponseStatus.INTERNAL_SERVER_ERROR.
Server beaks routing and responds with HttpResponseStatus.INTERNAL_SERVER_ERROR to the client.

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `HttpResponseStatus.NOT_FOUND`
| `transition`| EMPTY 


##How to configure?
Knot API specifies abstract `KnotConfiguration` class to handle JSON configuration support. This
abstraction can be used while custom Knot implementation but it is not required. Every Knot must be
exposed with unique Event Bus address - that's the only obligation (the same like for Adapters).
Please see example configurations for [[Action Knot|ActionKnot#how-to-configure]], 
[[Service Knot|ServiceKnot#how-to-configure]].

##How to extend?
We need to extend abstract 
[com.cognifide.knotx.knot.api.AbstractKnotProxy](https://github.com/Cognifide/knotx/blob/master/knotx-knots/knotx-knot-api/src/main/java/com/cognifide/knotx/knot/api/AbstractKnot.java)
class from `knotx-knots/knotx-knot-api`. AbstractKnot hides Event Bus communication and JSON configuration initialization parts
and lets you to focus on Knot logic:

- `initConfiguration` method that initialize Knot with `JsonObject` model
- `process` method that consumes `KnotContext` messages from [[Server|Server]] and returns modified `KnotContext` messages
- `processError` method which handle particular Exception and prepare response for Server

| ! Note |
|:------ |
| Please note that this section focuses on Java language only. Thanks to [Vert.x polyglotism mechanism](http://vertx.io) you can implement your Adapters and Knots using other languages. |

# Action Knot
Action Knot is an [[Knot|Knot]] implementation responsible for forms submissions handling. It supports 
simple (without file upload) forms including redirection to successful pages and multi-step forms flows. 
It provides also a service error handling mechanism. 

##How does it work?
Action Knot is used with default Knot.x settings while both GET and POST client request processing. 
It transforms a form template to Knot.x agnostic one for GET requests. When client submits the form 
Action Knot calls configured [[Adapter|Adapter]] and based on its response redirect the client to a 
successful / error / next step page.

Let's describe Action Knot behaviour with following example.

### Example
ActionKnot processes only those fragments that `<script>` tag has defined `data-api-type="form-{NAME}"` parameter, 
where `{NAME}` is a unique name of a form (assuming there may be more than one form on a single page 
it is used to distinguish a requested snippet).

The client opens a `/content/local/login/step1.html` page. The final form markup returned by Knot.x looks like:

```html
<!-- start compiled snippet -->  
<form method="post">
  <input name="_frmId" value="1" type="hidden"> 
  <input name="email" value="" type="email"> 
  <input value="Submit" type="submit"> 
 </form><p>Please provide your email address</p> 
  
 <div> 
  <strong>Pro tip: All emails that starts with <kbd>john.doe</kbd> will be accepted.</strong> 
 </div>
 <!-- end compiled snippet -->
```

There are no Knot.x specific attributes in a final markup besides one **hidden input tag**. 

This is how form looks in the repository:

```html
<script data-api-type="form-1" type="text/x-handlebars-template">
  {{#if action._result.validationErrors}}
  <p class="bg-danger">Email address does not exists</p>
  {{/if}}
  <p>Please provide your email address</p>
  <form data-knotx-action="step1" data-knotx-on-success="/content/local/login/step2.html" data-knotx-on-error="_self" method="post">
    <input type="email" name="email" value="{{#if action._result.validationError}} {{action._result.form.email}} {{/if}}" />
    <input type="submit" value="Submit"/>
  </form>
  <div>
    <strong>Pro tip: All emails that starts with <kbd>john.doe</kbd> will be accepted.</strong>
  </div>
</script>
```

Now we can explain how and why this additional hidden input `_frmId` with a value `1` appears . It
is automatically added by Action Knot and is used to distinguish a requested form during submission process 
(there could be more than one form at the same template). Its value comes from a script's `data-api-type`
attribute - it retrieve a `{NAME}` value from `data-api-type="form-{NAME}"`.

Following data attributes are available in the `<form>` tag with described purpose:
- `data-knotx-action` - this is a name of an [[Action Adapter|ActionAdapter]] that will be used to handle submitted data. 
It is similar concept as `data-service-{NAME}` in [[Service Knot|ServiceKnot]]. In the example, 
Action Handler registered under name `step1` will handle this form data submission.
- `data-knotx-on-{SIGNAL}` - name of a [Signal](#Signal) that should be applied. In the example 
there is one signal success with the value `'/content/local/login/step2.html'` and one signal error 
with the value `'_self'`. Signal `'_self'` means that after error response (error signal returned) 
the client will stay on the same page.

### Signal
Signal is basically a decision about further request processing. Value of the signal can be either:
- `path` of a page that user should be redirected to after processing form submit,
- `_self` - that indicates that there will not be redirect, instead current page will be processed (generated view for instance). 
In other words, the page processing will be delegated to next [[Knot|Knot]] in the graph.

##How to configure?
Action Knot is deployed as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html), 
depending on how it's deployed. You need to supply **Action Knot** configuration.

JSON presented below is an example how to configure Action Knot deployed as standalone fat jar:
```json
{
  "address": "knotx.knot.action",
  "adapters": [
    {
      "name": "step1",
      "address": "knotx.adapter.action.http",
      "params": {
        "path": "/service/mock/post-step-1.json"
      },
      "allowed.request.headers": [
        "Cookie"
      ],
      "allowed.response.headers": [
        "Set-Cookie",
        "Location"
      ]
    },
    {
      "name": "step2",
      "address": "knotx.adapter.action.http",
      "params": {
        "path": "/service/mock/post-step-2.json"
      },
      "allowed.request.headers": [
        "Cookie"
      ],
      "allowed.response.headers": [
        "Set-Cookie",
        "Location"
      ]
    }
  ],
  "formIdentifierName": "_frmId"
}
```
When deploying **Action Knot** using Knot.x starter verticle, configuration presented above should 
be wrapped in the JSON `config` section:
```json
"verticles" : {
  ...,
  "com.cognifide.knotx.knot.action.ActionKnotVerticle": {
    "config": {
      "PUT YOUR CONFIG HERE"
    }
  },
  ...,
}
```
Detailed description of each configuration option is described in the next subsection.

### Action Knot options

Main Action Knot options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Action Knot verticle. |
| `adapters`                  | `Array of AdapterMetadata`          | &#10004;       | Array if [AdapterMetadata](https://github.com/Cognifide/knotx/blob/master/knotx-core/knotx-knot-action/src/main/java/com/cognifide/knotx/knot/action/ActionKnotConfiguration.java) |
| `formIdentifierName`        | `String`                            | &#10004;       | Name of the hidden input tag which is added by Action Knot. |

Adapter metadata options available. Take into consideration that Adapters are used only for POST requests.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `name`                      | `String`                            | &#10004;       | Name of [[Adapter|Adapter]] which is referenced in `data-knotx-action`. |
| `address`                   | `Array of AdapterMetadata`          | &#10004;       | Event bus address of the **Adapter** verticle |
| `params`                    | `JSON object`                       | &#10004;       | Default params which are sent to Adapter. |
| `allowed.request.headers`   | `String`                            | &#10004;       | Array of HTTP client request headers that are allowed to be passed to Adapter. **No** request headers are allowed if not set. |
| `allowed.response.headers`  | `String`                            | &#10004;       | Array of HTTP response headers that are allowed to be sent in a client response. **No** response headers are allowed if not set. |

# Service Knot
Service Knot is a [[Knot|Knot]] implementation responsible for asynchronous Adapter calls to fetch the
data that will be later used to compose page final markup with [[Handlebars Knot|HandlebarsKnot]].

##How does it work?
Service Knot retrieves [[dynamic fragments|Splitter]] from [[Knot Context|Knot]]. Then for every dynamic
fragment it calls configured Adapters. At the end it collects responses from those 
Adapters and expose them in [[Knot Context|Knot]]. Let's describe how Adapters are invoked with 
following example.

Adapters calls are defined both on template and Knot configuration layers:

First Service Knot collects `data-service-{NAMESPACE}={ADAPTERNAME}` attributes which define accordingly:
 - namespace under which Adapter response will be available,
 - name of the Adapter tha will be called during snippet processing. 

Additionally with every Adapter `data-params-{NAMESPACE}={JSON DATA}` attribute can be defined 
which specifies parameters for Adapter call. An example `script` definition can look like:

```html
<script data-api-type="templating"
  data-service="first-service"
  data-service-second="second-service"
  data-params-second='{"path":"/overridden/path"}'
  type="text/x-handlebars-template">
```
Service Knot will call two Adapters with names: `first-service` and `second-service`.

Now we need to combine the service name with Adapter service address. This link is configured within
Service Knot configuration in `services` part. See example below:
```
"services": [
  {
    "name" : "first-service",
    "address" : "knotx.adapter.service.http",
    "params": {
      "path": "/service/mock/first.json"
    },
    "cacheKey": "first"
  },
  {
    "name" : "second-service",
    "address" : "knotx.adapter.service.http",
    "params": {
      "path": "/service/mock/second.json"
    }
  }
]
```
The configuration contains also params attribute which defines default parameter value which is passed
to Adapter. It can be overridden at template layer like in the example above. When `second-service`
Adapter will be called it will get `path` parameter from `params` with overridden value `{'path':'/overridden/path'}`
instead of default `"path": "/service/mock/second.json"`.

Now all Adapter calls are ready to perform. Knot.x fully uses asynchronous programming principles so
those calls have also asynchronous natures. It is visualized on diagram below.

[[assets/knotx-modules-advanced-request-flow.png|alt=Knot.x Request Flow]]

### Adapter Calls Caching
Template might consists of more than one Adapter call. It's also possible that there are multiple 
fragments on the page, each using same Adapter call. Knot.x does caching results of Adapter calls 
to avoid multiple calls for the same data.
Caching is performed within page request scope, this means another request will not get cached data.

## How to configure?
Service Knot is deployed as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html), 
depending on how it's deployed. You need to supply **Service Knot** configuration.

JSON presented below is an example how to configure Service Knot deployed as standalone fat jar:
```json
{
  "address": "knotx.knot.service",
  "client.options": {
    "maxPoolSize": 1000,
    "keepAlive": false
  },
  "services": [
    {
      "name" : "mock",
      "address" : "mock-service-adapter",
      "config": {
        "path": "/service/mock/.*"
      }
    }
  ]
}
```
When deploying **Service Knot** using Knot.x starter verticle, configuration presented above should 
be wrapped in the JSON `config` section:
```json
"verticles" : {
  ...,
  "com.cognifide.knotx.knot.service.ServiceKnotProxyVerticle": {
    "config": {
      "PUT YOUR CONFIG HERE"
    }
  },
  ...,
}
```
Detailed description of each configuration option is described in the next subsection.

### Service Knot options

Main Service Knot options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Service Knot verticle. |
| `client.options`            | `String`                            | &#10004;       | JSON representation of [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) configuration for [HttpClient](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClient.html) |
| `services`                  | `Array of ServiceMetadata`          | &#10004;       | Array of [ServiceMetadata](https://github.com/Cognifide/knotx/blob/master/knotx-core/knotx-knot-view/src/main/java/com/cognifide/knotx/knot/service/ServiceKnotConfiguration.java).|

Service (Adapter) metadata options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `name`                      | `String`                            | &#10004;       | Name of [[Adapter|Adapter]] which is referenced in `data-service-{NAMESPACE}={ADAPTERNAME}`. |
| `address`                   | `String`                            | &#10004;       | Event bus address of the **Adapter** verticle. |
| `params`                    | `JSON object`                       | &#10004;       | Default params which are sent to Adapter. |
| `cacheKey`                  | `String`                            |                | Cache key which is used for Adapters calls caching. **No** means that cache key has value `{NAME}|{PARAMS}` |

# Handlebars Knot
Handlebars Knot is a [[Knot|Knot]] implementation responsible for handlebars template processing.

##How does it work?
Handlebars Knot uses [Handlebars.js](http://handlebarsjs.com/) templating engine. More specifically it utilizes
Handlebars Java port - [Handlebars.java](https://github.com/jknack/handlebars.java) to compile and evaluate
templates.

Handlebars Knot retrieves all [[dynamic fragments|Splitter]] from [[Knot Context|Knot]]. Then for each fragment
it evaluates Handlebars template fragment using fragment context. In this way data from other [[Knots|Knot]]
can be applied to Handlebars snippets.

###Example
Example Knot Context contains:
```
{
  "_result": {
    "message":"this is webservice no. 1",
    ...
  },
  "_response": {
    "statusCode":"200"
  },
  "second": {
    "_result": {
      "message":"this is webservice no. 2",
    ...
    },
    "_response": {
      "statusCode":"200"
    }
  }
}
```
It can be reflected in Handlebars templates like:
```html
<div class="col-md-4">
  <h2>Snippet1 - {{second._result.message}}</h2>
  <div>Snippet1 - {{second._result.body.a}}</div>
  {{#string_equals second._response.statusCode "200"}}
    <div>Success! Status code : {{second._response.statusCode}}</div>
  {{/string_equals}}
</div>
```

## How to configure?
Handlebars Knot is deployed as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html), 
depending on how it's deployed. You need to supply **Handlebars Knot** configuration.

JSON presented below is an example how to configure Handlebars Knot deployed as standalone fat jar:
```json
{
  "address": "knotx.knot.handlebars",
  "template.debug": true
}
```
When deploying **Handlebars Knot** using Knot.x starter verticle, configuration presented above should 
be wrapped in the JSON `config` section:
```json
"verticles" : {
  ...,
  "com.cognifide.knotx.knot.templating.HandlebarsKnotProxyVerticle": {
    "config": {
      "PUT YOUR CONFIG HERE"
    }
  },
  ...,
}
```
Detailed description of each configuration option is described in the next subsection.

### Handlebars Knot options

Main Handlebars Knot options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Handlebars Knot verticle. |
| `template.debug`            | `Boolean`                           | &#10004;       | Template debug enabled option.|

## How to extend?

### Extending handlebars with custom helpers

If the list of available handlebars helpers is not enough, you can easily extend it. To do this the 
following actions should be undertaken:

1. Create a class implementing ```com.cognifide.knotx.handlebars.CustomHandlebarsHelper``` interface. 
This interface extends [com.github.jknack.handlebars.Helper](https://jknack.github.io/handlebars.java/helpers.html)
2. Register the implementation as a service in the JAR file containing the implementation
    * Create a configuration file called META-INF/services/com.cognifide.knotx.handlebars.CustomHandlebarsHelper 
    in the same project as your implementation class
    * Paste a fully qualified name of the implementation class inside the configuration file. If you're 
    providing multiple helpers in a single JAR, you can list them in new lines (one name per line is allowed) 
    * Make sure the configuration file is part of the JAR file containing the implementation class(es)
3. Run Knot.x with the JAR file in the classpath

#### Example extension

Sample application contains an example custom Handlebars helper - please take a look at the implementation of ```BoldHelper```:
* Implementation class: ```com.cognifide.knotx.example.monolith.handlebars.BoldHelper```
* service registration: ```knotx-example-monolith/src/main/resources/META-INF/services/com.cognifide.knotx.handlebars.CustomHandlebarsHelper```

# Adapters
Adapters are modules which are responsible for communication between Knot.x (exactly [[Knots|Knot]]) 
and external services.

[[assets/knotx-adapters.png|alt=Adapters]]


## How does it work?
Adapters can be thought as extension points where project specific logic appears. With custom [[Knots|Knot]] 
they provides very flexible mechanism to inject project specific requirements.

We recommend to create dedicated Adapter every time some service-level business logic or service 
response adaption to other format is required.


### Types of adapters
Knot.x Core by default introduces two types of Adapters connected with Knot implementations:
- [[Service Adapter|ServiceAdapter]] for [[Service Knot|ServiceKnot]],
- [[Action Adapter|ActionAdapter]] for [[Action Knot|ActionKnot]]

Knot.x comes with a generic implementation of [[Service Adapter|ServiceAdapter]], that enables communication 
with external services using HTTP Protocol (only GET requests). See [[Http Service Adapter|HttpServiceAdapter]] 
for more information. Please note, that this implementation is very generic and we recommend to create 
project-specific Adapters for any custom requirements.

Action Adapters are project specific in terms of error handling and redirection mechanisms. Knot.x Core
is not going to provide any generic Action Adapters.

For custom Knots we can introduce custom Adapter types. As far as Knots must follow [[Knot contract|Knot#how-does-it-work]],
Adapters are coupled with Knot directly so they can define their custom request, response or 
configuration. The communication between Knot and Adapter can be custom too. 

## How to configure?
Adapter API specifies abstract `AdapterConfiguration` class to handle JSON configuration support. This
abstraction can be used while custom Adapter implementation but it is not required. Every Adapter must be
exposed with unique Event Bus address - that's the only obligation (the same like for Knots).
Please see example configuration for [[Http Service Adapter|HttpServiceAdapter#how-to-configure]]

## How to extend?
First we need to extend abstract 
[com.cognifide.knotx.adapter.api.AbstractAdapter](https://github.com/Cognifide/knotx/blob/master/knotx-adapters/knotx-adapter-api/src/main/java/com/cognifide/knotx/adapter/api/AbstractAdapter.java)
class from `knotx-adapters/knotx-adapter-api`. AbstractAdapter hides Event Bus communication and JSON configuration reading parts
and lets you to focus on Adapter logic:

- `initConfiguration` method that initialize Adapter with `JsonObject` model
- `processMessage` method that consumes `AdapterRequest` messages from [[Knot|Knot]] and returns `AdapterResponse` messages

To deal with configuration model you may extend
[com.cognifide.knotx.adapter.api.AdapterConfiguration](https://github.com/Cognifide/knotx/blob/master/knotx-adapters/knotx-adapter-api/src/main/java/com/cognifide/knotx/adapter/api/AdapterConfiguration.java) class
from `knotx-adapters/knotx-adapter-api`.

Reference implementation of `com.cognifide.knotx.adapter.api.AbstractAdapter` is [[Http Service Adapter|HttpServiceAdapter]] from
`knotx-adapters/knotx-adapter-service-http` module.

| ! Note |
|:------ |
| Please note that this section focused on Java language only. Thanks to [Vert.x polyglotism mechanism](http://vertx.io) you can implement your Adapters and Knots using language you like. |

### Configuration file
Adapter could have its JSON configuration file that will be passed to `initConfiguration` method in form of `JsonObject`.
You may read more about example configuration for `HttpServiceAdapterVerticle` in [[Http Service Adapter|HttpServiceAdapter]] section.

### Adapters common library
For many useful and reusable Adapters concept, please check our [knotx-adapter-common](https://github.com/Cognifide/knotx/tree/master/knotx-adapters/knotx-adapter-common)
module. You will find there support for `placeholders` and `http connectivity`. 

### How to run a custom Adapter with Knot.x
Please refer to [[Deployment|KnotxDeployment]] section to find out more about deploying and running 
a custom Adapters with Knot.x.

# Service Adapter
Service Adapter is Component of a system, that mediate communication between Knot.x [[Service Knot|ServiceKnot]]
and external services that deliver data injected into template. In short, Service Adapter acts as a
`proxy` between Knot.x and external services.

## How does it work?
Service Adapter accepts message with the following data:

- `clientRequest` - object with all data of an original request,
- `params` - all additional params defined in configuration.

Result generated by Action Adapter must be a JsonObject with the fields as below:
- `clientResponse` - Json Object, `body` field of this response is suppose to carry on the actual response from the mocked service,

Find out more about contract described above in [[Service Knot|ServiceKnot]] section.

## How to configure?
Configuration of Service Adapter is specific to its implementation. You may see example configuration 
of [[Http Service Adapter|HttpServiceAdapter#how-to-configure]].

## How to extend?
Implementing custom Service Adapter that meet your project requirements allows 
you to adopt request from Knot.x into request understandable by an endpoint service, and adopts 
responses from that service into unified message understandable by Knot.x.
For you convenience, Knot.x comes with implementation of a [[Service Adapter|ServiceAdapter]], 
that enables communication with external services using HTTP Protocol. 
See [[Http Service Adapter|HttpServiceAdapter]] for more information.
Please note, that this implementation is very generic and we recommend to create project-specific 
adapters for any custom solution.

Writing custom Service Adapter requires fulfilling [[Service Knot|ServiceKnot]] contract.
Please refer also to [[Adapter|Adapter#how-to-extend]].

# Action Adapter
Action Adapter is Component of a system, that mediates communication between Knot.x [[Action Knot|ActionKnot]] 
and external Services that are responsible for handling form submissions.


## How does it work?
Action Adapter accepts message with the following data:

- `clientRequest` - object with all data of an original request,
- `params` - all additional params defined in Action Knot configuration (Adapter metadata).

Result generated by Action Adapter must be a JsonObject with the fields as below:
- `clientResponse` - Json Object, `body` field of this response is suppose to carry on the actual 
response from the mocked service,
- `signal` - string that defines how original request processing should be handled.

Find out more about contract described above in [[Action Knot|ActionKnot]].

## How to configure?
Configuration of Action Adapter is specific to its implementation. You may see example configuration 
of [[Http Service Adapter|HttpServiceAdapter#how-to-configure]].

## How to extend?
Implementing Action Adapter that meets your project requirements allows 
you to adopt request from Knot.x into request understandable by an endpoint service, and adopts 
responses from that service into unified message understandable by Knot.x.
For you convenience, Knot.x is shipped with a default Action Adapter called `HttpActionAdapter`.

Writing custom Service Adapter requires fulfilling [[Action Knot|ActionKnot]] contract.
Please refer also to [[Adapter|Adapter#how-to-extend]].

# Http Service Adapter
Http Service Adapter is an example of Adapter implementation embedded in Knot.x.
It enables communication between [[Service Knot|ServiceKnot]] and external services via HTTP.

## How does it work?
When Http Service Adapter starts processing a message from Event Bus, it expects following input:
- `clientRequest` - JSON object that contains client request (contains e.g. headers, params, formAttributes etc.).
- `params` - JSON object that contains additional parameters, among those parameter mandatory `path` parameter should be defined.

### Service path
`path` parameter is a mandatory parameter that must be passed to Http Service Adapter. 
It defines request path and may contain [placeholders](#parametrized-service-calls).

### Parametrized services calls
When found a placeholder within the `path` parameter it will be replaced with a dynamic value based on the 
current http request (data from `clientRequest`). Available placeholders are:
* `{header.x}` - is the client requests header value where `x` is the header name
* `{param.x}` - is the client requests query parameter value. For `x` = q from `/a/b/c.html?q=knot` it will produce `knot`
* `{uri.path}` - is the client requests sling path. From `/a/b/c.sel.it.html/suffix.html?query` it will produce `/a/b/c.sel.it.html/suffix.html`
* `{uri.pathpart[x]}` - is the client requests `x`th sling path part. For `x` = 2 from `/a/b/c.sel.it.html/suffix.html?query` it will produce `c.sel.it.html`
* `{uri.extension}` - is the client requests sling extension. From `/a/b/c.sel.it.html/suffix.xml?query` it will produce `xml`
* `{slingUri.path}` - is the client requests sling path. From `/a/b/c.sel.it.html/suffix.html?query` it will produce `/a/b/c`
* `{slingUri.pathpart[x]}` - is the client requests `x`th sling path part. For `x` = 1 from `/a/b/c.sel.it.html/suffix.html?query` it will produce `b`
* `{slingUri.selectorstring}` - is the client requests sling selector string. From `/a/b/c.sel.it.html/suffix.html?query` it will produce `sel.it`
* `{slingUri.selector[x]}` - is the client requests `x`th sling selector. For `x` = 1 from `/a/b/c.sel.it.html/suffix.html?query` it will produce `it`
* `{slingUri.extension}` - is the client requests sling extension. From `/a/b/c.sel.it.html/suffix.xml?query` it will produce `html`
* `{slingUri.suffix}` - is the client requests sling suffix. From `/a/b/c.sel.it.html/suffix.html?query` it will produce `/suffix.html`

All placeholders are always substituted with encoded values according to the RFC standard. However, there are two exceptions:

- Space character is substituted by `%20` instead of `+`.
- Slash character `/` remains as it is.

### Adapter Response
Http Service Adapter replies with `ClientResponse` that contains:

| Parameter       | Type                      |  Description  |
|-------:         |:-------:                  |-------|
| `statusCode`    | `HttpResponseStatus       | status code of a response from external service (e.g. `200 OK`) |
| `headers`       | `MultiMap`                | external service response headers |
| `body`          | `Buffer`                  | external service response, **please notice that it is expected, tha form of a response body from an external service is JSON** |

## How to configure?
Http Service Adapter is deployed as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html), 
depending on how it's deployed.
Http Service Adapter can be configured to support multiple external `services`, see example configuration:

```json
{
  "address": "knotx.adapter.service.http",
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
```

- `address` is the where adapter listen for events at Event Bus. Every event that will be sent at `knotx.adapter.service.http`
will be processed by Http Service Adapter.
- `client.options` are [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) used to configure HTTP connection. 
Any HttpClientOption may be defined in this section, at this example two options are defined: 
  - `maxPoolSize` -  maximum pool size for simultaneous connections,
  - `keepAlive` - that shows keep alive should be disabled on the client.
- `services` - an JSON array of services that Http Service Adapter can connect to. Each service is distinguished by `path` parameter which is regex.
In example above, two services are configured:
  - `/service/mock/.*` that will call `http://localhost:3000` domain with defined [path](#service-path),
  - `/service/.*` that will call `http://localhost:8080` domain with defined [path](#service-path).


## Example
Assuming, that Http Service Adapter was configured as presented in [Example configuration](#how-to-configure) and:

#### Service Knot configuration
Example configuration of a [[Service Knot|ServiceKnot]]:
```json
"com.cognifide.knotx.knot.service.ServiceKnotProxyVerticle": {
  "config": {
    "address": "knotx.knot.service",
    "client.options": {
      "maxPoolSize": 1000,
      "keepAlive": false
    },
    "services": [
      {
        "name" : "search",
        "address" : "knotx.adapter.service.http",
        "params": {
          "path": "/service/solr/search?q={param.q}"
        }
      },
      {
        "name" : "twitter",
        "address" : "knotx.adapter.service.http",
        "params": {
          "path": "/service/twitter/user/{header.userId}"
        }
      }
    ]
  }
}
```

#### snippet
Example html snippet in template:

```html
<script data-api-type="templating" type="text/x-handlebars-template"
    data-service-search="search"
    data-service-twitter="twitter">
        <h1>Welcome</h1>
        <h2>{{search.numberOfResults}}</h2>
        <h2>{{twitter.userName}}</h2>
</script>
```

#### request

- `path`: http://knotx.example.cognifide.com/search?q=hello
- `headers`: `[userId=johnDoe]`.

### Processing
When Knot.x resolves this request, Http Service Adapter will be called twice when example snipped is processed:

##### search service
Http Service Adapter request parameters should look like:

```json
{
  "clientRequest": {
    "path": "http://knotx.example.cognifide.com/search?q=hello",
    "headers": {
      "userId": "johnDoe"
    },
    "params": {
      "q": "hello"
    },
    "method": "GET"
  },
  "params": {
    "path": "/service/solr/search?q={param.q}"
  }
}
```

Http Service Adapter will lookup if `params.path` is supported and 2nd service from [Example configuration](#how-to-configure) `services` will be a match.
Next, `params.path` placeholders are resolved, and request to `http://localhost:8080/service/solr/search?q=hello` is made.
In response, external service returns: 

```json
{
  "numberOfResults": 2,
  "documents": [
    {"title": "first"},
    {"title": "second"}
  ]
}
```

which is finally wrapped into [Adapter Response](#adapter-response).

##### twitter service
Http Service Adapter request parameters should look like:

```json
{
  "clientRequest": {
    "path": "http://knotx.example.cognifide.com/search?q=hello",
    "headers": {
      "userId": "johnDoe"
    },
    "params": {
      "q": "hello"
    },
    "method": "GET"
  },
  "params": {
    "path": "/service/twitter/user/{header.userId}"
  }
}
```

Http Service Adapter will lookup if `params.path` is supported and 2nd service from [Example configuration](#how-to-configure) `services` will be a match.
Next, `params.path` placeholders are resolved, and request to `http://localhost:8080/service/twitter/user/johnDoe` is made.
In response, external service returns: 

```json
{
  "userName": "John Doe",
  "userId": "1203192031",
  "lastTweet": "27.10.2016"
}
```

which is finally wrapped into [Adapter Response](#adapter-response).

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

# Dependencies

- io.vertx
- io.reactivex
- org.jsoup
- com.github.jknack.handlebars
- com.google.code.gson
- guava
- commons-lang3
- junit

# What's new

* View Knot is divided into Service Knot and Handlebars Knot.
* KnotContext, AdapterRequest, AdapterResponse uses message codes which allows custom messages
to be marshalled across the event bus. Performance improvement.

