[![][license img]][license]
[![][central-repo img]][central-repo]
[![][gitter img]][gitter]

> If you are directed here from https://github.com/Cognifide/knotx or you are looking for 
Knot.x 1.X version please see this repository Tags, the latest 1.5.0 version code is available 
[here](https://github.com/Knotx/knotx/tree/1.5.0) along with the 
[documentation](https://github.com/Knotx/knotx/tree/1.5.0/documentation/src/main/wiki).

<p align="center">
  <img src="http://knotx.io/img/logo-knotx.png" alt="Knot.x Logo"/>
</p>
<p align="center">http://knotx.io</p>
<p align="center">
  web integration framework
</p>

Knot.x is an open source framework integrating content from different sources like headless or 
traditional CMS, with systems like CRM, e-commerce or search engines.

The heart of Knot.x is the [HTTP Server](https://github.com/Knotx/knotx-server-http) that uses 
[Fragment Processing](https://github.com/Knotx/knotx-fragments-handler) to connect many data sources
into one customer experience (like HTML, JSON or PDF) in the [configurable, reactive, scalable and fault-tolerant way](http://knotx.io/blog/configurable-integrations/).

Knot.x comes also with a [distribution](https://github.com/Knotx/knotx-stack) that enables quick start
with the framework, is a project structure and supports deployment automation (see the 
[Cookbook](https://github.com/Knotx/knotx-cookbook) and [Docker images](https://hub.docker.com/u/knotx)).
With the [Starter Kit](https://github.com/Knotx/knotx-starter-kit) template project, you can setup your project in a few minutes.

We build Knot.x on top of [Vert.x](http://vertx.io/), known as one of the leading toolkits for performant,
event-driven applications. It uses asynchronous programming principles which allows it to process a
large number of requests using a single thread. Asynchronous programming is a style promoting the
ability to write non-blocking code (no thread pools). The platform stays responsive under heavy and
varying load and is designed to follow [Reactive Manifesto](http://www.reactivemanifesto.org/) principles.

## How to start

See [knotx.io](http://knotx.io/tutorials) for tutorials and examples.
See [Knot.x Example Project](https://github.com/Knotx/knotx-example-project) for usage case examples.
See [Knot.x Starter Kit](https://github.com/Knotx/knotx-starter-kit) template project.
See https://github.com/Knotx extensions for user and developer documentation.

For more information on Knot.x and where Knot.x fits into the big picture please see http://knotx.io.

## Community / Issues

All feature requests and bugs can be filed as issues on [GitHub](https://github.com/Knotx/knotx/issues).
Do not use Github issues to ask questions, post them on the
[User Group](https://groups.google.com/forum/#!forum/knotx) or [Gitter Chat](https://gitter.im/Knotx/Lobby).

### GitHub issues labels
Knot.x project have couple of custom labels for [issues board](https://github.com/Knotx/knotx/issues) 
(also issues for each repository) to make it easier manage the tickets. Some of them are:
- `configuration` - tickets that have impact on Knot.x configuration and deployment.
- `discussion` - this is an open discussion over a feature (that e.g. may break compatiblity) - everyone 
is welcome to participate with comments and ideas.
- `performance` - tickets that have impact on system performance, e.g. some improvement.
- `wiki` - stuff with documentation e.g. missing documentation or wiki structure update.

## Backlog and Releasing

### Release Semantic Versioning
Knot.x releases follow [Semantic Versioning 2.0.0](https://semver.org/) guide.
Each release has a version number `MAJOR.MINOR.PATCH`. Those numbers are incremented when:
- **MAJOR** version when Knot.x introduce incompatible API changes or major architecture refactoring,
- **MINOR** version when Knot.x introduces new complex functionality in a backwards-compatible manner, MINOR dependencies updates (e.g. Vert.x or RxJava), and
- **PATCH** version when Knot.x introduces backwards-compatible bug fixes, small improvements or PATCH dependencies updates (e.g. Vert.x or RxJava).

#### Changes tracking
Knot.x provides two sources of tracking the changes:
- `CHANGELOG.md` for each repository, where all notable changes for the module are documented with links to the tickets and detailed description.
- `Upgrade Notes` section in the [release blog](https://knotx.io/blog/), where all crucial changes that concerns users during the migration (e.g. additional parameters, bugfix and workarounds, API/configuration changes, dependency upgrades like Vert.x or RxJava) are pointed out.

### When to migrate
- **MAJOR** - new project start or planned migration - migration may require a significant effort.
- **MINOR** - if you need some improvements that are in this release, planned migration - migration 
may require a minor effort.
- **PATCH** - as often as possible, no compatibility break, only bugfixes or very small improvements 
that does not change any system logic - migration should not take any effort.

### Bugfixes support and releasing
Knot.x as an Open Source project supports **the last MINOR** release with bugfixes released regularly 
as **PATCH** releases, until the next **MINOR** or **MAJOR** release.

### Work in progress and milestones
Knot.x roadmap is build of milestones. All **MAJOR** or **MINOR** improvements are developed on feature
branches that are reviewed and merged to the current `milestone/goal-of-milestone` branch which is 
frequently updated with `master` branch (that contains bugfixes and small improvements that are 
subject of **PATCH** releases). 

When all milestone goals are finished, **MAJOR** or **MINOR** release is announced, milestone
branch is merged to `master` branch and new Knot.x version is released.

We treat `master` branch as a stable branch that is always ready to release.

### Clear milestones, progress and branching
You may always see the current milestone goal in [Knot.x milestones board](https://github.com/Knotx/knotx/milestones).
You may read more about GitHub milestones [here](https://help.github.com/articles/about-milestones/).

## License

**Knot.x** is licensed under the [Apache License, Version 2.0 (the "License")](https://www.apache.org/licenses/LICENSE-2.0.txt)

[license]:https://github.com/Knotx/knotx/blob/master/LICENSE
[license img]:https://img.shields.io/badge/License-Apache%202.0-blue.svg

[central-repo]:http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.knotx%22
[central-repo img]:https://img.shields.io/maven-central/v/io.knotx/knotx-root.svg?label=Maven%20Central

[gitter]:https://gitter.im/Knotx/Lobby
[gitter img]:https://badges.gitter.im/Knotx/knotx-extensions.svg
