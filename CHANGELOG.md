# Knot.x Releases
All notable changes to Knot.x will be documented in this file.

# 1.6.0
This version is dependency updates only, no new features or bugfixes. If you are upgrading from older than 1.5.0 version, follow the [Upgrade Notes for 1.5](https://knotx.io/blog/release-1_5_0/) first.
- Upgraded Vert.x to 3.7.1.

## 1.5.0
List of changes that are finished but not yet released in any final version.
 - [PR-468](https://github.com/Cognifide/knotx/pull/468) - Fragment processing failure handling (configurable fallback)
 - [PR-465](https://github.com/Cognifide/knotx/pull/465) - Action Knot functionality moved to [Knot.x Forms](https://github.com/Knotx/knotx-forms).
 - [PR-467](https://github.com/Cognifide/knotx/pull/467) - fixed typo in logger format (URI already contains leading slash)
 - [PR-473](https://github.com/Cognifide/knotx/pull/473) - mark Handlebars Knot as deprecated on behalf of [Knot.x Template Engine](https://github.com/Knotx/knotx-template-engine).

### Breaking changes
#### Migration form Action Knot to Knot.x Forms
Knot.x (<= 1.4.0) used earlier Action Knot. Please follow step below in order to migrate your project from `ActionKnot` to `Knotx Forms` module.


##### Configuration file:
1. In your main config `application.conf`, update `modules` section from:
  ```
    "actionKnot=io.knotx.knot.action.ActionKnotVerticle"
    "actionAdapter=com.acme.adapter.action.http.HttpActionAdapterVerticle"
  ```
  to
  ```
    "forms=io.knotx.forms.core.FormsKnot"
    "formsAdapter=com.acme.forms.adapter.http.FormsAdapterVerticle"
  ```

  and define forms in the `global` section
  ```
  global {
    address {
      forms {
        knot = knotx.knot.forms
        example.adapter = knotx.forms.example.adapter.http
      }
    }
  }
  ```

  Change included configuration name `actionKnot.conf` to `forms.conf`

##### Page templates:

  - Rename data attributes accordingly

  | Old                        | New                              |
  | -------------------------- |:--------------------------------:|
  | data-knotx-on-             |data-knotx-forms-on-              |
  | data-knotx-action          |data-knotx-forms-adapter-name     |
  | data-knotx-adapter-params  |data-knotx-forms-adapter-params   |

  - In form snippet change `action` to `form`.

  For example:

  Old
  ```html
  <script data-knotx-knots="form-1" type="text/knotx-snippet">
    {{#if action._result.validationErrors}}
    <p class="bg-danger">Email address does not exists</p>
    {{/if}}
    <p>Please provide your email address</p>
    <form data-knotx-action="step1"
    data-knotx-on-success="/content/local/login/step2.html"
     data-knotx-on-error="_self"
      data-knotx-adapter-params='{"myKey":"myValue"}' method="post">
      <input type="email" name="email" value="{{#if action._result.validationError}} {{action._result.form.email}} {{/if}}" />
      <input type="submit" value="Submit"/>
    </form>
  </script>
  ```

  New
  ```html
  <script data-knotx-knots="form-1" type="text/knotx-snippet">
    {{#if form._result.validationErrors}}
    <p class="bg-danger">Email address does not exists</p>
    {{/if}}
    <p>Please provide your email address</p>
    <form data-knotx-forms-adapter-name ="step1"
      data-knotx-forms-on-success="/content/local/login/step2.html"
      data-knotx-forms-on-error="_self"
      data-knotx-forms-adapter-params='{"myKey":"myValue"}' method="post">
      <input type="email" name="email" value="{{#if form._result.validationError}} {{form._result.form.email}} {{/if}}" />
      <input type="submit" value="Submit"/>
    </form>
  </script>
```

##### Custom adapter
Refactor your custom adapter to inherit [`io.knotx.proxy.AdapterProxy`](https://github.com/Cognifide/knotx/blob/1.3.0/knotx-core/src/main/java/io/knotx/proxy/AdapterProxy.java)
to [`io.knotx.forms.api.FormsAdapterProxy`](https://github.com/Knotx/knotx-forms/blob/master/api/src/main/java/io/knotx/forms/api/FormsAdapterProxy.java)

#### Migration form Handlebars Knot to Knot.x Template Engine
Knot.x (<= 1.5.0) used `HandlebarsKnot`. Please follow step below in order
to migrate your project from `HandlebarsKnot` to `Template Engine` module.

Handlebars is still the default Template Engine strategy in Knot.x. Thanks to moving into Template Engine module you may now easily
create and configure your own Template Engine strategy and choose some snippets to be rendered by it.
See the [example project](https://github.com/Knotx/knotx-example-project) for more details.

> Notice! You may still use old Handlebars Knot with Knot.x 1.5 if you want.
> However, remember that it is marked as @Deprecated and will be removed in the next major version.

##### Configuration file:
1. In your main config `application.conf`, update `modules` section from:
  ```
    "hbsKnot=io.knotx.knot.templating.HandlebarsKnotVerticle"
  ```
  to
  ```
    "templateEngine=io.knotx.te.core.TemplateEngineKnot"
  ```

2. Define module address in the `global` section
  ```
  global {
    ...
    templateEngine.address = knotx.knot.te
  }
  ```
3. Replace all occurenes of the Handlebars Knot address in the Server routing `defaultFlow` from `${global.hbs.address}` to `${global.templateEngine.address}`.

3. Instead including `hbsKnot.conf` change include to `templateEngine.conf`. You will find examples configuration files in
the [knotx-stack distribution](https://github.com/Knotx/knotx-stack/blob/master/knotx-stack-manager/src/main/packaging/conf/includes).

##### Page templates:

1. Update `data-knotx-knots` values from `handlebars` to `te`.

2. Example helpers: `string_equals` and `encode_uri` that were embedded into Handlebars Knot are no longer available in the Template Engine.
You may introduce them by defining handlebars extension as it is presented in the [example project](https://github.com/Knotx/knotx-example-project/tree/master/acme-handlebars-ext).


## 1.4.0
 - [PR-427](https://github.com/Cognifide/knotx/pull/427) - HttpRepositoryConnectorProxyImpl logging improvements
 - [PR-422](https://github.com/Cognifide/knotx/pull/422) - Configurable Handlebars delimiters
 - [PR-428](https://github.com/Cognifide/knotx/pull/428) - Mark all Service Knot related classes deprecated.
 - [PR-432](https://github.com/Cognifide/knotx/pull/432) - Port unit and integration tests to JUnit 5
 - [PR-440](https://github.com/Cognifide/knotx/pull/440) - Enable different Vert.x Config stores types fix.
 - [PR-443](https://github.com/Cognifide/knotx/pull/443) - Update maven plugins versions.
 - [PR-445](https://github.com/Cognifide/knotx/pull/445) - Vert.x version upgrade to 3.5.3
 - [PR-458](https://github.com/Cognifide/knotx/pull/458) - Remove unused StringToPattern function

## 1.3.0
 - [PR-376](https://github.com/Cognifide/knotx/pull/376) - Knot.x configurations refactor - Changed the way how configurations and it's defaults are build.
 - [PR-384](https://github.com/Cognifide/knotx/pull/384) - Introduce Knot.x server backpressure mechanism
 - [PR-397](https://github.com/Cognifide/knotx/pull/397) - Introduce vertx-config module to enable configuration modularization and auto-reload. Thanks to this change, Knot.x instance Auto-redeploy itself
 after the configuration is changed, multiple configuration files format is supported (with favouring the [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md) and supporting nested configurations (includes).
 - [PR-399](https://github.com/Cognifide/knotx/pull/399) - Make knotx-core more concise and merge base Knot.x concepts into a single module
 - [PR-404](https://github.com/Cognifide/knotx/pull/404) - Refactoring of Knot.x Launcher, get rid of some messy hacks. That enables cleaner way to start Knot.x and extend launcher with custom commands.
 - [PR-405](https://github.com/Cognifide/knotx/pull/405) - Switched to BOM style dependencies: [`knotx-dependencies`](https://github.com/Knotx/knotx-dependencies) that define all common dependencies and their versions.
 - [PR-406](https://github.com/Cognifide/knotx/pull/406) - `standalone` module is not conceptually a part of Knot.x `core`. It was extracted to separate concept and will be available from now in [`knotx-stack`](https://github.com/Knotx/knotx-stack) repository.
 - [PR-407](https://github.com/Cognifide/knotx/pull/407) - Added vertx hooks to properly terminate instance on fatal failure, like missing configurations etc.
 - [PR-411](https://github.com/Cognifide/knotx/pull/411) - `example` module is not conceptually a part of Knot.x `core` and having it in the core repository was misleading. `integration-tests` module introduced here.
 - [PR-412](https://github.com/Cognifide/knotx/pull/412) - Knot.x `core` modules will use dependencies in `provided` scope, all dependencies will be provided by [`knotx-stack`](https://github.com/Knotx/knotx-stack) setup.
 - [PR-415](https://github.com/Cognifide/knotx/pull/415) - bugfix: headers configurations (e.g. `allowedHeaders`) are now case insensitive
 - [PR-418](https://github.com/Cognifide/knotx/pull/418) - Update to Vert.x 3.5.1
 - [PR-419](https://github.com/Cognifide/knotx/pull/419) - Knotx snippets parameters prefix is now customizable.
 - [PR-421](https://github.com/Cognifide/knotx/pull/421) - Support for system properties injection in HOCON config file

## Version 1.2.2
 - [PR-395](https://github.com/Cognifide/knotx/pull/395) - Fix for [#394](https://github.com/Cognifide/knotx/issues/394) - implemented encoding request parameter names in `HttpRepositoryConnectorProxyImpl`
 - [PR-420](https://github.com/Cognifide/knotx/pull/420) - Upgrade vert.x to 3.5.1

## Version 1.2.1
 - [PR-385](https://github.com/Cognifide/knotx/pull/385) - Fix for [#107](https://github.com/Cognifide/knotx/pull/107) - Support for snippet tags other than `script`

## Version 1.2.0
 - [PR-345](https://github.com/Cognifide/knotx/pull/345) - Update to Vert.x 3.5 and RxJava 2
 - [PR-320](https://github.com/Cognifide/knotx/pull/320) - Added KnotxServer configuration parameter to configure fileUploads folder
 - [PR-347](https://github.com/Cognifide/knotx/pull/347) - Improved error logging on Server routing, repository connector & Http service adapters. Added custom headers configuration for requests/responses, etc.
 - [PR-349](https://github.com/Cognifide/knotx/pull/349) - Enable to pass [`Delivery Options`](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/DeliveryOptions.html) to Knot.x Verticles to manipulate eventbus response timeouts.
 - [PR-350](https://github.com/Cognifide/knotx/pull/350) - Cosmetic cleanup of default logback.xml config files. Fixed URLs to the bootstrap CSS on example app.
 - [PR-352](https://github.com/Cognifide/knotx/pull/352) - Fix for #351 - Prevent NPE if service returns empty body
 - [PR-353](https://github.com/Cognifide/knotx/pull/353) - Fix for #351 - Deprecated [`ProxyHelper`](http://vertx.io/docs/apidocs/io/vertx/serviceproxy/ProxyHelper.html) replaced with [`ServiceBinder`](http://vertx.io/docs/apidocs/io/vertx/serviceproxy/ServiceBinder.html)
 - [PR-354](https://github.com/Cognifide/knotx/pull/354) - Improvement - added CSRF protection and ability to configure it on any route.
 - [PR-357](https://github.com/Cognifide/knotx/pull/357) - Fix for #356 - Create AdapterProxies on serviceKnot start instead for each request
 - [PR-364](https://github.com/Cognifide/knotx/pull/364) - Fixed #358 - Use compiled regexp instead of String while matching the fragment
 - [PR-361](https://github.com/Cognifide/knotx/pull/361) - Improved Adapter / Knots creation - we do not create them with every request.
 - [PR-359](https://github.com/Cognifide/knotx/pull/359) - Fix for #355 - Knot.x responses with the response of a repository in case of response with status code different than 200.
 - [PR-369](https://github.com/Cognifide/knotx/pull/369) - Better support for SSL for Repository Connector
 - [PR-371](https://github.com/Cognifide/knotx/pull/371) - Fixed debug logging of HTTP Repository Connector
 - [PR-372](https://github.com/Cognifide/knotx/pull/372) - Added cache for compiled Handlebars snippets
 - [PR-374](https://github.com/Cognifide/knotx/pull/374) - Enable keepAlive connection in http client options
 - [PR-380](https://github.com/Cognifide/knotx/pull/380) - Upgrade jsoup to 1.11.2
 - [PR-379](https://github.com/Cognifide/knotx/pull/379) - Added access logging capabilities to the Knotx HTTP Server. Establish standard configuration of logback logger.
 - [PR-383](https://github.com/Cognifide/knotx/pull/383) - Fix for [#382](https://github.com/Cognifide/knotx/pull/382) - Unhandled exception if query parameter consists of reserved characters.

## Version 1.1.2
 - [PR-318](https://github.com/Cognifide/knotx/pull/318) - Knot.x returns exit code `30` in case of missing config
 - [PR-332](https://github.com/Cognifide/knotx/pull/332) - Fixed timeout issues when deploying verticles in Junit Rule
 - [PR-335](https://github.com/Cognifide/knotx/pull/335) - Added support for HttpServerOptions on the configuration level.
 - [PR-328](https://github.com/Cognifide/knotx/pull/328) - Knot.x ignore config parts related to not existing modules and allows to start the instance with warnings

## Version 1.1.1
 - [PR-316](https://github.com/Cognifide/knotx/pull/316) - Gateway Processor has access to request body
 - [PR-307](https://github.com/Cognifide/knotx/pull/307) - Fixed KnotxServer default configuration

## Version 1.1.0
 - [PR-293](https://github.com/Cognifide/knotx/pull/293) - Use vert.x WebClient and RxJava Single
 - [PR-294](https://github.com/Cognifide/knotx/pull/294) - Replace deprecated rx methods
 - [PR-295](https://github.com/Cognifide/knotx/pull/295) - Javadocs for core classes
 - [PR-296](https://github.com/Cognifide/knotx/pull/296) - Support for params on markup and config
 - [PR-299](https://github.com/Cognifide/knotx/pull/299) - Customize request routing outside knots
 - [PR-300](https://github.com/Cognifide/knotx/pull/300) - Change the default configuration for tests execution
 - [PR-306](https://github.com/Cognifide/knotx/pull/306) - Additional parameters to adapter from template

## Version 1.0.1
- [PR-288](https://github.com/Cognifide/knotx/pull/288) - action knot refactor
- [PR-290](https://github.com/Cognifide/knotx/pull/290) - allow defining services without default `params` configured
- [PR-289](https://github.com/Cognifide/knotx/pull/289) - upgraded versions: Vert.x to 3.4.1 and RxJava to 1.2.7
- [PR-285](https://github.com/Cognifide/knotx/pull/285) - fixed handling of Multiple headers with the same name
- [PR-278](https://github.com/Cognifide/knotx/pull/278) - fixed closing files in Filesystem Repository

## Version 1.0.0
- Initial open source release.
