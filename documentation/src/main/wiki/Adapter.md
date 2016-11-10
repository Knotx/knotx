# Adapters
Adapter is a module that is responsible for communication between Knot.x (exactly [[Knots|Knot]]) and external services.

[[assets/knotx-adapters.png|alt=Adapters]]


## How does it work?
Adapters are thought as project specific logic and we recommend to create dedicated Adapter
every time some business logic or adapting service response to other format is required.

Each Knot that its contract that adapter must meet. Find out more about [[Knot contracts|Knot#how-does-it-work]].


### Types of adapters
There are two types of Adapters that Knot.x core can communicate with:
- [[Service Adapter|ServiceAdapter]] for [[Action Knot|ActionKnot]],
- [[Action Adapter|ActionAdapter]] for [[View Knot|ViewKnot]].

Knot.x comes with implementation of a [[Service Adapter|ServiceAdapter]], that enables communication with external 
services using HTTP Protocol. See [[Http Service Adapter|HttpServiceAdapter]] for more information.
Please note, that this implementation is very generic and we recommend to create project-specific 
adapters for any custom solution.

## How to configure?
Adapter may have its configuration in form of JSON object entry. 
Please see example configuration for [[Http Service Adapter|HttpServiceAdapter#how-to-configure]]

## How to extend?
| ! Note |
|:------ |
| Please note that this section explains how to write custom adapter using Java. But this is not the only way to connect with Knot.x thanks to [Vert.x polyglotism](http://vertx.io/). |

When writing a custom adapter you may find very useful to extend existing abstract 
[com.cognifide.knotx.adapter.api.AbstractAdapter](https://github.com/Cognifide/knotx/blob/master/knotx-adapters/knotx-adapter-api/src/main/java/com/cognifide/knotx/adapter/api/AbstractAdapter.java)
from `knotx-adapters/knotx-adapter-api`. This abstract parent does the part with [EventBus](http://vertx.io/docs/apidocs/io/vertx/core/eventbus/EventBus.html) 
and lets you simply focus on Adapter logic:

- `initConfiguration` method that init Adapter with `JsonObject` configuration read from configuration file.
- `processMessage` method that consumes message from [[Knot|Knot]] and returns message that will be send as Adapter response.

To deal with configuration model you may want to extend existing 
[com.cognifide.knotx.adapter.api.AdapterConfiguration](https://github.com/Cognifide/knotx/blob/master/knotx-adapters/knotx-adapter-api/src/main/java/com/cognifide/knotx/adapter/api/AdapterConfiguration.java)
from `knotx-adapters/knotx-adapter-api`.

Example implementation of `com.cognifide.knotx.adapter.api.AbstractAdapter` is [[Http Service Adapter|HttpServiceAdapter]] which
logic can be found in module `knotx-adapters/knotx-adapter-service-http`
in [com.cognifide.knotx.adapter.service.http.HttpServiceAdapterVerticle](https://github.com/Cognifide/knotx/blob/master/knotx-adapters/knotx-adapter-service-http/src/main/java/com/cognifide/knotx/adapter/service/http/HttpServiceAdapterVerticle.java).

### Configuration file
Adapter could have its JSON configuration file that will be passed to `initConfiguration` method in form of `JsonObject`.
You may read more about example configuration for `HttpServiceAdapterVerticle` in [[Http Service Adapter|HttpServiceAdapter]] section.

### Adapters common library
For many useful and reusable Adapters concept, please check our [knotx-adapter-common](https://github.com/Cognifide/knotx/tree/master/knotx-adapters/knotx-adapter-common)
module. You will find there support for `placeholders` and `http connectivity`. 

### How to run a custom adapter with Knot.x
Please refer to [[Deployment|KnotxDeployment]] section to find out more about deploying and running 
a custom adapter with Knot.x.
