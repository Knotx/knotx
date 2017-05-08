# Gateway Mode

The gateway mode provides an alternative way of processing requests, alternative to than presented in [[Knot Routing section|KnotRouting]].
In this mode, Knots don't have to operate on Fragments. 

## Example configuration

Configuration of gateway mode takes place in the [[Server Configuration file|Server#How-to-configure]]. 

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

Here, the gateway mode will work on paths beginning with `/customFlow`.
First, a Knot, called a Gateway Knot, sets the Knot Context of the request. The rest of the routing configuration can be customized to your needs. 

After the routing is over, the response is returned from a verticle called Response Provider. 

//TODO an image of the routing simillar to this on Knot Routing page

Depending of your routing implementation, you can use the Gateway Mode to return the external services response 
in raw form (e.g. JSON), while still having custom Knots, like authorization, to process the request.

An example usage of Gateway Mode is present in the [[demo application|RunningTheDemo]]. 
