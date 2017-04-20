# Getting started

## First steps

See our first blog post [Hello Rest Service](http://knotx.io/blog/hello-rest-service/) which is a great entry point to the Knot.x world.
See other [Knot.x tutorial blogs](http://knotx.io/blog/) to learn more.

## Getting Binaries
Knot.x binaries and dependency information for Maven, Ivy, Gradle and others can be found at 
[http://search.maven.org](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.vertx%22).

Besides jar files with the modules implementations, there are available additional resources 
such as far.jar (bundled with all dependencies allowing to run itself) and configuration json file, 
for three modules:
- knotx-mocks
- knotx-standalone
- knotx-sample-app

To run Knot.x you need Java 8.

## Hello world!

First download Knot.x sample app & config for latest version, or build it yourself (see [[Building|GettingStarted#building]] section):
- [knotx-example-app-X.Y.Z.fat.jar](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-example-app)
- [knotx-example-app-X.Y.Z.json](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-example-app)

Now you can run Knot.x:
```
java -jar knotx-example-app-X.Y.Z-fat.jar -conf knotx-example-app-X.Y.Z.json
```

That's all. Finally you can open a browser and type an url `http://localhost:8092/content/local/simple.html`. 
You should see a page which is served from a local repository and contains example data from mock services.

The page should look like:

[[assets/knotx-example-simple.png|alt=Example simple page]]

See also [[how to run Knot.x demo|RunningTheDemo]] for more details.

## Building

To checkout the source and build:

```
$ git clone https://github.com/Cognifide/knotx.git
$ cd knotx/
$ mvn clean install
```

You should see:

```
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] Knot.x ............................................. SUCCESS [  2.823 s]
[INFO] Knot.x - Reactive microservice assembler - Wiki Documentation SUCCESS [  0.763 s]
[INFO] Knot.x - Reactive microservice assembler - Core .... SUCCESS [  8.878 s]
[INFO] Knot.x - Reactive microservice assembler - JUnit Tests Knot.x helpers SUCCESS [  0.774 s]
[INFO] Knot.x - Reactive microservice assembler - Mocks ... SUCCESS [  3.298 s]
[INFO] Knot.x - Reactive microservice assembler - Repositories Connector SUCCESS [  0.112 s]
[INFO] Knot.x - Reactive microservice assembler - Repositories Connector - Filesystem SUCCESS [  4.231 s]
[INFO] Knot.x - Reactive microservice assembler - Repositories Connector - HTTP SUCCESS [  3.256 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter . SUCCESS [  0.073 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter - Common SUCCESS [  2.357 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter - Service HTTP SUCCESS [  7.030 s]
[INFO] Knot.x - Reactive microservice assembler - Knot .... SUCCESS [  0.162 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - HTML Fragment Splitter SUCCESS [  5.880 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - Fragment Assembler SUCCESS [  5.774 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - Action SUCCESS [  5.390 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - Service SUCCESS [  4.296 s]cd 
[INFO] Knot.x - Reactive microservice assembler - Knot - Handlebars SUCCESS [  2.742 s]
[INFO] Knot.x - Reactive microservice assembler - Knot.x HTTP Server SUCCESS [  5.971 s]
[INFO] Knot.x - Reactive microservice assembler - Standalone Knot.x SUCCESS [  2.375 s]
[INFO] Knot.x - Reactive microservice assembler - Example . SUCCESS [  0.128 s]
[INFO] Knot.x - Reactive microservice assembler - Example - Sample Handlebars Extension SUCCESS [  0.382 s]
[INFO] Knot.x - Reactive microservice assembler - Example - Action Adapter HTTP SUCCESS [  5.860 s]
[INFO] Knot.x - Reactive microservice assembler - Example - Sample Monolith App SUCCESS [  7.708 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:20 min
[INFO] Finished at: 2017-01-18T09:02:01+01:00
[INFO] Final Memory: 82M/901M
[INFO] ------------------------------------------------------------------------
```

See also [[how to run Knot.x demo|RunningTheDemo]] for the details how to run and configure the demo.

In case you wanted to try debugging Knot.x in IDE, see [[Debugging Knot.x|Debugging]].

