# Handlebars Knot
Handlebars Knot is a [[Knot|Knot]] implementation responsible for handlebars template processing.

##How does it work?
Handlebars Knot lies [Handlebars.js](http://handlebarsjs.com/) - it utilizes
Handlebars Java port - [Handlebars.java](https://github.com/jknack/handlebars.java) to compile and evaluate
templates.

Handlebars Knot retrieves all [[dynamic fragments|Splitter]] from [[Knot Context|Knot]]. Then for each fragment
it evaluates Handlebars template fragment using fragment context. In this way data from other [[Knots|Knot]]
can be applied to Handlebars snippets.

###Example
Example Knot Context contains:
```
{
  "_result": {
    "message":"this is webservice no. 1",
    ...
  },
  "_response": {
    "statusCode":"200"
  },
  "second": {
    "_result": {
      "message":"this is webservice no. 2",
    ...
    },
    "_response": {
      "statusCode":"200"
    }
  }
}
```
It can be reflected in Handlebars templates like:
```html
<div class="col-md-4">
  <h2>Snippet1 - {{second._result.message}}</h2>
  <div>Snippet1 - {{second._result.body.a}}</div>
  {{#string_equals second._response.statusCode "200"}}
    <div>Success! Status code : {{second._response.statusCode}}</div>
  {{/string_equals}}
</div>
```

## How to configure?
Handlebars Knot is deployed as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html), 
depending on how it's deployed. You need to supply **Handlebars Knot** configuration.

JSON presented below is an example how to configure Handlebars Knot deployed as standalone fat jar:
```json
{
  "address": "knotx.knot.handlebars",
  "template.debug": true
}
```
When deploying **Handlebars Knot** using Knot.x starter verticle, configuration presented above should 
be wrapped in the JSON `config` section:
```json
"verticles" : {
  ...,
  "com.cognifide.knotx.knot.templating.HandlebarsKnotVerticle": {
    "config": {
      "PUT YOUR CONFIG HERE"
    }
  },
  ...,
}
```
Detailed description of each configuration option is described in the next subsection.

### Handlebars Knot options

Main Handlebars Knot options available.

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Handlebars Knot verticle. |
| `template.debug`            | `Boolean`                           | &#10004;       | Template debug enabled option.|

## How to extend?

### Extending handlebars with custom helpers

If the list of available handlebars helpers is not enough, you can easily extend it. To do this the 
following actions should be undertaken:

1. Create a class implementing ```com.cognifide.knotx.handlebars.CustomHandlebarsHelper``` interface. 
This interface extends [com.github.jknack.handlebars.Helper](https://jknack.github.io/handlebars.java/helpers.html)
2. Register the implementation as a service in the JAR file containing the implementation
    * Create a configuration file called META-INF/services/com.cognifide.knotx.handlebars.CustomHandlebarsHelper 
    in the same project as your implementation class
    * Paste a fully qualified name of the implementation class inside the configuration file. If you're 
    providing multiple helpers in a single JAR, you can list them in new lines (one name per line is allowed) 
    * Make sure the configuration file is part of the JAR file containing the implementation class(es)
3. Run Knot.x with the JAR file in the classpath

#### Example extension

Sample application contains an example custom Handlebars helper - please take a look at the implementation of ```BoldHelper```:
* Implementation class: ```com.cognifide.knotx.example.monolith.handlebars.BoldHelper```
* service registration: ```knotx-example-monolith/src/main/resources/META-INF/services/com.cognifide.knotx.handlebars.CustomHandlebarsHelper```