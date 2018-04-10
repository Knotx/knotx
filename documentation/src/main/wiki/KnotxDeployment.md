# Knot.x Deployment

A guide how to prepare deployments of your custom application based on Knot.x

## Custom extension
Any custom extension your project is to produce, which is Handlebars extensions, custom Knots, Adapters, etc. can be developed in a regular way.
The only requirement is to assure that any `jar` file produced in your build is to deployed to your maven nexus repository.

## Prepare and run the instance
1. Download the latest Knot.x distribution from our [Downloads](http://knotx.io/downloads) page.
2. Unpack the package into the new folder. The unpacked distribution consists of the following elements:
```
bin/             # `knotx` script to launch Knot.x application
lib/             # a home for all core Knot.x as well as extension jar files
conf/            # configuration files for logger, cluser, and Knot.x application
knotx-stack.json # Knot.x stack descriptor. It consists array of all dependencies for your project (maven coordinates)
```
3. If you need to customize logger, edit `conf/logback.xml` file. See [[Knot.x Logging|Logging]] on how to do it.
4. Modify `application.conf` by adding your custom modules to start and/or configuration for them
5. Run knotx
```
$> bin/knotx run-knotx
```

### Vert.x metrics
You might want to enable Vert.x metrics in order to monitor how Knot.x performs.
Currently, it's possible to enable JMX metrics, so you can use any JMX tool, like JConsole, to inspect all the metrics Vert.x collects.

In order to enable it, uncommend `JMX_OPTS` variable in `bin/knotx` start script

Vert.x collects:
- Vert.x elements metrics (such as amount of verticles, worker pool sizes, etc.)
- Event bus metrics (such as bytes sent/received, messages delivered, etc.)
- HTTP Clients metrics (such as bytes sent/received, connections, amount of requests, per http code responses, etc.)
- HTTP Server metrics (such as bytes sent/received, connections, amount of requests, etc.)

For a detailed description of available metrics please check [Vert.x The Metrics](http://vertx.io/docs/vertx-dropwizard-metrics/java/#_the_metrics) page

| ! Warning |
|:------ |
| **We don’t recommend gathering metrics from your production environment. JMX’s RPC API is fragile and bonkers. However for development purposes and troubleshooting it can be very useful.** |

## How to configure ?
The `conf/application.conf` is the main configuration file describing what Knot.x modules need to be started as part of Knot.x.

`aplication.conf` configuration available on GitHub looks like below
```hocon
modules = [
    "server=io.knotx.server.KnotxServerVerticle"
    "httpRepo=io.knotx.repository.http.HttpRepositoryConnectorVerticle"
    "fsRepo=io.knotx.repository.fs.FilesystemRepositoryConnectorVerticle"
    "splitter=io.knotx.splitter.FragmentSplitterVerticle"
    "assembler=io.knotx.assembler.FragmentAssemblerVerticle"
    "hbsKnot=io.knotx.knot.templating.HandlebarsKnotVerticle"
    "serviceKnot=io.knotx.knot.service.ServiceKnotVerticle"
    "actionKnot=io.knotx.knot.action.ActionKnotVerticle"
    "serviceAdapter=io.knotx.adapter.service.http.HttpServiceAdapterVerticle"
  ]
```
As you see, it simply have list of modules that Knot.x should start in the form of:
`<alias>=<verticle.class.name>`.

In order to reconfigure Knot.x to your needs, e.g. to set port of HTTP server, or HTTP headers that are
being passed, or addresses of the client services used for rendering dynamic content you can use your configuration json file.

Just add `config` section for each module (using **alias** as the reference) that needs default configuration to be modified. 
You only need to specify elements that should be changed. Follow the guide of each Verticle to see the supported parameters.

Some of the verticles, might respect System properties to alter some configurations. E.g. see the [[Servcer|Server]] 

### How to configure Knot.x module
In your project specific `conf/application.conf` add `config` object for a given module alias. For each module you want to configure put a field with configuration object.
For instance, if you want to modify configuration of KnotxServer module, you can do it as follows:
```hocon
modules = [
  "server=io.knotx.server.KnotxServerVerticle"
  "httpRepo=io.knotx.repository.http.HttpRepositoryConnectorVerticle"
  "fsRepo=io.knotx.repository.fs.FilesystemRepositoryConnectorVerticle"
  "splitter=io.knotx.splitter.FragmentSplitterVerticle"
  "assembler=io.knotx.assembler.FragmentAssemblerVerticle"
  "hbsKnot=io.knotx.knot.templating.HandlebarsKnotVerticle"
  "serviceKnot=io.knotx.knot.service.ServiceKnotVerticle"
  "actionKnot=io.knotx.knot.action.ActionKnotVerticle"
  "serviceAdapter=io.knotx.adapter.service.http.HttpServiceAdapterVerticle"
]
config.server {
  options.instances: 2
  
  options.config {
    serverOptions.port: 9999
  }
}
```
Important things to remember:
- `options` field maps exactly to a [vert.x Deployment Options](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html) object.
It means, that you can specify here deployment options such as how many instances of that module should be deployed, etc.
- Inside `options` you can supply `config` object where you override configuration for the verticle provided by the module.
See Knot.x Verticle documentation to see what's available on each Verticle.

If you start Knot.x with the configuration as above, it will start all modules listed in the config, but the `server` will be deployed as:
- Two KnotxServer Verticle instances
- Both will listen on port 9999 (vert.x will do load balancing for you)


### What happens when config refers to non-existing module?
Let's assume that you work over a new `com.acme.MyCustomModuleVerticle` verticle and it will be implemented inside `custom-module.jar`. 
As mentioned above, you should do 2 things to start using it within Knot.x instance. 
You need to add it to the list of Knot.x modules in the main config file:

```hocon
modules = [
    "server=io.knotx.server.KnotxServerVerticle"
    "myFancyModule=com.acme.MyCustomModuleVerticle"
]
```

And add the `custom-module.jar` to the classpath. But what will happen, if you actually forgot to add `custom-module.jar` to the classpath?
Knot.x will start the instance with following error:

```
2018-01-01 15:11:22.003 [vert.x-eventloop-thread-0] ERROR i.k.launcher.KnotxStarterVerticle - Can't deploy myFancyModule=java:com.acme.MyCustomModuleVerticle: {}
...
```

and the list of deployment status at the end shows:
```
2018-02-21 15:28:46.419 [vert.x-eventloop-thread-0] INFO  i.k.launcher.KnotxStarterVerticle - Knot.x STARTED

		Failed deploying customKnot [java:com.acme.MyCustomModuleVerticle]
		Deployed fsRepo [java:io.knotx.repository.fs.FilesystemRepositoryConnectorVerticle] [ef35d8e8-887b-4f8b-bd2e-31943d8ff706]
		Deployed assembler [java:io.knotx.assembler.FragmentAssemblerVerticle] [0f5d74f4-2233-4937-b11c-d20d3e5451b1]
		...
```

Your Verticle instance not started as the `com.acme.MyCustomModuleVerticle` class didn't exist.

### Can I run Knot.x in Docker ?
Yes you can, however there is no Docker images available yet. Stay tuned, they're coming soon.

### Can I run Knot.x in virtualized environment ?
Yes you can. See the [Knotx/knotx-cookbook](https://github.com/Knotx/knotx-cookbook) Github repository for Chef cookbooks.