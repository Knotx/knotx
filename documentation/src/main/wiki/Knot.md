#Knot
Knot is a module which defines custom step during [[request routing|KnotRouting]]. It can process custom
fragments, invoke Adapters and redirect site visitors to a new site or an error page.

##How does it work?
Knot module gets Knot Context, does his job and responds with Knot Context. This is a very simple but 
powerful contract which makes Knot easy to integrate and develop.

Knot registers to Event Bus with an unique address and listens for Knot Context events. Each Knot is deployed
as a separate verticle and is triggered during [[request routing|KnotRouting]].

To understand what Knots really do we need to know what Knot Context is.

### Knot Context
Knot Context is a model which is exchanged between [[Server|Server]] and Knot. Server forward Knot Context
to Knots and gets Knot Context back from them. So Knot Context keeps information about a site visitor 
request, a current processing status and a site visitor response. Next we will use *client* and
*site visitor* words equivalently.

Knot Context contains:
* client request with path, headers, form attributes and parameters
* client response with body, headers and status code
* [[fragments|Splitter]]
* transition

Client request contains such information like site visitor path (requested URL), HTTP headers, form 
attributes (for POST requests) and request query parameters.

Client response has body (which represents final response body), HTTP headers (which are narrowed finally
by Server) and HTTP status code.

What fragments are and how they are produced is described in [[Splitter|Splitter]] section. Fragments
keeps both a content and their context. Knots can for example process a particular fragment content, 
call required Adapters and put responses from Adapters to a fragment context (which is JSON object).

Transition is simple text value which determines next step in [[request routing|KnotRouting]].

#### Knot request model
Table below represents Knot Context which is sent from Server to Knot.

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `clientRequest.path`                 | `String`                      | &#10004;       | client request url |
| `clientRequest.method`                 | `HttpMethod`                      | &#10004;       | client request method |
| `clientRequest.headers`                 | `MultiMap`                      | &#10004;       | client request headers |
| `clientRequest.params`                 | `MultiMap`                      | &#10004;       | client request parameters |
| `clientRequest.formAttributes`                 | `MultiMap`                      |       | form attributes, relevant to POST requests |
| `clientResponse.statusCode`                 | `HttpResponseStatus`                      |   &#10004;    | `OK` status |
| `clientResponse.headers`                 | `MultiMap`                      | &#10004;       | client response headers |
| `clientResponse.body`                 | `Buffer`                      |        | response body, can be empty at some routing steps (Knots), finally produced based on fragments |
| `fragments`                 | `List<Fragment>`                      |   &#10004;    | list of Fragments created with Splitter module |
| `transition`                 | `String`                      |        | empty |


#### Knot response model
Table below represents Knot Context response.

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `clientRequest.path`                 | `String`                      | &#10004;       | client request url |
| `clientRequest.method`                 | `HttpMethod`                      | &#10004;       | client request HTTP method |
| `clientRequest.headers`                 | `MultiMap`                      | &#10004;       | client request HTTP headers |
| `clientRequest.params`                 | `MultiMap`                      | &#10004;       | client request parameters |
| `clientRequest.formAttributes`                 | `MultiMap`                      |       | form attributes, relevant to POST requests |
| `clientResponse.statusCode`               | `HttpResponseStatus`                      |    &#10004;    | `OK` to process routing, other to beak routing  |
| `clientResponse.headers`                 | `MultiMap`                      | &#10004;       | client response HTTP headers, can be updated by Knot |
| `clientResponse.body`                 | `Buffer`                      |        | response body for client, can be empty for some routing steps (Knots), finally produced based on fragments |
| `fragments`                 | `List<Fragment>`                      |   &#10004;    | list of Fragments created with Splitter module |
| `transition`                 | `String`                      |        | defines next routing step (Knot), empty for redirects, errors and last routing steps |


##### Next routing step response

| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `HttpResponseStatus.OK`
| `transition`| `next` 

##### Redirect response
| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `HttpResponseStatus.MOVED_PERMANENTLY`
| `clientResponse.headers`| `location: /new/location`
| `transition`| EMPTY 

##### Error response
| Name | Value
|-------:                     | :-------  
| `clientResponse.statusCode`| `HttpResponseStatus.NOT_FOUND`
| `transition`| EMPTY 



##How to configure?
Configuration options.
#TODO:
Describe what's Knot, it's API - KnotContext, how to build your own knot, and links to Knot.x shipped Knots as below
##How to extend?
How to extend if possible.