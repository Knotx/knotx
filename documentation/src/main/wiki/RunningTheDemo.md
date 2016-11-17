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
2016-11-07 14:05:42 [vert.x-eventloop-thread-2] INFO  c.c.k.r.HttpRepositoryVerticle - Registered <HttpRepositoryVerticle>
2016-11-07 14:05:42 [vert.x-eventloop-thread-3] DEBUG c.c.k.k.a.AuthorizationKnotVerticle - Starting <com.cognifide.knotx.knot.authorization.AuthorizationKnotVerticle>
2016-11-07 14:05:42 [vert.x-eventloop-thread-4] DEBUG c.c.k.s.FragmentSplitterVerticle - Starting <com.cognifide.knotx.splitter.FragmentSplitterVerticle>
2016-11-07 14:05:42 [vert.x-eventloop-thread-1] DEBUG c.c.knotx.server.KnotxServerVerticle - Starting <com.cognifide.knotx.server.KnotxServerVerticle>
2016-11-07 14:05:42 [vert.x-eventloop-thread-4] INFO  c.c.k.r.FilesystemRepositoryVerticle - Registered <FilesystemRepositoryVerticle>
2016-11-07 14:05:42 [vert.x-eventloop-thread-3] INFO  c.c.k.m.MockServiceAdapterVerticle - Starting <MockServiceAdapterVerticle>
2016-11-07 14:05:42 [vert.x-eventloop-thread-6] DEBUG c.c.k.knot.action.ActionKnotVerticle - Starting <com.cognifide.knotx.knot.action.ActionKnotVerticle>
2016-11-07 14:05:42 [vert.x-eventloop-thread-5] INFO  c.c.k.knot.view.impl.TemplateEngine - Registered custom Handlebars helper: bold
2016-11-07 14:05:42 [vert.x-eventloop-thread-5] DEBUG c.c.knotx.knot.view.ViewKnotVerticle - Starting <com.cognifide.knotx.knot.view.ServiceKnotVerticle>
2016-11-07 14:05:42 [vert.x-eventloop-thread-5] INFO  c.c.k.m.MockRemoteRepositoryVerticle - Starting <MockRemoteRepositoryVerticle>
2016-11-07 14:05:43 [vert.x-eventloop-thread-0] DEBUG c.c.k.adapter.api.AbstractAdapter - Registered <HttpActionAdapterVerticle>
2016-11-07 14:05:43 [vert.x-eventloop-thread-7] DEBUG c.c.k.adapter.api.AbstractAdapter - Registered <HttpServiceAdapterVerticle>
2016-11-07 14:05:43 [vert.x-eventloop-thread-2] INFO  c.c.knotx.mocks.MockServiceVerticle - Starting <MockServiceVerticle>
2016-11-07 14:05:43 [vert.x-eventloop-thread-1] INFO  c.c.k.m.MockActionAdapterVerticle - Starting <MockActionAdapterVerticle>
2016-11-07 14:05:43 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2016-11-07 14:05:43 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED 

		Deployed 2e13527f-cb27-43bd-9af6-92f7dc980269 [com.cognifide.knotx.splitter.FragmentSplitterVerticle]
		Deployed b02ff03a-b88a-4363-a660-93aeea2e264b [com.cognifide.knotx.knot.authorization.AuthorizationKnotVerticle]
		Deployed 32f23829-942d-49e1-88a7-c0111a29bed2 [com.cognifide.knotx.repository.FilesystemRepositoryVerticle]
		Deployed d0d5e85b-eedb-4d1e-b916-8bc25d9f824c [com.cognifide.knotx.mocks.MockServiceAdapterVerticle]
		Deployed 077e72f6-2729-416d-9088-1b2fbab24ab9 [com.cognifide.knotx.knot.action.ActionKnotVerticle]
		Deployed e97ec584-fe35-43dd-b701-6fe29e2081d1 [com.cognifide.knotx.knot.view.ServiceKnotVerticle]
		Deployed 7ae8c292-1c50-4894-b5fa-fb17f099af5b [com.cognifide.knotx.repository.HttpRepositoryVerticle]
		Deployed a9249d7d-daad-4f4d-b253-7ebbe70525b3 [com.cognifide.knotx.adapter.service.http.HttpServiceAdapterVerticle]
		Deployed 08133c37-8d6e-4b38-a43a-a56df9823137 [com.cognifide.knotx.adapter.action.http.HttpActionAdapterVerticle]
		Deployed bd5eb9b3-8f44-4f89-9cdb-4ee3ac1eef3a [com.cognifide.knotx.mocks.MockRemoteRepositoryVerticle]
		Deployed 6a15f5a6-ff89-4925-ab4d-fe29252a5aaa [com.cognifide.knotx.mocks.MockServiceVerticle]
		Deployed 4f272e4a-0ffd-4f87-8bed-7ddc2ca11742 [com.cognifide.knotx.mocks.MockActionAdapterVerticle]
		Deployed 00085144-1f91-4311-b4d0-550bcae8923e [com.cognifide.knotx.server.KnotxServerVerticle]
```

This example app simulates Vert.x based application running Knot.x core verticles:
 - [[Server|Server]],
 - [[Repositories|Repository]]: File System Repository, Http Repository,
 - [[Splitter|Splitter]],
 - [[Action Knot|ActionKnot]],
 - [[View Knot|ViewKnot]],
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
