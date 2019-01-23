# How to release Knot.x

## Prerequisites
In order to create OpenPGP signatures, you will need a key pair (instructions on creating a key pair using the GnuPG tools can be found in the GnuPG HOWTOs). 
You need to provide the Signing Plugin with your key information, which means three things:

The public key ID (The last 8 symbols of the keyId. You can use gpg -K to get it).
The absolute path to the secret key ring file containing your private key. 
(Since gpg 2.1, you need to export the keys with command gpg --keyring secring.gpg --export-secret-keys > ~/.gnupg/secring.gpg).

The passphrase used to protect your private key.

These items must be supplied as the values of the signing.keyId, signing.secretKeyRingFile, and signing.password properties, respectively.

Given the personal and private nature of these values, a good practice is to store them in the gradle.properties file in the user’s Gradle home directory.

```
signing.keyId=24875D73
signing.password=secret
signing.secretKeyRingFile=/Users/me/.gnupg/secring.gpg
```

### Credentials
Additionally you need to configure your **ossrh** credentials to deploy artifacts to Maven Central.
```
ossrhUsername=username
ossrhPassword=secret
```

## Deploy snapshots
Snapshot deployment are performed when your version ends in -SNAPSHOT. You can simply run:
```
$> ./gradlew publish
```

## Deploy releases

1. Create a release branch from the `master` branch

   ```
   $> git checkout -b release/X.Y.Z
   ```
   where X, Y and Z are the major, mid and minor version number of the release respectively.

2. Set the release version in the `gradle.properties` file

3. Prepare release documentation following [documentation/README.md](https://github.com/Cognifide/knotx/blob/master/documentation/README.md).

4. Commit changes to the release branch and push to the remote

   ```
   $> git add -A .
   $> git commit -m "Prepare release X.Y.Z"
   $> git push
   ```

5. Build & deploy the artifact to the Nexus Staging repository

   ```
   $> ./gradlew publish
   ```
   
6. On successful deployment, confirm that the artifacts with version X.Y.Z are available on the Nexus Staging:
[https://oss.sonatype.org/content/groups/staging/io/knotx/](https://oss.sonatype.org/content/groups/staging/io/knotx/)

7. If everything is fine, promote the release to **Nexus Central Release Repositories** from WEB UI.
   
8. Create a release on Github: [https://github.com/Cognifide/knotx/releases/new](https://github.com/Cognifide/knotx/releases/new)
  - Set the proper Tag version, e.g.: `X.Y.Z` on the `release/X.Y.Z` branch
  - Set the title of the release: `X.Y.Z`
  - Describe the release as follows:

   ```md
   # What's new
     
     - Describe first changes
     - Second changes
     - etx

     Binaries and dependency information for Maven, Ivy, Gradle and others can be found at 
  [http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.knotx%22%20AND%20v%3A<X.Y.Z>)
  
     Additionally, 
     - Example App:
       - [knotx-example-app-X.Y.Z.fat.jar](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-example-app/X.Y.Z/knotx-example-app-X.Y.Z.fat.jar)
       - [knotx-example-app-X.Y.Z.json](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-example-app/X.Y.Z/knotx-example-app-X.Y.Z.json)
       - [knotx-example-app-X.Y.Z.logback.xml](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-example-app/X.Y.Z/knotx-example-app-X.Y.Z.logback.xml)
     - Mocks:
       - [knotx-mocks-X.Y.Z.fat.jar](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-mocks/X.Y.Z/knotx-mocks-X.Y.Z.fat.jar)
       - [knotx-mocks-X.Y.Z.json](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-mocks/X.Y.Z/knotx-mocks-X.Y.Z.json)
       - [knotx-mocks-X.Y.Z.logback.xml](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-mocks/X.Y.Z/knotx-mocks-X.Y.Z.logback.xml)
     - Knot.x Standalone:
       - [knotx-standalone-X.Y.Z.fat.jar](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-standalone/X.Y.Z/knotx-standalone-X.Y.Z.fat.jar)
       - [knotx-standalone-X.Y.Z.json](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-standalone/X.Y.Z/knotx-standalone-X.Y.Z.json)
       - [knotx-standalone-X.Y.Z.logback.xml](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-standalone/X.Y.Z/knotx-standalone-X.Y.Z.logback.xml)
   ```
   
     **Replace `<X.Y.Z>` in the maven.org URL with the proper version**
     
9. Set the SNAPSHOT version in the `gradle.properties` file.
