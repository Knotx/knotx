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
$ cd knotx-example-monolith
$ java -jar target/knotx-example-monolith-X.Y.Z-SNAPSHOT-fat.jar -conf src/main/resources/knotx-example-monolith.json
```
Where:
- `knotx-example-monolith.json` file is **starter** JSON of Knot.x. This file simply defines what Knot.x services (Verticles) should be started. It's also possible to amend default configuration of Knot.x in this file.

You will see output similar to the following:
```
...
2017-01-13 09:15:05 [vert.x-eventloop-thread-1] DEBUG c.c.knotx.server.KnotxServerVerticle - Starting <com.cognifide.knotx.server.KnotxServerVerticle>
2017-01-13 09:15:05 [vert.x-eventloop-thread-2] INFO  c.c.k.r.HttpRepositoryConnectorVerticle - Registered <HttpRepositoryConnectorVerticle>
2017-01-13 09:15:05 [vert.x-eventloop-thread-3] INFO  c.c.k.r.FilesystemRepositoryConnectorVerticle - Starting <FilesystemRepositoryConnectorVerticle>
2017-01-13 09:15:05 [vert.x-eventloop-thread-4] DEBUG c.c.k.s.FragmentSplitterVerticle - Starting <com.cognifide.knotx.splitter.FragmentSplitterVerticle>
2017-01-13 09:15:05 [vert.x-eventloop-thread-5] DEBUG c.c.k.k.a.FragmentAssemblerVerticle - Starting <com.cognifide.knotx.knot.assembler.FragmentAssemblerVerticle>
2017-01-13 09:15:05 [vert.x-eventloop-thread-6] DEBUG c.c.k.k.t.HandlebarsKnotVerticle - Starting <com.cognifide.knotx.knot.templating.HandlebarsKnotVerticle>
2017-01-13 09:15:05 [vert.x-eventloop-thread-7] DEBUG c.c.k.k.service.ServiceKnotVerticle - Starting <com.cognifide.knotx.knot.service.ServiceKnotVerticle>
2017-01-13 09:15:05 [vert.x-eventloop-thread-3] INFO  c.c.k.m.MockRemoteRepositoryVerticle - Starting <MockRemoteRepositoryVerticle>
2017-01-13 09:15:05 [vert.x-eventloop-thread-4] INFO  c.c.knotx.mocks.MockServiceVerticle - Starting <MockServiceVerticle>
2017-01-13 09:15:05 [vert.x-eventloop-thread-5] INFO  c.c.k.m.MockServiceAdapterVerticle - Starting <MockServiceAdapterVerticle>
2017-01-13 09:15:05 [vert.x-eventloop-thread-0] DEBUG c.c.k.knot.action.ActionKnotVerticle - Starting <com.cognifide.knotx.knot.action.ActionKnotVerticle>
2017-01-13 09:15:05 [vert.x-eventloop-thread-6] INFO  c.c.k.k.t.i.HandlebarsKnotProxyImpl - Registered custom Handlebars helper: bold
2017-01-13 09:15:05 [vert.x-eventloop-thread-6] INFO  c.c.k.m.MockActionAdapterVerticle - Starting <MockActionAdapterVerticle>
2017-01-13 09:15:06 [vert.x-eventloop-thread-2] DEBUG c.c.k.a.a.h.HttpActionAdapterVerticle - Starting <com.cognifide.knotx.adapter.action.http.HttpActionAdapterVerticle>
2017-01-13 09:15:06 [vert.x-eventloop-thread-1] DEBUG c.c.k.a.s.h.HttpServiceAdapterVerticle - Starting <com.cognifide.knotx.adapter.service.http.HttpServiceAdapterVerticle>
2017-01-13 09:15:06 [vert.x-eventloop-thread-4] INFO  c.c.knotx.mocks.MockServiceVerticle - Mock Service server started. Listening on port 3000
2017-01-13 09:15:06 [vert.x-eventloop-thread-3] INFO  c.c.k.m.MockRemoteRepositoryVerticle - Mock Remote Repository server started. Listening on port 3001
2017-01-13 09:15:06 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2017-01-13 09:15:06 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED

                Deployed e66138b0-20ee-4f69-9858-da1d0cc2ba8e [knotx:io.knotx.FilesystemRepositoryConnector]
                Deployed ac6b81d0-a136-40c3-b76c-d2ce04e4f61e [knotx:io.knotx.FragmentSplitter]
                Deployed 8a0cfe43-07f7-426a-bcf5-1a111ec3a29f [knotx:io.knotx.FragmentAssembler]
                Deployed 470d4a2f-b475-4d34-bec5-fcc11d90dd92 [knotx:io.knotx.ServiceKnot]
                Deployed 7d4a4deb-b969-46f8-b12b-0d6ad5c34d96 [knotx:io.knotx.ServiceAdapterMock]
                Deployed 433b3a3b-c7ba-480c-a211-01c589c2301e [knotx:example.io.knotx.ActionKnot]
                Deployed 4bf8a308-1822-49a6-bf81-bd55e827a925 [knotx:io.knotx.HandlebarsKnot]
                Deployed c138ed25-ae6c-4ace-b815-4cb0a056a335 [knotx:io.knotx.ActionAdapterMock]
                Deployed 82a237f3-4296-435b-aa4b-2a4f356969c1 [knotx:io.knotx.HttpRepositoryConnector]
                Deployed ec326504-5c08-42fc-8ef9-ec4a48e83d1a [knotx:io.knotx.HttpActionAdapter]
                Deployed a6a3c5ad-e2ce-4550-b365-3873cbbe9222 [knotx:io.knotx.HttpServiceAdapter]
                Deployed 9937d6e6-9b41-4562-be7c-813959cf94d1 [knotx:io.knotx.ServiceMock]
                Deployed 5f9dff50-4882-4c65-8d34-b1ddc4da6717 [knotx:io.knotx.RemoteRepositoryMock]
                Deployed f4fb44bf-a1ec-469b-961d-dce14a005f9c [knotx:example.io.knotx.KnotxServer]
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
```
- first serves HTML template from Remote Http Repository
- second serves HTML template from local storage
- third one serves HTML template with multiple forms on the page, one is AJAX based - served from local storage
- last one serves HTML template with multiple forms on the page, one is AJAX based - served from remote repository

## Reconfigure demo
You can play with the demo in order to get familiar with the ways how to configure Knot.x based application.

### Use starter JSON
1. Let's start with the starter JSON, the file which defines what Verticles Knot.x is composed of. Let's check the one from our demo application
[knotx-example-monolith.json](https://github.com/Cognifide/knotx/blob/master/knotx-example/knotx-example-monolith/src/main/resources/knotx-example-monolith.json)
2. Copy the file to computer that's running Demo app and make it new name for it, e.g.: `knotx-example-experiments.json`
3. Inside that JSON add new object `config` and configure KnotxServer service (take service name from `services` section), 
but change `httpPort` property only. Let's set it to `9999`.
```json
{
  "modules": [
    "knotx:example.io.knotx.KnotxServer",
    "knotx:io.knotx.HttpRepositoryConnector",
    "knotx:io.knotx.FilesystemRepositoryConnector",
    "knotx:io.knotx.FragmentSplitter",
    "knotx:io.knotx.HandlebarsKnot",
    "knotx:io.knotx.ServiceKnot",
    "knotx:example.io.knotx.ActionKnot",
    "knotx:io.knotx.HttpServiceAdapter",
    "knotx:io.knotx.HttpActionAdapter",
    "knotx:io.knotx.RemoteRepositoryMock",
    "knotx:io.knotx.ServiceMock",
    "knotx:io.knotx.ServiceAdapterMock",
    "knotx:io.knotx.ActionAdapterMock"
  ],
  "config": {
    "knotx:example.io.knotx.KnotxServer": {
      "options": {
        "config": {
          "httpPort": 9999
        }
      }
    }
  }
}
```
4. Start Knot.x with your new configuration. Notice in the console, the HTTP Server is now listening on port 9999
```
...
2017-01-03 12:25:31 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 9999
2017-01-03 12:25:31 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED
...
```

### Use JVM properties
Knot.x can be also reconfigured using JVM properties. With this method, you can set simple values through JVM properties, or provide JSON with the more complex object you want to use to override configuration.
The syntax of the property is as follows:
`-D<service-name>.<json-obj-path>=<value>`
Where:
- `<service-name>` is the name of the Knot.x service without `knotx:` prefix, e.g.: io.knotx.ServiceKnot, etc.
- `<json-obj-path>` is simply a **dot** delimited path in the Knot.x service configuration. E.g. `options.config.httpPort`
- `<value>` can be simply a value to be set on JSON property, or `file:/path/to/file.json`. Latter type of value, is the json file with JSON Object, that should be used to merge with the object pointed by `<json-obj-path>`.
  
E.g.`-Dexample.io.knotx.KnotxServer.options.config.httpPort=7777`
Or,`-Dexample.io.knotx.KnotxServer.options.config=file:test.json`

Let's modify `httpPort` once again, but this time using JVM property.
1. Restart Knot.x with your previous config, but this time start java with additional command line option:
```
$ java -Dexample.io.knotx.KnotxServer.options.config.httpPort=7777 -jar target/knotx-example-monolith-X.Y.Z-SNAPSHOT-fat.jar -conf src/main/resources/knotx-example-experiments.json
```
2. Notice that HTTP Server is listening on port **7777** now, so starter JSON configuration is overridden.
```
...
2017-01-03 12:35:31 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 7777
2017-01-03 12:35:31 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED
...
```
3. Now, create new file on your computer, e.g. `server-options.json` which will have JSON object that should be merged with `options` object of the KnotxServer service.
In this case, the object will specify how many instances to start. (This can be supplied by simple value instead of JSON file, but for demonstration purposes let do it as below)
```json
{
  "instances": 2
}
```
4. Start Knot.x once again, but this time with new JVM property
```
$ java -Dexample.io.knotx.KnotxServer.options=file:server-options.json -Dexample.io.knotx.KnotxServer.options.config.httpPort=7777 -jar target/knotx-example-monolith-X.Y.Z-SNAPSHOT-fat.jar -conf src/main/resources/knotx-example-experiments.json
```
5. Notice that Knot.x is started on port **7777* but two instances of KnotxServer where started.
```
...
2017-01-03 12:35:31 [vert.x-eventloop-thread-1] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 7777
2017-01-03 12:35:31 [vert.x-eventloop-thread-2] INFO  c.c.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 7777
2017-01-03 12:35:31 [vert.x-eventloop-thread-0] INFO  c.c.k.launcher.KnotxStarterVerticle - Knot.x STARTED
...
```

### Conclusions
- You can configure Knot.x using starter JSON by providing properties that should be added, or modified.
- You can configure Knot.x using JVM properties
- On JVM property, you can specify service and path to the property in service configuration and the value to be set on it
- On JVM property, instead of the simple value, you can provide JSON file that will be put into the service JSON at appropriate property
- JVM properties have highest priority
