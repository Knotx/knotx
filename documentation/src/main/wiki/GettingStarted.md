# Getting started

## Getting Binaries
Knot.x provides binaries and default JSON configuration files at [Releases](https://github.com/Cognifide/knotx/releases).
Binaries are packaged in *fat jars*. A fat jar is a standalone executable Jar file containing all 
the dependencies required to run the application. It make those jars easy to execute.

To run Knot.x you need Java 8.

## Hello world!
First download Knot.x sample app:

[knotx-example-app-X.X.X-fat.jar](https://github.com/Cognifide/knotx/releases/)

[knotx-example-app.json](https://github.com/Cognifide/knotx/releases/)


Now you can run Knot.x:

```
java -jar knotx-example-app-X.X.X-fat.jar -conf knotx-example-app.json
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

