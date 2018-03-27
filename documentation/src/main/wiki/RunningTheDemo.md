# Running Knot.x Demo

## Requirements

To run Knot.x you only need Java 8.
To build it you also need Maven (version 3.3.1 or higher).

## Running the demo
To run an [Example app](https://github.com/Cognifide/knotx/blob/master/knotx-example/knotx-example-app) do the following:

Build Knot.x with Example app:

```
$ git clone https://github.com/Cognifide/knotx.git
$ cd knotx
$ mvn clean install
```

or download [released](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-example-app) application `knotx-example-app-X.Y.Z.fat.jar` and configuration file `knotx-example-app-X.Y.Z.json`.

Run example app:
```
$ cd knotx-example/knotx-example-app
$ java -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory -jar target\knotx-example-app-1.3.0-SNAPSHOT-fat.jar -conf config\bootstrap.json
```
Where:
- `bootstrap.json` file is **starter** JSON of Knot.x. This file simply configures a set of configuration stores (one of them defines what Knot.x services (Verticles) should be started - see confg/application.conf). More details can be found [[here|KnotxDeployment]].

You will see output similar to the following:
```
...
21:44:12.395 [vert.x-eventloop-thread-1] INFO io.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
21:44:12.396 [vert.x-eventloop-thread-4] INFO io.knotx.mocks.MockServiceVerticle - Mock Service server started. Listening on port 3000
21:44:12.396 [vert.x-eventloop-thread-3] INFO io.knotx.mocks.MockRemoteRepositoryVerticle - Mock Remote Repository server started. Listening on port 3001
21:44:12.401 [vert.x-eventloop-thread-0] INFO io.knotx.launcher.KnotxStarterVerticle - Knot.x STARTED
                Deployed fsRepo=java:io.knotx.repository.fs.FilesystemRepositoryConnectorVerticle [9d71c2bb-4002-42cd-8802-2b3c9bd532f9]
                Deployed splitter=java:io.knotx.splitter.FragmentSplitterVerticle [892f4917-78af-432e-addb-2a0568fc7fda]
                Deployed assembler=java:io.knotx.assembler.FragmentAssemblerVerticle [6a763216-026d-4bd2-9e36-eba709cf613c]
                Deployed serviceKnot=java:io.knotx.knot.service.ServiceKnotVerticle [c7e2b548-8d07-4d0a-ab0d-067b37718716]
                Deployed actionKnot=java:io.knotx.knot.action.ActionKnotVerticle [45844a04-62fa-402e-98c9-a60eab3292c2]
                Deployed gatewayKnot=java:io.knotx.gateway.GatewayKnotVerticle [4434727b-98c1-45ce-85f7-66cedbd68585]
                Deployed respProviderKnot=java:io.knotx.gateway.ResponseProviderKnotVerticle [e010f86f-c955-4173-83a7-086d7119cf55]
                Deployed hbsKnot=java:io.knotx.knot.templating.HandlebarsKnotVerticle [12536445-00ef-411b-85b7-140941317fb6]
                Deployed reqProcessorKnot=java:io.knotx.gateway.RequestProcessorKnotVerticle [712c3cd2-ddf7-4ba9-8f69-059a1f92bdf1]
                Deployed httpRepo=java:io.knotx.repository.http.HttpRepositoryConnectorVerticle [1fed46f4-904b-4f9b-8db2-705877c62cda]
                Deployed actionAdapter=java:io.knotx.adapter.action.http.HttpActionAdapterVerticle [737e3c08-5466-4714-9396-a6e683b22d5e]
                Deployed serviceAdapter=java:io.knotx.adapter.service.http.HttpServiceAdapterVerticle [418fc76d-ee75-4042-bcf2-205ba12912ac]
                Deployed server=java:io.knotx.server.KnotxServerVerticle [07fb1307-f627-4e00-9af1-6dbfc7393cf2]
                Deployed serviceMock=java:io.knotx.mocks.MockServiceVerticle [6f29f354-ffc1-49d4-8b32-383a1bf36f77]
                Deployed repoMock=java:io.knotx.mocks.MockRemoteRepositoryVerticle [9a5237eb-5798-4703-994d-fcab81bd0bcc]

21:44:12.401 [vert.x-eventloop-thread-7] INFO io.vertx.core.impl.launcher.commands.VertxIsolatedDeployer - Succeeded in deploying verticle
```

This example app simulates Vert.x based application running Knot.x core verticles:
 - [[Server|Server]],
 - [[Repository Connectors|RepositoryConnectors]]: **File System** and **Http** Repository connectors,
 - [[Splitter|Splitter]],
 - [[Action Knot|ActionKnot]],
 - [[Service Knot|ServiceKnot]],
 - [[Handlebars Knot|HandlebarsKnot]],
 - [[Assembler|Assembler]],
 - [[Http Service Adapter|HttpServiceAdapter]], 
 - example Action Adapter,
 
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
http://localhost:8092/customFlow/remote/simple.json
```
- first serves HTML template from Remote Http Repository
- second serves HTML template from local storage
- third one serves HTML template with multiple forms on the page, one is AJAX based - served from local storage
- fourth one serves HTML template with multiple forms on the page, one is AJAX based - served from remote repository
- last one serves a JSON message using the [[Gateway Mode|GatewayMode]]
