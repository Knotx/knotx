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
$ java -jar target/knotx-example-app-X.Y.Z-SNAPSHOT-fat.jar -conf src/main/resources/knotx-example-app.json
```
Where:
- `knotx-example-app.json` file is **starter** JSON of Knot.x. This file simply defines what Knot.x services (Verticles) should be started. It's also possible to amend default configuration of Knot.x in this file.

You will see output similar to the following:
```
...
2018-01-15 23:45:40 [vert.x-eventloop-thread-5] INFO  io.knotx.mocks.MockServiceVerticle - Starting <MockServiceVerticle>
2018-01-15 23:45:40 [vert.x-eventloop-thread-4] INFO  i.k.m.MockRemoteRepositoryVerticle - Starting <MockRemoteRepositoryVerticle>
2018-01-15 23:45:40 [vert.x-eventloop-thread-6] INFO  io.knotx.gateway.GatewayKnotVerticle - Starting <GatewayKnotVerticle>
2018-01-15 23:45:40 [vert.x-eventloop-thread-0] INFO  i.k.knot.service.ServiceKnotVerticle - Starting <ServiceKnotVerticle>
2018-01-15 23:45:40 [vert.x-eventloop-thread-3] INFO  i.k.r.HttpRepositoryConnectorVerticle - Starting <HttpRepositoryConnectorVerticle>
2018-01-15 23:45:40 [vert.x-eventloop-thread-0] INFO  i.k.g.ResponseProviderKnotVerticle - Starting <ResponseProviderKnotVerticle>
2018-01-15 23:45:40 [vert.x-eventloop-thread-2] INFO  io.knotx.server.KnotxServerVerticle - Starting <KnotxServerVerticle>
2018-01-15 23:45:40 [vert.x-eventloop-thread-7] INFO  i.k.k.t.impl.HandlebarsKnotProxyImpl - Registered custom Handlebars helper: bold
2018-01-15 23:45:40 [vert.x-eventloop-thread-7] INFO  i.k.g.RequestProcessorKnotVerticle - Starting <RequestProcessorKnotVerticle>
2018-01-15 23:45:40 [vert.x-eventloop-thread-3] INFO  i.k.a.a.h.HttpActionAdapterVerticle - Starting <HttpActionAdapterVerticle>
2018-01-15 23:45:40 [vert.x-eventloop-thread-2] INFO  i.k.a.s.h.HttpServiceAdapterVerticle - Starting <HttpServiceAdapterVerticle>
2018-01-15 23:45:40 [vert.x-eventloop-thread-1] INFO  i.k.knot.action.ActionKnotVerticle - Starting <ActionKnotVerticle>
2018-01-15 23:45:40 [vert.x-eventloop-thread-4] INFO  i.k.m.MockRemoteRepositoryVerticle - Mock Remote Repository server started. Listening on port 3001
2018-01-15 23:45:40 [vert.x-eventloop-thread-5] INFO  io.knotx.mocks.MockServiceVerticle - Mock Service server started. Listening on port 3000
2018-01-15 23:45:40 [vert.x-eventloop-thread-1] INFO  io.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2018-01-15 23:45:40 [vert.x-eventloop-thread-0] INFO  i.k.launcher.KnotxStarterVerticle - Knot.x STARTED

		Deployed splitter [java:io.knotx.splitter.FragmentSplitterVerticle] [66517b40-e562-4f30-a579-58a7ec9ce727]
		Deployed assembler [java:io.knotx.assembler.FragmentAssemblerVerticle] [18050737-c066-493a-bc68-e67e170ae8ab]
		Deployed fsRepo [java:io.knotx.repository.fs.FilesystemRepositoryConnectorVerticle] [31c6974e-6b76-4953-8073-14a37b9d24c5]
		Deployed gatewatKnot [java:io.knotx.gateway.GatewayKnotVerticle] [50ce760b-cb7c-4598-95f6-d41976b7730b]
		Deployed serviceKnot [java:io.knotx.knot.service.ServiceKnotVerticle] [8b62c8d0-4168-4deb-aa8d-e486f208cc7c]
		Deployed respProviderKnot [java:io.knotx.gateway.ResponseProviderKnotVerticle] [3a02f6c4-4c6e-4c04-b8a4-9f21c74f620c]
		Deployed hbsKnot [java:io.knotx.knot.templating.HandlebarsKnotVerticle] [ea703d28-7025-49a8-8953-45283271413c]
		Deployed reqProcessorKnot [java:io.knotx.gateway.RequestProcessorKnotVerticle] [c4e50af8-5a3c-4a11-abde-f2bf1b8edc08]
		Deployed httpRepo [java:io.knotx.repository.http.HttpRepositoryConnectorVerticle] [b9027423-38f0-4448-ae3d-12e7cea61116]
		Deployed actionAdapter [java:io.knotx.adapter.action.http.HttpActionAdapterVerticle] [51db1515-4349-43d7-ab2b-78d50d56cf06]
		Deployed serviceAdapter [java:io.knotx.adapter.service.http.HttpServiceAdapterVerticle] [b0bcf7a4-fbdd-45d1-ac90-71879479d198]
		Deployed actionKnot [java:io.knotx.knot.action.ActionKnotVerticle] [222a95b0-30d5-480c-a183-ced823e48b3c]
		Deployed mockRepo [java:io.knotx.mocks.MockRemoteRepositoryVerticle] [eabbd720-d179-4824-a8df-e68097aeb73f]
		Deployed mockService [java:io.knotx.mocks.MockServiceVerticle] [d3982da1-aebc-48de-8ecc-3f696bd509e2]
		Deployed server [java:io.knotx.server.KnotxServerVerticle] [41970d7d-bc2a-4d12-86a5-76e19ca507bc]
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

