# Getting started

## Getting Binaries
Knot.x provides binaries and default JSON configuration files at [Releases](https://github.com/Cognifide/knotx/releases).
Binaries are packaged in *fat jars*. A fat jar is a standalone executable Jar file containing all 
the dependencies required to run the application. It make those jars easy to execute.

To run Knot.x you need Java 8.

## Hello world!
First download Knot.x sample app:

[knotx-example-monolith-X.X.X-fat.jar](https://github.com/Cognifide/knotx/releases/)

[knotx-example-monolith.json](https://github.com/Cognifide/knotx/releases/)


Now you can run Knot.x:

```
java -jar knotx-example-monolith-X.X.X-fat.jar -conf knotx-example-monolith.json
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
[INFO] Knot.x ............................................. SUCCESS [  2.304 s]
[INFO] Knot.x - Reactive microservice assembler - documentation SUCCESS [  0.067 s]
[INFO] Knot.x - Reactive microservice assembler - Core Root POM SUCCESS [  0.139 s]
[INFO] Knot.x - Reactive microservice assembler - Common .. SUCCESS [  6.657 s]
[INFO] Knot.x - Reactive microservice assembler - Launcher  SUCCESS [  3.008 s]
[INFO] Knot.x - Reactive microservice assembler - Repositories Connectors SUCCESS [  0.060 s]
[INFO] Knot.x - Reactive microservice assembler - Filesystem Repository Connector Verticle SUCCESS [  2.290 s]
[INFO] Knot.x - Reactive microservice assembler - Http Repository Connector Verticle SUCCESS [  2.415 s]
[INFO] Knot.x - Reactive microservice assembler - HTML Fragment Splitter SUCCESS [  4.615 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Server SUCCESS [  5.300 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter Root SUCCESS [  0.085 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter API SUCCESS [  0.337 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter Common SUCCESS [  1.384 s]
[INFO] Knot.x - Sample App with Mock service .............. SUCCESS [  0.668 s]
[INFO] Knot.x - Mocked services for sample app ............ SUCCESS [  2.068 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Service Adapter SUCCESS [  5.291 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Action Adapter SUCCESS [  4.974 s]
[INFO] Knot.x - Reactive microservice assembler - Knot Root SUCCESS [  0.076 s]
[INFO] Knot.x - Reactive microservice assembler - Knot API  SUCCESS [  0.263 s]
[INFO] Knot.x - Reactive microservice assembler - Action Knot Verticle SUCCESS [  4.553 s]
[INFO] Knot.x - Reactive microservice assembler - Service Knot SUCCESS [  3.496 s]
[INFO] Knot.x - Reactive microservice assembler - Handlebars Knot SUCCESS [  2.173 s]
[INFO] Knot.x - Reactive microservice assembler - Standalone Knot.x SUCCESS [  2.096 s]
[INFO] Knot.x - Sample App with Mock service .............. SUCCESS [  6.944 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:01 min
[INFO] Finished at: 2017-01-03T12:47:50+01:00
[INFO] Final Memory: 107M/910M
[INFO] ------------------------------------------------------------------------

```

See also [[how to run Knot.x demo|RunningTheDemo]].

