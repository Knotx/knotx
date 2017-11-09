# Upgrade Notes
If you are upgrading Knot.x from the previous version, here are notes that will help you do all necessary config
and snippets changes that were introduced in comparison to previous released Knot.x version. If you are upgrading
from older than one version (e.g. 1.0.1 -> 1.1.2) be sure, to do all the steps from the Upgrade Notes of all released
versions. You may see all changes in the [Changelog](https://github.com/Cognifide/knotx/blob/master/CHANGELOG.md).

## Master
List of changes that are finished but not yet released in any final version.

## Version 1.1.2
- [PR-335](https://github.com/Cognifide/knotx/pull/335) - Added support for HttpServerOptions on the configuration level.
  * **Important**: The biggest change here is the way port of [[Knot.x Server|Server#vertx-http-server-configurations]] is configured. 
  Previously it was defined in the `config.httpPort` property. Now `serverOptions` section was introduced, see  
  [Vert.x DataObjects page](http://vertx.io/docs/vertx-core/dataobjects.html#HttpServerOptions) for more details. 
  See example change of configuration
  [here](https://github.com/Cognifide/knotx/pull/335/files#diff-9eb56f60d7dcc72e56694b1a0aeb014dL5).

## Version 1.1.1
 - [PR-307](https://github.com/Cognifide/knotx/pull/307) - Fixed KnotxServer default configuration
  * Now default configuration of [[Knot.x Server|Server]] will accept all GET requests.

## Version 1.1.0
- [PR-296](https://github.com/Cognifide/knotx/pull/296) - Support for params on markup and config
  * You may now pass additional `queryParams` via `params` configuration in Service Adapter section.
- [PR-299](https://github.com/Cognifide/knotx/pull/299) - Customize request routing outside knots
  * **Important**: structure of [[Knot.x Server|Server]] configuration has changed because [[Gateway Mode|GatewayMode]] was introduced.
    Now, to configure custom `repositories`, `splitter`, `assembler` and `routing` move all those sections to `defaultFlow`.
    See the example change [here](https://github.com/Cognifide/knotx/pull/299/files#diff-d4c26ef67612264e462c7e4a882023cdL38).
    Additionally, new configuration section (which is not mandatory) `customFlow` was introduced. Read more about it 
    in [[Gateway Mode|GatewayMode]] docs.
- [PR-306](https://github.com/Cognifide/knotx/pull/306) - Additional parameters to adapter from template. 
  * You may pass additional parameters to Action Adapters using `data-knotx-adapter-params`. Read more in [[Action Knot|ActionKnot#example]] docs.

## Version 1.0.1
- [PR-290](https://github.com/Cognifide/knotx/pull/290) - allow defining services without default `params` configured
  * You no longer have to define empty `params` value in `ServiceKnot` config if you don't use any.
