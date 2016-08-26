![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

[![Build Status](https://travis-ci.org/Cognifide/knotx.svg?branch=master)](https://travis-ci.org/Cognifide/knotx)

# Knot.x

<p align="center">
  <img src="https://github.com/Cognifide/knotx/blob/master/icons/180x180.png?raw=true" alt="Knot.x Logo"/>
</p>

Knot.x is a lightweight and high-performance **reactive microservice assembler**. It allows you to get rid of all the dynamic data from your content repository and put it into a fast and scalable world of microservices.

We care a lot about speed and that is why we built it on [Vert.x](http://vertx.io/), known as one of the leading frameworks for performant, event-driven applications.

# Contents

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [How it works](#how-it-works)
  - [Templating](#templating)
  - [Architecture](#architecture)
  - [Flow diagram](#flow-diagram)
- [Getting started](#getting-started)
  - [Requirements](#requirements)
  - [Modules](#modules)
  - [Building](#building)
    - [Executing from Maven](#executing-from-maven)
    - [Executing fat jar](#executing-fat-jar)
  - [Configuration](#configuration)
    - [Services](#services)
    - [Repositories](#repositories)
      - [Local repositories](#local-repositories)
      - [Remote repositories](#remote-repositories)
    - [Application](#application)
    - [Using command line arguments and environment variables](#using-command-line-arguments-and-environment-variables)
- [Features](#features)
  - [Requests grouping](#requests-grouping)
- [Production](#production)
    - [Executing](#executing)
    - [Configuration](#configuration-1)
- [Licence](#licence)
- [Dependencies](#dependencies)
- [Roadmap](#roadmap)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# How it works

## Templating

In order to separate static content and dynamic data we introduced a Templating Engine, which merges a template obtained from the content repository and dynamic data provided by microservices using [Handlebars.js](http://handlebarsjs.com/). Here is what a template looks like:

```html
<script data-api-type="templating" data-uri-dataservice="/path/to/service.json" type="text/x-handlebars-template">
    <h2>{{dataservice.header}}</h2>
    <div>{{dataservice.body.content}}</div>
</script>
```

The following table describes all elements and attributes used in the template.

| Element                             | Description                                                              |
| ----------------------------------- | ------------------------------------------------------------------------ |
| `data-api-type="templating"`        | required for **Knot.x** to recognize the script as a template to process |
| `data-uri-post-dataservice`    | path to a microsevice that provides the data - it will be handled by a service, as described in the [Configuration](#configuration) section. Name of the attribute consist of following parts:<ul><li>'data-uri' : required. </li><li>post : optional. call to  microsevicervice will be made only if request method is of that type. Can be: post, get, all. </li><li>dataservice : optional. Defines the namespace name. Only placeholders with matching namespace will be filled by data coming from that service</li></ul>|            
| `type="text/x-handlebars-template"` | required by [Handlebars.js](http://handlebarsjs.com/) tool, which is used for templating |
| `{{dataservice.header}}` `{{dataservice.body.content}}`| Placeholders that will be filled by data taken from a JSON response provided by a microservice. Where 'dataservice' is an optional namespace(described above).|

In this case the microservice response could have the following format:

```json
{
    "header" : "Hello",
    "body" : {
        "content": "World"
    }
}
```

## Architecture
The HTTP Request which comes to **Knot.x** causes a request for a template to be sent to one of the available Content Repositories. For each script with `data-api-type="templating"` there is a request to a microservice for the data. After both requests are completed, [Handlebars.js](http://handlebarsjs.com/) merges the static content and the dynamic data and returns a complete document.

![Architecture without load balancer](https://github.com/Cognifide/knotx/blob/master/icons/architecture/without-load-balancer.png)

It's worth mentioning that this architecture scales very easily. Not only can you add as many microservices and repositories as you want, but you can also use multiple Knot.x nodes set up behind a load balancer if you need to handle more traffic.

![Architecture with load balancer](https://github.com/Cognifide/knotx/blob/master/icons/architecture/with-load-balancer.png)

## Flow diagram

The following diagram shows the asynchronous nature of **Knot.x**. After obtaining a template from a repository, we request all the necessary data from microservies, which reduces the time needed for building the whole document.

![Flow diagram](https://github.com/Cognifide/knotx/blob/master/icons/architecture/flow-diagram.png)

Please notice, that order of data calls is not guaranteed. Please consider following snippet as an example:
```html
<script data-api-type="templating" data-uri-post-saveUser="/saveUserService" data-uri-get-getUser="/getUserService" type="text/x-handlebars-template">
    <div>Hello {{getUser.name}}</div>
    <form method="post">
        <input type="input" name="name" />
        <input type="submit" value="Submit" />
    </form>
</script>
```

The order of calling services may be:
1. `/saveUserService`,
2. `/getUserService` .
or
1. `/getUserService`,
2. `/saveUserService`.

Because of this, service `/getUserService` should not depend on operations from `/saveUserService`.

# Getting started

## Requirements

To run Knot.x you only need Java 8.

To build it you also need Maven.

## Modules
The Knot.x project has two Maven modules: **knotx-core** and **knotx-example**.

The *core* module contains the Knot.x [verticle]((http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html)) without any example data or mock endpoints. See the [Configuration section](#configuration-1) for instructions on how to deploy the Knot.x core module.

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
```

There are two groups of services defined. Each one will be handled by a different server, i.e. all service requests which match the regular expression:

- `/service/mock/.*` will by handled by `localhost:3000`
- `/service/.*` will be handled by `localhost:8080`

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
    root: mock/service
  repository:
    port: 3001
    root: mock/repository
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

# Features

## Requests grouping

Template obtained from the repository may contain many snippets that will trigger microservice calls for data. There is a chance that some of the snippets will have the same `data-uri` attribute set, meaning they will request data from the same source.
In such case only one call to microservice shall be made and data retrieved from service call should be applied to all snippets sharing the same `data-uri`.

Example:
Let's assume that we obtained the following template from repository:
```html
<div>
<script data-api-type="templating" data-uri="/searchService" type="text/x-handlebars-template">
    <div>{{search.term}}</div>
</script>
</div>
...
<div>
<script data-api-type="templating" data-uri="/searchService" type="text/x-handlebars-template">
    <ul>
    {{#each search.results}}
      <li>{{result}}<li>
    {{/each}}
    </ul>
</script>
</div>
```
In this case only one call to microservice will be made, since both snippets share the same `data-uri`. Data retrived from `/searchService` will be applied to both snippets.

Notice: The following `data-uri` attributes
```
/searchService?q=first
```
```
/searchService?q=second
```
would trigger two calls for data because of the difference in query strings, even though the path to service is the same in both.

# Production

The example module is provided for testing purposes. Only the core module should be deployed in a production environment. (The Knot.x application runs as a single verticle without any dependencies).

### Executing

To run it, execute the following command:

```
java -jar knotx-core-XXX.jar -Dservice.configuration=<path to your service.yml> -Drepository.configuration=<path to your repository.yml>
```

This will run the server with production settings. For more information see the [Configuration](#configuration-1) section.

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

# Licence

**Knot.x** is licensed under the [Apache License, Version 2.0 (the "License")](https://www.apache.org/licenses/LICENSE-2.0.txt)

# Dependencies

- io.vertx
- io.reactivex
- org.jsoup
- com.github.jknack.handlebars
- com.google.code.gson
- guava
- commons-lang3
- junit

# Roadmap

- Extend ‘templateDebug’ mode,
- Extend service exception handling (templates should provide information how to behave when a service is not available or returns an error)
- Remote template repository support (for remote repositories like Apache, Redis, AEM Dispatcher)
- Support for POST requests to services (forms)
- Authentication and authorization solution based on JWT Tokens
- Placeholders in service-url for dynamic parameters (e.g. GET/POST/HEADER parameters rewritten to service request). https://github.com/Cognifide/knotx/issues/8
- Multidomain support, single Knot.x cluster can support whole platform with multiple sites. Each domian restricted to set of services to call.
- Docker + Cookbook for easy production setup.
- Kitchen integration tests.
- More Service Repositories implementations, e.g. for Redis.
- More template engines support (e.g. FreeMarker).
