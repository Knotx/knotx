# Logging
By default Knot.x picks up the logger configuration from its default location for the system (e.g. classpath:logback.xml), 
but you can set the location of the config file through `logback.configurationFile` system property.
```
java -Dlogback.configurationFile=/path/to/logback.xml -jar ...
```
Knot.x core provides preconfigured three log files:
- `knotx.log` that logs all **ERROR** messages from the Netty & Vert.x and **INFO** messages from Knot.x application. Enabled by default on Knot.x standalone
- `knotx-access.log` that logs all HTTP requests/responses to the Knot.x HTTP Server. Enabled by default on Knot.x standalone
- `knotx-netty.log` that logs all network activity (logged by the Netty). Not enabled on Knot.x standalone. See [[Log network activity|#log-network-activity]] on how to enable it.

All logs are configured by default:
- To log both to file and console
- Log files are rolled over every day or if the log file exceeds 10MB
- Rolled files are automatically compressed
- History log files are kept forever

A default configuration can be overriden in order to meet your log policy. See [[Configure logback|#configure-logback]] for details.

## Configure Logback
Knot.x provides a default set of logback configurations that you can include, if you just want to set levels, or create your own specific loggers, e.g.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true">
  <include resource="io/knotx/logging/logback/defaults.xml"/>
  <include resource="io/knotx/logging/logback/console-appender.xml"/>
  <include resource="io/knotx/logging/logback/file-appender.xml"/>

  <root level="INFO">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
  </root>

  <logger name="io.knotx" level="TRACE"/>
</configuration>
```
Will create console & file logger for Knot.x logs. Besides that there are other includes that brings new logs, these are:
- `io/knotx/logging/logback/access.xml` - access log
- `io/knotx/logging/logback/netty.xml` - network activity logs

All those configurations uses useful System properties which the Logback takes care of creating for you. These are:
- `${LOG_PATH}` - represents a directory for log files to live in, for knotx, access & netty logs. It's set with the value from `LOG_PATH` System property, or from your own logback.xml file. If none of these specified, logs to the current working directory `/logs` subfolder.
- `${KNOTX_LOG_FILE}`, `${ACCESS_LOG_FILE}`, `${NETTY_LOG_FILE}` - Ignores `LOG_PATH` and use that property to specify actual location for the knotx, access & netty log files (e.g. `/var/logs/knotx.log`, or `/var/logs/access.log`)
- `${KNOTX_LOG_DATEFORMAT_PATTERN}` - Allows to specify a different date-time format for log entries in `knotx.log` and `knotx-netty.log` files only. If not specified, `yyyy-MM-dd HH:mm:ss.SSS` is used.
- `${CONSOLE_KNOTX_LOG_PATTERN}` - Allows to override a default log pattern for console logging.
- `${FILE_KNOTX_LOG_PATTERN}`, `${FILE_ACCESS_LOG_PATTERN}`, `${FILE_NETTY_LOG_PATTERN}` - Allows to override a default log pattern for knotx, access & netty logs.
- `${KNOTX_LOG_FILE_MAX_SIZE}`, `${ACCESS_LOG_FILE_MAX_SIZE}`, `${NETTY_LOG_FILE_MAX_SIZE}` - Allows to define a maximum size of knotx, access & netty log files. If not specified, a max size is `10MB`
- `${KNOTX_LOG_FILE_MAX_HISTORY}`, `${ACCESS_LOG_FILE_MAX_HISTORY}`, `${NETTY_LOG_FILE_MAX_HISTORY}` - Allows to define a maximum amount of archived log files kept in the log folder (for knotx, access & netty logs). If not specified, it keeps files forever.

See the [Knot.x core logback settings](https://github.com/Cognifide/knotx/blob/master/knotx-core/src/main/resources/io/knotx/logging/logback/) configuration files for details.

## Configure logback for file only output
In a production system, you want to disable console logging and write output only to a file both for knotx & access logs. 
You need to create a custom `logback.xml` that imports `file-appender.xml` but not `console-appender.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="io/knotx/logging/logback/defaults.xml" />
    <include resource="io/knotx/logging/logback/file-appender.xml" />
    <include resource="io/knotx/logging/logback/access.xml" />
    
    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

**NOTE: Do not forgot to specify usage of your custom logback file through `-Dlogback.configurationFile` system property.**

Additionally, you want to specify a location of log files in your file system. And roll over policy to keep last 30 archived log files.
You can do this, through your custom logback file:
```xml
<configuration>
  <property name="LOG_PATH" value="/path/to/logs"/>
  <property name="KNOTX_LOG_FILE_MAX_HISTORY" value="30"/>
  <property name="ACCESS_LOG_FILE_MAX_HISTORY" value="30"/>
  
  <include resource="io/knotx/logging/logback/defaults.xml" />
  ...
</configuration>
```
Or, you can provide those settings through System properties:
```
-DLOG_PATH=/path/to/logs -DKNOTX_LOG_FILE_MAX_HISTORY=30 -DACCESS_LOG_FILE_MAX_HISTORY=30
```
Or, even you can mix both approaches. So define default settings inside logback, and configure to respect new System properties you can use to override
```xml
<configuration>
  <property name="LOG_PATH" value="${my.logs:-/path/to/logs}"/>
  <property name="KNOTX_LOG_FILE_MAX_HISTORY" value="${my.logs.history:-30}"/>
  <property name="ACCESS_LOG_FILE_MAX_HISTORY" value="${my.logs.history:-30}"/>
  
  <include resource="io/knotx/logging/logback/defaults.xml" />
  ...
</configuration>
```
And your System property, that will set different log path, and max history for both log files to `100`:
```
-Dmy.logs=/other/path -Dmy.logs.history=100
```
As you can see, Logback logger brings a tremendous amount of possibilities how to configure your logs. 
It's impossible to present here all the possibilities, so the best way would be to study [Logback Documentation](https://logback.qos.ch/manual/index.html). 

## Log network activity
Network activity (logged by Netty) logger settings are provided by the Knot.x core. In order to log it, you need to use your custom logback.xml file and configure it as follows
```xml
<configuration>
    <!-- Your properties is required -->
    
    <!-- Knotx & access logs -->
    <include resource="io/knotx/logging/logback/defaults.xml" />
    <include resource="io/knotx/logging/logback/file-appender.xml" />
    <include resource="io/knotx/logging/logback/access.xml" />
 
    <!-- Add netty network activity logger -->
    <include resource="io/knotx/logging/logback/netty.xml" />
    
    <!-- other logger settings -->
</configuration>
```
Additionally, in your knot.x json configuration file, you need to enable logging network activity. You can enable this both for HTTP server as well as HTTP clients used.
- To enable it for server, just overwrite KnotxServer configuration:
```json
"config": {
   "serverOptions": {
      "logActivity": true
   }
}
```
- To enable it for HTTP Clients, just overwrite any or all service adapter configurations:
```json
"config": {
   "clientOptions": {
      "logActivity": true
   }
}
```

## Configure logback to log my specific package
If you added your own Knot's, Adapters or any other extension to the Knot.x you want to have this information to be logged in your log files.
```xml
<configuration>
    <!-- Your properties is required -->
    
    <!-- Knotx & access logs -->
    <include resource="io/knotx/logging/logback/defaults.xml" />
    <include resource="io/knotx/logging/logback/file-appender.xml" />
    <include resource="io/knotx/logging/logback/access.xml" />
    
    <!-- other logger settings -->
    
    <!-- project specific logger -->
    <logger name="com.example.extension" level="INFO">
      <appender-ref ref="FILE" />
    </logger>
</configuration>
```
- Add `logger` for your package or class, define a level of logs
- Specify `FILE` appender as a logs target.
Your logs will appear in the `knotx.log`

However, you might wanted to log your package logs into a separate file, to not polute `knotx.log`.
- create your own file appender (see `io/knotx/logging/logback/file-appender.xml` as an example) with the name e.g. `MY_FILE`
- bind your logger to `MY_FILE` appender
- set logger to `additivity="false"`, so your logs will go just to your new file. If specified to `true` logs will go to the parent logger into `knotx.log` files too.

```xml
<appender name="MY_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
  <!-- Appender settings -->
</appender>

<!-- project specific logger -->
<logger name="com.example.extension" level="INFO" additivity="false">
  <appender-ref ref="MY_FILE" />
</logger>
```
