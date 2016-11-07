![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

[![][travis img]][travis]
[![][license img]][license]

#Knot.x - reactive multisource assembler 

<p align="center">
  <img src="https://github.com/Cognifide/knotx/blob/master/icons/180x180.png?raw=true"
         alt="Knot.x"/>
</p>


## What is Knot.x?
Very efficient, high-performance and scalable platform which assembles static and dynamic content from multiple sources.

## What's philosophy behind Knot.x?

### Stability and responsiveness
**Knot.x** uses asynchronous programming principles which allows it to process a large number of requests using a single thread.
Asynchronous programming is a style promoting the ability to write non-blocking code (no thread pools).
The platform stays responsive under heavy and varying load and is designed to follow [Reactive Manifesto](http://www.reactivemanifesto.org/) principles.

### Loose coupling
Relies on asynchronous message-passing to establish a boundary between system components that ensures 
loose coupling, isolation and location transparency. Base **Knot.x** component is called [Knot](https://github.com/Cognifide/knotx/wiki/Knot).

We care a lot about speed and that is why we built it on [Vert.x](http://vertx.io/), known as one of the leading frameworks for performant, event-driven applications.

### Scalability
Various scaling options are available to suit client needs and help in cost optimization. Using a 
simple concurrency model and message bus **Knot.x** can be scaled within a single host or cluster of 
servers


## What problems does Knot.x solve?
**Knot.x** joins content from multiple sources on Repository and Data Ingestion levels to render dynamic pages:

<p align="center">
  <img src="https://github.com/Cognifide/knotx/blob/master/documentation/src/main/wiki/assets/knotx-overview.png?raw=true"
         alt="Knot.x overview"/>
</p>

- Thanks to **Knot.x** your system may have several templates (pages) source, all joined in **Knot.x** using [Repository](https://github.com/Cognifide/knotx/wiki/Repository) feature.
- **Knot.x** can render dynamic page that requires data from multiple external sources (e.g. microservices) thanks to [View Knot](https://github.com/Cognifide/knotx/wiki/ViewKnot) feature.
- Thanks to using ultra fast and scalable [Vert.x](http://vertx.io/) engine, **Knot.x** can solve your performance problems. Learn more about [Knot.x Architecture](https://github.com/Cognifide/knotx/wiki/Architecture).
- **Knot.x** supports forms submission including multi-step forms. Find out more about this topic reading about [Action Knot](https://github.com/Cognifide/knotx/wiki/ActionKnot).

## Full Documentation

See our [Wiki](https://github.com/Cognifide/knotx/wiki) for full documentation, examples and other information.


## Communication, bugs and feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/Cognifide/knotx/issues).


## Demo

You can run **Knot.x** demo with less than 5 minutes, you only need Java 8. See [how to run Knot.x demo](https://github.com/Cognifide/knotx/wiki/RunningTheDemo).


## Licence

**Knot.x** is licensed under the [Apache License, Version 2.0 (the "License")](https://www.apache.org/licenses/LICENSE-2.0.txt)


[travis]:https://travis-ci.org/Cognifide/knotx
[travis img]:https://travis-ci.org/Cognifide/knotx.svg?branch=master

[license]:LICENSE
[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg
