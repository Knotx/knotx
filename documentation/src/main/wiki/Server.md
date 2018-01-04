# Server

Server is essentially a "heart" (a main [Verticle](http://vertx.io/docs/vertx-core/java/#_verticles)) of Knot.x.
It creates HTTP Server, listening for browser requests, and is responsible for coordination of
communication between [[Repository Connectors|RepositoryConnectors]], [[Splitter|Splitter]] and all deployed [[Knots|Knot]].

## How does it work?
Once the HTTP request from the browser comes to the Knot.x, it goes to the **Server** verticle.
Server performs following actions when receives HTTP request:

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

Default configuration shipped with the verticle as `io.knotx.KnotxServer.json` file available in classpath.
```json
{
  "main": "io.knotx.server.KnotxServerVerticle",
  "options": {
    "config": {
      "serverOptions": {
        "port": 8092,
        "keyStoreOptions": {}
      },
      "displayExceptionDetails": true,
      "customResponseHeader": {
        "name": "X-Server",
        "value": "Knot.x"
      },
      "allowedResponseHeaders": [
        "Access-Control-Allow-Origin",
        "Allow",
        "Cache-Control",
        "Content-Disposition",
        "Content-Encoding",
        "Content-Language",
        "Content-Location",
        "Content-MD5",
        "Content-Range",
        "Content-Type",
        "Content-Length",
        "Content-Security-Policy",
        "Date",
        "Edge-Control",
        "ETag",
        "Expires",
        "Last-Modified",
        "Location",
        "Pragma",
        "Proxy-Authenticate",
        "Server",
        "Set-Cookie",
        "Status",
        "Surrogate-Control",
        "Vary",
        "Via",
        "X-Frame-Options",
        "X-XSS-Protection",
        "X-Content-Type-Options",
        "X-UA-Compatible",
        "X-Request-ID",
        "X-Server"
      ],
      "defaultFlow": {
        "repositories": [
          {
            "path": "/content/local/.*",
            "address": "knotx.core.repository.filesystem"
          },
          {
            "path": "/content/.*",
            "address": "knotx.core.repository.http"
          }
        ],
        "splitter": {
          "address": "knotx.core.splitter"
        },
        "routing": {
          "GET": [
            {
              "path": ".*",
              "address": "knotx.knot.service",
              "onTransition": {
                "next": {
                  "address": "knotx.knot.handlebars"
                }
              }
            }
          ]
        },
        "assembler": {
          "address": "knotx.core.assembler"
        }
      }
    }
  }
}
```
In short, by default, server does:
- Listens on port 8092
- Displays exception details on error pages (for development purposes)
- Returns certain headers in Http Response to the client (as shown above)
- Always sets custom header in response **X-Server:Knot.x**
- Uses the [[default Knot.X routing mechanism|KnotRouting]]
- Communicates with two types of repositories: HTTP and Filesystem
- Uses core [[Splitter|Splitter]]
- Each GET request for any resource (`.*`) is routed through [[Service Knot|ServiceKnot]] and then [[Handlebars rendering engine|HandlebarsKnot]]

Detailed description of each configuration option that's available is described in next section.

## Server options
Main server options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `fileUploadDirectory`       | `String`                            |                | Uploads directory on server for file uploads - used during POST, PUT request methods. **file-uploads** if not set.|
| `fileUploadLimit`           | `Long`                              |                | Limits the size of file that can be uploaded to Knot.x using POST or PUT request methods. Default is **unlimited**.|
| `displayExceptionDetails`   | `Boolean`                           |                | (Debuging only) Displays exception stacktrace on error page. **False** if not set.|
| `customResponseHeader`      | `KnotxServerCustomHeader`           |                | Sets the custom header in each response from Knot.x to the client. Default value is **X-Server:Knot.x** |
| `allowedResponseHeaders`    | `Array of String`                   |                | Array of HTTP headers that are allowed to be send in response. **No** response headers are allowed if not set. |
| `csrf`                      | `KnotxCSRFConfiguration`            |                | Configuration of the CSRF tokens |
| `defaultFlow`               | `KnotxFlowConfiguration`            | &#10004;       | Configuration of [[default Knot.X routing|KnotRouting]] |
| `customFlow`                | `KnotxFlowConfiguration`            |                | Configuration of [[Gateway Mode|GatewayMode]] |

### KnotxServerCustomHeader options
 Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `name`      | `String`  | &#10004;       | Name of the response header. |
| `value`   | `String`  | &#10004;       | Value of the response header. |

### KnotxCSRFConfiguration options
 Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `secret`       | `String`  |       | Server secret to sign the token. Knot.x by default sets it's own secret. |
| `cookieName`   | `String`  |       | Set the cookie name. By default `XSRF-TOKEN` is used as it is the expected name by AngularJS however other frameworks might use other names. |
| `cookiePath`   | `String`  |       | Set the cookie path. By default `/` is used. |
| `headerName`   | `String`  |       | Set the header name. By default `X-XSRF-TOKEN` is used as it is the expected name by AngularJS however other frameworks might use other names. |
| `timeout`      | `long`    |       | Set the timeout for tokens generated by the handler, by default is `1800000`ms - `30 min` |


### KnotxFlowConfiguration options

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `repositories`              | `Array of RepositoryEntry`          | &#10004;       | Array of repositories configurations |
| `splitter`                  | `VerticleEntry`                     | &#10004;       | **Splitter** communication options |
| `assembler`                 | `VerticleEntry`                     | &#10004;       | **Assembler** communication options |
| `routing`                   | `Object of Method to RoutingEntry`  | &#10004;       | Set of HTTP method based routing entries, describing communication between **Knots**<br/>`"routing": {"GET": {}, "POST": {}}` |

The `repositories`, `splitter` and `assembler` verticles are specific to the default Knot.X processing flow.

### RepositoryEntry options

| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `path`      | `String`  | &#10004;       | Regular expression of the HTTP Request path |
| `csrf`      | `Boolean` |                | Enables CSRF Token generation (on **GET**) /validation (**POST/PUT/PATCH/DELETE**). Default value is `false` meaning the CSRF is disabled in this route.
| `address`   | `String`  | &#10004;       | Event bus address of the **Repository Connector** modules, that should deliver content for the requested path matching the regexp in `path` |

### VerticleEntry options

| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `address`  | `String`  | &#10004;       | Sets the event bus address of the verticle |

### RoutingEntry options
| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `path`           | `String`                               | &#10004;       | Regular expression of HTTP Request path |
| `address`        | `String`                               | &#10004;       | Event bus address of the **Knot** verticle, that should process the message, for the requested path matching the regexp in `path` |
| `onTransition`   | `Object of Strings to TransitionEntry` |        | Describes routing to addresses of other Knots based on the transition trigger returned from current Knot.<br/> `"onTransition": { "go-a": {}, "go-b": {} }` |

### KnotRouteEntry options
| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `address`      | `String`         | &#10004;       | Event bus address of the **Knot** verticle |
| `onTransition` | `KnotRouteEntry` |        | Describes routing to addresses of other Knots based on the transition trigger returned from current Knot.<br/>`"onTransition": { "go-d": {}, "go-e": {} }` |

### Vert.x HTTP Server configurations

Besides Knot.x specific configurations as mentioned above, the `config` field might have added Vert.x configurations related to the HTTP server.
It can be used to control the low level aspects of the HTTP server, server tuning, SSL.

The `serverOptions` need to be added in the following place, of the KnotsServerVerticle configuration
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
The list of remaining server options are described on the [Vert.x DataObjects page](http://vertx.io/docs/vertx-core/dataobjects.html#HttpServerOptions).

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

Other option is to provide those parameters through JVM properties:
- `-Dio.knotx.KnotxServer.options.config.serverOptions.keyStoreOptions.path=/path/to/keystore.jks` 
- `-Dio.knotx.KnotxServer.options.config.serverOptions.keyStoreOptions.password=keyPass`
- `-Dio.knotx.KnotxServer.options.config.serverOptions.ssl=true`

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
  "main": "io.knotx.server.KnotxServerVerticle",
  "options": {
    "config": {
      "httpPort": 8092,
      "displayExceptionDetails": true,
      "deliveryOptions": {
        "timeout": 15000
      },
      ...
    }
  }
}
```
