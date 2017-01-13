![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

[![][travis img]][travis]
[![][license img]][license]

#Knot.x - efficient, high-performance and scalable integration platform for modern websites   

<p align="center">
  <img src="https://github.com/Cognifide/knotx/blob/master/icons/180x180.png?raw=true" alt="Knot.x Logo"/>
</p>


## What is Knot.x?
Let's imagine **an online banking website** containing different features like a *chat box*, *exchange rates*, 
*stock data* and *user profile information*. The site has a high performance characteristic, despite of 
its complexity and *target publishing channels*.

All those features come from different providers/vendors having their own teams working in various modes, 
technologies and release cycles.

Knot.x connects all of them in a controlled and isolated way, preventing any undesired interferences.
It combines **asynchronous programming principles** and **message-driven architecture** providing **a scalable 
platform** for modern sites.

**Knot.x** connects all of the above **in a unified customer experience**.


## What problems does Knot.x solve?

###Features

<img align="right" 
  src="https://github.com/Cognifide/knotx/blob/master/documentation/src/main/wiki/assets/knotx-intro-features.png?raw=true"
  alt="Features"/>

Probably you have many features / services you want to connect to your site. They come from 
different vendors, talk using various protocols (REST / SOAP / binary) and have different 
reliability characteristic.
**Knot.x** assembles your static HTML pages with any features / services in a very 
performant manner. It loads and analyses static page from Repository, collects dynamic 
features from multiple sources asynchronously and injects them into the page.
If service you connect to has unpredicted or cyclic outages you can easily handle them according
your business rules. 

Find out more about this topic reading [Service Knot](https://github.com/Cognifide/knotx/wiki/ServiceKnot) 
section.

###Forms

<img align="left" 
  src="https://github.com/Cognifide/knotx/blob/master/documentation/src/main/wiki/assets/knotx-intro-forms.png?raw=true"
  alt="Forms"/>

Every site contains more or less complicated forms. **Knot.x** supports simple and multi-step forms. 
It handles submission errors, form validations and redirects to success pages. 

Transition flow mechanism allows to define multi-step forms with graph characteristic. Every step 
can define various next steps according to current submission state / site visitor choices.

Find out more about this topic reading [Action Knot](https://github.com/Cognifide/knotx/wiki/ActionKnot) 
section.

###Prototyping
Your potential client asked you to prepare a demo presenting a new site capabilities. The client
is connected with financial sector so your site needs to connect to exchange rates and stock data 
features. Those features are not public available so you have only sample data.
**Knot.x** gives you very simple Mocks mechanism. This allows to expose your sample data directly to
pages. Additionally your demo pages can be easily switched to life services without any 
development. Your client will be impressed how quick and fast it can be.

Find out more about this topic reading [Knot.x Demo](https://github.com/Cognifide/knotx/wiki/RunningTheDemo) 
section.

###Extensions
You need to implement custom authentication mechanism for your site and then integrate with service 
talking with its own custom protocol. **Knot.x** is fully modular platform with very flexible extension
points: [Knots](https://github.com/Cognifide/knotx/wiki/Knot) and [Adapters](https://github.com/Cognifide/knotx/wiki/Adapter).
Those extension points communicates with Knot.x Core using very performant Event Bus so you can
implement your integration layer in one place inside Knot.x. Not enough? If you wish you can implement
your extensions in language you like thanks [Vert.x](http://vertx.io/) capabilities.


## What's philosophy behind Knot.x?
We care a lot about speed and that is why we built **Knot.x** on [Vert.x](http://vertx.io/), known as one of the leading frameworks for performant, event-driven applications.

### Stability and responsiveness
**Knot.x** uses asynchronous programming principles which allows it to process a large number of requests using a single thread.
Asynchronous programming is a style promoting the ability to write non-blocking code (no thread pools).
The platform stays responsive under heavy and varying load and is designed to follow [Reactive Manifesto](http://www.reactivemanifesto.org/) principles.

### Loose coupling
Relies on asynchronous message-passing to establish a boundary between system components that ensures 
loose coupling, isolation and location transparency. Base **Knot.x** component is called [Knot](https://github.com/Cognifide/knotx/wiki/Knot).

### Scalability
Various scaling options are available to suit client needs and help in cost optimization. Using a 
simple concurrency model and message bus **Knot.x** can be scaled within a single host or cluster of 
servers.

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
