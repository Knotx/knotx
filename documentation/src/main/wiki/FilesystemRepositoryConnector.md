# Filesystem Repository Connector section

Filesystem Repository Connector allows to fetch templates from local file storage. 

## How does it work?
The diagram below depicts Knot.x modules and request flow in more details.

[[assets/knotx-filesystem-repository.png|alt=Http Repository Connector]]

## How to configure?
Filesystem Repository Connector is deployed as a separate Verticle, depending on how it's deployed. 
You need to supply **Filesystem Repository Connector** configuration as JSON below if deployed as Filesystem Repository Connector Verticle fat jar.
```json
"address": "knotx.core.repository.filesystem",
"catalogue": ""
```
Or, above configuration wrapped in the JSON `config` section as shown below, if deployed using Knot.x starter verticle.
```json
  "verticles": {
    ...,
    "com.cognifide.knotx.repository.FilesystemRepositoryConnectorVerticle": {
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
| `address`                   | `String`                            | &#10004;       | Event Bus address of Filesystem Repository Connector Verticle |
| `catalogue`                 | `String`                            |                | it determines where to take the resources from. If it's left empty, they will be taken from the classpath. It may be treated like a prefix to the requested resources. |