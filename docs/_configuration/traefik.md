---
layout: page
title: Gateway
---
Gateway is a reverse proxy used by deploy4j for zero-downtime deployments. We run it in its own container on the hosts.

During a deployment:
1. A new container is started which Gateway detects via labels
2. Gateway routes traffic to the new container
3. The old container is forced to fail its healthcheck, so Gateway stops routing to it
4. The old container is stopped

## Gateway settings

Configured under `gateway` in the root configuration. Example:

```yaml
gateway:

  # Image
  #
  # The Gateway image to use, defaults to `gateway:v2.11`
  image: gateway:v2.11

  # Host port
  #
  # The host port to publish the Gateway container on, defaults to `80`
  host_port: "8080"

  # Disabling publishing
  #
  # To avoid publishing the Gateway container, set this to `false`
  publish: false

  # Labels
  #
  # Additional labels to apply to the Gateway container
  labels:
    gateway.http.routers.catchall.entryPoints: http
    gateway.http.routers.catchall.rule: PathPrefix(`/`)
    gateway.http.routers.catchall.service: unavailable
    gateway.http.routers.catchall.priority: "1"
    gateway.http.services.unavailable.loadbalancer.server.port: "0"

  # Arguments
  #
  # Additional arguments to pass to the Gateway container
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
