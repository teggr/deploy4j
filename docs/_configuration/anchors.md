---
layout: page
title: Anchors
---
You can re-use parts of your deploy4j configuration by defining them as anchors and referencing them with aliases.

For example, you might need to define a shared healthcheck for multiple worker roles. Anchors begin with x- and are defined at the root level of your deploy.yml file.

```yaml
x-worker-healthcheck: &worker-healthcheck
  cmd: bin/worker-healthcheck
  path: /up
```

To use this anchor in your deploy configuration, reference it via the alias.

```yaml
service: my-app
registry:
  server: registry.example.com
  username: baseuser
healthcheck:
  <<: *worker-healthcheck
```