#Knot
Knot is a module which defines custom step during [[request routing|KnotRouting]]. It can process 
markup [[fragments|Splitter]], invoke [[Adapters|Adapter]] and redirect site visitor depending on 
Adapter response.

##How does it work?
Knot gets [Knot Context](#knot-context), does its job and responds with Knot Context. This is a very simple but 
powerful contract which makes Knot easy to integrate and develop.

Knot registers to Event Bus with an unique address and listens for [Knot Context events](#knot-request). 
Each Knot is deployed as a separate verticle and is triggered during [[request routing|KnotRouting]].

To understand what Knots really do we need to know what Knot Context is.

### Knot Context
Knot Context is a model which is exchanged between [[Server|Server]] and Knot. Server forward Knot Context
to Knots and gets Knot Context back from them. So Knot Context keeps information about site visitor 
request, current processing status and site visitor response. Next we will use *client* and
*site visitor* words equivalently.

Knot Context contains:
* client request with path, headers, form attributes and parameters
* client response with body, headers and status code
* [[fragments|Splitter]]
* transition

*Client request* includes site visitor path (requested URL), HTTP headers, form attributes 
(for POST requests) and request query parameters.

*Client response* includes a body (which represents final response body), HTTP headers (which are narrowed finally
by Server) and HTTP status code.

Please see [[Splitter|Splitter]] section to find out what fragments are and how they are produced. 
Fragments contain a template fragment content and a context. Knots can process a configured fragment content, 
call required Adapters and put responses from Adapters to the fragment context (fragment context is a JSON 
object).

Transition is a text value which determines next step in [[request routing|KnotRouting]].

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
| `clientResponse.body`                 | `Buffer`                      |        | final response body, can be empty until last View Knot |
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
| `clientResponse.body`                 | `Buffer`                      |        | final response body, can be empty until last View Knot |
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
| `clientResponse.statusCode`| `HttpResponseStatus.OK`
| `transition`| `next` 

*Redirect response*

Knot finds out that client must be redirected to an other URL.

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `HttpResponseStatus.MOVED_PERMANENTLY`
| `clientResponse.headers`| `location: /new/location.html`
| `transition`| EMPTY 

*Error response*

Knot calls Adapter Service and gets HttpResponseStatus.INTERNAL_SERVER_ERROR. Knot is not
aware how this error should be processed so it sets clientResponse.statusCode to HttpResponseStatus.INTERNAL_SERVER_ERROR.
Server beaks routing and responds with HttpResponseStatus.INTERNAL_SERVER_ERROR to the client.

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `HttpResponseStatus.NOT_FOUND`
| `transition`| EMPTY 


##How to configure?
Knot API specifies abstract `KnotConfiguration` class to handle JSON configuration support. This
abstraction can be used while custom Knot implementation but it is not required. Every Knot must be
exposed with unique Event Bus address - that's only one obligation (the same like for Adapters).
Please see example configurations for [[Action Knot|ActionKnot#how-to-configure]], 
[[View Knot|ViewKnot#how-to-configure]].

##How to extend?
We need to extend abstract 
[com.cognifide.knotx.knot.api.AbstractKnot](https://github.com/Cognifide/knotx/blob/master/knotx-knots/knotx-knot-api/src/main/java/com/cognifide/knotx/knot/api/AbstractKnot.java)
class from `knotx-knots/knotx-knot-api`. AbstractKnot hides Event Bus communication and JSON configuration initialization parts
and lets you to focus on Knot logic:

- `initConfiguration` method that initialize Knot with `JsonObject` model
- `handle` method that consumes `KnotContext` messages from [[Server|Server]] and returns modified `KnotContext` messages
- `processError` method which handle particular Exception and prepare response for Server

| ! Note |
|:------ |
| Please note that this section focused on Java language only. Thanks to [Vert.x polyglotism mechanism](http://vertx.io) you can implement your Adapters and Knots using language you like. |