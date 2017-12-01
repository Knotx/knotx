# Wiki for Knot.x 1.0.1
This documentation is dedicated for more advanced users and developers. Here you can find information how to extend Knot.x, how to tune particular 
modules according your needs. Additionally wiki contains base information how set up Knot.x and run examples.

User documentation is available at [http://knotx.io](http://knotx.io/).

- [Getting started](#getting-started)
  - [First steps](#first-steps)
  - [Getting Binaries](#getting-binaries)
  - [Hello world!](#hello-world)
  - [Building](#building)
- [Running Knot.x Demo](#running-knotx-demo)
  - [Requirements](#requirements)
  - [Running the demo](#running-the-demo)
  - [Reconfigure demo](#reconfigure-demo)
    - [Use starter JSON](#use-starter-json)
    - [Use JVM properties](#use-jvm-properties)
    - [Conclusions](#conclusions)
- [Debugging Knot.x Demo](#debugging-knotx-demo)
  - [Requirements](#requirements-1)
  - [How to set up Knot.x debugging in Intellij IDE](#how-to-set-up-knotx-debugging-in-intellij-ide)
- [Architecture](#architecture)
- [High Level Architecture](#high-level-architecture)
- [Knot.x Core Architecture](#knotx-core-architecture)
- [Communication Flow](#communication-flow)
- [Knot Routing](#knot-routing)
- [Gateway Mode](#gateway-mode)
  - [Example configuration](#example-configuration)
- [Knot.x Module](#knotx-module)
- [Server](#server)
  - [How does it work?](#how-does-it-work)
    - [Routing](#routing)
  - [How to configure?](#how-to-configure)
  - [Server options](#server-options)
    - [KnotxFlowConfiguration options](#knotxflowconfiguration-options)
    - [RepositoryEntry options](#repositoryentry-options)
    - [VerticleEntry options](#verticleentry-options)
    - [RoutingEntry options](#routingentry-options)
    - [KnotRouteEntry options](#knotrouteentry-options)
- [Repository Connectors](#repository-connectors)
  - [How does it work?](#how-does-it-work-1)
- [HTTP Repository Connector](#http-repository-connector)
  - [How does it work?](#how-does-it-work-2)
  - [How to configure?](#how-to-configure-1)
    - [Options](#options)
    - [Destination options](#destination-options)
- [Filesystem Repository Connector section](#filesystem-repository-connector-section)
  - [How does it work?](#how-does-it-work-3)
  - [How to configure?](#how-to-configure-2)
    - [Options](#options-1)
- [HTML Fragment Splitter](#html-fragment-splitter)
    - [Example](#example)
    - [Fragment](#fragment)
  - [How to configure?](#how-to-configure-3)
    - [Splitter config](#splitter-config)
- [Fragment Assembler](#fragment-assembler)
  - [How does it work?](#how-does-it-work-4)
    - [How Fragments are being joined?](#how-fragments-are-being-joined)
    - [How does Assembler join unprocessed Fragments?](#how-does-assembler-join-unprocessed-fragments)
      - [AS_IS strategy](#as_is-strategy)
      - [UNWRAP strategy](#unwrap-strategy)
      - [IGNORE strategy](#ignore-strategy)
  - [How to configure?](#how-to-configure-4)
    - [Fragment Assembler config](#fragment-assembler-config)
- [Knot](#knot)
  - [How does it work?](#how-does-it-work-5)
    - [Knot Election Rule](#knot-election-rule)
    - [Knot Context](#knot-context)
      - [Knot Request](#knot-request)
      - [Knot Response](#knot-response)
        - [Example Knot Responses](#example-knot-responses)
  - [How to configure?](#how-to-configure-5)
  - [How to implement your own Knot?](#how-to-implement-your-own-knot)
- [Action Knot](#action-knot)
  - [How does it work?](#how-does-it-work-6)
    - [Example](#example-1)
    - [Signal](#signal)
  - [How to configure?](#how-to-configure-6)
    - [Action Knot options](#action-knot-options)
- [Service Knot](#service-knot)
  - [How does it work?](#how-does-it-work-7)
    - [Adapter Calls Caching](#adapter-calls-caching)
  - [How to configure?](#how-to-configure-7)
    - [Service Knot options](#service-knot-options)
- [Handlebars Knot](#handlebars-knot)
  - [How does it work?](#how-does-it-work-8)
    - [Example](#example-2)
  - [How to configure?](#how-to-configure-8)
    - [Handlebars Knot options](#handlebars-knot-options)
  - [How to extend?](#how-to-extend)
    - [Extending handlebars with custom helpers](#extending-handlebars-with-custom-helpers)
      - [Example extension](#example-extension)
- [Adapters](#adapters)
  - [How does it work?](#how-does-it-work-9)
    - [Types of adapters](#types-of-adapters)
  - [How to configure?](#how-to-configure-9)
  - [How to extend?](#how-to-extend-1)
    - [Adapters common library](#adapters-common-library)
    - [How to run a custom Adapter with Knot.x](#how-to-run-a-custom-adapter-with-knotx)
- [Service Adapter](#service-adapter)
  - [How does it work?](#how-does-it-work-10)
  - [How to configure?](#how-to-configure-10)
  - [How to extend?](#how-to-extend-2)
- [Action Adapter](#action-adapter)
  - [How does it work?](#how-does-it-work-11)
      - [Service path](#service-path)
    - [Adapter Response](#adapter-response)
  - [How to configure?](#how-to-configure-11)
  - [How to extend?](#how-to-extend-3)
- [Http Service Adapter](#http-service-adapter)
  - [How does it work?](#how-does-it-work-12)
    - [Service path](#service-path-1)
    - [Service params and additional headers](#service-params-and-additional-headers)
    - [Parametrized services calls](#parametrized-services-calls)
    - [Adapter Response](#adapter-response-1)
  - [How to configure?](#how-to-configure-12)
      - [Service Knot configuration](#service-knot-configuration)
      - [snippet](#snippet)
      - [request](#request)
    - [Processing](#processing)
        - [search service](#search-service)
        - [twitter service](#twitter-service)
        - [Setting service query parameters](#setting-service-query-parameters)
- [Mocks](#mocks)
  - [HTTP Service mock](#http-service-mock)
    - [How does it work?](#how-does-it-work-13)
    - [How to configure ?](#how-to-configure-)
  - [HTTP Remote repository mock](#http-remote-repository-mock)
    - [How does it work?](#how-does-it-work-14)
    - [How to configure ?](#how-to-configure--1)
  - [How to configure delay ?](#how-to-configure-delay-)
    - [Delay all responses](#delay-all-responses)
    - [Delay reponses for specific paths](#delay-reponses-for-specific-paths)
- [Deploying Knot.x with custom modules](#deploying-knotx-with-custom-modules)
  - [Recommended Knot.x deployment](#recommended-knotx-deployment)
    - [Vert.x metrics](#vertx-metrics)
  - [How to configure ?](#how-to-configure--2)
    - [How to configure though starter JSON ?](#how-to-configure-though-starter-json-)
    - [How to configure through JVM properties ?](#how-to-configure-through-jvm-properties-)
    - [How to configure your own module ?](#how-to-configure-your-own-module-)
- [Dependencies](#dependencies)
- [Upgrade notes](#upgrade-notes)

# Getting started

## First steps

See our first blog post [Hello Rest Service](http://knotx.io/blog/hello-rest-service/) which is a great entry point to the Knot.x world.
See other [Knot.x tutorial blogs](http://knotx.io/blog/) to learn more.

## Getting Binaries
Knot.x binaries and dependency information for Maven, Ivy, Gradle and others can be found at 
[http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.vertx%22).

Besides jar files with the modules implementations, there are available additional resources 
such as far.jar (bundled with all dependencies allowing to run itself) and configuration json file, 
for three modules:
- knotx-mocks
- knotx-standalone
- knotx-sample-app

To run Knot.x you need Java 8.

## Hello world!

First download Knot.x sample app & config for latest version, or build it yourself (see [[Building|GettingStarted#building]] section):
- [knotx-example-app-X.Y.Z.fat.jar](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-example-app)
- [knotx-example-app-X.Y.Z.json](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-example-app)

Now you can run Knot.x:
```
java -jar knotx-example-app-X.Y.Z-fat.jar -conf knotx-example-app-X.Y.Z.json
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
[INFO] Knot.x ............................................. SUCCESS [  2.823 s]
[INFO] Knot.x - Reactive microservice assembler - Wiki Documentation SUCCESS [  0.763 s]
[INFO] Knot.x - Reactive microservice assembler - Core .... SUCCESS [  8.878 s]
[INFO] Knot.x - Reactive microservice assembler - JUnit Tests Knot.x helpers SUCCESS [  0.774 s]
[INFO] Knot.x - Reactive microservice assembler - Mocks ... SUCCESS [  3.298 s]
[INFO] Knot.x - Reactive microservice assembler - Repositories Connector SUCCESS [  0.112 s]
[INFO] Knot.x - Reactive microservice assembler - Repositories Connector - Filesystem SUCCESS [  4.231 s]
[INFO] Knot.x - Reactive microservice assembler - Repositories Connector - HTTP SUCCESS [  3.256 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter . SUCCESS [  0.073 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter - Common SUCCESS [  2.357 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter - Service HTTP SUCCESS [  7.030 s]
[INFO] Knot.x - Reactive microservice assembler - Knot .... SUCCESS [  0.162 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - HTML Fragment Splitter SUCCESS [  5.880 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - Fragment Assembler SUCCESS [  5.774 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - Action SUCCESS [  5.390 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - Service SUCCESS [  4.296 s]cd 
[INFO] Knot.x - Reactive microservice assembler - Knot - Handlebars SUCCESS [  2.742 s]
[INFO] Knot.x - Reactive microservice assembler - Knot.x HTTP Server SUCCESS [  5.971 s]
[INFO] Knot.x - Reactive microservice assembler - Standalone Knot.x SUCCESS [  2.375 s]
[INFO] Knot.x - Reactive microservice assembler - Example . SUCCESS [  0.128 s]
[INFO] Knot.x - Reactive microservice assembler - Example - Sample Handlebars Extension SUCCESS [  0.382 s]
[INFO] Knot.x - Reactive microservice assembler - Example - Action Adapter HTTP SUCCESS [  5.860 s]
[INFO] Knot.x - Reactive microservice assembler - Example - Sample Monolith App SUCCESS [  7.708 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:20 min
[INFO] Finished at: 2017-01-18T09:02:01+01:00
[INFO] Final Memory: 82M/901M
[INFO] ------------------------------------------------------------------------
```

See also [[how to run Knot.x demo|RunningTheDemo]] for the details how to run and configure the demo.

In case you wanted to try debugging Knot.x in IDE, see [[Debugging Knot.x|Debugging]].


# Running Knot.x Demo

## Requirements

To run Knot.x you only need Java 8.
To build it you also need Maven (version 3.3.1 or higher).

## Running the demo
To run an [Example app](https://github.com/Cognifide/knotx/blob/master/knotx-example/knotx-example-app) do the following:

Build Knot.x with Example app:

```
$ git clone https://github.com/Cognifide/knotx.git
$ cd knotx
$ mvn clean install
```

or download [released](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-example-app) application `knotx-example-app-X.Y.Z.fat.jar` and configuration file `knotx-example-app-X.Y.Z.json`.

Run example app:
```
$ cd knotx-example/knotx-example-app
$ java -jar target/knotx-example-app-X.Y.Z-SNAPSHOT-fat.jar -conf src/main/resources/knotx-example-app.json
```
Where:
- `knotx-example-app.json` file is **starter** JSON of Knot.x. This file simply defines what Knot.x services (Verticles) should be started. It's also possible to amend default configuration of Knot.x in this file.

You will see output similar to the following:
```
...
2017-01-18 09:18:40 [vert.x-eventloop-thread-1] INFO  io.knotx.server.KnotxServerVerticle - Starting <io.knotx.server.KnotxServerVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-2] INFO  i.k.r.HttpRepositoryConnectorVerticle - Starting <HttpRepositoryConnectorVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-3] INFO  i.k.r.FilesystemRepositoryConnectorVerticle - Starting <FilesystemRepositoryConnectorVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-0] INFO  i.k.knot.action.ActionKnotVerticle - Starting <io.knotx.knot.action.ActionKnotVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-4] INFO  i.k.s.FragmentSplitterVerticle - Starting <io.knotx.splitter.FragmentSplitterVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-5] INFO  i.k.k.a.FragmentAssemblerVerticle - Starting <io.knotx.knot.assembler.FragmentAssemblerVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-6] INFO  i.k.k.t.HandlebarsKnotVerticle - Starting <io.knotx.knot.templating.HandlebarsKnotVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-7] INFO  i.k.knot.service.ServiceKnotVerticle - Starting <io.knotx.knot.service.ServiceKnotVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-4] INFO  io.knotx.mocks.MockServiceVerticle - Starting <MockServiceVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-3] INFO  i.k.m.MockRemoteRepositoryVerticle - Starting <MockRemoteRepositoryVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-5] INFO  i.k.mocks.MockServiceAdapterVerticle - Starting <MockServiceAdapterVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-6] INFO  i.k.k.t.impl.HandlebarsKnotProxyImpl - Registered custom Handlebars helper: bold
2017-01-18 09:18:40 [vert.x-eventloop-thread-6] INFO  i.k.mocks.MockActionAdapterVerticle - Starting <MockActionAdapterVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-2] INFO  i.k.a.a.h.HttpActionAdapterVerticle - Starting <io.knotx.adapter.action.http.HttpActionAdapterVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-1] INFO  i.k.a.s.h.HttpServiceAdapterVerticle - Starting <io.knotx.adapter.service.http.HttpServiceAdapterVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-3] INFO  i.k.m.MockRemoteRepositoryVerticle - Mock Remote Repository server started. Listening on port 3001
2017-01-18 09:18:40 [vert.x-eventloop-thread-1] INFO  io.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2017-01-18 09:18:40 [vert.x-eventloop-thread-4] INFO  io.knotx.mocks.MockServiceVerticle - Mock Service server started. Listening on port 3000
2017-01-18 09:18:40 [vert.x-eventloop-thread-0] INFO  i.k.launcher.KnotxStarterVerticle - Knot.x STARTED

                Deployed bf28d297-87e1-40de-9328-d03a24428370 [knotx:io.knotx.FragmentSplitter]
                Deployed a86383e3-1f15-475f-a8e1-14cbc515d295 [knotx:io.knotx.ServiceKnot]
                Deployed 2ba723be-f68f-4636-95a2-1794ed5cc9c5 [knotx:io.knotx.FilesystemRepositoryConnector]
                Deployed df9f9967-8211-419a-8558-51b2aa378020 [knotx:io.knotx.FragmentAssembler]
                Deployed 0ea49334-888c-4f1a-9892-f2caab816b45 [knotx:io.knotx.ServiceAdapterMock]
                Deployed 4922cd4c-b5d1-4b61-adb1-4fd9f61e8867 [knotx:example.io.knotx.ActionKnot]
                Deployed 269bdf78-4701-4fde-8f8a-8479df7cbde6 [knotx:io.knotx.HandlebarsKnot]
                Deployed 623f93cd-656a-4dc4-87b4-fe3b665ea629 [knotx:io.knotx.ActionAdapterMock]
                Deployed 4ca0b3c7-01b2-4fbc-aea7-28adc7cc0d9c [knotx:io.knotx.HttpRepositoryConnector]
                Deployed a8faf376-f333-401c-9988-7f2288044ed5 [knotx:example.io.knotx.HttpActionAdapter]
                Deployed 70539977-9337-48bb-9b76-7c2c1c9a1898 [knotx:io.knotx.HttpServiceAdapter]
                Deployed 35797434-b27d-45d2-91f2-af2a86c1b6b0 [knotx:io.knotx.RemoteRepositoryMock]
                Deployed 32d14454-a3c8-4b6f-b5f0-0954be5ad763 [knotx:example.io.knotx.KnotxServer]
                Deployed 06c5a193-4a37-4da2-b8ee-015a42357a35 [knotx:io.knotx.ServiceMock]
```

This example app simulates Vert.x based application running Knot.x core verticles:
 - [[Server|Server]],
 - [[Repository Connectors|RepositoryConnectors]]: **File System** and **Http** Repository connectors,
 - [[Splitter|Splitter]],
 - [[Action Knot|ActionKnot]],
 - [[Service Knot|ServiceKnot]],
 - [[Handlebars Knot|HandlebarsKnot]],
 - [[Assembler|Assembler]],
 - [[Http Service Adapter|HttpServiceAdapter]], 
 - example Action Adapter,
 
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
http://localhost:8092/customFlow/remote/simple.html
```
- first serves HTML template from Remote Http Repository
- second serves HTML template from local storage
- third one serves HTML template with multiple forms on the page, one is AJAX based - served from local storage
- fourth one serves HTML template with multiple forms on the page, one is AJAX based - served from remote repository
- last one serves a JSON message using the [[Gateway Mode|GatewayMode]]

## Reconfigure demo
You can play with the demo in order to get familiar with the ways how to configure Knot.x based application.

### Use starter JSON
1. Let's start with the starter JSON, the file which defines what Verticles Knot.x is composed of. Let's check the one from our demo application
[knotx-example-app.json](https://github.com/Cognifide/knotx/blob/master/knotx-example/knotx-example-app/src/main/resources/knotx-example-app.json)
2. Copy the file to computer that's running Demo app and make it new name for it, e.g.: `knotx-example-experiments.json`
3. Inside that JSON add new object `config` and configure KnotxServer service (take service name from `services` section), 
but change `httpPort` property only. Let's set it to `9999`.
```json
{
  "modules": [
    "knotx:example.io.knotx.KnotxServer",
    "knotx:io.knotx.HttpRepositoryConnector",
    "knotx:io.knotx.FilesystemRepositoryConnector",
    "knotx:io.knotx.FragmentSplitter",
    "knotx:io.knotx.HandlebarsKnot",
    "knotx:io.knotx.ServiceKnot",
    "knotx:example.io.knotx.ActionKnot",
    "knotx:io.knotx.HttpServiceAdapter",
    "knotx:example.io.knotx.HttpActionAdapter",
    "knotx:io.knotx.RemoteRepositoryMock",
    "knotx:io.knotx.ServiceMock",
    "knotx:io.knotx.ServiceAdapterMock",
    "knotx:io.knotx.ActionAdapterMock"
  ],
  "config": {
    "knotx:example.io.knotx.KnotxServer": {
      "options": {
        "config": {
          "httpPort": 9999
        }
      }
    }
  }
}
```
4. Start Knot.x with your new configuration. Notice in the console, the HTTP Server is now listening on port 9999
```
...
2017-01-03 12:25:31 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 9999
2017-01-03 12:25:31 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED
...
```

### Use JVM properties
Knot.x can be also reconfigured using JVM properties. With this method, you can set simple values through JVM properties, or provide JSON with the more complex object you want to use to override configuration.
The syntax of the property is as follows:
`-D<service-name>.<json-obj-path>=<value>`
Where:
- `<service-name>` is the name of the Knot.x service without `knotx:` prefix, e.g.: io.knotx.ServiceKnot, etc.
- `<json-obj-path>` is simply a **dot** delimited path in the Knot.x service configuration. E.g. `options.config.httpPort`
- `<value>` can be simply a value to be set on JSON property, or `file:/path/to/file.json`. Latter type of value, is the json file with JSON Object, that should be used to merge with the object pointed by `<json-obj-path>`.
  
E.g.`-Dexample.io.knotx.KnotxServer.options.config.httpPort=7777`
Or,`-Dexample.io.knotx.KnotxServer.options.config=file:test.json`

Let's modify `httpPort` once again, but this time using JVM property.
1. Restart Knot.x with your previous config, but this time start java with additional command line option:
```
$ java -Dexample.io.knotx.KnotxServer.options.config.httpPort=7777 -jar target/knotx-example-app-X.Y.Z-SNAPSHOT-fat.jar -conf knotx-example-experiments.json
```
2. Notice that HTTP Server is listening on port **7777** now, so starter JSON configuration is overridden.
```
...
2017-01-03 12:35:31 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 7777
2017-01-03 12:35:31 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED
...
```
3. Now, create new file on your computer, e.g. `server-options.json` which will have JSON object that should be merged with `options` object of the KnotxServer service.
In this case, the object will specify how many instances to start. (This can be supplied by simple value instead of JSON file, but for demonstration purposes let do it as below)
```json
{
  "instances": 2
}
```
4. Start Knot.x once again, but this time with new JVM property
```
$ java -Dexample.io.knotx.KnotxServer.options=file:server-options.json -Dexample.io.knotx.KnotxServer.options.config.httpPort=7777 -jar target/knotx-example-app-X.Y.Z-SNAPSHOT-fat.jar -conf knotx-example-experiments.json
```
5. Notice that Knot.x is started on port **7777* but two instances of KnotxServer where started.
```
...
2017-01-03 12:35:31 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 7777
2017-01-03 12:35:31 [vert.x-eventloop-thread-2] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 7777
2017-01-03 12:35:31 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED
...
```

### Conclusions
- You can configure Knot.x using starter JSON by providing properties that should be added, or modified.
- You can configure Knot.x using JVM properties
- On JVM property, you can specify service and path to the property in service configuration and the value to be set on it
- On JVM property, instead of the simple value, you can provide JSON file that will be put into the service JSON at appropriate property
- JVM properties have highest priority

# Debugging Knot.x Demo

## Requirements

To run Knot.x you only need Java 8.
To build it you also need Maven (version 3.3.1 or higher).
Intellij IDE

## How to set up Knot.x debugging in Intellij IDE
Assuming you have Knot.x project opened in Intellij, you can set up Run/Debug Application configuration to be able to run or debug Demo or 
Standalone Knot.x application directly in your IDE.

1. Go to Run/Debug Configurations in IntelliJ
2. Add new Application Configuration

[[assets/knotx-debugging-new-config.png|alt=Knot.x Debugging new config]]
3. Set name of the configuration
4. Pick main class to be io.knotx.launcher.LogbackLauncher
5. In Program Arguments specify that KnotxStarterVerticle should be run with the `-conf` parameter pointing to the configuration you want to run (e.g. Example app)
```
run io.knotx.launcher.KnotxStarterVerticle -conf src/main/resources/knotx-example-app.json
```
6. Set **Working directory** to the module where your json config exists (e.g. knotx-example-app)
7. Set **Use classpath of module** by selecting the module in which you have your configuration (e.g. knotx-example-app)
8. Optionally, in VM options you can specify system properties if you want to override configuration or set memory properly of this configuration.
9. Finally, you can now Run or Debug this configuration and play with Knot.x as usuall.

[[assets/knotx-debugging-config.png|alt=Knot.x Debugging config]]

# Architecture

A simplified description of Knot.x can be `a tool which converts a static page (template) into 
dynamic page driven by data provided by microservices`.
Page visitor requests are directed to Knot.x. Then Knot.x calls [[Repository Connector|RepositoryConnectors]] for 
the template, split this template to static / dynamic fragments and process those fragments. Finally 
it calls external services if required.

The diagram below depicts Knot.x request flow at very high level point of view.

[[assets/knotx-overview.png|alt=Knot.x Overview]]

Thanks to modular nature, Knot.x can be easily extended by project-specific mechanics (see [[Knots|Knot]]).
Knot.x can easily adapt responses with different formats to required one (see e.g. [[Service Adapters|ServiceAdapter]]).
Additionally Knot.x does not concentrate on HTTP protocol so even custom protocols can be used if required.

# High Level Architecture

Knot.x is modular easily extensible and adaptable platform which assembles static and dynamic
content from multiple sources.

Knot.x hides its internal complexity and allows to use it with very basic knowledge about Knot.x
Core modules. Custom features can be easily added to Knot.x with two flexible extension points: [[Knots|Knot]]
and [[Adapters|Adapter]], that listen to the event bus and handle custom business logic.

Diagram below depicts high level Knot.x architecture.

[[assets/knotx-high-level-architecture.png|alt=High Level Architecture]]

Custom business logic can be encapsulated in dedicated Knots / Adapters.

A Knot is a module which defines a custom step in the process of [[request routing|KnotRouting]].
It can process custom fragments, invoke Adapters and redirect site visitors to new site or error page.
More information about Knots can be found in the [[dedicated section|Knot]].

[[Adapters|Adapter]] are used to communicate with external services. Knot.x recommends to create dedicated Adapter
every time we need to perform some business logic or adapt service response to other format.

If REST service responses can be used as is without any changes no custom Adapters will be required.
Knot.x provides generic HTTP Adapters ([[Http Service Adapter|HttpServiceAdapter]] and ) which can communicate with services.
It is marked on diagram with arrow between Knot.x and Services Layer.

# Knot.x Core Architecture

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

# Knot Routing

A Request from a user goes first to the [[Server|Server]].
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

It is also possible to define a custom request flow, skipping Repository Connector, Fragment Splitter and Fragment Assembler.
This feature is described in the [[Gateway Mode|GatewayMode]] section.

# Gateway Mode

The gateway mode provides a way of processing requests alternative to that presented in the [[Knot Routing section|KnotRouting]].
In this mode, Knots don't have to operate on [[Fragments|Splitter]].

## Example configuration

Configuration of a gateway mode takes place in the [[Server Configuration file|Server#How-to-configure]].

```
"customFlow": {
  "routing": {
    "GET": [
      {
        "path": "/customFlow/.*",
        "address": "knotx.gateway.gatewayknot",
        "onTransition": {
          "next": {
            "address": "knotx.gateway.requestprocessor"
          }
        }
      }
    ]
  },
  "responseProvider": {
    "address": "knotx.gateway.responseprovider"
  }
}
```

Here, the gateway mode will work on paths starting with `/customFlow`.
First, a Knot, called a Gateway Knot, sets the Knot Context of the request. The rest of the routing configuration can be customized to your needs.

After the routing is over, the response is returned from the verticle called Response Provider.

Depending on your routing implementation, you can use the Gateway Mode to return the external services response
in a raw form (e.g. JSON), while still having custom Knots, like authorization, to process the request.
An example is shown in the Knot.X example application, where a custom `RequestProcessorKnot` simulates a call to an external service to fetch a JSON message.

You can see an example usage of the Gateway Mode in the [[demo application|RunningTheDemo]].

# Knot.x Module
Knot.x is composed of set of Verticles. To simplify deployment process and configuration of each specialized Verticle, Knot.x is shipped with it's own implementation of Vert.x Service Factory.
It means that user while configuring Knot.x need to provide set of Knot.x module names that should be deployed, without knowledge about what's Verticle classes are behind each module.

As mentioned in [[Knot.x Deployment|KnotxDeployment]], the list of modules must be specified in JSON file provided as `-conf` parameter when starting Knot.x application.
```json
{
  "modules": [
    "knotx:io.knotx.KnotxServer",
    "knotx:my.custom.Service"
  ]
}
```
As you can notice on the example above, each module is prefixed with `knotx:` string, that tells to Vert.x engine a Knot.x Verticle Factory should be used to resolve actual Verticle.

When Knot.x is starting with the config above, we're actually asking to deploy two modules `io.knotx.KnotxServer` 
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
- by JVM property (it will override also values overriden by starter JSON)
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
4. Override default configuration if necessary directly in starter JSON, or through JVM properties.

# Server

Server is essentially a "heart" (a main [Verticle](http://vertx.io/docs/vertx-core/java/#_verticles)) of Knot.x.
It creates HTTP Server, listening for browser requests, and is responsible for coordination of
communication between [[Repository Connectors|RepositoryConnectors]], [[Splitter|Splitter]] and all deployed [[Knots|Knot]].

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
Server is deployed using Vert.x service factory as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default configuration.

Default configuration shipped with the verticle as `io.knotx.KnotxServer.json` file available in classpath.
```json
{
  "main": "io.knotx.server.KnotxServerVerticle",
  "options": {
    "config": {
      "httpPort": 8092,
      "displayExceptionDetails": true,
      "allowedResponseHeaders": [
        "Access-Control-Allow-Origin",
        "Allow",
        "Cache-Control",
        "Content-Disposition",
        "Content-Encoding",
        "Content-Language",
        "Content-Location",
        "Content-MD5",
        "Content-Range",
        "Content-Type",
        "Content-Length",
        "Content-Security-Policy",
        "Date",
        "ETag",
        "Expires",
        "Last-Modified",
        "Location",
        "Pragma",
        "Proxy-Authenticate",
        "Server",
        "Set-Cookie",
        "Status",
        "Vary",
        "Via",
        "X-Frame-Options",
        "X-XSS-Protection",
        "X-Content-Type-Options",
        "X-UA-Compatible",
        "X-Request-ID"
      ],
      "defaultFlow": {
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
        "splitter": {
          "address": "knotx.core.splitter"
        },
        "routing": {
          "GET": [
            {
              "path": ".*",
              "address": "knotx.knot.service",
              "onTransition": {
                "next": {
                  "address": "knotx.knot.handlebars"
                }
              }
            }
          ]
        },
        "assembler": {
          "address": "knotx.core.assembler"
        }
      }
    }
  }
}
```
In short, by default, server does:
- Listens on port 8092
- Displays exception details on error pages (for development purposes)
- Returns certain headers in Http Response to the client (as shown above)
- Uses the [[default Knot.X routing mechanism|KnotRouting]]
- Communicates with two types of repositories: HTTP and Filesystem
- Uses core [[Splitter|Splitter]]
- Each GET request for any resource (`.*`) is routed through [[Service Knot|ServiceKnot]] and then [[Handlebars rendering engine|HandlebarsKnot]]

Detailed description of each configuration option that's available is described in next section.

## Server options
Main server options available.

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `httpPort`                  | `Number (int)`                      | &#10004;       | HTTP Port on which Knot.x will listen for browser requests |
| `displayExceptionDetails`   | `Boolean`                           |                | (Debuging only) Displays exception stacktrace on error page. **False** if not set.|
| `allowedResponseHeaders`    | `Array of String`                   |                | Array of HTTP headers that are allowed to be send in response. **No** response headers are allowed if not set. |
| `defaultFlow`               | `KnotxFlowConfiguration`            | &#10004;       | Configuration of [[default Knot.X routing|KnotRouting]] |
| `customFlow`                | `KnotxFlowConfiguration`            |                | Configuration of [[Gateway Mode|GatewayMode]] |

### KnotxFlowConfiguration options

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `repositories`              | `Array of RepositoryEntry`          | &#10004;       | Array of repositories configurations |
| `splitter`                  | `VerticleEntry`                     | &#10004;       | **Splitter** communication options |
| `assembler`                 | `VerticleEntry`                     | &#10004;       | **Assembler** communication options |
| `routing`                   | `Object of Method to RoutingEntry`  | &#10004;       | Set of HTTP method based routing entries, describing communication between **Knots**<br/>`"routing": {"GET": {}, "POST": {}}` |

The `repositories`, `splitter` and `assembler` verticles are specific to the default Knot.X processing flow.

### RepositoryEntry options

| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `path`      | `String`  | &#10004;       | Regular expression of the HTTP Request path |
| `address`   | `String`  | &#10004;       | Event bus address of the **Repository Connector** modules, that should deliver content for the requested path matching the regexp in `path` |

### VerticleEntry options

| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `address`  | `String`  | &#10004;       | Sets the event bus address of the verticle |

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


# Repository Connectors
Knot.x gets templates from one or more repositories, processes them and serves to end users. Knot.x uses Repository Connectors to communicate with template repository.

## How does it work?
First it is important to understand what Repository is. Repositories are not part of Knot.x itself, 
these are the stores of templates, e.g. CMS systems, HTTP servers, or file system locations, or any other systems that are able to deliver HTML templates. 
The diagram below depicts how Knot.x uses repositories.

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
consumes requests for templates through Vertx Event Bus. 
This communication model allows adding custom repository connectors easily. For more information see sections:
* [[HTTP Repository Connector|HttpRepositoryConnector]]
* [[Filesystem Repository Connector|FilesystemRepositoryConnector]]



# HTTP Repository Connector

Http Repository Connector allows to fetch templates from an external repository via HTTP protocol. 

## How does it work?
The diagram below depicts Knot.x modules and request flow in more details.

[[assets/knotx-http-repository.png|alt=Http Repository Connector]]

## How to configure?
Http Repository Connector is deployed using Vert.x service factory as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default configuration.

Default configuration shipped with the verticle as `io.knotx.HttpRepositoryConnector.json` file available in classpath.

```json
{
  "main": "io.knotx.repository.HttpRepositoryConnectorVerticle",
  "options": {
    "config": {
      "address": "knotx.core.repository.http",
      "clientOptions": {
        "maxPoolSize": 1000,
        "keepAlive": false,
        "tryUseCompression": true
      },
      "clientDestination": {
        "domain": "localhost",
        "port": 3001
      },
      "allowedRequestHeaders": [
        "Accept*",
        "Authorization",
        "Connection",
        "Cookie",
        "Date",
        "Host",
        "If*",
        "Origin",
        "Pragma",
        "Proxy-Authorization",
        "User-Agent",
        "Via",
        "X-*"
      ]
    }
  }
}
```
In general, it:
- Listens on event bus address `knotx.core.repository.http` for requests to the repository
- It uses certain HTTP Client options while communicating with the remote repository
- It defines destination of the remote repository
- And specifies certain request headers from client request that are being passed to the remote repository

Detailed description of each configuration option is described in next section.

### Options
Main options available.

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `address`                   | `String`                            | &#10004;       | Event Bus address of Http Repository Connector Verticle |
| `clientOptions`             | `HttpClientOptions`                 | &#10004;       | HTTP Client options used when communicating with the destination repository. See [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) to get all options supported.|
| `clientDestination`         | `JsonObject`                        | &#10004;       | Allows to specify **domain** and **port** of the HTTP Repository endpoint |

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
Filesystem Repository Connector is deployed using Vert.x service factory as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default configuration.

Default configuration shipped with the verticle as `io.knotx.FilesystemRepositoryConnector.json` file available in classpath.

```json
{
  "main": "io.knotx.repository.FilesystemRepositoryConnectorVerticle",
  "options": {
    "config": {
      "address": "knotx.core.repository.filesystem",
      "catalogue": ""
    }
  }
}
```
In general, it:
- Listens of event bus address `knotx.core.repository.filesystem` address on requests to the repository
- It uses empty catalogue what means the classpath is the root folder of repository data.

Detailed description of each configuration option is described in next section.

### Options
Main options available.

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `address`                   | `String`                            | &#10004;       | Event Bus address of Filesystem Repository Connector Verticle |
| `catalogue`                 | `String`                            |                | it determines where to take the resources from. If it's left empty, they will be taken from the classpath. It may be treated like a prefix to the requested resources. |

# HTML Fragment Splitter
Fragment Splitter reads [[Knot Context|Knot]] having a HTML Template retrieved from Repository using configured connector, splits it into 
static and dynamic Fragments, updates Knot Context and returns back to the caller.

##How does it work?
It splits HTML Template using regexp `<script\s+data-knotx-knots\s*=\s*"([A-Za-z0-9-]+)"[^>]*>.+?</script>`.
All matched `script` tags are converted into Fragments containing list of supported [[Knots|Knot]] 
declared in `data-knotx-knots` attribute. HTML parts below, above and between matched scripts are 
converted into Fragments without Knot support. It means that they are not supposed to be processed
by Knots. See example for more details.

**Splitter requires `data-knotx-knots` attribute to be the first attribute in the `script` tag.**

### Example
Fragment Splitter reads Knot Context with HTML Template:
```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Knot.x example</title>
</head>
<body>
  <div class="row">
    <script data-knotx-knots="services,handlebars"
            data-knotx-service="first-service"
            type="text/knotx-snippet">
      <div class="col-md-4">
        <h2>Snippet - {{_result.message}}</h2>
      </div>
    </script>
  </div>
</body>
</html>
```
and splits Template into three following Fragments:

**Fragment 1** (knots = "_raw")
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
**Fragment 2** (knots = "services,handlebars")
```
    <script data-knotx-knots="services,handlebars"
            data-knotx-service="first-service"
            type="text/knotx-snippet">
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

More details about Fragments you can find in next section.

### Fragment
Fragment contains: 
- list of supported Knots (list of [[Knot Election Rules|Knot]]), 
- Fragment Content (matched script or simple HTML)
- Fragment Context (JSON with a progress state)

Fragments matching `script` tag declare Knots used while further processing (see [[Knots routing|KnotRouting]]). 
They can communicate with external services via [[Adapters|Adapter]], evaluate templates using 
Handlebars and so on. Every Knot defines value ([[Knot Election Rule|Knot]]) for `data-knotx-knots` 
attribute which determines if it will process particular Fragment or not.

Fragments not matching `script` tag are not supposed to be processed while Knots routing. They are 
used at the end of processing to assemble final HTML result (see [[Fragment Assembler|Assembler]]).

## How to configure?
Splitter is deployed using Vert.x service factory as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default configuration.

Default configuration shipped with the verticle as `io.knotx.FragmentSplitter.json` file available in classpath.
```json
{
  "main": "io.knotx.splitter.FragmentSplitterVerticle",
  "options": {
    "config": {
      "address": "knotx.core.splitter"
    }
  }
}
```
In short, the default configuration just defines event bus address on which the Splitter listens for jobs to process.

Detailed description of each configuration option is described in the next subsection.

### Splitter config

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Splitter verticle. |

# Fragment Assembler
Fragment Assembler joins all Fragments into the final output. It's executed at the end of 
all suitable Knots processing, just before generating the response to the page visitor.

## How does it work?
Fragment Assembler reads Knot Context having Fragments, joins all Fragments into one string, updates 
Knot Context and returns back to the caller. See examples below for more details.

### How Fragments are being joined?
Lets explain process of fragments join using example. Fragment Assembler reads Knot Context having 
three Fragments:
```html
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
```
```html
  <h2>this is webservice no. 1</h2>
  <div>message - a</div>
```
```html
</body>
</html>
```
Fragment Assembler joins all those Fragments into one string:
```html
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
  <h2>this is webservice no. 1</h2>
  <div>message - a</div>
</body>
</html>
```
### How does Assembler join unprocessed Fragments?
Lets imagine that some Fragments were not processed and they still contain dynamic snippets definitions. 
It is not expected behaviour, so Fragment Assembler must handle it. There are three possible strategies 
provided: `AS_IS`, `UNWRAP`, `IGNORE`. They can be configured with entry `unprocessedStrategy`.
See Fragments below and then compare those strategies. 
```html
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
```
```html
<script data-knotx-knots="services,handlebars" data-knotx-service="first-service" type="text/knotx-snippet">
  <h2>{{message}}</h2>
  <div>{{body.a}}</div>
</script>
```
```html
</body>
</html>
```
#### AS_IS strategy
It leaves fragments untouched. So, result of join will look like below for our example:
```
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
<script data-knotx-knots="services,handlebars" data-knotx-service="first-service" type="text/knotx-snippet">
  <h2>{{message}}</h2>
  <div>{{body.a}}</div>
</script>
</body>
</html>
```
#### UNWRAP strategy
It unwraps the snippet, by removing `<script>` tag leaving just body of the snippet. So, the result of 
join will look like this:
```
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
<!-- SNIPPET UNWRAPED START -->
  <h2>{{message}}</h2>
  <div>{{body.a}}</div>
<!-- SNIPPET UNWRAPED STOP -->
</body>
</html>
```
#### IGNORE strategy
It ignores all Fragments which contains dynamic tag definitions.
```
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
<!-- SNIPPET IGNORED -->
</body>
</html>
```

## How to configure?
Fragment Assembler is deployed using Vert.x service factory as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default configuration.

Default configuration shipped with the verticle as `io.knotx.FragmentAssembler.json` file available in classpath.
```json
{
  "main": "io.knotx.knot.assembler.FragmentAssemblerVerticle",
  "options": {
    "config": {
      "address": "knotx.core.assembler",
      "unprocessedStrategy": "UNWRAP"
    }
  }
}
```
In short, the default configuration just defines event bus address on which the Assembler listens
for jobs to process and strategy how to handle unprocessed snippets.

Detailed description of each configuration option is described in the next subsection.

### Fragment Assembler config

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Fragment Assembler verticle. |
| `unprocessedStrategy`       | `String`                             | &#10004;       | Strategy for unprocessed Fragments (`AS_IS`, `UNWRAP`, `IGNORE`). `UNWRAP` is default strategy if no strategy defined. |


# Knot
A Knot defines business logic which can be applied to a particular [[Fragment|Splitter]]. It can, for 
example, invoke an external services via [[Adapters|Adapter]], evaluate Handlebars snippets or simply 
redirect a site visitor to a different location.

## How does it work?
The Knot reads a [Knot Context](#knot-context) containing a list of Fragments to process, takes care of the processing,
updates the Knot Context and returns it back to the caller.

A particular Knot is applied to Fragments if two conditions are met:

- it is defined in [[Knot routing|KnotRouting]]
- there is at least one Fragment that declares a matching [Knot Election Rule](#knot-election-rule)

### Knot Election Rule
A Knot Election Rule determines if a Knot should be applied to a particular Fragment. Every time
a Knot reads the Knot Context, it checks if there is any Fragment that needs to be processed by it. 

The Knot Election Rule is simple `String` value coming from the `data-knotx-knots` attribute (the attribute contains a comma-separated list of Knot Election Rules). 

Knots can simply filter Fragments which do not contain a certain Knot Election Rule (for example `services` or `handlebars`).

### Knot Context
The Knot Context is a communication model passed between [[Server|Server]], [[Fragment Splitter|Splitter]], 
[[Knots|Knot]] and [[Fragment Assembler|Assembler]]. 

The flow is driven by [[Server|Server]] forwarding and getting back KnotContext to/from Splitter, Knots and Assembler. 
Knot Context keeps information about a site visitor request, current processing status and a site visitor response. 

From now on, we will be using the terms *client* and *site visitor* interchangeably.

Knot Context contains:
* client request with path, headers, form attributes and parameters
* client response with body, headers and status code
* [[fragments|Splitter]]
* transition value

*Client request* includes site visitor path (requested URL), HTTP headers, form attributes 
(for POST requests) and request query parameters.

*Client response* includes a body (which represents final response body), HTTP headers (which are narrowed finally
by Server) and HTTP status code.

Please see [[Splitter|Splitter]] section to find out what Fragments are and how they are produced. 
Fragments contain a template fragment content and a context. Knots can process a configured fragment content, 
call required Adapters and put responses from Adapters to the fragment context (fragment context is a JSON 
object).

**Transition** is a text value which determines next step in [[request routing|KnotRouting]].

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
| `clientResponse.statusCode`| `200`
| `transition`| `next` 

*Redirect response*

Knot finds out that client must be redirected to an other URL.

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `301`
| `clientResponse.headers.location`| `/new/location.html`
| `transition`| EMPTY 

*Error response*

Knot calls Adapter Service and gets **500**. Knot is not aware how this error should be processed so it sets clientResponse.statusCode to `500`.
Server beaks routing and responds with `500` to the client.

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `500`
| `transition`| EMPTY 


## How to configure?
the Knot API specifies an abstract class - `KnotConfiguration` to handle JSON configuration support. This
abstraction can be used while implementing a custom Knot but it is not required. Every Knot must be
exposed with a unique Event Bus address - that's the only obligation (as is the case with Adapters).
Please see example configurations for [[Action Knot|ActionKnot#how-to-configure]], 
[[Service Knot|ServiceKnot#how-to-configure]].

## How to implement your own Knot?
Implementation of a Knot does not require knowledge of how to communicate via the Vert.x event bus. It's wrapped by **Vert.x Service Proxy** functionality so any new implementation can focus on the business logic of the Knot. 

In order to implement a Knot, follow the guide below:

1. Create your Knot by extending `io.knotx.knot.AbstractKnotProxy` class, and implement your business logic in the `processRequest()` method with the return type of `Observable<KnotContext>` (a promise of the modified `KnotContext`).

   See `io.knotx.knot.service.impl.ServiceKnotProxyImpl.java` as an example.

2. Create a class extending `AbstractVerticle` that will read the configuration and register your `KnotProxy` implementation at the given `address`.

   Have a look at `io.knotx.knot.service.ServiceKnotVerticle.java` to see how the `ServiceKnotProxyImpl` is registered.

The `AbstractKnotProxy` class provides the following methods that you can override in your implementation in order to control the processing of Fragments:

- `boolean shouldProcess(Set<String> knots)` is executed on each Fragment from the given KnotContext, from each fragment it gets a set of Knot names (from the `data-knotx-knots` snippet attribute), and lets you decide whether the Fragment should be processed by your Knot or not (no pun intended).
- `Single<KnotContext> processRequest(KnotContext knotContext)` consumes `KnotContext` messages from a [[Server|Server]] and returns the modified `KnotContext` object as an instance of [`rx.Single`](http://reactivex.io/RxJava/javadoc/rx/Single.html).
- `KnotContext processError(KnotContext knotContext, Throwable error)` handles any Exception thrown during processing, and is responsible for preparing the proper KnotContext on such occasions, these will simply finish processing flows, as any error generated by a Knot will be immediately returned to the page visitor.

| ! Note |
|:------ |
| Please note that while this section focuses on the Java language specifically, it's not the only choice you have. Thanks to [the polyglot nature of Vert.x](http://vertx.io), you can implement your Adapters and Knots using other languages. |

| ! Note |
|:------ |
| Besides the Verticle implementation itself, a custom implementation of your Knot must be built as a Knot.x module in order to be deployed as part of Knot.x. Follow the [[Knot.x Modules|KnotxModules]] documentation in order to see how to make your Knot a module. | 

# Action Knot
Action Knot is an [[Knot|Knot]] implementation responsible for forms submissions handling. It supports
simple (without file upload) forms including redirection to successful pages and multi-step forms flows.
It provides also a service error handling mechanism.

## How does it work?
Action Knot is used with default Knot.x settings while both GET and POST client request processing.
It transforms a form template to Knot.x agnostic one for GET requests. When client submits the form
Action Knot calls configured [[Adapter|Adapter]] and based on its response redirect the client to a
successful / error / next step page.

Let's describe Action Knot behaviour with following example.

### Example
ActionKnot processes Fragments having `form-{NAME}` in `data-knotx-knots` attribute,
where `{NAME}` is a unique name of a form (assuming there may be more than one form on a single page
it is used to distinguish a requested snippet). {NAME} can contain only small and capital letters. So
[[Knot Election Rule|Knot]] for Action Knot is pattern `form-[a-zA-Z]`.

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
<script data-knotx-knots="form-1" type="text/knotx-snippet">
  {{#if action._result.validationErrors}}
  <p class="bg-danger">Email address does not exists</p>
  {{/if}}
  <p>Please provide your email address</p>
  <form data-knotx-action="step1" data-knotx-on-success="/content/local/login/step2.html" data-knotx-on-error="_self" data-knotx-adapter-params='{"myKey":"myValue"}' method="post">
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
(there could be more than one form at the same template). Its value comes from a script's `data-knotx-knots`
attribute - it retrieve a `{NAME}` value from `data-knotx-knots="form-{NAME}"`.

Following data attributes are available in the `<form>` tag with described purpose:
- `data-knotx-action` - this is a name of an [[Action Adapter|ActionAdapter]] that will be used to handle submitted data.
It is similar concept as `data-knotx-service-{NAME}` in [[Service Knot|ServiceKnot]]. In the example,
Action Handler registered under name `step1` will handle this form data submission.
- `data-knotx-on-{SIGNAL}` - name of a [Signal](#Signal) that should be applied. In the example
there is one signal success with the value `'/content/local/login/step2.html'` and one signal error
with the value `'_self'`. Signal `'_self'` means that after error response (error signal returned)
the client will stay on the same page.
- `data-knotx-adapter-params` - JSON Object that can be passed to the corresponding `Adapter`. It will be
available in `AdapterRequest` as `adapterParams`. 


### Signal
Signal is basically a decision about further request processing. Value of the signal can be either:
- `path` of a page that user should be redirected to after processing form submit,
- `_self` - that indicates that there will not be redirect, instead current page will be processed (generated view for instance).
In other words, the page processing will be delegated to next [[Knot|Knot]] in the graph.

## How to configure?
Action Knot is deployed using Vert.x service factory as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default configuration.

Default configuration shipped with the verticle as `io.knotx.ActionKnot.json` file available in classpath.

```json
{
  "main": "io.knotx.knot.action.ActionKnotVerticle",
  "options": {
    "config": {
      "address": "knotx.knot.action",
      "adapters": [
        {
          "name": "action-self",
          "address": "test",
          "params": {
            "example": "example-value"
          },
          "allowedRequestHeaders": [
            "Cookie"
          ],
          "allowedResponseHeaders": [
            "Set-Cookie"
          ]
        }
      ],
      "formIdentifierName": "snippet-identifier"
    }
  }
}

```
In general, it:
- Listens on event bus address `knotx.knot.action` on messages to process
- It communicates with the [Action Adapter|ActionAdapter] on event bus address `test` for processing POST requests to the services
  - It pass the example parameter to the adapter
  - It pass `Cookie` request header to the adapter
  - It returns `Set-Cookie` response header from adapter
- It uses `snippet-identifier` value as hidden field name that's used by Action Knot to identify form that sent POST request

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
| `allowedRequestHeaders`     | `String`                            | &#10004;       | Array of HTTP client request headers that are allowed to be passed to Adapter. **No** request headers are allowed if not set. |
| `allowedResponseHeaders`    | `String`                            | &#10004;       | Array of HTTP response headers that are allowed to be sent in a client response. **No** response headers are allowed if not set. |

# Service Knot
Service Knot is a [[Knot|Knot]] implementation responsible for asynchronous Adapter calls to fetch the
data that will be later used to compose page final markup with [[Handlebars Knot|HandlebarsKnot]].

## How does it work?
Service Knot filters Fragments containing `services` in `data-knots-types` attribute (see
[[Knot Election Rule|Knot]]). Then for every Fragment it calls configured Adapters. At the end
it collects responses from those Adapters and expose them in [[Knot Context|Knot]]. Let's describe
how Adapters are invoked with following example.

Adapters calls are defined both on template and Knot configuration layers:

First Service Knot collects `data-knotx-service-{NAMESPACE}={ADAPTERNAME}` attributes which define accordingly:
 - namespace under which Adapter response will be available,
 - name of the Adapter tha will be called during snippet processing.

Additionally with every Adapter `data-knotx-params-{NAMESPACE}={JSON DATA}` attribute can be defined
which specifies parameters for Adapter call. An example `script` definition can look like:

```html
<script data-knotx-knots="services,handlebars"
  data-knotx-service="first-service"
  data-knotx-service-second="second-service"
  data-knotx-params-second='{"path":"/overridden/path"}'
  type="text/knotx-snippet">
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
Service Knot is deployed using Vert.x service factory as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default configuration.

Default configuration shipped with the verticle as `io.knotx.ServiceKnot.json` file available in classpath.

```json
{
  "main": "io.knotx.knot.service.ServiceKnotVerticle",
  "options": {
    "config": {
      "address": "knotx.knot.service",
      "services": [
        {
          "name": "mock",
          "address": "mock-service-adapter",
          "params": {
            "path": "/service/mock/.*"
          }
        }
      ]
    }
  }
}
```
In general, it:
- Listens on event bus address `knotx.knot.service` on messages to process
- It communicates with the [[Service Adapter|ServiceAdapter]] on event bus address `mock-service-adapter` for processing GET requests to the services
- It defines service adapter configuration

Detailed description of each configuration option is described in the next subsection.

### Service Knot options

Main Service Knot options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Service Knot verticle. |
| `services`                  | `Array of ServiceMetadata`          | &#10004;       | Array of [ServiceMetadata](https://github.com/Cognifide/knotx/blob/master/knotx-core/knotx-knot-view/src/main/java/com/cognifide/knotx/knot/service/ServiceKnotConfiguration.java).|

ServiceMetadata options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `name`                      | `String`                            | &#10004;       | Name of [[Adapter|Adapter]] which is referenced in `data-knotx-service-{NAMESPACE}={ADAPTERNAME}`. |
| `address`                   | `String`                            | &#10004;       | Event bus address of the **Adapter** verticle. |
| `params`                    | `JSON object`                       | &#10004;       | Json Object with default params which are sent to Adapter. |
| `cacheKey`                  | `String`                            |                | Cache key which is used for Adapters calls caching. **No** means that cache key has value `{NAME}|{PARAMS}` |

# Handlebars Knot
Handlebars Knot is a [[Knot|Knot]] implementation responsible for handlebars template processing.

## How does it work?
Handlebars Knot uses [Handlebars.js](http://handlebarsjs.com/) templating engine. More specifically it utilizes
Handlebars Java port - [Handlebars.java](https://github.com/jknack/handlebars.java) to compile and evaluate
templates.

Handlebars Knot filters Fragments containing `handlebars` in `data-knots-types` attribute (see 
[[Knot Election Rule|Knot]]). Then for each Fragment it merges Fragment Content (Handlebars snippet) 
with data from Fragment Context (for example data from external services or form submission response).

### Example
Example Knot Context contains:
*Fragment Content*
```html
<script data-knotx-knots="services,handlebars" data-knotx-service="first-service" type="text/knotx-snippet">
<div class="col-md-4">
  <h2>Snippet1 - {{_result.message}}</h2>
  <div>Snippet1 - {{_result.body.a}}</div>
  {{#string_equals _response.statusCode "200"}}
    <div>Success! Status code : {{_response.statusCode}}</div>
  {{/string_equals}}
</div>
</script>
```
*Fragment Context*
```
{
  "_result": {
    "message":"this is webservice no. 1",
    "body": {
      "a": "message a"
    }
  },
  "_response": {
    "statusCode":"200"
  }
}
```
Handlebars Knot uses data from Fragment Context and applies it to Fragment Content:
```html
<div class="col-md-4">
  <h2>Snippet1 - this is webservice no. 1</h2>
  <div>Snippet1 - message a</div>
  <div>Success! Status code : 200</div>
</div>
```
Finally Fragment Content is replaced with merged result.

## How to configure?
Handlebars Knot is deployed using Vert.x service factory as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default configuration.

Default configuration shipped with the verticle as `io.knotx.HandlebarsKnot.json` file available in classpath.
```json
{
  "main": "io.knotx.knot.templating.HandlebarsKnotVerticle",
  "options": {
    "config": {
      "address": "knotx.knot.handlebars",
      "templateDebug": true
    }
  }
}
```
In general, it:
- Listens on event bus address 'knotx.knot.handlebars'
- Renders HTML debug comments on the output HTML

Detailed description of each configuration option is described in the next subsection.

### Handlebars Knot options

Main Handlebars Knot options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Handlebars Knot verticle. |
| `templateDebug`             | `Boolean`                           | &#10004;       | Template debug enabled option.|

## How to extend?

### Extending handlebars with custom helpers

If the list of available handlebars helpers is not enough, you can easily extend it. To do this the 
following actions should be undertaken:

1. Use io.knotx:knotx-knot-handlebars module as dependency
2. Create a class implementing ```io.knotx.knot.templating.handlebars.CustomHandlebarsHelper``` interface. 
This interface extends [com.github.jknack.handlebars.Helper](https://jknack.github.io/handlebars.java/helpers.html)
3. Register the implementation as a service in the JAR file containing the implementation
    * Create a configuration file called META-INF/services/io.knotx.handlebars.CustomHandlebarsHelper 
    in the same project as your implementation class
    * Paste a fully qualified name of the implementation class inside the configuration file. If you're 
    providing multiple helpers in a single JAR, you can list them in new lines (one name per line is allowed) 
    * Make sure the configuration file is part of the JAR file containing the implementation class(es)
3. Run Knot.x with the JAR file in the classpath

#### Example extension

Sample application contains an example custom Handlebars helper - please take a look at the implementation of ```BoldHelper```:
* Implementation class: ```io.knotx.example.monolith.handlebars.BoldHelper```
* service registration: ```knotx-example/knotx-example-app/src/main/resources/META-INF/services/io.knotx.knot.templating.handlebars.CustomHandlebarsHelper```

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
The Adapter API specifies an abstract class - `AdapterConfiguration` to handle JSON configuration support. This
abstraction can be used while implementing a custom Adapter but it is not required. Every Adapter must be
exposed with a unique Event Bus address - that's the only obligation (as is the case with Knots).
Please see an example configuration for [[Http Service Adapter|HttpServiceAdapter#how-to-configure]]

## How to extend?
Implementation of an Adapter does not require knowledge of how to communicate via the Vert.x event bus. It's wrapped by the **Vert.x Service Proxy** functionality so any new implementation can focus on the business logic of the Adapter. 

In order to implement an Adapter, follow the guide below:

1. Create your Knot by extending the `io.knotx.adapter.AbstractAdapterProxy` class and implement your business logic in the `processRequest()` method with the return type of `Observable<AdapterResponse>` (promise of the `AdapterResponse`).

   You can refer to `io.knotx.adapter.service.http.impl.HttpServiceAdapterProxyImpl.java` as an example.

2. Create a class extending `AbstractVerticle` that will simply read the configuration and register your `AdapterProxy` implementation at the `address` provided.

   Have a look at `io.knotx.adapter.service.http.HttpServiceAdapterVerticle.java` to see how the `HttpServiceAdapterProxyImpl` is registered.

The `AbstractAdapterProxy` class provides the following methods that you can extend in your implementation:

- `Observable<AdapterResponse> processRequest(AdapterRequest message)` method that consumes `AdapterRequest` messages from [[Knot|Knot]] and returns `AdapterResponse` object as `rx.Observable`
- Optionally, `AdapterResponse getErrorResponse(AdapterRequest request, Throwable error)` method which handles any Exception thrown during processing, and is responsible for preparing proper AdapterResponse on such situations. By default `AbstractAdapterProxy` implements this method, and returns `AdapterResponse` with the `ClientResponse` object having `500` status code and the error message in response body. 

| ! Note |
|:------ |
| Please note that while this section focuses on the Java language specifically, it's not the only choice you have. Thanks to [the polyglot nature of Vert.x](http://vertx.io), you can implement your Adapters and Knots using other languages. |

| ! Note |
|:------ |
| Besides the Verticle implementation itself, a custom implementation of your Knot must be built as a Knot.x module in order to be deployed as part of Knot.x. Follow the [[Knot.x Modules|KnotxModules]] documentation in order to learn how to make your Knot a module. | 

### Adapters common library
For many useful and reusable Adapters concept, please check our [knotx-adapter-common](https://github.com/Cognifide/knotx/tree/master/knotx-adapter/knotx-adapter-common)
module. You will find there support for `placeholders` and `http connectivity`. 

### How to run a custom Adapter with Knot.x
Please refer to [[Deployment|KnotxDeployment]] section to find out more about deploying and running 
a custom Adapters with Knot.x.

# Service Adapter
Service Adapter is Component of a system, that mediate communication between Knot.x [[Service Knot|ServiceKnot]]
and external services that deliver data injected into template. In short, Service Adapter acts as a
element translating messages between external services and Knots.

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
adapters for your setup.

Writing custom Service Adapter requires fulfilling [[Service Knot|ServiceKnot]] contract.
Please refer also to [[Adapter|Adapter#how-to-extend]].

| ! Note |
|:------ |
| Besides Verticle implementation itself, a custom implementation of your Service Adapter must be build as Knot.x module in order to be deployed as part of Knot.x. Follow the [[Knot.x Modules|KnotxModules]] in order to see how to make your Service Adapter a module. | 


# Action Adapter
Action Adapter is Component of a system, that mediates communication between Knot.x [[Action Knot|ActionKnot]] 
and external Services that are responsible for handling form submissions.


## How does it work?
Action Adapter accepts message with the following data:

- `clientRequest` - object with all data of an original request,
- `params` - JSON object that contains additional parameters, among those parameter mandatory 
[`path`](#service-path) 

#### Service path
`path` parameter is a mandatory parameter that must be passed to Action Adapter. 
It defines request path.

### Adapter Response
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

| ! Note |
|:------ |
| Besides Verticle implementation itself, a custom implementation of your Action Adapter must be build as Knot.x module in order to be deployed as part of Knot.x. Follow the [[Knot.x Modules|KnotxModules]] in order to see how to make your Action Adapter a module. | 


# Http Service Adapter
Http Service Adapter is an example of Adapter implementation embedded in Knot.x.
It enables communication between [[Service Knot|ServiceKnot]] and external services via HTTP.

## How does it work?
When Http Service Adapter starts processing a message from Event Bus, it expects following input:
- `clientRequest` - JSON object that contains client request (contains e.g. headers, params, formAttributes etc.).
- `params` - JSON object that contains additional parameters, among those parameter mandatory
[`path`](#service-path) parameter should be defined, enables passing additional
[query params and headers](#service-params-and-additional-headers).

### Service path
`path` parameter is a mandatory parameter that must be passed to Http Service Adapter.
It defines request path and may contain [placeholders](#parametrized-services-calls).

### Service params and additional headers
It is possible to pass additional query parameters and headers that Http Service Adapter will send
to external service.
- `queryParams` - JSON object that contains parameters passed in query.
- `headers` - JSON object that contains headers. Those headers will *overwrite* existing values.

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
| `statusCode`    | `HttpResponseStatus`       | status code of a response from external service (e.g. `200 OK`) |
| `headers`       | `MultiMap`                | external service response headers |
| `body`          | `Buffer`                  | external service response, **please notice that it is expected, tha form of a response body from an external service is JSON** |

## How to configure?
Http Service Adapter is deployed using Vert.x service factory as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default configuration.

Default configuration shipped with the verticle as `io.knotx.HttpServiceAdapter.json` file available in classpath.
```json
{
  "main": "io.knotx.adapter.service.http.HttpServiceAdapterVerticle",
  "options": {
    "config": {
      "address": "knotx.adapter.service.http",
      "clientOptions": {
        "maxPoolSize": 1000,
        "keepAlive": false,
        "logActivity": true
      },
      "services": [
        {
          "path": "/service/mock/.*",
          "domain": "localhost",
          "port": 3000,
          "allowedRequestHeaders": [
            "Content-Type",
            "X-*"
          ]
        },
        {
          "path": "/service/.*",
          "domain": "localhost",
          "port": 8080,
          "allowedRequestHeaders": [
            "Content-Type",
            "X-*"
          ]
        }
      ]
    }
  }
}
```
In general, the default configuration covers:
- `address` is the where adapter listen for events at Event Bus. Every event that will be sent at `knotx.adapter.service.http`
will be processed by Http Service Adapter.
- `clientOptions` are [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) used to configure HTTP connection.
Any HttpClientOption may be defined in this section, at this example two options are defined:
  - `maxPoolSize` -  maximum pool size for simultaneous connections,
  - `keepAlive` - that shows keep alive should be disabled on the client.
- `services` - an JSON array of services that Http Service Adapter can connect to. Each service is distinguished by `path` parameter which is regex.
In example above, two services are configured:
  - `/service/mock/.*` that will call `http://localhost:3000` domain with defined [path](#service-path),
  - `/service/.*` that will call `http://localhost:8080` domain with defined [path](#service-path).


#### Service Knot configuration
Example configuration of a [[Service Knot|ServiceKnot]]:
```json
  "config": {
    "address": "knotx.knot.service",
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
      },
      {
        "name" : "javabooks",
        "address" : "knotx.adapter.service.http",
        "params": {
          "path": "/books/v1/volumes",
          "queryParams": {
              "q": "java"
          },
          "headers": {
            "token": "knotx-request"
          }
        }
      }
    ]
  }
```

#### snippet
Example html snippet in template:

```html
<script data-knotx-knots="services,handlebars" type="text/knotx-snippet"
    data-knotx-service-search="search"
    data-knotx-service-twitter="twitter">
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

##### Setting service query parameters
We can use the `queryParams` JSON object to define the service query parameters and their values directly from template. Consider the following service configuration:

```json
  "config": {
    "address": "knotx.knot.service",
    "services": [
      {
        "name" : "products",
        "address" : "knotx.adapter.service.http",
        "params": {
          "path": "/service/products/"
        }
      }
    ]
  }
```

We can set query parameters sent to the service using the following snippet:

```html
<script data-knotx-knots="services,handlebars" type="text/knotx-snippet"
    data-knotx-service="products"
    data-knotx-params='{"queryParams":{"amount":"4"}}'>
        <h1>Products</h1>
        {{#each _result.products}}
        <p>{{productName}}</p>
        {{/each}}
</script>
```

This way, you can modify the request parameters being sent to the external service, without re-starting Knot.X, just by updating the template.
In this example, the request would be `/service/products?amount=4`

Please note that Knot.X caches templates fetched by the [[Filesystem Repository Connector|FilesystemRepositoryConnector]].
As a result, the "hot-swap" mechanism described above might not work with templates stored in local repositories.

You can also set the `queryParams` from the configuration file by amending the snippet presented above:
```json
  "config": {
    "address": "knotx.knot.service",
    "services": [
      {
        "name" : "products",
        "address" : "knotx.adapter.service.http",
        "params": {
          "path": "/service/products/",
          "queryParams": {
            "amount": "4"
          }
        }
      }
    ]
  }
```
Bear in mind that a Knot.X restart is needed in order to apply the service configuration, as the configuration is loaded on startup.

This mechanism can be also used simultaneously with the `path` property being parametrized by placeholders. Take into consideration, however, that placeholder values can only be resolved based on the `ClientRequest` (current http request), and not the `queryParams` value.

Please note that if the `queryParams` are defined both in the configuration file and in the template, the parameters from the template will override the configuration.

# Mocks
For the prototyping or development purposes Knot.x is shipped with the Mocks Verticle. Currently, a following mocks are available:
- HTTP Service mock - HTTP endpoint serving JSON files on GET and POST requests.
- HTTP Remote repository mock - HTTP endpoint serving any file from local file system or classpath.

## HTTP Service mock
### How does it work?
- Listens on configured HTTP port on client requests
- On GET request
  - Retrieves file from the file system from the path `mockDataRoot`+`request.path`.
  - Sends response to the client with the content of the file setting proper response headers and status code `200` (Or `404` is no requested mock file)
- On POST request, does the same as above. Optionally, if `bouncing=true` it adds to the response JSON Object form attributes from the POST request.  

### How to configure ?
Default configuration shipped with the verticle as `io.knotx.ServiceMock.json` file available in classpath.
```json
{
  "main": "io.knotx.mocks.MockServiceVerticle",
  "options": {
    "config": {
      "mockDataRoot": "mock/service",
      "bouncing": true,
      "httpPort": 3000
    }
  }
}

```
In short, the default configuration defines:
- Mock listens on HTTP port 3000 for requests
- Root folder of mocked responses is `mock/service` relative to the classpath. If path starts from `/` it will search for files in local filesystem instead.
- Do bouncing of POST requests - Adds to the response JSON object form attributes from POST request.

Besides simple configuration as above, you can introduce delays to the service responses, to simulate real cases.
See [[how to configure delay ?|#how-to-configure-delay-]] section for details.

## HTTP Remote repository mock
### How does it work?
- Listens on configured HTTP port on client requests
- On GET request only
  - Retrieves file from the file system from the path `mockDataRoot`+`request.path`.
  - Sends response to the client with the content of the file setting proper response headers and status code `200` (Or `404` is no requested mock file)

### How to configure ?
Default configuration shipped with the verticle as `io.knotx.RemoteRepositoryMock.json` file available in classpath.
```json
{
  "main": "io.knotx.mocks.MockRemoteRepositoryVerticle",
  "options": {
    "config": {
      "mockDataRoot": "mock/repository",
      "httpPort": 3001
    }
  }
}
```
In short, the default configuration defines:
- Mock listens on HTTP port 3001 for requests
- Root folder of mocked responses is `mock/repository` relative to the classpath. If path starts from `/` it will search for files in local filesystem instead.

Besides simple configuration as above, you can introduce delays to the repository responses, to simulate real cases.
See [[how to configure delay ?|#how-to-configure-delay-]] section for details.

## How to configure delay ?
You can modify a default configuration by overriding it in starter JSON (see [[Configure through starter JSON|KnotxDeployment#how-to-configure-though-starter-json-]]).
Besides, properties shown in default configuration, you can supply **delay** configuration to simulate many situations.

See below for two options how to delay responses in your starter JSON.

### Delay all responses
Delay all responses of Service Mock by `100ms` and Remote Repository Mock responses by `20ms`
```json
{
  "modules": [
    "io.knotx.ServiceMock",
    "io.knotx.RemoteRepositoryMock"
  ],
  "config": {
    "io.knotx.ServiceMock": {
      "options" : {
        "config": {
          "delayAllMs": 100
        }
      }
    },
    "io.knotx.RemoteRepositoryMock": {
      "options" : {
        "config": {
          "delayAllMs": 20
        }
      }
    }    
  }
}
```
Alternatively, instead of configuring using starter JSON, you can supply it using JVM properties when starting mocks, e.g.:
```
$ java -Dio.knotx.ServiceMock.options.config.delayAllMs=100 -Dio.knotx.RemoteRepositoryMock.options.config.delayAllMs=20 -jar knotx-mocks-XXXX.jar ....
```

### Delay reponses for specific paths
Config below does:
- On Service Mock, delay response on path `/service/first.json` by `10ms`, others without delay
- On Remote Repository Mock, delay response on:
  - path `/content/remote/simple.html` by `50ms`
  - path `/content/remote/multiple-forms.html` by `100ms`
  - other paths without delay

```json
{
  "modules": [
    "io.knotx.ServiceMock",
    "io.knotx.RemoteRepositoryMock"
  ],
  "config": {
    "io.knotx.ServiceMock": {
      "options": {
        "config": {
          "delay": {
            "/service/first.json" : {
              "delayMs": 10
            }
          }
        }
      }
    },
    "io.knotx.RemoteRepositoryMock": {
      "options": {
        "config": {
          "delay": {
            "/content/remote/simple.html": {
              "delayMs": 50
            },
            "/content/remote/multiple-forms.html": {
              "delayMs": 100
            }
          }
        }
      }
    }    
  }
}
```

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

- Create your own configuration JSON (any location on the host) based on `knotx-standalone.json` 
from the [latest release](https://github.com/Cognifide/knotx/releases). In this example, 
created file is named `knotx-starter.json` and is placed in `KNOTX_HOME`.

- Create your own [Logback logger configuration](http://logback.qos.ch/manual/configuration.html) 
(any location on the host) basing e.g. on `logback.xml` 
from the [latest release](https://github.com/Cognifide/knotx/releases).

At this step `KNOTX_HOME` should contain:
```
- app
    - knotx-standalone-X.Y.Z-fat.jar
    - custom-modules.jar
- knotx-starter.json
- logback.xml
```

To start Knot.x with custom modules, use following command

```
java -Dlogback.configurationFile=logback.xml -cp "app/*" io.knotx.launcher.LogbackLauncher -conf knotx-custom-starter.json
```

### Vert.x metrics
For monitoring purposes you might want to enable Vert.x metrics in order to monitor how Knot.x performs. 
Currently, it's possible to enable JMX metrics, so you can use any JMX tool, like Jconsole to inspect all the metrics Vert.x collects.

In order to enable it, add following JVM property when starting Knot.x
```
java -Dcom.sun.management.jmxremote -Dvertx.metrics.options.jmxEnabled=true -Dvertx.metrics.options.jmxDomain=knotx ...
```
Vert.x collects:
- Vert.x elements metrics (such as amount of verticles, worker pool sizes, etc.)
- Event bus metrics (such as bytes sent/received, messages delivered, etc. )
- HTTP Clients metrics (such as bytes sent/received, connections, amount of requests, per http code responses, etc. )
- HTTP Server metrics (such as bytes sent/received, connections, amount of requests, etc.)

For a detailed description about available metrics please check [Vert.x The Metrics](http://vertx.io/docs/vertx-dropwizard-metrics/java/#_the_metrics) page

| ! Warning |
|:------ |
| **We don’t recommend that you try to gather metrics from your production environment. JMX’s RPC API is fragile and bonkers. For development purposes and browsing, though, it can be very useful.** | 

## How to configure ?
As mentioned above, the knotx-starter.json is the main configuration file describing what Knot.x modules (verticles) need to be started as part of Knot.x.

`knotx-standalone.json` configuration available on Github looks like below
```json
{
  "modules": [
    "knotx:io.knotx.KnotxServer",
    "knotx:io.knotx.HttpRepositoryConnector",
    "knotx:io.knotx.FilesystemRepositoryConnector",
    "knotx:io.knotx.FragmentSplitter",
    "knotx:io.knotx.ServiceKnot",
    "knotx:io.knotx.ActionKnot",
    "knotx:io.knotx.HandlebarsKnot",
    "knotx:io.knotx.HttpServiceAdapter"
  ]
}
```
As you see, it simply have list of modules (verticles) that Knot.x should start. Out of the box, no other configuration is required as each verticle 
is shipped with default config.

However, at the production environment you must alter the configuration parameters such as port of HTTP server, or HTTP headers that are 
being passed, or finally addresses to the client services that are going to be used for rendering dynamic content.

Thanks to the Knot.x capabilities you can provide your configurations that modifies defaults. There are two ways:
- in your `knotx-starter.json` file add `config` section where you can put configuration for each Verticle you want to modify - but only elements that 
you need to change. Follow the guide of each Verticle to see the supported parameters.
- through JVM properties, you can provide single values for desired fields (e.g. http port) or even whole json objects from external JSON file. 
Any parameter provided through system properties will always override default and starter values.
- in some scenarios, it's also possible to create your own module that uses Knot.x core Verticle, where you can build the configuration from scratch. 
Such module might be also overridden using starter JSON and/or JVM properties.

### How to configure though starter JSON ?
In your project specific `knots-starter.json` add `config` object. Inside, put field with configuration object for each module you want to change configuration for.
For instance, you want to modify configuration of KnotxServer module, you can do it as follows:
```json
{
  "modules": [
    "knotx:io.knotx.KnotxServer",
    "knotx:io.knotx.HttpRepositoryConnector",
    "knotx:io.knotx.FilesystemRepositoryConnector",
    "knotx:io.knotx.FragmentSplitter",
    "knotx:io.knotx.ServiceKnot",
    "knotx:io.knotx.ActionKnot",
    "knotx:io.knotx.HandlebarsKnot",
    "knotx:io.knotx.HttpServiceAdapter"
  ],
  "config": {
    "knotx:io.knotx.KnotxServer" : {
      "options": {
        "config": {
          "httpPort": 9999
        },
        "instances": 2
      }
    }
  }
}
```
Important things to remember:
- `options` field which maps exactly to a [vert.x Deployment Options](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html) object. 
It means, that you can specify here deployment options such as how many instances of that module should be deployed, etc.
- Inside `options` you can supply `config` object where you're actualy can provide configuration to the verticle represented by the module. 
See Knot.x Verticle documentation to see what's available on each Verticle. 

If you start Knot.x with the configuration as above, it will start all modules listed in the config, but the KnotxServer will be deployed as:
- Two instances
- HTTP Port it listens is 9999

### How to configure through JVM properties ?
In some cases, you might want to provide configuration parameters through JVM properties, e.g. you can have same config used on all environments, 
but you wanted to specify HTTP port of the server to be different on each host. 
In such case, you can simply provide env specific port as JVM property, e.g.  
```
-Dio.knotx.KnotxServer.options.config.httpPort=9999
```

Additionally, if you wanted to change more than one value, you can create separate JSON file with all parameters you want to modify, and inject that config using JVM property.

For instance, you want to have routing of KnotxServer defined in a separate JSON file, let call it `my-routing.json` that might looks like below:
```json
{
  "GET": [
    {
      "path": "/content/custom-path/.*",
      "address": "knotx.knot.service",
      "onTransition": {
        "next": {
          "address": "knotx.knot.handlebars"
        }
      }
    }
  ]
}
```
Then, you can use that config to override default routing as below:
```
-Dio.knotx.KnotxServer.options.config.routing=file:my-routing.json
```

### How to configure your own module ?
The last option to change Verticle configuration, is to redefine Knotx module by creating your own descriptor. 
The descriptor is simply a JSON file that must be available in classpath (e.g. in JAR file of your custom Verticle implementation, 
or inside folder where all JAR files you put on your installation).

Name of the descriptor file is important as it's a module name used in starter JSON.

For instance, you want to create KnotxServer configuration from scratch, ignoring default config available. 
All you have to do is, to create module descriptor file, e.g. `my.KnotxServer.json` with the content as below
```json
{
  "main": "io.knotx.server.KnotxServerVerticle",
  "options": {
    "config": {
      
    }
  }
}
```
Where:
- `main` is the fully qualified class name of the Verticle the module represents
- `options` object, where you can specify deployment options, and inside it a `config` object that should have Verticle specific configuration

Next step, is to use your new module in `knotx-starter.json`.
```json
{
  "modules": [
    "knotx:my.KnotxServer",
    "......"
  ],
  "config": {
    "knotx:my.KnotxServer": {
      "options": {
        "config": {
        
        }
      }
    }
  }
}
```
Finally, you can still do override of that config as described above, through starter JSON as in the example, or through JVM properties:

Single value:
```
-Dmy.KnotxServer.options.config.httpPort=9999
```

Or, whole JSON Object from external file
```
-Dmy.KnotxServer.options.config.routing=file:/path/to/my-routing.json
```

# Dependencies

- io.vertx
- io.reactivex
- org.jsoup
- com.github.jknack.handlebars
- com.google.code.gson
- guava
- commons-lang3
- commons-io
- junit
- hamcrest
- assertj
- powermock
- mockito
- zohhak
- com.github.stefanbirkner.system-rules

# Upgrade Notes
If you are upgrading Knot.x from the previous version, here are notes that will help you do all necessary config
and snippets changes that were introduced in comparison to previous released Knot.x version. If you are upgrading
from older than one version (e.g. 1.0.1 -> 1.1.2) be sure, to do all the steps from the Upgrade Notes of all released
versions. You may see all changes in the [Changelog](https://github.com/Cognifide/knotx/blob/master/CHANGELOG.md).

## Version 1.0.1
- [PR-290](https://github.com/Cognifide/knotx/pull/290) - allow defining services without default `params` configured
  * You no longer have to define empty `params` value in `ServiceKnot` config if you don't use any.
