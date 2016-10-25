![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

[![][travis img]][travis]
[![][license img]][license]

# Knot.x - reactive template assembler
<p align="center">
  <img src="https://github.com/Cognifide/knotx/blob/master/icons/180x180.png?raw=true"
         alt="Knot.x"/>
</p>

Knot.x is a lightweight and high-performance **reactive template assembler**. It allows you to get rid of all the dynamic data from your content repository and put it into a fast and scalable world of microservices.

![Flow diagram](icons/architecture/flow-diagram.png)

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
11:52:04,513 |-INFO in ch.qos.logback.classic.joran.JoranConfigurator@4f933fd1 - Registering current configuration as safe fallback point

2016-10-19 11:52:06 [vert.x-eventloop-thread-3] INFO  c.c.knotx.mocks.MockServiceVerticle - Starting <MockServiceVerticle>
2016-10-19 11:52:06 [vert.x-eventloop-thread-1] DEBUG c.c.knotx.server.KnotxServerVerticle - Starting <com.cognifide.knotx.server.KnotxServerVerticle>
2016-10-19 11:52:06 [vert.x-eventloop-thread-2] INFO  c.c.k.r.HttpRepositoryVerticle - Registered <HttpRepositoryVerticle>
2016-10-19 11:52:06 [vert.x-eventloop-thread-0] INFO  c.c.k.m.MockRemoteRepositoryVerticle - Starting <MockRemoteRepositoryVerticle>
2016-10-19 11:52:06 [vert.x-eventloop-thread-6] INFO  c.c.k.m.MockServiceAdapterVerticle - Starting <MockServiceAdapterVerticle>
2016-10-19 11:52:06 [vert.x-eventloop-thread-7] INFO  c.c.k.r.FilesystemRepositoryVerticle - Registered <FilesystemRepositoryVerticle>
2016-10-19 11:52:06 [vert.x-eventloop-thread-4] INFO  c.c.k.e.view.impl.TemplateEngine - Registered custom Handlebars helper: bold
2016-10-19 11:52:06 [vert.x-eventloop-thread-4] DEBUG c.c.k.knot.view.ViewKnotVerticle - Starting <com.cognifide.knotx.knot.view.ViewKnotVerticle>
2016-10-19 11:52:07 [vert.x-eventloop-thread-5] DEBUG c.c.k.c.s.h.HttpServiceAdapterVerticle - Registered <HttpServiceAdapterVerticle>
2016-10-19 11:52:07 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2016-10-19 11:52:07 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED 

		Deployed fe2a7ef0-1f91-4ee0-ac7a-8617aedd8f9a [com.cognifide.knotx.mocks.MockServiceAdapterVerticle]
		Deployed 1d92984b-27a2-495d-baa1-50bea688c869 [com.cognifide.knotx.repository.FilesystemRepositoryVerticle]
		Deployed 11776e93-4541-465a-ab05-a0bf472088a6 [com.cognifide.knotx.knot.view.ViewKnotVerticle]
		Deployed 73e3ad21-b121-4d13-8cd4-c17ea868a640 [com.cognifide.knotx.adapter.service.http.HttpServiceAdapterVerticle]
		Deployed b8d9b6e8-2faf-4eb9-a43f-375d15805e5f [com.cognifide.knotx.repository.HttpRepositoryVerticle]
		Deployed 1aa196a0-461c-4c46-913b-2dd85be8503e [com.cognifide.knotx.mocks.MockRemoteRepositoryVerticle]
		Deployed 54083a13-681f-411c-968c-f2efb810b28e [com.cognifide.knotx.mocks.MockServiceVerticle]
		Deployed 4688e02e-8e1b-4858-a3af-d59262b1500c [com.cognifide.knotx.server.KnotxServerVerticle]
```

This example app simulates Vert.x based application running Knot.x core verticles:
 - Knot.x Server
 - Knot.x Repository: File System Repository, Http Repository
 - Knot.x View Engine
 - Knot.x Http Service Adapter
 
Besides Knot.x mock verticles are started:
 - Mock Service  -> simulates services used by View Engine feeding the Handlebars snippets
 - Mock Remote Repository -> simulates HTTP Remote repository serving HTML templates
 - Mock Service Adapter -> simulates real service adapters on event bus

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
Please notice we use [ZenHub](https://www.zenhub.com/) extension to manage issues.

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
