# Performance

- [What do we measure - KPIs](#what-do-we-measure---kpis)
  - [KPI #1 - 1 second response time (90% line)](#kpi-1---1-second-response-time-90%25-line)
  - [KPI #2 - No error responses](#kpi-2---no-error-responses)
  - [KPI #3 - stability](#kpi-3---stability)
  - [KPI #4 - 1 hour of 1 second response time (90% line)](#kpi-4---1-hour-of-1-second-response-time-90%25-line)
- [Performance infrastructure and setup](#performance-infrastructure-and-setup)
  - [Knot.x instance VM and GC settings](#knotx-instance-vm-and-gc-settings)
- [Tools](#tools)
- [Performance tests](#performance-tests)
  - [Scenarios](#scenarios)
    - [1. One snippet and one service](#1-one-snippet-and-one-service)
      - [Details](#details)
    - [2. One snippet and five services](#2-one-snippet-and-five-services)
      - [Details](#details-1)
    - [3. Five snippets one service each](#3-five-snippets-one-service-each)
      - [Details](#details-2)
    - [4. Heavy template with one snippet one services](#4-heavy-template-with-one-snippet-one-services)
      - [Details](#details-3)
    - [5. Heavy template with 100 snippets and one heavy service](#5-heavy-template-with-100-snippets-and-one-heavy-service)
      - [Details](#details-4)
    - [6. Heavy template with one big snippet and one heavy service](#6-heavy-template-with-one-big-snippet-and-one-heavy-service)
      - [Details](#details-5)
  - [Results](#results)
  - [Observations](#observations)
- [Soak test](#soak-test)
  - [Results](#results-1)
    - [Users/throughput](#usersthroughput)
    - [CPU utilization](#cpu-utilization)
    - [CPU load](#cpu-load)
    - [JVM memory heap space](#jvm-memory-heap-space)
    - [GC collections per second](#gc-collections-per-second)
    - [Network traffic](#network-traffic)
    - [Event bus usage](#event-bus-usage)
  - [Observations](#observations-1)
- [Terminology](#terminology)

## What do we measure - KPIs

### KPI #1 - 1 second response time (90% line)
This KPI will be fulfilled as long as 90% of the responses will be returned within less than 1 second. The measured result is the peak when KPI was met (when 90% requests line exceeded 1 second).
This KPI shows what traffic peaks that the instance can process with the reasonable response time.

### KPI #2 - No error responses
This KPI will be fulfilled as long as there will be no error responses.
This KPI shows what throughput can the instance process without errors.

### KPI #3 - stability
- No memory leaks.
- No errors (logs, responses).
- Infrastructure resources OK.
- Application resources OK.

This KPI shows how much traffic can the instance outlive without crashing.

### KPI #4 - 1 hour of 1 second response time (90% line)
This KPI will be fulfilled as long as 90% of the responses will be returned within less than 1 second for 60 minutes with constant throughput with no error responses and system is stable.
This KPI is measured within 70 min session starting with 10 min rampup.

This KPI shows how does instance behaves with high traffic that lasts for a longer period.

## Performance infrastructure and setup
[[assets/performance-setup.png|alt=Performance infrastructure and setup]]

All servers are running under CentOS 7.

### Knot.x instance VM and GC settings

```
-Xms1024m
-Xmx2048m
-XX:ReservedCodeCacheSize=128m
-XX:+UseBiasedLocking
-XX:BiasedLockingStartupDelay=0
-XX:+UseConcMarkSweepGC
-XX:ParallelGCThreads=2
-XX:ParallelCMSThreads=1
```

## Tools
* JMeter 3.1 with plugins (at AWS) as a Load Generator.
* Zabbix 3.2.1 as an Server Monitoring.
* Influx (Internal Instance at AWS set up on JMeter Master machine) as a Live Test Monitoring database.
* Grafana 3.0.1 as a Live Test Monitoring tool.
* DropWizard metrics monitoring Knot.x module.

## Performance tests
The goal of performance tests is to check the system performance and stability with KPIs defined above.
During our tests, we focus on the following metrics which, when correlated, allow us to come up with number of observations about the system:
- Throughput.
- Response time - 90% Line.
- Quantity of Virtual Users.
- Number of errors (HTTP response codes > 400, Knot.x logs).
- Infrastructure resources (cpu, memory, disk, network).
- Application resources (jmx heap size, garbage collections).

All performance and soak scenarios were executed with default number of instances for each Knot.x Verticle:

| Knot.x module | No. of instances |
| ------------- | ---------------- |
| HttpRepositoryConnector | 1 |
| FragmentSplitter | 1 |
| FragmentAssembler | 1 |
| ServiceKnot | 1 |
| HttpServiceAdapter | 1 |
| HandlebarsKnot | 1 |
| KnotxServer | 1 |

### Scenarios

#### 1. One snippet and one service
The page is quite small, it contains only one dynamic Knot.x snippet that requires 1 data source integration.

##### Details
- page:  [simple-1snippet-1service.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-1snippet-1service.html)
- raw template size: `3.2 Kb`
- service response (json) size: `750 byte`

#### 2. One snippet and five services
The page is still small, it contains only one dynamic Knot.x snippet but it requires 5 different data source integration.

##### Details
- page:  [simple-1snippet-5services.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-1snippet-5services.html)
- raw template size: `4.7 Kb`
- service response (json) size: `750\191\212\279\174 byte`

#### 3. Five snippets one service each
The page is variation of a previous one. It is still small, it contains five dynamic Knot.x snippets and each of those snippets uses separate data source integration.

##### Details
- page:  [simple-5snippets.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-5snippets.html)
- raw template size: `5.3 Kb`
- service response (json) size: `750\191\212\279\174 byte`

#### 4. Heavy template with one snippet one services
The page contains big blocks of content and is heavy. There is no much work for integration here (only one snippet with single data soruce), however the challenge here is to process (split and then assemble) a big chunk of html code.

##### Details
- page:  [simple-big-data.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-big-data.html)
- raw template size: `116 Kb`
- service response (json) size: `234 byte`

#### 5. Heavy template with 100 snippets and one heavy service
The page contains 100 snippets, each of them uses the same service. However, the service response is heavy. The challenge here is to process (split and then assemble) a big chunk of html code with multiple snippets and additionaly pass the payload of service response (heavy json) to templating engine Knot.

##### Details
- page:  [100-small-snippets-1-service-wtih-big-json.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/100-small-snippets-1-service-wtih-big-json.html)
- raw template size: `54 Kb`
- service response (json) size: `111 Kb`

#### 6. Heavy template with one big snippet and one heavy service
This test is variation of previous one. All 100 snippets were merged into one big snippet. The service response is heavy. The challenge here is to process (split and then assemble) a big chunk of html code with one heavy snippet and additionaly pass the payload of service response (heavy json) to templating engine Knot.

##### Details
- page:  [1-big-snippet-1-service-wtih-big-json.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/1-big-snippet-1-service-wtih-big-json.html)
- raw template size: `34 Kb`
- service response (json) size: `111 Kb`

### Results

| Scenario | KPI #1 | KPI #2 | KPI #3 | KPI #4 |
| -------- | ------ | ------ | ------ | ------- |
| [1. One snippet and one service](#1-one-snippet-and-one-service) | <ul><li>Throughput: `2600 req/sec`</li></ul> | <ul><li>Throughput: `4000 req/sec`</li><li>90% line avg: `3.65 s`</li><li>Virtual users: `5200`</li></ul> | OK | <ul><li>Throughput: `1810 req/sec`</li><li>90% line avg: `385 ms`</li><li>avg CPU user time: `77.32%`</li><li>avg CPU system time: `5.26%`</li><li>avg CPU load: `157%`</li></ul> |
| [2. One snippet and five services](#2-one-snippet-and-five-services) | <ul><li>Throughput: `1400 req/sec`</li></ul> | <ul><li>Throughput: `2200 req/sec`</li><li>90% line avg: `3.67 s`</li><li>Virtual users: `3000`</li></ul> | OK | <ul><li>Throughput: `1109 req/sec`</li><li>90% line avg: `343 ms`</li><li>avg CPU user time: `75.16%`</li><li>avg CPU system time: `5.68%`</li><li>avg CPU load: `152%`</li></ul> |
| [3. Five snippets one service each](#3-five-snippets-one-service-each) | <ul><li>Throughput: `1000 req/sec`</li></ul> | <ul><li>Throughput: `2000 req/sec`</li><li>90% line avg: `18.8 s`</li><li>Virtual users: `8000`</li></ul> | OK | <ul><li>Throughput: `850 req/sec`</li><li>90% line avg: `65 ms`</li><li>avg CPU user time: `66.49%`</li><li>avg CPU system time: `5.38%`</li><li>avg CPU load: `122%`</li></ul> |
| [4. Heavy template with one snippet one services](#4-heavy-template-with-one-snippet-one-services) | <ul><li>Throughput: `400 req/sec`</li></ul> | <ul><li>Throughput: `317 req/sec`</li><li>90% line avg: `21.24 s`</li><li>Virtual users: `4600`</li></ul> | OK | <ul><li>Throughput: `190 req/sec`</li><li>90% line avg: `50 ms`</li><li>avg CPU user time: `50.11%`</li><li>avg CPU system time: `1.68%`</li><li>avg CPU load: `62%`</li></ul> |
| [5. Heavy template with 100 snippets and one heavy service](#5-heavy-template-with-100-snippets-and-one-heavy-service) | <ul><li>Throughput: `10 req/sec`</li></ul> | <ul><li>Throughput: `100 req/sec`</li><li>90% line avg: `42 s`</li><li>Virtual users: `112`</li></ul> | Crashed at `422` VU - `OOM`. <br>Heap was full (`2GB` of `2GB` used) at this time. <br>Knot.x didn't recover within 15 min.  | <ul><li>Throughput: `100 req/sec`</li><li>90% line avg: `850 ms`</li><li>avg CPU user time: `52.1%`</li><li>avg CPU system time: `0.2%`</li><li>avg CPU load: `65%`</li></ul> |
| [6. Heavy template with one big snippet and one heavy service](#6-heavy-template-with-one-big-snippet-and-one-heavy-service) | <ul><li>Throughput: `200 req/sec`</ul> | <ul><li>Throughput: `200 req/sec`</li><li>90% line avg: `59.4 s`</li><li>Virtual users: `1100`</li></ul> | Crashed at `1164` VU - `OOM`. <br>Heap was full (`2GB` of `2GB` used) at this time. <br>Knot.x recovered. | <ul><li>Throughput: `125 req/sec`</li><li>90% line avg: `243 ms`</li><li>avg CPU user time: `59.75%`</li><li>avg CPU system time: `0.71%`</li><li>avg CPU load: `65%`</li></ul> |

### Observations
- It is not worth to lead to the situation when Knot.x uses 100% of all CPUs. In that case throughput is up to 40% lower than during the more balanced test, when CPU usage is about 75%.
- In scenarios (5) and (6) the same page is displayed, however using different number of snippets (100 small vs 1 big). The approach with 100 small snippets has better performance characteristics (the reason here is templating engine overload with big snippet to parse).

## Soak test
Motivation for this test is to check how Knot.x instance behaves in realistic scenario. Soak test is planned to last 24 hours and is a
combination of requests to pages from scenarios above in occurrence defined below:
- 40% [simple-1snippet-1service.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-1snippet-1service.html)
- 25% [simple-1snippet-5services.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-1snippet-5services.html)
- 25% [simple-5snippets.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-5snippets.html)
- 5% [simple-big-data.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/simple-big-data.html)
- 5% [1-big-snippet-1-service-wtih-big-json.html](https://github.com/Cognifide/knotx/blob/master/knotx-performance-tests/mocks-knotx/content/1-big-snippet-1-service-wtih-big-json.html)

Traffic throughput plan (notice that since we have 2 jMeter slaves, the final number of users is counted twice):
[[assets/soak-traffic-plan.png|alt=Soak traffic plan]]

### Results

| Scenario | Results | Remarks |
| -------- | ------ | ------ |
| 24h soak test | <ul><li>Throughput: `168 req/sec`</li><li>Peak: `400 req/sec`</li><li>Average response in: `16 ms`</li><li>90% line avg: `49 ms`</li><li>99% line avg: `89 ms`</li><li>avg CPU load: `21.3 %`</li><li>`1` error within `7.31 milion` requests</li></ul> | Error was a single `404`. |

#### Users/throughput
[[assets/soak-users-throughput.png|alt=Users/throughput]]

#### CPU utilization
[[assets/soak-cpu-utilization.png|alt=CPU utilization]]

#### CPU load
[[assets/soak-cpu-load.png|alt=CPU load]]

#### JVM memory heap space
[[assets/soak-jvm-heap-space.png|alt=JVM memory heap space]]

#### GC collections per second
[[assets/soak-gc-per-second.png|alt=GC collections per second]]

#### Network traffic
[[assets/soak-network-traffic.png|alt=Network traffic]]

#### Event bus usage
[[assets/soak-eb-usage.png|alt=Event bus usage]]

### Observations
- After the highest peak, heap didn't behave in the same way as before the peak. It is bigger and cleared less frequently. However, it looks that there is no influence on performance or throughput.

## Terminology
* [JMeter Glossary](https://jmeter.apache.org/usermanual/glossary.html)
* [Performance Testing Guidance for Web Applications](https://msdn.microsoft.com/en-us/library/bb924356.aspx)
* [Dictionary of Load Testing Terms](http://www.webperformance.com/library/dictionary.html)
