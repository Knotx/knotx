# Filesystem Repository Connector section

Filesystem Repository Connector allows to fetch templates from local file storage. 

## How does it work?
The diagram below depicts Knot.x modules and request flow in more details.

[[assets/knotx-filesystem-repository.png|alt=Http Repository Connector]]

## How to configure?

See the [FilesystemRepositoryOptions](https://github.com/Cognifide/knotx/blob/master/documentation/src/main/cheatsheet/cheatsheets.adoc#filesystemrepositoryoptions) for all configuration options and its defaults.
In general, it:
- Listens of event bus address `knotx.core.repository.filesystem` address on requests to the repository
- It uses empty catalogue what means the classpath is the root folder of repository data.

