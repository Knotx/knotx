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
- [[Service Adapter|ServiceAdapter]] for [[Service Knot|ServiceKnot]],
- [[Action Adapter|ActionAdapter]] for [[Action Knot|ActionKnot]]

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
The Adapter API specifies an abstract class - `AdapterConfiguration` to handle JSON configuration support. This
abstraction can be used while implementing a custom Adapter but it is not required. Every Adapter must be
exposed with a unique Event Bus address - that's the only obligation (as is the case with Knots).
Please see an example configuration for [[Http Service Adapter|HttpServiceAdapter#how-to-configure]]

## How to extend?
Implementation of an Adapter does not require knowledge of how to communicate via the Vert.x event bus. It's wrapped by the **Vert.x Service Proxy** functionality so any new implementation can focus on the business logic of the Adapter. 

In order to implement an Adapter, follow the guide below:

1. Create your Knot by extending the `com.cognifide.knotx.adapter.AbstractAdapterProxy` class and implement your business logic in the `processRequest()` method with the return type of `Observable<AdapterResponse>` (promise of the `AdapterResponse`).

   You can refer to `com.cognifide.knotx.adapter.service.http.impl.HttpServiceAdapterProxyImpl.java` as an example.

2. Create a class extending `AbstractVerticle` that will simply read the configuration and register your `AdapterProxy` implementation at the `address` provided.

   Have a look at `com.cognifide.knotx.adapter.service.http.HttpServiceAdapterVerticle.java` to see how the `HttpServiceAdapterProxyImpl` is registered.

The `AbstractAdapterProxy` class provides the following methods that you can extend in your implementation:

- `Observable<AdapterResponse> processRequest(AdapterRequest message)` method that consumes `AdapterRequest` messages from [[Knot|Knot]] and returns `AdapterResponse` object as `rx.Observable`
- Optionally, `AdapterResponse getErrorResponse(AdapterRequest request, Throwable error)` method which handles any Exception thrown during processing, and is responsible for preparing proper AdapterResponse on such situations. By default `AbstractAdapterProxy` implements this method, and returns `AdapterResponse` with the `ClientResponse` object having `500` status code and the error message in response body. 

| ! Note |
|:------ |
| Please note that while this section focuses on the Java language specifically, it's not the only choice you have. Thanks to [the polyglot nature of Vert.x](http://vertx.io), you can implement your Adapters and Knots using other languages. |

| ! Note |
|:------ |
| Besides the Verticle implementation itself, a custom implementation of your Knot must be built as a Knot.x module in order to be deployed as part of Knot.x. Follow the [[Knot.x Modules|KnotxModules]] documentation in order to learn how to make your Knot a module. | 

### Adapters common library
For many useful and reusable Adapters concept, please check our [knotx-adapter-common](https://github.com/Cognifide/knotx/tree/master/knotx-adapter/knotx-adapter-common)
module. You will find there support for `placeholders` and `http connectivity`. 

### How to run a custom Adapter with Knot.x
Please refer to [[Deployment|KnotxDeployment]] section to find out more about deploying and running 
a custom Adapters with Knot.x.
