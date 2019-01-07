# High Level Architecture

Knot.x is modular easily extensible and adaptable platform which assembles static and dynamic
content from multiple sources.

Knot.x hides its internal complexity and allows to use it with very basic knowledge about Knot.x
Core modules. Custom features can be easily added to Knot.x with [[Knots|Knot]] extension point, 
that listen to the event bus and handle custom business logic.

Diagram below depicts high level Knot.x architecture.

[[assets/knotx-high-level-architecture.png|alt=High Level Architecture]]

Custom business logic can be encapsulated in dedicated Knots.

A Knot is a module which defines a custom step in the process of [[request routing|KnotRouting]].
It can process custom fragments, invoke Adapters and redirect site visitors to new site or error page.
More information about Knots can be found in the [[dedicated section|Knot]].
