# Getting started

## Requirements

To run Knot.x you only need Java 8.

To build it you also need Maven.

## Modules
The Knot.x project has two Maven modules: **knotx-core** and **knotx-example**.

The *core* module contains the Knot.x [verticle]((http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html)) without any example data or mock endpoints. See the [[Configuration section|ProductionUsage#configuration]] for instructions on how to deploy the Knot.x core module.

The *example* module contains the Knot.x application, example template repositories and mock services. Internally, it starts three independent verticles (for the Knot.x application, example template repositories and mock services). This module is a perfect fit for those getting started with Knot.x. 

## Building

To build it, simply checkout the project, navigate to the project root and run the following command:

```
mvn clean install
```
This will create executable JAR files for both *core* and *example* modules in the `knotx-core/target` and `knotx-example/target` directories respectively.

### Executing from Maven

To run Knot.x from Maven, execute the following command from the project's root directory:
```
mvn spring-boot:run
```
This will run the sample server with mock services and sample repositories. Sample pages are available at:

```
http://localhost:8092/content/local/simple.html
http://localhost:8092/content/remote/simple.html
http://localhost:8092/content/jsonplaceholder/remote.html
```

### Executing fat jar

To run it execute the following command:

```
java -jar knotx-example-XXX.jar
```

This will run the server with sample data.

In order to run the server with your own configuration add this to the command:

```
-Dservice.configuration=<path to your service.yml> -Drepository.configuration=<path to your repository.yml>
```

or provide environment variables that will hold locations of your configuration files.

For windows:
```
SET service.configuration=<path to your service.yml>
SET repository.configuration=<path to your repository.yml>
```
For Unix:
```
export service.configuration=<path to your service.yml>
export repository.configuration=<path to your repository.yml>
```

As you may notice, there are two files that need to be defined in order to configure your services and repositories. Please note that the paths should be compatible with the [Spring Resources](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/resources.html) format, for example:

- `file:///data/config.yml` on Linux
- `file:c:\\data\config.yml` on Windows
 

## Configuration

The Knot.x verticle, as well as sample services and repositories can be customised using dedicated YAML configuration files. This section explains their structure and the meaning of specific fields.

Please mind that this set of examples depicts a valid setup of the example module and is not fit for use in production environments. To learn how to configure Knot.x for use in production, see the [Production](#configuration-1) section.

Here's how configuration files look:

### Services

**service.yml**
```yaml
services:

  - path: /service/mock/.*
    domain: localhost
    port: 3000

  - path: /service/.*
    domain: localhost
    port: 8080

  - path: /photos/.*
    domain: jsonplaceholder.typicode.com
    port: 80
```

There are three groups of services defined. Each one will be handled by a different server, i.e. all service requests which match the regular expression:

- `/service/mock/.*` will by handled by `localhost:3000`,
- `/service/.*` will be handled by `localhost:8080`,
- `/photos/.*` will be handled by `jsonplaceholder.typicode.com`.

The first matched service will handle the request or, if there's no service matched, the corresponding template's script block will be empty. Please note that in the near future it will be improved to define fallbacks in the template for cases when the service does not respond or cannot be matched.

### Repositories

**repository.yml**
```yaml
repositories:

  - type: local
    path: /content/local/.*
    catalogue:

  - type: remote
    path: /content/.*
    domain: localhost
    port: 3001
```

There are two sample repositories defined - `local` and `remote`. Each of them defines a `path` - a regular expression that indicates which resources will be taken from this repository. The first one matched will handle the request or, if no repository is matched, **Knot.x** will return a `404 Not found` response for the given request.


#### Local repositories

If you need to take files from a local machine, this is the kind of repository you want to use. It's perfect for mocking data. 

Second parameter to define is `catalogue` - it determines where to take the resources from. If left empty, they will be taken from the classpath. It may be treated like a prefix to the requested resources.

#### Remote repositories

This kind of repository connects with an external server to fetch templates.

To specify where the remote instance is, please configure the `domain` and `port` parameters.

### Application

**application.yml**
```yaml
#
# configuration specific to knotx-core were omitted for brevity
#

mock:
  service:
    port: 3000
    root: mock-service
  repository:
    port: 3001
    root: mock-remote-repository
```

There are two mock endpoints in the application configuration: one for mock services and one for mock remote repository. Those endpoints are deployed as separate verticles.

### Using command line arguments and environment variables

Often some properties are sensitive and we do not want to expose them in configuration files, e.g. passwords. In such case we can use command line arguments or environment variables to inject the values of those properties into the configuration.
Let's assume the following repository configuration is present:
```yaml
repositories:

  - type: db
    user: db.user
    password: ${db.password}
```
Since we do not want to expose the database password, we can use a placeholder and substitute it with the value of a command line argument while starting our application:
```
--db.password=passw0rd
```
Another way to provide a value for the password placeholder shown above is to set an evironment variable `db.password`.

>Notice: command line arguments take precedence over environment variables.
