---
layout: page
title: Let's Encrypt Certificate for SSL
---

This guide demonstrates how to deploy your application with free, automatically-renewed SSL/TLS certificates from Let's Encrypt using Traefik's built-in ACME (Automatic Certificate Management Environment) support.

## When to Use Let's Encrypt

Let's Encrypt certificates are ideal when:

* You have a public domain name pointing to your server
* You want automatic certificate renewal (certificates are valid for 90 days and auto-renew)
* You need trusted SSL certificates recognized by all browsers
* You want zero-cost SSL/TLS encryption for production applications

**Note:** Let's Encrypt requires your server to be publicly accessible on ports 80 (HTTP) or 443 (HTTPS), and you must have a valid domain name configured with DNS pointing to your server.

## Prerequisites

Before configuring Let's Encrypt, ensure you have:

1. **A registered domain name** (e.g., `yourdomain.com` or `app.yourdomain.com`)
2. **DNS configured** with an A record pointing to your server's IP address
3. **Port 80 and/or 443 accessible** from the public internet
4. **Email address** for Let's Encrypt registration and renewal notifications

You can verify your DNS is configured correctly:

```shell
# Check if your domain resolves to your server IP
nslookup yourdomain.com

# Or using dig
dig yourdomain.com +short
```

## Configuration

Traefik provides comprehensive Let's Encrypt support through ACME certificate resolvers. To enable it, modify your `config/deploy.yml` file:

### Basic HTTP Challenge Configuration

The HTTP-01 challenge is the most common method. Let's Encrypt verifies domain ownership by making an HTTP request to your domain:

```yaml
# Service configuration
service: your-service-name
image: your-docker-image

# Server configuration with HTTPS labels
servers:
  web:
    hosts:
      - your-server-ip
    labels:
      traefik.http.routers.yourservice.entrypoints: websecure
      traefik.http.routers.yourservice.rule: Host(`yourdomain.com`)
      traefik.http.routers.yourservice.tls: true
      traefik.http.routers.yourservice.tls.certresolver: letsencrypt

# Traefik configuration for HTTPS with Let's Encrypt
traefik:
  options:
    publish:
      - "443:443"
    volume:
      - "/etc/deploy4j/letsencrypt:/letsencrypt"
  args:
    entryPoints.web.address: ":80"
    entryPoints.websecure.address: ":443"
    entryPoints.web.http.redirections.entryPoint.to: websecure
    entryPoints.web.http.redirections.entryPoint.scheme: https
    certificatesResolvers.letsencrypt.acme.email: "your-email@example.com"
    certificatesResolvers.letsencrypt.acme.storage: "/letsencrypt/acme.json"
    certificatesResolvers.letsencrypt.acme.httpChallenge.entryPoint: web
```

### Configuration Breakdown

#### Service Labels

* `traefik.http.routers.yourservice.entrypoints: websecure` - Use the HTTPS entry point (port 443)
* `traefik.http.routers.yourservice.rule: Host(\`yourdomain.com\`)` - Route traffic for your domain
* `traefik.http.routers.yourservice.tls: true` - Enable TLS for this router
* `traefik.http.routers.yourservice.tls.certresolver: letsencrypt` - Use the Let's Encrypt certificate resolver

#### Traefik Configuration

* `publish: "443:443"` - Expose HTTPS port 443
* `volume: "/etc/deploy4j/letsencrypt:/letsencrypt"` - Persist Let's Encrypt certificates within the deploy4j data directory
* `entryPoints.web.address: ":80"` - Define HTTP entry point (required for HTTP challenge)
* `entryPoints.websecure.address: ":443"` - Define HTTPS entry point
* `entryPoints.web.http.redirections.*` - Automatically redirect HTTP to HTTPS
* `certificatesResolvers.letsencrypt.acme.email` - Your email for Let's Encrypt notifications
* `certificatesResolvers.letsencrypt.acme.storage` - Where to store certificates
* `certificatesResolvers.letsencrypt.acme.httpChallenge.entryPoint: web` - Use HTTP challenge on port 80

## Deployment

After updating your `config/deploy.yml` and preparing the server, deploy your application:

```shell
# For initial setup
deploy4j setup --version <your-version>

# For updates to existing deployment
deploy4j traefik reboot
deploy4j deploy --version <your-version>
```

The first deployment will:
1. Start Traefik with Let's Encrypt configuration
2. Request a certificate from Let's Encrypt for your domain
3. Install the certificate and begin serving HTTPS traffic
4. Set up automatic renewal (certificates renew automatically before expiration)

## Accessing Your Application

Once deployed, your application will be accessible via HTTPS with a valid, trusted certificate:

```
https://yourdomain.com
```

Browsers will show a secure padlock icon, and the certificate will be trusted without warnings.

## Certificate Renewal

Let's Encrypt certificates are valid for 90 days. Traefik automatically handles renewal:

* Traefik checks for certificate expiration
* Certificates are automatically renewed ~30 days before expiration
* No manual intervention required
* Renewal notifications are sent to the email you configured

## Staging Environment (Testing)

When first setting up Let's Encrypt, it's recommended to test with the staging environment to avoid hitting rate limits:

