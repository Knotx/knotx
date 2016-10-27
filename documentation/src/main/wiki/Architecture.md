[TO REVIEW]

Knot.x can be treated as tool which fetches requested templates from Repository and processes those 
templates. So requests from site visitor are first seen by Knot.x. Then Knot.x calls Repository for 
the template, split this template to static / dynamic fragments and process those fragments. Finally 
it calls external services if required.

The diagram below depicts Knot.x request flow at very high level point of view.

[[assets/knotx-overview.png|alt=Knot.x Overview]]

Knot.x can be easily extensible by custom integration implementations. So Knot.x can easily adapt 
responses with different formats to required one. Additionally Knot.x does not concentrate on HTTP
protocol so even custom protocols can be used if required.