![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

[![][travis img]][travis]
[![][license img]][license]
[![][central-repo img]][central-repo]
[![][gitter img]][gitter]

# Knot.x Core
This is the repository for Knot.x Core.

<p align="center">
  <img src="https://github.com/Cognifide/knotx/blob/master/icons/180x180.png?raw=true" alt="Knot.x Logo"/>
</p>
<p align="center">http://knotx.io</p>
<p align="center">
  reactive <i>Vert.x-based</i>  integration platform
</p>

## Overview

Knot.x Core is a contract-driven, microservice-oriented, reactive web platform that is build on the 
top of [Vert.x](http://vertx.io). It transforms **unopinionated** Vert.x toolkit into customizable 
and scalable tool with integration web patterns (like 
[Knot.x Data Bridge](https://github.com/Knotx/knotx-data-bridge),
[Knot.x Forms](https://github.com/Knotx/knotx-forms)) build-in. It starts a [Netty-based](https://netty.io/) 
HTTP [server](https://github.com/Cognifide/knotx/wiki/Server) that processes all incoming requests 
in configurable way. 

We build Knot.x on top of [Vert.x](http://vertx.io/), known as one of the leading frameworks for performant,
event-driven applications. It uses asynchronous programming principles which allows it to process a
large number of requests using a single thread. Asynchronous programming is a style promoting the
ability to write non-blocking code (no thread pools). The platform stays responsive under heavy and
varying load and is designed to follow [Reactive Manifesto](http://www.reactivemanifesto.org/) principles.

### Microservices
With [OpenAPI Specification (OAS)](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md) 
support, you can define your RESTful APIs using standardized and language-agnostic approach. Your API 
definition and its capabilities can be easily discovered and understood without access to source code or
documentation. With the design first approach, your API consumer can interact with the remote service 
with a minimal amount of implementation logic (all you need is to implement a simple 
[`callback`](https://vertx.io/docs/apidocs/io/vertx/core/Handler.html) function, no Vert.x knowledge 
is required). Knot.x combines [Vert.x-Web](https://vertx.io/docs/vertx-web/java/), 
[Vert.x-Web API Contract](https://vertx.io/docs/vertx-web-api-contract/java/) and the 
[Java ServiceLoader](https://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html) to make a
reactive micro-service implementation even simpler.  

### Integration layer
Knot.x allows to connect various systems like CMS, CRM and other in controlled and isolated way, 
preventing any undesired interferences. It introduces an integration layer concept where Knot.x is an
entry point that accepts all incoming requests, collects data from any datasource (like REST / SOAP 
service, search engine, CRM etc.) and transforms it into an unified customer experience using a template from a
[repository](https://github.com/Cognifide/knotx/wiki/RepositoryConnectors). The template can contain
dynamic [fragments](https://github.com/Cognifide/knotx/wiki/Splitter) which determine the way how
the data is used. All those steps require a minimal amount of implementation logic. 

## Getting started
We recommend to use [Knot.x Stack](https://github.com/Knotx/knotx-stack) to setup Knot.x with all
required dependencies. All required steps are described in this [tutorial](http://knotx.io/blog/getting-started-wiht-knotx-stack/).
More details about Knot.x deployment can be found [here](https://github.com/Cognifide/knotx/wiki/KnotxDeployment).

You can also play with Docker to setup [an example project](https://github.com/Knotx/knotx-stack/tree/master/knotx-docker).

## Documentation

See [KNOTX.io](http://knotx.io/tutorials) for tutorials, examples and user documentation.

See [Wiki](https://github.com/Cognifide/knotx/wiki) for developer
documentation, examples and other information.

See [Knot.x Extensions Github](https://github.com/Knotx) for Stack, the
example project, cookbook, Data Bridge and other Knot.x extensions.

## Community / Issues

All feature requests and bugs can be filed as issues on [Gitub](https://github.com/Cognifide/knotx/issues).
Do not use Github issues to ask questions, post them on the
[User Group](https://groups.google.com/forum/#!forum/knotx) or [Gitter Chat](https://gitter.im/Knotx/Lobby).


## Licence

**Knot.x** is licensed under the [Apache License, Version 2.0 (the "License")](https://www.apache.org/licenses/LICENSE-2.0.txt)


[travis]:https://travis-ci.org/Cognifide/knotx
[travis img]:https://travis-ci.org/Cognifide/knotx.svg?branch=master

[license]:https://github.com/Cognifide/knotx/blob/master/LICENSE
[license img]:https://img.shields.io/badge/License-Apache%202.0-blue.svg

[central-repo]:http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.knotx%22
[central-repo img]:https://img.shields.io/maven-central/v/io.knotx/knotx-root.svg?label=Maven%20Central

[gitter]:https://gitter.im/Knotx/Lobby
[gitter img]:https://badges.gitter.im/Knotx/knotx-extensions.svg
