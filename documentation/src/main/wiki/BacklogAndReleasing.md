# Backlog and Releasing

## Release Semantic Versioning
Knot.x releases follow [Semantic Versioning 2.0.0](https://semver.org/) guide.
Each release has a version number `MAJOR.MINOR.PATCH`. Those numbers are incremented when:
- **MAJOR** version when Knot.x introduce incompatible API changes or major architecture refactoring,
- **MINOR** version when Knot.x introduces new complex functionality in a backwards-compatible manner, MINOR dependencies updates (e.g. Vert.x or RxJava), and
- **PATCH** version when Knot.x introduces backwards-compatible bug fixes, small improvements or PATCH dependencies updates (e.g. Vert.x or RxJava).

### Changes tracking
Knot.x provides two sources of tracking the changes:
- [`CHANGELOG.md`](https://github.com/Cognifide/knotx/blob/master/CHANGELOG.md) where all notable changes to Knot.x core are documented with links to the tickets with detailed description.
- [[Upgrade Notes|UpgradeNotes]] where all crucial changes that concerns users during the migration (e.g. additional parameters, bugfix and workarounds, API/configuration changes, dependency upgrades like Vert.x or RxJava).

## When to migrate
- **MAJOR** - new project start or planned migration - migration may require a significant effort.
- **MINOR** - if you need some improvements that are in this release, planned migration - migration 
may require a minor effort.
- **PATCH** - as often as possible, no compatibility break, only bugfixes or very small improvements 
that does not change any system logic - migration should not take any effort.


## Bugfixes support and releasing
Knot.x as an Open Source project supports **the last MINOR** release with bugfixes released regularly 
as **PATCH** releases, until the next **MINOR** or **MAJOR** release.

## Work in progress and milestones
Knot.x roadmap is build of milestones. All **MAJOR** or **MINOR** improvements are developed on feature
branches that are reviewed and merged to the current `milestone/goal-of-milestone` branch which is 
frequently updated with `master` branch (that contains bugfixes and small improvements that are 
subject of **PATCH** releases). 

When all milestone goals are finished, **MAJOR** or **MINOR** release is announced, milestone
branch is merged to `master` branch and new Knot.x version is released.

We treat `master` branch as a stable branch that is always ready to release.

## Clear milestones, progress and branching
You may always see the current milestone goal in [Knot.x milestones board](https://github.com/Cognifide/knotx/milestones).
You may read more about GitHub milestones [here](https://help.github.com/articles/about-milestones/).

## GitHub issues labels
Knot.x project have couple of custom labels for [issues board](https://github.com/Cognifide/knotx/issues) 
to make it easier manage the tickets. Some of them are:
- `configuration` - tickets that have impact on Knot.x configuration and deployment.
- `discussion` - this is an open discussion over a feature (that e.g. may break compatiblity) - everyone 
is welcome to participate with comments and ideas.
- `performance` - tickets that have impact on system performance, e.g. some improvement.
- `wiki` - stuff with documentation e.g. missing documentation or wiki structure update.
