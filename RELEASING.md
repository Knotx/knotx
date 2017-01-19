#How to release Knot.x

##Prerequisites
1. Sonatype.org [JIRA](https://issues.sonatype.org/secure/Signup!default.jspa) account

2. Your Sonatype.org account added to Knot.x project (if not, please contact Knot.x team: 
[knotx.team@gmail.com](email:knotx.team@gmail.com))

3. You have create GPG key for the email you have registered on Sonatype.org JIRA 
(Follow the [Working with PGP Signatures](http://central.sonatype.org/pages/working-with-pgp-signatures.html) 
guide how to do it). 
**Don't forget to deploy your public key to the key server** 

4. Add `<server>` entry to your `settings.xml` file
```xml
<servers>
  ...
  <server>
    <id>ossrh</id>
    <username>your_sonatype_org_jira_username</username>
    <password>your_sonatype_org_jira_password</password>
  </server>
    ...
</servers>    
```

##Steps
1. Create release branch from *master* branch
```
$> git checkout -b release/X.Y.Z
```
2. Set release version
```
$> mvn versions:set -DnewVersion=X.Y.Z -DgenerateBackupPoms=false
```
3. Prepare release documentation following [documentation/README.md](https://github.com/Cognifide/knotx/blob/master/documentation/README.md).
4. Commit changes to the release branch and push to remote
```
$> git add -A .
$> git commit -m "Prepare release X.Y.Z"
$> git push
```
5. Build & deploy to Nexus staging repository
```
$> mvn clean deploy -Prelease -Dgpg.passphrase=<your_gpg_key_passphrase>
```
6. On successful deploy, validate artifacts with version X.Y.Z are available on Nexus Staging:
[https://oss.sonatype.org/content/groups/staging/io/knotx/](https://oss.sonatype.org/content/groups/staging/io/knotx/)
7. If everything is fine, promote release to **Nexus Central Release Repositories**
```
$> mvn nexus-staging:release
```
In case you want to drop the release, you can use `nexus-staging:drop`
8. If artifacts on staging repository are correctly verified you can promote release to **Nexus Central Release Repositories**
```
$> mvn nexus-staging:drop
```
9. Create release on Github: [https://github.com/Cognifide/knotx/releases/new](https://github.com/Cognifide/knotx/releases/new)
  - Set proper Tag version, e.g.: `X.Y.Z` at `release/X.Y.Z` branch
  - Set title of the release: `X.Y.Z`
  - Describe release as follows:
  ```md
  # What's new
    - Describe first changes
    - Second changes
    - etx

    Binaries and dependency information for Maven, Ivy, Gradle and others can be found at 
    [http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.vertx%22%20AND%20v%3A<X.Y.Z>)
    
    Additionally, 
    - Example App:
      - [knotx-example-app-X.Y.Z.fat.jar](https://oss.sonatype.org/content/groups/public/io/knotx/example-app/X.Y.Z/example-app-X.Y.Z.fat.jar)
      - [knotx-example-app-X.Y.Z.json](https://oss.sonatype.org/content/groups/public/io/knotx/example-app/X.Y.Z/example-app-X.Y.Z.json)
      - [knotx-example-app-X.Y.Z.logback.xml](https://oss.sonatype.org/content/groups/public/io/knotx/example-app/X.Y.Z/example-app-X.Y.Z.logback.xml)
    - Mocks:
      - [knotx-mocks-X.Y.Z.fat.jar](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-mocks/X.Y.Z/knotx-mocks-X.Y.Z.fat.jar)
      - [knotx-mocks-X.Y.Z.json](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-mocks/X.Y.Z/knotx-mocks-X.Y.Z.json)
      - [knotx-mocks-X.Y.Z.logback.xml](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-mocks/X.Y.Z/knotx-mocks-X.Y.Z.logback.xml)
    - Knot.x Standalone:
      - [knotx-standalone-X.Y.Z.fat.jar](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-standalone/X.Y.Z/knotx-standalone-X.Y.Z.fat.jar)
      - [knotx-standalone-X.Y.Z.json](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-standalone/X.Y.Z/knotx-standalone-X.Y.Z.json)
      - [knotx-standalone-X.Y.Z.logback.xml](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-standalone/X.Y.Z/knotx-standalone-X.Y.Z.logback.xml)
  ```
  Replace `<X.Y.Z>` in the maven.org URL with proper version


