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
- **Knot.x** can assemble dynamic page that requires data from multiple external sources (e.g. microservices) thanks to [[View Knot|ViewKnot]] feature.
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
