---
layout: page
title: Monitoring with Spring Boot Admin
---

[Spring Boot Admin](https://github.com/codecentric/spring-boot-admin) is a web console that monitors and manages Spring Boot applications through their Actuator endpoints. It has a server application and a client dependency added to each monitored app. See the article [Spring Boot 4 Observability Options](https://robintegg.com/2026/08/10/spring-boot-4-observability-options.html) for more context.

The server can run as its own application (typical for production) or be embedded in one of your services, in which case the app registers with itself:

```yaml
spring:
  boot:
    admin:
      context-path: /admin
      client:
        url: http://localhost:8080
```

Locally the dashboard is then at `http://localhost:8080/admin/applications`. Its main strength is viewing a fleet of services and instances in one place.

## Set up the Admin server

Create a small Spring Boot application with the server starter:

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
    <version>4.0.4</version>
</dependency>
```

```java
@SpringBootApplication
@EnableAdminServer
public class AdminServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminServerApplication.class, args);
    }
}
```

Secure the console with Spring Security, then deploy it like any other application with deploy4j (as a separate service or on the same server).

## Register your application

Add the client to the application you want to monitor:

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
    <version>4.0.4</version>
</dependency>
```

Configure it in `config/deploy.yml`, along with the Actuator settings from the [Spring Boot Actuator guide]({{ 'guides/spring-boot-actuator' | relative_url }}):

```yaml
env:
  clear:
    SPRING_BOOT_ADMIN_CLIENT_URL: "https://admin.your-domain.com"
    SPRING_BOOT_ADMIN_CLIENT_INSTANCE_SERVICE_URL: "https://your-domain.com"
    MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE: "*"
  secret:
    - SPRING_BOOT_ADMIN_CLIENT_USERNAME
    - SPRING_BOOT_ADMIN_CLIENT_PASSWORD
```

Set `SPRING_BOOT_ADMIN_CLIENT_INSTANCE_SERVICE_URL` to the public URL: inside a container the auto-detected host name is not reachable by the Admin server. Put credentials in `.deploy4j/secrets`, redeploy, and the application appears in the Admin console.
