# Performance Tests

##Architecture
[[assets/performance/tests_architecture.png|alt=Tests Architecture]]


##Servers specification
* JMeter master: Intel(R) Xeon(R) CPU E5-2666 v3 @ 2.90GHz, 8GB RAM, jdk 1.8.0_66
* JMeter slave1 / JMeter slave2: Intel(R) Xeon(R) CPU E5-2666 v3 @ 2.90GHz, 8GB RAM, jdk 1.8.0_66
* Knot.x: Intel(R) Xeon(R) CPU E5-2666 v3 @ 2.90GHz, 8GB RAM, jdk 1.8.0_66
* Mocked Service and Template Repository: Intel(R) Xeon(R) CPU E5-2666 v3 @ 2.90GHz, 4GB RAM, jdk 1.8.0_66

##Test scenarios and results
Each test scenario on 60 threads x 2 slave -> 120 threads 

### Fifty snippets and two service calls
Called page contains 50 snippets. Each snippet requires data from 2 different services.

#### Response times over time chart
[[assets/performance/50_snippets_two_calls.png|alt=50 snippets 2 calls]]

### Four snippets and five service calls
Called page contains 4 snippets. Each snippet requires data from 5 different services.

#### Response times over time chart
[[assets/performance/4snippets_5calls.png|alt=50 snippets 2 calls]]

### One snippet and ten service calls
Called page contains 1 snippet. Snippet requires data from 10 different services.

#### Response times over time chart
[[assets/performance/1_snippet_10_service_calls.png|alt=1 snippet 10 services calls]]

### One Snippet and twenty services calls
Called page contains one snippet which contains 20 services calls.

#### Response times over time chart
[[assets/performance/20_snippets_one_call.png|alt=One snippet 20 calls]]

### Snippet with two forms
Called page contains one snippet. Snippet consists of two forms.

#### Response times over time chart
[[assets/performance/multiple_forms_memory_fix.png|alt=Multiple Forms]]
