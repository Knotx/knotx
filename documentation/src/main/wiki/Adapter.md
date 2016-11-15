# Adapters
Adapters are modules which are responsible for communication between Knot.x (exactly [[Knots|Knot]]) 
and external services.

[[assets/knotx-adapters.png|alt=Adapters]]


## How does it work?
Adapters can be thought as extension points where project specific logic appears. With custom [[Knots|Knot]] 
they provides very flexible mechanism to inject project specific requirements.

We recommend to create dedicated Adapter every time some service-level business logic or service 
response adaption to other format is required.


### Types of adapters
Knot.x Core by default introduces two types of Adapters connected with Knot implementations:
- [[Service Adapter|ServiceAdapter]] for [[Action Knot|ActionKnot]],
- [[Action Adapter|ActionAdapter]] for [[View Knot|ViewKnot]]

Knot.x comes with a generic implementation of [[Service Adapter|ServiceAdapter]], that enables communication 
with external services using HTTP Protocol (only GET requests). See [[Http Service Adapter|HttpServiceAdapter]] 
for more information. Please note, that this implementation is very generic and we recommend to create 
project-specific Adapters for any custom requirements.

Action Adapters are project specific in terms of error handling and redirection mechanisms. Knot.x Core
is not going to provide any generic Action Adapters.

For custom Knots we can introduce custom Adapter types. As far as Knots must follow [[Knot contract|Knot#how-does-it-work]],
Adapters are coupled with Knot directly so they can define their custom request, response or 
configuration. The communication between Knot and Adapter can be custom too. 

## How to configure?
Adapter API specifies abstract `AdapterConfiguration` class to handle JSON configuration support. This
abstraction can be used while custom Adapter implementation but it is not required. Every Adapters must be
exposed with unique Event Bus address - that's only one obligation.
Please see example configuration for [[Http Service Adapter|HttpServiceAdapter#how-to-configure]]

## How to extend?
First we need to extend abstract 
[com.cognifide.knotx.adapter.api.AbstractAdapter](https://github.com/Cognifide/knotx/blob/master/knotx-adapters/knotx-adapter-api/src/main/java/com/cognifide/knotx/adapter/api/AbstractAdapter.java)
class from `knotx-adapters/knotx-adapter-api`. AbstractAdapter hides Event Bus communication and JSON configuration reading parts
and lets you to focus on Adapter logic:

- `initConfiguration` method that init Adapter configuration with `JsonObject` model.
- `processMessage` method that consumes `JsonObject` messages from [[Knot|Knot]] and returns `JsonObject` messages 
with Adapter responses.

To deal with configuration model you may extend
[com.cognifide.knotx.adapter.api.AdapterConfiguration](https://github.com/Cognifide/knotx/blob/master/knotx-adapters/knotx-adapter-api/src/main/java/com/cognifide/knotx/adapter/api/AdapterConfiguration.java) class
from `knotx-adapters/knotx-adapter-api`.

Reference implementation of `com.cognifide.knotx.adapter.api.AbstractAdapter` is [[Http Service Adapter|HttpServiceAdapter]] from
`knotx-adapters/knotx-adapter-service-http` module.

| ! Note |
|:------ |
| Please note that this section focused on Java language only. Thanks to [Vert.x polyglotism mechanism](http://vertx.io) you can implement your Adapters and Knots using language you like. |

### Configuration file
Adapter could have its JSON configuration file that will be passed to `initConfiguration` method in form of `JsonObject`.
You may read more about example configuration for `HttpServiceAdapterVerticle` in [[Http Service Adapter|HttpServiceAdapter]] section.

### Adapters common library
For many useful and reusable Adapters concept, please check our [knotx-adapter-common](https://github.com/Cognifide/knotx/tree/master/knotx-adapters/knotx-adapter-common)
module. You will find there support for `placeholders` and `http connectivity`. 

### How to run a custom Adapter with Knot.x
Please refer to [[Deployment|KnotxDeployment]] section to find out more about deploying and running 
a custom Adapters with Knot.x.
