# Performance Tests Methodology

## Introduction
This page describes the methodology of Knot.x performance tests.

## Goals
The goals of the tests conducted are as follows:
* Assess system performance.
* Assess system stability.

### Metrics
During our tests, we focus on the following metrics which, when correlated, allow us to come up with number of observations about the system:
* Throughput.
* Response time - 90% Line.
* Quantity of Virtual Users.
* Number of errors (HTTP response codes > 400, Knot.x logs).
* Infrastructure resources (cpu, memory, disk, network).
* Application resources (jmx heap size, garbage collections).

### KPIs
* 1 second for response time (90% Line).
* Error occurrences.
* Stability:
    * No memory leaks.
    * No errors (logs, responses).
    * Infrastructure resources OK.
    * Application resources OK.
	
## Test Environment
Test Environment is set up on AWS Cloud.
![Test environment](assets/knotx-test-environment.png)

### System under test
* 1 x Apache HTTP Server - c4.large, 2CPU, 4GB RAM, 500Mbps bandwidth,centos 6.8.
* 1 x Knot.x - c4.large, 2CPU, 4GB RAM, 500Mbps bandwidth,centos 6.8.
* 1 x Repository & Services Mocks - c4.large, 2CPU, 4GB RAM, 500Mbps bandwidth,centos 6.8.


### Knotx author start command
`java -Dlogback.configurationFile=/content/knotx/author/logback.xml -Dvertx.cacheDirBase=/content/knotx/author/.vertx -Xms1024m -Xmx2048m -XX:ReservedCodeCacheSize=128m -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=18092 -Dcom.sun.management.jmxremote.rmi.port=18092 -Djava.rmi.server.hostname=10.254.165.236 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Xloggc:/content/logs/knotx/gc.log -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCApplicationConcurrentTime -XX:+PrintGC -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=5M -jar /content/knotx/author/knotx.jar -conf /content/knotx/author/config/author-config.json`

### Knotx publish start command
`java -Dlogback.configurationFile=/content/knotx/publish/logback.xml -Dvertx.cacheDirBase=/content/knotx/publish/.vertx -Xms1024m -Xmx2048m -XX:ReservedCodeCacheSize=128m -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=18093 -Dcom.sun.management.jmxremote.rmi.port=18093 -Djava.rmi.server.hostname=10.254.165.236 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Xloggc:/content/logs/knotx/gc.log -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCApplicationConcurrentTime -XX:+PrintGC -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=5M -jar /content/knotx/publish/knotx.jar -conf /content/knotx/publish/config/publish-config.json`

### Knotx mocks start command
`java -Dlogback.configurationFile=/content/knotx/mocks/logback.xml -Dvertx.cacheDirBase=/content/knotx/mocks/.vertx -Xms1024m -Xmx2048m -XX:ReservedCodeCacheSize=128m -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=19000 -Dcom.sun.management.jmxremote.rmi.port=19000 -Djava.rmi.server.hostname=10.254.165.163 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Xloggc:/content/logs/knotx/gc.log -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCApplicationConcurrentTime -XX:+PrintGC -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=5M -jar /content/knotx/mocks/knotx.jar -conf /content/knotx/mocks/config/mocks-config.json`

### Test harness
* 1 x Jmeter Master - c4.xlarge, 4CPU, 8GB RAM, 750Mbps bandwidth,centos 6.8.
* 2 x Jmeter Servers - c4.xlarge, 4CPU, 8GB RAM, 750Mbps bandwidth,centos 6.8.

### Repositories
* mocks-knotx - [knotx-performance-tests\config-knotx](https://github.com/Cognifide/knotx/tree/master/knotx-performance-tests/config-knotx)
* config-knotx - [knotx-performance-tests\mocks-knotx](https://github.com/Cognifide/knotx/tree/master/knotx-performance-tests/mocks-knotx)

## Tools
* JMeter 3.1 with plugins (at AWS) as a Load Generator.
* Zabbix 3.2.1 (Internal instance at Cognifide Office) as an Server Monitoring.
* Influx (Internal Instance at AWS set up on JMeter Master machine) as a Live Test Monitoring database.
* Grafana 3.0.1 (Internal Instance at Cognifide Office) as a Live Test Monitoring tool.

## Test Process
Test process that we used is based on Microsoft [Microsoft Performance testing process](https://msdn.microsoft.com/en-us/library/bb924376.aspx).

## Test Scripts
* Test script implemented in JMeter 3.1.
* Think time 4-6 seconds (Gaussian Random Timer).
* No embedded resources are downloaded, just HTML.
* Assertions for each requests to verify correct data is returned (Response Assertion).
* Test scripts are tested.
* Load ramp-up for 5400 (1.5 hour)  seconds, then sustain load for another 1 hour.

## Execution
* Jmeter run in Distribute Mode (Remote).
* Restart all servers before tests (Apache HTTP Server, Knot.x, Mocks, JMeter machines).
* Verify no network traffic on servers (via Zabbix).
* Verify no CPU Utilization on Servers (via Zabbix).
* Open tested page in browser - open and data OK.
* Warm-up for 5 minutes - no errors.
* Wait for cool down.
* Execute tests.

## Logging/Monitoring
* .csv Simple Data Writer.
* Backend Listener for Influx database, summary only.
* Live monitoring in Grafana (throughput, response times, errors, vitrual users).
* Zabbix (Knot.x, mocks, apache, jmeter machines).

## Test results and artefacts collection
* Jmeter log from all JMeter machines.
* Knot.x logs.
* JMeter tests results.
* Zabbix charts.
* Grafana Charts.
* All test executions are documented with Test Reports.

## Retests
* After facing an issue we investigate it and tweak/fix.
* After fix we retest with the same configuration/test/environment to verify it.

## Terminology
* [JMeter Glossary](https://jmeter.apache.org/usermanual/glossary.html)
* [Performance Testing Guidance for Web Applications](https://msdn.microsoft.com/en-us/library/bb924356.aspx)
* [Dictionary of Load Testing Terms](http://www.webperformance.com/library/dictionary.html)





