# Mocks
For the prototyping or development purposes Knot.x is shipped with the Mocks Verticle. Currently, a following mocks are available:
- HTTP Service mock - HTTP endpoint serving JSON files on GET and POST requests.
- HTTP Remote repository mock - HTTP endpoint serving any file from local file system or classpath.

## HTTP Service mock
### How does it work?
- Listens on configured HTTP port on client requests
- On GET request
  - Retrieves file from the file system from the path `mockDataRoot`+`request.path`.
  - Sends response to the client with the content of the file setting proper response headers and status code `200` (Or `404` is no requested mock file)
- On POST request, does the same as above. Optionally, if `bouncing=true` it adds to the response JSON Object form attributes from the POST request.  

### How to configure ?
A service mock has following configuration parameters that you need to set in your application configuration json
```json
{
  "io.knotx.mocks.MockServiceVerticle" : {
    "options": {
      "config": {
        "mockDataRoot": "mock/service",
        "bouncing": true,
        "httpPort": 3000
      }
    }
  }
}

```
The configuration options are:
- `httpPort` - HTTP port on which the service mock listens for requests
- `mockDataRoot` - Root folder of mocked responses. If path starts from `/` it will search for files in local filesystem, or on classpath if not leading slash.
- `bouncing` - Adds to the mock response a JSON object form attributes of the received POST request.

Mock service is already shipped with some mock responses for testing example project. If you want to use them, specify:
`mockDataRoot: "mock/service"`

Besides simple configuration as above, you can introduce delays to the service responses, to simulate real cases.
See [[how to configure delay ?|#how-to-configure-delay-]] section for details.

## HTTP Remote repository mock
### How does it work?
- Listens on configured HTTP port on client requests
- On GET request only
  - Retrieves file from the file system from the path `mockDataRoot`+`request.path`.
  - Sends response to the client with the content of the file setting proper response headers and status code `200` (Or `404` is no requested mock file)

### How to configure ?
A service mock has following configuration parameters that you need to set in your application configuration json
```json
{
  "io.knotx.mocks.MockRemoteRepositoryVerticle": {
    "options": {
      "config": {
        "mockDataRoot": "mock/repository",
        "httpPort": 3001
      }
    }
  }
}
```
The configuration options are:
- `httpPort` - HTTP port on which the service mock listens for requests
- `mockDataRoot` - Root folder of mocked responses. If path starts from `/` it will search for files in local filesystem, or on classpath if not leading slash.

Mock service is already shipped with some mock responses for testing example project. If you want to use them, specify:
`mockDataRoot: "mock/repository"`

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
    "repo=io.knotx.mocks.MockRemoteRepositoryVerticle",
    "service=io.knotx.mocks.MockServiceVerticle"
  ],
  "config": {
    "service": {
      "options" : {
        "config": {
          "delayAllMs": 100
        }
      }
    },
    "repo": {
      "options" : {
        "config": {
          "delayAllMs": 20
        }
      }
    }    
  }
}
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
    "repo=io.knotx.mocks.MockRemoteRepositoryVerticle",
    "service=io.knotx.mocks.MockServiceVerticle"
  ],
  "config": {
    "service": {
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
    "repo": {
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
