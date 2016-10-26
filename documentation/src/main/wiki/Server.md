#### 1.4. HTTP Server section
```json
{
  ...
  "com.cognifide.knotx.server.KnotxServerVerticle": {
    "config": {
      "http.port": 8092,
      "allowed.response.headers": [
        "User-Agent",
        "X-Solr-Core-Key",
        "X-Language-Code",
        "X-Requested-With"
      ],
      "repositories": [
        {
          "path" : "/content/local/.*",
          "address" : "knotx.core.repository.filesystem"
        },
        {
          "path" : "/content/.*",
          "address" : "knotx.core.repository.http"
        }
      ],
      "engine" : {
        "address": "knotx.knot.view"
      }
    }
  },
  ...
}
```
This section configures the Knot.x HTTP server. The config node consists of:

- **http.port** - an HTTP port on which the server listens for requests,
- **allowed.response.headers** - list of the headers that should be passed back to the client
- **repositories** - configuration of repositories. It's a array of mappings what paths are supported by what repository verticles (by specifing its event bus addresses). The order of mappings is important as they are evaluated from top to down on each request. The first one matched will handle the request or, if no repository is matched, **Knot.x** will return a `404 Not found` response for the given request.
- **engine** - configuration about view engine dependency. You can configure event bus **address** of engine verticle here.