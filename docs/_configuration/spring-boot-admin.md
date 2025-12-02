---
layout: page
title: Spring Boot Admin
---
Spring Boot Admin is a community project to manage and monitor Spring Boot applications. Deploy4j provides first-class support for running Spring Boot Admin dashboard alongside your deployed applications.

## Deployment

By default, Spring Boot Admin runs on the same hosts as Traefik (hosts with the `traefik: true` configuration in their role). This ensures that the admin dashboard is co-located with your load balancer for optimal performance and accessibility.

You can configure which hosts run Spring Boot Admin by setting `traefik: true` in the role configuration:

```yaml
servers:
  web:
    hosts:
      - 192.168.1.1
    traefik: true  # Both Traefik and Spring Boot Admin will run on this host
  workers:
    hosts:
      - 192.168.1.2
      - 192.168.1.3
```

## Spring Boot Admin settings

Configured under `spring_boot_admin` in the root configuration. Example:

```yaml
spring_boot_admin:

  # Image
  #
  # The Spring Boot Admin image to use, defaults to `teggr/deploy4j-spring-boot-admin:latest`
  image: teggr/deploy4j-spring-boot-admin:latest

  # Host port
  #
  # The host port to publish the Spring Boot Admin container on, defaults to `8080`
  host_port: 8080

  # Disabling publishing
  #
  # To avoid publishing the Spring Boot Admin container, set this to `false`
  publish: false

  # Labels
  #
  # Additional labels to apply to the Spring Boot Admin container
  labels:
    my.custom.label: value

  # Arguments
  #
  # Additional arguments to pass to the Spring Boot Admin container
  args:
    spring.application.name: "Spring Boot Admin"

  # Options
  #
  # Additional options to pass to `docker run`
  options:
    cpus: 1
    memory: 512m

  # Environment variables
  #
  # See deploy4j docs env
  env:
    clear:
      SPRING_PROFILES_ACTIVE: production
    secret:
      - ADMIN_USERNAME
      - ADMIN_PASSWORD

```

### Configuring Spring Boot applications

Add the Spring Boot Admin Client dependency to your applications and configure them to register with the admin server:

```yaml
spring:
  boot:
    admin:
      client:
        url: http://spring-boot-admin:8080
```

The admin dashboard provides:
- Application health monitoring
- Metrics and performance data
- Log file viewing
- Environment property inspection
- JVM thread dumps and heap dumps
- HTTP request tracing
