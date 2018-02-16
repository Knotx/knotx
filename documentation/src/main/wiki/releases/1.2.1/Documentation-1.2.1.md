# Wiki
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
  - [How to debug remote instance of Knot.x](#how-to-debug-remote-instance-of-knotx)
- [Architecture](#architecture)
- [High Level Architecture](#high-level-architecture)
- [Knot.x Core Architecture](#knotx-core-architecture)
- [Communication Flow](#communication-flow)
- [Knot Routing](#knot-routing)
- [Gateway Mode](#gateway-mode)
  - [Configuration](#configuration)
  - [Processing](#processing)
- [Knot.x Module](#knotx-module)
  - [How to create your service?](#how-to-create-your-service)
- [Server](#server)
  - [How does it work?](#how-does-it-work)
    - [Routing](#routing)
  - [How to configure?](#how-to-configure)
    - [Knot.x application specific configurations](#knotx-application-specific-configurations)
  - [Server options](#server-options)
    - [KnotxServerCustomHeader options](#knotxservercustomheader-options)
    - [KnotxCSRFConfiguration options](#knotxcsrfconfiguration-options)
    - [KnotxFlowConfiguration options](#knotxflowconfiguration-options)
    - [RepositoryEntry options](#repositoryentry-options)
    - [VerticleEntry options](#verticleentry-options)
    - [RoutingEntry options](#routingentry-options)
    - [KnotRouteEntry options](#knotrouteentry-options)
    - [AccessLogConfiguration options](#accesslogconfiguration-options)
    - [Vert.x HTTP Server configurations](#vertx-http-server-configurations)
    - [How to configure Knot.x to listen with SSL/TLS](#how-to-configure-knotx-to-listen-with-ssltls)
    - [How to enable CSRF Token generation and validation](#how-to-enable-csrf-token-generation-and-validation)
    - [Vert.x Event Bus delivery options](#vertx-event-bus-delivery-options)
    - [Configure access log](#configure-access-log)
- [Repository Connectors](#repository-connectors)
  - [How does it work?](#how-does-it-work-1)
- [HTTP Repository Connector](#http-repository-connector)
  - [How does it work?](#how-does-it-work-2)
  - [How to configure?](#how-to-configure-1)
    - [Options](#options)
    - [Destination options](#destination-options)
  - [How to configure SSL connection to the repository](#how-to-configure-ssl-connection-to-the-repository)
- [Filesystem Repository Connector section](#filesystem-repository-connector-section)
  - [How does it work?](#how-does-it-work-3)
  - [How to configure?](#how-to-configure-2)
    - [Options](#options-1)
- [HTML Fragment Splitter](#html-fragment-splitter)
  - [How does it work?](#how-does-it-work-4)
    - [Example](#example)
    - [Fragment](#fragment)
  - [How to configure?](#how-to-configure-3)
    - [Splitter config](#splitter-config)
- [Fragment Assembler](#fragment-assembler)
  - [How does it work?](#how-does-it-work-5)
    - [How Fragments are being joined?](#how-fragments-are-being-joined)
    - [How does Assembler join unprocessed Fragments?](#how-does-assembler-join-unprocessed-fragments)
      - [AS_IS strategy](#as_is-strategy)
      - [UNWRAP strategy](#unwrap-strategy)
      - [IGNORE strategy](#ignore-strategy)
  - [How to configure?](#how-to-configure-4)
    - [Fragment Assembler config](#fragment-assembler-config)
- [Knot](#knot)
  - [How does it work?](#how-does-it-work-6)
    - [Knot Election Rule](#knot-election-rule)
    - [Knot Context](#knot-context)
      - [Knot Request](#knot-request)
      - [Knot Response](#knot-response)
        - [Example Knot Responses](#example-knot-responses)
  - [How to configure?](#how-to-configure-5)
  - [How to implement your own Knot?](#how-to-implement-your-own-knot)
      - [Building and running example Knot](#building-and-running-example-knot)
    - [How to handle blocking code in your own Knot?](#how-to-handle-blocking-code-in-your-own-knot)
    - [How to implement your own Knot without Rx Java?](#how-to-implement-your-own-knot-without-rx-java)
- [Action Knot](#action-knot)
  - [How does it work?](#how-does-it-work-7)
    - [Example](#example-1)
    - [Signal](#signal)
  - [How to configure?](#how-to-configure-6)
    - [Action Knot options](#action-knot-options)
    - [Vert.x Event Bus delivery options](#vertx-event-bus-delivery-options-1)
- [Service Knot](#service-knot)
  - [How does it work?](#how-does-it-work-8)
    - [Adapter Calls Caching](#adapter-calls-caching)
  - [How to configure?](#how-to-configure-7)
    - [Service Knot options](#service-knot-options)
    - [Vert.x Event Bus delivery options](#vertx-event-bus-delivery-options-2)
- [Handlebars Knot](#handlebars-knot)
  - [How does it work?](#how-does-it-work-9)
    - [Example](#example-2)
  - [How to configure?](#how-to-configure-8)
    - [Handlebars Knot options](#handlebars-knot-options)
  - [How to extend?](#how-to-extend)
    - [Extending handlebars with custom helpers](#extending-handlebars-with-custom-helpers)
      - [Example extension](#example-extension)
- [Adapters](#adapters)
  - [How does it work?](#how-does-it-work-10)
    - [Types of adapters](#types-of-adapters)
      - [Adapter Request](#adapter-request)
      - [Adapter Response](#adapter-response)
  - [How to configure?](#how-to-configure-9)
  - [How to implement your own Adapter?](#how-to-implement-your-own-adapter)
      - [Building and running example Adapter](#building-and-running-example-adapter)
    - [How to handle blocking code in your own Adapter?](#how-to-handle-blocking-code-in-your-own-adapter)
    - [How to implement your own Adapter without Rx Java?](#how-to-implement-your-own-adapter-without-rx-java)
    - [Adapters common library](#adapters-common-library)
    - [How to run a custom Adapter with Knot.x](#how-to-run-a-custom-adapter-with-knotx)
- [Service Adapter](#service-adapter)
  - [How does it work?](#how-does-it-work-11)
  - [How to configure?](#how-to-configure-10)
  - [How to implement your own Service Adapter?](#how-to-implement-your-own-service-adapter)
- [Action Adapter](#action-adapter)
  - [How does it work?](#how-does-it-work-12)
      - [Service path](#service-path)
    - [Adapter Response](#adapter-response-1)
  - [How to configure?](#how-to-configure-11)
  - [How to implement your own Action Adapter?](#how-to-implement-your-own-action-adapter)
- [Http Service Adapter](#http-service-adapter)
  - [How does it work?](#how-does-it-work-13)
    - [Service path](#service-path-1)
    - [Service params and additional headers](#service-params-and-additional-headers)
    - [Parametrized services calls](#parametrized-services-calls)
    - [Adapter Response](#adapter-response-2)
  - [How to configure?](#how-to-configure-12)
      - [Service Knot configuration](#service-knot-configuration)
      - [snippet](#snippet)
      - [request](#request)
    - [Processing](#processing-1)
        - [search service](#search-service)
        - [twitter service](#twitter-service)
        - [Setting service query parameters](#setting-service-query-parameters)
- [Mocks](#mocks)
  - [HTTP Service mock](#http-service-mock)
    - [How does it work?](#how-does-it-work-14)
    - [How to configure ?](#how-to-configure-)
  - [HTTP Remote repository mock](#http-remote-repository-mock)
    - [How does it work?](#how-does-it-work-15)
    - [How to configure ?](#how-to-configure--1)
  - [How to configure delay ?](#how-to-configure-delay-)
    - [Delay all responses](#delay-all-responses)
    - [Delay reponses for specific paths](#delay-reponses-for-specific-paths)
- [Deploying Knot.x with custom modules](#deploying-knotx-with-custom-modules)
  - [Recommended Knot.x deployment](#recommended-knotx-deployment)
    - [Vert.x metrics](#vertx-metrics)
  - [How to configure ?](#how-to-configure--2)
    - [How to configure Knot.x in starter JSON ?](#how-to-configure-knotx-in-starter-json-)
    - [How to configure with JVM properties ?](#how-to-configure-with-jvm-properties-)
    - [How to configure your own module ?](#how-to-configure-your-own-module-)
    - [What happens when config refers to non-existing module?](#what-happens-when-config-refers-to-non-existing-module)
- [Knot.x Tuning](#knotx-tuning)
  - [JVM settings](#jvm-settings)
    - [Heap size](#heap-size)
    - [GC settings](#gc-settings)
    - [Single thread model optimisation](#single-thread-model-optimisation)
  - [OS Tuning](#os-tuning)
    - [System settings](#system-settings)
    - [Opened descriptors limits](#opened-descriptors-limits)
- [Logging](#logging)
  - [Configure Logback](#configure-logback)
  - [Configure logback for file only output](#configure-logback-for-file-only-output)
  - [Log network activity](#log-network-activity)
  - [Configure logback to log my specific package](#configure-logback-to-log-my-specific-package)
- [Performance](#performance)
  - [What do we measure - KPIs](#what-do-we-measure---kpis)
    - [KPI #1 - 1 second response time (90% line)](#kpi-1---1-second-response-time-90%25-line)
    - [KPI #2 - No error responses](#kpi-2---no-error-responses)
    - [KPI #3 - stability](#kpi-3---stability)
    - [KPI #4 - 1 hour of 1 second response time (90% line)](#kpi-4---1-hour-of-1-second-response-time-90%25-line)
  - [Performance infrastructure and setup](#performance-infrastructure-and-setup)
    - [Knot.x instance VM and GC settings](#knotx-instance-vm-and-gc-settings)
  - [Tools](#tools)
  - [Performance tests](#performance-tests)
    - [Scenarios](#scenarios)
      - [1. One snippet and one service](#1-one-snippet-and-one-service)
        - [Details](#details)
      - [2. One snippet and five services](#2-one-snippet-and-five-services)
        - [Details](#details-1)
      - [3. Five snippets one service each](#3-five-snippets-one-service-each)
        - [Details](#details-2)
      - [4. Heavy template with one snippet one services](#4-heavy-template-with-one-snippet-one-services)
        - [Details](#details-3)
      - [5. Heavy template with 100 snippets and one heavy service](#5-heavy-template-with-100-snippets-and-one-heavy-service)
        - [Details](#details-4)
      - [6. Heavy template with one big snippet and one heavy service](#6-heavy-template-with-one-big-snippet-and-one-heavy-service)
        - [Details](#details-5)
    - [Results](#results)
    - [Observations](#observations)
  - [Soak test](#soak-test)
    - [Results](#results-1)
      - [Users/throughput](#usersthroughput)
      - [CPU utilization](#cpu-utilization)
      - [CPU load](#cpu-load)
      - [JVM memory heap space](#jvm-memory-heap-space)
      - [GC collections per second](#gc-collections-per-second)
      - [Network traffic](#network-traffic)
      - [Event bus usage](#event-bus-usage)
    - [Observations](#observations-1)
  - [Terminology](#terminology)
- [Dependencies](#dependencies)
- [FAQ](#faq)
  - [1. Setup and first steps](#1-setup-and-first-steps)
    - [1.1. What do I need to setup Knot.x instance?](#11-what-do-i-need-to-setup-knotx-instance)
  - [2. Development and best practices](#2-development-and-best-practices)
    - [2.1 How can I inject the data from a web service that responses with JSON into my site using Knot.x?](#21-how-can-i-inject-the-data-from-a-web-service-that-responses-with-json-into-my-site-using-knotx)
    - [2.2 How can I use non web based services to inject the data into my site using Knot.x?](#22-how-can-i-use-non-web-based-services-to-inject-the-data-into-my-site-using-knotx)
    - [2.3 I'd like to keep an option to integrate with services using front-end integration. Does Knot.x support that?](#23-id-like-to-keep-an-option-to-integrate-with-services-using-front-end-integration-does-knotx-support-that)
  - [3. Supported and unsupported features](#3-supported-and-unsupported-features)
    - [3.1 Will nested scripts be supported?](#31-will-nested-scripts-be-supported)
    - [3.2 Can I have more than one remote repository defined?](#32-can-i-have-more-than-one-remote-repository-defined)
    - [3.3 Does Knot.x come with an Action Adapter implementation?](#33-does-knotx-come-with-an-action-adapter-implementation)
- [Upgrade Notes](#upgrade-notes)

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
but change `port` property only. Let's set it to `9999`.
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
          "serverOptions": {
             "port": 9999
          }
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
- `<json-obj-path>` is simply a **dot** delimited path in the Knot.x service configuration. E.g. `options.config.serverOptions.port`
- `<value>` can be simply a value to be set on JSON property, or `file:/path/to/file.json`. Latter type of value, is the json file with JSON Object, that should be used to merge with the object pointed by `<json-obj-path>`.
  
E.g.`-Dexample.io.knotx.KnotxServer.options.config.serverOptions.port=7777`
Or,`-Dexample.io.knotx.KnotxServer.options.config=file:test.json`

Let's modify `port` once again, but this time using JVM property.
1. Restart Knot.x with your previous config, but this time start java with additional command line option:
```
$ java -Dexample.io.knotx.KnotxServer.options.config.serverOptions.port=7777 -jar target/knotx-example-app-X.Y.Z-SNAPSHOT-fat.jar -conf knotx-example-experiments.json
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
$ java -Dexample.io.knotx.KnotxServer.options=file:server-options.json -Dexample.io.knotx.KnotxServer.options.config.serverOptions.port=7777 -jar target/knotx-example-app-X.Y.Z-SNAPSHOT-fat.jar -conf knotx-example-experiments.json
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

## How to debug remote instance of Knot.x 
Assuming you have running Knot.x on dedicated machine (not localhost) and you'd like to debug it. All you have to do is just add JVM properties to enable it.

E.g.:
```
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
```

Then, you can connect to that instance (machine IP) on the port specified in the properties above.

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
The Gateway Mode allows to place your micro-services directly inside an Knot.x instance. It simplifies
a client-side integration reducing an infrastructure needs (an additional application server for 
micro-services is not required). The Gateway Mode makes Knot.x the right tool for both client-side 
and backend-side integrations.

The Gateway Mode provides a way of processing requests alternative to that presented in the 
[[Knot Routing section|KnotRouting]]. In this mode, Knots don't have to operate on 
[[Fragments|Splitter]]. An HTTP request body is available in fragments (one RAW fragment).

## Configuration
The Gateway Mode is not enabled by default. [[Server configuration file|Server#How-to-configure]] for
production defines only default Knot.x request flow (with Repository and Splitter). 

Example Web API endpoint is configured and delivered in [[Knot.x demo|RunningTheDemo]].
This example endpoint is configured like below:

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
    ],
    "PUT": [
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

Here, the Gateway Mode works on paths starting with `/customFlow/`.

Lets test our endpoint with calls:

Simple GET call: 
```
> curl -X GET localhost:8092/customFlow/
{"message":"This is a sample custom flow response"}
```

Simple PUT call with body message:
```
> curl -X PUT -d'{ "message": "Hello from Web API!" }' localhost:8092/customFlow/
{ "message": "Hello from Web API!" }
```

Our endpoint behaviour is coded as simple [[Knot|Knot]] extension: 
io.knotx.gateway.impl.RequestProcessorKnotProxyImpl and available at `knotx.gateway.requestprocessor`
module address (see example.io.knotx.RequestProcessorKnot.json).

## Processing

First, a Knot, called a Gateway Knot, checks if the request is allowed or not. The rest of the routing 
configuration can be customized to your needs.

After the routing is over, the response is returned from the verticle called Response Provider.

Depending on your routing implementation, you can use the Gateway Mode to return the external services responses
in a raw form (e.g. JSON), while still having custom Knots, like authorization, to process the request.

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
      "serverOptions": {
         "port": 4555
      },
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
          "serverOptions": {
            "port": 6666
          }
        },
        "instances": 1
      }
    }
  }
}
```
- by JVM property (it will override also values overridden by starter JSON)
```
$ java -Dio.knotx.KnotxServer.options.config.serverOptions.port=2000 -jar knotx-xxxx-fat.jar -conf starter.json
```
See [[Knot.x Deployments|KnotxDeployment]] for details how to supply your configurations.

## How to create your service?
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
4. If necessary, override the default configuration directly in starter JSON, or through JVM properties.

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
- Once the last Knot returns processed **KnotContext**, server calls **assembler** to create HTTP Response based on data from the KnotContext
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

The HTTP Server configuration consists two parts:
- Knot.x application specific configurations
- Vert.x HTTP Server configurations
- Vert.x Event Bus delivery options

### Knot.x application specific configurations

Default configuration shipped with the verticle as `io.knotx.KnotxServer.json` file available in classpath.
```json
{
  "main": "io.knotx.server.KnotxServerVerticle",
  "options": {
    "config": {
      "serverOptions": {
        "port": 8092,
        "keyStoreOptions": {}
      },
      "displayExceptionDetails": true,
      "customResponseHeader": {
        "name": "X-Server",
        "value": "Knot.x"
      },
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
        "Edge-Control",
        "ETag",
        "Expires",
        "Last-Modified",
        "Location",
        "Pragma",
        "Proxy-Authenticate",
        "Server",
        "Set-Cookie",
        "Status",
        "Surrogate-Control",
        "Vary",
        "Via",
        "X-Frame-Options",
        "X-XSS-Protection",
        "X-Content-Type-Options",
        "X-UA-Compatible",
        "X-Request-ID",
        "X-Server"
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
- Always sets custom header in response **X-Server:Knot.x**
- Uses the [[default Knot.X routing mechanism|KnotRouting]]
- Communicates with two types of repositories: HTTP and Filesystem
- Uses core [[Splitter|Splitter]]
- Each GET request for any resource (`.*`) is routed through [[Service Knot|ServiceKnot]] and then [[Handlebars rendering engine|HandlebarsKnot]]

Detailed description of each configuration option that's available is described in next section.

## Server options
Main server options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `fileUploadDirectory`       | `String`                            |                | Uploads directory on server for file uploads - used during POST, PUT request methods. **file-uploads** if not set.|
| `fileUploadLimit`           | `Long`                              |                | Limits the size of file that can be uploaded to Knot.x using POST or PUT request methods. Default is **unlimited**.|
| `displayExceptionDetails`   | `Boolean`                           |                | (Debuging only) Displays exception stacktrace on error page. **False** if not set.|
| `customResponseHeader`      | `KnotxServerCustomHeader`           |                | Sets the custom header in each response from Knot.x to the client. Default value is **X-Server:Knot.x** |
| `allowedResponseHeaders`    | `Array of String`                   |                | Array of HTTP headers that are allowed to be send in response. **No** response headers are allowed if not set. |
| `csrf`                      | `KnotxCSRFConfiguration`            |                | Configuration of the CSRF tokens |
| `defaultFlow`               | `KnotxFlowConfiguration`            | &#10004;       | Configuration of [[default Knot.X routing|KnotRouting]] |
| `customFlow`                | `KnotxFlowConfiguration`            |                | Configuration of [[Gateway Mode|GatewayMode]] |
| `accessLog`                 | `AccessLogConfiguration`            |                | Configuration of the KnotxServer access log |

### KnotxServerCustomHeader options
 Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `name`      | `String`  | &#10004;       | Name of the response header. |
| `value`   | `String`  | &#10004;       | Value of the response header. |

### KnotxCSRFConfiguration options
 Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `secret`       | `String`  |       | Server secret to sign the token. Knot.x by default sets it's own secret. |
| `cookieName`   | `String`  |       | Set the cookie name. By default `XSRF-TOKEN` is used as it is the expected name by AngularJS however other frameworks might use other names. |
| `cookiePath`   | `String`  |       | Set the cookie path. By default `/` is used. |
| `headerName`   | `String`  |       | Set the header name. By default `X-XSRF-TOKEN` is used as it is the expected name by AngularJS however other frameworks might use other names. |
| `timeout`      | `long`    |       | Set the timeout for tokens generated by the handler, by default is `1800000`ms - `30 min` |


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
| `csrf`      | `Boolean` |                | Enables CSRF Token generation (on **GET**) /validation (**POST/PUT/PATCH/DELETE**). Default value is `false` meaning the CSRF is disabled in this route.
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


### AccessLogConfiguration options
| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `enabled`   | `boolean` |       | Enable/Disable access log. Default is `true` |
| `immediate` | `boolean` |       | Log before request or after. Default is `false` - log after request |
| `format`    | `String` |        | Format of the access log. Allowed valueds are `DEFAULT`, `SHORT`, `TINY`. See [[Configure Access Log|#configure-access-log]]. Default format is `DEFAULT` |


### Vert.x HTTP Server configurations

Besides Knot.x specific configurations as mentioned above, the `config` field might have added Vert.x configurations related to the HTTP server.
It can be used to control the low level aspects of the HTTP server, server tuning, SSL.

The `serverOptions` need to be added in the following place, of the KnotsServerVerticle configuration
```
{
  "options": {
    "config": {
      "serverOptions": {
        "port": 8888,
         ...
      },
      ...
```
The list of remaining server options are described on the [Vert.x DataObjects page](http://vertx.io/docs/vertx-core/dataobjects.html#HttpServerOptions).

### How to configure Knot.x to listen with SSL/TLS

Generate certificates for your machine (e.g. localhost)
`keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass keyPass -validity 360 -keysize 2048`

Where:
- `keystore.jks` - is a filename of the keystore
- `keyPass` - is the keystore password

Below is the sample configuration that enabled SSL:
```
{
  "options": {
    "config": {
      "serverOptions": {
        "port": 8043,
        "ssl": true,
        "keyStoreOptions": {
          "path": "keystore.jks",
          "password": "keyPass"
        }
      },
      ...
```
Where:
- `path` - is the path where keystore is located, optional if `value` is used
- `password` - keystore password

Other option is to provide those parameters through JVM properties:
- `-Dio.knotx.KnotxServer.options.config.serverOptions.keyStoreOptions.path=/path/to/keystore.jks` 
- `-Dio.knotx.KnotxServer.options.config.serverOptions.keyStoreOptions.password=keyPass`
- `-Dio.knotx.KnotxServer.options.config.serverOptions.ssl=true`

### How to enable CSRF Token generation and validation

As soon as you start implementing the REST API or form capturing via Knot.x, you might want to enable CSRF attack protection to prevent unwanted actions on your application. E.g. to prevent accepting POST requests if the request is unauthenticated by the Knot.x.
In order to do so, you need to configure two things:
- CSRF Token generation: When the user requests the page with `GET` method a Knot.x drops a cookie with a token
- All routes for POST, PUT, PATCH, DELETE Http methods to be accepted if request consists of CSRF token that was issued by the Knot.x

Below you can find an example configuration on a default flow, where CSRF is enabled on GET and POST routes. The same can be done on the custom flow.
In other scenarios, you might want to enable CSRF on the `GET` route of `DefaultFlow` in order to have token generated. But on the `POST` route of `CustomFlow` you will enable csrf, so the Knot.x will validate the request before passing it to the Knots.
```json
{
  "options": {
    "config": {
     "defaultFlow": {
        ....
        "routing": {
          "GET": [
            {
              "path": ".*",
              "address": "knotx.knot.service",
              "csrf": true,
              ...
            }
          ],
          "POST": [
            {
              "path": ".*",
              "address": "knotx.knot.action",
              "csrf": true,
              ...
            }
          ]
        },
        ....
      }
      ...
```

Besides routes configuration you can customize name of the cookies, headers, timeout for the token, secret key used to sign the token, etc. You can do this by overriding configuration of the Knotx Server as follows:
```json
{
  "options": {
    "config": {
      "csrf": {
        "secret": "eXW}z2uMWfGb",
        "cookieName": "XSRF-TOKEN",
        "cookiePath": "/",
        "headerName": "X-XSRF-TOKEN",
        "timeout": 10000
      },
      ...
```


### Vert.x Event Bus delivery options

While HTTP request processing, Server calls other modules like Repository Connectors, Knots using 
[Vert.x Event Bus](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/EventBus.html). 
The `config` field can contain [Vert.x Delivery Options](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/DeliveryOptions.html)
related to the event bus. It can be used to control the low level aspects of the event bus communication like timeouts, 
headers, message codec names.

For example, add `deliveryOptions` section in the KnotxServer configuration to define the 
timeout for all eventbus responses (Repositories, Splitter, Knots configured in routing, Assembler, etc) 
for eventubs requests that come from `KnotxServer`.
```
{
  "main": "io.knotx.server.KnotxServerVerticle",
  "options": {
    "config": {
      "httpPort": 8092,
      "displayExceptionDetails": true,
      "deliveryOptions": {
        "timeout": 15000
      },
      ...
    }
  }
}
```

### Configure access log
Knot.x uses a default Logging handler from the Vert.x web distribution that allows to log all incomming requests to the Http server.
It supports three log line formats that are:
- DEFAULT that tries to log in a format similar to Apache log format (APACHE/NCSA COMBINED LOG FORMAT) as in the example
`127.0.0.1 - - [Tue, 23 Jan 2018 14:16:34 GMT] "GET /content/local/simple.html HTTP/1.1" 200 2963 "-" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36"`
- SHORT
`127.0.0.1 - GET /content/local/simple.html HTTP/1.1 200 2963 - 19 ms`
- TINY
`GET /content/local/simple.html 200 2963 - 24 ms`

By default access log is enabled with a `DEFAULT` format. If you want to change it, just add access logging section on the KnotxServer configuration in your application.json config file :
```json
{
  "config": {
    "knotx:io.knotx.KnotxServer": {
      "options": {
        "config": {
          "accessLog": {
            "format": "TINY"
          }
        }
      }
    }
  }
}
```
In order to configure logger for access log, see [[Logging|Logging]].

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
        "setIdleTimeout": 600,
        "tryUseCompression": true
      },
      "clientDestination": {
        "scheme": "http",
        "domain": "localhost",
        "port": 3001,
        "hostHeader": "localhost"
      },
      "customRequestHeader": {
        "name": "Server-User-Agent",
        "value": "Knot.x"
      },
      "allowedRequestHeaders": [
        "Accept*",
        "Authorization",
        "Connection",
        "Cookie",
        "Date",
        "Edge*",
        "Host",
        "If*",
        "Origin",
        "Pragma",
        "Proxy-Authorization",
        "Surrogate*",
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
| `clientDestination`         | `JsonObject`                        | &#10004;       | Allows to specify HTTP repository connection details using **scheme**, **domain**, **port** values (<scheme>://<domain>:<port>). Additionally, it's possible to specify override of the host header - **hostHeader** field |
| `customRequestHeader`       | `JsonObject`                        |                | Allows to specify header **name** and its **value**. The header will be send in each request to the configured services. |

### Destination options

| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `scheme`      | `String`  | &#10004;       | Scheme of the connection. Allowed values: **http**, **https** (<scheme>://)|
| `domain`      | `String`  | &#10004;       | Http Repository domain / IP (<scheme>://<domain>) |
| `port`        | `Number`  | &#10004;       | Http Repository port number (<scheme>://<domain>:<port>) |
| `hostHeader`  | `String`  |                | Override of the host header used in that communication. If set, this is the value that will be effectively send. |


## How to configure SSL connection to the repository
- Set up `clientDestination` options with a proper scheme **https**
- ClientOptions consists set of parameters that you might need to set up depending on your needs:
  - `forceSni` - true -> It will force SSL SNI (Server Name Indication). The SNI will be set to the same value as Host header (set in `clientDestination`)
  - `trustAll` - true/false - weather all server certificates should be trusted or not
  - `verifyHost` - true/false - hostname verification
  - `trustStoreOptions` - if you want to put the server certificates here in order to trust only specific ones - see [Vert.x Http Client Options](http://vertx.io/docs/vertx-core/dataobjects.html#HttpClientOptions) for details
  
E.g.
```json
"clientOptions": {
  "forceSni": true,
  "trustAll": true,
  "verifyHost": false
},
"clientDestination": {
  "scheme": "https",
  "domain": "my.internal.repo.domain",
  "port": 443,
  "hostHeader": "specific.repo.resolution.domain"
}
```

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
Fragment Splitter reads [[Knot Context|Knot]] that contains a HTML Template retrieved from 
the Repository, splits it into static and dynamic Fragments, updates Knot Context and returns back 
to the caller. We also call those dynamic Fragments "Snippets".

## How does it work?
Splitter splits HTML Template using regexp 
`<${SNIPPET_TAG_NAME}\s+data-knotx-knots\s*=\s*"([A-Za-z0-9-]+)"[^>]*>.+?</${SNIPPET_TAG_NAME}>`.
This is efficient method, however it has a limitation that one should remember about. Knot.x just 
scans the markup for the opening of snippet tag (`<${SNIPPET_TAG_NAME}>`) and the first occurrence of 
the end of that tag (`</${SNIPPET_TAG_NAME}>`). Because of that `${SNIPPET_TAG_NAME}` should be
configured wisely. Good example is `knotx:snippet`. Do not use standard html tags like `div` or 
`span` etc. The default value of snippet tag name (`${SNIPPET_TAG_NAME}`) is `script` and you may 
configure it to any value you want (see [configuration section](#how-to-configure)).

During the HTML splitting, all matched snippet tags are converted into Fragments containing list of 
supported [[Knots|Knot]] declared in `data-knotx-knots` attribute. HTML parts below, above and 
between matched snippets are converted into Fragments without Knot support (static Fragments). 
It means that they are not supposed to be processed by Knots. See example for more details.

**Splitter requires `data-knotx-knots` attribute to be the first attribute in the snippet tag.**

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
```html
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
```html
    <script data-knotx-knots="services,handlebars"
            data-knotx-service="first-service"
            type="text/knotx-snippet">
      <div class="col-md-4">
        <h2>Snippet - {{_result.message}}</h2>
      </div>
    </script>
```
**Fragment 3** (identifier = "_raw")
```html
  </div>
</body>
</html>
```

More details about Fragments can be found in the next section.

### Fragment
Fragment contains: 
- list of supported Knots (list of [[Knot Election Rules|Knot]]), 
- Fragment Content (matched snippet or simple HTML)
- Fragment Context (JSON with a progress state)

Fragments matching snippet name tag declare Knots used while further processing (see [[Knots routing|KnotRouting]]). 
They can communicate with external services via [[Adapters|Adapter]], evaluate templates using 
Handlebars and so on. Every Knot defines value ([[Knot Election Rule|Knot]]) for `data-knotx-knots` 
attribute which determines if it will process particular Fragment or not.

Fragments not matching snippet tag are not supposed to be processed while Knots routing. They are 
used at the end of processing to assemble final HTML result (see [[Fragment Assembler|Assembler]]).

## How to configure?
Splitter is deployed using Vert.x service factory as a separate 
[verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default 
configuration.

Default configuration shipped with the verticle as `io.knotx.FragmentSplitter.json` file available 
in the classpath.

Example configuration may look like this:
```json
{
  "main": "io.knotx.splitter.FragmentSplitterVerticle",
  "options": {
    "config": {
      "address": "knotx.core.splitter",
      "snippetTagName": "knotx:snippet"
    }
  }
}
```
In short, the default configuration just defines event bus address on which the Splitter listens 
for jobs to process.

Detailed description of each configuration option is described in the next subsection.

### Splitter config

| Name                        | Type         | Mandatory      | Description  |
|-------:                     |:-------:     |:-------:       |-------|
| `address`                   | `String`     | &#10004;       | Event bus address of the Splitter verticle. |
| `snippetTagName`            | `String`     | &#10004;       | The name of a tag that will be recognised as a Knot.x snippet. The default value is `script`. Remember to update [[Assembler configuration\|Assembler#how-to-configure]] |

**Important - when specifying `snippetTagName` remember to not use standard HTML tags like `div`, `span`, etc.
Knot.x splits an HTML into fragments by parsing it as a string to get the best possible performance. 
It simply search the text for the opening and first matching closing tag. It does not analyse the text 
as HTML. So, if you use `div` as fragmentTagName, and inside your will use multiple `div` tags too, 
then it will not pick the one that matches first opening, instead it will get the fragment up to the 
first closing `div` tag. It will result in a broken HTML structure.**

# Fragment Assembler
Fragment Assembler joins all Fragments into the final output. It's executed at the end of 
all suitable Knots processing, just before generating the response to the page visitor.

## How does it work?
Fragment Assembler reads Fragments from Knot Context, joins them all into one string (future body 
of the response), packs it back to Knot Context and returns back to the caller. 
See examples below for more details.

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
```html
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
It unwraps the snippet, by removing snippet tag tag leaving just body of the snippet. So, the result of 
join will look like this:
```html
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
```html
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
Fragment Assembler is deployed using Vert.x service factory as a separate
[verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default 
configuration.

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

| Name                        | Type      | Mandatory      | Description  |
|-------:                     |:-------:  |:-------:       |-------|
| `address`                   | `String`  | &#10004;       | Event bus address of the Fragment Assembler verticle. |
| `unprocessedStrategy`       | `String`  | &#10004;       | Strategy for unprocessed Fragments (`AS_IS`, `UNWRAP`, `IGNORE`). `UNWRAP` is default strategy if no strategy defined. |
| `snippetTagName`            | `String`  | &#10004;       | The name of a tag that will be recognised as a Knot.x snippet. The default value is `script`. Remember to update [[Splitter configuration\|Splitter#how-to-configure]] |

**Important - when specifying `snippetTagName` remember to not use standard HTML tags like `div`, `span`, etc.
Knot.x splits an HTML into fragments by parsing it as a string to get the best possible performance. 
It simply search the text for the opening and first matching closing tag. It does not analyse the text 
as HTML. So, if you use `div` as fragmentTagName, and inside your will use multiple `div` tags too, 
then it will not pick the one that matches first opening, instead it will get the fragment up to the 
first closing `div` tag. It will result in a broken HTML structure.**

# Knot
Knot defines a business logic which can be applied to a particular [[Fragment|Splitter]]. It can, for 
example, invoke an external services via [[Adapter|Adapter]], evaluate Handlebars snippets or simply 
redirect a site visitor to a different location. 

## How does it work?
Knots are invoked by the [[Server|Server]] **sequentially** according to [[Knots Routing|KnotRouting]] configuration.
Every Knot operates on [Knot Context](#knot-context) which contains a list of Fragments to process. Knot takes care of a processing, optionally updates the Knot Context and returns it back to the caller so that it will be an input for the next called Knot.

A particular Knot will process a Fragment only when those two conditions are met:

- it is defined in [[Knots Routing|KnotRouting]]
- there is at least one Fragment that declares matching [Knot Election Rule](#knot-election-rule)

### Knot Election Rule
Knot Election Rule determines if Knot should process a Fragment or not.
Knot Election Rule is a simple `String` value that comes from a `data-knotx-knots` attribute from the [[Fragment script tag|Splitter#example]]. 
The attribute contains a comma-separated list of Knot Election Rules which *can* be used by Knot to determine if it should
process that particular Fragment or not.

Knots **can** simply filter out Fragments which do not contain the certain Knot Election Rule (for example `services` or `handlebars`).

### Knot Context
Knot Context is a communication model passed between [[Server|Server]], [[Fragment Splitter|Splitter]], 
[[Knots|Knot]] and [[Fragment Assembler|Assembler]]. 

The flow is driven by the [[Server|Server]] forwarding. Originally KnotContext is created by the [[Fragment Splitter|Splitter]] module basing on the [[Repository|RepositoryConnectors]] template input. 

Knot Context contains:
* a client request with a path, headers, form attributes and parameters
* a client response with a body, headers and a status code
* [[Fragments|Splitter]]
* [[Transition|KnotRouting]]] value

From now, we will be using terms *client* and *site visitor* interchangeably.

*A client request* includes a site visitor path (a requested URL), HTTP headers, form attributes 
(for POST requests) and request query parameters.

*A client response* includes a body (which represents the final response body set by [[Fragment Assembler|Assembler]]), 
HTTP headers (which are narrowed finally by [[Server|Server]] according to `allowedResponseHeaders` parameter) and HTTP status code.

Please see [[Splitter|Splitter]] section to find out what Fragments are and how they are produced. 
Fragments are documented [[here|Splitter#fragment]]. Knots can, for example, process 
Fragment Content, call required Adapters and put responses from Adapters to Fragment Context (a JSON object).

**Transition** is a text value which determines the next step in [[Knots Routing|KnotRouting]].

#### Knot Request
The table below represents an event model consumed by Knot. Client request attributes are not modifiable within Knots. 
Client response and Transition attributes are modified by Knots according to required behaviour (continue routing, redirect
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
Knot responds with Knot Context that is consumed and updated according to required behaviour Knot Context object from a request.

Knots are designed to process Knot Context and finally decides what a next step in Knots Routing is valid (via Transition).
It is the default Knot behaviour. Knots can also break Knots Routing and decide to return an error or redirect 
response to the client.

The table below represents Knot response values.

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
| `transition`                 | `String`                      |        | defines the next routing step (Knot), empty for redirects, errors and last routing step |

##### Example Knot Responses
Knot can decide what next routing step (Knot) should be invoked (via `transition` property) or even break Knots Routing. This section 
contains a few example responses.

*Next Routing Step*

Knot decides that routing should be continued. It sets Transition value to `next` and then Server continues 
routing according to its [[configuration|Server]].

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `200`
| `transition`| `next` 

*Redirect response*

Knot finds out that a client must be redirected to another URL.

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `301`
| `clientResponse.headers.location`| `/new/location.html`
| `transition`| EMPTY 

*Error response*

Knot calls Adapter Service and gets **500**. Knot is not aware how this error should be processed so it sets `clientResponse.statusCode` to `500`.
Server breaks the routing and responds with `500` to the client.

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `500`
| `transition`| EMPTY 


## How to configure?
Knots are exposed with an unique Event Bus address - that's the only obligation (this is also true for Adapters).
Please see the example configurations for [[Action Knot|ActionKnot#how-to-configure]], 
[[Service Knot|ServiceKnot#how-to-configure]].

## How to implement your own Knot?

Knot.x provides the [maven archetypes](https://github.com/Knotx/knotx-extension-archetype) to generate custom Knots / [[Adapters|Adapter]]. 
It is the **recommended** way to create your own Knots.

A Knot code is executed on a [Vert.x event loop](http://vertx.io/docs/vertx-core/java/#_reactor_and_multi_reactor). [The 
Vert.x Golden Rule](http://vertx.io/docs/vertx-core/java/#golden_rule) says that the code should **never block** the 
event loop. So all time-consuming operations should be coded in an asynchronous way. By default Knots uses [RxJava](http://vertx.io/docs/vertx-rx/java/) 
which is a popular library for composing asynchronous and event-based programs using observable sequences for the Java VM.
RxJava introduce Reactive Programming what is a development model structured around asynchronous data streams. 

| ! Note |
|:------ |
| Reactive programming code first requires a mind-shift. You are notified of asynchronous events. Then, the API can be hard to grasp (just look at the list of operators). Don’t abuse, write comments, explain, or draw diagrams. RX is powerful, abusing it or not explaining it will make your coworkers grumpy. [Read more](https://developers.redhat.com/blog/2017/06/30/5-things-to-know-about-reactive-programming/) |


In order to implement an Knot generate a new Knot module using maven archetype:

`mvn archetype:generate -DarchetypeGroupId=io.knotx.archetypes -DarchetypeArtifactId=knotx-knot-archetype -DarchetypeVersion=X.Y.Z`

Note that the Knot archetype generates both the code and all configuration
files required to run a Knot.x instance containing the custom Knot. More details about the Knot.x deployment can be found in the
[[deployment section|KnotxDeployment]].

The `ExampleKnotProxy` class contains the Knot processing logic. It extends [`io.knotx.knot.AbstractKnotProxy`](https://github.com/Cognifide/knotx/blob/master/knotx-core/src/main/java/io/knotx/knot/AbstractKnotProxy.java) 
class, and implements the example processing logic in the `processRequest()` method with the return type of `Single<KnotContext>` 
(a promise of the modified `KnotContext`).

The `AbstractKnotProxy` class provides the following methods that you can override in your implementation in order to 
control the processing of Fragments:

- `boolean shouldProcess(Set<String> knots)` is executed on each Fragment from the given KnotContext, from each fragment it gets a set of Knot Election Rules (from the `data-knotx-knots` snippet attribute), and lets you decide whether Fragment should be processed by your Knot or not (no pun intended).
- `Single<KnotContext> processRequest(KnotContext knotContext)` consumes `KnotContext` messages from the [[Server|Server]] and returns the modified `KnotContext` object as an instance of [`rx.Single`](http://reactivex.io/RxJava/javadoc/rx/Single.html).
- `KnotContext processError(KnotContext knotContext, Throwable error)` handles any Exception thrown during processing, and is responsible for preparing the proper KnotContext on such occasions, these will simply finish processing flows, as any error generated by Knot will be immediately returned to the page visitor.

| ! Note |
|:------ |
| Please note that while this section focuses on the Java language specifically, it's not the only choice you have. Thanks to [the polyglot nature of Vert.x](http://vertx.io), you can implement your Adapters and Knots using other languages. |


#### Building and running example Knot
1. Download and copy [Knot.x standalone fat jar](https://github.com/Cognifide/knotx/releases/latest) to the `app` folder
2. Compile, run tests and build a jar package using `mvn package`
3. Copy custom Knot jar (`target/*-1.0-SNAPSHOT-fat.jar`) to the `app` folder
3. Execute `run.sh` script. Console log should contain an entry with ExampleKnot

```
2018-01-08 09:53:46 [vert.x-eventloop-thread-2] INFO  i.k.r.FilesystemRepositoryConnectorVerticle - Starting <FilesystemRepositoryConnectorVerticle>
2018-01-08 09:53:46 [vert.x-eventloop-thread-1] INFO  io.knotx.server.KnotxServerVerticle - Starting <KnotxServerVerticle>
2018-01-08 09:53:46 [vert.x-eventloop-thread-0] INFO  i.k.e.knot.example.ExampleKnot - Starting <ExampleKnot>
2018-01-08 09:53:47 [vert.x-eventloop-thread-3] INFO  i.k.s.FragmentSplitterVerticle - Starting <FragmentSplitterVerticle>
2018-01-08 09:53:47 [vert.x-eventloop-thread-4] INFO  i.k.k.a.FragmentAssemblerVerticle - Starting <FragmentAssemblerVerticle>
2018-01-08 09:53:47 [vert.x-eventloop-thread-5] INFO  i.k.knot.service.ServiceKnotVerticle - Starting <ServiceKnotVerticle>
2018-01-08 09:53:47 [vert.x-eventloop-thread-7] INFO  i.k.k.t.HandlebarsKnotVerticle - Starting <HandlebarsKnotVerticle>
2018-01-08 09:53:47 [vert.x-eventloop-thread-6] INFO  i.k.knot.action.ActionKnotVerticle - Starting <ActionKnotVerticle>
2018-01-08 09:53:47 [vert.x-eventloop-thread-1] INFO  io.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2018-01-08 09:53:47 [vert.x-eventloop-thread-0] INFO  i.k.launcher.KnotxStarterVerticle - Knot.x STARTED

                Deployed 7e0a0bb8-4704-431f-9a21-0d6a6f013587 [knotx:io.knotx.FilesystemRepositoryConnector]
                Deployed 67c6c8e9-5249-4b58-bda6-7f78361c50c5 [knotx:io.knotx.exampleknot.knot.example.ExampleKnot]
                Deployed eee726c5-2e12-4455-95c6-32b2e02eef0f [knotx:io.knotx.FragmentAssembler]
                Deployed 4920772b-dd96-4325-adef-488b1d541d0b [knotx:io.knotx.FragmentSplitter]
                Deployed 947721ba-e107-4500-a282-fb73d4316eac [knotx:io.knotx.ServiceKnot]
                Deployed d987e3a2-acad-4e93-89b3-216b1551426a [knotx:io.knotx.ActionKnot]
                Deployed 906ea442-6863-4a89-bddc-f538a6e27084 [knotx:io.knotx.HandlebarsKnot]
                Deployed 0b2e0245-4afa-4e9d-8a39-5e8fbbaab810 [knotx:io.knotx.KnotxServer]
```

5. Open a [http://localhost:8092/content/local/template.html](http://localhost:8092/content/local/template.html) link in your 
browser to validate a Knot header message (`Knot example`).

### How to handle blocking code in your own Knot?
The easiest way to handle a blocking code inside your Knot is to deploy it as a [Vert.x worker](http://vertx.io/docs/vertx-core/java/#worker_verticles).
No change in your code is required.

To do so you need to tell Vert.x that your custom Knot should be processed in workers pool via [DeploymentOptions](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html).
```
{
  "main": "some.package.knot.example.ExampleKnot",
  "options": {
    "worker": true,
    "config": {
      ...
    }
  }
}
```
Now in your Knot.x instance log file you should see
```
2018-01-08 10:00:16 [vert.x-worker-thread-0] INFO  i.k.e.knot.example.ExampleKnot - Starting <ExampleKnot>
```
For more information about deployment options of Worker verticles see [Vert.x documentation](http://vertx.io/docs/vertx-core/java/#worker_verticles).

### How to implement your own Knot without Rx Java?
Extending `AbstractKnotProxy` is the **recommended** way to implement your custom Knots. But still you can resign from
this approach and implement your custom Knots with Vert.x handlers (without using RxJava). The only one thing to change 
is to implement `KnotProxy` instead of extending `AbstractKnotProxy`. Then you need to implement a 
method `void process(KnotContext knotContext, Handler<AsyncResult<KnotContext>> result)` where you should implement your 
custom Knot Election Rule and processing logic. 

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
<form method="post">
  <input name="_frmId" value="1" type="hidden">
  <input name="email" value="" type="email">
  <input value="Submit" type="submit">
 </form><p>Please provide your email address</p>

 <div>
  <strong>Pro tip: All emails that starts with <kbd>john.doe</kbd> will be accepted.</strong>
 </div>
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

### Vert.x Event Bus delivery options

While HTTP request processing, Action Knot calls Adapter using 
[Vert.x Event Bus](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/EventBus.html). The `config` field can contain 
[Vert.x Delivery Options](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/DeliveryOptions.html) related to the event 
bus. It can be used to control the low level aspects of the event bus communication like timeouts, headers, message 
codec names.

The `deliveryOptions` need to be added in the following place, of the Action Knot configuration to define the 
timeout for the Adapter response.
```
{
  "options": {
    "config": {
      "deliveryOptions": {
        "timeout": 15000,
         ...
      },
      ...
```

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

### Vert.x Event Bus delivery options

While HTTP request processing, Service Knot calls Adapter / Adapters using 
[Vert.x Event Bus](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/EventBus.html). The `config` field can contain 
[Vert.x Delivery Options](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/DeliveryOptions.html) related to the event 
bus. It can be used to control the low level aspects of the event bus communication like timeouts, headers, message 
codec names.

The `deliveryOptions` need to be added in the following place, of the Service Knot configuration to define the 
timeout for the Adapter response.
```
{
  "options": {
    "config": {
      "deliveryOptions": {
        "timeout": 15000,
         ...
      },
      ...
```

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
      "address": "knotx.knot.handlebars"
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
| `cacheSize`                 | `Long`                              |                | Max cache size for compiled Handlebars snippets. The default is 1000. |
| `cacheKeyAlgorithm`         | `String: MD5,SHA-1,SHA-256`         |                | Fragment content hashing algorithm. The default is `MD5`. |

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

We recommend to create a dedicated Adapter every time some service-level business logic or service 
response adaption to other format is required. E.g. we need to 
[inject the data directly form the database](http://knotx.io/blog/adapt-service-without-webapi/).


### Types of adapters
Knot.x Core by default introduces two types of Adapters connected with Knot implementations:
- [[Service Adapter|ServiceAdapter]] for [[Service Knot|ServiceKnot]],
- [[Action Adapter|ActionAdapter]] for [[Action Knot|ActionKnot]]

Knot.x comes with a generic implementation of [[Service Adapter|ServiceAdapter]], that enables communication 
with external services using HTTP Protocol (only GET requests).
This [Hello Rest Service Tutorial](http://knotx.io/blog/hello-rest-service/) contains an example of
how to integrate external Web based service data into your webpage. See also [[Http Service Adapter|HttpServiceAdapter]] 
for more information. Please note, that this implementation is very generic and we recommend to create 
project-specific Adapters for any custom requirements.

Action Adapters are project specific in terms of error handling and redirection mechanisms. Knot.x Core
is not going to provide any generic Action Adapters.

For custom Knots we can introduce custom Adapter types. As far as Knots must follow [[Knot contract|Knot#how-does-it-work]],
Adapters are coupled with Knot directly so they can define their custom request, response or 
configuration. The communication between Knot and Adapter can be custom too.

Communication contract between [[Service Knot|ServiceKnot]] and [[Http Service Adapter|HttpServiceAdapter]] is defined by:
- `AdapterRequest` input,
- `AdapterResponse` output.

#### Adapter Request
The table below shows all the fields in the `AdapterRequest` - the communication model between Knot.x Service Knot and Service Adapters.

| Name                        | Type           | Mandatory | Description  |
|-------:                     |:-------:       |:-------:   |-------|
| `clientRequest.path`        | `String`       | &#10004;   | client request url, e.g. `/services/mock/first.json` |
| `clientRequest.method`      | `HttpMethod`   | &#10004;   | client request method, e.g. `GET`, `PUT`, etc. |
| `clientRequest.headers`     | `MultiMap`     | &#10004;   | client request headers |
| `clientRequest.params`      | `MultiMap`     | &#10004;   | client request parameters |
| `params`                    | `JsonObject`   | &#10004;   | `JsonObject` with additional params that can be passed via configuration file, e.g. `"params": { "example": "example-value" }` |
| `adapterParams`             | `JsonObject`   |            |  `JsonObject` with additional adapter parameters that can be set in the form of `data-knotx-adapter-params` in the snippet, e.g. `data-knotx-adapter-params='{"myKey":"myValue"}'` |

#### Adapter Response
The table below shows all the fields in the `AdapterResponse` - an object returned by the Adapter to the Service Knot.

| Name                        | Type          | Mandatory   | Description  |
|-------:                      |:-------:     |:-------:    |-------|
| `clientResponse.statusCode`  | `int`        | &#10004;    | status code of service response, e.g. `200`, `302`, `404` |
| `clientResponse.headers`     | `MultiMap`   | &#10004;    | client response headers |
| `clientResponse.body`        | `Buffer`     |             | final response body |
| `signal`                     | `String`     |             | defines how original request processing should be handled (currently used only by Action Knot), e.g. `next` |


## How to configure?
The Adapter API specifies an abstract class - `AdapterConfiguration` to handle JSON configuration support. This
abstraction can be used while implementing a custom Adapter but it is not required. Every Adapter must be
exposed with a unique Event Bus address - that's the only obligation (as is the case with Knots).
Please see an example configuration for [[Http Service Adapter|HttpServiceAdapter#how-to-configure]]

## How to implement your own Adapter?
Knot.x provides the [maven archetypes](https://github.com/Knotx/knotx-extension-archetype) to generate custom Adapters. 
It is the **recommended** way to create your own Adapter.

An Adapter logic is executed on a [Vert.x event loop](http://vertx.io/docs/vertx-core/java/#_reactor_and_multi_reactor). [The 
Vert.x Golden Rule](http://vertx.io/docs/vertx-core/java/#golden_rule) says that the code should **never block** the 
event loop. So all time-consuming operations should be coded in an asynchronous way. Default Knot.x Adapters use [RxJava](http://vertx.io/docs/vertx-rx/java/) 
which is a popular library for composing asynchronous and event-based programs using observable sequences for the Java VM.
RxJava introduce a Reactive Programming that is a development model structured around asynchronous data streams. 

| Note |
|:------ |
| Reactive programming code first requires a mind-shift. You are notified of asynchronous events. Then, the API can be hard to grasp (just look at the list of operators). Don’t abuse, write comments, explain, or draw diagrams. RX is powerful, abusing it or not explaining it will make your coworkers grumpy. [Read more](https://developers.redhat.com/blog/2017/06/30/5-things-to-know-about-reactive-programming/) |

Implementation of an Adapter does not require knowledge of how to communicate via the Vert.x event bus.
It's wrapped by the **Vert.x Service Proxy** functionality so any new implementation can focus on 
the business logic of the Adapter.

In order to implement an Adapter generate a new Adapter module using maven archetype:

   `mvn archetype:generate -DarchetypeGroupId=io.knotx.archetypes -DarchetypeArtifactId=knotx-adapter-archetype -DarchetypeVersion=X.Y.Z`

Note that, the Adapter archetype generates not only a skeleton of your custom Adapter, but also all
the configuration files that's required to run a Knot.x instance. 
More details about the Knot.x deployment can be found in the [[deployment section|KnotxDeployment]].

Archetype generates 3 important java files:
 
 - `ExampleServiceAdapterConfiguration` a simple POJO with configuration of the Adapter,
 - `ExampleServiceAdapterProxy` implement your business logic here in the `processRequest()` method with the return type of `Observable<AdapterResponse>` (promise of the `AdapterResponse`).
 - `ExampleServiceAdapter` that extends `AbstractVerticle`. It will simply read the configuration and register your `AdapterProxy` implementation at the provided `address`. 

The `AbstractAdapterProxy` class provides the following methods that you can extend in your implementation:

- `Observable<AdapterResponse> processRequest(AdapterRequest message)` method that consumes 
`AdapterRequest` messages from [[Knot|Knot]] and returns [`AdapterResponse`](#adapter-response) object as `rx.Observable`
- Optionally, `AdapterResponse getErrorResponse(AdapterRequest request, Throwable error)` method 
which handles any Exception thrown during processing, and is responsible for preparing proper 
[AdapterResponse](#adapter-response) on such situations. By default `AbstractAdapterProxy` implements this method, 
and returns `AdapterResponse` with the `ClientResponse` object having `500` status code and the 
error message in response body. 

| ! Note |
|:------ |
| Please note that while this section focuses on the Java language specifically, it's not the only choice you have. Thanks to [the polyglot nature of Vert.x](http://vertx.io), you can implement your Adapters and Knots using other languages. |

| ! Note |
|:------ |
| Besides the Verticle implementation itself, a custom implementation of your Adapter must be built as a Knot.x module in order to be deployed as part of Knot.x. Follow the [Knot.x Modules](https://github.com/Cognifide/knotx/wiki/KnotxModules) documentation in order to learn how to make your Adapter a module. | 

#### Building and running example Adapter
To run the extension:

1. [Download the Knot.x fat jar](https://github.com/Cognifide/knotx/releases/latest) and  it to the `apps` folder.
2. Build the extension using `mvn package`
3. Copy custom Adapter fat jar from the `target` directory into the `apps` directory
4. Execute the `run.sh` bash script. You will see output similar to the following:
```
2017-08-04 15:10:21 [vert.x-eventloop-thread-2] INFO  i.k.r.FilesystemRepositoryConnectorVerticle - Starting <FilesystemRepositoryConnectorVerticle>
2017-08-04 15:10:21 [vert.x-eventloop-thread-3] INFO  i.k.s.FragmentSplitterVerticle - Starting <FragmentSplitterVerticle>
2017-08-04 15:10:21 [vert.x-eventloop-thread-1] INFO  io.knotx.server.KnotxServerVerticle - Starting <KnotxServerVerticle>
2017-08-04 15:10:21 [vert.x-eventloop-thread-0] INFO  i.k.a.example.ExampleServiceAdapter - Starting <ExampleServiceAdapter>
2017-08-04 15:10:21 [vert.x-eventloop-thread-4] INFO  i.k.k.a.FragmentAssemblerVerticle - Starting <FragmentAssemblerVerticle>
2017-08-04 15:10:21 [vert.x-eventloop-thread-7] INFO  i.k.k.t.HandlebarsKnotVerticle - Starting <HandlebarsKnotVerticle>
2017-08-04 15:10:21 [vert.x-eventloop-thread-6] INFO  i.k.knot.action.ActionKnotVerticle - Starting <ActionKnotVerticle>
2017-08-04 15:10:21 [vert.x-eventloop-thread-5] INFO  i.k.knot.service.ServiceKnotVerticle - Starting <ServiceKnotVerticle>
2017-08-04 15:10:22 [vert.x-eventloop-thread-1] INFO  io.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2017-08-04 15:10:22 [vert.x-eventloop-thread-0] INFO  i.k.launcher.KnotxStarterVerticle - Knot.x STARTED

                Deployed 3bd0365c-10ba-4a53-a29c-59b4df06eaff [knotx:io.knotx.FragmentSplitter]
                Deployed defc43bc-ffe0-40cf-8c0c-1be06c5e8739 [knotx:com.example.adapter.example.ExampleServiceAdapter]
                Deployed 6891d9d6-bcfc-42fa-8b21-bd4ea11154da [knotx:io.knotx.FragmentAssembler]
                Deployed 59a2967f-0d87-4dea-ad37-b8fe1dafb898 [knotx:io.knotx.FilesystemRepositoryConnector]
                Deployed b923df1c-f8f9-4615-9b9e-4fba3806a575 [knotx:io.knotx.ActionKnot]
                Deployed c135524e-b4b1-452c-b8e7-628dfc58195d [knotx:io.knotx.ServiceKnot]
                Deployed 93ea2509-e045-49f8-9ddb-fc167e16f020 [knotx:io.knotx.HandlebarsKnot]
                Deployed 880116cf-54a7-4849-954e-dfb4eb536685 [knotx:io.knotx.KnotxServer]
```
5. Open a page: [http://localhost:8092/content/local/template.html](http://localhost:8092/content/local/template.html) in your 
browser to validate a page displays value `Hello Knot.x`.

### How to handle blocking code in your own Adapter?
The easiest way to handle a blocking code inside your Adapter is to deploy it as a [Vert.x worker](http://vertx.io/docs/vertx-core/java/#worker_verticles).
No change in your code is required.

To do so you need to tell Vert.x that your custom Adapter should be processed in workers pool via [DeploymentOptions](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html).
```
{
  "main": "com.example.SomeAdapter",
  "options": {
    "worker": true,
    "config": {
      ...
    }
  }
}
```
Now in your Knot.x instance log file you should see
```
2018-01-08 10:00:16 [vert.x-worker-thread-0] INFO  com.example.SomeAdapter - Starting <SomeAdapter>
```
For more information about deployment options of Worker verticles see [Vert.x documentation](http://vertx.io/docs/vertx-core/java/#worker_verticles).

### How to implement your own Adapter without Rx Java?
Extending `AbstractAdapterProxy` is the **recommended** way to implement your custom Adapter. But still you can resign from
this approach and implement your custom Adapter with Vert.x handlers (without using RxJava). The only one thing to change 
is to implement `AdapterProxy` instead of extending `AbstractAdapterProxy`. Then you need to implement a 
method `void process(AdapterRequest request, Handler<AsyncResult<AdapterResponse>> result)` where you should implement your 
custom Adapter business logic. 

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

## How to implement your own Service Adapter?
Implementing custom Service Adapter that meets your project requirements allows 
you to adopt request from Knot.x into request understandable by an endpoint service, and adopts 
responses from that service into unified message understandable by Knot.x.

For you convenience, Knot.x comes with implementation of a [[Service Adapter|ServiceAdapter]], 
that enables communication with external services using HTTP Protocol. 

See [[Http Service Adapter|HttpServiceAdapter]] for more information.
Please note, that this implementation is very generic and we recommend to create project-specific 
adapters for your setup.

Writing custom Service Adapter requires fulfilling [[Service Knot|ServiceKnot]] contract.
Please refer also to [[Adapter|Adapter#how-to-implement-your-own-adapter]].

| ! Note |
|:------ |
| Besides Verticle implementation itself, a custom implementation of your Service Adapter must be build as Knot.x module in order to be deployed as part of Knot.x. Follow the [Knot.x Modules](https://github.com/Cognifide/knotx/wiki/KnotxModules) in order to see how to make your Service Adapter a module. | 


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

## How to implement your own Action Adapter?
Implementing Action Adapter that meets your project requirements allows 
you to adopt request from Knot.x into request understandable by an endpoint service, and adopts 
responses from that service into unified message understandable by Knot.x.

For you convenience, Knot.x is shipped with a default Action Adapter called `HttpActionAdapter`.

Writing custom Service Adapter requires fulfilling [[Action Knot|ActionKnot]] contract.
Please refer also to [[Adapter|Adapter#how-to-implement-your-own-adapter]].

| ! Note |
|:------ |
| Besides Verticle implementation itself, a custom implementation of your Action Adapter must be build as Knot.x module in order to be deployed as part of Knot.x. Follow the [Knot.x Modules](https://github.com/Cognifide/knotx/wiki/KnotxModules) in order to see how to make your Action Adapter a module. | 


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
        "setIdleTimeout": 600,
        "tryUseCompression": true,
        "logActivity": true
      },
      "customRequestHeader": {
        "name": "Server-User-Agent",
        "value": "Knot.x"
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
  - `setIdleTimeout` - any connections not used within this timeout will be closed, set in seconds,
  - `keepAlive` - that shows keep alive, we recommend to leave it set to `true` as the default value in Vert.x. You can find more information [here](http://vertx.io/docs/vertx-core/java/#_http_1_x_pooling_and_keep_alive).
- `customRequestHeader` - an JSON object that consists of name and value of the header to be sent in each request to any service configured. If the same header comes from the client request, it will be always overwritten with the value configured here.
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
Thanks to the modular architecture of Knot.x, there are many ways to deploy Knot.x for
the production usage. However, the easiest approach is to use Knot.x as one **fat** jar together with
jar files specific for your target implementation (such as custom [[Adapters|Adapter]], [[Knots|Knot]]
or Handlebars helpers). These jar files should be all available in the classpath.

## Recommended Knot.x deployment
For the purpose of this example let's assume you have `KNOTX_HOME` folder created on the host machine where Knot.x
is going to be run. Following simple steps is a recommended way
to deploy Knot.x with custom modules:

- Create a subfolder `KNOTX_HOME/app` and put **knotx-standalone-X.Y.Z-fat.jar** in that folder.

- If you have custom Handlebars helpers, you can put them in that folder as well (as JAR files).

- If you have project specific [[Adapters|Adapter]] or [[Knots|Knot]], you can put their jar files in the same folder.
You don't need to embed Knot.x dependencies (e.g. `knotx-common` and `knotx-adapter-api`) in your custom jar files.
They will be taken from **knotx-standalone-X.Y.Z-fat.jar**.

- Create your own configuration JSON (any location on the host). Use `knotx-standalone.json`
from the [latest release](https://github.com/Cognifide/knotx/releases) as a reference. In this example,
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
java -Dlogback.configurationFile=logback.xml -cp "app/*" io.knotx.launcher.LogbackLauncher -conf knotx-starter.json
```

The execution of Knot.x using a launcher as above it uses a following exit codes as specified in [Vert.x documentation|http://vertx.io/docs/vertx-core/java/#_launcher_and_exit_code].
Additionally, Knot.x adds following exit codes:
- `30` - If the configuration is missing or it's empty

### Vert.x metrics
You might want to enable Vert.x metrics in order to monitor how Knot.x performs.
Currently, it's possible to enable JMX metrics, so you can use any JMX tool, like JConsole, to inspect all the metrics Vert.x collects.

In order to enable it, add following JVM property when starting Knot.x
```
java -Dcom.sun.management.jmxremote -Dvertx.metrics.options.jmxEnabled=true -Dvertx.metrics.options.jmxDomain=knotx ...
```
Vert.x collects:
- Vert.x elements metrics (such as amount of verticles, worker pool sizes, etc.)
- Event bus metrics (such as bytes sent/received, messages delivered, etc.)
- HTTP Clients metrics (such as bytes sent/received, connections, amount of requests, per http code responses, etc.)
- HTTP Server metrics (such as bytes sent/received, connections, amount of requests, etc.)

For a detailed description of available metrics please check [Vert.x The Metrics](http://vertx.io/docs/vertx-dropwizard-metrics/java/#_the_metrics) page

| ! Warning |
|:------ |
| **We don’t recommend gathering metrics from your production environment. JMX’s RPC API is fragile and bonkers. However for development purposes and troubleshooting it can be very useful.** |

## How to configure ?
As mentioned above, the `knotx-starter.json` is the main configuration file describing what Knot.x modules need to be started as part of Knot.x.

`knotx-standalone.json` configuration available on GitHub looks like below
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
As you see, it simply have list of modules that Knot.x should start. Out of the box, no other configuration is required as each module is shipped with its default config.

However, at the production environment you often need to alter the configuration parameters such as port of HTTP server, or HTTP headers that are
being passed, or addresses of the client services used for rendering dynamic content.

Thanks to the Knot.x capabilities you can provide your configurations that modifies defaults. There are three ways:
1. In your `knotx-starter.json` file add `config` section for each module that needs default configuration to be modified. You only need to specify elements that
should be changed. Follow the guide of each Verticle to see the supported parameters.
2. With JVM properties: you can provide single values for desired fields (e.g. http port) or even whole json objects from external JSON file.
Any parameter provided through system properties will always override default and starter values.
3. It is also possible to create your own module that uses existing Knot.x Verticle. In that module you can build the configuration file from scratch.
Such module configuration might be also overridden using starter JSON and/or JVM properties.

### How to configure Knot.x in starter JSON ?
In your project specific `knots-starter.json` add `config` object. For each module you want to configure put a field with configuration object.
For instance, if you want to modify configuration of KnotxServer module, you can do it as follows:
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
          "serverOptions": {
            "port": 9999
          }
        },
        "instances": 2
      }
    }
  }
}
```
Important things to remember:
- `options` field maps exactly to a [vert.x Deployment Options](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html) object.
It means, that you can specify here deployment options such as how many instances of that module should be deployed, etc.
- Inside `options` you can supply `config` object where you override configuration for the verticle provided by the module.
See Knot.x Verticle documentation to see what's available on each Verticle.

If you start Knot.x with the configuration as above, it will start all modules listed in the config, but the `io.knotx.KnotxServer` will be deployed as:
- Two instances
- It will listen on port 9999

### How to configure with JVM properties ?
In some cases, you might want to provide configuration parameters with JVM properties, e.g. you can have same config used on all environments,
but you wanted to specify HTTP port of the server to be different on each host.
In such case, you can simply provide environment specific port as JVM property, e.g.
```
-Dio.knotx.KnotxServer.options.config.serverOptions.port=9999
```

Additionally, if you want to change more than one value, you can create separate JSON file with all parameters you want to modify, and inject that config using JVM property.

For instance, you might want to have routing of `io.knotx.KnotxServer` defined in a separate JSON file called `my-routing.json` that might looks like below:
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
Then, you can use that file to override default routing as below:
```
-Dio.knotx.KnotxServer.options.config.routing=file:my-routing.json
```

### How to configure your own module ?
The last option to change Verticle configuration, is to redefine Knotx module by creating your own descriptor.
The descriptor is simply a JSON file that must be available in classpath (e.g. in JAR file of your custom Verticle implementation,
or inside folder where all JAR files you put on your installation).

Name of the descriptor file is important as it's a module name used in starter JSON.

For instance, you might want to create KnotxServer configuration from scratch, ignoring default config available.
All you have to do is to create module descriptor file, e.g. `my.KnotxServer.json` with the content as below
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
Finally, you can still override that config as described above: through starter JSON, or through JVM properties:

Single value:
```
-Dmy.KnotxServer.options.config.serverOptions.port=9999
```

Or, whole JSON Object from external file
```
-Dmy.KnotxServer.options.config.routing=file:/path/to/my-routing.json
```

### What happens when config refers to non-existing module?
Let's assume that you work over a new `io.example.MyCustomModule` module and it will be implemented inside `custom-module.jar`. As mentioned above, you should do 2 things to start using it
within Knot.x instance. You need to add it to the list of Knot.x modules in the main config file:

```json
{
  "modules": [
    "knotx:io.knotx.KnotxServer",
    ...
    "knotx:io.example.MyCustomModule"
  ]
}
```

And add the `custom-module.jar` to the classpath. But what will happen if you actually forgot to add `custom-module.jar` to the classpath?
Knot.x will start the instance with following warning:

```
2017-08-29 15:42:57 [vert.x-eventloop-thread-0] WARN i.k.launcher.KnotxStarterVerticle - Cannot find module descriptor file io.example.MyCustomModule.json on classpath
```

Instance will start as though `io.example.MyCustomModule` didn't exist.

# Knot.x Tuning
[[Knotx deployment section|KnotxDeployment]] describes how to configure and run a Knot.x application. 
It is a great place to start the adventure with Knot.x. However, this configuration is not sufficient
for your production. Knot.x is the JVM application on the top of the [Vert.x framework](http://vertx.io/).
So uses an asynchronous non-blocking development model and not thread-based concurrency 
strategy called [event loop](http://vertx.io/docs/vertx-core/java/#_reactor_and_multi_reactor).
Those features allow Knot.x to be very efficient integration platform but introduce new aspects and 
challenges connected with reactive programming.

This section describes how our JVM process should be configured (including JVM Garbage Collector 
settings) and how our Knots and Adapters can be scaled vertically.
 
You can also play with a [Knot.x cookbook](https://github.com/Knotx/knotx-cookbook) which comes with 
all recommended JVM settings. But still all sections below are mandatory!  

## JVM settings
Knot.x is designed to run on Java 8. All the settings in this section were validated with:
```
java version "1.8.0_131"
Java(TM) SE Runtime Environment (build 1.8.0_131-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.131-b11, mixed mode)
```

### Heap size
The settings below defines initial and maximum memory allocation pool for JVM. 
```
-Xms1024m -Xmx3072m
```
As Knot.x can be easily extended with your custom modules the memory settings should be validated with 
your current set of modules.

### GC settings
Knot.x creates a large number of short-lived objects with every HTTP request - it is a common 
characteristic for reactive systems. With Netty and Vert.x event loops, Knot.x is not limited with any
thread pool. So it can consume a large number of concurrent HTTP requests. When Knot.x is overloaded 
we face a typical producer-consumer problem - when a producer is too active (e.g. more HTTP requests 
than the server is able to process). As the system engineers, our goal is to set the GC in such way that
Knot.x will not crash because of `OutOfMemoryError` under a heavy load and will be able to come back to 
the normal state when the load is lower (read more about the 
[Linux Out-of-Memory Killer](http://www.oracle.com/technetwork/articles/servers-storage-dev/oom-killer-1911807.html)).

The settings below were validated with *limited number of cases* so you need to perform a performance
tests with your own set of modules. In our tests, Knotx was deployed to AWS *c5.large* instances with
`2CPU` and `4GB` of RAM.

```
-XX:+UseConcMarkSweepGC -XX:ParallelGCThreads=2 -XX:ParallelCMSThreads=1
``` 

The ParallelGCThreads parameter should be the same as the number of available CPUs.

### Single thread model optimisation
With Vert.x concurrency model all objects are locked only by one thread (event loop) so the JVM can make an 
optimization and "bias" that object to that thread in such a way that subsequent atomic operations 
on the object incurs no synchronization cost. More details can be found [here](http://www.oracle.com/technetwork/java/tuning-139912.html#section4.2.5).

```
-XX:+UseBiasedLocking -XX:BiasedLockingStartupDelay=0
```

## OS Tuning
When running Knot.x on a server where high traffic is expected don't forget about OS settings. 
It is very important to set specific system properties to keep Knot.x instance alive during high traffic spikes.

We performed our tests on *CentOS 7* and following recommendations consider this OS.

### System settings
Following sysctl properties are recommended for the production Knot.x setup:

```
  net.core.netdev_max_backlog=2048
  net.core.rmem_max=8388608
  net.core.somaxconn=4096
  net.core.wmem_max=8388608
  net.ipv4.tcp_fin_timeout=10
  net.ipv4.tcp_max_syn_backlog=8192
  net.ipv4.tcp_rmem=4096 81920 8388608
  net.ipv4.tcp_slow_start_after_idle=0
  net.ipv4.tcp_syn_retries=2
  net.ipv4.tcp_synack_retries=2
  net.ipv4.tcp_tw_reuse=1
  net.ipv4.tcp_wmem=4096 16384 8388608
  vm.swappiness=10
```

### Opened descriptors limits
Remember to increase the default opened descriptor limits to at least `65536`. 
Without increasing this limit you may end with following exception in Knot.x logs:
```
Jan 18, 2018 1:25:08 PM sun.rmi.transport.tcp.TCPTransport$AcceptLoop executeAcceptLoop
WARNING: RMI TCP Accept-18092: accept loop for ServerSocket[addr=0.0.0.0/0.0.0.0,localport=18092] throws
java.net.SocketException: Too many open files (Accept failed)
        at java.net.PlainSocketImpl.socketAccept(Native Method)
        at java.net.AbstractPlainSocketImpl.accept(AbstractPlainSocketImpl.java:409)
        at java.net.ServerSocket.implAccept(ServerSocket.java:545)
        at java.net.ServerSocket.accept(ServerSocket.java:513)
        at sun.rmi.transport.tcp.TCPTransport$AcceptLoop.executeAcceptLoop(TCPTransport.java:400)
        at sun.rmi.transport.tcp.TCPTransport$AcceptLoop.run(TCPTransport.java:372)
        at java.lang.Thread.run(Thread.java:748)
```
This warning means that Knot.x tried to open more connections than the OS allows.

# Logging
By default Knot.x picks up the logger configuration from its default location for the system (e.g. classpath:logback.xml), 
but you can set the location of the config file through `logback.configurationFile` system property.
```
java -Dlogback.configurationFile=/path/to/logback.xml -jar ...
```
Knot.x core provides preconfigured three log files:
- `knotx.log` that logs all **ERROR** messages from the Netty & Vert.x and **INFO** messages from Knot.x application. Enabled by default on Knot.x standalone
- `knotx-access.log` that logs all HTTP requests/responses to the Knot.x HTTP Server. Enabled by default on Knot.x standalone
- `knotx-netty.log` that logs all network activity (logged by the Netty). Not enabled on Knot.x standalone. See [[Log network activity|#log-network-activity]] on how to enable it.

All logs are configured by default:
- To log both to file and console
- Log files are rolled over every day or if the log file exceeds 10MB
- Rolled files are automatically compressed
- History log files are kept forever

A default configuration can be overriden in order to meet your log policy. See [[Configure logback|#configure-logback]] for details.

## Configure Logback
Knot.x provides a default set of logback configurations that you can include, if you just want to set levels, or create your own specific loggers, e.g.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true">
  <include resource="io/knotx/logging/logback/defaults.xml"/>
  <include resource="io/knotx/logging/logback/console-appender.xml"/>
  <include resource="io/knotx/logging/logback/file-appender.xml"/>

  <root level="INFO">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
  </root>

  <logger name="io.knotx" level="TRACE"/>
</configuration>
```
Will create console & file logger for Knot.x logs. Besides that there are other includes that brings new logs, these are:
- `io/knotx/logging/logback/access.xml` - access log
- `io/knotx/logging/logback/netty.xml` - network activity logs

All those configurations uses useful System properties which the Logback takes care of creating for you. These are:
- `${LOG_PATH}` - represents a directory for log files to live in, for knotx, access & netty logs. It's set with the value from `LOG_PATH` System property, or from your own logback.xml file. If none of these specified, logs to the current working directory `/logs` subfolder.
- `${KNOTX_LOG_FILE}`, `${ACCESS_LOG_FILE}`, `${NETTY_LOG_FILE}` - Ignores `LOG_PATH` and use that property to specify actual location for the knotx, access & netty log files (e.g. `/var/logs/knotx.log`, or `/var/logs/access.log`)
- `${KNOTX_LOG_DATEFORMAT_PATTERN}` - Allows to specify a different date-time format for log entries in `knotx.log` and `knotx-netty.log` files only. If not specified, `yyyy-MM-dd HH:mm:ss.SSS` is used.
- `${CONSOLE_KNOTX_LOG_PATTERN}` - Allows to override a default log pattern for console logging.
- `${FILE_KNOTX_LOG_PATTERN}`, `${FILE_ACCESS_LOG_PATTERN}`, `${FILE_NETTY_LOG_PATTERN}` - Allows to override a default log pattern for knotx, access & netty logs.
- `${KNOTX_LOG_FILE_MAX_SIZE}`, `${ACCESS_LOG_FILE_MAX_SIZE}`, `${NETTY_LOG_FILE_MAX_SIZE}` - Allows to define a maximum size of knotx, access & netty log files. If not specified, a max size is `10MB`
- `${KNOTX_LOG_FILE_MAX_HISTORY}`, `${ACCESS_LOG_FILE_MAX_HISTORY}`, `${NETTY_LOG_FILE_MAX_HISTORY}` - Allows to define a maximum amount of archived log files kept in the log folder (for knotx, access & netty logs). If not specified, it keeps files forever.

See the [Knot.x core logback settings](https://github.com/Cognifide/knotx/blob/master/knotx-core/src/main/resources/io/knotx/logging/logback/) configuration files for details.

## Configure logback for file only output
In a production system, you want to disable console logging and write output only to a file both for knotx & access logs. 
You need to create a custom `logback.xml` that imports `file-appender.xml` but not `console-appender.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="io/knotx/logging/logback/defaults.xml" />
    <include resource="io/knotx/logging/logback/file-appender.xml" />
    <include resource="io/knotx/logging/logback/access.xml" />
    
    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

**NOTE: Do not forgot to specify usage of your custom logback file through `-Dlogback.configurationFile` system property.**

Additionally, you want to specify a location of log files in your file system. And roll over policy to keep last 30 archived log files.
You can do this, through your custom logback file:
```xml
<configuration>
  <property name="LOG_PATH" value="/path/to/logs"/>
  <property name="KNOTX_LOG_FILE_MAX_HISTORY" value="30"/>
  <property name="ACCESS_LOG_FILE_MAX_HISTORY" value="30"/>
  
  <include resource="io/knotx/logging/logback/defaults.xml" />
  ...
</configuration>
```
Or, you can provide those settings through System properties:
```
-DLOG_PATH=/path/to/logs -DKNOTX_LOG_FILE_MAX_HISTORY=30 -DACCESS_LOG_FILE_MAX_HISTORY=30
```
Or, even you can mix both approaches. So define default settings inside logback, and configure to respect new System properties you can use to override
```xml
<configuration>
  <property name="LOG_PATH" value="${my.logs:-/path/to/logs}"/>
  <property name="KNOTX_LOG_FILE_MAX_HISTORY" value="${my.logs.history:-30}"/>
  <property name="ACCESS_LOG_FILE_MAX_HISTORY" value="${my.logs.history:-30}"/>
  
  <include resource="io/knotx/logging/logback/defaults.xml" />
  ...
</configuration>
```
And your System property, that will set different log path, and max history for both log files to `100`:
```
-Dmy.logs=/other/path -Dmy.logs.history=100
```
As you can see, Logback logger brings a tremendous amount of possibilities how to configure your logs. 
It's impossible to present here all the possibilities, so the best way would be to study [Logback Documentation](https://logback.qos.ch/manual/index.html). 

## Log network activity
Network activity (logged by Netty) logger settings are provided by the Knot.x core. In order to log it, you need to use your custom logback.xml file and configure it as follows
```xml
<configuration>
    <!-- Your properties is required -->
    
    <!-- Knotx & access logs -->
    <include resource="io/knotx/logging/logback/defaults.xml" />
    <include resource="io/knotx/logging/logback/file-appender.xml" />
    <include resource="io/knotx/logging/logback/access.xml" />
 
    <!-- Add netty network activity logger -->
    <include resource="io/knotx/logging/logback/netty.xml" />
    
    <!-- other logger settings -->
</configuration>
```
Additionally, in your knot.x json configuration file, you need to enable logging network activity. You can enable this both for HTTP server as well as HTTP clients used.
- To enable it for server, just overwrite KnotxServer configuration:
```json
"config": {
   "serverOptions": {
      "logActivity": true
   }
}
```
- To enable it for HTTP Clients, just overwrite any or all service adapter configurations:
```json
"config": {
   "clientOptions": {
      "logActivity": true
   }
}
```

## Configure logback to log my specific package
If you added your own Knot's, Adapters or any other extension to the Knot.x you want to have this information to be logged in your log files.
```xml
<configuration>
    <!-- Your properties is required -->
    
    <!-- Knotx & access logs -->
    <include resource="io/knotx/logging/logback/defaults.xml" />
    <include resource="io/knotx/logging/logback/file-appender.xml" />
    <include resource="io/knotx/logging/logback/access.xml" />
    
    <!-- other logger settings -->
    
    <!-- project specific logger -->
    <logger name="com.example.extension" level="INFO">
      <appender-ref ref="FILE" />
    </logger>
</configuration>
```
- Add `logger` for your package or class, define a level of logs
- Specify `FILE` appender as a logs target.
Your logs will appear in the `knotx.log`

However, you might wanted to log your package logs into a separate file, to not polute `knotx.log`.
- create your own file appender (see `io/knotx/logging/logback/file-appender.xml` as an example) with the name e.g. `MY_FILE`
- bind your logger to `MY_FILE` appender
- set logger to `additivity="false"`, so your logs will go just to your new file. If specified to `true` logs will go to the parent logger into `knotx.log` files too.

```xml
<appender name="MY_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
  <!-- Appender settings -->
</appender>

<!-- project specific logger -->
<logger name="com.example.extension" level="INFO" additivity="false">
  <appender-ref ref="MY_FILE" />
</logger>
```

# Performance

- [What do we measure - KPIs](#what-do-we-measure---kpis)
  - [KPI #1 - 1 second response time (90% line)](#kpi-1---1-second-response-time-90%25-line)
  - [KPI #2 - No error responses](#kpi-2---no-error-responses)
  - [KPI #3 - stability](#kpi-3---stability)
  - [KPI #4 - 1 hour of 1 second response time (90% line)](#kpi-4---1-hour-of-1-second-response-time-90%25-line)
- [Performance infrastructure and setup](#performance-infrastructure-and-setup)
  - [Knot.x instance VM and GC settings](#knotx-instance-vm-and-gc-settings)
- [Tools](#tools)
- [Performance tests](#performance-tests)
  - [Scenarios](#scenarios)
    - [1. One snippet and one service](#1-one-snippet-and-one-service)
      - [Details](#details)
    - [2. One snippet and five services](#2-one-snippet-and-five-services)
      - [Details](#details-1)
    - [3. Five snippets one service each](#3-five-snippets-one-service-each)
      - [Details](#details-2)
    - [4. Heavy template with one snippet one services](#4-heavy-template-with-one-snippet-one-services)
      - [Details](#details-3)
    - [5. Heavy template with 100 snippets and one heavy service](#5-heavy-template-with-100-snippets-and-one-heavy-service)
      - [Details](#details-4)
    - [6. Heavy template with one big snippet and one heavy service](#6-heavy-template-with-one-big-snippet-and-one-heavy-service)
      - [Details](#details-5)
  - [Results](#results)
  - [Observations](#observations)
- [Soak test](#soak-test)
  - [Results](#results-1)
    - [Users/throughput](#usersthroughput)
    - [CPU utilization](#cpu-utilization)
    - [CPU load](#cpu-load)
    - [JVM memory heap space](#jvm-memory-heap-space)
    - [GC collections per second](#gc-collections-per-second)
    - [Network traffic](#network-traffic)
    - [Event bus usage](#event-bus-usage)
  - [Observations](#observations-1)
- [Terminology](#terminology)

## What do we measure - KPIs

### KPI #1 - 1 second response time (90% line)
This KPI will be fulfilled as long as 90% of the responses will be returned within less than 1 second. The measured result is the peak when KPI was met (when 90% requests line exceeded 1 second).
This KPI shows what traffic peaks that the instance can process with the reasonable response time.

### KPI #2 - No error responses
This KPI will be fulfilled as long as there will be no error responses.
This KPI shows what throughput can the instance process without errors.

### KPI #3 - stability
- No memory leaks.
- No errors (logs, responses).
- Infrastructure resources OK.
- Application resources OK.

This KPI shows how much traffic can the instance outlive without crashing.

### KPI #4 - 1 hour of 1 second response time (90% line)
This KPI will be fulfilled as long as 90% of the responses will be returned within less than 1 second for 60 minutes with constant throughput with no error responses and system is stable.
This KPI is measured within 70 min session starting with 10 min rampup.

This KPI shows how does instance behaves with high traffic that lasts for a longer period.

## Performance infrastructure and setup
[[assets/performance-setup.png|alt=Performance infrastructure and setup]]

All servers are running under CentOS 7.

### Knot.x instance VM and GC settings

```
-Xms1024m
-Xmx2048m
-XX:ReservedCodeCacheSize=128m
-XX:+UseBiasedLocking
-XX:BiasedLockingStartupDelay=0
-XX:+UseConcMarkSweepGC
-XX:ParallelGCThreads=2
-XX:ParallelCMSThreads=1
```

## Tools
* JMeter 3.1 with plugins (at AWS) as a Load Generator.
* Zabbix 3.2.1 as an Server Monitoring.
* Influx (Internal Instance at AWS set up on JMeter Master machine) as a Live Test Monitoring database.
* Grafana 3.0.1 as a Live Test Monitoring tool.
* DropWizard metrics monitoring Knot.x module.

## Performance tests
The goal of performance tests is to check the system performance and stability with KPIs defined above.
During our tests, we focus on the following metrics which, when correlated, allow us to come up with number of observations about the system:
- Throughput.
- Response time - 90% Line.
- Quantity of Virtual Users.
- Number of errors (HTTP response codes > 400, Knot.x logs).
- Infrastructure resources (cpu, memory, disk, network).
- Application resources (jmx heap size, garbage collections).

All performance and soak scenarios were executed with default number of instances for each Knot.x Verticle:

| Knot.x module | No. of instances |
| ------------- | ---------------- |
| HttpRepositoryConnector | 1 |
| FragmentSplitter | 1 |
| FragmentAssembler | 1 |
| ServiceKnot | 1 |
| HttpServiceAdapter | 1 |
| HandlebarsKnot | 1 |
| KnotxServer | 1 |

### Scenarios

#### 1. One snippet and one service
The page is quite small, it contains only one dynamic Knot.x snippet that requires 1 data source integration.

##### Details
- page:  [simple-1snippet-1service.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-1snippet-1service.html)
- raw template size: `3.2 Kb`
- service response (json) size: `750 byte`

#### 2. One snippet and five services
The page is still small, it contains only one dynamic Knot.x snippet but it requires 5 different data source integration.

##### Details
- page:  [simple-1snippet-5services.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-1snippet-5services.html)
- raw template size: `4.7 Kb`
- service response (json) size: `750\191\212\279\174 byte`

#### 3. Five snippets one service each
The page is variation of a previous one. It is still small, it contains five dynamic Knot.x snippets and each of those snippets uses separate data source integration.

##### Details
- page:  [simple-5snippets.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-5snippets.html)
- raw template size: `5.3 Kb`
- service response (json) size: `750\191\212\279\174 byte`

#### 4. Heavy template with one snippet one services
The page contains big blocks of content and is heavy. There is no much work for integration here (only one snippet with single data soruce), however the challenge here is to process (split and then assemble) a big chunk of html code.

##### Details
- page:  [simple-big-data.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-big-data.html)
- raw template size: `116 Kb`
- service response (json) size: `234 byte`

#### 5. Heavy template with 100 snippets and one heavy service
The page contains 100 snippets, each of them uses the same service. However, the service response is heavy. The challenge here is to process (split and then assemble) a big chunk of html code with multiple snippets and additionaly pass the payload of service response (heavy json) to templating engine Knot.

##### Details
- page:  [100-small-snippets-1-service-wtih-big-json.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/100-small-snippets-1-service-wtih-big-json.html)
- raw template size: `54 Kb`
- service response (json) size: `111 Kb`

#### 6. Heavy template with one big snippet and one heavy service
This test is variation of previous one. All 100 snippets were merged into one big snippet. The service response is heavy. The challenge here is to process (split and then assemble) a big chunk of html code with one heavy snippet and additionaly pass the payload of service response (heavy json) to templating engine Knot.

##### Details
- page:  [1-big-snippet-1-service-wtih-big-json.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/1-big-snippet-1-service-wtih-big-json.html)
- raw template size: `34 Kb`
- service response (json) size: `111 Kb`

### Results

| Scenario | KPI #1 | KPI #2 | KPI #3 | KPI #4 |
| -------- | ------ | ------ | ------ | ------- |
| [1. One snippet and one service](#1-one-snippet-and-one-service) | <ul><li>Throughput: `2600 req/sec`</li></ul> | <ul><li>Throughput: `4000 req/sec`</li><li>90% line avg: `3.65 s`</li><li>Virtual users: `5200`</li></ul> | OK | <ul><li>Throughput: `1810 req/sec`</li><li>90% line avg: `385 ms`</li><li>avg CPU user time: `77.32%`</li><li>avg CPU system time: `5.26%`</li><li>avg CPU load: `157%`</li></ul> |
| [2. One snippet and five services](#2-one-snippet-and-five-services) | <ul><li>Throughput: `1400 req/sec`</li></ul> | <ul><li>Throughput: `2200 req/sec`</li><li>90% line avg: `3.67 s`</li><li>Virtual users: `3000`</li></ul> | OK | <ul><li>Throughput: `1109 req/sec`</li><li>90% line avg: `343 ms`</li><li>avg CPU user time: `75.16%`</li><li>avg CPU system time: `5.68%`</li><li>avg CPU load: `152%`</li></ul> |
| [3. Five snippets one service each](#3-five-snippets-one-service-each) | <ul><li>Throughput: `1000 req/sec`</li></ul> | <ul><li>Throughput: `2000 req/sec`</li><li>90% line avg: `18.8 s`</li><li>Virtual users: `8000`</li></ul> | OK | <ul><li>Throughput: `850 req/sec`</li><li>90% line avg: `65 ms`</li><li>avg CPU user time: `66.49%`</li><li>avg CPU system time: `5.38%`</li><li>avg CPU load: `122%`</li></ul> |
| [4. Heavy template with one snippet one services](#4-heavy-template-with-one-snippet-one-services) | <ul><li>Throughput: `400 req/sec`</li></ul> | <ul><li>Throughput: `317 req/sec`</li><li>90% line avg: `21.24 s`</li><li>Virtual users: `4600`</li></ul> | OK | <ul><li>Throughput: `190 req/sec`</li><li>90% line avg: `50 ms`</li><li>avg CPU user time: `50.11%`</li><li>avg CPU system time: `1.68%`</li><li>avg CPU load: `62%`</li></ul> |
| [5. Heavy template with 100 snippets and one heavy service](#5-heavy-template-with-100-snippets-and-one-heavy-service) | <ul><li>Throughput: `10 req/sec`</li></ul> | <ul><li>Throughput: `100 req/sec`</li><li>90% line avg: `42 s`</li><li>Virtual users: `112`</li></ul> | Crashed at `422` VU - `OOM`. <br>Heap was full (`2GB` of `2GB` used) at this time. <br>Knot.x didn't recover within 15 min.  | <ul><li>Throughput: `100 req/sec`</li><li>90% line avg: `850 ms`</li><li>avg CPU user time: `52.1%`</li><li>avg CPU system time: `0.2%`</li><li>avg CPU load: `65%`</li></ul> |
| [6. Heavy template with one big snippet and one heavy service](#6-heavy-template-with-one-big-snippet-and-one-heavy-service) | <ul><li>Throughput: `200 req/sec`</ul> | <ul><li>Throughput: `200 req/sec`</li><li>90% line avg: `59.4 s`</li><li>Virtual users: `1100`</li></ul> | Crashed at `1164` VU - `OOM`. <br>Heap was full (`2GB` of `2GB` used) at this time. <br>Knot.x recovered. | <ul><li>Throughput: `125 req/sec`</li><li>90% line avg: `243 ms`</li><li>avg CPU user time: `59.75%`</li><li>avg CPU system time: `0.71%`</li><li>avg CPU load: `65%`</li></ul> |

### Observations
- It is not worth to lead to the situation when Knot.x uses 100% of all CPUs. In that case throughput is up to 40% lower than during the more balanced test, when CPU usage is about 75%.
- In scenarios (5) and (6) the same page is displayed, however using different number of snippets (100 small vs 1 big). The approach with 100 small snippets has better performance characteristics (the reason here is templating engine overload with big snippet to parse).

## Soak test
Motivation for this test is to check how Knot.x instance behaves in realistic scenario. Soak test is planned to last 24 hours and is a
combination of requests to pages from scenarios above in occurrence defined below:
- 40% [simple-1snippet-1service.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-1snippet-1service.html)
- 25% [simple-1snippet-5services.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-1snippet-5services.html)
- 25% [simple-5snippets.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-5snippets.html)
- 5% [simple-big-data.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-big-data.html)
- 5% [1-big-snippet-1-service-wtih-big-json.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/1-big-snippet-1-service-wtih-big-json.html)

Traffic throughput plan (notice that since we have 2 jMeter slaves, the final number of users is counted twice):
[[assets/soak-traffic-plan.png|alt=Soak traffic plan]]

### Results

| Scenario | Results | Remarks |
| -------- | ------ | ------ |
| 24h soak test | <ul><li>Throughput: `168 req/sec`</li><li>Peak: `400 req/sec`</li><li>Average response in: `16 ms`</li><li>90% line avg: `49 ms`</li><li>99% line avg: `89 ms`</li><li>avg CPU load: `21.3 %`</li><li>`1` error within `7.31 milion` requests</li></ul> | Error was a single `404`. |

#### Users/throughput
[[assets/soak-users-throughput.png|alt=Users/throughput]]

#### CPU utilization
[[assets/soak-cpu-utilization.png|alt=CPU utilization]]

#### CPU load
[[assets/soak-cpu-load.png|alt=CPU load]]

#### JVM memory heap space
[[assets/soak-jvm-heap-space.png|alt=JVM memory heap space]]

#### GC collections per second
[[assets/soak-gc-per-second.png|alt=GC collections per second]]

#### Network traffic
[[assets/soak-network-traffic.png|alt=Network traffic]]

#### Event bus usage
[[assets/soak-eb-usage.png|alt=Event bus usage]]

### Observations
- After the highest peak, heap didn't behave in the same way as before the peak. It is bigger and cleared less frequently. However, it looks that there is no influence on performance or throughput.

## Terminology
* [JMeter Glossary](https://jmeter.apache.org/usermanual/glossary.html)
* [Performance Testing Guidance for Web Applications](https://msdn.microsoft.com/en-us/library/bb924356.aspx)
* [Dictionary of Load Testing Terms](http://www.webperformance.com/library/dictionary.html)

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

# FAQ
This section contains answers for frequently asked questions. If you can't find an answer to your question here
please use the [Issues Tool](https://github.com/Cognifide/knotx/issues) to raise a question or contact us via 
[Gitter](https://gitter.im/Knotx/Lobby).

- [1. Setup and first steps](#1-setup-and-first-steps)
  - [1.1. What do I need to setup Knot.x instance?](#11-what-do-i-need-to-setup-knotx-instance)
- [2. Development and best practices](#2-development-and-best-practices)
  - [2.1 How can I inject the data from a web service that responses with JSON into my site using Knot.x?](#21-how-can-i-inject-the-data-from-a-web-service-that-responses-with-json-into-my-site-using-knotx)
  - [2.2 How can I use non web based services to inject the data into my site using Knot.x?](#22-how-can-i-use-non-web-based-services-to-inject-the-data-into-my-site-using-knotx)
  - [2.3 I'd like to keep an option to integrate with services using front-end integration. Does Knot.x support that?](#23-id-like-to-keep-an-option-to-integrate-with-services-using-front-end-integration-does-knotx-support-that)
- [3. Supported and unsupported features](#3-supported-and-unsupported-features)
  - [3.1 Will nested scripts be supported?](#31-will-nested-scripts-be-supported)
  - [3.2 Can I have more than one remote repository defined?](#32-can-i-have-more-than-one-remote-repository-defined)
  - [3.3 Does Knot.x come with an Action Adapter implementation?](#33-does-knotx-come-with-an-action-adapter-implementation)

## 1. Setup and first steps

### 1.1. What do I need to setup Knot.x instance?
To run Knot.x you need only Java 8. Find more information in the [[Getting Started|GettingStarted]] guide.
To build Knot.x you also need Maven (version 3.3.1 or higher).

---

## 2. Development and best practices

### 2.1 How can I inject the data from a web service that responses with JSON into my site using Knot.x?
There is a dedicated tutorial for that case. Please visit the [Hello Rest Service Tutorial](http://knotx.io/blog/hello-rest-service/).

### 2.2 How can I use non web based services to inject the data into my site using Knot.x?
Please see the [Adapt Service without Web API](http://knotx.io/blog/adapt-service-without-webapi/) tutorial.

### 2.3 I'd like to keep an option to integrate with services using front-end integration. Does Knot.x support that?
Knot.x does not define the way your website integrates with external services. Out of the box, Knot.x supports backend
and front-end integration (read more in the [Client-side integration approach with Knot.x](http://knotx.io/blog/client-side-integration-with-knotx/) tutorial).

---

## 3. Supported and unsupported features

### 3.1 Will nested scripts be supported?
No - please refer to [#87](https://github.com/Cognifide/knotx/issues/87) for detailed information.

### 3.2 Can I have more than one remote repository defined?
Yes, you may have more than one instance of `HttpRepositoryConnectorVerticle` defined in your server configuration.
Please see the example configuration in [#342](https://github.com/Cognifide/knotx/issues/342).

### 3.3 Does Knot.x come with an Action Adapter implementation?
We noticed that action adapter implementation will be very specific for each project. Because of that Knot.x does 
not come with an out of the box Action Adapter implementation (not like in case of [[Http Service Adapter|HttpServiceAdapter]]).
You may see an [Example Action Adapter](https://github.com/Cognifide/knotx/tree/master/knotx-example/knotx-example-action-adapter-http)
that is used for demo purposes. This is not a production ready code.

# Upgrade Notes
If you are upgrading Knot.x from the previous version, here are notes that will help you do all necessary config
and snippets changes that were introduced in comparison to previous released Knot.x version. If you are upgrading
from older than one version (e.g. 1.0.1 -> 1.1.2) be sure, to do all the steps from the Upgrade Notes of all released
versions. You may see all changes in the [Changelog](https://github.com/Cognifide/knotx/blob/master/CHANGELOG.md).

## Master
List of changes that are finished but not yet released in any final version.

## Version 1.2.1
- [PR-385](https://github.com/Cognifide/knotx/pull/385) - From now on you can define the custom 
`snippetTagName` and use that tag name to define dynamic snippets in the templates (instead of 
default `<script>`). Read more about that chang in [[Splitter|Splitter]] and [[Assembler|Assembler]] docs.

## Version 1.2.0
- [PR-345](https://github.com/Cognifide/knotx/pull/335) - Update to Vert.x 3.5 and RxJava 2. Most notable changes are upgrade of Vert.x and RxJava.
The API is changed, so your custom implementations need to adopt to the latest APIs both of Vert.x and RxJava.
- [PR-320](https://github.com/Cognifide/knotx/pull/320) - Now you can configure the file-uploads folder for POST requests. See [Server options](https://github.com/Cognifide/knotx/wiki/Server#server-options) for details.
- [PR-347](https://github.com/Cognifide/knotx/pull/320) - You can now add [custom response header](https://github.com/Cognifide/knotx/wiki/Server#server-options) to the Server. Also custom header can be added to [repository](https://github.com/Cognifide/knotx/wiki/HttpRepositoryConnector#options) & [service](https://github.com/Cognifide/knotx/wiki/HttpServiceAdapter#how-to-configure) requests.
- [PR-349](https://github.com/Cognifide/knotx/pull/349) - You can pass [`Delivery Options`](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/DeliveryOptions.html) to Knot.x Verticles e.g. to manipulate eventbus response timeouts. 
For more details see documentation sections in [[Server|Server#vertx-event-bus-delivery-options]], [[Action Knot|ActionKnot#vertx-event-bus-delivery-options]] and [[Service Knot|ServiceKnot#vertx-event-bus-delivery-options]].
- [PR-354](https://github.com/Cognifide/knotx/pull/354) - You can now enable XSRF protection on default or custom flow at any path. See [[Server|Server#how-to-enable-csrf-token-generation-and-validation]] for details.
- [PR-359](https://github.com/Cognifide/knotx/pull/359) - From now on Knot.x responses with the response of a repository in case of response with status code different than 200. 
 You may play with [`displayExceptionDetails`](https://github.com/Cognifide/knotx/wiki/Server#server-options) flag
 in order to receive errors stack traces.
- [PR-369](https://github.com/Cognifide/knotx/pull/369) - Better support for SSL for Repository Connector. Please check the documentation of [[HttpRepositoryConnector|HttpRepositoryConnector#how-to-configure-ssl-connection-to-the-repository]] for details of how to setup SSL connection.
- [PR-372](https://github.com/Cognifide/knotx/pull/372) - Added cache for compiled Handlebars snippets, you may configure it in Handlebars config, see more in [[Handlebars Knot docs|HandlebarsKnot#how-to-configure]].
- [PR-374](https://github.com/Cognifide/knotx/pull/374) - Enable keepAlive connection in http client options. This is important fix and we recommend to update your existing configuration of any http client and enable `keepAlive` option.
- [PR-379](https://github.com/Cognifide/knotx/pull/379) - Added access logging capabilities to the Knotx HTTP Server. Establish standard configuration of logback logger. Check [[Configure Access Log|Server#configure-access-log]] to see how to configure access logs, and [[Logging|Logging]] to see how loggers can be configured to log to console, files for knot.x, access & netty logs.

## Version 1.1.2
- [PR-335](https://github.com/Cognifide/knotx/pull/335) - Added support for HttpServerOptions on the configuration level.
  * **Important**: The biggest change here is the way port of [[Knot.x Server|Server#vertx-http-server-configurations]] is configured. 
  Previously it was defined in the `config.httpPort` property. Now `serverOptions` section was introduced, see  
  [Vert.x DataObjects page](http://vertx.io/docs/vertx-core/dataobjects.html#HttpServerOptions) for more details. 
  See example change of configuration
  [here](https://github.com/Cognifide/knotx/pull/335/files#diff-9eb56f60d7dcc72e56694b1a0aeb014dL5).

## Version 1.1.1
 - [PR-307](https://github.com/Cognifide/knotx/pull/307) - Fixed KnotxServer default configuration
  * Now default configuration of [[Knot.x Server|Server]] will accept all GET requests.

## Version 1.1.0
- [PR-296](https://github.com/Cognifide/knotx/pull/296) - Support for params on markup and config
  * You may now pass additional `queryParams` via `params` configuration in Service Adapter section.
- [PR-299](https://github.com/Cognifide/knotx/pull/299) - Customize request routing outside knots
  * **Important**: structure of [[Knot.x Server|Server]] configuration has changed because [[Gateway Mode|GatewayMode]] was introduced.
    Now, to configure custom `repositories`, `splitter`, `assembler` and `routing` move all those sections to `defaultFlow`.
    See the example change [here](https://github.com/Cognifide/knotx/pull/299/files#diff-d4c26ef67612264e462c7e4a882023cdL38).
    Additionally, new configuration section (which is not mandatory) `customFlow` was introduced. Read more about it 
    in [[Gateway Mode|GatewayMode]] docs.
- [PR-306](https://github.com/Cognifide/knotx/pull/306) - Additional parameters to adapter from template. 
  * You may pass additional parameters to Action Adapters using `data-knotx-adapter-params`. Read more in [[Action Knot|ActionKnot#example]] docs.

## Version 1.0.1
- [PR-290](https://github.com/Cognifide/knotx/pull/290) - allow defining services without default `params` configured
  * You no longer have to define empty `params` value in `ServiceKnot` config if you don't use any.

