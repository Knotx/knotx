# What's new

* Simplified JSON configuration. Knot.x Application should have minimal config with list of services with ability to override default settings in this config and through system properties.
* Changed attribute name `data-api-type` to `data-knotx-knots` which is now multi-value attribute with comma as separator, it keeps information which Knots should be used while particular Fragment processing
* Changed attribute name `data-service` to `data-knotx-service`
* Changed attribute name `data-params` to `data-knotx-params`
* Introduced *FragmentAssembler* which joins all Fragments just before the response to the site visitor
