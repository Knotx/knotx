# Http Service Adapter

Http Service Adapter is an example of Adapter implementation embedded in Knot.x.
It enables communication between [[View Knot|ViewKnot]] and external services via HTTP.

## Example configuration
Http Service Adapter can be configured to support multiple external `services`, see example configuration:

```json
{
  "address": "knotx.adapter.service.http",
  "client.options": {
    "maxPoolSize": 1000,
    "keepAlive": false
  },
  "services": [
    {
      "path": "/service/mock/.*",
      "domain": "localhost",
      "port": 3000,
      "allowed.request.headers": [
        "Content-Type",
        "X-*"
      ]
    },
    {
      "path": "/service/.*",
      "domain": "localhost",
      "port": 8080,
      "allowed.request.headers": [
        "Content-Type",
        "X-*"
      ]
    }
  ]
}

```

- `address` is the where adapter listen for events at Event Bus. Every event that will be sent at `knotx.adapter.service.http`
will be processed by Http Service Adapter.
- `client.options` are [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) used to configure HTTP connection. 
Any HttpClientOption may be defined in this section, at this example two options are defined: 
  - `maxPoolSize` -  maximum pool size for simultaneous connections,
  - `keepAlive` - that shows keep alive should be disabled on the client.
- `services` - an JSON array of services that Http Service Adapter can connect to. Each service is distinguished by `path` parameter which is regex.
In example above, two services are configured:
  - `/service/mock/.*` that will call `http://localhost:3000` domain with defined [path](#service-path),
  - `/service/.*` that will call `http://localhost:8080` domain with defined [path](#service-path).
  
## Adapter Request
When Http Service Adapter starts processing a message from Event Bus, it expects following input:
- `clientRequest` - JSON object that contains original client request (contains e.g. headers, params, formAttributes etc.).
- `params` - JSON object that contains additional parameters, among those parameter mandatory `path` parameter should be defined.

### Service path
`path` parameter is a mandatory parameter that must be passed to Http Service Adapter. 
It defines request path and may contain [placeholders](#parametrized-service-calls).

### Parametrized services calls
When found a placeholder within the `path` parameter it will be replaced with a dynamic value based on the current http request (data from `clientRequest`).
Available placeholders are:
* `{header.x}` - is the original requests header value where `x` is the header name
* `{param.x}` - is the original requests query parameter value. For `x` = q from `/a/b/c.html?q=knot` it will produce `knot`
* `{uri.path}` - is the original requests sling path. From `/a/b/c.sel.it.html/suffix.html?query` it will produce `/a/b/c.sel.it.html/suffix.html`
* `{uri.pathpart[x]}` - is the original requests `x`th sling path part. For `x` = 2 from `/a/b/c.sel.it.html/suffix.html?query` it will produce `c.sel.it.html`
* `{uri.extension}` - is the original requests sling extension. From `/a/b/c.sel.it.html/suffix.xml?query` it will produce `xml`
* `{slingUri.path}` - is the original requests sling path. From `/a/b/c.sel.it.html/suffix.html?query` it will produce `/a/b/c`
* `{slingUri.pathpart[x]}` - is the original requests `x`th sling path part. For `x` = 1 from `/a/b/c.sel.it.html/suffix.html?query` it will produce `b`
* `{slingUri.selectorstring}` - is the original requests sling selector string. From `/a/b/c.sel.it.html/suffix.html?query` it will produce `sel.it`
* `{slingUri.selector[x]}` - is the original requests `x`th sling selector. For `x` = 1 from `/a/b/c.sel.it.html/suffix.html?query` it will produce `it`
* `{slingUri.extension}` - is the original requests sling extension. From `/a/b/c.sel.it.html/suffix.xml?query` it will produce `html`
* `{slingUri.suffix}` - is the original requests sling suffix. From `/a/b/c.sel.it.html/suffix.html?query` it will produce `/suffix.html`

All placeholders are always substituted with encoded values according to the RFC standard. However, there are two exceptions:

- Space character is substituted by `%20` instead of `+`.
- Slash character `/` remains as it is.

## Adapter Response
Http Service Adapter replies with `ClientResponse` that contains:

| Parameter       | Type                      |  Description  |
|-------:         |:-------:                  |-------|
| `statusCode`    | `Number`                  | status code of a response from external service (e.g. `200`) |
| `headers`       | `MultiMap`                | external service response headers |
| `body`          | `Buffer`                  | external service response, **please notice that it is expected, tha form of a response body from an external service is JSON** |

## Example

Assuming, that Http Service Adapter was configured as presented in [Example configuration](#example-configuration) and:

#### View Knot configuration
Example configuration of a [[View Knot|ViewKnot]]:
```json
"com.cognifide.knotx.knot.view.ViewKnotVerticle": {
  "config": {
    "address": "knotx.knot.view",
    "template.debug": true,
    "client.options": {
      "maxPoolSize": 1000,
      "keepAlive": false
    },
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
      }
    ]
  }
}
```

#### snippet
Example html snippet in template:

```html
<script data-api-type="templating" type="text/x-handlebars-template"
    data-service-search="search"
    data-service-twitter="twitter">
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

#### search service
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

Http Service Adapter will lookup if `params.path` is supported and 2nd service from [Example configuration](#example-configuration) `services` will be a match.
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

#### twitter service
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

Http Service Adapter will lookup if `params.path` is supported and 2nd service from [Example configuration](#example-configuration) `services` will be a match.
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
