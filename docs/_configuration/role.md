---
layout: page
title: Roles
---
Roles are used to configure different types of servers in the deployment. The most common use is to run web servers and job servers.

deploy4j expects there to be a `web` role, unless you set a different `primary_role` in the root configuration.

## Role configuration

Roles are specified under the `servers` key. Examples:

```yaml
servers:

  # Simple role configuration
  #
  #
  # This can be a list of hosts, if you don't need custom configuration for the role.
  #
  # You can set tags on the hosts for custom env variables (see deploy4j docs env)
  web:
    - 172.1.0.1
    - 172.1.0.2: experiment1
    - 172.1.0.2: [ experiment1, experiment2 ]

  # Custom role configuration
  #
  # When there are other options to set, the list of hosts goes under the `hosts` key
  #
  # By default only the primary role uses Gateway, but you can set `gateway` to change
  # it.
  #
  # You can also set a custom cmd to run in the container, and overwrite other settings
  # from the root configuration.
  workers:
    hosts:
      - 172.1.0.3
      - 172.1.0.4: experiment1
    gateway: true
    cmd: "bin/jobs"
    options:
      memory: 2g
      cpus: 4
    healthcheck:
      ...
    logging:
      ...
    labels:
      my-label: workers
    env:
      ...
    spring_boot:
      actuator_port: 8081
      actuator_base_path: /custom/actuator
```
