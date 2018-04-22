# Debugging Knot.x Demo

## Requirements

To run Knot.x you only need Java 8.
To build it you also need Maven (version 3.3.1 or higher).
Intellij IDE

## How to set up Knot.x debugging in Intellij IDE
Assuming you have Knot.x project opened in Intellij, you can set up Run/Debug Application configuration to be able to run or debug Demo/Example or 
Standalone Knot.x application directly in your IDE.

1. Go to Run/Debug Configurations in IntelliJ
2. Add new Application Configuration

[[assets/knotx-debugging-new-config.png|alt=Knot.x Debugging new config]]

3. Set name of the configuration
4. Pick main class to be `io.vertx.core.Launcher`
5. Set VM options to `-Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory`
6. In Program Arguments specify that `KnotxStarterVerticle` should be run with the `-conf` parameter pointing to the `bootstrap.json` that points to your configuration (e.g. Example app)
```
run io.knotx.launcher.KnotxStarterVerticle -conf config/bootstrap.json
```
7. Set **Working directory** to the module where your json config exists (e.g. knotx-example-app)
8. Set **Use classpath of module** by selecting the module in which you have your configuration (e.g. knotx-example-app/config)
9. Finally, you can now Run or Debug this configuration and play with Knot.x as usual.

[[assets/knotx-debugging-config.png|alt=Knot.x Debugging config]]

## How to debug remote instance of Knot.x 
Assuming you have running Knot.x on dedicated machine (not localhost) as Knot.x distribution.
In order to enable debug port, uncomment `JVM_DEBUG` variable in `bin/knotx` and change to the the desired debug port.

Then, restart Knot.x and know you can connect to that instance (machine IP) on the port specified in the properties above.
