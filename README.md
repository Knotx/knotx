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

To build it you also need Maven at least version 3.3.1

## Modules
The Knot.x project has two main Maven modules: **knotx-core** and **knotx-example**.

The *core* module contains the Knot.x [verticles]((http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html)) without any example data or mock endpoints. See the [Configuration section](#configuration-1) for instructions on how to deploy the Knot.x core module.

The *example* module contains the Knot.x application, example template repositories and mock services. Internally, it starts five verticles (Knot.x Repository, Knot.x Template Engine, Knot.x Server, Services Mocks and Mocked Remote repository). This module is a perfect fit for those getting started with Knot.x. 

## Building

To build it, simply checkout the project, navigate to the project root and run the following command:

```
mvn clean install
```
This will create executable fat JAR files for each Knot.x:
- Knot.x Repository in `knotx-core/knotx-repository/target`
- Knot.x Template Engine in `knotx-core/knotx-engine/target`
- Knot.x Http Server in `knotx-core/knotx-server/target`

And in example application:
- Mocks verticles in `knotx-example/knotx-mocks/target`
- Sample app in `knotx-example/knotx-example-monolith/target`

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

To run sample app execute following commands
```
$cd knotx-example/knotx-example-monolith
java -jar target/knotx-example-monolith-XXX.jar -conf src/main/resources/application.json
```

This will run the server with sample data. You can supply your own configuration using -conf parameter.Sample pages are available at:
```
http://localhost:8092/content/local/simple.html
http://localhost:8092/content/remote/simple.html
```
## Configuration

The Knot.x Sample application consists of multiple verticles, each requiring dedicated configuration entry.
Here's how JSON configuration files look:
**1. application.json**
```json
{
  "repository": {
    "config" : {
        ...
    }
  },
  "templateEngine": {
    "config" : {
        ...
    }
  },
  "knotxServer": {
    "config" : {
        ...
    }
  },  
  "mockRepo": {
    "config" : {
        ...
    }
  },
  "mockService": {
    "config" : {
        ...
    }
  }
}
```
This is the main configuration supplying config entries for each verticle started by Sample application.

**1.1. repository** section
```json
{
  "repository": {
    "config": {
      "service.name": "template-repository",
      "repositories": [
        {
          "type": "local",
          "path": "/content/local/.*",
          "catalogue": ""
        },
        {
          "type": "remote",
          "path": "/content/.*",
          "domain": "localhost",
          "port": 3001
        }
      ]
    }
  },
  ...
}
```
This section configures the Knot.x Repository Verticle listening for requests on Vert.x event bus. config node consists of:
- **service.name** - name/address of the event bus to which Repository Verticle subscribes on.
- **repositories** - Array consists of definitions of all repositories used providing HTML Templates

There are two sample repositories defined - `local` and `remote`. Each of them defines a `path` - a regular expression that indicates which resources will be taken from this repository. 
The first one matched will handle the request or, if no repository is matched, **Knot.x** will return a `404 Not found` response for the given request.

- **Local repositories**
```json
{
    "type": "local",
    "path": "/content/local/.*",
    "catalogue": ""    
}
```
If you need to take files from a local machine, this is the kind of repository you want to use. It's perfect for mocking data. 
Second parameter to define is `catalogue` - it determines where to take the resources from. If left empty, they will be taken from the classpath. It may be treated like a prefix to the requested resources.

- **Remote repositories**
This kind of repository connects with an external server to fetch templates. To specify where the remote instance is, please configure the `domain` and `port` parameters.
```json
{
    "type": "remote",
    "path": "/content/.*",
    "domain": "localhost",
    "port": 3001   
}
```

**1.2. templateEngine** section
```json
  ...
  "templateEngine": {
    "config": {
      "service.name": "template-engine",
      "template.debug": true,
      "services": [
        {
          "path": "/service/mock/.*",
          "domain": "localhost",
          "port": 3000
        },
        {
          "path": "/service/.*",
          "domain": "localhost",
          "port": 8080
        }
      ]
    }
  },
  ...,
```
This section configures the Knot.x Template Engine Verticle listening for requests on Vert.x event bus. config node consists of:
- **service.name** - name/address of the event bus to which Repository Verticle subscribes on.
- **template.debug** - bolean flag to enable/disable rendering HTML comment entities around dynamic snippets.
- **services** - Array consists of definitions of all service endpoints used by dynamic snippets.

There are two groups of services defined. Each one will be handled by a different server, i.e. all service requests which match the regular expression:
- `/service/mock/.*` will by handled by `localhost:3000`
- `/service/.*` will be handled by `localhost:8080`

The first matched service will handle the request or, if there's no service matched, the corresponding template's script block will be empty. Please note that in the near future it will be improved to define fallbacks in the template for cases when the service does not respond or cannot be matched.

**1.3. knotxServer** section
```json
{
  ...
  "knotxServer": {
    "config": {
      "http.port": 8092,
      "preserved.headers": [
        "User-Agent",
        "X-Solr-Core-Key",
        "X-Language-Code"
      ],
      "dependencies" : {
        "repository.address" : "template-repository",
        "engine.address": "template-engine"
      }
    }
  },
  ...
}
```
This section configures the Knot.x HTTP server. config node consists of:
- **http.port** - And HTTP port on which server listens for requests
- **preserved.headers** - Array of HTTP Headers that will be forwarded to service calls
- **dependencies** - consists Vert.x Event Bus addresses to Knot.x verticles used by Knot.x
    - **repository.address** - event bus address of Knot.x Repository verticle. This is the same value as **service.name** in the **repository** section of the application.json
    - **engine.address** - event bus address of Knot.x Template Engine verticle. This is the same value as **service.name** in the **templateEngine** section of the application.json    

**mockRepo** section
```json
{
    ... 
    "mockRepo": {
        "mock.data.root": "mock/repository",
        "http.port": 3001
    },     
    ...
}
```
This section configures the Remote repository mock used by the example application. It consists of:
- **mock.data.root** - relative (to knotx-mocks/src/main/resources) path where mocked HTML responses are located on local storage
- **http.port** - HTTP Port the mock service is listening to.

**mockService** section
```json
{
    ... 
    "mockService": {
        "mock.data.root": "mock/service",
        "http.port": 3000
    },   
    ...
}
```
This section configures the Services mock used by the example application. It consists of:
- **mock.data.root** - relative (to knotx-mocks/src/main/resources) path where mocked JSON responses are located on local storage
- **http.port** - HTTP Port the mock service is listening to.

Please mind that this an example that depicts a valid setup of Sample monolith application is not fit for use in production environments.
To learn how to configure Knot.x for use in production, see the [Production](#configuration-1) section.

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

The example module is provided for testing purposes. Only the core verticles should be deployed in a production environment. (The Knot.x application consists of 3 verticles).

### Executing Knot.x core verticles as standalone fat jar

To run it, execute the following command:
```
$cd knotx-core/knotx-standalone
java -jar target/knotx-standalone-XXX-fat.jar -conf <path-to-your-configuration.json>
```

This will run the server with production settings. For more information see the [Configuration](#configuration-1) section.

#### Configuration

The *core* module contains 3 Knot.x verticle without any sample data. Here's how its configuration files look based on sample **standalone.json** available in knotx-standalone module:
**standalone.json**
```json
{
  "server": {
    "config": {
      "http.port": 8092,
      "preserved.headers": [
        "User-Agent",
        "X-Solr-Core-Key",
        "X-Language-Code"
      ],
      "dependencies" : {
        "repository.address" : "template-repository",
        "engine.address": "template-engine"
      }
    }
  },
  "repository": {
    "config": {
      "service.name": "template-repository",
      "repositories": [
        {
          "type": "local",
          "path": "/content/local/.*",
          "catalogue": ""
        },
        {
          "type": "remote",
          "path": "/content/.*",
          "domain": "localhost",
          "port": 3001
        }
      ]
    }
  },
  "engine": {
    "config": {
      "service.name": "template-engine",
      "template.debug": true,
      "services": [
        {
          "path": "/service/mock/.*",
          "domain": "localhost",
          "port": 3000
        },
        {
          "path": "/service/.*",
          "domain": "localhost",
          "port": 8080
        }
      ]
    }
  }
}
```
Configuration json contains three config sections, each for each Knot.x verticle.

### Executing Knot.x core verticles as cluster
Thanks to the modular structure of the Knot.x project, it's possible to run each Knot.x verticle on separate JVM or host as cluster. Out of the box requirement to form cluster (driven by Hazelcast) is that network supports multicast.
For other network configurations please consult Vert.x documentation [Vert.x Hazelcast - Configuring cluster manager](http://vertx.io/docs/vertx-hazelcast/java/#_configuring_this_cluster_manager)

To run it, execute the following command:
**Host 1 - Repository Verticle**
```
java -jar knotx-repository-XXX-fat.jar -conf <path-to-repository-configuration.json>
```
**Host 2 - Template Engine Verticle**
```
java -jar knotx-template-engine-XXX-fat.jar -conf <path-to-engine-configuration.json>
```
**Host 3 - Knot.x Server Verticle**
```
java -jar knotx-server-XXX-fat.jar -conf <path-to-server-configuration.json>
```

### Remarks
For clustering testing purposes you can start also a separate verticle with repository and services mocks. In order to do so, run the following commands:
```
$ cd knotx-example/knotx-mocks
$ java -jar target/knotx-mocks-XXXX-far.jar -conf src/main/resources/knotx-mocks.json
```
The mocks verticle is configured as follows:
- **mock remote repository** listens on port **3001**
- **mock service** listens on port **3000**

### Configuration
Each verticle require JSON configuration with *config* object. The configuration consists of the same parameters as previous examples.
For instance, a configuration JSON for *repository* verticle
```json
{
  "config": {
    "service.name": "template-repository",
    "repositories": [
      {
        "type": "remote",
        "path": "/content/.*",
        "domain": "localhost",
        "port": 3001
      }
    ]
  }
}
```

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
