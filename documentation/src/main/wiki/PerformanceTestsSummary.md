# Performance Tests Summary

## Introduction
This page gathers all tests summaries, recommendations and actions undertaken.

## Overview
* Throughput of Knot.x varies from **223 requests/sec** up to **840 requests/second** for different scenarios for **1 second response time**.
* Knot.x is stable.
* Knot.x recovers after crashes.
* [[Several fixes applied to Knot.x as well as configuration tweaks both for Knot.x and Apache HTTP Server|PerformanceTestsTuning]]

## Tests Summary
![Test summary](assets/knotx-perf-tests-summary.png)

<table>
  <tbody>
  <tr><th>#</th><td colspan="1">1</td><td colspan="1">2</td><td colspan="1">3</td><td colspan="1">4</td><td colspan="1">5</td></tr><tr>
  <th>Scenario</th>
  <td colspan="1">
        <span style="color: rgb(0,0,0);">1-snippet-and-1-service</span>
      </td><td colspan="1">
        <span style="color: rgb(0,0,0);">1-snippet-and-5-services</span>
      </td><td colspan="1">
        <span style="color: rgb(0,0,0);">5-snippets-and-1-service-for-each-snippet</span>
      </td><td colspan="1">
        <span style="color: rgb(0,0,0);">big-data</span>
      </td><td colspan="1">Soak Test</td></tr><tr>
      <th>Scenario Details</th>
      <td colspan="1">
        <ul>
          <li>Virtual Users: 5000 (2 x 2500 VU)</li>
          <li>Ramp-up: 5400,</li>
          <li>2.5h test duration,</li>
        </ul>
      </td><td colspan="1">
        <ul>
          <li>Virtual users: 5000 (2 x 2500 VU)</li>
          <li>Ramp-up: 5400</li>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">Knotx 1.0.0-RC4</span>
            </span>
          </li>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">2.5h test duration,</span>
            </span>
          </li>
        </ul>
      </td><td colspan="1">
        <ul>
          <li>Virtual users: 5000 (2 x 2500 VU)</li>
          <li>Ramp-up: 5400</li>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">Knotx 1.0.0-RC4</span>
            </span>
          </li>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">2.5h test duration,</span>
            </span>
          </li>
        </ul>
      </td><td colspan="1">
        <ul>
          <li>Virtual Users: 5000 (2 x 2500 VU)</li>
          <li>Ramp-up: 5400</li>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">1.0.0-RC8</span>
            </span>
          </li>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">2.5h test duration,</span>
            </span>
          </li>
        </ul>
      </td><td colspan="1">
        <ul>
          <li>
            <p>Threads: 1000 (2 x 500 VU)</p>
          </li>
          <li>
            <p>Ramp-up: 10800</p>
          </li>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">1.0.0-RC8</span>
            </span>
          </li>
          <li>
            <p>2 days test duration,</p>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">&nbsp;</span>
            </span>
          </li>
        </ul>
      </td></tr><tr>
      <th>KPI 1 - 1 second 90% Line response time</th>
      <td colspan="1">
        <ul>
          <li>
            <span>Throughput 840 req/second,</span>
          </li>
          <li>
            <span>Virtual Users: 4288,</span>
          </li>
          <li>
            <span>
              <span>
                <span>no errors,</span>
              </span>
            </span>
          </li>
        </ul>
      </td><td colspan="1">
        <ul>
          <li>Throughput: 400 req/sec,</li>
          <li>Virtual Users: 2000,</li>
          <li>no errors,</li>
        </ul>
      </td><td colspan="1">
        <ul>
          <li>Throughput: 345 req/sec,</li>
          <li>Virtual Users: 1748,</li>
          <li>no errors,</li>
        </ul>
      </td><td colspan="1">
        <ul>
          <li>Throughput: 223 req/second,</li>
          <li>Virtual users: 1154,</li>
        </ul>
      </td><td colspan="1">
        <ul>
          <li>Throughput: 242 req/second,</li>
          <li>Virtual users: 1000,</li>
        </ul>
      </td></tr><tr>
      <th>KPI 2 - No errors</th>
      <td colspan="1">
        <ul>
          <li>
            <span>Throughput <span> 800 req/second,<br>
              </span>
            </span>
          </li>
          <li>
            <span>
              <span>Virtual Users: 5000,</span>
            </span>
          </li>
          <li>
            <span>
              <span>no errors,<br>
              </span>
            </span>
          </li>
        </ul>
      </td><td colspan="1">
        <ul>
          <li>Throughput: 400 req/sec,</li>
          <li>Virtual Users: 5000,</li>
          <li>no errors,</li>
        </ul>
      </td><td colspan="1">
        <ul>
          <li>Throughput: 330req/sec,</li>
          <li>Virtual Users: 5000,</li>
          <li>no errors,</li>
        </ul>
      </td><td colspan="1">
        <ul>
          <li>Throughput: 220 req/second,</li>
          <li>Virtual users: 4800,</li>
        </ul>
        <p>(crash)</p>
      </td><td colspan="1">
        <ul>
          <li>Throughput: 242 req/second,</li>
          <li>Virtual users: 1000,</li>
        </ul>
      </td></tr><tr>
      <th>KPI - Stability</th>
      <td colspan="1">OK</td><td colspan="1">OK</td><td colspan="1">OK</td><td colspan="1">
        <ul>
          <li>Crashed and recovered</li>
        </ul>
        <p>application crashed at throughput: 200 req/sec, 4 800 Virtual Users,</p>
      </td><td colspan="1">
        <ul>
          <li>OK</li>
          <li>14 errors within 42 million requests,</li>
          <li>First error occurred after 7h of tests,</li>
        </ul>
      </td></tr><tr>
      <th>Remarks</th>
      <td colspan="1">
        <ul>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">Tweak Apache HTTP Server settings and retest,</span>
            </span>
          </li>
        </ul>
      </td><td colspan="1">
        <ul>
          <li>Investigate system crash at the end of tests,</li>
          <li>increase count of open descriptors 4096 -&gt; 32K for Apache HTTP Server and KnotX</li>
          <li>increase KnotX heap size from 1GB to 2GB,</li>
        </ul>
      </td><td colspan="1">&nbsp;</td><td colspan="1">
        <ul>
          <li>OOM issue fixed,</li>
          <li>We reached Knotx limit in this scenario.</li>
        </ul>
      </td><td colspan="1">
        <ul>
          <li>Response times and disk writes correlation issue to be investigated,</li>
          <li>errors to be investigated,</li>
        </ul>
      </td></tr></tbody>
