---
layout: page
title: Healthcheck configuration
---
By default, no healthcheck is made when launching an application. We wait for the container to reach a running state and then pause for the readiness delay.

On roles that are running Traefik and supply a healthcheck, deploy4j will supply the healthcheck to the `docker run` command.

The default healthcheck is `curl -f http://localhost:<port>/<path>`, with a backup of `wget`.

## Healthcheck options

These go under the `healthcheck` key in the root or role configuration. Example:

```yaml
healthcheck:

  # Command
  #
  # The command to run, defaults to `curl -f http://localhost:<port>/<path>` on roles running Traefik
  cmd: "curl -f http://localhost"

  # Interval
  #
  # The Docker healthcheck interval, defaults to `1s`
  interval: 10s

  # Max attempts
  #
  # The maximum number of times we poll the container to see if it is healthy, defaults to `7`
  # Each check is separated by an increasing interval starting with 1 second.
  max_attempts: 3

  # Port
  #
  # The port to use in the healthcheck, defaults to `8080`
  port: "8080"

  # Path
  #
  # The path to use in the healthcheck, defaults to `/actuator/health`
  path: /actuator/health

  # Cords for zero-downtime deployments
  #
  # The cord file is used for zero-downtime deployments. The healthcheck is augmented with a check
  # for the existance of the file. This allows us to delete the file and force the container to
  # become unhealthy, causing Traefik to stop routing traffic to it.
  #
  # deploy4j mounts a volume at this location and creates the file before starting the container.
  # You can set the value to `false` to disable the cord file, but this loses the zero-downtime
  # guarantee.
  #
  # The default value is `/tmp/deploy4j-cord`
  cord: /cord

  # Log lines
  #
  # Number of lines to log from the container when the healthcheck fails, defaults to `50`
  log_lines: 100
```

The `cord` file is used for zero-downtime deployments and is created by deploy4j before starting the container. Set it to `false` to disable the cord file.
