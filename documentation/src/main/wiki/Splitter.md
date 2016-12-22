#HTML Fragment Splitter
HTML Fragment Splitter divides HTML template into static and dynamic chunks. Those chunks (fragments) 
goes to Knot context and can be processed later by [[Knots|Knot]].

##How does it work?
HTML Fragment Splitter gets a Knot context as input and responds with the modified Knot context. It 
divides HTML using regexp `<script\s+data-knot-types\s*=\s*"([A-Za-z0-9-]+)"[^>]*>.+?</script>` into
static and dynamic fragments. So all `script` tags with a `data-knot-types` attribute are converted to 
dynamic fragments. **According to performance reasons Splitter requires `data-knot-types` 
attribute to be the first attribute in the `script` tag.**
All HTML markup outside script tags is considered as static fragments.

Fragment contains an *list of knot types, a content (a template chunk) and a context*. The *list of knot types* contains `data-knot-types` 
attribute values (list of knot types separated with commas) or `_raw` value for static fragments. It can be used by Knots to select required fragment / fragments 
(performance enhancement) without additional snippet content processing. The *content* contains 
script tag with its content for dynamic fragments or static HTML content for static fragments. 
The *context* can be omitted at this moment.

At the end Splitter updates the Knot context with the list of fragments and returns it to further processing.

####Example
A site visitor requests for page *example.html* page. Knot.x fetches a page template from Repository and asks 
Splitter to retrieve fragments from the template: 
```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Knot.x example</title>
</head>
<body>
  <div class="row">
    <script data-knot-types="services,handlebars"
            data-service="first-service"
            type="text/x-handlebars-template">
      <div class="col-md-4">
        <h2>Snippet - {{_result.message}}</h2>
      </div>
    </script>
  </div>
</body>
</html>
```
Splitter divides page into 3 following fragments:

**Fragment 1** (identifier = "_raw")
```
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Knot.x example</title>
</head>
<body>
  <div class="row">
```
**Fragment 2** (identifier = "templating")
```
    <script data-knot-types="services,handlebars"
            data-service="first-service"
            type="text/x-handlebars-template">
      <div class="col-md-4">
        <h2>Snippet - {{_result.message}}</h2>
      </div>
    </script>
```
**Fragment 3** (identifier = "_raw")
```
  </div>
</body>
</html>
```

##How to configure?
Splitter is deployed as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html), 
depending on how it's deployed. You need to supply **Splitter** configuration.

JSON presented below is an example how to configure Splitter deployed as standalone fat jar:
```json
{
  "address": "knotx.core.splitter"
}
```
When deploying **Splitter** using Knot.x starter verticle, configuration presented above should 
be wrapped in the JSON `config` section:
```json
"verticles" : {
  ...,
  "com.cognifide.knotx.splitter.FragmentSplitterVerticle": {
    "config": {
      "PUT YOUR CONFIG HERE"
    }
  },
  ...,
}
```
Detailed description of each configuration option is described in the next subsection.

### Splitter options

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Splitter verticle. |