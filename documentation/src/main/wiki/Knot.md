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
A table below represents an event model consumed by Knot. First rows relate to client request attributes
which are not modifiable within Knots. Next rows are connected with client response attributes and 
Transition. Those rows are modified by Knots according to required behaviour (continue routing, redirect
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
Knot responds with Knot Context. So Knot Context from a request is consumed and updated according to required behaviour.

Knots are designed to process Knot Context and finally decides what a next step in Knots Routing is valid (via Transition).
It is the default Knot behaviour. Knots can also beak Knots Routing and decide to return an error or redirect 
response to the client.

A table below represents Knot response values.

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
| `Transition`                 | `String`                      |        | defines a next routing step (Knot), empty for redirects, errors and last routing step |

##### Example Knot Responses
Knot can decide what next routing step (Knot) should be invoked (via Transition) or even break Knots Routing. This section 
contains a few example responses.

*Next Routing Step*

Knot decides that routing should be continued. It sets `Transition` to `next` and then Server continues 
routing according to its [[configuration|Server]].

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `200`
| `transition`| `next` 

*Redirect response*

Knot finds out that a client must be redirected to an ohter URL.

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `301`
| `clientResponse.headers.location`| `/new/location.html`
| `transition`| EMPTY 

*Error response*

Knot calls Adapter Service and gets **500**. Knot is not aware how this error should be processed so it sets clientResponse.statusCode to `500`.
Server breaks the routing and responds with `500` to the client.

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `500`
| `transition`| EMPTY 


## How to configure?
Knots are exposed with an unique Event Bus address - that's the only one obligation (as is the case with Adapters).
Please see example configurations for [[Action Knot|ActionKnot#how-to-configure]], 
[[Service Knot|ServiceKnot#how-to-configure]].

## How to implement your own Knot?

Knot.x provides the [maven archetype](https://github.com/Knotx/knotx-extension-archetype) to generate a custom Knot. 
It is the **recommended** approach.

A Knot code is executed on a [Vert.x event loop](http://vertx.io/docs/vertx-core/java/#_reactor_and_multi_reactor). [The 
Vert.x Golden Rule](http://vertx.io/docs/vertx-core/java/#golden_rule) says that the code should **never block** the 
event loop. So all time consuming operations should be coded in an asynchronous way. By default Knots uses [RxJava](http://vertx.io/docs/vertx-rx/java/) 
which is a popular library for composing asynchronous and event-based programs using observable sequences for the Java VM.
RxJava introduce Reactive Programming what is a development model structured around asynchronous data streams. 

| ! Note |
|:------ |
| Reactive programming code first requires a mind-shift. You are notified of asynchronous events. Then, the API can be hard to grasp (just look at the list of operators). Don’t abuse, write comments, explain, or draw diagrams (I’m sure you are an asciiart artist). RX is powerful, abusing it or not explaining it will make your coworkers grumpy. [Read more](https://developers.redhat.com/blog/2017/06/30/5-things-to-know-about-reactive-programming/) |


In order to implement a Knot, follow the guide below. Note that the Knot archetype generates both the code and all configuration
files requires to run Knot.x with the custom Knot according to [[deployment recommendations|KnotxDeployment]].

1. Generate the Knot module

   `mvn archetype:generate -DarchetypeGroupId=io.knotx.archetypes -DarchetypeArtifactId=knotx-knot-archetype -DarchetypeVersion=X.Y.Z`

2. Compile, run tests and build a jar package

   `mvn package`

3. Copy [the released Knot.x standalone fat jar](https://github.com/Cognifide/knotx/releases/tag/1.1.2) to `app` folder

4. Execute `run.sh` script.

5. A console log should contain an entry with ExampleKnot.

The class with the Knot business logic is named `ExampleKnotProxy`. It extends `io.knotx.knot.AbstractKnotProxy` class, and 
implements the example business logic in the `processRequest()` method with the return type of `Single<KnotContext>` 
(a promise of the modified `KnotContext`).

The `AbstractKnotProxy` class provides the following methods that you can override in your implementation in order to 
control the processing of Fragments:

- `boolean shouldProcess(Set<String> knots)` is executed on each Fragment from the given KnotContext, from each fragment it gets a set of Knot Election Rules (from the `data-knotx-knots` snippet attribute), and lets you decide whether Fragment should be processed by your Knot or not (no pun intended).
- `Single<KnotContext> processRequest(KnotContext knotContext)` consumes `KnotContext` messages from a [[Server|Server]] and returns the modified `KnotContext` object as an instance of [`rx.Single`](http://reactivex.io/RxJava/javadoc/rx/Single.html).
- `KnotContext processError(KnotContext knotContext, Throwable error)` handles any Exception thrown during processing, and is responsible for preparing the proper KnotContext on such occasions, these will simply finish processing flows, as any error generated by Knot will be immediately returned to the page visitor.

| ! Note |
|:------ |
| Please note that while this section focuses on the Java language specifically, it's not the only choice you have. Thanks to [the polyglot nature of Vert.x](http://vertx.io), you can implement your Adapters and Knots using other languages. |

### How to run blocking code in your own Knot?
The easiest way to handle a blocking code inside your Knot is to deploy it as a [Vert.x worker](http://vertx.io/docs/vertx-core/java/#worker_verticles).
No change in your code is required - it is only the matter of a configuration:

```
{
  "main": "io.knot.example.ExampleKnot",
  "options": {
    "worker": true,
    ""multiThreaded": true,
    "config": {
      ...
    }
  }
}

```

### How to implement your own Knot without Rx Java?
Extending `AbstractKnotProxy` is the **recommended way** to implement your custom Knots. But still you can resign from
this approach and implement your custom Knots with Vert.x handlers. The only one thing to change is to implement `KnotProxy`
instead of extending `AbstractKnotProxy`. Then your process you need to implement a method `void process(KnotContext knotContext, Handler<AsyncResult<KnotContext>> result);`
