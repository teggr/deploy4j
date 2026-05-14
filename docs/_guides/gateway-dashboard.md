---
layout: page
title: Gateway Dashboard
---
## Accessing Gateway Dashboard

Deploy4j uses Gateway as a dynamic reverse-proxy. Gateway has an actuator API that can be used to inspect and manage routes. Deploy4j is not configured to expose those endpoints by default.

Gateway dashboard can run in two modes - secure and insecure. Gateway recommends secure mode but this post will cover how to enable both modes.

First, let’s see the insecure mode. Insecure mode is very easy to configure. It is insecure because it exposes the Gateway API on the entrypoint. It means that after configuring the dashboard in insecure mode, the dashboard will be available on port 8080 of the host.

Adding the following snippet in the config/deploy.yml file will enable the unsecure dashboard.

```yaml
# config/deploy.yml
gateway:
  options:
    publish:
      - 8080:8080
  args:
    api.dashboard: true
    api.insecure: true
```

After adding the configuration, run the `deploy4j gateway reboot` command to apply the configuration. This command will stop, remove and start new container again with the latest configuration. The dashboard should be visible on the port 8080 of the host server now e.g. http://99.99.99.99:8080

## Securing the Gateway Dashboard

Now, let’s see about the secure mode. It’s called secure mode because the API is not expose on the entrypoint. We need to create a router rule that uses api@internal service. Let’s look at the configuration for the secure model.

```yaml
gateway:
  options:
    publish:
      - 8080:8080
  args:
    api.dashboard: true
  labels:
    gateway.enable: "true"
    gateway.http.routers.dashboard.rule: (PathPrefix(`/api`) || PathPrefix(`/dashboard`))
    gateway.http.routers.dashboard.service: "api@internal"
    gateway.http.routers.dashboard.middlewares: "auth"
    gateway.http.middlewares.auth.basicauth.users: test:$2y$05$H2o72tMaO.TwY1wNQUV1K.fhjRgLHRDWohFvUZOJHBEtUXNKrqUKi
```

Add `Host(`gateway.example.com`) &&` to the `gateway.http.routers.dashboard.rule` to further restrict access to a specific host.

Here, we have configured Gateway dynamically with Docker labels. First, we created a router. Then we attached the router to the api@internal service because in secure mode we have to do this manually. After that, we added the auth middleware to Gateway. Finally, we configured this auth middleware to use HTTP Basic Authentication and provided credentials. You can read more about these rules in the Gateway docs.

The credentials are in the “username:hashed_password” format. The credentials are generated with the htpasswd command. Let’s say you want to create a user with the username “admin” and the password “super_strong_password” then you can use the following command:

```yaml
htpasswd -nb admin super_strong_password
# output: admin:$apr1$2FGO09Gu$PSZdmmJqyrXWYvidWAm6p0
```

You will get the password hash in the output. Just copy paste the output with the username:password in the labels. The official Gateway docs mention that you need to escape the $ character but you don’t need if you are using Deploy4j but Deploy4j escapes the $ sign for these labels.

That’s it! Don’t forget to reboot the Gateway container with the `deploy4j gateway reboot` command. After that, the dashboard should be accessible on the http://gateway.example.com/dashboard endpoint.

### References

* Spring Cloud Gateway actuator docs - https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway-server-webflux/actuator-api.html
