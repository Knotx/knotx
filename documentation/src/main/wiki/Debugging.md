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
4. Pick main class to be `io.knotx.launcher.KnotxLauncher`
5. Set VM options to `-Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory`
6. In Program Arguments specify that `KnotxStarterVerticle` should be run with the `-conf` parameter pointing to the JSON configuration you want to run (e.g. Example app)
```
run io.knotx.launcher.KnotxStarterVerticle -conf config/bootstrap.json
```
7. Set **Working directory** to the module where your json config exists (e.g. knotx-example-app)
8. Set **Use classpath of module** by selecting the module in which you have your configuration (e.g. knotx-example-app)
9. Finally, you can now Run or Debug this configuration and play with Knot.x as usual.

[[assets/knotx-debugging-config.png|alt=Knot.x Debugging config]]

## How to debug remote instance of Knot.x 
Assuming you have running Knot.x on dedicated machine (not localhost) and you'd like to debug it. All you have to do is just add JVM properties to enable it.

E.g.:
```
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
```

Then, you can connect to that instance (machine IP) on the port specified in the properties above.
