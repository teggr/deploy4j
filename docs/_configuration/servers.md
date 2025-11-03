---
layout: page
title: Servers
---
Servers are split into different roles, with each role having its own configuration.

For simpler deployments where all servers are identical, you can just specify a list of servers which will be implicitly assigned to the `web` role.

```yaml
servers:
  - 172.0.0.1
  - 172.0.0.2
  - 172.0.0.3
```

### Tagging servers

Servers can be tagged, with the tags used to add custom env variables. Example:

```yaml
servers:
  - 172.0.0.1
  - 172.0.0.2: experiments
  - 172.0.0.3: [ experiments, three ]
```

### Roles

For more complex deployments you can specify roles and configure each separately. Example:

```yaml
servers:
  web:
    ...
  workers:
    ...
```