</table>

<table>
  <tbody>
    <tr>
      <th>#</th>
      <th>Scenario</th>
      <th>Scenario Details</th>
      <th>KPI 1 - 1 second 90% Line response time</th>
      <th colspan="1">KPI 2 - No errors</th>
      <th colspan="1">KPI - Stability</th>
      <th>Remarks</th>
    </tr>
    <tr>
      <td colspan="1">1</td>
      <td colspan="1">
        <span style="color: rgb(0,0,0);">1-snippet-and-1-service</span>
      </td>
      <td colspan="1">
        <ul>
          <li>Virtual Users: 5000 (2 x 2500 VU)</li>
          <li>Ramp-up: 5400,</li>
          <li>2.5h test duration,</li>
        </ul>
      </td>
      <td colspan="1">
        <ul>
          <li>
            <span>Throughput 840 req/second,</span>
          </li>
          <li>
            <span>Virtual Users: 4288,</span>
          </li>
          <li>
            <span>
              <span>
                <span>no errors,</span>
              </span>
            </span>
          </li>
        </ul>
      </td>
      <td colspan="1">
        <ul>
          <li>
            <span>Throughput <span> 800 req/second,<br/>
              </span>
            </span>
          </li>
          <li>
            <span>
              <span>Virtual Users: 5000,</span>
            </span>
          </li>
          <li>
            <span>
              <span>no errors,<br/>
              </span>
            </span>
          </li>
        </ul>
      </td>
      <td colspan="1">OK</td>
      <td colspan="1">
        <ul>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">Tweak Apache HTTP Server settings and retest,</span>
            </span>
          </li>
        </ul>
      </td>
    </tr>
    <tr>
      <td colspan="1">2</td>
      <td colspan="1">
        <span style="color: rgb(0,0,0);">1-snippet-and-5-services</span>
      </td>
      <td colspan="1">
        <ul>
          <li>Virtual users: 5000 (2 x 2500 VU)</li>
          <li>Ramp-up: 5400</li>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">Knotx 1.0.0-RC4</span>
            </span>
          </li>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">2.5h test duration,</span>
            </span>
          </li>
        </ul>
      </td>
      <td colspan="1">
        <ul>
          <li>Throughput: 400 req/sec,</li>
          <li>Virtual Users: 2000,</li>
          <li>no errors,</li>
        </ul>
      </td>
      <td colspan="1">
        <ul>
          <li>Throughput: 400 req/sec,</li>
          <li>Virtual Users: 5000,</li>
          <li>no errors,</li>
        </ul>
      </td>
      <td colspan="1">OK</td>
      <td colspan="1">
        <ul>
          <li>Investigate system crash at the end of tests,</li>
          <li>increase count of open descriptors 4096 -&gt; 32K for Apache HTTP Server and KnotX</li>
          <li>increase KnotX heap size from 1GB to 2GB,</li>
        </ul>
      </td>
    </tr>
    <tr>
      <td colspan="1">3</td>
      <td colspan="1">
        <span style="color: rgb(0,0,0);">5-snippets-and-1-service-for-each-snippet</span>
      </td>
      <td colspan="1">
        <ul>
          <li>Virtual users: 5000 (2 x 2500 VU)</li>
          <li>Ramp-up: 5400</li>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">Knotx 1.0.0-RC4</span>
            </span>
          </li>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">2.5h test duration,</span>
            </span>
          </li>
        </ul>
      </td>
      <td colspan="1">
        <ul>
          <li>Throughput: 345 req/sec,</li>
          <li>Virtual Users: 1748,</li>
          <li>no errors,</li>
        </ul>
      </td>
      <td colspan="1">
        <ul>
          <li>Throughput: 330req/sec,</li>
          <li>Virtual Users: 5000,</li>
          <li>no errors,</li>
        </ul>
      </td>
      <td colspan="1">OK</td>
      <td colspan="1"> </td>
    </tr>
    <tr>
      <td colspan="1">4</td>
      <td colspan="1">
        <span style="color: rgb(0,0,0);">big-data</span>
      </td>
      <td colspan="1">
        <ul>
          <li>Virtual Users: 5000 (2 x 2500 VU)</li>
          <li>Ramp-up: 5400</li>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">1.0.0-RC8</span>
            </span>
          </li>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">2.5h test duration,</span>
            </span>
          </li>
        </ul>
      </td>
      <td colspan="1">
        <ul>
          <li>Throughput: 223 req/second,</li>
          <li>Virtual users: 1154,</li>
        </ul>
      </td>
      <td colspan="1">
        <ul>
          <li>Throughput: 220 req/second,</li>
          <li>Virtual users: 4800,</li>
        </ul>
        <p>(crash)</p>
      </td>
      <td colspan="1">
        <ul>
          <li>Crashed and recovered</li>
        </ul>
        <p>application crashed at throughput: 200 req/sec, 4 800 Virtual Users,</p>
      </td>
      <td colspan="1">
        <ul>
          <li>OOM issue fixed,</li>
          <li>We reached Knotx limit in this scenario.</li>
        </ul>
      </td>
    </tr>
    <tr>
      <td colspan="1">5</td>
      <td colspan="1">Soak Test</td>
      <td colspan="1">
        <ul>
          <li>
            <p>Threads: 1000 (2 x 500 VU)</p>
          </li>
          <li>
            <p>Ramp-up: 10800</p>
          </li>
          <li>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);">1.0.0-RC8</span>
            </span>
          </li>
          <li>
            <p>2 days test duration,</p>
            <span style="color: rgb(255,0,0);">
              <span style="color: rgb(0,0,0);"> </span>
            </span>
          </li>
        </ul>
      </td>
      <td colspan="1">
        <ul>
          <li>Throughput: 242 req/second,</li>
          <li>Virtual users: 1000,</li>
        </ul>
      </td>
      <td colspan="1">
        <ul>
          <li>Throughput: 242 req/second,</li>
          <li>Virtual users: 1000,</li>
        </ul>
      </td>
      <td colspan="1">
        <ul>
          <li>OK</li>
          <li>14 errors within 42 million requests,</li>
          <li>First error occurred after 7h of tests,</li>
        </ul>
      </td>
      <td colspan="1">
        <ul>
          <li>Response times and disk writes correlation issue to be investigated,</li>
          <li>errors to be investigated,</li>
        </ul>
      </td>
    </tr>
  </tbody>
</table>
