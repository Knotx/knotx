#Snippet processing
##Overview
In order to fetch the data for snippet different services can be called. Decision which services to call is made depends on service path data attribute and incoming request method.
The result of the call is always available in **_result** context and it contains the exactly same structure as the response from the service - it might be JSON Object or Array.

###GET services calls
When service path is marked as data-uri-**get**, call will be executed only if http method of incoming request is GET.
```html
<script data-api-type="templating" type="text/x-handlebars-template"
    data-uri-get="/service/sample">
        <h1>Welcome</h1>
        <h2>{{_result.welcomeMessage}}</h2>
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
    <h1>{{labelsrepository._result.welcomeInCompetition}}</h1>
    {{#if formresponse.result}}
    <p>{{labelsrepository._result.thankYouForSubscribingToCompetition}}</p>
    {{else}}
    <p>{{labelsrepository._result.generalInfo}}</p>
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
  <h1>{{labelsRepository._result.welcomeInCompetition}}</h1>
  {{#if formResponse.result}}
     <p>{{labelsRepository._result.thankYouForSubscribingToCompetition}}</p>
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
  <h1>{{labelsRepository._result.subscribeToNewsletter}}</h1>
  {{#if formResponse.result}}
    <p>{{labelsRepository._result.thankYouForSubscribingToNewsletter}}</p>
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


