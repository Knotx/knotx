#HTML Fragment Splitter
HTML Fragment Splitter divides HTML template into static and dynamic chunks. Those chunks (fragments) 
goes to Knot context and can be processed later by [Knots](#Knot).

##How does it work?
HTML Fragment Splitter gets a Knot context as input and responds with the modified Knot context. It 
divides HTML using regexp `<script\s+data-api-type\s*=\s*"([A-Za-z0-9-]+)"[^>]*>.+?</script>` into
static and dynamic fragments. So all `script` tags with a `data-api-type` attribute are converted to 
dynamic fragments. <b>According to performance reasons Splitter requires `data-api-type` 
attribute to be the first attribute in the `script` tag.</b>
All HTML markup outside script tags is converted static fragments.

Fragment contains an identifier, a content (a template chunk) and a context. The <b>identifier</b> has `data-api-type` 
attribute value or `_raw` for static fragments. It can be used by Knots to select required fragment / fragments 
(performance enhancement) without additional snippet content processing. The <b>content</b> contains 
script tag with its content for dynamic fragments or static HTML content for static fragments. 
The context can be omitted at this moment.

At the end Splitter updates the Knot context with the list of fragments and returns it to further processing.

##How to configure?
Splitter is deployed as separate verticle. It listens to communication bus events on a configured address.
```json
"com.cognifide.knotx.splitter.FragmentSplitterVerticle": {
  "config": {
    "address": "knotx.core.splitter"
  }
}
```

####Splitter options

| Name                        | Type                                | Mandatory | Description  |
|-------:                     |:-------:                            |:-------:  |-------|
| `address`                 | `String`                      | &#10004;       | Address on which Splitter will listen for events |