## Reconfigure demo
You can play with the demo in order to get familiar with the ways how to configure Knot.x based application.

### Use starter JSON
1. Let's start with the starter JSON, the file which defines what Verticles Knot.x is composed of. Let's check the one from our demo application
[knotx-example-app.json](https://github.com/Cognifide/knotx/blob/master/knotx-example/knotx-example-app/src/main/resources/knotx-example-app.json)
2. Copy the file to computer that's running Demo app and make it new name for it, e.g.: `knotx-example-experiments.json`
3. Inside that JSON, locate `config.server.options` object.
4. Put the `serverOptions` object with port set to `9999` as in the example below.
```json
{
  "modules": [
    "server=io.knotx.server.KnotxServerVerticle",
    "httpRepo=io.knotx.repository.http.HttpRepositoryConnectorVerticle",
    "fsRepo=io.knotx.repository.fs.FilesystemRepositoryConnectorVerticle",
    "splitter=io.knotx.splitter.FragmentSplitterVerticle",
    "assembler=io.knotx.assembler.FragmentAssemblerVerticle",
    "hbsKnot=io.knotx.knot.templating.HandlebarsKnotVerticle",
    "serviceKnot=io.knotx.knot.service.ServiceKnotVerticle",
    "actionKnot=io.knotx.knot.action.ActionKnotVerticle",
    "serviceAdapter=io.knotx.adapter.service.http.HttpServiceAdapterVerticle",
    "actionAdapter=io.knotx.adapter.action.http.HttpActionAdapterVerticle",
    "mockRepo=io.knotx.mocks.MockRemoteRepositoryVerticle",
    "mockService=io.knotx.mocks.MockServiceVerticle",
    "gatewatKnot=io.knotx.gateway.GatewayKnotVerticle",
    "reqProcessorKnot=io.knotx.gateway.RequestProcessorKnotVerticle",
    "respProviderKnot=io.knotx.gateway.ResponseProviderKnotVerticle"
  ],
  "config": {
    "server": {
      "options": {
        "config": {
          "serverOptions": {
             "port": 9999
          },
          "defaultFlow": {
            ....
          },
          .....
        }
      }
    }
  }
}
```
5. Start Knot.x with your new configuration. Notice in the console, the HTTP Server is now listening on port 9999
```
...
2017-01-03 12:25:31 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 9999
2017-01-03 12:25:31 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED
...
````

