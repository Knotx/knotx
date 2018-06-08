![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

[![][travis img]][travis]
[![][license img]][license]
[![][central-repo img]][central-repo]
[![][gitter img]][gitter]

# Knot.x Core
This is the repository for Knot.x core.

<p align="center">
  <img src="https://github.com/Cognifide/knotx/blob/master/icons/180x180.png?raw=true" alt="Knot.x Logo"/>
</p>
<p align="center">http://knotx.io</p>
<p align="center">
  reactive <i>Vert.x-based</i>  integration platform
</p>

Knot.x core is a reactive integration framework allowing to connect with all kinds of systems from CMS,
through web services, to low-level storages like databases or caches and it's not limited to HTTP.

We build Knot.x on top of [Vert.x](http://vertx.io/), known as one of the leading frameworks for performant,
event-driven applications. It uses asynchronous programming principles which allows it to process a
large number of requests using a single thread. Asynchronous programming is a style promoting the
ability to write non-blocking code (no thread pools). The platform stays responsive under heavy and
varying load and is designed to follow [Reactive Manifesto](http://www.reactivemanifesto.org/) principles.

It exposes [Netty-based](https://netty.io/) HTTP [server](https://github.com/Cognifide/knotx/wiki/Server)
which collects data from any source (like REST / SOAP service, search engine, CRM etc.) and transforms
it into an unified customer experience using a template from a
[repository](https://github.com/Cognifide/knotx/wiki/RepositoryConnectors). The template can contain
dynamic [fragments](https://github.com/Cognifide/knotx/wiki/Splitter) which determine the way how
the data is used. The repository can be CMS system, Apache or simple catalogue from the filesystem with static HTML pages.

For more information on Knot.x and where Knot.x core fits into the big picture please see the [KNOTX.IO](http://knotx.io).

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
