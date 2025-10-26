# Deploy4j TLS/HTTPS Quick Start Guide

This guide will help you enable HTTPS for your deploy4j applications in under 10 minutes.

## Prerequisites

Before you begin, ensure you have:

1. ✅ **A domain name** (e.g., `myapp.example.com`)
2. ✅ **DNS configured** - Domain points to your server's IP address
3. ✅ **Ports open** - Ports 80 and 443 accessible from the internet
4. ✅ **Valid email** - For Let's Encrypt certificate notifications
5. ✅ **Deploy4j working** - Successfully deployed an HTTP application

## Step-by-Step Configuration

### Step 1: Update your deploy.yml

Add TLS configuration to your existing `config/deploy.yml`:

```yaml
service: "myapp"
image: "myorg/myapp"

servers:
  web:
    hosts:
      - "YOUR_SERVER_IP"
    labels:
      # Add these labels for HTTPS support
      traefik.http.routers.myapp-web.rule: "Host(`myapp.example.com`)"
      traefik.http.routers.myapp-web.entrypoints: "https"
      traefik.http.routers.myapp-web.tls: "true"
      traefik.http.routers.myapp-web.tls.certresolver: "letsencrypt"

# Add Traefik TLS configuration
traefik:
  host_port: 80
  args:
    log.level: "INFO"
    # Define entry points
    entrypoints.http.address: ":80"
    entrypoints.https.address: ":443"
    # Let's Encrypt configuration
    certificatesresolvers.letsencrypt.acme.email: "YOUR_EMAIL@example.com"
    certificatesresolvers.letsencrypt.acme.storage: "/letsencrypt/acme.json"
    certificatesresolvers.letsencrypt.acme.httpchallenge.entrypoint: "http"
  options:
    publish: "443:443"
    volume: "/etc/deploy4j/letsencrypt:/letsencrypt"
```

### Step 2: Update DNS

Ensure your domain points to your server:

```bash
# Check DNS resolution
nslookup myapp.example.com

# Should return your server's IP address
```

### Step 3: Deploy

Reboot Traefik and redeploy your application:

```bash
# Reboot Traefik with new configuration
deploy4j traefik reboot

# Redeploy your application
deploy4j app boot
```

### Step 4: Verify

1. **Check certificate issuance** (may take 1-2 minutes):
   ```bash
   deploy4j traefik logs --lines 100 | grep -i acme
   ```

2. **Test HTTPS endpoint**:
   ```bash
   curl -I https://myapp.example.com
   ```

3. **Open in browser**: Navigate to `https://myapp.example.com`

🎉 **Done!** Your application is now secured with HTTPS.

## Enable HTTP to HTTPS Redirect (Recommended)

Add automatic redirect from HTTP to HTTPS:

```yaml
traefik:
  args:
    # ... existing args ...
    # Add these lines for automatic redirect
    entrypoints.http.http.redirections.entrypoint.to: "https"
    entrypoints.http.http.redirections.entrypoint.scheme: "https"
    entrypoints.http.http.redirections.entrypoint.permanent: "true"
```

Redeploy Traefik:

```bash
deploy4j traefik reboot
```

## Testing with Let's Encrypt Staging

Before going to production, test with Let's Encrypt staging server to avoid rate limits:

```yaml
traefik:
  args:
    # ... existing args ...
    # Add this line for testing
    certificatesresolvers.letsencrypt.acme.caserver: "https://acme-staging-v02.api.letsencrypt.org/directory"
```

**Note:** Staging certificates will show a security warning in browsers. This is expected.

**For production:** Remove the `caserver` line.

## Troubleshooting

### Certificate Not Issued

**Check Traefik logs:**
```bash
deploy4j traefik logs --lines 200 | grep -i acme
```

**Common issues:**
- DNS not pointing to server → Update DNS, wait for propagation
- Port 80/443 blocked → Check firewall rules
- Invalid email → Update email in configuration
- Rate limit hit → Use staging server or wait

### HTTPS Not Working

**Verify configuration:**
```bash
# Check Traefik is running
deploy4j traefik details

# Check ports are open
nc -zv YOUR_SERVER_IP 443

# Test from server itself
curl -v https://localhost
```

### Certificate Shows Wrong Domain

**Cause:** Router rule doesn't match domain

**Fix:** Ensure `Host()` rule matches your domain exactly:
```yaml
traefik.http.routers.myapp-web.rule: "Host(`myapp.example.com`)"
```

## Multiple Applications

To add HTTPS to multiple applications on the same server:

