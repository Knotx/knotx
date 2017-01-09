# What's new

* Simplified JSON configuration. Knot.x Application should have minimal config with list of services with ability to override default settings in this config and through system properties.
* Changed `data-api-type` to `data-knot-types` which is now multi-value attribute with comma as separator, it keeps information which Knots should be used while particular Fragment processing
* Introduced *FragmentAssembler* which joins all Fragments just before the response to the site visitor
