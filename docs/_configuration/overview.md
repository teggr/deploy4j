---
layout: page
title: deploy4j Configuration
default: true
short_title: Overview
date: 2025-11-03
---
Configuration is read from the `config/deploy.yml`.

## Destinations

When running commands, you can specify a destination with the `-d` flag, e.g. `deploy4j deploy -d staging`.
In that case the configuration will also be read from `config/deploy.staging.yml` and merged with the base configuration.

## Extensions

deploy4j will not accept unrecognized keys in the configuration file. You can declare extension blocks by prefixing them with `x-` to avoid errors; deploy4j will ignore keys with that prefix.

## Example configuration

```yaml
# The service name
# This is a required value. It is used as the container name prefix.
service: myapp

# The Docker image name
#
# The image will be pushed to the configured registry.
image: my-image

# Labels
#
# Additional labels to add to the container
labels:
  my-label: my-value

# Additional volumes to mount into the container
volumes:
  - /path/on/host:/path/in/container:ro

# Registry
#
# The Docker registry configuration, see deploy4j docs registry
registry:
  ...

# Servers
#
# The servers to deploy to, optionally with custom roles, see deploy4j docs servers
servers:
  ...

# Environment variables
#
# See deploy4j docs env
env:
  ...

# Path to hooks, defaults to `.deploy4j/hooks`
# See http://localhost:4000/deploy4j/hooks/overview for more information
hooks_path: /user_home/deploy4j/hooks

# Require destinations
#
# Whether deployments require a destination to be specified, defaults to `false`
require_destination: true

# The primary role
#
# This defaults to `web`, but if you have no web role, you can change this
primary_role: workers

# Allowing empty roles
#
# Whether roles with no servers are allowed. Defaults to `false`.
allow_empty_roles: false

# Stop wait time
#
# How long we wait for a container to stop before killing it, defaults to 30 seconds
stop_wait_time: 60

# Retain containers
#
# How many old containers and images we retain, defaults to 5
retain_containers: 3

# Minimum version
#
# The minimum version of deploy4j required to deploy this configuration, defaults to nil
minimum_version: 1.3.0

# Readiness delay
#
# Seconds to wait for a container to boot after is running, default 7
# This only applies to containers that do not specify a healthcheck
readiness_delay: 4

# Run directory
#
# Directory to store deploy4j runtime files in on the host, default `.deploy4j`
run_directory: /etc/deploy4j

# SSH options
#
# See deploy4j docs ssh
ssh:
  ...

# Builder options
#
# See deploy4j docs builder
builder:
  ...

# Accessories
#
# Additionals services to run in Docker, see deploy4j docs accessory
accessories:
  ...

# Traefik
#
# The Traefik proxy is used for zero-downtime deployments, see deploy4j docs traefik
traefik:
  ...

# SSHKit
#
# See deploy4j docs sshkit
sshkit:
  ...

# Boot options
#
# See deploy4j docs boot
boot:
  ...

# Healthcheck
#
# Configuring healthcheck commands, intervals and timeouts, see deploy4j docs healthcheck
healthcheck:
  ...

# Logging
#
# Docker logging configuration, see deploy4j docs logging
logging:
  ...
```
