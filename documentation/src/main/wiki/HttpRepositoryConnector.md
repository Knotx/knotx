# HTTP Repository Connector

Http Repository Connector allows to fetch templates from an external repository via HTTP protocol. 

## How does it work?
The diagram below depicts Knot.x modules and request flow in more details.

[[assets/knotx-http-repository.png|alt=Http Repository Connector]]

## How to configure?
See the [HttpRepositoryOptions](https://github.com/Cognifide/knotx/blob/master/documentation/src/main/cheatsheet/cheatsheets.adoc#httprepositoryoptions) for all configuration options and its defaults.

In general, it:
- Listens on event bus address `knotx.core.repository.http` for requests to the repository
- It uses certain HTTP Client options while communicating with the remote repository
- It defines destination of the remote repository
- And specifies certain request headers from client request that are being passed to the remote repository

## How to configure SSL connection to the repository
- Set up `clientDestination` options with a proper scheme **https**
- `ClientOptions` consists set of parameters that you might need to set up depending on your needs:
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
