# HTML Fragment Splitter
Fragment Splitter reads [[Knot Context|Knot]] having a HTML Template retrieved from Repository using configured connector, splits it into 
static and dynamic Fragments, updates Knot Context and returns back to the caller.

## How does it work?
It splits HTML Template using regexp `<script\s+data-knotx-knots\s*=\s*"([A-Za-z0-9-]+)"[^>]*>.+?</script>`.
All matched `script` tags are converted into Fragments containing list of supported [[Knots|Knot]] 
declared in `data-knotx-knots` attribute. HTML parts below, above and between matched scripts are 
converted into Fragments without Knot support. It means that they are not supposed to be processed
by Knots. See example for more details.

**Splitter requires `data-knotx-knots` attribute to be the first attribute in the `script` tag.**

### Example
Fragment Splitter reads Knot Context with HTML Template:
```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>Knot.x example</title>
</head>
<body>
  <div class="row">
    <script data-knotx-knots="services,handlebars"
            data-knotx-service="first-service"
            type="text/knotx-snippet">
      <div class="col-md-4">
        <h2>Snippet - {{_result.message}}</h2>
      </div>
    </script>
  </div>
</body>
</html>
```
and splits Template into three following Fragments:

**Fragment 1** (knots = "_raw")
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
**Fragment 2** (knots = "services,handlebars")
```
    <script data-knotx-knots="services,handlebars"
            data-knotx-service="first-service"
            type="text/knotx-snippet">
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

More details about Fragments you can find in next section.

### Fragment
Fragment contains: 
- list of supported Knots (list of [[Knot Election Rules|Knot]]), 
- Fragment Content (matched script or simple HTML)
- Fragment Context (JSON with a progress state)

Fragments matching `script` tag declare Knots used while further processing (see [[Knots routing|KnotRouting]]). 
They can communicate with external services via [[Adapters|Adapter]], evaluate templates using 
Handlebars and so on. Every Knot defines value ([[Knot Election Rule|Knot]]) for `data-knotx-knots` 
attribute which determines if it will process particular Fragment or not.

Fragments not matching `script` tag are not supposed to be processed while Knots routing. They are 
used at the end of processing to assemble final HTML result (see [[Fragment Assembler|Assembler]]).

## How to configure?
Splitter is deployed using Vert.x service factory as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default configuration.

Default configuration shipped with the verticle as `io.knotx.FragmentSplitter.json` file available in classpath.
```json
{
  "main": "io.knotx.splitter.FragmentSplitterVerticle",
  "options": {
    "config": {
      "address": "knotx.core.splitter"
    }
  }
}
```
In short, the default configuration just defines event bus address on which the Splitter listens for jobs to process.

Detailed description of each configuration option is described in the next subsection.

### Splitter config

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Splitter verticle. |
