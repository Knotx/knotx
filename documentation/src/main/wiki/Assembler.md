#Fragment Assembler
Fragment Assembler joins all Fragments into the final output. It occurs after Knots processing and
just before the response to the site visitor.

##How does it work?
Fragment Assembler reads Knot Context having Fragments, joins all Fragments into one output, updates 
Knot Context and returns back to the caller. See examples below for more details.

###How are Fragments joined?
Lets explain this join process with the example. Fragment assembler reads Knot Context having 
three Fragments:
```html
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
```
```html
  <h2>this is webservice no. 1</h2>
  <div>message - a</div>
```
```html
</body>
</html>
```
Fragment Assembler joins all those Fragments into one output:
```html
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
  <h2>this is webservice no. 1</h2>
  <div>message - a</div>
</body>
</html>
```
###How does Assembler join unprocessed Fragments?
Lets imagine that some Fragments were not processed and they still contain dynamic snippets definitions. 
It is not expected behaviour so Fragment Assembler must handle it. There are three possible strategies 
provided: `SIMPLE`, `EXTRACT`, `CLEAR`. They can be configured with entry `assemblyStrategy`.
See Fragments below and then compare those strategies. 
```html
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
```
```html
<script data-knot-types="services,handlebars" data-service="first-service" type="text/x-handlebars-template">
  <h2>{{message}}</h2>
  <div>{{body.a}}</div>
</script>
```
```html
</body>
</html>
```
#### SIMPLE strategy
It is the simplest strategy - it leaves fragments untouched. So our example response will look like:
```
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
<script data-knot-types="services,handlebars" data-service="first-service" type="text/x-handlebars-template">
  <h2>{{message}}</h2>
  <div>{{body.a}}</div>
</script>
</body>
</html>
```
#### EXTRACT strategy
It gets dynamic tag body instead of full dynamic tag definition. So our example response will look like:
```
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
<!-- SNIPPET EXTRACTED START -->
  <h2>{{message}}</h2>
  <div>{{body.a}}</div>
<!-- SNIPPET EXTRACTED STOP -->
</body>
</html>
```
#### CLEAR strategy
It removes all Fragments which contains dynamic tag definitions.
```
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
<!-- SNIPPET CLEARED -->
</body>
</html>
```

##How to configure?
Fragment Assembler is deployed using Vert.x service factory as a separate [verticle](http://vertx.io/docs/apidocs/io/vertx/core/Verticle.html) and it's shipped with default configuration.

Default configuration shipped with the verticle as `io.knotx.FragmentAssembler.json` file available in classpath.
```json
{
  "main": "com.cognifide.knotx.knot.assembler.FragmentAssemblerVerticle",
  "options": {
    "config": {
      "address": "knotx.core.assembler",
      "assemblyStrategy": "EXTRACT"
    }
  }
}
```
In short, the default configuration just defines event bus address on which the Assembler listens
for jobs to process and strategy how to behave if any fragment contains unprocessed snippet definition
tag.

Detailed description of each configuration option is described in the next subsection.

### Fragment Assembler config

| Name                        | Type                                | Mandatory      | Description  |
|-------:                     |:-------:                            |:-------:       |-------|
| `address`                   | `String`                            | &#10004;       | Event bus address of the Fragment Assembler verticle. |
| `assemblyStrategy`          | `Enum(SIMPLE,EXTRACT,CLEAR)`        | &#10004;       | Strategy for fragments with unprocessed dynamic tags. |

