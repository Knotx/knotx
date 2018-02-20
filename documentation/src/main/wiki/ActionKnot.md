# Action Knot
Action Knot is an [[Knot|Knot]] implementation responsible for forms submissions handling. It supports
simple (without file upload) forms including redirection to successful pages and multi-step forms flows.
It provides also a service error handling mechanism.

## How does it work?
Action Knot is used with default Knot.x settings while both GET and POST client request processing.
It transforms a form template to Knot.x agnostic one for GET requests. When client submits the form
Action Knot calls configured [[Adapter|Adapter]] and based on its response redirect the client to a
successful / error / next step page.

Let's describe Action Knot behaviour with following example.

### Example
ActionKnot processes Fragments having `form-{NAME}` in `data-knotx-knots` attribute,
where `{NAME}` is a unique name of a form (assuming there may be more than one form on a single page
it is used to distinguish a requested snippet). {NAME} can contain only small and capital letters. So
[[Knot Election Rule|Knot]] for Action Knot is pattern `form-[a-zA-Z]`.

The client opens a `/content/local/login/step1.html` page. The final form markup returned by Knot.x looks like:

```html
<form method="post">
  <input name="_frmId" value="1" type="hidden">
  <input name="email" value="" type="email">
  <input value="Submit" type="submit">
 </form><p>Please provide your email address</p>

 <div>
  <strong>Pro tip: All emails that starts with <kbd>john.doe</kbd> will be accepted.</strong>
 </div>
```

There are no Knot.x specific attributes in a final markup besides one **hidden input tag**.

This is how form looks in the repository:

```html
<script data-knotx-knots="form-1" type="text/knotx-snippet">
  {{#if action._result.validationErrors}}
  <p class="bg-danger">Email address does not exists</p>
  {{/if}}
  <p>Please provide your email address</p>
  <form data-knotx-action="step1" data-knotx-on-success="/content/local/login/step2.html" data-knotx-on-error="_self" data-knotx-adapter-params='{"myKey":"myValue"}' method="post">
    <input type="email" name="email" value="{{#if action._result.validationError}} {{action._result.form.email}} {{/if}}" />
    <input type="submit" value="Submit"/>
  </form>
  <div>
    <strong>Pro tip: All emails that starts with <kbd>john.doe</kbd> will be accepted.</strong>
  </div>
</script>
```

Now we can explain how and why this additional hidden input `_frmId` with a value `1` appears . It
is automatically added by Action Knot and is used to distinguish a requested form during submission process
(there could be more than one form at the same template). Its value comes from a script's `data-knotx-knots`
attribute - it retrieve a `{NAME}` value from `data-knotx-knots="form-{NAME}"`.

Following data attributes are available in the `<form>` tag with described purpose:
- `data-knotx-action` - this is a name of an [[Action Adapter|ActionAdapter]] that will be used to handle submitted data.
It is similar concept as `data-knotx-service-{NAME}` in [[Service Knot|ServiceKnot]]. In the example,
Action Handler registered under name `step1` will handle this form data submission.
- `data-knotx-on-{SIGNAL}` - name of a [Signal](#Signal) that should be applied. In the example
there is one signal success with the value `'/content/local/login/step2.html'` and one signal error
with the value `'_self'`. Signal `'_self'` means that after error response (error signal returned)
the client will stay on the same page.
- `data-knotx-adapter-params` - JSON Object that can be passed to the corresponding `Adapter`. It will be
available in `AdapterRequest` as `adapterParams`. 


### Signal
Signal is basically a decision about further request processing. Value of the signal can be either:
- `path` of a page that user should be redirected to after processing form submit,
- `_self` - that indicates that there will not be redirect, instead current page will be processed (generated view for instance).
In other words, the page processing will be delegated to next [[Knot|Knot]] in the graph.

## How to configure?
For all configuration fields and their defaults consult [ActionKnotOptions](https://github.com/Cognifide/knotx/blob/master/documentation/src/main/cheatsheet/cheatsheets.adoc#actionknotoptions)

In short, by default, server:
- Listens on event bus address `knotx.knot.action` on messages to process
- It communicates with the [Action Adapter|ActionAdapter] on event bus address `test` for processing POST requests to the services
  - It pass the example parameter to the adapter
  - It pass `Cookie` request header to the adapter
  - It returns `Set-Cookie` response header from adapter
- It uses `snippet-identifier` value as hidden field name that's used by Action Knot to identify form that sent POST request

### Vert.x Event Bus delivery options

While HTTP request processing, Action Knot calls Adapter using 
[Vert.x Event Bus](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/EventBus.html). The `config` field can contain 
[Vert.x Delivery Options](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/DeliveryOptions.html) related to the event 
bus. It can be used to control the low level aspects of the event bus communication like timeouts, headers, message 
codec names.

The `deliveryOptions` need to be added in the following place, of the Action Knot configuration to define the 
timeout for the Adapter response.
```
{
  "options": {
    "config": {
      "deliveryOptions": {
        "timeout": 15000,
         ...
      },
      ...
```
