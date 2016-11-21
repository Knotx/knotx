# Repository
Knot.x gets templates from one or more repositories, processes them and serves to end users.

## How does it work?
First it is important to understand what Repository is. Repositories are not part of Knot.x itself, 
they are stores where templates live. The diagram below depicts how Knot.x uses repositories.

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

Knot.x supports by default two repository types: HTTP Repository and Filesystem Repository. Both 
[[HTTP Repository|HttpRepositoryConnector]] and [[Filesystem Repository|FilesystemRepositoryConnector]] connectors 
handle template requests using the Vertx Event Bus. This
communication model allows to add custom repositories connectors easily. For more information see sections:
* [[HTTP Repository Connector|HttpRepositoryConnector]]
* [[Filesystem Repository Connector|FilesystemRepositoryConnector]]


