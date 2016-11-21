# Communication Flow

A diagram below depicts a very basic request flow inside Knot.x:
[[assets/knotx-modules-basic-request-flow.png|alt=Knot.x Request Flow]]

This simple request fetches template and injects dynamic data form Service layer in order to render dynamic page.

More complex processing is presented below, where request travel through several [[Knots|Knot]] before final response is ready.
[[assets/knotx-modules-advanced-request-flow.png|alt=Knot.x Request Flow]]
 
The diagram presents that a request from a user hits Server first. Server fetches a template from a 
repository, then request for the template fragments and at the end calls Knots on matched route - 
see [[Knots routing|KnotRouting]] for more details. Server calls are synchronous, it means that before a 
next call the current one must finished. The synchronous nature of a Server does not prevent non-blocking 
implementation - Server still follows the asynchronous programming principles.

[[Knots|Knot]] can communicate with services using Adapters. When using Knot.x our recommendation is to write
 custom Adapters for cases when a service response must be adapted to required format or when one service call depends on another call. 

Knots can perform their jobs both synchronously and asynchronously. Service Adapters calls in Service Knot 
are asynchronously - GET service calls are independent so there is no reason to wait for a service response 
before the next call.
