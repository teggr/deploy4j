---
layout: page
title: Monitoring with Ostara
---

[Ostara](https://ostara.dev) is a desktop application that connects to the Actuator endpoints of your Spring Boot applications. There is no server to deploy. See the article [Spring Boot 4 Observability Options](https://robintegg.com/2026/08/10/spring-boot-4-observability-options.html) for a comparison with other options.

## Setup

1. Enable and secure Actuator as described in the [Spring Boot Actuator guide]({{ 'guides/spring-boot-actuator' | relative_url }}). Expose the endpoints you want to view:

   ```yaml
   env:
     clear:
       MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE: "health,info,metrics,env,loggers"
     secret:
       - SPRING_SECURITY_USER_NAME
       - SPRING_SECURITY_USER_PASSWORD
   ```

2. Redeploy: `deploy4j deploy`.
3. [Download and install Ostara](https://ostara.dev) on your desktop.
4. Add a new application instance and enter the Actuator base URL, e.g. `https://your-domain.com/actuator`.
5. If the endpoints are secured, enter the Basic authentication credentials from your `.deploy4j/secrets` file.

You can define and group connections to monitor several applications and environments from one place. Beyond Actuator support, no application changes are needed.

Ostara then shows health, metrics, environment, loggers and other exposed endpoints.

## Tips

- Use HTTPS (see the [Let's Encrypt guide]({{ 'guides/lets-encrypt-certificate' | relative_url }})) so credentials aren't sent in clear text.
- Only exposed endpoints appear in Ostara; add more to `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE` as needed.
