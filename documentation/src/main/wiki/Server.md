# Server

Server is essentially a "heart", a main [Verticle](http://vertx.io/docs/vertx-core/java/#_verticles) of Knot.x.
It creates HTTP Server, listening for browser requests, and is responsible for coordination of communication between [[Repository|Repository]], [[Splitter|Splitter]] and all deployed [[Knots|Knot]].

## How does it work?
Once the HTTP request from the browser come to the Knot.x, it comes to the **Server** verticle. The server does:

- Verifies if request **method** is configured in `routing` (see config below), and sends **Method Not Allowed** response if not matches
- Search for the **repository** address in `repositories` configuration, by matching the requested path with the regexp from config, and sends **Not Found** response if none is matched.
- Calls the matching **repository** address with the original request
- Calls the **splitter** address with the template got from **repository**
- Builds [[KnotContext|Knot]] communication model (that consists of original request, response from repository & split HTML fragments)
- Calls **[[Knots|Knot]]** according to the [[routing||#_Routing]] configuration, with the **KnotContext**
- Once the last Knot returns processed **KnotContext**, server creates HTTP Response based on data from the KnotContext
- Filters the response headers according to the `allowed.response.headers` configuration and returns to the browser.

The diagram below depicts flow of data coordinated by the **Server** based on the hypothetical configuration of routing (as described in next section).
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
Server is deployed as a separate Verticle, depending on how it's deployed. You need to supply **Server** configuration as JSON below if deployed as Server Verticle fat jar.
```json
{
  "http.port": 8080,
  "allowed.response.headers": [ "Content-Type", "Content-Length", "Location", "Set-Cookie" ],
  "repositories" : [
    {
      "path": "/content/local/.*\.html",
      "address": "filesystem.repository.eventbus.address"
    },
    {
      "path": "/content/.*\.html",
      "address": "http.repository.eventbus.address"
    }
  ],
  "splitter": {
    "address" : "splitter.eventbus.address"
  },
  "routing": {
    "GET": {
      "path": "/.*\.html",
      "address": "first.knot.eventbus.address",
      "onTransition": {
        "go-second": {
          "address": "second.knot.eventbus.address",
          "onTransition": {
            "go-third": {
              "address": "third.knots.eventbus.address"
            }
          }
        },
        "go-alt": {
          "address": "alternate.knot.eventbus.address"
        }
      }
    },
    "POST": {
      "path": "/.*\.html",
      "address": "first.knot.eventbus.address"
    }
  }
}
```

Or, above configuration wrapped in the JSON `config` section as shown below, if deployed using Knot.x starter verticle.
```json
  "verticles": {
    ...,
    "com.cognifide.knotx.server.KnotxServerVerticle": {
      "config": {
         "PUT YOUR CONFIG HERE"
      }
    },
    ...,
  }
```

Detailed description of each configuration option is described in next section.

## Server options
Main server options available.

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `http.port`                 | `Number (int)`                      | &#10004;       | HTTP Port on which Knot.x will listen for browser requests |
| `displayExceptionDetails`   | `Boolean`                           |         | (Debuging only) Displays exception stacktrace on error page. **False** if not set.|
| `allowed.response.headers`  | `Array of String`                   |         | Array of HTTP headers that are allowed to be send in response. **No** response headers are allowed if not set. |
| `repositories`              | `Array of RepositoryEntry`          | &#10004;       | Array of repositories configurations |
| `splitter`                  | `SplitterEntry`                     | &#10004;       | **Splitter** communication options |
| `routing`                   | `Object of Method to RoutingEntry`  | &#10004;       | Set of HTTP method based routing entries, describing communication between **Knots**<br/>`"routing": {"GET": {}, "POST": {}}` |

### RepositoryEntry options

| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `path`      | `String`  | &#10004;       | Regular expression of the HTTP Request path |
| `address`   | `String`  | &#10004;       | Event bus address of the **Repository|Repository** verticle, that should deliver content for the requested path matching the regexp in `path` |

### SplitterEntry options

| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `address`  | `String`  | &#10004;       | Sets the event bus address of the **Splitter** verticle |

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