```yaml
# App 1
servers:
  web:
    labels:
      traefik.http.routers.app1.rule: "Host(`app1.example.com`)"
      traefik.http.routers.app1.entrypoints: "https"
      traefik.http.routers.app1.tls: "true"
      traefik.http.routers.app1.tls.certresolver: "letsencrypt"

# App 2 (in accessories)
accessories:
  app2:
    labels:
      traefik.http.routers.app2.rule: "Host(`app2.example.com`)"
      traefik.http.routers.app2.entrypoints: "https"
      traefik.http.routers.app2.tls: "true"
      traefik.http.routers.app2.tls.certresolver: "letsencrypt"
```

Each application gets its own certificate automatically!

## Security Best Practices

1. **Use a role-based email** for certificate notifications:
   ```yaml
   certificatesresolvers.letsencrypt.acme.email: "ssl-admin@example.com"
   ```

2. **Backup certificate storage**:
   ```bash
   # Backup acme.json
   scp root@YOUR_SERVER:/etc/deploy4j/letsencrypt/acme.json ./acme-backup.json
   ```

3. **Monitor certificate expiry**:
   - Let's Encrypt sends expiry notifications to configured email
   - Traefik auto-renews 30 days before expiry

4. **Use HSTS headers** (optional):
   ```yaml
   servers:
     web:
       labels:
         traefik.http.middlewares.hsts.headers.stsSeconds: "31536000"
         traefik.http.middlewares.hsts.headers.stsIncludeSubdomains: "true"
         traefik.http.routers.myapp-web.middlewares: "hsts"
   ```

## Complete Example

Here's a complete, production-ready configuration:

```yaml
service: "myapp"
image: "myorg/myapp"
registry:
  username: "DOCKER_USERNAME"
  password: "DOCKER_PASSWORD"

servers:
  web:
    hosts:
      - "123.45.67.89"
    labels:
      # HTTPS configuration
      traefik.http.routers.myapp-web.rule: "Host(`myapp.example.com`)"
      traefik.http.routers.myapp-web.entrypoints: "https"
      traefik.http.routers.myapp-web.tls: "true"
      traefik.http.routers.myapp-web.tls.certresolver: "letsencrypt"
      # Security headers
      traefik.http.middlewares.security.headers.stsSeconds: "31536000"
      traefik.http.middlewares.security.headers.stsIncludeSubdomains: "true"
      traefik.http.routers.myapp-web.middlewares: "security"

ssh:
  user: "root"
  privateKey: "PRIVATE_KEY"

env:
  DATABASE_HOST: "db.example.com"
  DATABASE_PORT: "5432"

traefik:
  host_port: 80
  args:
    log.level: "INFO"
    # Entry points
    entrypoints.http.address: ":80"
    entrypoints.https.address: ":443"
    # HTTP to HTTPS redirect
    entrypoints.http.http.redirections.entrypoint.to: "https"
    entrypoints.http.http.redirections.entrypoint.scheme: "https"
    entrypoints.http.http.redirections.entrypoint.permanent: "true"
    # Let's Encrypt
    certificatesresolvers.letsencrypt.acme.email: "ssl-admin@example.com"
    certificatesresolvers.letsencrypt.acme.storage: "/letsencrypt/acme.json"
    certificatesresolvers.letsencrypt.acme.httpchallenge.entrypoint: "http"
  options:
    publish: "443:443"
    volume: "/etc/deploy4j/letsencrypt:/letsencrypt"
```

## Next Steps

- ✅ **Monitor certificates**: Check email for Let's Encrypt notifications
- ✅ **Test renewal**: Traefik handles this automatically
- ✅ **Add more apps**: Use same Traefik config, different domains
- 📖 **Learn more**: See [TRAEFIK_TLS_PROPOSAL.md](TRAEFIK_TLS_PROPOSAL.md) for advanced configurations

## FAQ

**Q: How long does it take to get a certificate?**  
A: Usually 1-2 minutes after deployment.

**Q: Do I need to renew certificates manually?**  
A: No! Traefik automatically renews certificates 30 days before expiry.

**Q: Can I use the same configuration for multiple servers?**  
A: Yes, but each server needs its own certificates (automatic with Let's Encrypt).

**Q: What if I don't have a domain?**  
A: You need a domain for Let's Encrypt. Consider services like DuckDNS for free subdomains.

**Q: Is Let's Encrypt free?**  
A: Yes! Completely free with no limits for normal use.

**Q: Can I force HTTPS only?**  
A: Yes! Use the HTTP redirect configuration shown above.

## Support

- 📖 [Full Documentation](TRAEFIK_TLS_PROPOSAL.md)
- 🐛 [Report Issues](https://github.com/teggr/deploy4j/issues)
- 💬 [Discussions](https://github.com/teggr/deploy4j/discussions)

---

**Happy deploying with HTTPS! 🔒**
