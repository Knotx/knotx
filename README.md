![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

[![][travis img]][travis]
[![][license img]][license]
[![][central-repo img]][central-repo]
[![][gitter img]][gitter]

# Knot.x is a highly efficient and scalable integration platform for modern websites.

<p align="center">
  <img src="https://github.com/Cognifide/knotx/blob/master/icons/180x180.png?raw=true" alt="Knot.x Logo"/>
</p>

## What problems does Knot.x solve?

**Knot.x** uses data from any source (like REST / SOAP service, search engine, CRM etc.) and transforms it into an unified customer experience using a
template from a repository. The template can contain dynamic snippets which determine the way how data is used. The repository can be CMS system, Apache or
simple catalogue with static HTML pages.

<p align="center">
  <img align="right"
    src="https://github.com/Cognifide/knotx/blob/master/documentation/src/main/wiki/assets/knotx-high-level-architecture.png?raw=true"
    alt="High Level Architecture"/>
</p>

## What's the philosophy behind Knot.x?
We care a lot about speed and that is why we built **Knot.x** on [Vert.x](http://vertx.io/), known as one of the leading frameworks for performant, event-driven applications.

### Stability and responsiveness
**Knot.x** uses asynchronous programming principles which allows it to process a large number of requests using a single thread.
Asynchronous programming is a style promoting the ability to write non-blocking code (no thread pools).
The platform stays responsive under heavy and varying load and is designed to follow [Reactive Manifesto](http://www.reactivemanifesto.org/) principles.

### Loose coupling
Relies on asynchronous message-passing to establish a boundary between system components that ensures
loose coupling, isolation and location transparency. Base **Knot.x** component is called [Knot](https://github.com/Cognifide/knotx/wiki/Knot).

<p align="center">
  <img align="right"
    src="https://github.com/Cognifide/knotx/blob/master/documentation/src/main/wiki/assets/knotx-modules-basic-request-flow.png?raw=true"
    alt="Basic request flow"/>
</p>

### Scalability
Various scaling options are available to suit client needs and help in cost optimization. Using a
simple concurrency model and message bus **Knot.x** can be scaled within a single host or cluster of
servers.

## Documentation

See [KNOTX.io](http://knotx.io) for tutorials, examples and user documentation.

See [Wiki](https://github.com/Cognifide/knotx/wiki) for developer documentation, examples and other information.


## Community

#### Online Chat

[Gitter Chat](https://gitter.im/Knotx/Lobby) is a way for users to chat with the Knot.x team. Feel free to leave a message, even if we’re not around, we will definitely respond to you when available.

#### Google Groups

Google Groups are memorable ways to ask questions and communicate with the Knot.x team and other users. There are two groups available:

* [User Group](https://groups.google.com/forum/#!forum/knotx) – for all Knot.x users
* [Developers Group](https://groups.google.com/forum/#!forum/knotx-contributors) – for Knot.x Core team and project contributors

## Bugs

All feature requests and bugs can be filed as issues on [Gitub](https://github.com/Cognifide/knotx/issues). Do not use Github issues to ask questions, post them on the [User Group](https://groups.google.com/forum/#!forum/knotx) or [Gitter Chat](https://gitter.im/Knotx/Lobby).


## Demo

You can run a **Knot.x** demo within less than 5 minutes, all you need is Java 8. See [how you can run the Knot.x demo](https://github.com/Cognifide/knotx/wiki/RunningTheDemo).


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
