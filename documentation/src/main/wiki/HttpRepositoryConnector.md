# HTTP Repository Connector

Http Repository Connector allows to fetch templates from an external repository via HTTP protocol. 

## How does it work?
The diagram below depicts Knot.x modules and request flow in more details.

[[assets/knotx-http-repository.png|alt=Http Repository Connector]]

## How to configure?
Http Repository Connector is deployed as a separate Verticle, depending on how it's deployed. 
You need to supply **Http Repository Connector** configuration as JSON below if deployed as Http Repository Connector Verticle fat jar.
```json
"address": "knotx.core.repository.http",
"client.options": {
  "maxPoolSize": 1000,
  "keepAlive": false,
  "tryUseCompression": true
},
"client.destination" : {
  "domain": "localhost",
  "port": 3001
}
```
Or, above configuration wrapped in the JSON `config` section as shown below, if deployed using Knot.x starter verticle.
```json
  "verticles": {
    ...,
    "com.cognifide.knotx.repository.HttpRepositoryConnectorVerticle": {
      "config": {
         "PUT YOUR CONFIG HERE"
      }
    },
    ...,
  }
```

Detailed description of each configuration option is described in next section.

### Options
Main options available.

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `address`                   | `String`                            | &#10004;       | Event Bus address of Http Repository Connector Verticle |
| `client.options`            | `HttpClientOptions`                 | &#10004;       | HTTP Client options used when communicating with the destination repository. See [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) to get all options supported.|
| `client.destination`        | `JsonObject`                        | &#10004;       | Allows to specify **domain** and **port** of the HTTP Repository endpoint |

### Destination options

| Name  | Type  | Mandatory | Description  |
|-------:|:-------:|:-------:  |-------|
| `domain`      | `String`  | &#10004;       | Http Repository domain / IP |
| `port`        | `Number`  | &#10004;       | Http Repository port number |