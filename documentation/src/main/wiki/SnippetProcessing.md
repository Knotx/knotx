#Snippet processing
##Overview
In order to fetch the data for snippet different services can be called. Decision which services to 
call is made depends on service path data attribute and incoming request method.
The result of the call is always available in **_result** context and it contains the exactly 
same structure as the response from the service - it might be JSON Object or Array.

###Caching service calls
Snippet might consists of more than one service call. It's also possible that there are multiple 
snippets on the page, each using same services. Knot.x does caching results of service calls to avoid multiple calls for the same data.
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

All placeholders are always substituted with encoded values according to the RFC standard. However, there are two exceptions:

- Space character is substituted by **%20** instead of **+**
- Slash character **/** remains as it is

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


##### Routing request basing on request type
Knot.x provides a mechanism that causes different approach to HTTP request and XmlHttpRequest. 
When Knot.x is requested with HTTP default processing path is started and flow works as usually. 
When Knot.x is requested with XHR only requested part of a template is processed and returned.

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
    data-service-myservice="/service/myService">
  <h1>Welcome!</h1>

  {{#if myservice._response.statusCode == 503
    <p>Failed. Service was unavailable.</p>
  {{else}}
    <p>Success.</p>
  {{/if}}

</script>
```


