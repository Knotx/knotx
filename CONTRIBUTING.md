# How to contribute to Knot.x
Thank you for taking the time to contribute!
We appreciate all commits and improvements, feel free to join Knot.x community and contribute.

## How to start
- See [knotx.io](https://knotx.io/tutorials) for tutorials and examples.
- You will findand user documentation in every repository at https://github.com/Knotx in a form of markdown documents.
- See [Knot.x Example Project](https://github.com/Knotx/knotx-example-project) for usage examples.

## Knot.x Contributor License Agreement
Project License: [Apache License Version 2.0](https://github.com/Knotx/knotx/blob/master/LICENSE)
- You will only Submit Contributions where You have authored 100% of the content.
- You will only Submit Contributions to which You have the necessary rights. This means that if You are employed You have received the necessary permissions from Your employer to make the Contributions.
- Whatever content You Contribute will be provided under the Project License(s).

## Commit Messages
When writing a commit message, please follow the guidelines in [How to Write a Git Commit Message](http://chris.beams.io/posts/git-commit/).

### References in commit messages
When the commit refers to an issue, add this information to the commit message header, e.g. `#57`.
GitHub web platform automatically converts issue ids (`57`) to URLs referring to the related issue.

To link to another Knot.x repository, please use following convention:

`Knotx/KNOTX-RESPOSITORY-NAME#ISSUE-ID`

E.g. `Knotx/knotx-dependencies#28` will link to the https://github.com/Knotx/knotx-dependencies/issues/28.
In markdown you may paste the full url `https://github.com/Knotx/knotx-dependencies/issues/28`.
For more information, please refer to [this article](https://help.github.com/en/github/writing-on-github/autolinked-references-and-urls#issues-and-pull-requests).

## Pull Requests
Please add the following lines to your pull request description:

```

---

I hereby agree to the terms of the Knot.x Contributor License Agreement.
```

After the code review process, we kindly ask you to squash all commits so that every pull request contains a single commit before it is merged. 

Before mreging, please remember to rebase with `master` and squash all commits, e.g. with:
```
git rebase master
git rebase -i HEAD~3
git push -f
```
The first command moves the entire feature branch to begin on the tip of the `master` branch, effectively incorporating all of the new commits in `master`.
The second command executes `git rebase` command in interactive mode and updates last 3 commits (please squash them to single commit). See more [here](https://git-scm.com/book/en/v2/Git-Branching-Rebasing).

## Documentation
All Knot.x documentation is in the same repository as codebase in `README.md` markdown files.
When submitting a Pull Request with code update please remember to update docs and include it with your pull request.

## Changelog
When changing or fixing some important part of Knot.x, please remember to update `CHANGELOG.md` in the repository.
Your entry should be enlisted in `Unreleased` section. It will be moved to appropriate release notes when released.
Please use convention `- [PR-ABC](https://github.com/Knotx/REPOSITORY/pull/ABC) - short description of the PR.`.

## Upgrade Notes
When changing or fixing anything related with changes in Knot.x configuration (e.g. its structure, new property etc.) or
snippet structure (e.g. change convention of naming services) remember to include short description in your PR under `Upgrade Notes`.

## Tests naming convention
Tests written in Knot.x should use Junit 5 `@DisplayName` annotation with proper description.
Please also name test methods in some reasonable way.

### Example:
```
@Test
@DisplayName("Expect request not served when there is no FormId and Post attributes")
expectRequestNotServedwhenNoFormIdAndPostAttribute
```

## Coding Conventions
Below is short list of things that will help us keep Knot.x quality and accept pull requests:
- Follow Google Style Guide code formatting from Knot.x Github, particularly set your IDE `tab size`/`ident` to 2 spaces and `continuation ident` to 4 spaces.
  - [Google Style Guide for Eclipse](https://github.com/Knotx/knotx/tree/master/eclipse-java-google-style.xml)
  - [Google Style Guide for IntelliJ](https://github.com/Knotx/knotx/tree/master/intellij-java-google-style.xml)
- write tests (integration and Unit Tests) following defined convention,
- write javadoc, especially for interfaces and abstract methods,
- update documentation and include changes in the same pull request which modifies the code,
- when logging use proper levels: `INFO` and `WARNING` should log only very important messages.
