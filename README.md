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
08:54:52,604 |-INFO in ch.qos.logback.classic.joran.JoranConfigurator@6767c1fc - Registering current configuration as safe fallback point

2016-10-19 08:54:54 [vert.x-eventloop-thread-2] INFO  c.c.k.r.HttpRepositoryVerticle - Registered <HttpRepositoryVerticle>
2016-10-19 08:54:54 [vert.x-eventloop-thread-1] DEBUG c.c.knotx.server.KnotxServerVerticle - Starting <com.cognifide.knotx.server.KnotxServerVerticle>
2016-10-19 08:54:54 [vert.x-eventloop-thread-3] INFO  c.c.knotx.mocks.MockServiceVerticle - Starting <MockServiceVerticle>
2016-10-19 08:54:55 [vert.x-eventloop-thread-0] INFO  c.c.k.m.MockRemoteRepositoryVerticle - Starting <MockRemoteRepositoryVerticle>
2016-10-19 08:54:55 [vert.x-eventloop-thread-7] INFO  c.c.k.r.FilesystemRepositoryVerticle - Registered <FilesystemRepositoryVerticle>
2016-10-19 08:54:55 [vert.x-eventloop-thread-6] INFO  c.c.k.m.MockServiceAdapterVerticle - Starting <MockServiceAdapterVerticle>
2016-10-19 08:54:55 [vert.x-eventloop-thread-4] INFO  c.c.k.viewengine.impl.TemplateEngine - Registered custom Handlebars helper: bold
2016-10-19 08:54:55 [vert.x-eventloop-thread-4] DEBUG c.c.k.viewengine.ViewEngineVerticle - Starting <com.cognifide.knotx.viewengine.ViewEngineVerticle>
2016-10-19 08:54:55 [vert.x-eventloop-thread-5] DEBUG c.c.k.c.s.h.HttpServiceAdapterVerticle - Registered <HttpServiceAdapterVerticle>
2016-10-19 08:54:56 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2016-10-19 08:54:56 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED 

		Deployed 35bba51f-305b-4197-aeb6-f5e21175a4ec [com.cognifide.knotx.mocks.MockServiceAdapterVerticle]
		Deployed 2267a2cd-6d69-476e-b97b-53a9114576fc [com.cognifide.knotx.repository.FilesystemRepositoryVerticle]
		Deployed 7f9b3792-360d-4992-b78f-f8e262e2a2c3 [com.cognifide.knotx.viewengine.ViewEngineVerticle]
		Deployed c194a0b2-c702-4af6-a4bd-593ea662b5c9 [com.cognifide.knotx.core.serviceadapter.http.HttpServiceAdapterVerticle]
		Deployed ac12212f-5cd4-4ad5-8c6b-3f823189d3b8 [com.cognifide.knotx.repository.HttpRepositoryVerticle]
		Deployed c987cce6-e6d8-4a7b-aaf9-2c91d0d9b0ca [com.cognifide.knotx.mocks.MockRemoteRepositoryVerticle]
		Deployed 1ee64be5-744a-44c8-b5a7-03f10a34452e [com.cognifide.knotx.mocks.MockServiceVerticle]
		Deployed 5077ba0f-e8bd-4bc0-8e5a-49f2fd22b5f4 [com.cognifide.knotx.server.KnotxServerVerticle]

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