```yaml
traefik:
  options:
    publish:
      - "443:443"
    volume:
      - "/etc/deploy4j/letsencrypt:/letsencrypt"
  args:
    entryPoints.web.address: ":80"
    entryPoints.websecure.address: ":443"
    entryPoints.web.http.redirections.entryPoint.to: websecure
    entryPoints.web.http.redirections.entryPoint.scheme: https
    certificatesResolvers.letsencrypt.acme.email: "your-email@example.com"
    certificatesResolvers.letsencrypt.acme.storage: "/letsencrypt/acme.json"
    certificatesResolvers.letsencrypt.acme.httpChallenge.entryPoint: web
    certificatesResolvers.letsencrypt.acme.caServer: "https://acme-staging-v02.api.letsencrypt.org/directory"
```

The staging certificates won't be trusted by browsers, but you can verify the process works. Once confirmed, remove the `caServer` line to use production certificates.

## Multiple Domains

To support multiple domains or subdomains, configure multiple routers:

```yaml
servers:
  web:
    hosts:
      - your-server-ip
    labels:
      # Main domain
      traefik.http.routers.yourservice.entrypoints: websecure
      traefik.http.routers.yourservice.rule: Host(`yourdomain.com`)
      traefik.http.routers.yourservice.tls: true
      traefik.http.routers.yourservice.tls.certresolver: letsencrypt
      # Additional domain
      traefik.http.routers.yourservice-www.entrypoints: websecure
      traefik.http.routers.yourservice-www.rule: Host(`www.yourdomain.com`)
      traefik.http.routers.yourservice-www.tls: true
      traefik.http.routers.yourservice-www.tls.certresolver: letsencrypt
```

Traefik will automatically request and manage separate certificates for each domain.

## Complete Example

Here's a complete example with a Spring Boot application using Let's Encrypt:

```yaml
service: my-spring-app
image: myregistry/my-spring-app

servers:
  web:
    hosts:
      - 203.0.113.10
    labels:
      traefik.http.routers.my-spring-app.entrypoints: websecure
      traefik.http.routers.my-spring-app.rule: Host(`app.example.com`)
      traefik.http.routers.my-spring-app.tls: true
      traefik.http.routers.my-spring-app.tls.certresolver: letsencrypt

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

traefik:
  options:
    publish:
      - "443:443"
    volume:
      - "/etc/deploy4j/letsencrypt:/letsencrypt"
  args:
    entryPoints.web.address: ":80"
    entryPoints.websecure.address: ":443"
    entryPoints.web.http.redirections.entryPoint.to: websecure
    entryPoints.web.http.redirections.entryPoint.scheme: https
    certificatesResolvers.letsencrypt.acme.email: "admin@example.com"
    certificatesResolvers.letsencrypt.acme.storage: "/letsencrypt/acme.json"
    certificatesResolvers.letsencrypt.acme.httpChallenge.entryPoint: web
```

## Troubleshooting

### Certificate Not Being Issued

1. **Check DNS configuration**: Ensure your domain resolves to your server's IP
   ```shell
   nslookup yourdomain.com
   ```

2. **Verify port 80 is accessible**: Let's Encrypt needs to reach your server on port 80
   ```shell
   curl -I http://yourdomain.com
   ```

3. **Check Traefik logs**: Look for ACME errors
   ```shell
   deploy4j traefik logs
   ```

4. **Verify acme.json permissions**: Must be 600
   ```shell
   ssh root@your-server-ip "ls -la /letsencrypt/acme.json"
   ```

### Rate Limits

Let's Encrypt has rate limits:
* 50 certificates per registered domain per week
* 5 duplicate certificates per week

If you hit rate limits, use the staging environment for testing, or wait for the limit to reset.

### Certificate Expired

If your certificate expires (rare with auto-renewal):

1. Check Traefik logs for renewal errors
2. Ensure your server was online during renewal attempts
3. Manually trigger renewal by restarting Traefik:
   ```shell
   deploy4j traefik reboot
   ```

### HTTP Redirect Not Working

If HTTP doesn't redirect to HTTPS, verify:
1. Port 80 is published and accessible
2. The redirection configuration is in the Traefik args
3. Both `web` and `websecure` entry points are configured

## DNS Challenge (Alternative)

For servers not publicly accessible on port 80, or for wildcard certificates, use DNS challenge:

```yaml
traefik:
  options:
    publish:
      - "443:443"
    volume:
      - "/etc/deploy4j/letsencrypt:/letsencrypt"
  env:
    clear:
      CLOUDFLARE_EMAIL: "your-email@example.com"
      CLOUDFLARE_API_KEY: "your-api-key"
  args:
    entryPoints.websecure.address: ":443"
    certificatesResolvers.letsencrypt.acme.email: "your-email@example.com"
    certificatesResolvers.letsencrypt.acme.storage: "/letsencrypt/acme.json"
    certificatesResolvers.letsencrypt.acme.dnsChallenge.provider: cloudflare
    certificatesResolvers.letsencrypt.acme.dnsChallenge.resolvers: "1.1.1.1:53,8.8.8.8:53"
```

This requires DNS provider API credentials. See [Traefik DNS Challenge documentation](https://doc.traefik.io/traefik/https/acme/#dnschallenge) for supported providers.

## References

* Inspired by: [Secure your Kamal app deployments with Let's Encrypt](https://dennmart.com/articles/secure-your-kamal-app-deployments-with-lets-encrypt/) by Dennis Martinez
* [Traefik ACME Documentation](https://doc.traefik.io/traefik/reference/install-configuration/tls/certificate-resolvers/acme/)
* [Let's Encrypt Documentation](https://letsencrypt.org/docs/)
* [Let's Encrypt Rate Limits](https://letsencrypt.org/docs/rate-limits/)
