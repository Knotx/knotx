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
2016-12-21 15:14:23 [vert.x-eventloop-thread-2] INFO  c.c.k.r.FilesystemRepositoryConnectorVerticle - Registered <FilesystemRepositoryConnectorVerticle>
2016-12-21 15:14:23 [vert.x-eventloop-thread-3] DEBUG c.c.knotx.knot.api.AbstractKnot - Starting <com.cognifide.knotx.knot.service.ServiceKnotVerticle>
2016-12-21 15:14:23 [vert.x-eventloop-thread-5] DEBUG c.c.k.s.FragmentSplitterVerticle - Starting <com.cognifide.knotx.splitter.FragmentSplitterVerticle>
2016-12-21 15:14:23 [vert.x-eventloop-thread-1] DEBUG c.c.knotx.server.KnotxServerVerticle - Starting <com.cognifide.knotx.server.KnotxServerVerticle>
2016-12-21 15:14:23 [vert.x-eventloop-thread-6] DEBUG c.c.knotx.knot.api.AbstractKnot - Starting <com.cognifide.knotx.knot.action.ActionKnotVerticle>
2016-12-21 15:14:23 [vert.x-eventloop-thread-2] INFO  c.c.knotx.mocks.MockServiceVerticle - Starting <MockServiceVerticle>
2016-12-21 15:14:23 [vert.x-eventloop-thread-3] INFO  c.c.k.m.MockServiceAdapterVerticle - Starting <MockServiceAdapterVerticle>
2016-12-21 15:14:23 [vert.x-eventloop-thread-5] INFO  c.c.k.m.MockRemoteRepositoryVerticle - Starting <MockRemoteRepositoryVerticle>
2016-12-21 15:14:23 [vert.x-eventloop-thread-4] INFO  c.c.k.k.t.HandlebarsKnotVerticle - Registered custom Handlebars helper: bold
2016-12-21 15:14:23 [vert.x-eventloop-thread-4] DEBUG c.c.knotx.knot.api.AbstractKnot - Starting <com.cognifide.knotx.knot.templating.HandlebarsKnotVerticle>
2016-12-21 15:14:23 [vert.x-eventloop-thread-4] INFO  c.c.k.r.HttpRepositoryConnectorVerticle - Registered <HttpRepositoryConnectorVerticle>
2016-12-21 15:14:23 [vert.x-eventloop-thread-0] DEBUG c.c.k.adapter.api.AbstractAdapter - Registered <HttpActionAdapterVerticle>
2016-12-21 15:14:23 [vert.x-eventloop-thread-7] DEBUG c.c.k.adapter.api.AbstractAdapter - Registered <HttpServiceAdapterVerticle>
2016-12-21 15:14:24 [vert.x-eventloop-thread-1] INFO  c.c.k.m.MockActionAdapterVerticle - Starting <MockActionAdapterVerticle>
2016-12-21 15:14:24 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2016-12-21 15:14:24 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED 

		Deployed 0e136ce8-4d54-4288-bbc1-40a19b2a3fac [com.cognifide.knotx.splitter.FragmentSplitterVerticle]
		Deployed 7ffc7c76-06ed-4561-8c80-f8cd3bd8b34d [com.cognifide.knotx.knot.service.ServiceKnotVerticle]
		Deployed 305e9c5d-6fb0-4d94-ace7-61ce18ca94bb [com.cognifide.knotx.repository.FilesystemRepositoryConnectorVerticle]
		Deployed 69ad36e8-ff6a-4086-b075-46818e23c221 [com.cognifide.knotx.knot.action.ActionKnotVerticle]
		Deployed 6bfa2358-1666-499d-bc12-a8a093737b43 [com.cognifide.knotx.mocks.MockServiceAdapterVerticle]
		Deployed 5da35f2a-a55e-4867-9559-3229172cfcfd [com.cognifide.knotx.knot.templating.HandlebarsKnotVerticle]
		Deployed 2fc35934-3a74-4738-a95e-d36723b37864 [com.cognifide.knotx.repository.HttpRepositoryConnectorVerticle]
		Deployed 8eee1b27-b7c7-4359-ad0f-99327a98cbc4 [com.cognifide.knotx.adapter.service.http.HttpServiceAdapterVerticle]
		Deployed 41e46bd3-bea8-4117-9161-a707625c10c3 [com.cognifide.knotx.adapter.action.http.HttpActionAdapterVerticle]
		Deployed ddf77771-e83f-4a48-b68c-2f13cf820d40 [com.cognifide.knotx.mocks.MockRemoteRepositoryVerticle]
		Deployed 98c5d75d-557b-42ee-bc60-edb33ca9148b [com.cognifide.knotx.mocks.MockServiceVerticle]
		Deployed dce03c9f-a630-43af-842a-e6d0e5729e76 [com.cognifide.knotx.mocks.MockActionAdapterVerticle]
		Deployed 7d5d4a11-4f2b-40a4-8189-1f14d3f10ac7 [com.cognifide.knotx.server.KnotxServerVerticle]

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
