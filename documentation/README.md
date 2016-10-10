# Knot.x

<p align="center">
  <img src="https://github.com/Cognifide/knotx/blob/master/icons/180x180.png?raw=true" alt="Knot.x Logo"/>
</p>

## Documentation Module
Contains Knot.x documentation.

### How to contribute to documentation
Below are basic rules how to write documentation.

* The `wiki` directory contains the documentation, that should be uploaded to the project's wiki repository after changes are merged to `master` branch.
* GitHub wiki does not handle hierarchy of pages and directories. This means, that each markdown file's name has to be unique, even if pages are placed in different directories. Pages are being referenced by file name without extension or path.
* All assets (images etc.) should be placed in `wiki/assets` directory and referenced as `assets/<fileName>`. Here relative path to directory is required.
* `_Sidebar.md` file defines the structure of navigation sidebar placed on the right side on GitHub wiki. It contains links to documentation pages. All pages should be linked in this file. 
* `DocumentationTemplate.md` file defines the structure of released version documentation file. All documentation files have to be included in this file in correct order, preferably in the same order as in `_Sidebar.md` file.
* Use only `#` style headings. TOC in generated released version documentation is based on headings and alternative heading styles are not supported.
* Use different heading levels. Hierarchy in TOC of released version documentation is based on heading levels.

### Generating released version documentation [only during release]
Below are instructions how to prepare documentation of a new released application version and how to upload it to GitHub wiki.

#### Prerequisites
* `node.js` installed.
* `doctoc` node module installed globally (`npm install -g doctoc`).

#### Steps
1. Make sure that `package.json` file contains correct version number.
2. Run maven command `mvn install -P process-docs`.
3. There should be compiled single page `Documentation-<version>.md` file in `wiki/releases/<version>` directory.
4. From `wiki/releases/<version>` directory run command: `doctoc Documentation-<version>.md`.
5. Check generated TOC in `Documentation-<version>.md`. You can modify its heading if you want.
6. Add link to generated release file to `wiki/_Sidebar.md` file if it is not present. Note that link target should be just page name without path and extension, ie. `Documentation-1.0.0.md` rather than `releases/Documentation-1.0.0.md`.
7. Commit changes to main Knot.x repository.
8. Copy contents of `wiki` directory to main directory of github wiki repository, commit and push changes.