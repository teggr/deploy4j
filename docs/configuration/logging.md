---
layout: page
title: Custom logging configuration
aside:
  group: configuration
  title: Logging
---
Set these to control the Docker logging driver and options.

## Logging settings

These go under the `logging` key in the configuration file. Example:

This can be specified in the root level or for a specific role.

```yaml
logging:

  # Driver
  #
  # The logging driver to use, passed to Docker via `--log-driver`
  driver: json-file

  # Options
  #
  # Any logging options to pass to the driver, passed to Docker via `--log-opt`
  options:
    max-size: 100m
```
