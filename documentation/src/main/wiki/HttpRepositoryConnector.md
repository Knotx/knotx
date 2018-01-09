# HTTP Repository Connector

Http Repository Connector allows to fetch templates from an external repository via HTTP protocol. 

## How does it work?
The diagram below depicts Knot.x modules and request flow in more details.

[[assets/knotx-http-repository.png|alt=Http Repository Connector]]

## How to configure?
Http Repository Connector is deployed using Vert.x service factory as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default configuration.

Default configuration shipped with the verticle as `io.knotx.HttpRepositoryConnector.json` file available in classpath.

```json
{
  "main": "io.knotx.repository.HttpRepositoryConnectorVerticle",
  "options": {
    "config": {
      "address": "knotx.core.repository.http",
      "clientOptions": {
        "maxPoolSize": 1000,
        "keepAlive": false,
        "tryUseCompression": true
      },
      "clientDestination": {
        "domain": "localhost",
        "port": 3001
      },
      "customHttpHeader": {
        "name": "Server-User-Agent",
        "value": "Knot.x"
      },
      "allowedRequestHeaders": [
        "Accept*",
        "Authorization",
        "Connection",
        "Cookie",
        "Date",
        "Edge*",
        "Host",
        "If*",
        "Origin",
        "Pragma",
        "Proxy-Authorization",
        "Surrogate*",
        "User-Agent",
        "Via",
        "X-*"
      ]
    }
  }
}

```
In general, it:
- Listens on event bus address `knotx.core.repository.http` for requests to the repository
- It uses certain HTTP Client options while communicating with the remote repository
- It defines destination of the remote repository
- And specifies certain request headers from client request that are being passed to the remote repository

Detailed description of each configuration option is described in next section.

### Options
Main options available.

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `address`                   | `String`                            | &#10004;       | Event Bus address of Http Repository Connector Verticle |
| `clientOptions`             | `HttpClientOptions`                 | &#10004;       | HTTP Client options used when communicating with the destination repository. See [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) to get all options supported.|
| `clientDestination`         | `JsonObject`                        | &#10004;       | Allows to specify **domain** and **port** of the HTTP Repository endpoint |
| `customHttpHeader`       | `JsonObject`                        |                | Allows to specify header **name** and its **value**. The header will be send in each request to the configured services. |

### Destination options

| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `domain`      | `String`  | &#10004;       | Http Repository domain / IP |
| `port`        | `Number`  | &#10004;       | Http Repository port number |
