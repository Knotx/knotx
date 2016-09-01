#Snippet processing
##Overview
Different services can be called by snippet, depending on service path data attribute and incoming request method.

###GET services calls
When service path is marked as data-uri-**get**, call will be executed only if http method of incoming request is GET   
```html
<script data-api-type="templating" type="text/x-handlebars-template" 
    data-uri-get="/service/sample">
        <h1>Welcome</h1>
        <h2>{{welcomeMessage}}</h2>   
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
When service path is marked as data-uri-**all**, call will be executed with http method of incoming request.
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

### Multiple forms processing
Snippet can contain an **data-id** attribute. This attribute allows template engine to process multiple forms in one template. Value of **data-id** defines which snippet should be processed by combining it with **_id** parameter sent with POST method.

**Example:**

Snippet in template below will call /service/subscribeToCompetition only when request method is POST and **_id** parameter value is competition-form.
/service/subscribeToNewsletter will be called only when request method is POST and **_id** parameter value is newsletter-form.
Notice that example below uses Enable calling more than one service for snippet data feature.

**Snippet:**
```html
<script data-api-type="templating" 
  type="text/x-handlebars-template" 
  data-uri-post-formResponse="/service/subscribeToCompetition" 
  data-uri-all-labelsRepository="/service/labelsRepository"
  data-id="competition-form">
  <h1>{{labelsRepository.welcomeInCompetition}}</h1>
  {{#if formResponse}}
     <p>{{labelsRepository.thankYouForSubscribingToCompetition}}</p>
  {{else}}
      <p>Please subscribe to our new competition:</p>
      <form method="post">
        <input type="name" name="name" />
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
  <h1>{{labelsRepository.subscribeToNewsletter}}</h1>
  {{#if formResponse}}
    <p>{{labelsRepository.thankYouForSubscribingToNewsletter}}</p>
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


