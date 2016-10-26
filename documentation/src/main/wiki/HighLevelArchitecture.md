[WORKING IN PROGRESS]

Knot.x uses two main concepts which make it easily customizable: dedicated modules with single 
responsibility and communication / event bus. Those two factors make Knot.x very expendable 
and adaptable.

Knot.x delivers a default configuration which hides its internal complexity and allows to use it 
in projects with limited knowledge about Knot.x Core modules. Project specific features can be added 
to Knot.x as external adapters. Those adapters register in the event bus and handle project
specific business logic.

Diagram below depicts high level Knot.x architecture:

[[assets/highLevelArchitecture-external.png|alt=High Level Architecture]]

Every business logic functionality is encapsulated in dedicated adapters. There are two main Knot.x
Core adapter types: Action and Service Adapters. Knot.x recommends to create those very small adapters
to communicate with external services or perform some business logic.

For cases where Knot.x can consume service responses without any modifications no custom adapters 
are required. Internally Knot.x provides default HTTP adapters which can communicate with services.
On diagram it is marked with arrow between Knot.x and cloud services.