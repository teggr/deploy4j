---
layout: page
title: Monitoring with Boot UI
---

[Boot UI](https://www.julien-dubois.com/boot-ui/) is a local-only developer console for Spring Boot 4 that runs inside your application. It shows live metrics, runtime status, configuration, diagnostics and architecture/security advisors. See the article [Spring Boot 4 Observability Options](https://robintegg.com/2026/08/10/spring-boot-4-observability-options.html) for setup details and a walkthrough.

## Setup

With `spring-boot-devtools` on the classpath, Boot UI is enabled by default in development mode. Add the starter:

```xml
<dependency>
  <groupId>com.julien-dubois.bootui</groupId>
  <artifactId>bootui-spring-boot-starter</artifactId>
  <version>1.13.1</version>
</dependency>
```

Run the app locally and open `http://localhost:8080/bootui`.

## Local use only

Boot UI is designed for local development, **not** for deployed applications. Keep it out of the artifact that deploy4j pushes to your servers, for example by adding the starter only in a Maven profile:

```xml
<profiles>
    <profile>
        <id>local</id>
        <dependencies>
            <dependency>
              <groupId>com.julien-dubois.bootui</groupId>
              <artifactId>bootui-spring-boot-starter</artifactId>
              <version>1.13.1</version>
            </dependency>
        </dependencies>
    </profile>
</profiles>
```

To monitor an application once deployed use [Ostara]({{ 'guides/monitoring-with-ostara' | relative_url }}) or [Spring Boot Admin]({{ 'guides/monitoring-with-spring-boot-admin' | relative_url }}).
