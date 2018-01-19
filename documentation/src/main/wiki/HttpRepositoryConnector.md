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
        "setIdleTimeout": 600,
        "tryUseCompression": true
      },
      "clientDestination": {
        "scheme": "http",
        "domain": "localhost",
        "port": 3001,
        "hostHeader": "localhost"
      },
      "customRequestHeader": {
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
| `clientDestination`         | `JsonObject`                        | &#10004;       | Allows to specify HTTP repository connection details using **scheme**, **domain**, **port** values (<scheme>://<domain>:<port>). Additionally, it's possible to specify override of the host header - **hostHeader** field |
| `customRequestHeader`       | `JsonObject`                        |                | Allows to specify header **name** and its **value**. The header will be send in each request to the configured services. |

### Destination options

| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `scheme`      | `String`  | &#10004;       | Scheme of the connection. Allowed values: **http**, **https** (<scheme>://)|
| `domain`      | `String`  | &#10004;       | Http Repository domain / IP (<scheme>://<domain>) |
| `port`        | `Number`  | &#10004;       | Http Repository port number (<scheme>://<domain>:<port>) |
| `hostHeader`  | `String`  |                | Override of the host header used in that communication. If set, this is the value that will be effectively send. |


## How to configure SSL connection to the repository
- Set up `clientDestination` options with a proper scheme **https**
- ClientOptions consists set of parameters that you might need to set up depending on your needs:
  - `forceSni` - true -> It will force SSL SNI (Server Name Indication). The SNI will be set to the same value as Host header (set in `clientDestination`)
  - `trustAll` - true/false - weather all server certificates should be trusted or not
  - `verifyHost` - true/false - hostname verification
  - `trustStoreOptions` - if you want to put the server certificates here in order to trust only specific ones - see [Vert.x Http Client Options](http://vertx.io/docs/vertx-core/dataobjects.html#HttpClientOptions) for details
  
E.g.
```json
"clientOptions": {
  "forceSni": true,
  "trustAll": true,
  "verifyHost": false
},
"clientDestination": {
  "scheme": "https",
  "domain": "my.internal.repo.domain",
  "port": 443,
  "hostHeader": "specific.repo.resolution.domain"
}
```
