# Adapters
Adapters are modules which are responsible for communication between Knot.x (exactly [[Knots|Knot]]) 
and external services.

[[assets/knotx-adapters.png|alt=Adapters]]

## How does it work?
Adapters can be thought as extension points where project specific logic appears. With custom [[Knots|Knot]] 
they provides very flexible mechanism to inject project specific requirements.

We recommend to create a dedicated Adapter every time some service-level business logic or service 
response adaption to other format is required. E.g. we need to 
[inject the data directly form the database](http://knotx.io/blog/adapt-service-without-webapi/).


### Types of adapters
Knot.x Core by default introduces two types of Adapters connected with Knot implementations:
- [[Service Adapter|ServiceAdapter]] for [[Service Knot|ServiceKnot]],
- [[Action Adapter|ActionAdapter]] for [[Action Knot|ActionKnot]]

Knot.x comes with a generic implementation of [[Service Adapter|ServiceAdapter]], that enables communication 
with external services using HTTP Protocol (only GET requests).
This [Hello Rest Service Tutorial](http://knotx.io/blog/hello-rest-service/) contains an example of
how to integrate external Web based service data into your webpage. See also [[Http Service Adapter|HttpServiceAdapter]] 
for more information. Please note, that this implementation is very generic and we recommend to create 
project-specific Adapters for any custom requirements.

Action Adapters are project specific in terms of error handling and redirection mechanisms. Knot.x Core
is not going to provide any generic Action Adapters.

For custom Knots we can introduce custom Adapter types. As far as Knots must follow [[Knot contract|Knot#how-does-it-work]],
Adapters are coupled with Knot directly so they can define their custom request, response or 
configuration. The communication between Knot and Adapter can be custom too.

Communication contract between [[Service Knot|ServiceKnot]] and [[Http Service Adapter|HttpServiceAdapter]] is defined by:
- `AdapterRequest` input,
- `AdapterResponse` output.

#### Adapter Request
The table below shows all the fields in the `AdapterRequest` - the communication model between Knot.x Service Knot and Service Adapters.

| Name                        | Type           | Mandatory | Description  |
|-------:                     |:-------:       |:-------:   |-------|
| `clientRequest.path`        | `String`       | &#10004;   | client request url, e.g. `/services/mock/first.json` |
| `clientRequest.method`      | `HttpMethod`   | &#10004;   | client request method, e.g. `GET`, `PUT`, etc. |
| `clientRequest.headers`     | `MultiMap`     | &#10004;   | client request headers |
| `clientRequest.params`      | `MultiMap`     | &#10004;   | client request parameters |
| `params`                    | `JsonObject`   | &#10004;   | `JsonObject` with additional params that can be passed via configuration file, e.g. `"params": { "example": "example-value" }` |
| `adapterParams`             | `JsonObject`   |            |  `JsonObject` with additional adapter parameters that can be set in the form of `data-knotx-adapter-params` in the snippet, e.g. `data-knotx-adapter-params='{"myKey":"myValue"}'` |

#### Adapter Response
The table below shows all the fields in the `AdapterResponse` - an object returned by the Adapter to the Service Knot.

| Name                        | Type          | Mandatory   | Description  |
|-------:                      |:-------:     |:-------:    |-------|
| `clientResponse.statusCode`  | `int`        | &#10004;    | status code of service response, e.g. `200`, `302`, `404` |
| `clientResponse.headers`     | `MultiMap`   | &#10004;    | client response headers |
| `clientResponse.body`        | `Buffer`     |             | final response body |
| `signal`                     | `String`     |             | defines how original request processing should be handled (currently used only by Action Knot), e.g. `next` |


## How to configure?
The Adapter API specifies an abstract class - `AdapterConfiguration` to handle JSON configuration support. This
abstraction can be used while implementing a custom Adapter but it is not required. Every Adapter must be
exposed with a unique Event Bus address - that's the only obligation (as is the case with Knots).
Please see an example configuration for [[Http Service Adapter|HttpServiceAdapter#how-to-configure]]

## How to implement your own Adapter?
Knot.x provides the [maven archetypes](https://github.com/Knotx/knotx-extension-archetype) to generate custom Adapters. 
It is the **recommended** way to create your own Adapter.

An Adapter logic is executed on a [Vert.x event loop](http://vertx.io/docs/vertx-core/java/#_reactor_and_multi_reactor). [The 
Vert.x Golden Rule](http://vertx.io/docs/vertx-core/java/#golden_rule) says that the code should **never block** the 
event loop. So all time-consuming operations should be coded in an asynchronous way. Default Knot.x Adapters use [RxJava](http://vertx.io/docs/vertx-rx/java/) 
which is a popular library for composing asynchronous and event-based programs using observable sequences for the Java VM.
RxJava introduce a Reactive Programming that is a development model structured around asynchronous data streams. 

| Note |
|:------ |
| Reactive programming code first requires a mind-shift. You are notified of asynchronous events. Then, the API can be hard to grasp (just look at the list of operators). Don’t abuse, write comments, explain, or draw diagrams. RX is powerful, abusing it or not explaining it will make your coworkers grumpy. [Read more](https://developers.redhat.com/blog/2017/06/30/5-things-to-know-about-reactive-programming/) |

Implementation of an Adapter does not require knowledge of how to communicate via the Vert.x event bus.
It's wrapped by the **Vert.x Service Proxy** functionality so any new implementation can focus on 
the business logic of the Adapter.

In order to implement an Adapter generate a new Adapter module using maven archetype:

   `mvn archetype:generate -DarchetypeGroupId=io.knotx.archetypes -DarchetypeArtifactId=knotx-adapter-archetype -DarchetypeVersion=X.Y.Z`

Note that, the Adapter archetype generates not only a skeleton of your custom Adapter, but also all
the configuration files that's required to run a Knot.x instance. 
More details about the Knot.x deployment can be found in the [[deployment section|KnotxDeployment]].

Archetype generates 3 important java files:
 
 - `ExampleServiceAdapterConfiguration` a simple POJO with configuration of the Adapter,
 - `ExampleServiceAdapterProxy` implement your business logic here in the `processRequest()` method with the return type of `Observable<AdapterResponse>` (promise of the `AdapterResponse`).
 - `ExampleServiceAdapter` that extends `AbstractVerticle`. It will simply read the configuration and register your `AdapterProxy` implementation at the provided `address`. 

The `AbstractAdapterProxy` class provides the following methods that you can extend in your implementation:

- `Observable<AdapterResponse> processRequest(AdapterRequest message)` method that consumes 
`AdapterRequest` messages from [[Knot|Knot]] and returns [`AdapterResponse`](#adapter-response) object as `rx.Observable`
- Optionally, `AdapterResponse getErrorResponse(AdapterRequest request, Throwable error)` method 
which handles any Exception thrown during processing, and is responsible for preparing proper 
[AdapterResponse](#adapter-response) on such situations. By default `AbstractAdapterProxy` implements this method, 
and returns `AdapterResponse` with the `ClientResponse` object having `500` status code and the 
error message in response body. 

| ! Note |
|:------ |
| Please note that while this section focuses on the Java language specifically, it's not the only choice you have. Thanks to [the polyglot nature of Vert.x](http://vertx.io), you can implement your Adapters and Knots using other languages. |

| ! Note |
|:------ |
| Besides the Verticle implementation itself, a custom implementation of your Adapter must be built as a Knot.x module in order to be deployed as part of Knot.x. Follow the [Knot.x Modules](https://github.com/Cognifide/knotx/wiki/KnotxModules) documentation in order to learn how to make your Adapter a module. | 

#### Building and running example Adapter
To run the extension:

1. [Download the Knot.x fat jar](https://github.com/Cognifide/knotx/releases/latest) and  it to the `apps` folder.
2. Build the extension using `mvn package`
3. Copy custom Adapter fat jar from the `target` directory into the `apps` directory
4. Execute the `run.sh` bash script. You will see output similar to the following:
```
2017-08-04 15:10:21 [vert.x-eventloop-thread-2] INFO  i.k.r.FilesystemRepositoryConnectorVerticle - Starting <FilesystemRepositoryConnectorVerticle>
2017-08-04 15:10:21 [vert.x-eventloop-thread-3] INFO  i.k.s.FragmentSplitterVerticle - Starting <FragmentSplitterVerticle>
2017-08-04 15:10:21 [vert.x-eventloop-thread-1] INFO  io.knotx.server.KnotxServerVerticle - Starting <KnotxServerVerticle>
2017-08-04 15:10:21 [vert.x-eventloop-thread-0] INFO  i.k.a.example.ExampleServiceAdapter - Starting <ExampleServiceAdapter>
2017-08-04 15:10:21 [vert.x-eventloop-thread-4] INFO  i.k.k.a.FragmentAssemblerVerticle - Starting <FragmentAssemblerVerticle>
2017-08-04 15:10:21 [vert.x-eventloop-thread-7] INFO  i.k.k.t.HandlebarsKnotVerticle - Starting <HandlebarsKnotVerticle>
2017-08-04 15:10:21 [vert.x-eventloop-thread-6] INFO  i.k.knot.action.ActionKnotVerticle - Starting <ActionKnotVerticle>
2017-08-04 15:10:21 [vert.x-eventloop-thread-5] INFO  i.k.knot.service.ServiceKnotVerticle - Starting <ServiceKnotVerticle>
2017-08-04 15:10:22 [vert.x-eventloop-thread-1] INFO  io.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2017-08-04 15:10:22 [vert.x-eventloop-thread-0] INFO  i.k.launcher.KnotxStarterVerticle - Knot.x STARTED

                Deployed 3bd0365c-10ba-4a53-a29c-59b4df06eaff [knotx:io.knotx.FragmentSplitter]
                Deployed defc43bc-ffe0-40cf-8c0c-1be06c5e8739 [knotx:com.example.adapter.example.ExampleServiceAdapter]
                Deployed 6891d9d6-bcfc-42fa-8b21-bd4ea11154da [knotx:io.knotx.FragmentAssembler]
                Deployed 59a2967f-0d87-4dea-ad37-b8fe1dafb898 [knotx:io.knotx.FilesystemRepositoryConnector]
                Deployed b923df1c-f8f9-4615-9b9e-4fba3806a575 [knotx:io.knotx.ActionKnot]
                Deployed c135524e-b4b1-452c-b8e7-628dfc58195d [knotx:io.knotx.ServiceKnot]
                Deployed 93ea2509-e045-49f8-9ddb-fc167e16f020 [knotx:io.knotx.HandlebarsKnot]
                Deployed 880116cf-54a7-4849-954e-dfb4eb536685 [knotx:io.knotx.KnotxServer]
```
5. Open a page: [http://localhost:8092/content/local/template.html](http://localhost:8092/content/local/template.html) in your 
browser to validate a page displays value `Hello Knot.x`.

### How to handle blocking code in your own Adapter?
The easiest way to handle a blocking code inside your Adapter is to deploy it as a [Vert.x worker](http://vertx.io/docs/vertx-core/java/#worker_verticles).
No change in your code is required.

To do so, you need to override configuration of your verticle and set verticle options to be deployed in workers pool via [DeploymentOptions](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html).
```
{
  "config": {
    "myAdapter": {
       "options": {
          "worker": true,
          "config": {
            ...
          }
       }
    }
  }
}
```
Now in your Knot.x instance log file you should see
```
2018-01-08 10:00:16 [vert.x-worker-thread-0] INFO  com.example.SomeAdapter - Starting <SomeAdapter>
```
For more information about deployment options of Worker verticles see [Vert.x documentation](http://vertx.io/docs/vertx-core/java/#worker_verticles).

### How to implement your own Adapter without Rx Java?
Extending `AbstractAdapterProxy` is the **recommended** way to implement your custom Adapter. But still you can resign from
this approach and implement your custom Adapter with Vert.x handlers (without using RxJava). The only one thing to change 
is to implement `AdapterProxy` instead of extending `AbstractAdapterProxy`. Then you need to implement a 
method `void process(AdapterRequest request, Handler<AsyncResult<AdapterResponse>> result)` where you should implement your 
custom Adapter business logic. 

### Adapters common library
For many useful and reusable Adapters concept, please check our [knotx-adapter-common](https://github.com/Cognifide/knotx/tree/master/knotx-adapter/knotx-adapter-common)
module. You will find there support for `placeholders` and `http connectivity`. 

### How to run a custom Adapter with Knot.x
Please refer to [[Deployment|KnotxDeployment]] section to find out more about deploying and running 
a custom Adapters with Knot.x.
