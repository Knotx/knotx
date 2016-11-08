# Server

Server is essentially a "heart", a main [Verticle](http://vertx.io/docs/vertx-core/java/#_verticles) of Knot.x.
It creates HTTP Server, listening for browser requests, and is responsible for coordination of communication between [[Repository|Repository]], [[Splitter|Splitter]] and all deployed [[Knots|Knot]].

## How it works
[TBD Description]
[[assets/knotx-server.png|alt=Knot.x Server How it Works flow diagram]]

## Configuration
Configuration of the Server is being supplied in Knot.x configuration JSON as for other verticles. The `config` section looks like the one below
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

