---
layout: page
title: Traefik Dashboard
---
## Accessing Traefik Dashboard

Deploy4j uses Traefik as a dynamic reverse-proxy. Traefik has a [beautiful dashboard](https://doc.traefik.io/traefik/reference/install-configuration/api-dashboard/#configuration-options) to visually display the configuration. Deploy4j is not configured to show the dashboard by default.

Traefik dashboard can run in two modes - secure and insecure. Traefik recommends secure mode but this post wil cover how to enable both the modes.

First, let’s see the unsecure mode. Unsecure mode is very easy to configure. It is unsecure because it exposes Traefix API on the entrypoint . It means that after configuring the dashboard in unsecure mode, the dashboard will be available on the port 8080 of the host.

Adding the following snippet in the config/deploy.yml file will enable the unsecure dashboard.

```yaml
# config/deploy.yml
traefik:
  options:
    publish:
      - 8080:8080
  args:
    api.dashboard: true
    api.insecure: true
```

After adding the configuration, run the `deploy4j traefik reboot` command to apply the configuration. This command will stop, remove and start new container again with the latest configuration. The dashboard should be visible on the port 8080 of the host server now e.g. http://99.99.99.99:8080

## Securing the Traefik Dashboard

Now, let’s see about the secure mode. It’s called secure mode because the API is not expose on the entrypoint. We need to create a router rule that uses api@internal service. Let’s look at the configuration for the secure model.

```yaml
traefik:
  options:
    publish:
      - 8080:8080
  args:
    api.dashboard: true
  labels:
    traefik.enable: "true"
    traefik.http.routers.dashboard.rule: (PathPrefix(`/api`) || PathPrefix(`/dashboard`))
    traefik.http.routers.dashboard.service: "api@internal"
    traefik.http.routers.dashboard.middlewares: "auth"
    traefik.http.middlewares.auth.basicauth.users: test:$2y$05$H2o72tMaO.TwY1wNQUV1K.fhjRgLHRDWohFvUZOJHBEtUXNKrqUKi
```

Add `Host(`traefik.example.com`) &&` to the `traefik.http.routers.dashboard.rule` to further restrict access to a specific host.

Here, we have configured Traefik dynamically with help of Docker labels. First of we have created a router. Then we attached the router with the api@internal service because in the secure mode we have to do this manually. After that, we added the auth middleware to Trafeik. In the last, we conifgured this auth middleware to use HTTP Basic Authentication and provided it with the credentials. You can read more about the rules in the details on the Traefik docs .

The credentials are in the “username:hashed_password” format. The credentials are generated with the htpasswd command. Let’s say you want to create a user with the username “admin” and the password “super_strong_password” then you can use the following command:

```yaml
htpasswd -nb admin super_strong_password
# output: admin:$apr1$2FGO09Gu$PSZdmmJqyrXWYvidWAm6p0
```

You will get the password hash in the output. Just copy paste the output with the username:password in the labels. The official Traefik docs mention that you need to escape the $ character but you don’t need if you are using Deploy4j but Deploy4j escapes the $ sign for these labels.

That’s it! Don’t forget to reboot the Trafeik container with the `deploy4j traefik reboot` comamnd. After that, the dashboard should be accessible on the http://traefik.example.com/dashboard endpoint.

### References

* Original source - https://www.kartikey.dev/2023/04/12/how-to-enable-traefik-dashboard-with-mrsk.html
* Traefik Dashboard docs - https://doc.traefik.io/traefik/reference/install-configuration/api-dashboard/#configuration-options