ActionKnot is Knot.x system component that is responsible for handling forms submission.

Example:
```html
<script data-api-type="form-1" type="text/x-handlebars-template">
  {{#if action._result.validationErrors}}
  <p class="bg-danger">Email address does not exists</p>
  {{/if}}
  <p>Please provide your email address</p>
  <form data-knotx-action="step1" data-knotx-on-success="/content/local/login/step2.html" data-knotx-on-error="_self" method="post">
    <input type="email" name="email" value="{{#if action._result.validationError}} {{action._result.form.email}} {{/if}}" />
    <input type="submit" value="Submit"/>
  </form>
  <div>
    <strong>Pro tip: All emails that starts with <kbd>john.doe</kbd> will be accepted.</strong>
  </div>
</script>
```

ActionKnot processes only those fragments that `<script>` tag has defined `data-api-type="form-{NAME}"` parameter, 
where `{NAME}` is a unique name of a form (assuming there may be more than one form on a single page it is used to distinguish requested snippet).
In the example above, `{NAME}` is `1`.

Following data attributes are available in the `<form>` tag with described purpose:

- `data-knotx-action` - this is a name of an [[Action Adapter|ActionAdapter]] that will be used to handle submitted data. 
It is similar concept as `data-service-{NAME}` in [[View Knot|ViewKnot]]. In the example above, 
Action Handler registered under name `step1` will handle this form data submit.
- `data-knotx-on-{SIGNAL}` - name of a [Signal](#Sginal) that should be applied.

#### Signal
Signal is basically a decision about further request processing. Value of the signal can be either:

- `url` of a page that user should be redirected to after processing form submit,
- `_self` - that indicates that there will not be redirect, instead current page will be processed (generated view for instance). 
In other words, the page processing will be delegated to next [[Knot|Knot]] in the graph.
