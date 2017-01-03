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
2017-01-03 12:25:31 [vert.x-eventloop-thread-1] DEBUG c.c.knotx.server.KnotxServerVerticle - Starting <com.cognifide.knotx.server.KnotxServerVerticle>
2017-01-03 12:25:31 [vert.x-eventloop-thread-2] INFO  c.c.k.r.HttpRepositoryConnectorVerticle - Registered <HttpRepositoryConnectorVerticle>
2017-01-03 12:25:31 [vert.x-eventloop-thread-3] INFO  c.c.k.r.FilesystemRepositoryConnectorVerticle - Registered <FilesystemRepositoryConnectorVerticle>
2017-01-03 12:25:31 [vert.x-eventloop-thread-4] DEBUG c.c.k.s.FragmentSplitterVerticle - Starting <com.cognifide.knotx.splitter.FragmentSplitterVerticle>
2017-01-03 12:25:31 [vert.x-eventloop-thread-7] INFO  c.c.knotx.knot.api.AbstractKnot - Starting <com.cognifide.knotx.knot.action.ActionKnotVerticle>
2017-01-03 12:25:31 [vert.x-eventloop-thread-6] INFO  c.c.knotx.knot.api.AbstractKnot - Starting <com.cognifide.knotx.knot.service.ServiceKnotVerticle>
2017-01-03 12:25:31 [vert.x-eventloop-thread-3] INFO  c.c.knotx.mocks.MockServiceVerticle - Starting <MockServiceVerticle>
2017-01-03 12:25:31 [vert.x-eventloop-thread-4] INFO  c.c.k.m.MockServiceAdapterVerticle - Starting <MockServiceAdapterVerticle>
2017-01-03 12:25:31 [vert.x-eventloop-thread-5] INFO  c.c.k.k.t.HandlebarsKnotVerticle - Registered custom Handlebars helper: bold
2017-01-03 12:25:31 [vert.x-eventloop-thread-5] INFO  c.c.knotx.knot.api.AbstractKnot - Starting <com.cognifide.knotx.knot.templating.HandlebarsKnotVerticle>
2017-01-03 12:25:31 [vert.x-eventloop-thread-5] INFO  c.c.k.m.MockActionAdapterVerticle - Starting <MockActionAdapterVerticle>
2017-01-03 12:25:31 [vert.x-eventloop-thread-0] DEBUG c.c.k.adapter.api.AbstractAdapter - Registered <HttpServiceAdapterVerticle>
2017-01-03 12:25:31 [vert.x-eventloop-thread-2] INFO  c.c.k.m.MockRemoteRepositoryVerticle - Starting <MockRemoteRepositoryVerticle>
2017-01-03 12:25:31 [vert.x-eventloop-thread-1] DEBUG c.c.k.adapter.api.AbstractAdapter - Registered <HttpActionAdapterVerticle>
2017-01-03 12:25:31 [vert.x-eventloop-thread-3] INFO  c.c.knotx.mocks.MockServiceVerticle - Mock Service server started. Listening on port 3000
2017-01-03 12:25:31 [vert.x-eventloop-thread-2] INFO  c.c.k.m.MockRemoteRepositoryVerticle - Mock Remote Repository server started. Listening on port 3001
2017-01-03 12:25:31 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2017-01-03 12:25:31 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED

                Deployed 2c037987-b4ad-4609-9080-f51e09609564 [knot:io.knotx.FilesystemRepositoryConnector]
                Deployed cf16eae3-ebd9-451f-9795-e07ba38ecf82 [knot:io.knotx.FragmentSplitter]
                Deployed 6ad50647-f6da-46d7-9522-ada87fa964c8 [knot:example.io.knotx.ActionKnot]
                Deployed 889de10e-701f-4a67-872a-85a2a98d5c4e [knot:example.io.knotx.ServiceKnot]
                Deployed fa9e552b-7ed2-423c-94f7-be2f7ed22340 [knot:io.knotx.ServiceAdapterMock]
                Deployed 1b32ea8c-0c16-410e-97b3-347613890db8 [knot:io.knotx.HandlebarsKnot]
                Deployed 6adcb862-057c-4f68-886c-35ca6a5b509f [knot:io.knotx.ActionAdapterMock]
                Deployed a3e1617e-61b0-4862-b49e-3b94063af755 [knot:io.knotx.HttpRepositoryConnector]
                Deployed 7b564688-d1eb-48a1-8e14-0d437630e9d7 [knot:example.io.knotx.HttpServiceAdapter]
                Deployed 6b460934-3914-4b28-8685-45ee3af11cf6 [knot:example.io.knotx.HttpActionAdapter]
                Deployed 2a6d2769-2dad-4dc2-a5d4-ac23dbdeb781 [knot:io.knotx.ServiceMock]
                Deployed e7c0d7f3-9f67-49bb-8eac-a381550050b0 [knot:example.io.knotx.KnotxServer]
                Deployed 94c4ddac-656b-487e-b471-a0c7844593f7 [knot:io.knotx.RemoteRepositoryMock]
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
