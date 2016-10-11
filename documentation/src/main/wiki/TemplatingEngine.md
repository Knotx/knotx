# Templating Engine

At the heart of the Knot.x Templating Engine lies [Handlebars.js](http://handlebarsjs.com/). Knot.x utilizes its Java port - [Handlebars.java](https://github.com/jknack/handlebars.java) to compile and evaluate templates.

## Extending handlebars with custom helpers

If the list of available handlebars helpers is not enough, you can easily extend it. To do this the following actions should be undertaken:

1. Create a class implementing ```com.cognifide.knotx.handlebars.CustomHandlebarsHelper``` interface. This interface extends [com.github.jknack.handlebars.Helper](https://jknack.github.io/handlebars.java/helpers.html)
2. Register the implementation as a service in the JAR file containing the implementation
    * Create a configuration file called META-INF/services/com.cognifide.knotx.handlebars.CustomHandlebarsHelper in the same project as your implementation class
    * Paste a fully qualified name of the implementation class inside the configuration file. If you're providing multiple helpers in a single JAR, you can list them in new lines (one name per line is allowed) 
    * Make sure the configuration file is part of the JAR file containing the implementation class(es)
3. Run Knot.x with the JAR file in the classpath

### Example extension

Sample application contains an example custom Handlebars helper - please take a look at the implementation of ```BoldHelper```:
* Implementation class: ```com.cognifide.knotx.example.monolith.handlebars.BoldHelper```
* service registration: ```knotx-example-monolith/src/main/resources/META-INF/services/com.cognifide.knotx.handlebars.CustomHandlebarsHelper```

