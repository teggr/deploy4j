---
layout: page
title: Monitoring Spring Boot Apps
---

Once your application is deployed with deploy4j you'll want to see what it's doing. These guides cover consoles and desktop apps for monitoring a deployed Spring Boot application, based on the article [Spring Boot 4 Observability Options: Boot UI, Spring Boot Admin, and Ostara](https://robintegg.com/2026/08/10/spring-boot-4-observability-options.html).

| Tool | Type | Suitable for deployed apps? | Guide |
|------|------|-----------------------------|-------|
| Ostara | Desktop app | Yes, connects to your Actuator endpoints | [Ostara]({{ 'guides/monitoring-with-ostara' | relative_url }}) |
| Spring Boot Admin | Web console | Yes, run as its own app | [Spring Boot Admin]({{ 'guides/monitoring-with-spring-boot-admin' | relative_url }}) |
| Boot UI | In-app developer console | Local development only | [Boot UI]({{ 'guides/monitoring-with-boot-ui' | relative_url }}) |

## Prerequisite: Actuator

Ostara and Spring Boot Admin read data from [Spring Boot Actuator]({{ 'guides/spring-boot-actuator' | relative_url }}). Enable and secure the endpoints first, and check them with:

```shell
curl -u user:password https://your-domain.com/actuator/health
```

Only expose the endpoints you need and always protect them with authentication when they are reachable from the internet.
