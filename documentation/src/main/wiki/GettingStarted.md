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
[INFO] Knot.x - Reactive microservice assembler - Adapter - Service HTTP SUCCESS [ 11.199 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter - Action HTTP SUCCESS [  6.620 s]
[INFO] Knot.x - Reactive microservice assembler - Knot .... SUCCESS [  0.137 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - HTML Fragment Splitter SUCCESS [  5.504 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - Fragment Assembler SUCCESS [  5.578 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - Action SUCCESS [  6.433 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - Service SUCCESS [  4.957 s]
[INFO] Knot.x - Reactive microservice assembler - Knot - Handlebars SUCCESS [  3.124 s]
[INFO] Knot.x - Reactive microservice assembler - Knot.x HTTP Server SUCCESS [  6.564 s]
[INFO] Knot.x - Reactive microservice assembler - Standalone Knot.x SUCCESS [  2.510 s]
[INFO] Knot.x - Reactive microservice assembler - Example Knot.x App SUCCESS [  7.975 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:01 min
[INFO] Finished at: 2017-01-12T22:04:02+01:00
[INFO] Final Memory: 73M/1054M
[INFO] ------------------------------------------------------------------------
```

See also [[how to run Knot.x demo|RunningTheDemo]] for the details how to run and configure the demo.

In case you wanted to try debugging Knot.x in IDE, see [[Debugging Knot.x|Debugging]].

