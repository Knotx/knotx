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
Knot.x is designed to run on Java 8. All the settings in this section were validated with:
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
we face a typical producer-consumer problem - when a producer is too active (e.g. more HTTP requests 
than the server is able to process). As the system engineers, our goal is to set the GC in such way that
Knot.x will not crash because of `OutOfMemoryError` under a heavy load and will be able to come back to 
the normal state when the load is lower (read more about the 
[Linux Out-of-Memory Killer](http://www.oracle.com/technetwork/articles/servers-storage-dev/oom-killer-1911807.html)).

The settings below were validated with *limited number of cases* so you need to perform a performance
tests with your own set of modules. In our tests, Knotx was deployed to AWS *c5.large* instances with
`2CPU` and `4GB` of RAM.

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

## OS Tuning
When running Knot.x on a server where high traffic is expected don't forget about OS settings. 
It is very important to set specific system properties to keep Knot.x instance alive during high traffic spikes.

We performed our tests on *CentOS 7* and following recommendations consider this OS.

### System settings
Following sysctl properties are recommended for the production Knot.x setup:

```
  net.core.netdev_max_backlog=2048
  net.core.rmem_max=8388608
  net.core.somaxconn=4096
  net.core.wmem_max=8388608
  net.ipv4.tcp_fin_timeout=10
  net.ipv4.tcp_max_syn_backlog=8192
  net.ipv4.tcp_rmem=4096 81920 8388608
  net.ipv4.tcp_slow_start_after_idle=0
  net.ipv4.tcp_syn_retries=2
  net.ipv4.tcp_synack_retries=2
  net.ipv4.tcp_tw_reuse=1
  net.ipv4.tcp_wmem=4096 16384 8388608
  vm.swappiness=10
```

### Opened descriptors limits
Remember to increase the default opened descriptor limits to at least `65536`. 
Without increasing this limit you may end with following exception in Knot.x logs:
```
Jan 18, 2018 1:25:08 PM sun.rmi.transport.tcp.TCPTransport$AcceptLoop executeAcceptLoop
WARNING: RMI TCP Accept-18092: accept loop for ServerSocket[addr=0.0.0.0/0.0.0.0,localport=18092] throws
java.net.SocketException: Too many open files (Accept failed)
        at java.net.PlainSocketImpl.socketAccept(Native Method)
        at java.net.AbstractPlainSocketImpl.accept(AbstractPlainSocketImpl.java:409)
        at java.net.ServerSocket.implAccept(ServerSocket.java:545)
        at java.net.ServerSocket.accept(ServerSocket.java:513)
        at sun.rmi.transport.tcp.TCPTransport$AcceptLoop.executeAcceptLoop(TCPTransport.java:400)
        at sun.rmi.transport.tcp.TCPTransport$AcceptLoop.run(TCPTransport.java:372)
        at java.lang.Thread.run(Thread.java:748)
```
This warning means that Knot.x tried to open more connections than the OS allows.
