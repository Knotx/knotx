[WORKING IN PROGRESS]
# Repository

##Overview
Knot.x gets templates from one or more repositories, processes them and serves to end users. Repositories
are not part of Knot.x itself, they are stores where templates live. The diagram below depicts how Knot.x uses
repositories.
 
[[assets/knotx-overview.png|alt=Knot.x Overview]]

Mapping between incoming request and repository is defined in a Server configuration section. It specifies
which requests should go to which repository address.

```json
[
  {
    "path": "/content/local/.*",
    "address": "knotx.core.repository.filesystem"
  },
  {
    "path": "/content/.*",
    "address": "knotx.core.repository.http"
  }
]
```

## Repositories Connectors
Knot.x supports by default two repository types: HTTP repository and Filesystem repository. Both Http 
Repository and Filesystem Repository connectors handle template requests using the Vertx Event Bus. This 
communication model allows to add custom repositories connectors easily.

### HTTP Repository Connector section

Http Repository Connector allows to fetch templates from an external repository via HTTP protocol. The diagram below
depicts Knot.x modules and request flow in more details.

[[assets/knotx-http-repository.png|alt=Http Repository Connector]]

Http Repository Connector configuration options:
```json
{
  "com.cognifide.knotx.repository.HttpRepositoryVerticle": {
    "config": {
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
    }
  },
  ...
}
```

The config node consists of:

- **address** - the event bus address on which Http Repository Connector listens for template requests.
- **client.options** - HTTP Client options used when communicating with the destination repository. See [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) to get all options supported.
- **client.destination** - Allows to specify **domain** and **port** of the HTTP repository endpoint.

### Filesystem Repository Connector section

Filesystem Repository Connector allows to fetch templates from local file storage. The diagram below
depicts Knot.x modules and request flow in more details.

[[assets/knotx-filesystem-repository.png|alt=Http Repository Connector]]


Filesystem Repository Connector configuration options:
```json
{
  "com.cognifide.knotx.repository.FilesystemRepositoryVerticle": {
    "config": {
      "address": "knotx.core.repository.filesystem",
      "catalogue": ""
    }
  },
  ...
}
```
If you need to take files from a local machine, this is the kind of repository you want to use. It's perfect for mocking data.
The config node consists of:

- **address** - the event bus address on which file system Repository Connector listens for template requests.
- **catalogue** - it determines where to take the resources from. If it's left empty, they will be taken from the classpath. It may be treated like a prefix to the requested resources.
