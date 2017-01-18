# Knot.x

<p align="center">
  <img src="https://github.com/Cognifide/knotx/blob/master/icons/180x180.png?raw=true" alt="Knot.x Logo"/>
</p>

Knot.x is a lightweight and high-performance **reactive microservice assembler**. It allows you to get rid of all the dynamic data from your content repository and put it into a fast and scalable world of microservices.

We care a lot about speed and that is why we built it on [Vert.x](http://vertx.io/), known as one of the leading frameworks for performant, event-driven applications.

**Table of Contents**

- [How it works](#how-it-works)
  - [Templating](#templating)
  - [Architecture](#architecture)
  - [Flow diagram](#flow-diagram)
- [Getting started](#getting-started)
  - [Requirements](#requirements)
  - [Modules](#modules)
    - [Core](#core)
      - [knotx-common](#knotx-common)
      - [knotx-repository](#knotx-repository)
      - [knotx-server](#knotx-server)
      - [knotx-standalone](#knotx-standalone)
      - [knotx-template-engine](#knotx-template-engine)
    - [Example](#example)
      - [knotx-example-monolith](#knotx-example-monolith)
      - [knotx-mocks](#knotx-mocks)
  - [Building](#building)
    - [Executing fat jar](#executing-fat-jar)
  - [Configuration](#configuration)
    - [1. knotx-example-monolith.json](#1-knotx-example-monolithjson)
      - [1.1. HTTP Repository section](#11-http-repository-section)
      - [1.2. Filesystem Repository section](#12-filesystem-repository-section)
      - [1.3. Engine section](#13-engine-section)
      - [1.4. HTTP Server section](#14-http-server-section)
      - [1.5 MockRepo section](#15-mockrepo-section)
      - [1.6 MockService section](#16-mockservice-section)
  - [Deployment options](#deployment-options)
    - [Forms processing](#forms-processing)
      - [Multiple forms processing](#multiple-forms-processing)
        - [Routing request basing on request type](#routing-request-basing-on-request-type)
- [Templating Engine](#templating-engine)
  - [Extending handlebars with custom helpers](#extending-handlebars-with-custom-helpers)
    - [Example extension](#example-extension)
- [Production](#production)
    - [Executing Knot.x core verticles as standalone fat jar](#executing-knotx-core-verticles-as-standalone-fat-jar)
      - [Configuration](#configuration-1)
    - [Executing Knot.x core verticles as a cluster](#executing-knotx-core-verticles-as-a-cluster)
    - [Remarks](#remarks)
    - [Configuration](#configuration-2)
- [Dependencies](#dependencies)
- [What's new in 0.4.0](#whats-new-in-040)

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

Additionally, Knot.x does caching of services calls results within one request, to avoid multiple calls to the same services when rendering a page.

## Architecture
The HTTP Request which comes to **Knot.x** causes a request for a template to be sent to one of the available Content Repositories. For each script with `data-api-type="templating"` there is a request to a microservice for the data. After both requests are completed, [Handlebars.js](http://handlebarsjs.com/) merges the static content and the dynamic data and returns a complete document.

[[assets/without-load-balancer.png|alt=Architecture without load balancer]]

It's worth mentioning that this architecture scales very easily. Not only can you add as many microservices and repositories as you want, but you can also use multiple Knot.x nodes set up behind a load balancer if you need to handle more traffic.

[[assets/with-load-balancer.png|alt=Architecture with load balancer]]

## Flow diagram

The following diagram shows the asynchronous nature of **Knot.x**. After obtaining a template from a repository, we request all the necessary data from microservies, which reduces the time needed for building the whole document.

[[assets/flow-diagram.png|alt=Flow diagram]]

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

To build it you also need Maven (version 3.3.1 or higher).

## Modules
The Knot.x project has two main Maven modules: **knotx-core** and **knotx-example**.

### Core
The *core* module contains the Knot.x [verticles](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) without any example data or mock endpoints. See the [Configuration section](#configuration-1) for instructions on how to deploy the Knot.x core module.

#### knotx-common
A module that defines:

- the communication model used to send event-related messages via the Event Bus.
- Knotx Starter Verticle supporting verticles definition in the given configuration JSON and corresponding Vert.x launcher

#### knotx-repository
A module that contains submodules for specialized repository [verticles](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) imlementations. 

- **knotx-repository-http** - Http Repository verticle
- **knotx-repository-filesystem** - Local filesystem Repository verticle

#### knotx-server
A module that contains the **server** [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) implementation. Server is responsible for communication between Knot.x and the World. It handles initial request, dispatches them to `repository` and `templating-engine` verticles using [Event Bus](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/EventBus.html) and finally sends responses to the client.

#### knotx-standalone
A module that contains JSON configuration to start Knot.x as standalone system. It means only following verticles to be started: `server`, `repository` and `template-engine`. It enables one to quickly set up a standalone Knot.x core application.

#### knotx-template-engine
A module that contains the **template-engine** [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) implementation. Templating Engine is responsible for processing template snippets, calling external services for dynamic data and producing final markup with injected data.
See [[Templating Engine|TemplatingEngine]] to learn more.

### Example
The *example* module contains the Knot.x application, example template repositories and mock services. Internally, it starts five verticles (Knot.x Repository, Knot.x Template Engine, Knot.x Server, Services Mocks and Mocked Remote repository). This module is a perfect fit for those getting started with Knot.x. 

#### knotx-example-monolith
A module that can be used to set up a Knot.x instance with mocked external services (from `knotx-mocks`) on a single Vert.x instance using one command. Please mind that this an example that depicts a valid setup of Sample monolith application and is not fit for use in production environments.
We recommend to start playing with Knot.x using this module. To learn how to configure Knot.x for use in production, see the [Production](#configuration-1) section.
More information about setup can be found in [Getting started](#executing-fat-jar) section.

#### knotx-mocks
A module that contains simple mocks of `templates repository` and `micorservices endpoints` implemented as [verticles](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html). 
When started, *mock remote repository* listens on port `3001` and *mock service* listens on port `3000`.

## Building

To build it, simply checkout the project, navigate to the project root and run the following command:

```
mvn clean install
```
This will create executable fat JAR files for each Knot.x:
- Knot.x Http Repository in `knotx-core/knotx-repository/knotx-repository-http/target`
- Knot.x Filesystem Repository in `knotx-core/knotx-repository/knotx-repository-filesystem/target`
- Knot.x Template Engine in `knotx-core/knotx-engine/target`
- Knot.x Http Server in `knotx-core/knotx-server/target`

And in example application:
- Mocks verticles in `knotx-example/knotx-mocks/target`
- Sample app (bundled with all required verticles) in `knotx-example/knotx-example-monolith/target`

### Executing fat jar

To run sample app, execute the following commands:
```
$cd knotx-example/knotx-example-monolith
java -jar target/knotx-example-monolith-XXX.jar -conf src/main/resources/knotx-example-monolith.json
```

This will run the server with sample data. You can supply your own configuration using the `-conf` parameter. Sample pages are available at:
```
http://localhost:8092/content/local/simple.html
http://localhost:8092/content/local/multiple-forms.html
http://localhost:8092/content/remote/simple.html
http://localhost:8092/content/remote/multiple-forms.html
```
## Configuration

The Knot.x Sample application consists of multiple verticles, each requiring a dedicated configuration entry.
Here's how JSON configuration files look:
### 1. knotx-example-monolith.json
```json
{
  "verticles" : {  
      "com.cognifide.knotx.repository.HttpRepositoryVerticle": {
        "config" : {
            ...
        }
      },
      "com.cognifide.knotx.repository.FilesystemRepositoryVerticle": {
        "config" : {
            ...
        }
      },      
      "com.cognifide.knotx.viewengine.ViewEngineVerticle": {
        "config" : {
            ...
        }
      },
      "com.cognifide.knotx.server.KnotxServerVerticle": {
        "config" : {
            ...
        }
      },  
      "com.cognifide.knotx.mocks.MockRemoteRepositoryVerticle": {
        "config" : {
            ...
        }
      },
      "com.cognifide.knotx.mocks.MockServiceVerticle": {
        "config" : {
            ...
        }
      }
  }
}
```
The configuration consists of **verticles** object containing set of configurations for verticles that are going to be deployed(started). Each object in a set should have name of class name of the **Verticle** that are going to be deployed.

#### 1.1. HTTP Repository section
```json
{
  "com.cognifide.knotx.repository.HttpRepositoryVerticle": {
    "config": {
      "address": "knotx.core.repository.http",
      "configuration": {
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
    }
  },
  ...
}
```
This section configures the Knot.x HTTP Repository Verticle that fetches a template from external server via HTTP protocol.
The config node consists of:

- **address** - the event bus address on which Http Repository Verticle listens for template requests.
- **configuration** - it's a configuration section specific to the Http protocol used by verticle, it contains:
-- **client.options** - HTTP Client options used when communicating with the destination repository. See [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) to get all options supported.
-- **client.destination** - Allows to specify **domain** and **port** of the HTTP repository endpoint.

#### 1.2. Filesystem Repository section
```json
{
  "com.cognifide.knotx.repository.FilesystemRepositoryVerticle": {
    "config": {
      "address": "knotx.core.repository.filesystem",
      "configuration": {
        "catalogue": ""
      }
    }
  },
  ...
}
```
If you need to take files from a local machine, this is the kind of repository you want to use. It's perfect for mocking data.
The config node consists of:

- **address** - the event bus address on which file system Repository Verticle listens for template requests.
- **configuration** - it's a configuration section specific to the file system.
-- **catalogue** - it determines where to take the resources from. If it's left empty, they will be taken from the classpath. It may be treated like a prefix to the requested resources.

#### 1.3. Engine section
```json
  ...
  "com.cognifide.knotx.viewengine.ViewEngineVerticle": {
    "config": {
      "address": "knotx.core.engine",
      "template.debug": true,
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
  },
  ...,
```
This section configures the Knot.x Template Engine Verticle responsible for rendering page consists of Handlebars template using data from corresponding services. The config node consists of:
- **address** - event bus address of the verticle it listens on,
- **template.debug** - boolean flag to enable/disable rendering HTML comment entities around dynamic snippets,
- **client.options** - contains json representation of [HttpClientOptions](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientOptions.html) configuration for [HttpClient](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClient.html), 
- **services** - an array of definitions of all service endpoints used by dynamic snippets.

There are two groups of services defined. Each one will be handled by a different server, i.e. all service requests which match the regular expression:
- `/service/mock/.*` will by handled by `localhost:3000`
- `/service/.*` will be handled by `localhost:8080`

The first matched service will handle the request or, if there's no service matched, the corresponding template's script block will be empty. Please note that in the near future it will be improved to define fallbacks in the template for cases when the service does not respond or cannot be matched.

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
        "address": "knotx.core.engine"
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
- **engine** - configuration about templating engine dependency. You can configure event bus **address** of engine verticle here.

#### 1.5 MockRepo section
```json
{
    ... 
  "com.cognifide.knotx.mocks.MockRemoteRepositoryVerticle": {
    "config": {
      "mock.data.root": "mock/repository",
      "http.port": 3001
    }
  },   
    ...
}
```
This section configures the Remote repository mock used by the example application. It consists of:
- **mock.data.root** - a path (relative to `knotx-mocks/src/main/resources`) where mocked HTML responses are located on local storage,
- **http.port** - HTTP Port the mock service is listening to.

#### 1.6 MockService section
```json
{
  ..., 
  "com.cognifide.knotx.mocks.MockServiceVerticle": {
    "config": {
      "mock.data.root": "mock/service",
      "http.port": 3000
    }
  }
    ...
}
```
This section configures the Services mock used by the example application. It consists of:
- **mock.data.root** - relative (to knotx-mocks/src/main/resources) path where mocked JSON responses are located on local storage
- **http.port** - HTTP Port the mock service is listening to.

Please mind that this an example that depicts a valid setup of the Sample monolith application and it is not fit for use in production environments.
To learn how to configure Knot.x for use in production, see the [Production](#configuration-1) section.

## Deployment options
To deploy verticle with advanced options use following properties:
```json
{
    ... 
  "com.cognifide.knotx.server.KnotxServerVerticle": {
    "config": {...},
    
    "instances": 2,
    "worker" : false,
    "multiThreaded": false,
    "isolationGroup": "null",
    "ha": false,
    "extraClasspath": [],
    "isolatedClasses": []
  }
    ...
}
```
- **instances** - number of verticle instances.
- **worker** - deploy verticle as a worker.
- **multiThreaded** - deploy verticle as a multi-threaded worker.
- **isolationGroup** - array of isolation group.
- **ha** - deploy verticle as highly available.
- **extraClasspath** - extra classpath to be used when deploying the verticle.
- **isolatedClasses** - array of isolated classes.

Read more about [vert.x Deployment Options](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html)
#Snippet processing
##Overview
In order to fetch the data for snippet different services can be called. Decision which services to call is made depends on service path data attribute and incoming request method.

###GET services calls
When service path is marked as data-uri-**get**, call will be executed only if http method of incoming request is GET.  
```html
<script data-api-type="templating" type="text/x-handlebars-template" 
    data-uri-get="/service/sample">
        <h1>Welcome</h1>
        <h2>{{welcomeMessage}}</h2>
</script>
```

###POST services calls
When service path is marked as data-uri-**post**, call will be executed only if http method of incoming request is POST.
```html
<script data-api-type="templating" type="text/x-handlebars-template" 
    data-uri-post="/service/formSubmit">
  <p>Please subscribe to the newsletter:</p>
  <form method="post">
    <input type="email" name="email" />
    <input type="submit" value="Submit" />
  </form>
  <p>{{message}}</p>
</script>
```

###Any method services calls
When service path is marked as data-uri-**all**, GET call will be execute regardless of incoming request method type.
```html
<script data-api-type="templating" type="text/x-handlebars-template" 
    data-uri-all="/service/formSubmit">
  <p>Please subscribe to the newsletter:</p>
  <form method="post">
    <input type="email" name="email" />
    <input type="submit" value="Submit" />
  </form>
  <p>{{message}}</p>
</script>
```

Note: it is possible to call service using `data-uri` without method postfix (e.g. `data-uri="/service/formSubmit"`. Such construction will be treated as an alias for `data-uri-all`.

###Caching service calls
Snippet might consists of more than one service call. It's also possible that there are multiple snippets on the page, each using same services. Knot.x does caching results of service calls to avoid multiple calls for the same data. 
Caching is performed within one request only. It means second request will not get cached data. 

###Parametrized services calls
When found a placeholder within the data-uri-**-get** call it will be replaced with a dynamic value based on the current http request.
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

How would you use a placeholder within your script:
```html
<script data-api-type="templating" type="text/x-handlebars-template" 
    data-uri-get-search="/service/sample/search?q={param.q}"
    data-uri-get-twitter="/service/twitter/{uri.pathpart[2]}">
        <h1>Welcome</h1>
        <h2>{{search.numberOfResults}}</h2>
        <h2>{{twitter.firstTweetTitle}}</h2>
</script>
```

### Forms processing
When form request is sent to Knot.x it will handle that by resending all of the form attributes to data service that is marked as data-uri-**post**.
 
Consider below scenario: user visits page with form for the first time and then submits that form using POST method. 

> template for the form user will receive
```html
...
<script data-api-type="templating"
        type="text/x-handlebars-template"
        data-uri-post-formresponse="/service/mock/subscribeToCompetition.json"
        data-uri-get-infoservice="/service/mock/infoService.json"
        data-uri-all-labelsrepository="/service/mock/labelsRepository.json"
        data-id="competition-form">
    <h1>{{labelsrepository.welcomeInCompetition}}</h1>
    {{#if formresponse}}
    <p>{{labelsrepository.thankYouForSubscribingToCompetition}}</p>
    {{else}}
    <p>{{labelsrepository.generalInfo}}</p>
    <form method="post" class="form-inline">
        <div class="form-group">
            <label for="name">Name</label>
            <input type="text" name="name" id="name"/>
        </div>
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" name="email" id="email"/>
        </div>
        <button type="submit" class="btn btn-default">Submit</button>
        <input type="hidden" name="_id" value="competition-form"/>
    </form>
    {{/if}}
</script>
...
```

[[assets/knot.x-form-get.png|alt=Form get request]]

1. User makes GET request.
2. Template is fetched using GET request.
3. GET request is made  to "Info Service"
4. GET request is made  to "Labels Repository"
5. User receives built html with form(See above) 

[[assets/knot.x-form-post.png|alt=Form post request]]

1. User submits the **competition-form** form. Following form attributes are sent: 

    - name : "john smith"
    - email : "john.smith@mail.com"
    - _id : "competition-form"
    
2. Template is fetched using GET request.
3. Because **_id** attribute from request matches the **data-id** attribute in template POST request is sent "Subscribe To Competition" service.  All form attributes that was submitted are sent to that service.
4. GET request is made  to "Labels Repository"
5. User receives built html with form response:
```html
...
<h1>Welcome user/h1>
<p>Thank you for registering to newsletter.</p>
...
```


#### Multiple forms processing
Templates can contain many snippets with forms. Knot.x provides mechanism to distinguish which snippet should make call when **POST** request is done. 
Snippet can contain an **data-id** attribute. Its value defines which snippet should be processed by comparing it with **_id** parameter sent with POST method.

**Example:**

Snippet in example below will call /service/subscribeToCompetition only when request method is POST and **_id** parameter value is competition-form.
/service/subscribeToNewsletter will be called only when request method is POST and **_id** parameter value is newsletter-form.

**Snippet:**
```html
<script data-api-type="templating" 
  type="text/x-handlebars-template" 
  data-uri-post-formResponse="/service/subscribeToCompetition" 
  data-uri-all-labelsRepository="/service/labelsRepository"
  data-id="competition-form">
  <h1>{{labelsRepository.welcomeInCompetition}}</h1>
  {{#if formResponse}}
     <p>{{labelsRepository.thankYouForSubscribingToCompetition}}</p>
  {{else}}
      <p>Please subscribe to our new competition:</p>
      <form method="post">
        <input type="text" name="name" />
        <input type="email" name="email" />
        <input type="submit" value="Submit" />
        <input type="hidden" name="_id" value="competition-form" />
      </form>
  {{/if}}
</script>

<script data-api-type="templating" 
  type="text/x-handlebars-template" 
  data-uri-post-formResponse="/service/subscribeToNewsletter" 
  data-uri-all-labelsRepository="/service/labelsRepository"
  data-id="newsletter-form">
  <h1>{{labelsRepository.subscribeToNewsletter}}</h1>
  {{#if formResponse}}
    <p>{{labelsRepository.thankYouForSubscribingToNewsletter}}</p>
  {{else}}
      <p>Please subscribe to our newsletter:</p>
      <form method="post">
        <input type="email" name="email" />
        <input type="submit" value="Submit" />
        <input type="hidden" name="_id" value="newsletter-form" />
      </form>
  {{/if}}
</script>
```

##### Routing request basing on request type
Knot.x provides a mechanism that causes different approach to HTTP request and XmlHttpRequest. When Knot.x is requested with HTTP default processing path is started and flow works as usually. When Knot.x is requested with XHR only requested part of a template is processed and returned.

**Example:**

When user sends below XHR only snippet that contains **data-id** attribute equals to **newsletter-form** will be processed and returned to the user. 
```
POST /content/examplePage.html?_id=newsletter-form&email=JohnDoe@example.com
Host: www.example.com
Connection: keep-alive
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36
Accept: */*
Content-Type: text/html
```
               


###Service response status code
Service response status code can be used in snippets e.g. as condition to display messages if response is not success.

**Example:**
```html
<script data-api-type="templating" 
    type="text/x-handlebars-template" 
    data-uri-post-formResponse="/service/formSubmit">
  <h1>Welcome!</h1>

  {{#if formResponse._response.statusCode == 503
    <p>Failed. Service was unavailable.</p>
  {{else}}
    <p>Success.</p>
  {{/if}}

</script>
```



# Templating Engine

At the heart of the Knot.x Templating Engine lies [Handlebars.js](http://handlebarsjs.com/). Knot.x utilizes its Java port - [Handlebars.java](https://github.com/jknack/handlebars.java) to compile and evaluate templates.

## Extending handlebars with custom helpers

If the list of available handlebars helpers is not enough, you can easily extend it. To do this the following actions should be undertaken:

1. Create a class implementing ```com.cognifide.knotx.handlebars.CustomHandlebarsHelper``` interface. This interface extends [com.github.jknack.handlebars.Helper](https://jknack.github.io/handlebars.java/helpers.html)
2. Register the implementation as a service in the JAR file containing the implementation
    * Create a configuration file called META-INF/services/com.cognifide.knotx.handlebars.CustomHandlebarsHelper in the same project as your implementation class
    * Paste a fully qualified name of the implementation class inside the configuration file. If you're providing multiple helpers in a single JAR, you can list them in new lines (one name per line is allowed) 
    * Make sure the configuration file is part of the JAR file containing the implementation class(es)
3. Run Knot.x with the JAR file in the classpath

### Example extension

Sample application contains an example custom Handlebars helper - please take a look at the implementation of ```BoldHelper```:
* Implementation class: ```com.cognifide.knotx.monolith.handlebars.BoldHelper```
* service registration: ```knotx-example-monolith/src/main/resources/META-INF/services/com.cognifide.knotx.handlebars.CustomHandlebarsHelper```


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

The *core* module contains four Knot.x verticles without any sample data. Here's how its configuration files look based on a sample **standalone.json** available in knotx-standalone module:
**standalone.json**
```json
{
  "verticles": {
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
            "path": "/content/local/.*",
            "address": "knotx.core.repository.filesystem"
          },
          {
            "path": "/content/.*",
            "address": "knotx.core.repository.http"
          }
        ],
        "engine": {
          "address": "knotx.core.engine"
        }
      }
    },
    "com.cognifide.knotx.repository.HttpRepositoryVerticle": {
      "config": {
        "address": "knotx.core.repository.http",
        "configuration": {
          "client.options": {
            "maxPoolSize": 1000,
            "keepAlive": false,
            "tryUseCompression": true
          },
          "client.destination": {
            "domain": "localhost",
            "port": 3001
          }
        }
      }
    },
    "com.cognifide.knotx.repository.FilesystemRepositoryVerticle": {
      "config": {
        "address": "knotx.core.repository.filesystem",
        "configuration": {
          "catalogue": ""
        }
      }
    },
    "com.cognifide.knotx.viewengine.ViewEngineVerticle": {
      "config": {
        "address": "knotx.core.engine",
        "template.debug": true,
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
    }
  }
}
```
Configuration JSON contains four config sections, one for each Knot.x verticle.
Each verticle can be configured with additional [Deployment Options](https://github.com/Cognifide/knotx/wiki/GettingStarted#deployment-options) 

### Executing Knot.x core verticles as a cluster
Thanks to the modular structure of the Knot.x project, it's possible to run each Knot.x verticle on a separate JVM or host them as a cluster. An out of the box requirement to form a cluster (driven by Hazelcast) is that the network supports multicast.
For other network configurations please consult Vert.x documentation [Vert.x Hazelcast - Configuring cluster manager](http://vertx.io/docs/vertx-hazelcast/java/#_configuring_this_cluster_manager)

To run it, execute the following command:
**Host 1 - Http Repository Verticle**
```
java -jar knotx-repository-http-XXX-fat.jar -conf <path-to-http-repository-configuration.json>
```
**Host 2 - Filesystem Repository Verticle**
```
java -jar knotx-repository-filesystem-XXX-fat.jar -conf <path-to-filesystem-repository-configuration.json>
```
**Host 2 - Template Engine Verticle**
```
java -jar knotx-template-engine-XXX-fat.jar -conf <path-to-engine-configuration.json>
```
**Host 3 - Knot.x Server Verticle**
```
java -jar knotx-server-XXX-fat.jar -conf <path-to-server-configuration.json>
```

For production setup you might need to configure a logger according to your needs. You can start each of the verticles specifying a path to the logback logger configuration via a system property.
```
java -Dlogback.configurationFile=<full-path-to-logback-configuration-file> -jar XXX
```
Please refer to [LOGBack manual](http://logback.qos.ch/manual/index.html) for details on how to configure the logger.

### Remarks
For clustering testing purposes you can also start a separate verticle with repository and services mocks. In order to do so, run the following commands:
```
$ cd knotx-example/knotx-mocks
$ java -jar target/knotx-mocks-XXXX-far.jar -conf src/main/resources/knotx-mocks.json
```
The mocks verticle is configured as follows:
- **mock remote repository** listens on port **3001**
- **mock service** listens on port **3000**

### Configuration
####Server configuration
Knot.x server requires JSON configuration with *config* object. **Config** section allows to define:

- **http.port** property to set http port which will be used to start Knot.x server
- **allowed.response.headers** list of headers that should be passed back to the client.
- **repositories** mapping of paths to the repository verticles that should deliver Templates
- **engine** event bus address of the Rendering engine verticle
```json
{
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
          "path": "/content/local/.*",
          "address": "knotx.core.repository.filesystem"
        },
        {
          "path": "/content/.*",
          "address": "knotx.core.repository.http"
        }
      ],
      "engine": {
        "address": "knotx.core.engine"
      }
     }
     ...
 ``` 
####Verticle configuration
Each verticle requires JSON configuration of **config** object. The configuration consists of the same parameters as previous examples.
For instance, a configuration JSON for the *HTTP repository* verticle could look like this:
```json
{
  "address": "knotx.core.repository.http",
  "configuration": {
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
}
```
#####Preserving headers passed to microservices
Single service configuration allows to define which headers should be passed to microservices. 
If **allowed.request.headers** section is not present, no headers will be forwarded to microservice. It is possible to use wildcard character (*) e.g.
```json
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
    ...
]        
```

```json
"services": [
    {
      "path": "/service/mock/.*",
      "domain": "localhost",
      "port": 3000,
      "allowed.request.headers": [
        "*"
      ]
    },
    ...
]
```

# Dependencies

- io.vertx
- io.reactivex
- org.jsoup
- com.github.jknack.handlebars
- com.google.code.gson
- guava
- commons-lang3
- junit

# What's new in 0.4.0

* Comprehensive verticles deployment options
* Placeholders in service url support
* Repositories request / response HTTP headers rules
* Services request / response HTTP headers rules
* Caching service calls within one request
* Custom handlebars helpers registration support
* Specialized repositories verticle implementation
