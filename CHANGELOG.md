# Knot.x Releases
All notable changes to Knot.x will be documented in this file.

## Unreleased
List of changes that are finished but not yet released in any final version.

## Version 1.0.0 (*In progress*)
- Initial open source release.

## Version [1.0.0-RC6](https://github.com/Cognifide/knotx/releases/tag/1.0.0-RC6)
- [PR-243](https://github.com/Cognifide/knotx/pull/243) - fixed allowed headers configuration entry in HttpAdapterConfiguration
- [PR-244](https://github.com/Cognifide/knotx/pull/244) - fixed Form submission encoding for 'application/x-www-form-urlencoded'
- [PR-246](https://github.com/Cognifide/knotx/pull/246) - fixed client options configuration entry in HttpAdapterConfiguration (could influence on a platform performance)


## Version [1.0.0-RC5](https://github.com/Cognifide/knotx/releases/tag/1.0.0-RC5)
- Changed Knot.x modules naming convention to io.knotx.*.
- [PR-238](https://github.com/Cognifide/knotx/pull/238) - fixed Form submission.

## Version [1.0.0-RC4](https://github.com/Cognifide/knotx/releases/tag/1.0.0-RC4)
- Introduced _FragmentAssembler_ which joins all Fragments just before the response to the site visitor.
- Added delay configuration to HTTP Service & Remote Repository mocks.
- Introduce Vert.x Service Proxy mechanism for Adapter, Knot, RepositoryConnector implementation, to hide the event bus implementation, simplify development process and to enable service discovery functionality in the near future.

## Version [1.0.0-RC3](https://github.com/Cognifide/knotx/releases/tag/1.0.0-RC3)
- Simplified JSON configuration. Knot.x Application should have minimal config with list of services 
with ability to override default settings in this config and through system properties.

## Version [1.0.0-RC2](https://github.com/Cognifide/knotx/releases/tag/1.0.0-RC2)
- View Knot is divided into Service Knot and Handlebars Knot.
- KnotContext, AdapterRequest, AdapterResponse uses message codes which allows custom messages
  to be marshalled across the event bus. Performance improvement.
  
## Version [1.0.0-RC1](https://github.com/Cognifide/knotx/releases/tag/1.0.0-RC1)
- Fully [Event-Bus driven architecture](https://github.com/Cognifide/knotx/issues/63).
- Knot.x is now modular [Knot](https://github.com/Cognifide/knotx/wiki/Knot).
- Multiple fixes and examples.
