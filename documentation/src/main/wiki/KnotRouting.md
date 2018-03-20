# Knot Routing

A Request from a user goes first to the [[Server|Server]].
Server passes the request to Knot.x modules until processing is finished and result can be returned.
The request flow is described in [[Communication Flow|CommunicationFlow]] section.

Server uses Router from [Vert.x-Web](http://vertx.io/docs/vertx-web/java/) library to define which
[[Knots|Knot]] should participate in request processing and in what order.

A router takes a request, finds the first matching route for that request and passes the
request to that route. The route has a Knot associated with it, which then receives the request.
Knot does his job and returns response to the Server. Server then can end processing or pass request
to the next matching Knot.


Routes entries example configuration:
```
"routing": {
  "GET": {
    "items": [
      {
        "path": "/secure/.*",
        "address": "knotx.knot.authorization",
        ...
      },
      {
        "path": "/forms/.*",
        "address": "knotx.knot.action",
        ...
      },
      {
        "path": "/view/.*",
        "address": "knotx.knot.service",
        ...
      }
    ]
  },
  "POST": {
    "items": [
      {
        "path": "/secure/.*",
        "address": "knotx.knot.authorization",
        ...
      },
      {
        "path": "/forms/.*",
        "address": "knotx.knot.action",
        ...
      }
    ]
  }
}
```
Knot.x understands Knot as a vertex in a graph which has one input and many outputs. Those outputs are
called transitions. Example graph configuration can look like:
```
{
  "path": "/secure/.*",
  "address": "knotx.knot.authorization",
  "onTransition": {
    "view": {
      "address": "knotx.knot.service",
      "onTransition": {
        "next": {
          "address": "knotx.knot.handlebars"
        }
      }
    },
    "next": {
      "address": "knotx.knot.action"
      "onTransition": {
        "next": {
          "address": "knotx.knot.handlebars"
        }
      }
    }
  }
}
```
Knot.x uses Router mechanism to define many routes and adds transitions to make routes easily
configurable. Example request flow can be illustrated with diagram:

[[assets/knotx-routing-graph.png|alt=Knot Routing]]

The diagram depicts which modules take part in a request processing. A user request is first seen by Server,
then Knot.x fetches a template and split the template to fragments.

After that routing begins. Based on the request method and path the route is selected, and the request is passed to first Knot on the route.
Knot performs its business logic and returns a transition. The transition defines next Knot the request is passed to.
In some cases Knot can decide to break the route and redirect the user to a different page.

When all Knots on the route processed the request or one of Knots break the routing, Server returns a response to the user.

It is also possible to define a custom request flow, skipping Repository Connector, Fragment Splitter and Fragment Assembler.
This feature is described in the [[Gateway Mode|GatewayMode]] section.
