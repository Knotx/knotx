![Cognifide logo](http://cognifide.github.io/images/cognifide-logo.png)

# How to contribute to Knot.x
Thank you for taking the time to contribute!
We appreciate all commits and improvements, feel free to join Knot.x community and contribute.

## How to start
Please refer to [Getting started](https://github.com/Cognifide/knotx#getting-started) section in the README file.
After setting up Knot.x instance you may check if it works properly entering [http://localhost:8092/content/local/simple.html](http://localhost:8092/content/local/simple.html) (when running default configuration).

## Knot.x Contributor License Agreement
Project License: [Apache License Version 2.0](https://github.com/Cognifide/knotx/blob/master/LICENSE)
- You will only Submit Contributions where You have authored 100% of the content.
- You will only Submit Contributions to which You have the necessary rights. This means that if You are employed You have received the necessary permissions from Your employer to make the Contributions.
- Whatever content You Contribute will be provided under the Project License(s).

## Commit Messages
When writing a commit message, please follow the guidelines in [How to Write a Git Commit Message](http://chris.beams.io/posts/git-commit/).

## Pull Requests
Please add the following lines to your pull request description:

```

---

I hereby agree to the terms of the Knot.x Contributor License Agreement.
```

## Documentation
All Knot.x documentation is in the same repository as codebase in [documentation](https://github.com/Cognifide/knotx/tree/master/documentation) module.
This documentation after update is ported to [Knot.x wiki](https://github.com/Cognifide/knotx/wiki).
When updating documentation please update proper markdown pages in [documentation](https://github.com/Cognifide/knotx/tree/master/documentation) module following [instructions](https://github.com/Cognifide/knotx/blob/master/documentation/README.md) and include it with your pull request.
After your pull request is merged, wiki pages will be updated. **Please do not update wiki pages directly because your changes will be lost.**

## Changelog
When changing or fixing some important part of Knot.x, please remember to update [Changelog](https://github.com/Cognifide/knotx/blob/master/CHANGELOG.md).
Your entry should be enlisted in `Unreleased` section. It will be moved to appropriate release notes when released.
Please use convention `- [PR-ABC](https://github.com/Cognifide/knotx/pull/ABC) - short description of the PR.`.

## Upgrade Notes
When changing or fixing anything related with changes in Knot.x configuration (e.g. its structure, new property etc.) or
snippet structure (e.g. change convention of naming services) remember to update 
[Wiki Upgrade Notes](https://github.com/Cognifide/knotx/blob/master/documentation/src/main/wiki/UpgradeNotes.md).

## Tests naming convention
Tests written in Knot.x should be named with `methodName_whenStateUnderTest_expectBehavior` convention proposed as the first example in [7 Popular Unit Test Naming Conventions](https://dzone.com/articles/7-popular-unit-test-naming).

### Examples:
**Unit tests**
`canServeRequest_whenNoFormIdAndPostAttribute_expectRequestNotServed`

**Integration tests**
In integration tests method name is omitted, and test class name should suggest what part of system we test:
`whenRepositoryDidNotReturnTemplateBody_expectNoSnippetsProcessing`

## Coding Conventions
Below is short list of things that will help us keep Knot.x quality and accept pull requests:
- Follow Google Style Guide code formatting from Knot.x Github, particularly set your IDE `tab size`/`ident` to 2 spaces and `continuation ident` to 4 spaces.
  - [Google Style Guide for Eclipse](https://github.com/Cognifide/knotx/tree/master/eclipse-java-google-style.xml) 
  - [Google Style Guide for IntelliJ](https://github.com/Cognifide/knotx/tree/master/intellij-java-google-style.xml)
- write tests (integration and Unit Tests) following defined convention,
- write javadoc, especially for interfaces and abstract methods,
- update [documentation](https://github.com/Cognifide/knotx/tree/master/documentation) and include changes in the same pull request which modifies the code,
- when committing an improvement, try to show it in local demo example,
- when logging use proper levels: `INFO` and `WARNING` should log only very important messages. 
