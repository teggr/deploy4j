---
layout: page
title: Self-Signed Certificate for SSL
---

This guide demonstrates how to deploy your application using Gateway's built-in self-signed certificate feature for SSL/TLS encryption.

## When to Use Self-Signed Certificates

Self-signed certificates are useful when:

* You are deploying behind a service like Cloudflare or a Load Balancer that terminates SSL
* Using Let's Encrypt is not an option for your setup
* You need end-to-end encryption with a self-signed certificate on the server

**Note:** Self-signed certificates will trigger browser security warnings. They are best suited for development environments or when used with services that can accept self-signed certificates (like Cloudflare's Full SSL mode).

## Configuration

Gateway provides self-signed certificate support out of the box. To enable it, modify the `servers` and `gateway` sections of your `config/deploy.yml` file:

```yaml
# Service configuration
service: your-service-name
image: your-docker-image

# Server configuration with HTTPS labels
servers:
  web:
    hosts:
      - your-host-ip-or-domain
    labels:
      gateway.http.routers.yourservice.entrypoints: websecure

# Gateway configuration for HTTPS with self-signed certificate
gateway:
  options:
    publish:
      - "443:443"
  args:
    entryPoints.websecure.address: ":443"
    entrypoints.websecure.http.tls: true
```

### Configuration Breakdown

* `gateway.http.routers.yourservice.entrypoints: websecure` - Configures the service router to use the secure HTTPS entry point
* `publish: "443:443"` - Publishes port 443 (HTTPS) from the Gateway container to the host
* `entryPoints.websecure.address: ":443"` - Defines the websecure entry point on port 443
* `entrypoints.websecure.http.tls: true` - Enables TLS on the websecure entry point, which automatically generates a self-signed certificate

## Deployment

After updating your `config/deploy.yml`, deploy or update your application:

```shell
# For initial setup
deploy4j setup --version <your-version>

# For updates to existing deployment
deploy4j gateway reboot
deploy4j deploy --version <your-version>
```

The `deploy4j gateway reboot` command will restart Gateway with the new configuration, enabling HTTPS with a self-signed certificate.

## Accessing Your Application

Once deployed, you can access your application using HTTPS:

```
https://your-host-ip-or-domain
```

**Important:** Your browser will display a security warning because the certificate is self-signed and not issued by a trusted Certificate Authority. You can:

* Click through the warning to proceed (for development/testing)
* Add an exception for the certificate in your browser
* Configure your CDN/proxy (like Cloudflare) to accept the self-signed certificate

## Using with Cloudflare

If you're using Cloudflare as your CDN/proxy, you can configure end-to-end encryption:

1. In your Cloudflare dashboard, go to SSL/TLS settings
2. Set the SSL/TLS encryption mode to "Full" (not "Full (strict)")
3. This allows Cloudflare to encrypt traffic to your origin server using the self-signed certificate

With this setup:
* Traffic from users to Cloudflare is encrypted with Cloudflare's trusted certificate
* Traffic from Cloudflare to your server is encrypted with your self-signed certificate
* Users won't see any certificate warnings

## Example Configuration

Here's a complete example with a Spring Boot application:

```yaml
service: my-spring-app
image: myregistry/my-spring-app

servers:
  web:
    hosts:
      - 203.0.113.10
    labels:
      gateway.http.routers.my-spring-app.entrypoints: websecure

registry:
  username:
    - DOCKER_USERNAME
  password:
    - DOCKER_PASSWORD

env:
  clear:
    SPRING_DATASOURCE_URL: jdbc:postgresql://203.0.113.10:5432/mydb
    SPRING_DATASOURCE_USERNAME: dbuser
    SPRING_DATASOURCE_PASSWORD: dbpass

ssh:
  key_path:
    - PRIVATE_KEY
  key_passphrase:
    - PRIVATE_KEY_PASSPHRASE
  known_hosts_path:
    - KNOWN_HOSTS_PATH

gateway:
  options:
    publish:
      - "443:443"
  args:
    entryPoints.websecure.address: ":443"
    entrypoints.websecure.http.tls: true
```

## References

* Original blog post: [Kamal Deploy with a self signed certificate for SSL](https://dimitrikoenig.com/blog/kamal-deploy-with-a-self-signed-certificate-for-ssl) by Dimitri Koenig
* [Spring Cloud Gateway TLS Configuration](https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway-server-webflux/tls-and-ssl.html)
* [Cloudflare SSL Modes](https://developers.cloudflare.com/ssl/origin-configuration/ssl-modes/)
