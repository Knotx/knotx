# Http Service Adapter
Http Service Adapter is an example of Adapter implementation embedded in Knot.x.
It enables communication between [[Service Knot|ServiceKnot]] and external services via HTTP.

## How does it work?
When Http Service Adapter starts processing a message from Event Bus, it expects following input:
- `clientRequest` - JSON object that contains client request (contains e.g. headers, params, formAttributes etc.).
- `params` - JSON object that contains additional parameters, among those parameter mandatory
[`path`](#service-path) parameter should be defined, enables passing additional
[query params and headers](#service-params-and-additional-headers).

### Service path
`path` parameter is a mandatory parameter that must be passed to Http Service Adapter.
It defines request path and may contain [placeholders](#parametrized-services-calls).

### Service params and additional headers
It is possible to pass additional query parameters and headers that Http Service Adapter will send
to external service.
- `queryParams` - JSON object that contains parameters passed in query.
- `headers` - JSON object that contains headers. Those headers will *overwrite* existing values.

### Parametrized services calls
When found a placeholder within the `path` parameter it will be replaced with a dynamic value based on the
current http request (data from `clientRequest`). Available placeholders are:
* `{header.x}` - is the client requests header value where `x` is the header name
* `{param.x}` - is the client requests query parameter value. For `x` = q from `/a/b/c.html?q=knot` it will produce `knot`
* `{uri.path}` - is the client requests sling path. From `/a/b/c.sel.it.html/suffix.html?query` it will produce `/a/b/c.sel.it.html/suffix.html`
* `{uri.pathpart[x]}` - is the client requests `x`th sling path part. For `x` = 2 from `/a/b/c.sel.it.html/suffix.html?query` it will produce `c.sel.it.html`
* `{uri.extension}` - is the client requests sling extension. From `/a/b/c.sel.it.html/suffix.xml?query` it will produce `xml`
* `{slingUri.path}` - is the client requests sling path. From `/a/b/c.sel.it.html/suffix.html?query` it will produce `/a/b/c`
* `{slingUri.pathpart[x]}` - is the client requests `x`th sling path part. For `x` = 1 from `/a/b/c.sel.it.html/suffix.html?query` it will produce `b`
* `{slingUri.selectorstring}` - is the client requests sling selector string. From `/a/b/c.sel.it.html/suffix.html?query` it will produce `sel.it`
* `{slingUri.selector[x]}` - is the client requests `x`th sling selector. For `x` = 1 from `/a/b/c.sel.it.html/suffix.html?query` it will produce `it`
* `{slingUri.extension}` - is the client requests sling extension. From `/a/b/c.sel.it.html/suffix.xml?query` it will produce `html`
* `{slingUri.suffix}` - is the client requests sling suffix. From `/a/b/c.sel.it.html/suffix.html?query` it will produce `/suffix.html`

All placeholders are always substituted with encoded values according to the RFC standard. However, there are two exceptions:

- Space character is substituted by `%20` instead of `+`.
- Slash character `/` remains as it is.

### Adapter Response
Http Service Adapter replies with `ClientResponse` that contains:

| Parameter       | Type                      |  Description  |
|-------:         |:-------:                  |-------|
| `statusCode`    | `HttpResponseStatus`       | status code of a response from external service (e.g. `200 OK`) |
| `headers`       | `MultiMap`                | external service response headers |
| `body`          | `Buffer`                  | external service response, **please notice that it is expected, tha form of a response body from an external service is JSON** |

## How to configure?
For all configuration fields and their defaults consult [ServiceAdapterOptions](https://github.com/Cognifide/knotx/blob/master/documentation/src/main/cheatsheet/cheatsheets.adoc#serviceadapteroptions)

In general, the default configuration covers:
- `address` is the where adapter listen for events at Event Bus. Every event that will be sent at `knotx.adapter.service.http`
will be processed by Http Service Adapter.
- `clientOptions` are [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) used to configure HTTP connection.
Any HttpClientOption may be defined in this section, at this example two options are defined:
  - `maxPoolSize` -  maximum pool size for simultaneous connections,
  - `setIdleTimeout` - any connections not used within this timeout will be closed, set in seconds,
  - `keepAlive` - that shows keep alive, we recommend to leave it set to `true` as the default value in Vert.x. You can find more information [here](http://vertx.io/docs/vertx-core/java/#_http_1_x_pooling_and_keep_alive).
- `customHttpHeader` - an JSON object that consists of name and value of the header to be sent in each request to any service configured. If the same header comes from the client request, it will be always overwritten with the value configured here.
- `services` - an JSON array of services that Http Service Adapter can connect to. Each service is distinguished by `path` parameter which is regex.
In example above, two services are configured:
  - `/service/mock/.*` that will call `http://localhost:3000` domain with defined [path](#service-path),
  - `/service/.*` that will call `http://localhost:8080` domain with defined [path](#service-path).

#### Service Knot configuration
Example configuration of a [[Service Knot|ServiceKnot]]:
```json
  "config": {
    "address": "knotx.knot.service",
    "services": [
      {
        "name" : "search",
        "address" : "knotx.adapter.service.http",
        "params": {
          "path": "/service/solr/search?q={param.q}"
        }
      },
      {
        "name" : "twitter",
        "address" : "knotx.adapter.service.http",
        "params": {
          "path": "/service/twitter/user/{header.userId}"
        }
      },
      {
        "name" : "javabooks",
        "address" : "knotx.adapter.service.http",
        "params": {
          "path": "/books/v1/volumes",
          "queryParams": {
              "q": "java"
          },
          "headers": {
            "token": "knotx-request"
          }
        }
      }
    ]
  }
```

#### snippet
Example html snippet in template:

```html
<script data-knotx-knots="services,handlebars" type="text/knotx-snippet"
    data-knotx-service-search="search"
    data-knotx-service-twitter="twitter">
        <h1>Welcome</h1>
        <h2>{{search.numberOfResults}}</h2>
        <h2>{{twitter.userName}}</h2>
</script>
```

#### request

- `path`: http://knotx.example.cognifide.com/search?q=hello
- `headers`: `[userId=johnDoe]`.

### Processing
When Knot.x resolves this request, Http Service Adapter will be called twice when example snipped is processed:

##### search service
Http Service Adapter request parameters should look like:

```json
{
  "clientRequest": {
    "path": "http://knotx.example.cognifide.com/search?q=hello",
    "headers": {
      "userId": "johnDoe"
    },
    "params": {
      "q": "hello"
    },
    "method": "GET"
  },
  "params": {
    "path": "/service/solr/search?q={param.q}"
  }
}
```

Http Service Adapter will lookup if `params.path` is supported and 2nd service from [Example configuration](#how-to-configure) `services` will be a match.
Next, `params.path` placeholders are resolved, and request to `http://localhost:8080/service/solr/search?q=hello` is made.
In response, external service returns:

```json
{
  "numberOfResults": 2,
  "documents": [
    {"title": "first"},
    {"title": "second"}
  ]
}
```

which is finally wrapped into [Adapter Response](#adapter-response).

##### twitter service
Http Service Adapter request parameters should look like:

```json
{
  "clientRequest": {
    "path": "http://knotx.example.cognifide.com/search?q=hello",
    "headers": {
      "userId": "johnDoe"
    },
    "params": {
      "q": "hello"
    },
    "method": "GET"
  },
  "params": {
    "path": "/service/twitter/user/{header.userId}"
  }
}
```

Http Service Adapter will lookup if `params.path` is supported and 2nd service from [Example configuration](#how-to-configure) `services` will be a match.
Next, `params.path` placeholders are resolved, and request to `http://localhost:8080/service/twitter/user/johnDoe` is made.
In response, external service returns:

```json
{
  "userName": "John Doe",
  "userId": "1203192031",
  "lastTweet": "27.10.2016"
}
```

which is finally wrapped into [Adapter Response](#adapter-response).

##### Setting service query parameters
We can use the `queryParams` JSON object to define the service query parameters and their values directly from template. Consider the following service configuration:

```json
  "config": {
    "address": "knotx.knot.service",
    "services": [
      {
        "name" : "products",
        "address" : "knotx.adapter.service.http",
        "params": {
          "path": "/service/products/"
        }
      }
    ]
  }
```

We can set query parameters sent to the service using the following snippet:

```html
<script data-knotx-knots="services,handlebars" type="text/knotx-snippet"
    data-knotx-service="products"
    data-knotx-params='{"queryParams":{"amount":"4"}}'>
        <h1>Products</h1>
        {{#each _result.products}}
        <p>{{productName}}</p>
        {{/each}}
</script>
```

This way, you can modify the request parameters being sent to the external service, without re-starting Knot.X, just by updating the template.
In this example, the request would be `/service/products?amount=4`

Please note that Knot.X caches templates fetched by the [[Filesystem Repository Connector|FilesystemRepositoryConnector]].
As a result, the "hot-swap" mechanism described above might not work with templates stored in local repositories.

You can also set the `queryParams` from the configuration file by amending the snippet presented above:
```json
  "config": {
    "address": "knotx.knot.service",
    "services": [
      {
        "name" : "products",
        "address" : "knotx.adapter.service.http",
        "params": {
          "path": "/service/products/",
          "queryParams": {
            "amount": "4"
          }
        }
      }
    ]
  }
```
Bear in mind that a Knot.X restart is needed in order to apply the service configuration, as the configuration is loaded on startup.

This mechanism can be also used simultaneously with the `path` property being parametrized by placeholders. Take into consideration, however, that placeholder values can only be resolved based on the `ClientRequest` (current http request), and not the `queryParams` value.

Please note that if the `queryParams` are defined both in the configuration file and in the template, the parameters from the template will override the configuration.
