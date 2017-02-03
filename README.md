![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

[![][travis img]][travis]
[![][sonarqube img]][sonarqube]
[![][license img]][license]
[![][gitter img]][gitter]

#Knot.x is a highly-efficient and scalable integration platform for modern websites.

<p align="center">
  <img src="https://github.com/Cognifide/knotx/blob/master/icons/180x180.png?raw=true" alt="Knot.x Logo"/>
</p>


## What is Knot.x?
Let's imagine **an online banking website** containing different features like a *chat box*, *exchange rates*,
*stock data* and *user profile information*. The site has a high performance characteristic, despite
its complexity and *target publishing channels*.

All those features come from different providers/vendors having their own teams working in various modes,
technologies and release cycles.

**Knot.x** connects all of them in a controlled and isolated way, preventing any undesired interferences.
It combines **asynchronous programming principles** and **message-driven architecture** providing **a scalable
platform** for modern sites.

**Knot.x** connects all of the above **into a unified customer experience**.

## What problems does Knot.x solve?

###Features

<img align="right"
  src="https://github.com/Cognifide/knotx/blob/master/documentation/src/main/wiki/assets/knotx-intro-features.png?raw=true"
  alt="Knot.x Features"/>

You may have many features / services that you want to connect to your site. They come from
different vendors, communicate using various network protocols (HTTP/WebSocket/TCP etc.) and message
formats (SOAP/JSON/XML/binary etc.).

**Knot.x** assembles all your features / services into HTML pages in a very
efficient manner. It loads and analyses static pages from Repository, collects dynamic
features from multiple sources asynchronously and injects them into the page.
If a service you connect to has unpredictable or cyclic outages you can easily handle them according
to your business rules.

Read the [Service Knot](https://github.com/Cognifide/knotx/wiki/ServiceKnot)
section to find out more about this topic.

###Forms

<img align="left"
  src="https://github.com/Cognifide/knotx/blob/master/documentation/src/main/wiki/assets/knotx-intro-forms.png?raw=true"
  alt="Knot.x Forms"/>

Every site contains more or less complicated forms. **Knot.x** supports simple and multi-step forms.
It handles submission errors, form validations and redirects to success pages.

Forms can be used to compose multi-step workflows. **Knot.x** allows you to define a graph of interconnected steps, responding to user input or site visitor choices.

Find out more about this topic by reading the [Action Knot](https://github.com/Cognifide/knotx/wiki/ActionKnot)
section of the **Knot.x** documentation.

###Prototyping

<img align="right"
  src="https://github.com/Cognifide/knotx/blob/master/documentation/src/main/wiki/assets/knotx-intro-prototyping.png?raw=true"
  alt="Knot.x Prototyping"/>

A potential client asks you to prepare a demo presenting capabilities of a new site. The client
operates in the financial sector so your site needs to connect to exchange rates and stock data
services. Those features are not publicly available so you only have some sample data to work with.

**Knot.x** gives you the ability to use simple Mocks. This allows you to expose your sample data directly to
pages. Additionally your demo pages can be easily changed to use live services without any further
development work. Your client will be impressed with how fast it can happen.

Find out more about this topic by reading the [Mocks](https://github.com/Cognifide/knotx/wiki/Mocks)
section of this documentation.

###Extensions

<img align="left"
  src="https://github.com/Cognifide/knotx/blob/master/documentation/src/main/wiki/assets/knotx-intro-extensions.png?raw=true"
  alt="Knot.x Extensions"/>

You need to implement custom authentication mechanism for your site and then integrate with service which 
uses its own custom protocol. **Knot.x** is a fully modular platform with very flexible extension
points that we call [Knots](https://github.com/Cognifide/knotx/wiki/Knot) and [Adapters](https://github.com/Cognifide/knotx/wiki/Adapter).

Those extension points communicate with Knot.x Core using a efficient Event Bus so you can
implement your integration layer in one place inside **Knot.x**. Not enough? If you wish, you can implement
your extensions in any language you like, as long as it's supported by [Vert.x](http://vertx.io/).

Find out more about this topic by reading the [Knots](https://github.com/Cognifide/knotx/wiki/Knot) and [Adapters](https://github.com/Cognifide/knotx/wiki/Adapter)
sections of this documentation.


## Full Documentation

See our [Wiki](https://github.com/Cognifide/knotx/wiki) for full documentation, examples and other information.


## Community

####Online Chat

[Gitter Chat](https://gitter.im/Knotx/Lobby) is a way for users to chat with the Knot.x team. Feel free to leave a message, even if we’re not around, we will definitely respond to you when available.

####Google Groups

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

[sonarqube]:https://sonarqube.com/dashboard/index/io.knotx:knotx-root
[sonarqube img]:https://sonarqube.com/api/badges/gate?key=io.knotx:knotx-root

[gitter]:https://gitter.im/Knotx/Lobby
[gitter img]:https://badges.gitter.im/Knotx/knotx-extensions.svg
