# HTML Fragment Splitter
Fragment Splitter reads [[Knot Context|Knot]] that contains a HTML Template retrieved from 
the Repository, splits it into static and dynamic Fragments, updates Knot Context and returns back 
to the caller. We also call those dynamic Fragments "Snippets".

## How does it work?
Splitter splits HTML Template using regexp 
`<${SNIPPET_TAG_NAME}\s+data-knotx-knots\s*=\s*"([A-Za-z0-9-]+)"[^>]*>.+?</${SNIPPET_TAG_NAME}>`.
This is efficient method, however it has a limitation that one should remember about. Knot.x just 
scans the markup for the opening of snippet tag (`<${SNIPPET_TAG_NAME}>`) and the first occurrence of 
the end of that tag (`</${SNIPPET_TAG_NAME}>`). Because of that `${SNIPPET_TAG_NAME}` should be
configured wisely. Good example is `knotx:snippet`. Do not use standard html tags like `div` or 
`span` etc. The default value of snippet tag name (`${SNIPPET_TAG_NAME}`) is `script` and you may 
configure it to any value you want (see [configuration section](#how-to-configure)).

During the HTML splitting, all matched snippet tags are converted into Fragments containing list of 
supported [[Knots|Knot]] declared in `data-knotx-knots` attribute. HTML parts below, above and 
between matched snippets are converted into Fragments without Knot support (static Fragments). 
It means that they are not supposed to be processed by Knots. See example for more details.

**Splitter requires `data-knotx-knots` attribute to be the first attribute in the snippet tag.**

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
```html
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
```html
    <script data-knotx-knots="services,handlebars"
            data-knotx-service="first-service"
            type="text/knotx-snippet">
      <div class="col-md-4">
        <h2>Snippet - {{_result.message}}</h2>
      </div>
    </script>
```
**Fragment 3** (identifier = "_raw")
```html
  </div>
</body>
</html>
```

More details about Fragments can be found in the next section.

### Fragment
Fragment contains: 
- list of supported Knots (list of [[Knot Election Rules|Knot]]), 
- Fragment Content (matched snippet or simple HTML)
- Fragment Context (JSON with a progress state)

Fragments matching snippet name tag declare Knots used while further processing (see [[Knots routing|KnotRouting]]). 
They can communicate with external services via [[Adapters|Adapter]], evaluate templates using 
Handlebars and so on. Every Knot defines value ([[Knot Election Rule|Knot]]) for `data-knotx-knots` 
attribute which determines if it will process particular Fragment or not.

Fragments not matching snippet tag are not supposed to be processed while Knots routing. They are 
used at the end of processing to assemble final HTML result (see [[Fragment Assembler|Assembler]]).

## How to configure?

See the [FragmentSplitterOptions](https://github.com/Cognifide/knotx/blob/master/documentation/src/main/cheatsheet/cheatsheets.adoc#fragmentsplitteroptions) for all configuration options and its defaults.

**Important - when specifying `snippetTagName` remember to not use standard HTML tags like `div`, `span`, etc.
Knot.x splits an HTML into fragments by parsing it as a string to get the best possible performance. 
It simply search the text for the opening and first matching closing tag. It does not analyse the text 
as HTML. So, if you use `div` as fragmentTagName, and inside your will use multiple `div` tags too, 
then it will not pick the one that matches first opening, instead it will get the fragment up to the 
first closing `div` tag. It will result in a broken HTML structure.**
