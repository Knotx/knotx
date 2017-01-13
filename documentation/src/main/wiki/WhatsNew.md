# What's new

* Simplified JSON configuration. Knot.x Application should have minimal config with list of services with ability to override default settings in this config and through system properties.
* Changed snippet attribute `data-api-type` to `data-knotx-knots` which is now multi-value attribute with comma as separator, it keeps information which Knots should be used while Fragment processing
* Changed snippet attribute name `data-service` to `data-knotx-service`
* Changed snippet attribute name `data-params` to `data-knotx-params`
* Changed snippet attribute `type` value `text/x-handlebars-template` to `text/knotx-snippet`
* Introduced *FragmentAssembler* which joins all Fragments just before the response to the site visitor
* Introduce Vert.x Service Proxy mechanism for Adapter, Knot, RepositoryConnector implementation, to hide the event bus implementation, simplify development process and to enable service discovery functionality in the near future.
