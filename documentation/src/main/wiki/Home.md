# Knot.x

<p align="center">
  <img src="https://github.com/Cognifide/knotx/blob/master/icons/180x180.png?raw=true" alt="Knot.x Logo"/>
</p>

Knot.x is a lightweight and high-performance **reactive microservice assembler**. It allows you to get rid of all the dynamic data from your content repository and put it into a fast and scalable world of microservices.

We care a lot about speed and that is why we built it on [Vert.x](http://vertx.io/), known as one of the leading frameworks for performant, event-driven applications.


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
