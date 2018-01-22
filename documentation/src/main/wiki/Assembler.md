# Fragment Assembler
Fragment Assembler joins all Fragments into the final output. It's executed at the end of 
all suitable Knots processing, just before generating the response to the page visitor.

## How does it work?
Fragment Assembler reads Knot Context having Fragments, joins all Fragments into one string, updates 
Knot Context and returns back to the caller. See examples below for more details.

### How Fragments are being joined?
Lets explain process of fragments join using example. Fragment Assembler reads Knot Context having 
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
Fragment Assembler joins all those Fragments into one string:
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
### How does Assembler join unprocessed Fragments?
Lets imagine that some Fragments were not processed and they still contain dynamic snippets definitions. 
It is not expected behaviour, so Fragment Assembler must handle it. There are three possible strategies 
provided: `AS_IS`, `UNWRAP`, `IGNORE`. They can be configured with entry `unprocessedStrategy`.
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
<script data-knotx-knots="services,handlebars" data-knotx-service="first-service" type="text/knotx-snippet">
  <h2>{{message}}</h2>
  <div>{{body.a}}</div>
</script>
```
```html
</body>
</html>
```
#### AS_IS strategy
It leaves fragments untouched. So, result of join will look like below for our example:
```
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
<script data-knotx-knots="services,handlebars" data-knotx-service="first-service" type="text/knotx-snippet">
  <h2>{{message}}</h2>
  <div>{{body.a}}</div>
</script>
</body>
</html>
```
#### UNWRAP strategy
It unwraps the snippet, by removing `<script>` tag leaving just body of the snippet. So, the result of 
join will look like this:
```
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
<!-- SNIPPET UNWRAPED START -->
  <h2>{{message}}</h2>
  <div>{{body.a}}</div>
<!-- SNIPPET UNWRAPED STOP -->
</body>
</html>
```
#### IGNORE strategy
It ignores all Fragments which contains dynamic tag definitions.
```
<html>
<head>
  <title>Test</title>
</head>
<body>
<h1>test</h1>
<!-- SNIPPET IGNORED -->
</body>
</html>
```

## How to configure?
See the [Fragment Assembler Options](https://github.com/Cognifide/knotx/blob/master/knotx-knot/knotx-knot-fragment-splitter/src/main/asciidoc/dataobjects.adoc) for all configuration options and its defaults.
