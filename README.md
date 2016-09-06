![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)
![Knot.x](https://github.com/Cognifide/knotx/blob/master/icons/180x180.png?raw=true)

# Knot.x - reactive microservice assembler
[![][travis img]][travis]
[![][license img]][license]

Knot.x is a lightweight and high-performance **reactive microservice assembler**. It allows you to get rid of all the dynamic data from your content repository and put it into a fast and scalable world of microservices.

We care a lot about speed and that is why we built it on [Vert.x](http://vertx.io/), known as one of the leading frameworks for performant, event-driven applications.

## Full Documentation

See the [Wiki](https://github.com/Cognifide/knotx/wiki) for full documentation, examples and other information.

## Communication
- [GitHub Issues](https://github.com/Cognifide/knotx/issues)

## Requirements

To run Knot.x you only need Java 8.

To build it you also need Maven (version 3.3.1 or higher).

## Run Demo

To run a [Example monolith app](https://github.com/Cognifide/knotx/blob/master/knotx-example/knotx-example-monolith) do the following:

Build Knot.x with Example app:

```
$ git clone https://github.com/Cognifide/knotx.git
$ cd knotx
$ mvn clean install
```

Run example app:
```
$ cd knotx-example/knotx-example-monolith
$ java -jar target/knotx-example-monolith-X.Y.Z-SNAPSHOT-fat.jar -conf src/main/resources/knotx-example-monolith.json
```

You will see output similar to the following:
```
...
16:16:07,030 |-INFO in ch.qos.logback.classic.joran.JoranConfigurator@737996a0 - Registering current configuration as safe fallback point

2016-08-30 16:16:07 [vert.x-eventloop-thread-1] DEBUG c.c.knotx.mocks.MockServiceVerticle - Registered <MockServiceVerticle>
2016-08-30 16:16:07 [vert.x-eventloop-thread-0] DEBUG c.c.k.m.MockRemoteRepositoryVerticle - Registered <MockRemoteRepositoryVerticle>
2016-08-30 16:16:07 [vert.x-eventloop-thread-2] DEBUG c.c.k.repository.RepositoryVerticle - Registered <RepositoryVerticle>
2016-08-30 16:16:07 [vert.x-eventloop-thread-3] DEBUG c.c.k.engine.TemplateEngineVerticle - Registered <TemplateEngineVerticle>
2016-08-30 16:16:08 [vert.x-eventloop-thread-0] DEBUG c.c.knotx.server.KnotxServerVerticle - Registered <KnotxServerVerticle>
2016-08-30 16:16:08 [vert.x-eventloop-thread-0] INFO  c.c.knotx.server.KnotxServerVerticle - Successfully Started

```

This example app simulates Vert.x based application running Knot.x core verticles:
 - Knot.x Server
 - Knot.x Repository
 - Knot.x Template Engine
 
Besides Knot.x two mock verticles are started:
 - Mock Service  -> simulates services used by Template Engine feeding the Handlebars snippets
 - Mock Remote Repository -> simulates HTTP Remote repository serving HTML templates

You can access example Knot.x application from the following URLs
```
http://localhost:8092/content/remote/simple.html
http://localhost:8092/content/local/simple.html
http://localhost:8092/content/local/multiple-forms.html
http://localhost:8092/content/remote/multiple-forms.html
```
- first serves HTML template from Remote Http Repository
- second serves HTML template from local storage
- third one serves HTML template with multiple forms on the page, one is AJAX based - served from local storage
- last one serves HTML template with multiple forms on the page, one is AJAX based - served from remote repository

## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/Cognifide/knotx/issues).

# Licence

**Knot.x** is licensed under the [Apache License, Version 2.0 (the "License")](https://www.apache.org/licenses/LICENSE-2.0.txt)

# Dependencies

- io.vertx
- io.reactivex
- org.jsoup
- com.github.jknack.handlebars
- com.google.code.gson
- guava
- commons-lang3
- junit


[travis]:https://travis-ci.org/Cognifide/knotx
[travis img]:https://travis-ci.org/Cognifide/knotx.svg?branch=master

[license]:LICENSE
[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg