# Running Knot.x Demo

## Requirements

To run Knot.x you only need Java 8.
To build it you also need Maven (version 3.3.1 or higher).

## Running the demo
To run an [Example monolith app](https://github.com/Cognifide/knotx/blob/master/knotx-example/knotx-example-monolith) do the following:

Build Knot.x with Example app:

```
$ git clone https://github.com/Cognifide/knotx.git
$ cd knotx
$ mvn clean install
```

or download [released](https://github.com/Cognifide/knotx/releases) application `knotx-example-monolith-X.Y.Z-fat.jar` and configuration file `knotx-example-monolith.json`.

Run example app:
```
$ cd knotx-example/knotx-example-monolith
$ java -jar target/knotx-example-monolith-X.Y.Z-SNAPSHOT-fat.jar -conf src/main/resources/knotx-example-monolith.json
```

You will see output similar to the following:
```
...
2016-11-20 15:22:18 [vert.x-eventloop-thread-2] INFO  c.c.k.r.HttpRepositoryVerticle - Registered <HttpRepositoryVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-3] DEBUG c.c.knotx.knot.api.AbstractKnot - Starting <com.cognifide.knotx.knot.service.ServiceKnotVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-5] DEBUG c.c.knotx.knot.api.AbstractKnot - Starting <com.cognifide.knotx.knot.authorization.AuthorizationKnotVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-6] DEBUG c.c.k.s.FragmentSplitterVerticle - Starting <com.cognifide.knotx.splitter.FragmentSplitterVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-1] DEBUG c.c.knotx.server.KnotxServerVerticle - Starting <com.cognifide.knotx.server.KnotxServerVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-7] DEBUG c.c.knotx.knot.api.AbstractKnot - Starting <com.cognifide.knotx.knot.action.ActionKnotVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-3] INFO  c.c.knotx.mocks.MockServiceVerticle - Starting <MockServiceVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-5] INFO  c.c.k.r.FilesystemRepositoryVerticle - Registered <FilesystemRepositoryVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-6] INFO  c.c.k.m.MockRemoteRepositoryVerticle - Starting <MockRemoteRepositoryVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-4] INFO  c.c.k.k.t.HandlebarsKnotVerticle - Registered custom Handlebars helper: bold
2016-11-20 15:22:18 [vert.x-eventloop-thread-4] DEBUG c.c.knotx.knot.api.AbstractKnot - Starting <com.cognifide.knotx.knot.templating.HandlebarsKnotVerticle>
2016-11-20 15:22:18 [vert.x-eventloop-thread-4] INFO  c.c.k.m.MockServiceAdapterVerticle - Starting <MockServiceAdapterVerticle>
2016-11-20 15:22:19 [vert.x-eventloop-thread-0] DEBUG c.c.k.adapter.api.AbstractAdapter - Registered <HttpServiceAdapterVerticle>
2016-11-20 15:22:19 [vert.x-eventloop-thread-2] INFO  c.c.k.m.MockActionAdapterVerticle - Starting <MockActionAdapterVerticle>
2016-11-20 15:22:19 [vert.x-eventloop-thread-1] DEBUG c.c.k.adapter.api.AbstractAdapter - Registered <HttpActionAdapterVerticle>
2016-11-20 15:22:19 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2016-11-20 15:22:19 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED 

		Deployed 126460c4-a1ce-4edb-8a27-f7bcc46a8c6d [com.cognifide.knotx.knot.authorization.AuthorizationKnotVerticle]
		Deployed d15b83dd-963b-40fb-967b-05f999cd5175 [com.cognifide.knotx.splitter.FragmentSplitterVerticle]
		Deployed 6cff9e78-979b-423d-97d9-bdf9c26209bc [com.cognifide.knotx.knot.service.ServiceKnotVerticle]
		Deployed 4ec1beb0-e231-4c55-bf3e-dca9a940f1ef [com.cognifide.knotx.knot.action.ActionKnotVerticle]
		Deployed 3b0e9f44-9e89-48b8-8b8e-ba17a376e987 [com.cognifide.knotx.repository.FilesystemRepositoryConnectorVerticle]
		Deployed e96f425c-4fdc-4415-be92-c65e759e8476 [com.cognifide.knotx.knot.templating.HandlebarsKnotVerticle]
		Deployed 9a3ea907-d513-4d22-b846-8395d6afe5e9 [com.cognifide.knotx.mocks.MockServiceAdapterVerticle]
		Deployed 828a05f5-3ace-4d54-89bd-f5efcaaceb33 [com.cognifide.knotx.repository.HttpRepositoryConnectorVerticle]
		Deployed e34358f2-48da-47de-b42c-d91cfc2ef2b7 [com.cognifide.knotx.mocks.MockActionAdapterVerticle]
		Deployed 3bcd0b2b-2d3f-41e0-87bf-983f3a3cb630 [com.cognifide.knotx.adapter.service.http.HttpServiceAdapterVerticle]
		Deployed 5d3e7cce-1ceb-49b9-b901-8f5fc4b5a64c [com.cognifide.knotx.mocks.MockRemoteRepositoryVerticle]
		Deployed 649b6ed0-baa7-4bc4-94b7-e71cbb44fed0 [com.cognifide.knotx.mocks.MockServiceVerticle]
		Deployed 2bd2ea06-aa9d-4afe-ba5f-776f93ebda77 [com.cognifide.knotx.adapter.action.http.HttpActionAdapterVerticle]
		Deployed 6dccd9b6-c887-4f59-a50d-8675f52dbe17 [com.cognifide.knotx.server.KnotxServerVerticle]

```

This example app simulates Vert.x based application running Knot.x core verticles:
 - [[Server|Server]],
 - [[Repositories|Repository]]: File System Repository, Http Repository,
 - [[Splitter|Splitter]],
 - [[Action Knot|ActionKnot]],
 - [[Service Knot|ServiceKnot]],
 - [[Handlebars Knot|HandlebarsKnot]],
 - [[Http Service Adapter|HttpServiceAdapter]], 
 - example Action Adapter,
 - example Authorization Knot.
 
Besides Knot.x, mock verticles are started:
 - Mock Service  -> simulates services used by View Engine feeding the Handlebars snippets
 - Mock Remote Repository -> simulates HTTP Remote repository serving HTML templates
 - Mock Service Adapter -> simulates real service adapters on event bus

With default configuration, Knot.x starts on port `8092`. You can access example Knot.x application from the following URLs:
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
