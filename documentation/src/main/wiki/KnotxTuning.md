# Knot.x Tuning
[[Knotx deployment section|KnotxDeployment]] describes how to configure and run a Knot.x application. 
It is a great place to start the adventure with Knot.x. However, this configuration is not sufficient
for your production. Knot.x is the JVM application on the top of the [Vert.x framework](http://vertx.io/).
So uses an asynchronous non-blocking development model and not thread-based concurrency 
strategy called [event loop](http://vertx.io/docs/vertx-core/java/#_reactor_and_multi_reactor).
Those features allow Knot.x to be very efficient integration platform but introduce new aspects and 
challenges connected with reactive programming.

This section describes how our JVM process should be configured (including JVM Garbage Collector 
settings) and how our Knots and Adapters can be scaled vertically.
 
You can also play with a [Knot.x cookbook](https://github.com/Knotx/knotx-cookbook) which comes with 
all recommended JVM settings. But still all sections below are mandatory!  

## JVM settings
Knot.x supports Java 8. All settings in this section were validated with:
```
java version "1.8.0_131"
Java(TM) SE Runtime Environment (build 1.8.0_131-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.131-b11, mixed mode)
```
### Heap size
The settings below defines initial and maximum memory allocation pool for JVM. 
```
-Xms1024m -Xmx3072m
```
As Knot.x can be easily extended with your custom modules the memory settings should be validated with 
your current set of modules.

### GC settings
Knot.x creates a large number of short-lived objects with every HTTP request - it is a common 
characteristic for reactive systems. With Netty and Vert.x event loops, Knot.x is not limited with any
thread pool. So it can consume a large number of concurrent HTTP requests. When Knot.x is overloaded 
we face a typical producer-consumer problem - what if a producer is to active. The goal for Knot.x 
is to not crash with `OutOfMemoryError` under a heavy load and come back to the normal state when the 
load is lower.

The settings below were validated with *limited number of cases* so you need to perform a performance
tests with your own set of modules. In our tests, Knotx was deployed to AWS c5.large instances with
2CPU and 4GB of RAM.

```
-XX:+UseConcMarkSweepGC -XX:ParallelGCThreads=2 -XX:ParallelCMSThreads=1
``` 

The ParallelGCThreads parameter should be the same as the number of available CPUs.

### Single thread model optimisation
With Vert.x concurrency model all objects are locked only by one thread (event loop) so the JVM can make an 
optimization and "bias" that object to that thread in such a way that subsequent atomic operations 
on the object incurs no synchronization cost. More details can be found [here](http://www.oracle.com/technetwork/java/tuning-139912.html#section4.2.5).

```
-XX:+UseBiasedLocking -XX:BiasedLockingStartupDelay=0
```
