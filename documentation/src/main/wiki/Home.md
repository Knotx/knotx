# Knot.x

<p align="center">
  <img src="https://github.com/Cognifide/knotx/blob/master/icons/180x180.png?raw=true" alt="Knot.x Logo"/>
</p>

**Knot.x** is a highly-efficient and scalable integration platform for modern websites developed in Java on top of [Vert.x](http://vertx.io/) framework.

## What problems does Knot.x solve?

**Knot.x** uses data from any source (like REST / SOAP service, search engine, CRM etc.) and transforms it into an unified customer experience using a 
template from a repository. The template can contain dynamic snippets which determine the way how data is used. The repository can be CMS system, Apache or 
simple catalogue with static HTML pages. 

[[assets/knotx-high-level-architecture.png|alt=High Level Architecture]]

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


## Community

####Online Chat

[Gitter Chat](https://gitter.im/Knotx/Lobby) is a way for users to chat with the Knot.x team. Feel free to leave a message, even if we’re not around, we will definitely respond to you when available.

####Google Groups

Google Groups are memorable ways to ask questions and communicate with the Knot.x team and other users. There are two groups available:

* [User Group](https://groups.google.com/forum/#!forum/knotx) – for all Knot.x users
* [Developers Group](https://groups.google.com/forum/#!forum/knotx-contributors) – for Knot.x Core team and project contributors


## Licence

**Knot.x** is licensed under the [Apache License, Version 2.0 (the "License")](https://www.apache.org/licenses/LICENSE-2.0.txt)
