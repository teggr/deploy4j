---
layout: page
title: deploy4j spring_boot
---
```shell
Spring Boot management commands
      --help   Display help about a command
Commands:
  manage  Manage Spring Boot Actuator endpoints
```

## Manage Spring Boot Applications

Use the `deploy4j spring_boot manage` command to interact with Spring Boot Actuator endpoints.

```shell
Usage: manage [--help] [COMMAND]
Manage Spring Boot Actuator endpoints
      --help   Display help about a command
Commands:
  health          Get health status from Spring Boot Actuator health endpoint
  info            Get application info from Spring Boot Actuator info endpoint
  env             Get environment properties from Spring Boot Actuator env
                    endpoint
  loggers         Get logger configurations from Spring Boot Actuator loggers
                    endpoint
  metrics         Get application metrics from Spring Boot Actuator metrics
                    endpoint
  threaddump      Get thread dump from Spring Boot Actuator threaddump endpoint
  heapdump        Get heap dump from Spring Boot Actuator heapdump endpoint
  scheduledtasks  Get scheduled tasks from Spring Boot Actuator scheduledtasks
                    endpoint
  httptrace       Get HTTP trace from Spring Boot Actuator httptrace endpoint
                    (deprecated, use httpexchanges for Spring Boot 3.x+)
  httpexchanges   Get HTTP exchange info from Spring Boot Actuator
                    httpexchanges endpoint (Spring Boot 3.x+)
  beans           Get beans from Spring Boot Actuator beans endpoint
  conditions      Get conditions from Spring Boot Actuator conditions endpoint
  configprops     Get config props from Spring Boot Actuator configprops
                    endpoint
  mappings        Get mappings from Spring Boot Actuator mappings endpoint
  shutdown        Shutdown the application using Spring Boot Actuator shutdown
                    endpoint
  caches          Get caches from Spring Boot Actuator caches endpoint
  flyway          Get flyway migrations from Spring Boot Actuator flyway
                    endpoint
  liquibase       Get liquibase migrations from Spring Boot Actuator liquibase
                    endpoint
  sessions        Get sessions from Spring Boot Actuator sessions endpoint
  startup         Get startup info from Spring Boot Actuator startup endpoint
  endpoint        Access a custom actuator endpoint
```

Each action will connect to each server in the `web` role (or the role specified with `--role` option) and call the corresponding Actuator endpoint.

```shell
deploy4j spring_boot manage health --version 0.0.3-SNAPSHOT

Running actuator health on 1 host(s)...
Hook pre-connect started
=== localhost (deploy4j-demo-web-0.0.3-SNAPSHOT) ===
{"status":"UP"}
```