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
