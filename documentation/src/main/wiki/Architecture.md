# Architecture

A simplified description of Knot.x can be `a tool which converts a static page (template) into 
dynamic page driven by data provided by microservices`.
Page visitor requests are directed to Knot.x. Then Knot.x calls [[Repository Connector|RepositoryConnectors]] for 
the template, split this template to static / dynamic fragments and process those fragments. Finally 
it calls external services if required.

The diagram below depicts Knot.x request flow at very high level point of view.

[[assets/knotx-overview.png|alt=Knot.x Overview]]

Thanks to modular nature, Knot.x can be easily extended by project-specific mechanics (see [[Knots|Knot]]).
Knot.x can easily adapt responses with different formats to required one (see e.g. [[Service Adapters|ServiceAdapter]]).
Additionally Knot.x does not concentrate on HTTP protocol so even custom protocols can be used if required.
