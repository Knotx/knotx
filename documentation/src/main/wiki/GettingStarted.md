# Getting started

## Getting Binaries
Knot.x provides binaries and default JSON configuration files at [[Releases | https://github.com/Cognifide/knotx/releases]].
Binaries are packaged in *fat jars*. A fat jar is a standalone executable Jar file containing all 
the dependencies required to run the application. It make those jars easy to execute.

To run Knot.x you need Java 8.

## Hello world!
First download Knot.x sample app:

```
[[knotx-example-monolith-0.4.1-fat.jar|https://github.com/Cognifide/knotx/releases/download/0.4.1/knotx-example-monolith-0.4.1-fat.jar]]
[[knotx-example-monolith.json|https://github.com/Cognifide/knotx/releases/download/0.4.1/knotx-example-monolith.json]]
```

Now you can run Knot.x:

```
java -jar knotx-example-monolith-0.4.1-fat.jar -conf knotx-example-monolith.json
```

That's all. Finally you can open a browser and type an url `http://localhost:8092/content/local/simple.html`. 
You should see page which is served from local repository and contains example data from mock services.

Page should look like:

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
[INFO] Knot.x ............................................. SUCCESS [  1.806 s]
[INFO] Knot.x - Reactive microservice assembler - documentation SUCCESS [  0.083 s]
[INFO] Knot.x - Reactive microservice assembler - Core Root POM SUCCESS [  0.181 s]
[INFO] Knot.x - Reactive microservice assembler - Common .. SUCCESS [  6.460 s]
[INFO] Knot.x - Reactive microservice assembler - Repository Verticle SUCCESS [  0.079 s]
[INFO] Knot.x - Reactive microservice assembler - Specialized Filesysten Repository Verticle SUCCESS [  3.380 s]
[INFO] Knot.x - Reactive microservice assembler - Specialized Http Repository Verticle SUCCESS [  2.962 s]
[INFO] Knot.x - Reactive microservice assembler - HTML Fragment Splitter SUCCESS [  5.468 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Server SUCCESS [  7.419 s]
[INFO] Knot.x - Reactive microservice assembler - View Knot SUCCESS [  8.251 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter Root SUCCESS [  0.101 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter API SUCCESS [  0.497 s]
[INFO] Knot.x - Reactive microservice assembler - Adapter Common SUCCESS [  1.591 s]
[INFO] Knot.x - Reactive microservice assembler - Action Knot Verticle SUCCESS [  5.725 s]
[INFO] Knot.x - Sample App with Mock service .............. SUCCESS [  0.047 s]
[INFO] Knot.x - Mocked services for sample app ............ SUCCESS [  2.079 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Service Adapter SUCCESS [  6.607 s]
[INFO] Knot.x - Reactive microservice assembler - HTTP Action Adapter SUCCESS [  5.988 s]
[INFO] Knot.x - Reactive microservice assembler - Auhtorization Knot Verticle SUCCESS [  5.128 s]
[INFO] Knot.x - Reactive microservice assembler - Standalone Knot.x SUCCESS [  2.235 s]
[INFO] Knot.x - Sample App with Mock service .............. SUCCESS [  6.488 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:13 min
[INFO] Finished at: 2016-11-08T11:47:35+01:00
[INFO] Final Memory: 156M/837M
[INFO] ------------------------------------------------------------------------
```

See also [[how to run Knot.x demo|RunningTheDemo]].

