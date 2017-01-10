#Knot
Knot defines business logic which can be applied for particular [[Fragment|Splitter]]. It can for 
example invoke external services via [[Adapters|Adapter]], evaluate Handlebars snippet or simply 
redirect a site visitor to different location.

##How does it work?
Knot reads [Knot Context](#knot-context) having list of Fragments to process, does its job,
updates Knot Context and returns it back to the caller. 

Particular Knot is applied to Fragments if two conditions are met:

- it is defined in [[Knot routing|KnotRouting]]
- there is at least one Fragment which declares matching [Knot Election Rule](#knot-election-rule)

### Knot Election Rule
Knot Election Rule allows to define if Knot should be applied for particular Fragment. Every time 
Knot reads Knot Context it checks if there is any Fragment which requires it. Knot Election Rule is 
simple String value coming from `data-knot-types` attribute (the attribute contains list of Knot
Election Rules separated with comma). Knots can simply filter Fragments which does not contain 
certain Knot Election Rule (for example `services` or `handlebars`).

### Knot Context
Knot Context is a communication model passed between [[Server|Server]], [[Fragment Splitter|Splitter]], 
[[Knots|Knot]] and [[Fragment Assembler|Assembler]]. The flow is driven by Server forwarding and getting 
back Knot Context to / from Splitter, Knots and Assembler. Knot Context keeps information about a 
site visitor request, current processing status and a site visitor response. Next we will use *client* and
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

Please see [[Splitter|Splitter]] section to find out what Fragments are and how they are produced. 
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
exposed with unique Event Bus address - that's the only obligation (the same like for Adapters).
Please see example configurations for [[Action Knot|ActionKnot#how-to-configure]], 
[[Service Knot|ServiceKnot#how-to-configure]].

##How to extend?
We need to extend abstract 
[com.cognifide.knotx.knot.api.AbstractKnot](https://github.com/Cognifide/knotx/blob/master/knotx-knots/knotx-knot-api/src/main/java/com/cognifide/knotx/knot/api/AbstractKnot.java)
class from `knotx-knots/knotx-knot-api`. AbstractKnot hides Event Bus communication and JSON configuration initialization parts
and lets you to focus on Knot logic:

- `initConfiguration` method that initialize Knot with `JsonObject` model
- `process` method that consumes `KnotContext` messages from [[Server|Server]] and returns modified `KnotContext` messages
- `processError` method which handle particular Exception and prepare response for Server

| ! Note |
|:------ |
| Please note that this section focuses on Java language only. Thanks to [Vert.x polyglotism mechanism](http://vertx.io) you can implement your Adapters and Knots using other languages. |

| ! Note |
|:------ |
| Besides Verticle implementation itself, a custom implementation of your Knot must be build as Knot.x module in order to be deployed as part of Knot.x. Follow the [[Knot.x Modules|KnotxModules]] in order to see how to make your Knot a module. | 
