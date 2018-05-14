# Configuration

The Knot.x configuration is basically split into two configuration files, such as
- bootstrap.json - a starter configuration what defines the application configuration format, location of application configurations (e.g. file, directory), as well as the ability to define whether the config should be scanned periodically
- application.conf - a main configuration file for the Knot.x based application. Knot.x promotes a [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md) format as it provides usefull mechanism, such as includes, variable substitutions, etc.

No matter how you start Knot.x (using Knot.x distribution or fat jar) the configuration of your setup is resolved in the following steps:
- If not specified in command line (`-conf /path/to/bootstrap.json`), Knot.x search for `bootstrap.json` in the classpath
- The definitions of config stores are read from `bootstrap.json` and configured configuration files locations are being loaded and validated
- E.g. if `application.conf` was specified, the Knot.x loads it from the defined location, parses it, resolve any includes and starts all the verticles defined in there.
- In case of any missing files Knot.x stops with the proper error message

## bootstrap.json
The file is structured around:
- a **Configuration Retriever** based on [Vert.x config module](https://vertx.io/docs/vertx-config/java/) that configures a set of configuration stores and ability to scan the changes
- a **Configuration store** that defines a location from where the configuration data is read and a syntax

The structure of the file is as follows
```json
{
  "configRetrieverOptions": {
    "scanPeriod": 5000,
    "stores": [
      {
        "type": "file",
        "format": "conf",
        "config": {
          "path": "config/application.conf"
        }
      }
    ]
  }
}
```
- `scanPeriod` in miliseconds. If property is specified, the Knot.x scans the defined configuration stores and redeploys the Knot.x application on changes.
- `stores` it's an array of configuration stores. Each store requires two properties:
  - `type` a declared data store, such as File(**file**), JSON(**json**), Environment Variables(**env**), System Properties(**sys**), HTTP endpoint(**http**), Event Bus (**event-bus**), Directory(**dir**), Git (**git**), Kubernetes Config Map(**configmap**), Redis(**redis**), Zookeeper (**zookeeper**), Consul (**consul**), Spring Config (**spring-config-server**), Vault (**vault**)
  - `format` a format of the configuration file, such as JSON(**json**), HOCON(**conf**) and YAML(**yaml**)
  
In addition to the the out of the box config stores and formats it's easy to provide your own custom implementation thanks to the Vert.x config SPI.
  
See the [Vert.x Config](https://vertx.io/docs/vertx-config/java/) for details how to use and configure any type of the store.

## application.conf
The application.conf used in Knot.x distribution supports the [HOCON](https://github.com/typesafehub/config/blob/master/HOCON.md) format. 
In short, the HOCON is the human optimized JSON that keeps the semantics (tree structure; set of types; encoding/escaping) from JSON, but 
make it more convenient as a human-editable config file format. Notable differences from JSON are comments, variables and includes.

The structure of the file is composed on the following sections:
- `modules` - an array of Verticles to start
- `global` - global variables you can use to reference instead of repeating the data each time you need it
- `config` - actual configuraiton for the given Verticle.
```hocon
########### Modules to start ###########
modules = [
  "server=io.knotx.server.KnotxServerVerticle"
  # Other modules to start
]

########### Globals ###########
global {
  serverPort = 8092

  snippetTagName = script

  #Other variables
}

########### Modules configurations ###########
config.server {
  options.config {
    include required("includes/server.conf")
  }
}

## More configs below

```

The `global` section is optional. If you want you can rename it, or remove it at all but you'd need to align references to those variables in each place you used it.
The `config` section can be defined in the form that works best for you, e.g.
It can be just raw JSON, or HOCONized version of it as follows:
```hocon
config {
   server {
      options {
         config {
            # verticle configuration
         }
      }
   }
}
```
Or define it in the form of path in JSON as below
```hocon
config.server.options.config {
    # verticle configuration
}
config.server.options.instances=2
```

Consult [HOCON specification](https://github.com/typesafehub/config/blob/master/HOCON.md) to explore all possibilities.

### Configuration options
The Knot.x distribution is shipped with a default `application.conf` that's split into multiple files, each per Verticle 
listed in `modules` section.
Thanks to the HOCON capabilities, the file is full of comments describing what you can configure in the system. E.g.
```hocon
# Event bus address of the Basic HTTP Service Adapter
address = ${global.address.adapter.basic}

clientOptions {
  maxPoolSize = 1000
  idleTimeout = 120 # seconds

  # If your services are using SSL you'd need to configure here low level details on how the
  # SSL connection is to be maintaned. Currently, if configured all defined in 'services' section
  # will use SSL
  #
  # Enable SSL
  # ssl = true
  #
  # Whether all server certificated should be trusted or not (e.g. self-signed certificates)
  # trustAll = true
  #
  # Hostname verification
  # verifyHost = false
  
  #....
}

```
In rare situation you can always consult the [Knot.x verticles cheatsheet](https://github.com/Cognifide/knotx/blob/master/documentation/src/main/cheatsheet/cheatsheets.adoc)
to find all available configuration options.

### System properties
As mentioned above, you can reference any configuration object or property using `${var}` syntax. Thanks to the HOCON capabilities
you can also use the same notation to get the value from JVM system properties, e.g.:
```
java -Dmy.property=1234
```
You can inject to your configuration in a such a way:
```
someField = ${my.property}
```
Additionally, if the value from system property is optional you can use `${?var}` syntax to say that inject that value only if it's available.
E.g. you can configure default value directly in the application.conf and customize it through system property if necessary.
```
someField = 1234
someField = ${?my.field.value}
```
