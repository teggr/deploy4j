---
layout: page
title: Traefik
---
Traefik is a reverse proxy used by deploy4j for zero-downtime deployments. We run it in its own container on the hosts.

During a deployment:
1. A new container is started which Traefik detects via labels
2. Traefik routes traffic to the new container
3. The old container is forced to fail its healthcheck, so Traefik stops routing to it
4. The old container is stopped

## Traefik settings

Configured under `traefik` in the root configuration. Example:

```yaml
traefik:

  # Image
  #
  # The Traefik image to use, defaults to `traefik:v2.11`
  image: traefik:v2.11

  # Host port
  #
  # The host port to publish the Traefik container on, defaults to `80`
  host_port: "8080"

  # Disabling publishing
  #
  # To avoid publishing the Traefik container, set this to `false`
  publish: false

  # Labels
  #
  # Additional labels to apply to the Traefik container
  labels:
    traefik.http.routers.catchall.entryPoints: http
    traefik.http.routers.catchall.rule: PathPrefix(`/`)
    traefik.http.routers.catchall.service: unavailable
    traefik.http.routers.catchall.priority: "1"
    traefik.http.services.unavailable.loadbalancer.server.port: "0"

  # Arguments
  #
  # Additional arguments to pass to the Traefik container
  args:
    entryPoints.http.address: ":80"
    entryPoints.http.forwardedHeaders.insecure: true
    accesslog: true
    accesslog.format: json

  # Options
  #
  # Additional options to pass to `docker run`
  options:
    cpus: 2

  # Environment variables
  #
  # See deploy4j docs env
  env:
    ...

```
