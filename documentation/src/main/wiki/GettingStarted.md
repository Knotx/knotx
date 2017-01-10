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
[INFO] Knot.x ............................................. SUCCESS [  3.487 s]
[INFO] Knot.x - Reactive microservice assembler - documentation SUCCESS [  0.891 s]
[INFO] Knot.x - Reactive microservice assembler - Core Root POM SUCCESS [  0.156 s]
[INFO] Knot.x - Reactive microservice assembler - Common .. SUCCESS [  6.683 s]
[INFO] Knot.x - Reactive microservice assembler - Launcher  SUCCESS [  3.739 s]
[INFO] Knot.x - Reactive microservice assembler - Repositories Connectors SUCCESS [  0.087 s]
[INFO] Knot.x - Reactive microservice assembler - Filesystem Repository Connector Verticle SUCCESS [  2.635 s]
[INFO] Knot.x - Reactive microservice assembler - Http Repository Connector Verticle SUCCESS [  2.908 s]
[INFO] Knot.x - Reactive microservice assembler - HTML Fragment Splitter SUCCESS [  5.687 s]
[INFO] Knot.x - Reactive microservice assembler - Fragment Assembler SUCCESS [  5.900 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Server SUCCESS [  6.614 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter Root SUCCESS [  0.103 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter API SUCCESS [  0.414 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter Common SUCCESS [  1.932 s]
[INFO] Knot.x - Sample App with Mock service .............. SUCCESS [  0.389 s]
[INFO] Knot.x - Mocked services for sample app ............ SUCCESS [  3.328 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Service Adapter SUCCESS [  6.614 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Action Adapter SUCCESS [  6.442 s]
[INFO] Knot.x - Reactive microservice assembler - Knot Root SUCCESS [  0.084 s]
[INFO] Knot.x - Reactive microservice assembler - Knot API  SUCCESS [  0.357 s]
[INFO] Knot.x - Reactive microservice assembler - Action Knot Verticle SUCCESS [  5.906 s]
[INFO] Knot.x - Reactive microservice assembler - Service Knot SUCCESS [  4.062 s]
[INFO] Knot.x - Reactive microservice assembler - Handlebars Knot SUCCESS [  2.613 s]
[INFO] Knot.x - Reactive microservice assembler - Standalone Knot.x SUCCESS [  2.428 s]
[INFO] Knot.x - Sample App with Mock service .............. SUCCESS [  8.365 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:22 min
[INFO] Finished at: 2017-01-09T15:54:53+01:00
[INFO] Final Memory: 110M/1187M
[INFO] ------------------------------------------------------------------------

```

See also [[how to run Knot.x demo|RunningTheDemo]].

