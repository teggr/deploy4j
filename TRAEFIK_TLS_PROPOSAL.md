# Traefik TLS with Let's Encrypt - Solution Proposal

## Executive Summary

This document provides a comprehensive solution for enabling HTTPS/TLS support in deploy4j using Traefik with Let's Encrypt automatic certificate management. The solution is designed to require **minimal code changes** while providing full TLS functionality through configuration.

## Table of Contents

1. [Current State Analysis](#current-state-analysis)
2. [Requirements](#requirements)
3. [Proposed Solution](#proposed-solution)
4. [Configuration Examples](#configuration-examples)
5. [Implementation Plan](#implementation-plan)
6. [Testing Strategy](#testing-strategy)
7. [Documentation Requirements](#documentation-requirements)
8. [Security Considerations](#security-considerations)

## Current State Analysis

### Existing Architecture

Deploy4j currently deploys Traefik v2.11 as a reverse proxy with the following configuration:

**Location:** `deploy4j-core/src/main/java/dev/deploy4j/deploy/host/commands/TraefikHostCommands.java`

**Current Docker Run Command:**
```java
docker().run()
  .args("--name", "traefik")
  .args("--detach")
  .args("--restart", "unless-stopped")
  .args(publishArgs())  // Currently publishes port 80
  .args("--volume", "/var/run/docker.sock:/var/run/docker.sock")
  .args(envArgs())
  .args(config().loggingArgs())
  .args(labelArgs())
  .args(dockerOptionsArgs())
  .args(image())
  .args("--providers.docker")
  .args(cmdOptionArgs())
```

**Default Configuration:**
- Image: `traefik:v2.11`
- Port: 80 (HTTP only)
- Log level: DEBUG
- Docker provider enabled
- Catch-all router for 502 errors

**Configuration Files:**
- `Traefik.java` - Configuration wrapper with defaults
- `TraefikConfig.java` - Raw configuration data class
- `example-deployment.yml` - Sample configuration

### Limitations

1. **No HTTPS support** - Only HTTP (port 80) is exposed
2. **No automatic certificates** - Manual certificate management would be required
3. **No encryption** - All traffic is transmitted in plain text
4. **Limited production readiness** - HTTPS is essential for production deployments

## Requirements

### Functional Requirements

1. **HTTPS Support**
   - Enable HTTPS (port 443) in addition to HTTP (port 80)
   - Support Let's Encrypt automatic certificate provisioning
   - Automatic certificate renewal

2. **Configuration Flexibility**
   - Users can configure via `deploy.yml`
   - Support for HTTP-only (backward compatible)
   - Support for HTTPS-only
   - Support for HTTP with redirect to HTTPS

3. **Certificate Management**
   - Persistent certificate storage
   - Multiple certificate resolvers support
   - Support for Let's Encrypt staging (testing)

4. **Service Integration**
   - Services can specify TLS requirements via labels
   - Support for domain-based routing
   - Multiple services on same host with different domains

### Non-Functional Requirements

1. **Backward Compatibility**
   - Existing HTTP-only deployments continue to work
   - No breaking changes to configuration format

2. **Security**
   - Secure certificate storage
   - Proper file permissions for ACME storage
   - Support for security best practices

3. **Usability**
   - Clear documentation
   - Sensible defaults
   - Validation and error messages

## Proposed Solution

### Solution Overview

The solution leverages **existing configuration mechanisms** (args, options, labels) to enable TLS without requiring new code. This approach:

✅ **Minimizes code changes** - Uses existing `args` and `options` configuration  
✅ **Backward compatible** - HTTP-only deployments continue to work  
✅ **Flexible** - Supports various TLS configurations  
✅ **Production-ready** - Based on Traefik's official TLS implementation  

### Key Components

#### 1. Entry Points Configuration

Traefik requires explicit entry point definitions for HTTP and HTTPS:

```yaml
traefik:
  args:
    entrypoints.http.address: ":80"
    entrypoints.https.address: ":443"
```

#### 2. Certificate Resolver

Let's Encrypt certificate provisioning via ACME protocol:

```yaml
traefik:
  args:
    certificatesresolvers.letsencrypt.acme.email: "admin@example.com"
    certificatesresolvers.letsencrypt.acme.storage: "/letsencrypt/acme.json"
    certificatesresolvers.letsencrypt.acme.httpchallenge.entrypoint: "http"
```

**Note:** The resolver name `letsencrypt` can be customized.

#### 3. Port Publishing

Expose port 443 for HTTPS traffic:

```yaml
traefik:
  options:
    publish: "443:443"
```

**Note:** The existing `host_port` configuration handles port 80.

#### 4. Certificate Storage

Persist certificates across container restarts:

```yaml
traefik:
  options:
    volume: "/etc/deploy4j/letsencrypt:/letsencrypt"
```

#### 5. HTTP to HTTPS Redirect (Optional)

Automatically redirect all HTTP traffic to HTTPS:

```yaml
traefik:
  args:
    entrypoints.http.http.redirections.entrypoint.to: "https"
    entrypoints.http.http.redirections.entrypoint.scheme: "https"
```

#### 6. Service-Level TLS Configuration

Services specify TLS requirements via Docker labels:

```yaml
servers:
  web:
    labels:
      traefik.http.routers.myapp.rule: "Host(`myapp.example.com`)"
      traefik.http.routers.myapp.entrypoints: "https"
      traefik.http.routers.myapp.tls: "true"
      traefik.http.routers.myapp.tls.certresolver: "letsencrypt"
```

## Configuration Examples

### Example 1: Basic HTTPS with Let's Encrypt

**Scenario:** Enable HTTPS for a single application with automatic Let's Encrypt certificates.

**deploy.yml:**
```yaml
service: "myapp"
image: "myorg/myapp"

servers:
  web:
    hosts:
      - "123.45.67.89"
    labels:
      traefik.http.routers.myapp-web.rule: "Host(`myapp.example.com`)"
      traefik.http.routers.myapp-web.entrypoints: "https"
      traefik.http.routers.myapp-web.tls: "true"
      traefik.http.routers.myapp-web.tls.certresolver: "letsencrypt"

traefik:
  host_port: 80
  args:
    log.level: "INFO"
    entrypoints.http.address: ":80"
    entrypoints.https.address: ":443"
    certificatesresolvers.letsencrypt.acme.email: "admin@example.com"
    certificatesresolvers.letsencrypt.acme.storage: "/letsencrypt/acme.json"
    certificatesresolvers.letsencrypt.acme.httpchallenge.entrypoint: "http"
  options:
    publish: "443:443"
    volume: "/etc/deploy4j/letsencrypt:/letsencrypt"
```

**Prerequisites:**
- Domain `myapp.example.com` must point to `123.45.67.89`
- Ports 80 and 443 must be open in firewall
- Valid email address for Let's Encrypt notifications

### Example 2: HTTP with Automatic Redirect to HTTPS

**Scenario:** Redirect all HTTP traffic to HTTPS automatically.

**deploy.yml:**
```yaml
service: "myapp"
image: "myorg/myapp"

servers:
  web:
    hosts:
      - "123.45.67.89"
    labels:
      traefik.http.routers.myapp-web.rule: "Host(`myapp.example.com`)"
      traefik.http.routers.myapp-web.entrypoints: "https"
      traefik.http.routers.myapp-web.tls: "true"
      traefik.http.routers.myapp-web.tls.certresolver: "letsencrypt"

traefik:
  host_port: 80
  args:
    log.level: "INFO"
    entrypoints.http.address: ":80"
    entrypoints.https.address: ":443"
    # Automatic HTTP to HTTPS redirect
    entrypoints.http.http.redirections.entrypoint.to: "https"
    entrypoints.http.http.redirections.entrypoint.scheme: "https"
    entrypoints.http.http.redirections.entrypoint.permanent: "true"
    # Let's Encrypt configuration
    certificatesresolvers.letsencrypt.acme.email: "admin@example.com"
    certificatesresolvers.letsencrypt.acme.storage: "/letsencrypt/acme.json"
    certificatesresolvers.letsencrypt.acme.httpchallenge.entrypoint: "http"
  options:
    publish: "443:443"
    volume: "/etc/deploy4j/letsencrypt:/letsencrypt"
```

**Behavior:**
- HTTP requests to `http://myapp.example.com` → redirected to `https://myapp.example.com`
- Permanent redirect (HTTP 301) cached by browsers

### Example 3: Multiple Services with Different Domains

**Scenario:** Host multiple applications on the same server with different domains, all using HTTPS.

**deploy.yml:**
```yaml
service: "app1"
image: "myorg/app1"

servers:
  web:
    hosts:
      - "123.45.67.89"
    labels:
      traefik.http.routers.app1.rule: "Host(`app1.example.com`)"
      traefik.http.routers.app1.entrypoints: "https"
      traefik.http.routers.app1.tls: "true"
      traefik.http.routers.app1.tls.certresolver: "letsencrypt"

accessories:
  app2:
    image: "myorg/app2"
    host: "123.45.67.89"
    labels:
      traefik.http.routers.app2.rule: "Host(`app2.example.com`)"
      traefik.http.routers.app2.entrypoints: "https"
      traefik.http.routers.app2.tls: "true"
      traefik.http.routers.app2.tls.certresolver: "letsencrypt"

traefik:
  host_port: 80
  args:
    log.level: "INFO"
    entrypoints.http.address: ":80"
    entrypoints.https.address: ":443"
    certificatesresolvers.letsencrypt.acme.email: "admin@example.com"
    certificatesresolvers.letsencrypt.acme.storage: "/letsencrypt/acme.json"
    certificatesresolvers.letsencrypt.acme.httpchallenge.entrypoint: "http"
  options:
    publish: "443:443"
    volume: "/etc/deploy4j/letsencrypt:/letsencrypt"
```

**Result:**
- `app1.example.com` → app1 service (HTTPS)
- `app2.example.com` → app2 service (HTTPS)
- Both share same Let's Encrypt certificate resolver
- Certificates automatically managed per domain

### Example 4: Testing with Let's Encrypt Staging

**Scenario:** Test TLS configuration without hitting Let's Encrypt rate limits.

**deploy.yml:**
```yaml
service: "myapp"
image: "myorg/myapp"

servers:
  web:
    hosts:
      - "123.45.67.89"
    labels:
      traefik.http.routers.myapp-web.rule: "Host(`myapp.example.com`)"
      traefik.http.routers.myapp-web.entrypoints: "https"
      traefik.http.routers.myapp-web.tls: "true"
      traefik.http.routers.myapp-web.tls.certresolver: "letsencrypt"

traefik:
  host_port: 80
  args:
    log.level: "DEBUG"
    entrypoints.http.address: ":80"
    entrypoints.https.address: ":443"
    certificatesresolvers.letsencrypt.acme.email: "admin@example.com"
    certificatesresolvers.letsencrypt.acme.storage: "/letsencrypt/acme.json"
    certificatesresolvers.letsencrypt.acme.httpchallenge.entrypoint: "http"
    # Use Let's Encrypt STAGING server
    certificatesresolvers.letsencrypt.acme.caserver: "https://acme-staging-v02.api.letsencrypt.org/directory"
  options:
    publish: "443:443"
    volume: "/etc/deploy4j/letsencrypt:/letsencrypt"
```

**Note:** Staging certificates are not trusted by browsers (show security warning). Remove the `caserver` line for production.

### Example 5: TLS Challenge (Alternative to HTTP Challenge)

**Scenario:** Use TLS challenge when port 80 is not available or for additional security.

**deploy.yml:**
```yaml
service: "myapp"
image: "myorg/myapp"

servers:
  web:
    hosts:
      - "123.45.67.89"
    labels:
      traefik.http.routers.myapp-web.rule: "Host(`myapp.example.com`)"
      traefik.http.routers.myapp-web.entrypoints: "https"
      traefik.http.routers.myapp-web.tls: "true"
      traefik.http.routers.myapp-web.tls.certresolver: "letsencrypt"

traefik:
  host_port: 80
  args:
    log.level: "INFO"
    entrypoints.https.address: ":443"
    # Use TLS challenge instead of HTTP challenge
    certificatesresolvers.letsencrypt.acme.email: "admin@example.com"
    certificatesresolvers.letsencrypt.acme.storage: "/letsencrypt/acme.json"
    certificatesresolvers.letsencrypt.acme.tlschallenge: "true"
  options:
    publish: "443:443"
    volume: "/etc/deploy4j/letsencrypt:/letsencrypt"
```

**Note:** TLS challenge requires port 443 to be accessible.

### Example 6: Backward Compatible HTTP-Only (No Changes)

**Scenario:** Existing HTTP-only deployments continue to work without modifications.

**deploy.yml:**
```yaml
service: "myapp"
image: "myorg/myapp"

servers:
  web:
    hosts:
      - "123.45.67.89"

traefik:
  host_port: 80
  args:
    log.level: "DEBUG"
```

**Result:** Application accessible via HTTP on port 80 (existing behavior unchanged).

## Implementation Plan

### Phase 1: No Code Changes Required ✅

The current deploy4j implementation **already supports** all required TLS configuration through existing mechanisms:

1. ✅ `args` configuration in `TraefikConfig` - Used for Traefik command-line arguments
2. ✅ `options` configuration in `TraefikConfig` - Used for Docker run options (publish, volume)
3. ✅ `labels` in servers configuration - Used for service-level routing and TLS

**Required Actions:**
- Documentation only
- Configuration examples
- User guide

### Phase 2: Enhanced User Experience (Optional Future Work)

Potential improvements for better usability:

1. **TLS Configuration Helper**
   ```yaml
   traefik:
     tls:
       enabled: true
       email: "admin@example.com"
       resolver: "letsencrypt"
       staging: false
       http_redirect: true
   ```
   
   This would auto-generate the verbose `args` and `options` configuration.

2. **Validation**
   - Validate email format for ACME
   - Check domain format in service labels
   - Warn if TLS enabled but services don't use HTTPS entrypoint

3. **CLI Commands**
   - `deploy4j traefik cert list` - List certificates
   - `deploy4j traefik cert renew` - Force certificate renewal
   - `deploy4j traefik cert status` - Check certificate expiry

4. **Default TLS Configuration**
   - Prompt for email during `deploy4j init` with TLS flag
   - Generate TLS-enabled configuration template

### Phase 3: Advanced Features (Future)

1. **DNS Challenge Support**
   - Support for DNS providers (Cloudflare, Route53, etc.)
   - Better for wildcard certificates
   - No need to expose port 80

2. **Custom CA Support**
   - Support for internal CAs
   - Self-signed certificates for development

3. **Certificate Monitoring**
   - Dashboard for certificate status
   - Expiry notifications
   - Automatic health checks

## Testing Strategy

### Local Testing Limitations

**Challenge:** Let's Encrypt requires publicly accessible domains and standard ports (80/443).

**Solutions:**

1. **Configuration Testing**
   - Verify correct Docker command generation
   - Test args/options parsing
   - Unit tests for configuration classes

2. **Staging Environment**
   - Use Let's Encrypt staging server
   - Test with real domain on test VPS ($6/month)
   - Verify certificate issuance and renewal

3. **Manual Verification**
   ```bash
   # Check Traefik configuration
   docker exec traefik cat /etc/traefik/traefik.yml
   
   # Verify certificate storage
   ls -la /etc/deploy4j/letsencrypt/
   
   # Check Traefik logs
   docker logs traefik | grep -i acme
   
   # Test HTTPS endpoint
   curl -v https://myapp.example.com
   ```

4. **Integration Tests**
   - Deploy test app with TLS config
   - Verify HTTPS accessibility
   - Check certificate validity
   - Test HTTP to HTTPS redirect

### Test Scenarios

| Scenario | Expected Result |
|----------|----------------|
| HTTP-only (no TLS config) | Works on port 80, backward compatible |
| HTTPS with Let's Encrypt | Certificate issued, HTTPS works |
| HTTP redirect to HTTPS | HTTP requests redirected |
| Multiple domains | Each domain gets certificate |
| Certificate renewal | Auto-renewal before expiry |
| Staging server | Test certificate issued (not trusted) |
| Invalid email | Error message, deployment fails |
| Missing domain | Clear error message |
| Port 80 blocked | HTTP challenge fails, clear error |
| TLS challenge | Certificate issued via port 443 |

## Documentation Requirements

### User Documentation

#### 1. Getting Started with TLS

**File:** `docs/tls-getting-started.md`

Topics:
- Prerequisites (domain, DNS, ports)
- Basic HTTPS configuration
- Testing with staging
- Going to production
- Troubleshooting common issues

#### 2. TLS Configuration Reference

**File:** `docs/tls-reference.md`

Topics:
- All Traefik TLS-related args
- Entry points configuration
- Certificate resolvers (HTTP, TLS, DNS)
- Service labels for TLS
- Examples for each challenge type

#### 3. Security Best Practices

**File:** `docs/tls-security.md`

Topics:
- Email configuration
- Certificate storage security
- Rate limiting considerations
- Staging vs production
- Certificate backup and recovery
- Monitoring and alerting

#### 4. Update README.md

Add TLS section:
```markdown
## HTTPS/TLS Support

Deploy4j supports automatic HTTPS certificate provisioning via Let's Encrypt.

**Quick Start:**
1. Point your domain to your server
2. Configure TLS in `deploy.yml`
3. Deploy with `deploy4j deploy`

See [TLS Getting Started Guide](docs/tls-getting-started.md) for details.
```

#### 5. Update Example Configuration

**File:** `deploy4j-core/src/test/resources/deployment/example-deployment.yml`

Add TLS example:
```yaml
# Example with HTTPS/TLS support
traefik:
  host_port: 80
  args:
    log.level: "INFO"
    entrypoints.http.address: ":80"
    entrypoints.https.address: ":443"
    certificatesresolvers.letsencrypt.acme.email: "admin@example.com"
    certificatesresolvers.letsencrypt.acme.storage: "/letsencrypt/acme.json"
    certificatesresolvers.letsencrypt.acme.httpchallenge.entrypoint: "http"
  options:
    publish: "443:443"
    volume: "/etc/deploy4j/letsencrypt:/letsencrypt"
```

### FAQ Section

**Q: Do I need TLS/HTTPS?**  
A: HTTPS is strongly recommended for production. Required for:
- Secure data transmission
- Modern browser features (geolocation, camera, etc.)
- SEO benefits
- User trust

**Q: What's the difference between HTTP and TLS challenge?**  
A: 
- **HTTP challenge**: Requires port 80, easier to setup
- **TLS challenge**: Requires port 443, more secure
- Choose HTTP challenge for simplicity

**Q: How much does Let's Encrypt cost?**  
A: Let's Encrypt is completely free! Rate limits apply but are generous for normal use.

**Q: Will my certificates auto-renew?**  
A: Yes! Traefik automatically renews certificates 30 days before expiry.

**Q: Can I use my own SSL certificate?**  
A: Currently not directly supported. Use Let's Encrypt or mount certificates via volumes (advanced).

**Q: What if port 80 is already in use?**  
A: Use TLS challenge instead of HTTP challenge (requires port 443 only).

**Q: How do I test without a real domain?**  
A: Use Let's Encrypt staging server. Note that staging certificates show browser warnings.

**Q: What's the rate limit for Let's Encrypt?**  
A: 50 certificates per domain per week. Use staging for testing.

## Security Considerations

### 1. Certificate Storage

**Risk:** Compromised `acme.json` file exposes private keys.

**Mitigation:**
- Traefik automatically sets permissions to 600 (owner read/write only)
- Store on encrypted filesystem if possible
- Regular backups to secure location
- Monitor file access logs

**Implementation:**
```yaml
traefik:
  options:
    volume: "/etc/deploy4j/letsencrypt:/letsencrypt"
```

Ensure `/etc/deploy4j/letsencrypt/` is:
- Owned by root
- Not world-readable
- On encrypted volume (optional but recommended)

### 2. Email Address

**Risk:** Certificate expiry notifications sent to wrong/public email.

**Best Practices:**
- Use role-based email (e.g., `ssl-admin@example.com`)
- Don't use personal email
- Monitor the inbox regularly
- Set up email forwarding to team

### 3. Let's Encrypt Rate Limits

**Risk:** Hit rate limits during testing, unable to get production certificates.

**Mitigation:**
- Always use staging for testing
- Rate limits: 50 certs/domain/week, 5 failed validations/account/hour
- Plan certificate renewals carefully
- Document rate limit handling in user guide

**Staging Configuration:**
```yaml
traefik:
  args:
    certificatesresolvers.letsencrypt.acme.caserver: "https://acme-staging-v02.api.letsencrypt.org/directory"
```

### 4. DNS Security

**Risk:** DNS hijacking leads to certificate issuance for attacker's server.

**Best Practices:**
- Use DNSSEC if possible
- Enable registrar lock
- Monitor DNS changes
- Use CAA records to restrict CAs

**CAA Record Example:**
```
example.com. CAA 0 issue "letsencrypt.org"
```

### 5. Port Exposure

**Risk:** Unnecessary port exposure increases attack surface.

**Best Practices:**
- Only expose ports 80 and 443
- Use firewall rules (iptables, ufw, security groups)
- Consider fail2ban for brute force protection

**Firewall Configuration:**
```bash
# Allow HTTP and HTTPS
ufw allow 80/tcp
ufw allow 443/tcp

# Allow SSH (be careful!)
ufw allow 22/tcp

# Enable firewall
ufw enable
```

### 6. Certificate Transparency

**Note:** All certificates are logged to public CT logs (required by Let's Encrypt).

**Implications:**
- Your domains are publicly visible
- Cannot hide infrastructure
- Monitor CT logs for unauthorized certificates

**Monitoring:**
- Use crt.sh to monitor certificates
- Set up alerts for new certificates
- Investigate unexpected certificate issuance

### 7. ACME Account Security

**Risk:** ACME account compromise allows certificate revocation or issuance.

**Best Practices:**
- Protect `acme.json` file
- Backup account credentials
- Use separate accounts for staging/production
- Rotate credentials periodically (requires new account)

### 8. TLS Configuration

**Best Practices:**
- Use TLS 1.2+ only (Traefik default)
- Use strong cipher suites (Traefik default)
- Enable HSTS (add via labels)
- Consider OCSP stapling (Traefik automatic)

**HSTS Configuration Example:**
```yaml
servers:
  web:
    labels:
      traefik.http.middlewares.hsts.headers.stsSeconds: "31536000"
      traefik.http.middlewares.hsts.headers.stsIncludeSubdomains: "true"
      traefik.http.middlewares.hsts.headers.stsPreload: "true"
      traefik.http.routers.myapp.middlewares: "hsts"
```

## Comparison with Alternatives

### Manual Certificate Management

**Pros:**
- Full control over certificates
- Can use any CA

**Cons:**
- Manual renewal required
- Error-prone
- Time-consuming
- Easy to forget renewals

**Verdict:** Not recommended. Let's Encrypt automation is superior.

### Other Reverse Proxies (nginx, Apache)

**Pros:**
- Well-known and documented
- Flexible configuration

**Cons:**
- Manual certificate setup
- More complex configuration
- No automatic Docker service discovery
- Requires nginx/Apache knowledge

**Verdict:** Traefik is better fit for Docker deployments.

### Paid SSL Certificates

**Pros:**
- Warranty/insurance
- Extended validation certificates
- Some support wildcard by default

**Cons:**
- Cost ($50-$200+/year)
- Manual management
- Not necessary for most use cases

**Verdict:** Let's Encrypt sufficient for 99% of use cases.

## Conclusion

### Summary

This proposal demonstrates that **deploy4j already supports TLS/HTTPS** through existing configuration mechanisms. No code changes are required.

### Implementation Effort

- **Code Changes:** None required ✅
- **Documentation:** 2-3 days
- **Testing:** 1-2 days with test environment
- **Total Effort:** ~1 week

### Recommendations

1. **Immediate Action:**
   - Create comprehensive documentation
   - Add examples to README
   - Update example-deployment.yml

2. **Short Term (Optional):**
   - Add TLS section to init template
   - Configuration validation
   - Better error messages

3. **Long Term (Future):**
   - Simplified TLS configuration helper
   - CLI commands for certificate management
   - Dashboard integration

### Next Steps

1. Review this proposal
2. Create documentation files
3. Update example configurations
4. Test with real domain on VPS
5. Publish documentation

---

## Appendix A: Traefik TLS Arguments Reference

Complete list of relevant Traefik TLS configuration arguments:

### Entry Points
```yaml
entrypoints.<name>.address: ":port"                          # Define entry point
entrypoints.<name>.http.tls: true                            # Enable TLS
entrypoints.<name>.http.tls.certResolver: "<resolver-name>"  # Default cert resolver
```

### Certificate Resolvers
```yaml
# Common
certificatesresolvers.<name>.acme.email: "email@example.com"
certificatesresolvers.<name>.acme.storage: "/path/acme.json"

# HTTP Challenge
certificatesresolvers.<name>.acme.httpchallenge.entrypoint: "http"

# TLS Challenge
certificatesresolvers.<name>.acme.tlschallenge: true

# DNS Challenge
certificatesresolvers.<name>.acme.dnschallenge.provider: "cloudflare"
certificatesresolvers.<name>.acme.dnschallenge.delaybeforecheck: "0"

# Staging
certificatesresolvers.<name>.acme.caserver: "https://acme-staging-v02.api.letsencrypt.org/directory"
```

### Redirections
```yaml
entrypoints.http.http.redirections.entrypoint.to: "https"
entrypoints.http.http.redirections.entrypoint.scheme: "https"
entrypoints.http.http.redirections.entrypoint.permanent: "true"
```

## Appendix B: Docker Labels Reference

Service-level TLS configuration via Docker labels:

### Basic TLS
```yaml
traefik.http.routers.<router-name>.tls: "true"
traefik.http.routers.<router-name>.tls.certresolver: "<resolver-name>"
traefik.http.routers.<router-name>.entrypoints: "https"
```

### Advanced TLS
```yaml
# Minimum TLS version
traefik.http.routers.<router-name>.tls.options: "<options-name>"

# SNI
traefik.http.routers.<router-name>.tls.domains[0].main: "example.com"
traefik.http.routers.<router-name>.tls.domains[0].sans: "*.example.com"
```

### Middlewares (Security Headers)
```yaml
# HSTS
traefik.http.middlewares.<name>.headers.stsSeconds: "31536000"
traefik.http.middlewares.<name>.headers.stsIncludeSubdomains: "true"
traefik.http.middlewares.<name>.headers.stsPreload: "true"

# Apply middleware
traefik.http.routers.<router-name>.middlewares: "<middleware-name>"
```

## Appendix C: Troubleshooting Guide

### Common Issues and Solutions

#### Issue: Certificate not issued

**Symptoms:**
- HTTPS not working
- Browser shows "connection refused"
- Traefik logs show ACME errors

**Debugging:**
```bash
# Check Traefik logs
docker logs traefik | grep -i acme

# Verify DNS resolution
nslookup myapp.example.com

# Check port accessibility
nc -zv myapp.example.com 80
nc -zv myapp.example.com 443

# Verify domain points to server
dig myapp.example.com +short
```

**Solutions:**
- Ensure DNS points to correct IP
- Verify ports 80/443 are open
- Check firewall rules
- Use staging server to test

#### Issue: Rate limit exceeded

**Symptoms:**
- Error: "too many certificates already issued"
- Cannot get new certificates

**Solutions:**
- Wait for rate limit window to reset (1 week)
- Use staging server for testing
- Review certificate issuance logs
- Plan production deployment carefully

#### Issue: HTTP challenge fails

**Symptoms:**
- ACME challenge fails
- Logs show "connection refused" or "timeout"

**Debugging:**
```bash
# Test HTTP accessibility
curl -I http://myapp.example.com/.well-known/acme-challenge/test

# Check if port 80 reaches server
nc -zv <server-ip> 80
```

**Solutions:**
- Verify port 80 is open
- Check no other service uses port 80
- Try TLS challenge instead

#### Issue: Certificates not persisting

**Symptoms:**
- New certificates issued after restart
- acme.json file empty

**Solutions:**
- Verify volume mount correct
- Check filesystem permissions
- Ensure path exists on host
- Check Traefik can write to path

#### Issue: Browser shows "not secure"

**Symptoms:**
- Certificate warning in browser
- HTTPS works but not trusted

**Possible Causes:**
- Using staging server (expected)
- Certificate for wrong domain
- Mixed content (HTTP resources on HTTPS page)

**Solutions:**
- Switch from staging to production
- Verify domain in certificate matches URL
- Fix mixed content warnings

---

**Document Version:** 1.0  
**Last Updated:** 2025-10-26  
**Author:** Deploy4j Team  
**Status:** Proposed
