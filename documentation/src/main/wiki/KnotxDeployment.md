## Deployment options
To deploy verticle with advanced options use following properties:
```json
{
    ... 
  "com.cognifide.knotx.server.KnotxServerVerticle": {
    "config": {...},
    
    "instances": 2,
    "worker" : false,
    "multiThreaded": false,
    "isolationGroup": "null",
    "ha": false,
    "extraClasspath": [],
    "isolatedClasses": []
  }
    ...
}
```
- **instances** - number of verticle instances.
- **worker** - deploy verticle as a worker.
- **multiThreaded** - deploy verticle as a multi-threaded worker.
- **isolationGroup** - array of isolation group.
- **ha** - deploy verticle as highly available.
- **extraClasspath** - extra classpath to be used when deploying the verticle.
- **isolatedClasses** - array of isolated classes.

Read more about [vert.x Deployment Options](http://vertx.io/docs/apidocs/io/vertx/core/DeploymentOptions.html)


# TODO
Describe how to configure knot.x as a whole, recommended approach for deployment - as it's now, logging config etc.
