---
layout: page
title: Booting
aside:
  group: configuration
---
When deploying to large numbers of hosts, you might prefer not to restart your services on every host at the same time.

deploy4j’s default is to boot new containers on all hosts in parallel. You can control this with the boot configuration.

## Fixed group sizes

Boot a fixed number of hosts at a time. Example:

```yaml
boot:
  limit: 2
  wait: 10
```

## Percentage of hosts

Boot a percentage of hosts at a time. Example:

```yaml
boot:
  limit: 25%
  wait: 2
```
