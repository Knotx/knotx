# Server

Server is essentially a "heart" (a main [Verticle](http://vertx.io/docs/vertx-core/java/#_verticles)) of Knot.x.
It creates HTTP Server, listening for browser requests, and is responsible for coordination of
communication between [[Repository Connectors|RepositoryConnectors]], [[Splitter|Splitter]] and all deployed [[Knots|Knot]].

## How does it work?
Once the HTTP request from the browser comes to the Knot.x, it goes to the **Server** verticle.
Server performs following actions when receives HTTP request:

- Decides if there are too many concurrent requests, if the system is overloaded, [[incoming request is dropped|Server#dropping-the-requests]]
- Verifies if request **method** is configured in `routing` (see config below), and sends
**Method Not Allowed** response if not matches
- Search for the **repository** address in `repositories` configuration, by matching the
requested path with the regexp from config, and sends **Not Found** response if none is matched.
- Calls the matching **repository** address with the original request
- Calls the **splitter** address with the template got from **repository**
- Builds [[KnotContext|Knot]] communication model (that consists of original request, response from
repository & split HTML fragments)
- Calls **[[Knots|Knot]]** according to the [routing](#routing) configuration, with the **KnotContext**
- Once the last Knot returns processed **KnotContext**, server calls **assembler** to create HTTP Response based on data from the KnotContext
- Filters the response headers according to the `allowed.response.headers` configuration and returns to the browser.

The diagram below depicts flow of data coordinated by the **Server** based on the hypothetical
configuration of routing (as described in next section).
[[assets/knotx-server.png|alt=Knot.x Server How it Works flow diagram]]

### Dropping the requests
Knot.x implements a backpressure mechanism. It allows to drop requests after exceeding a certain amount of requests at a time.
If the Knot.x can't process the coming requests fast enough, he's able to tell to client that it's unable to process new requests by serving proper response code.
The logic is fairly simple. The incoming stream of requests are getting buffered if the Knot.x is unable to process them on the fly. 
When the buffer become full, server starts dropping any new requests with a configured response code. After the buffer slots will be released the new requests will start to be accepted and finally processed.

You have certain options available to control the mechanism, these are:
- **buffer capacity** - the amount of requests in the buffer. This number does not tell you how many requests per second system is able to handle. It depends on your custom implementation and external services, and how long the Knot.x processes your request.
- **buffer overflow strategy** - how the buffer overflow to be handled. Default is drop latest requests.

That solution prevent `OutOfMemoryError` errors when there are too many requests (e.g. during the peak hours). Additionally response times should be more stable when system is under high stress.

### Routing
Routing specifies how the system should behave for different [Knots|Knot] responses. The request flow at
the diagram above is reflected in a `routing` JSON node in the configuration section below. This routing
defines that all requests for HTML pages must be processed first by Knot listening on address
`first.knot.eventbus.address`. Then based on its response there are two next steps: `go-second` and
`go-alt`:
- If returned transition is `go-second`, Server will call next `second.knot.eventbus.address`.
- If returned transition is `go-alt`, Server will call next `alternate.knot.eventbus.address`.

For the route with `go-second` transition there is one more strep after `second.knot.eventbus.address` -
for `go-third` transition Server will call `third.knots.eventbus.address` at the end.
For the route with `go-alt` transition Server will call `alternate.knot.eventbus.address` only.
In both cases the response will be returned to the client.

For more details please see [[Routing|Routing]] and [[Communication Flow|CommunicationFlow]] sections.

## How to configure?
Server is deployed using Vert.x service factory as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default configuration.

The HTTP Server configuration consists two parts:
- Knot.x application specific configurations
- Vert.x HTTP Server configurations
- Vert.x Event Bus delivery options

### Knot.x application specific configurations

For all configuration fields and their defaults consult [KnotxServerOptions](https://github.com/Cognifide/knotx/blob/master/documentation/src/main/cheatsheet/cheatsheets.adoc#knotxserveroptions)

In short, by default, server:
- Listens on port 8092
- Displays exception details on error pages (for development purposes)
- Returns certain headers in Http Response to the client (as shown above)
- Always sets custom header in response **X-Server:Knot.x**
- Uses the [[default Knot.X routing mechanism|KnotRouting]]
- Communicates with two types of repositories: HTTP and Filesystem
- Uses core [[Splitter|Splitter]] and [[Assembler|Assembler]]
- Each GET request for any resource (`.*`) is routed through [[Service Knot|ServiceKnot]] and then [[Handlebars rendering engine|HandlebarsKnot]]

### Vert.x HTTP Server Options

Besides Knot.x specific configurations as mentioned above, you can tune the HTTP server itself and its low level details. 
The `serverOptions` is the place to do so, e.g. set server port as follows:
```
{
  "options": {
    "config": {
      "serverOptions": {
        "port": 8888,
         ...
      },
      ...
```
The details of remaining server options are described on the [Vert.x DataObjects page](http://vertx.io/docs/vertx-core/dataobjects.html#HttpServerOptions).

A HTTP server port can be also specified through system property `knotx.port` that takes precedence over the value in the configuration file.
```
java -Dknotx.port=9999 ...
```

Additionally, you can use JVM system property `knotx.fileUploadDir` to control where file uploads are to be stored in filesystem. 
The system property takes precedence over the value configured in configuration file. 
```
java -Dknotx.fileUploadDir=/tmp/knotx-uploads ...
```

### How to configure Knot.x to listen with SSL/TLS

Generate certificates for your machine (e.g. localhost)
`keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass keyPass -validity 360 -keysize 2048`

Where:
- `keystore.jks` - is a filename of the keystore
- `keyPass` - is the keystore password

Below is the sample configuration that enabled SSL:
```
{
  "options": {
    "config": {
      "serverOptions": {
        "port": 8043,
        "ssl": true,
        "keyStoreOptions": {
          "path": "keystore.jks",
          "password": "keyPass"
        }
      },
      ...
```
Where:
- `path` - is the path where keystore is located, optional if `value` is used
- `password` - keystore password

### How to enable CSRF Token generation and validation

As soon as you start implementing the REST API or form capturing via Knot.x, you might want to enable CSRF attack protection to prevent unwanted actions on your application. E.g. to prevent accepting POST requests if the request is unauthenticated by the Knot.x.
In order to do so, you need to configure two things:
- CSRF Token generation: When the user requests the page with `GET` method a Knot.x drops a cookie with a token
- All routes for POST, PUT, PATCH, DELETE Http methods to be accepted if request consists of CSRF token that was issued by the Knot.x

Below you can find an example configuration on a default flow, where CSRF is enabled on GET and POST routes. The same can be done on the custom flow.
In other scenarios, you might want to enable CSRF on the `GET` route of `DefaultFlow` in order to have token generated. But on the `POST` route of `CustomFlow` you will enable csrf, so the Knot.x will validate the request before passing it to the Knots.
```json
{
  "options": {
    "config": {
     "defaultFlow": {
        ....
        "routing": {
          "GET": [
            {
              "path": ".*",
              "address": "knotx.knot.service",
              "csrf": true,
              ...
            }
          ],
          "POST": [
            {
              "path": ".*",
              "address": "knotx.knot.action",
              "csrf": true,
              ...
            }
          ]
        },
        ....
      }
      ...
```

Besides routes configuration you can customize name of the cookies, headers, timeout for the token, secret key used to sign the token, etc. You can do this by overriding configuration of the Knotx Server as follows:
```json
{
  "options": {
    "config": {
      "csrf": {
        "secret": "eXW}z2uMWfGb",
        "cookieName": "XSRF-TOKEN",
        "cookiePath": "/",
        "headerName": "X-XSRF-TOKEN",
        "timeout": 10000
      },
      ...
```


### Vert.x Event Bus delivery options

While HTTP request processing, Server calls other modules like Repository Connectors, Knots using 
[Vert.x Event Bus](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/EventBus.html). 
The `config` field can contain [Vert.x Delivery Options](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/DeliveryOptions.html)
related to the event bus. It can be used to control the low level aspects of the event bus communication like timeouts, 
headers, message codec names.

For example, add `deliveryOptions` section in the KnotxServer configuration to define the 
timeout for all eventbus responses (Repositories, Splitter, Knots configured in routing, Assembler, etc) 
for eventubs requests that come from `KnotxServer`.
```
{
  "options": {
    "config": {
      "deliveryOptions": {
        "timeout": 15000
      },
      ...
    }
  }
}
```

### Configure access log
Knot.x uses a default Logging handler from the Vert.x web distribution that allows to log all incomming requests to the Http server.
It supports three log line formats that are:
- DEFAULT that tries to log in a format similar to Apache log format (APACHE/NCSA COMBINED LOG FORMAT) as in the example
`127.0.0.1 - - [Tue, 23 Jan 2018 14:16:34 GMT] "GET /content/local/simple.html HTTP/1.1" 200 2963 "-" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36"`
- SHORT
`127.0.0.1 - GET /content/local/simple.html HTTP/1.1 200 2963 - 19 ms`
- TINY
`GET /content/local/simple.html 200 2963 - 24 ms`

By default access log is enabled with a `DEFAULT` format. If you want to change it, just add access logging section on the KnotxServer configuration in your application.json config file :
```json
{
  "config": {
    "server": {
      "options": {
        "config": {
          "accessLog": {
            "format": "TINY"
          }
        }
      }
    }
  }
}
```
In order to configure logger for access log, see [[Logging|Logging]].
