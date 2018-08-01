# Debugging Knot.x Demo

## How to set up Knot.x debugging in Intellij IDE
When you run Knot.x using [Knot.x stack](https://github.com/Knotx/knotx-stack) you would like to debug it as a remote application.
To enable debugging edit `bin/knotx` (or `bin/knotx.bat` for Windows users) and uncomment the `JVM_DEBUG` line.

> JVM_DEBUG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=18092"

Then, restart Knot.x.

1. Go to Run/Debug Configurations in IntelliJ
2. Add new Remote Configuration
3. Set name of the configuration
4. Set `port` to `18092` (or another number if you changed it in the shell config).

### How to debug remote instance of Knot.x
Assuming you have running Knot.x on dedicated machine (not localhost) as Knot.x distribution,
the instructions are exactly the same as for debbuging locally running Knot.x stack.

