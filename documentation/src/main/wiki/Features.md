# Features

## Requests grouping

Template obtained from the repository may contain many snippets that will trigger microservice calls for data. There is a chance that some of the snippets will have the same `data-call-uri` attribute set, meaning they will request data from the same source.
In such case only one call to microservice shall be made and data retrieved from service call should be applied to all snippets sharing the same `data-call-uri`.

Example:
Let's assume that we obtained the following template from repository:
```html
<div>
<script data-api-type="templating" data-call-uri="/searchService" type="text/x-handlebars-template">
    <div>{{search.term}}</div>
</script>
</div>
...
<div>
<script data-api-type="templating" data-call-uri="/searchService" type="text/x-handlebars-template">
    <ul>
    {{#each search.results}}
      <li>{{result}}<li>
    {{/each}}
    </ul>
</script>
</div>
```
In this case only one call to microservice will be made, since both snippets share the same `data-call-uri`. Data retrived from `/searchService` will be applied to both snippets.

Notice: The following `data-call-uri` attributes
```
/searchService?q=first
```
```
/searchService?q=second
```
would trigger two calls for data because of the difference in query strings, even though the path to service is the same in both.
