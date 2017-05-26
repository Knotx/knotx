# Gateway Mode

The gateway mode provides a way of processing requests alternative to that presented in the [[Knot Routing section|KnotRouting]].
In this mode, Knots don't have to operate on [[Fragments|Splitter]].

## Example configuration

Configuration of a gateway mode takes place in the [[Server Configuration file|Server#How-to-configure]].

```
"customFlow": {
  "routing": {
    "GET": [
      {
        "path": "/customFlow/.*",
        "address": "knotx.gateway.gatewayknot",
        "onTransition": {
          "next": {
            "address": "knotx.gateway.requestprocessor"
          }
        }
      }
    ]
  },
  "responseProvider": {
    "address": "knotx.gateway.responseprovider"
  }
}
```

Here, the gateway mode will work on paths starting with `/customFlow`.
First, a Knot, called a Gateway Knot, sets the Knot Context of the request. The rest of the routing configuration can be customized to your needs.

After the routing is over, the response is returned from the verticle called Response Provider.

Depending on your routing implementation, you can use the Gateway Mode to return the external services response
in a raw form (e.g. JSON), while still having custom Knots, like authorization, to process the request.
An example is shown in the Knot.X example application, where a custom `RequestProcessorKnot` simulates a call to an external service to fetch a JSON message.

You can see an example usage of the Gateway Mode in the [[demo application|RunningTheDemo]].
