# Knot
A Knot defines business logic which can be applied to a particular [[Fragment|Splitter]]. It can, for 
example, invoke an external services via [[Adapters|Adapter]], evaluate Handlebars snippets or simply 
redirect a site visitor to a different location.

## How does it work?
The Knot reads a [Knot Context](#knot-context) containing a list of Fragments to process, takes care of the processing,
updates the Knot Context and returns it back to the caller.

A particular Knot is applied to Fragments if two conditions are met:

- it is defined in [[Knot routing|KnotRouting]]
- there is at least one Fragment that declares a matching [Knot Election Rule](#knot-election-rule)

### Knot Election Rule
A Knot Election Rule determines if a Knot should be applied to a particular Fragment. Every time
a Knot reads the Knot Context, it checks if there is any Fragment that needs to be processed by it. 

The Knot Election Rule is simple `String` value coming from the `data-knotx-knots` attribute (the attribute contains a comma-separated list of Knot Election Rules). 

Knots can simply filter Fragments which do not contain a certain Knot Election Rule (for example `services` or `handlebars`).

### Knot Context
The Knot Context is a communication model passed between [[Server|Server]], [[Fragment Splitter|Splitter]], 
[[Knots|Knot]] and [[Fragment Assembler|Assembler]]. 

The flow is driven by [[Server|Server]] forwarding and getting back KnotContext to/from Splitter, Knots and Assembler. 
Knot Context keeps information about a site visitor request, current processing status, site visitor response 
and optionally uploaded files. 

From now on, we will be using the terms *client* and *site visitor* interchangeably.

Knot Context contains:
* client request with path, headers, form attributes and parameters
* client response with body, headers and status code
* [[fragments|Splitter]]
* uploaded files
* transition value

*Client request* includes site visitor path (requested URL), HTTP headers, form attributes 
(for POST requests) and request query parameters.

*Client response* includes a body (which represents final response body), HTTP headers (which are narrowed finally
by Server) and HTTP status code.

Please see [[*Splitter*|Splitter]] section to find out what Fragments are and how they are produced. 
Fragments contain a template fragment content and a context. Knots can process a configured fragment content, 
call required Adapters and put responses from Adapters to the fragment context (fragment context is a JSON 
object).

*Uploaded files* is a set of file-uploads from an HTTP multipart form submission.

*Transition* is a text value which determines next step in [[request routing|KnotRouting]].

#### Knot Request
A table below represents an event model consumed by Knot. First rows relates to client request attributes
which are not modifiable within Knots. Next, rows are connected with client response attributes and 
transition. Those rows are modified by Knots according to required behaviour (continue routing, redirect
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

Knots are designed to process Knot Context and finally decides what next step in routing is valid.
It is the default Knot behaviour. Knots can also beak routing and decide to return an error or redirect 
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
| `transition`                 | `String`                      |        | defines next routing step (Knot), empty for redirects, errors and last routing step |

##### Example Knot Responses
Knots can decide what next routing step is valid. They can also break the routing. This section shows
example responses.

*Next Routing Step*

Knot decides that routing should be continued. It sets `transition` to `next` and then Server continues 
routing according to its [[configuration|Server]].

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `200`
| `transition`| `next` 

*Redirect response*

Knot finds out that client must be redirected to an other URL.

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `301`
| `clientResponse.headers.location`| `/new/location.html`
| `transition`| EMPTY 

*Error response*

Knot calls Adapter Service and gets **500**. Knot is not aware how this error should be processed so it sets clientResponse.statusCode to `500`.
Server beaks routing and responds with `500` to the client.

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `500`
| `transition`| EMPTY 


## How to configure?
the Knot API specifies an abstract class - `KnotConfiguration` to handle JSON configuration support. This
abstraction can be used while implementing a custom Knot but it is not required. Every Knot must be
exposed with a unique Event Bus address - that's the only obligation (as is the case with Adapters).
Please see example configurations for [[Action Knot|ActionKnot#how-to-configure]], 
[[Service Knot|ServiceKnot#how-to-configure]].

## How to implement your own Knot?
Implementation of a Knot does not require knowledge of how to communicate via the Vert.x event bus. It's wrapped by **Vert.x Service Proxy** functionality so any new implementation can focus on the business logic of the Knot. 

In order to implement a Knot, follow the guide below:

1. Create your Knot by extending `io.knotx.knot.AbstractKnotProxy` class, and implement your business logic in the `processRequest()` method with the return type of `Observable<KnotContext>` (a promise of the modified `KnotContext`).

   See `io.knotx.knot.service.impl.ServiceKnotProxyImpl.java` as an example.

2. Create a class extending `AbstractVerticle` that will read the configuration and register your `KnotProxy` implementation at the given `address`.

   Have a look at `io.knotx.knot.service.ServiceKnotVerticle.java` to see how the `ServiceKnotProxyImpl` is registered.

The `AbstractKnotProxy` class provides the following methods that you can override in your implementation in order to control the processing of Fragments:

- `boolean shouldProcess(Set<String> knots)` is executed on each Fragment from the given KnotContext, from each fragment it gets a set of Knot names (from the `data-knotx-knots` snippet attribute), and lets you decide whether the Fragment should be processed by your Knot or not (no pun intended).
- `Single<KnotContext> processRequest(KnotContext knotContext)` consumes `KnotContext` messages from a [[Server|Server]] and returns the modified `KnotContext` object as an instance of [`rx.Single`](http://reactivex.io/RxJava/javadoc/rx/Single.html).
- `KnotContext processError(KnotContext knotContext, Throwable error)` handles any Exception thrown during processing, and is responsible for preparing the proper KnotContext on such occasions, these will simply finish processing flows, as any error generated by a Knot will be immediately returned to the page visitor.

| ! Note |
|:------ |
| Please note that while this section focuses on the Java language specifically, it's not the only choice you have. Thanks to [the polyglot nature of Vert.x](http://vertx.io), you can implement your Adapters and Knots using other languages. |

| ! Note |
|:------ |
| Besides the Verticle implementation itself, a custom implementation of your Knot must be built as a Knot.x module in order to be deployed as part of Knot.x. Follow the [[Knot.x Modules|KnotxModules]] documentation in order to see how to make your Knot a module. | 
