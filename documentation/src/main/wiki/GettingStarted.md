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
[INFO] Knot.x ............................................. SUCCESS [  2.747 s]
[INFO] Knot.x - Reactive microservice assembler - documentation SUCCESS [  0.091 s]
[INFO] Knot.x - Reactive microservice assembler - Core Root POM SUCCESS [  0.159 s]
[INFO] Knot.x - Reactive microservice assembler - Common .. SUCCESS [  5.713 s]
[INFO] Knot.x - Reactive microservice assembler - Repository Verticle SUCCESS [  0.071 s]
[INFO] Knot.x - Reactive microservice assembler - Specialized Filesysten Repository Verticle SUCCESS [  4.622 s
]
[INFO] Knot.x - Reactive microservice assembler - Specialized Http Repository Verticle SUCCESS [  4.670 s]
[INFO] Knot.x - Reactive microservice assembler - HTML Fragment Splitter SUCCESS [  7.849 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Server SUCCESS [  7.902 s]
[INFO] Knot.x - Reactive microservice assembler - Knot Root SUCCESS [  0.109 s]
[INFO] Knot.x - Reactive microservice assembler - Knot API  SUCCESS [  0.414 s]
[INFO] Knot.x - Reactive microservice assembler - Service Knot SUCCESS [  4.563 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter Root SUCCESS [  0.100 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter API SUCCESS [  0.405 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter Common SUCCESS [  1.866 s]
[INFO] Knot.x - Reactive microservice assembler - Action Knot Verticle SUCCESS [  6.901 s]
[INFO] Knot.x - Reactive microservice assembler - Handlebars Knot SUCCESS [  3.623 s]
[INFO] Knot.x - Sample App with Mock service .............. SUCCESS [  0.305 s]
[INFO] Knot.x - Mocked services for sample app ............ SUCCESS [  2.894 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Service Adapter SUCCESS [ 10.189 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Action Adapter SUCCESS [  9.619 s]
[INFO] Knot.x - Reactive microservice assembler - Auhtorization Knot Verticle SUCCESS [ 10.165 s]
[INFO] Knot.x - Reactive microservice assembler - Standalone Knot.x SUCCESS [  2.779 s]
[INFO] Knot.x - Sample App with Mock service .............. SUCCESS [ 11.764 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:40 min
[INFO] Finished at: 2016-11-20T15:32:38+01:00
[INFO] Final Memory: 166M/1491M
[INFO] ------------------------------------------------------------------------
```

See also [[how to run Knot.x demo|RunningTheDemo]].

