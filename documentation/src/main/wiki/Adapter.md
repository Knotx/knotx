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
Adapter API specifies abstract `AdapterConfiguration` class to handle JSON configuration support. This
abstraction can be used while custom Adapter implementation but it is not required. Every Adapter must be
exposed with unique Event Bus address - that's the only obligation (the same like for Knots).
Please see example configuration for [[Http Service Adapter|HttpServiceAdapter#how-to-configure]]

## How to extend?
Implementation of Adapter does not require knowledge how to communicate via Vert.x event bus. It's wrapped by **Vert.x Service Proxy** functionality, 
so any new implementation need to focus on the business logic of the Adapter. 

In order to implement Adapter, follow the guide below:
1. Create your Knot by extending `com.cognifide.knotx.adapter.AbstractAdapterProxy` class, and implement your business logic in the `processRequest()` method that 
should return of `Observable<AdapterResponse>` (promise of the AdapterResponse).
*See `com.cognifide.knotx.adapter.service.http.impl.HttpServiceAdapterProxyImpl.java` as an example.*

2. Create class extending `AbstractVerticle` that will should simple read the configuration, and register your AdapterProxy implementation on the given `address`.
See `com.cognifide.knotx.adapter.service.http.HttpServiceAdapterVerticle.java` how the `HttpServiceAdapterProxyImpl` is registered.

`AbstractAdapterProxy` class provides following methods that you can extend in your implementation.
These are:
- `Observable<AdapterResponse> processRequest(AdapterRequest message)` method that consumes `AdapterRequest` messages from [[Knot|Knot]] and returns `AdapterResponse` object as `rx.Observable`
- Optionally, `AdapterResponse getErrorResponse(AdapterRequest request, Throwable error)` method which handles any Exception thrown during processing, and is responsible for preparing proper AdapterResponse on such situations. By default `AbstractAdapterProxy` implements this method, and returns `AdapterResponse` with the `ClientResponse` object having `500` status code and the error message in response body. 

| ! Note |
|:------ |
| Please note, that this section focuses on Java language only. Thanks to [Vert.x polyglotism mechanism](http://vertx.io) you can implement your Adapters and Knots using other languages. |

| ! Note |
|:------ |
| Besides Verticle implementation itself, a custom implementation of your Knot must be build as Knot.x module in order to be deployed as part of Knot.x. Follow the [[Knot.x Modules|KnotxModules]] in order to see how to make your Knot a module. | 

### Adapters common library
For many useful and reusable Adapters concept, please check our [knotx-adapter-common](https://github.com/Cognifide/knotx/tree/master/knotx-adapter/knotx-adapter-common)
module. You will find there support for `placeholders` and `http connectivity`. 

### How to run a custom Adapter with Knot.x
Please refer to [[Deployment|KnotxDeployment]] section to find out more about deploying and running 
a custom Adapters with Knot.x.
