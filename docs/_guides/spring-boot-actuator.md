---
layout: page
title: Spring Boot Actuator
---

[Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/enabling.html) provides production-ready features to help you monitor and manage your application. When deploying Spring Boot applications with deploy4j, you can leverage Actuator endpoints to gain insights into the application's health, metrics, and other operational information.

## Enabling Actuator Endpoints

To enable Actuator endpoints in your Spring Boot application, add the following dependency to your `pom.xml`:

```xml
<dependency>        
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

For security purposes, only the `/health` endpoint is exposed over HTTP by default. You can use the `management.endpoints.web.exposure.include` property to configure the endpoints that are exposed.

You can customize the actuator configuration using the `env` section of your `config/deploy.yml` file:

```yaml
env:
  MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE: "health,info,metrics"
  MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS: "always"
```

We'd [recommend](https://12factor.net/config) customizations to be defined in your deployment configuration to keep them separate from your application code. However, depending on your preferred approach, you can also configure which endpoints to expose in your `application.properties` or `application.yml` file::

```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

## Accessing Actuator Endpoints

Once your application is deployed using deploy4j, you can access the Actuator endpoints via HTTP. For example, if your application is running on `http://your-domain.com`, you can access the health endpoint at:

```shell
curl -v http://your-domain.com/actuator/health
``` 

This will return a JSON response indicating the health status of your application.

## Securing Actuator Endpoints

It's important to secure Actuator endpoints, especially in production environments. You can use [Spring Security to restrict access to these endpoints](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.security). Add the following dependency to your `pom.xml`:

```xml
<dependency>        
    <groupId>org.springframework.boot</groupId>     
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

If Spring Security is on the classpath and no other SecurityFilterChain bean is present, all actuators other than /health are secured by Spring Boot auto-configuration.

Then, configure security settings in your `config/deploy.yml` file:

```yaml
env:
  clear:
    MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE: "health"
    MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS: "never"
  secret:
    - SPRING_SECURITY_USER_NAME
    - SPRING_SECURITY_USER_PASSWORD
```

Use the clear/secrets feature to ensure sensitive information is not hardcoded. Put the values for `SPRING_SECURITY_USER_NAME` and `SPRING_SECURITY_USER_PASSWORD` in your `.env` file.

This configuration will now require authentication to access the Actuator endpoints.
