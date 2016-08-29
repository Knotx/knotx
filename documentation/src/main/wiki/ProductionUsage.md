# Production

The example module is provided for testing purposes. Only the core module should be deployed in a production environment. (The Knot.x application runs as a single verticle without any dependencies).

### Executing

To run it, execute the following command:

```
java -jar knotx-core-XXX.jar -Dservice.configuration=<path to your service.yml> -Drepository.configuration=<path to your repository.yml>
```

This will run the server with production settings. For more information see the Configuration section below.

### Configuration

The *core* module contains a Knot.x verticle without any sample data. Here's how its configuration files look:

**service.yml**
```yaml
services:

  - path: ${service.path}
    domain: ${service.domain}
    port: ${service.port}
```

**repository.yml**
```yaml
repositories:

  - type: local
    path: ${local.repository.path}
    catalogue: ${local.repository.catalogue}

  - type: remote
    path: ${remote.repository.path}
    domain: ${remote.repository.domain}
    port: ${remote.repository.port}
```

All placeholders can be replaced with command line arguments and environment variables. See [Using command line arguments and environment variables](#using-command-line-arguments-and-environment-variables) section.
