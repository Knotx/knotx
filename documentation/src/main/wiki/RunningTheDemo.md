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
2017-01-18 09:18:40 [vert.x-eventloop-thread-1] INFO  io.knotx.server.KnotxServerVerticle - Starting <io.knotx.server.KnotxServerVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-2] INFO  i.k.r.HttpRepositoryConnectorVerticle - Starting <HttpRepositoryConnectorVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-3] INFO  i.k.r.FilesystemRepositoryConnectorVerticle - Starting <FilesystemRepositoryConnectorVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-0] INFO  i.k.knot.action.ActionKnotVerticle - Starting <io.knotx.knot.action.ActionKnotVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-4] INFO  i.k.s.FragmentSplitterVerticle - Starting <io.knotx.splitter.FragmentSplitterVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-5] INFO  i.k.k.a.FragmentAssemblerVerticle - Starting <io.knotx.knot.assembler.FragmentAssemblerVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-6] INFO  i.k.k.t.HandlebarsKnotVerticle - Starting <io.knotx.knot.templating.HandlebarsKnotVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-7] INFO  i.k.knot.service.ServiceKnotVerticle - Starting <io.knotx.knot.service.ServiceKnotVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-4] INFO  io.knotx.mocks.MockServiceVerticle - Starting <MockServiceVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-3] INFO  i.k.m.MockRemoteRepositoryVerticle - Starting <MockRemoteRepositoryVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-5] INFO  i.k.mocks.MockServiceAdapterVerticle - Starting <MockServiceAdapterVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-6] INFO  i.k.k.t.impl.HandlebarsKnotProxyImpl - Registered custom Handlebars helper: bold
2017-01-18 09:18:40 [vert.x-eventloop-thread-6] INFO  i.k.mocks.MockActionAdapterVerticle - Starting <MockActionAdapterVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-2] INFO  i.k.a.a.h.HttpActionAdapterVerticle - Starting <io.knotx.adapter.action.http.HttpActionAdapterVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-1] INFO  i.k.a.s.h.HttpServiceAdapterVerticle - Starting <io.knotx.adapter.service.http.HttpServiceAdapterVerticle>
2017-01-18 09:18:40 [vert.x-eventloop-thread-3] INFO  i.k.m.MockRemoteRepositoryVerticle - Mock Remote Repository server started. Listening on port 3001
2017-01-18 09:18:40 [vert.x-eventloop-thread-1] INFO  io.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2017-01-18 09:18:40 [vert.x-eventloop-thread-4] INFO  io.knotx.mocks.MockServiceVerticle - Mock Service server started. Listening on port 3000
2017-01-18 09:18:40 [vert.x-eventloop-thread-0] INFO  i.k.launcher.KnotxStarterVerticle - Knot.x STARTED

                Deployed bf28d297-87e1-40de-9328-d03a24428370 [knotx:io.knotx.FragmentSplitter]
                Deployed a86383e3-1f15-475f-a8e1-14cbc515d295 [knotx:io.knotx.ServiceKnot]
                Deployed 2ba723be-f68f-4636-95a2-1794ed5cc9c5 [knotx:io.knotx.FilesystemRepositoryConnector]
                Deployed df9f9967-8211-419a-8558-51b2aa378020 [knotx:io.knotx.FragmentAssembler]
                Deployed 0ea49334-888c-4f1a-9892-f2caab816b45 [knotx:io.knotx.ServiceAdapterMock]
                Deployed 4922cd4c-b5d1-4b61-adb1-4fd9f61e8867 [knotx:example.io.knotx.ActionKnot]
                Deployed 269bdf78-4701-4fde-8f8a-8479df7cbde6 [knotx:io.knotx.HandlebarsKnot]
                Deployed 623f93cd-656a-4dc4-87b4-fe3b665ea629 [knotx:io.knotx.ActionAdapterMock]
                Deployed 4ca0b3c7-01b2-4fbc-aea7-28adc7cc0d9c [knotx:io.knotx.HttpRepositoryConnector]
                Deployed a8faf376-f333-401c-9988-7f2288044ed5 [knotx:example.io.knotx.HttpActionAdapter]
                Deployed 70539977-9337-48bb-9b76-7c2c1c9a1898 [knotx:io.knotx.HttpServiceAdapter]
                Deployed 35797434-b27d-45d2-91f2-af2a86c1b6b0 [knotx:io.knotx.RemoteRepositoryMock]
                Deployed 32d14454-a3c8-4b6f-b5f0-0954be5ad763 [knotx:example.io.knotx.KnotxServer]
                Deployed 06c5a193-4a37-4da2-b8ee-015a42357a35 [knotx:io.knotx.ServiceMock]
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
[knotx-example-app.json](https://github.com/Cognifide/knotx/blob/master/knotx-example/knotx-example-app/src/main/resources/knotx-example-app.json)
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
    "knotx:example.io.knotx.HttpActionAdapter",
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
$ java -Dexample.io.knotx.KnotxServer.options.config.httpPort=7777 -jar target/knotx-example-app-X.Y.Z-SNAPSHOT-fat.jar -conf knotx-example-experiments.json
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
$ java -Dexample.io.knotx.KnotxServer.options=file:server-options.json -Dexample.io.knotx.KnotxServer.options.config.httpPort=7777 -jar target/knotx-example-app-X.Y.Z-SNAPSHOT-fat.jar -conf knotx-example-experiments.json
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
