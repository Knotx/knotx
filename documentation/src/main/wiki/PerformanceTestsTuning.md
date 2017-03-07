# Tuning

## Introduction
During performance tests we've realized that default configurations are a bottleneck. This applies to Apache HTTP Server, Knot.x and Mocks configuration. To move forward, we've introduced following tuning.

## Apache HTTP Server tuning
```
<IfModule worker.c>
  ServerLimit          200
  StartServers         4
  MaxClients           12672
  MinSpareThreads      25
  MaxSpareThreads      75
  ThreadsPerChild      64
  MaxRequestsPerChild  0
  ListenBackLog        511
</IfModule>
```

## Apache HTTP Server ulimit
`apache - nofile 16384`

## Sysctl tuning on Knot.x, Mocks and Apache HTTP Servers
```
net.core.netdev_max_backlog=2048
net.core.rmem_max=8388608
net.core.somaxconn=4096
net.core.wmem_max=8388608
net.ipv4.tcp_fin_timeout=10
net.ipv4.tcp_max_syn_backlog=8192
net.ipv4.tcp_rmem=4096 81920 8388608
net.ipv4.tcp_slow_start_after_idle=0
net.ipv4.tcp_syn_retries=2
net.ipv4.tcp_synack_retries=2
net.ipv4.tcp_tw_reuse=1
net.ipv4.tcp_wmem=4096 16384 8388608
vm.swappiness=10
```

## Knotx, mocks ulimit
`knotx - nofile 32768`