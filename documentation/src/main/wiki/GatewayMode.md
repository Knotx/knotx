# Gateway Mode
The Gateway Mode allows to place your micro-services directly inside an Knot.x instance. It simplifies
a client-side integration reducing an infrastructure needs (an additional application server for 
micro-services is not required). The Gateway Mode makes Knot.x the right tool for both client-side 
and backend-side integrations.

The Gateway Mode provides a way of processing requests alternative to that presented in the 
[[Knot Routing section|KnotRouting]]. In this mode, Knots don't have to operate on 
[[Fragments|Splitter]]. An HTTP request body is available in fragments (one RAW fragment).

## Configuration
The Gateway Mode is not enabled by default. [[Server configuration file|Server#How-to-configure]] for
production defines only default Knot.x request flow (with Repository and Splitter). 

Example Web API endpoint is configured and delivered in [[Knot.x demo|RunningTheDemo]].
This example endpoint is configured like below:

```
"customFlow": {
  "routing": {
    "GET": {
      "items": [
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
    "PUT": {
      "items": [
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
     }
  },
  "responseProvider": "knotx.gateway.responseprovider"
}
```

Here, the Gateway Mode works on paths starting with `/customFlow/`.

Lets test our endpoint with calls:

Simple GET call: 
```
> curl -X GET localhost:8092/customFlow/
{"message":"This is a sample custom flow response"}
```

Simple PUT call with body message:
```
> curl -X PUT -d'{ "message": "Hello from Web API!" }' localhost:8092/customFlow/
{ "message": "Hello from Web API!" }
```

Our endpoint behaviour is coded as simple [[Knot|Knot]] extension: `io.knotx.gateway.RequestProcessorKnotVerticle`.

## Processing

First, a Knot, called a Gateway Knot, checks if the request is allowed or not. The rest of the routing 
configuration can be customized to your needs.

After the routing is over, the response is returned from the verticle called Response Provider.

Depending on your routing implementation, you can use the Gateway Mode to return the external services responses
in a raw form (e.g. JSON), while still having custom Knots, like authorization, to process the request.
