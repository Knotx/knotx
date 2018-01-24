# Logging
By default Knot.x picks up the logger configuration from its default location for the system (e.g. classpath:logback.xml), but you can set the location of the config file through `logback.configurationFile` system property.
```
java -Dlogback.configurationFile=/path/to/logback.xml -jar ...
```
- ### TBD: what types of logs availble (knotx, access, netty)
- ### TBD: how it logs by default

## Configure Logback
Knot.x provides a default base configuration that you can include if you just want to set levels, e.g.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration scan="true">
    <include resource="io/knotx/logging/logback/base.xml"/>
    <logger name="io.knotx" level="TRACE"/>
</configuration>
```
The `base.xml` file uses useful System properties which the Logback takes care of creating for you. These are:
- `${LOG_PATH}` - represents a directory for log files to live in).
- #### TBD ######

See the [default `base.xml`](https://github.com/Cognifide/knotx/blob/master/knotx-core/src/main/resources/io/knotx/logging/logback/base.xml) configuration for details.

## Configure logback for file only output
If you want to disable console logging and write output only to a file you need a custom logback.xml that imports file-appender.xml but not console-appender.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="io/knotx/logging/logback/defaults.xml" />
    <include resource="io/knotx/logging/logback/file-appender.xml" />
    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

You also need to set `LOG_PATH` System property to point one where the file will be logged. As an alternative you can set that property inside your logback.xml
```xml
<configuration>
  <property name="LOG_PATH" value="/path/to/logs"/>
  <include resource="io/knotx/logging/logback/defaults.xml" />
  ...
</configuration>
```

## Configure logback to log into multiple files

## Extras
Logback logger brings a tremendous amount of possibilities how to store your logs. It's impossible to present here all possibilities, so the best way would be a [Logback Documentation](https://logback.qos.ch/manual/index.html). 
