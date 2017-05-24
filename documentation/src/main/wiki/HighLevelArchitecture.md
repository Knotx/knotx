# High Level Architecture

Knot.x is modular easily extensible and adaptable platform which assembles static and dynamic
content from multiple sources.

Knot.x hides its internal complexity and allows to use it with very basic knowledge about Knot.x
Core modules. Custom features can be easily added to Knot.x with two flexible extension points: [[Knots|Knot]]
and [[Adapters|Adapter]], that listen to the event bus and handle custom business logic.

Diagram below depicts high level Knot.x architecture.

[[assets/knotx-high-level-architecture.png|alt=High Level Architecture]]

Custom business logic can be encapsulated in dedicated Knots / Adapters.

A Knot is a module which defines a custom step in the process of [[request routing|KnotRouting]].
It can process custom fragments, invoke Adapters and redirect site visitors to new site or error page.
More information about Knots can be found in the [[dedicated section|Knot]].

[[Adapters|Adapter]] are used to communicate with external services. Knot.x recommends to create dedicated Adapter
every time we need to perform some business logic or adapt service response to other format.

If REST service responses can be used as is without any changes no custom Adapters will be required.
Knot.x provides generic HTTP Adapters ([[Http Service Adapter|HttpServiceAdapter]] and ) which can communicate with services.
It is marked on diagram with arrow between Knot.x and Services Layer.
