Action Adapter is Component of a system, that mediate communication between Knot.x (ActionKnot) and external 
Services that are responsible for handling form submitting.

Action Adapter is a project custom part of a system, however Knot.x comes with default `HttpActionAdapter`.
Action Adapter entry are:

- `ClientRequest` - object with all data of an original request,
- `params` - all additional params defined in configuration.

Action Adapter result contract is Json Object with fields:
- `clientResponse` - Json Object, `body` field of this response is suppose to carry on the actual response from the mocked service,
- `signal` - string that defines how original request processing should be handled, see more in [[Action Knot|ActionKnot]].

# TODO
- Action Adapter API
- how to build your own action adapter
