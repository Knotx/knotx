[TO REVIEW]

# Communication Flow

A diagram below depicts a request flow inside Knot.x:

[[assets/knotx-modules-request-flow.png|alt=Knot.x Request Flow]]
 
The diagram presents that a request from a user hits Server first. Server fetches a template from a 
repository, then request for the template fragments and at the end calls Knots on matched route - 
see [Knots routing](#KnotRouting) section. Server calls are synchronous, it means that before a 
next call the current one must finish. The synchronous nature of Server does not prevent non-blocking 
implementation - Server still follows the asynchronous programming principles.

Knots can communicate with services using Adapters. Knot.x recommends custom Adapters for cases when
a service response must be adapted to required format or when one service call depends on another call. 

Knots can perform their jobs both synchronously and asynchronously. Service Adapters calls in View Knot 
are asynchronously - GET service calls are independent so there is no reason to wait for a service response 
before next call.