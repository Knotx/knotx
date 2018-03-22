# Knot
Knot defines a business logic which can be applied to a particular [[Fragment|Splitter]]. It can, for 
example, invoke an external services via [[Adapter|Adapter]], evaluate Handlebars snippets or simply 
redirect a site visitor to a different location. 

## How does it work?
Knots are invoked by the [[Server|Server]] **sequentially** according to [[Knots Routing|KnotRouting]] configuration.
Every Knot operates on [Knot Context](#knot-context) which contains a list of Fragments to process. Knot takes care of a processing, optionally updates the Knot Context and returns it back to the caller so that it will be an input for the next called Knot.

A particular Knot will process a Fragment only when those two conditions are met:

- it is defined in [[Knots Routing|KnotRouting]]
- there is at least one Fragment that declares matching [Knot Election Rule](#knot-election-rule)

### Knot Election Rule
Knot Election Rule determines if Knot should process a Fragment or not.
Knot Election Rule is a simple `String` value that comes from a `data-knotx-knots` attribute from the [[Fragment script tag|Splitter#example]]. 
The attribute contains a comma-separated list of Knot Election Rules which *can* be used by Knot to determine if it should
process that particular Fragment or not.

Knots **can** simply filter out Fragments which do not contain the certain Knot Election Rule (for example `services` or `handlebars`).

### Knot Context
Knot Context is a communication model passed between [[Server|Server]], [[Fragment Splitter|Splitter]], 
[[Knots|Knot]] and [[Fragment Assembler|Assembler]]. 

The flow is driven by the [[Server|Server]] forwarding. Originally KnotContext is created by the [[Fragment Splitter|Splitter]] module basing on the [[Repository|RepositoryConnectors]] template input. 

Knot Context contains:
* a client request with a path, headers, form attributes and parameters
* a client response with a body, headers and a status code
* [[Fragments|Splitter]]
* [[Transition|KnotRouting]]] value

From now, we will be using terms *client* and *site visitor* interchangeably.

*A client request* includes a site visitor path (a requested URL), HTTP headers, form attributes 
(for POST requests) and request query parameters.

*A client response* includes a body (which represents the final response body set by [[Fragment Assembler|Assembler]]), 
HTTP headers (which are narrowed finally by [[Server|Server]] according to `allowedResponseHeaders` parameter) and HTTP status code.

Please see [[Splitter|Splitter]] section to find out what Fragments are and how they are produced. 
Fragments are documented [[here|Splitter#fragment]]. Knots can, for example, process 
Fragment Content, call required Adapters and put responses from Adapters to Fragment Context (a JSON object).

**Transition** is a text value which determines the next step in [[Knots Routing|KnotRouting]].

#### Knot Request
The table below represents an event model consumed by Knot. Client request attributes are not modifiable within Knots. 
Client response and Transition attributes are modified by Knots according to required behaviour (continue routing, redirect
to another url, return an error response).

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `clientRequest.path`                 | `String`                      | &#10004;       | client request url |
| `clientRequest.method`                 | `HttpMethod`                      | &#10004;       | client request method |
| `clientRequest.headers`                 | `MultiMap`                      | &#10004;       | client request headers |
| `clientRequest.params`                 | `MultiMap`                      | &#10004;       | client request parameters |
| `clientRequest.formAttributes`                 | `MultiMap`                      |       | form attributes, relevant to POST requests |
| `clientResponse.statusCode`                 | `HttpResponseStatus`                      |   &#10004;    | `HttpResponseStatus.OK` |
| `clientResponse.headers`                 | `MultiMap`                      | &#10004;       | client response headers |
| `clientResponse.body`                 | `Buffer`                      |        | final response body, can be empty until last Handlebars Knot |
| `fragments`                 | `List<Fragment>`                      |   &#10004;    | list of Fragments created by Splitter |
| `transition`                 | `String`                      |        | empty |


#### Knot Response 
Knot responds with Knot Context that is consumed and updated according to required behaviour Knot Context object from a request.

Knots are designed to process Knot Context and finally decides what a next step in Knots Routing is valid (via Transition).
It is the default Knot behaviour. Knots can also break Knots Routing and decide to return an error or redirect 
response to the client.

The table below represents Knot response values.

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `clientRequest.path`                 | `String`                      | &#10004;       | client request url |
| `clientRequest.method`                 | `HttpMethod`                      | &#10004;       | client request method |
| `clientRequest.headers`                 | `MultiMap`                      | &#10004;       | client request headers |
| `clientRequest.params`                 | `MultiMap`                      | &#10004;       | client request parameters |
| `clientRequest.formAttributes`                 | `MultiMap`                      |       | form attributes, relevant to POST requests |
| `clientResponse.statusCode`               | `HttpResponseStatus`                      |    &#10004;    | `HttpResponseStatus.OK` to process routing, other to beak routing  |
| `clientResponse.headers`                 | `MultiMap`                      | &#10004;       | client response headers, can be updated by Knot |
| `clientResponse.body`                 | `Buffer`                      |        | final response body, can be empty until last Handlebars Knot |
| `fragments`                 | `List<Fragment>`                      |   &#10004;    | list of Fragments created by Splitter |
| `transition`                 | `String`                      |        | defines the next routing step (Knot), empty for redirects, errors and last routing step |

##### Example Knot Responses
Knot can decide what next routing step (Knot) should be invoked (via `transition` property) or even break Knots Routing. This section 
contains a few example responses.

*Next Routing Step*

Knot decides that routing should be continued. It sets Transition value to `next` and then Server continues 
routing according to its [[configuration|Server]].

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `200`
| `transition`| `next` 

*Redirect response*

Knot finds out that a client must be redirected to another URL.

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `301`
| `clientResponse.headers.location`| `/new/location.html`
| `transition`| EMPTY 

*Error response*

Knot calls Adapter Service and gets **500**. Knot is not aware how this error should be processed so it sets `clientResponse.statusCode` to `500`.
Server breaks the routing and responds with `500` to the client.

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `500`
| `transition`| EMPTY 


## How to configure?
Knots are exposed with an unique Event Bus address - that's the only obligation (this is also true for Adapters).
Please see the example configurations for [[Action Knot|ActionKnot#how-to-configure]], 
[[Service Knot|ServiceKnot#how-to-configure]].

## How to implement your own Knot?

Knot.x provides the [maven archetypes](https://github.com/Knotx/knotx-extension-archetype) to generate custom Knots / [[Adapters|Adapter]]. 
It is the **recommended** way to create your own Knots.

A Knot code is executed on a [Vert.x event loop](http://vertx.io/docs/vertx-core/java/#_reactor_and_multi_reactor). [The 
Vert.x Golden Rule](http://vertx.io/docs/vertx-core/java/#golden_rule) says that the code should **never block** the 
event loop. So all time-consuming operations should be coded in an asynchronous way. By default Knots uses [RxJava](http://vertx.io/docs/vertx-rx/java/) 
which is a popular library for composing asynchronous and event-based programs using observable sequences for the Java VM.
RxJava introduce Reactive Programming what is a development model structured around asynchronous data streams. 

| ! Note |
|:------ |
| Reactive programming code first requires a mind-shift. You are notified of asynchronous events. Then, the API can be hard to grasp (just look at the list of operators). Don’t abuse, write comments, explain, or draw diagrams. RX is powerful, abusing it or not explaining it will make your coworkers grumpy. [Read more](https://developers.redhat.com/blog/2017/06/30/5-things-to-know-about-reactive-programming/) |


In order to implement an Knot generate a new Knot module using maven archetype:

`mvn archetype:generate -DarchetypeGroupId=io.knotx.archetypes -DarchetypeArtifactId=knotx-knot-archetype -DarchetypeVersion=X.Y.Z`

Note that the Knot archetype generates both the code and all configuration
files required to run a Knot.x instance containing the custom Knot. More details about the Knot.x deployment can be found in the
[[deployment section|KnotxDeployment]].

The `ExampleKnotProxy` class contains the Knot processing logic. It extends [`io.knotx.knot.AbstractKnotProxy`](https://github.com/Cognifide/knotx/blob/master/knotx-core/src/main/java/io/knotx/knot/AbstractKnotProxy.java) 
class, and implements the example processing logic in the `processRequest()` method with the return type of `Single<KnotContext>` 
(a promise of the modified `KnotContext`).

The `AbstractKnotProxy` class provides the following methods that you can override in your implementation in order to 
control the processing of Fragments:

- `boolean shouldProcess(Set<String> knots)` is executed on each Fragment from the given KnotContext, from each fragment it gets a set of Knot Election Rules (from the `data-knotx-knots` snippet attribute), and lets you decide whether Fragment should be processed by your Knot or not (no pun intended).
- `Single<KnotContext> processRequest(KnotContext knotContext)` consumes `KnotContext` messages from the [[Server|Server]] and returns the modified `KnotContext` object as an instance of [`rx.Single`](http://reactivex.io/RxJava/javadoc/rx/Single.html).
- `KnotContext processError(KnotContext knotContext, Throwable error)` handles any Exception thrown during processing, and is responsible for preparing the proper KnotContext on such occasions, these will simply finish processing flows, as any error generated by Knot will be immediately returned to the page visitor.

| ! Note |
|:------ |
| Please note that while this section focuses on the Java language specifically, it's not the only choice you have. Thanks to [the polyglot nature of Vert.x](http://vertx.io), you can implement your Adapters and Knots using other languages. |


#### Building and running example Knot
1. Download and copy [Knot.x standalone fat jar](https://github.com/Cognifide/knotx/releases/latest) to the `app` folder
2. Compile, run tests and build a jar package using `mvn package`
3. Copy custom Knot jar (`target/*-1.0-SNAPSHOT-fat.jar`) to the `app` folder
3. Execute `run.sh` script. Console log should contain an entry with ExampleKnot

```
2018-01-08 09:53:46 [vert.x-eventloop-thread-2] INFO  i.k.r.FilesystemRepositoryConnectorVerticle - Starting <FilesystemRepositoryConnectorVerticle>
2018-01-08 09:53:46 [vert.x-eventloop-thread-1] INFO  io.knotx.server.KnotxServerVerticle - Starting <KnotxServerVerticle>
2018-01-08 09:53:46 [vert.x-eventloop-thread-0] INFO  i.k.e.knot.example.ExampleKnot - Starting <ExampleKnot>
2018-01-08 09:53:47 [vert.x-eventloop-thread-3] INFO  i.k.s.FragmentSplitterVerticle - Starting <FragmentSplitterVerticle>
2018-01-08 09:53:47 [vert.x-eventloop-thread-4] INFO  i.k.k.a.FragmentAssemblerVerticle - Starting <FragmentAssemblerVerticle>
2018-01-08 09:53:47 [vert.x-eventloop-thread-5] INFO  i.k.knot.service.ServiceKnotVerticle - Starting <ServiceKnotVerticle>
2018-01-08 09:53:47 [vert.x-eventloop-thread-7] INFO  i.k.k.t.HandlebarsKnotVerticle - Starting <HandlebarsKnotVerticle>
2018-01-08 09:53:47 [vert.x-eventloop-thread-6] INFO  i.k.knot.action.ActionKnotVerticle - Starting <ActionKnotVerticle>
2018-01-08 09:53:47 [vert.x-eventloop-thread-1] INFO  io.knotx.server.KnotxServerVerticle - Knot.x HTTP Server started. Listening on port 8092
2018-01-08 09:53:47 [vert.x-eventloop-thread-0] INFO  i.k.launcher.KnotxStarterVerticle - Knot.x STARTED

                Deployed 7e0a0bb8-4704-431f-9a21-0d6a6f013587 [knotx:io.knotx.FilesystemRepositoryConnector]
                Deployed 67c6c8e9-5249-4b58-bda6-7f78361c50c5 [knotx:io.knotx.exampleknot.knot.example.ExampleKnot]
                Deployed eee726c5-2e12-4455-95c6-32b2e02eef0f [knotx:io.knotx.FragmentAssembler]
                Deployed 4920772b-dd96-4325-adef-488b1d541d0b [knotx:io.knotx.FragmentSplitter]
                Deployed 947721ba-e107-4500-a282-fb73d4316eac [knotx:io.knotx.ServiceKnot]
                Deployed d987e3a2-acad-4e93-89b3-216b1551426a [knotx:io.knotx.ActionKnot]
                Deployed 906ea442-6863-4a89-bddc-f538a6e27084 [knotx:io.knotx.HandlebarsKnot]
                Deployed 0b2e0245-4afa-4e9d-8a39-5e8fbbaab810 [knotx:io.knotx.KnotxServer]
```

5. Open a [http://localhost:8092/content/local/template.html](http://localhost:8092/content/local/template.html) link in your 
browser to validate a Knot header message (`Knot example`).

### How to handle blocking code in your own Knot?
The easiest way to handle a blocking code inside your Knot is to deploy it as a [Vert.x worker](http://vertx.io/docs/vertx-core/java/#worker_verticles).
No change in your code is required.

To do so, you need to override configuration of your verticle and set verticle options to be deployed in in workers pool via [DeploymentOptions](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html).
```
{
  "config": {
    "myExample": {
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
2018-01-08 10:00:16 [vert.x-worker-thread-0] INFO  i.k.e.knot.example.ExampleKnot - Starting <ExampleKnot>
```
For more information about deployment options of Worker verticles see [Vert.x documentation](http://vertx.io/docs/vertx-core/java/#worker_verticles).

### How to implement your own Knot without Rx Java?
Extending `AbstractKnotProxy` is the **recommended** way to implement your custom Knots. But still you can resign from
this approach and implement your custom Knots with Vert.x handlers (without using RxJava). The only one thing to change 
is to implement `KnotProxy` instead of extending `AbstractKnotProxy`. Then you need to implement a 
method `void process(KnotContext knotContext, Handler<AsyncResult<KnotContext>> result)` where you should implement your 
custom Knot Election Rule and processing logic. 
