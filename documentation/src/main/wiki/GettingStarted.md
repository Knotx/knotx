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
- knotx-example-app

To run Knot.x you need Java 8.

## Hello world!

**TODO: The instructions below will change in 1.3 thanks to the knotx starter distribution**

First download Knot.x sample app & config for latest version, or build it yourself (see [[Building|GettingStarted#building]] section):
- [knotx-example-app-X.Y.Z.fat.jar](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-example-app)
- [knotx-example-app-X.Y.Z.json](https://oss.sonatype.org/content/groups/public/io/knotx/knotx-example-app)

Now you can run Knot.x:
```
java -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory -Dknotx.home=. -cp config:lib/knotx-example-app-X.Y.Z-fat.jar io.vertx.core.Launcher run-knotx
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
[INFO] Reactor Summary:
[INFO]
[INFO] Knot.x ............................................. SUCCESS [  2.473 s]
[INFO] Knot.x Core ........................................ SUCCESS [ 15.869 s]
[INFO] Knot.x - Reactive microservice assembler - Mocks ... SUCCESS [  3.372 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter . SUCCESS [  0.052 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter - Common SUCCESS [  3.755 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter - Service HTTP SUCCESS [  5.101 s]
[INFO] Knot.x - Reactive microservice assembler - Knot .... SUCCESS [  0.068 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - Action SUCCESS [  5.388 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - Service SUCCESS [  4.156 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - Handlebars SUCCESS [  3.952 s]
[INFO] Knot.x - Reactive microservice assembler - Standalone Knot.x SUCCESS [  2.567 s]
[INFO] Knot.x - Reactive microservice assembler - Example . SUCCESS [  0.049 s]
[INFO] Knot.x - Reactive microservice assembler - Example - Sample Handlebars Extension SUCCESS [  0.423 s]
[INFO] Knot.x - Reactive microservice assembler - Example - Action Adapter HTTP SUCCESS [  4.880 s]
[INFO] Knot.x - Reactive microservice assembler - Example - Sample Gateway SUCCESS [  0.321 s]
[INFO] Knot.x - Reactive microservice assembler - Example - Sample Monolith App SUCCESS [  7.985 s]
[INFO] Knot.x - Reactive microservice assembler - Wiki Documentation SUCCESS [  0.452 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:01 min
[INFO] Finished at: 2018-03-26T09:56:18+02:00
[INFO] Final Memory: 134M/1215M
[INFO] ------------------------------------------------------------------------
```

See also [[how to run Knot.x demo|RunningTheDemo]] for the details how to run and configure the demo.

In case you wanted to try debugging Knot.x in IDE, see [[Debugging Knot.x|Debugging]].

