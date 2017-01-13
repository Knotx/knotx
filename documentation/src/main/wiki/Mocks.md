# Mocks
For the prototyping or development purposes Knot.x is shipped with the Mocks Verticle. Currently, a following mocks are available:
- HTTP Service mock - HTTP endpoint serving JSON files on GET and POST requests.
- HTTP Remote repository mock - HTTP endpoint serving any file from local file system or classpath.

## HTTP Service mock
### How does it work?
- Listens on configured HTTP port on client requests
- On GET request
  - Retrieves file from the file system from the path `mockDataRoot`+`request.path`.
  - Sends response to the client with the content of the file setting proper response headers and status code
- On POST request, does the same as above. Optionally, if `bouncing=true` it adds to the response JSON Object form attributes from the POST request.  

### How to configure ?
Default configuration shipped with the verticle as `io.knotx.ServiceMock.json` file available in classpath.
```json
{
  "main": "com.cognifide.knotx.mocks.MockServiceVerticle",
  "options": {
    "config": {
      "mockDataRoot": "mock/service",
      "bouncing": true,
      "httpPort": 3000
    }
  }
}

```
In short, the default configuration defines:
- Mock listens on HTTP port 3000 for requests
- Root folder of mocked responses is `mock/service` relative to the classpath. If path starts from `/` it will search for files in local filesystem instead.
- Do bouncing of POST requests - Adds to the response JSON object form attributes from POST request.

Besides simple configuration as above, you can introduce delays to the service responses, to simulate real cases.
See [[how to configure delay ?|#how-to-configure-delay-]] section for details.

## HTTP Remote repository mock
### How does it work?
- Listens on configured HTTP port on client requests
- On GET request only
  - Retrieves file from the file system from the path `mockDataRoot`+`request.path`.
  - Sends response to the client with the content of the file setting proper response headers and status code

### How to configure ?
Default configuration shipped with the verticle as `io.knotx.RemoteRepositoryMock.json` file available in classpath.
```json
{
  "main": "com.cognifide.knotx.mocks.MockRemoteRepositoryVerticle",
  "options": {
    "config": {
      "mockDataRoot": "mock/repository",
      "httpPort": 3001
    }
  }
}
```
In short, the default configuration defines:
- Mock listens on HTTP port 3001 for requests
- Root folder of mocked responses is `mock/repository` relative to the classpath. If path starts from `/` it will search for files in local filesystem instead.

Besides simple configuration as above, you can introduce delays to the repository responses, to simulate real cases.
See [[how to configure delay ?|#how-to-configure-delay-]] section for details.

## How to configure delay ?
You can modify a default configuration by overriding it in starter JSON (see [[Configure through starter JSON|KnotxDeployment#how-to-configure-though-starter-json-]]).
Besides, properties shown in default configuration, you can supply **delay** configuration to simulate many situations.

See below for two options how to delay responses in your starter JSON.

### Delay all responses
Delay all responses of Service Mock by `100ms` and Remote Repository Mock responses by `20ms`
```json
{
  "modules": [
    "io.knotx.ServiceMock",
    "io.knotx.RemoteRepositoryMock"
  ],
  "config": {
    "io.knotx.ServiceMock": {
      "options" : {
        "config": {
          "delayAllMs": 100
        }
      }
    },
    "io.knotx.RemoteRepositoryMock": {
      "options" : {
        "config": {
          "delayAllMs": 20
        }
      }
    }    
  }
}
```
Alternatively, instead of configuring using starter JSON, you can supply it using JVM properties when starting mocks, e.g.:
```
$ java -Dio.knotx.ServiceMock.options.config.delayAllMs=100 -Dio.knotx.RemoteRepositoryMock.options.config.delayAllMs=20 -jar knotx-mocks-XXXX.jar ....
```

### Delay reponses for specific paths
Config below does:
- On Service Mock, delay response on path `/service/first.json` by `10ms`, others without delay
- On Remote Repository Mock, delay response on:
  - path `/content/remote/simple.html` by `50ms`
  - path `/content/remote/multiple-forms.html` by `100ms`
  - other paths without delay

```json
{
  "modules": [
    "io.knotx.ServiceMock",
    "io.knotx.RemoteRepositoryMock"
  ],
  "config": {
    "io.knotx.ServiceMock": {
      "options": {
        "config": {
          "delay": {
            "/service/first.json" : {
              "delayMs": 10
            }
          }
        }
      }
    },
    "io.knotx.RemoteRepositoryMock": {
      "options": {
        "config": {
          "delay": {
            "/content/remote/simple.html": {
              "delayMs": 50
            },
            "/content/remote/multiple-forms.html": {
              "delayMs": 100
            }
          }
        }
      }
    }    
  }
}
```
