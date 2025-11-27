---
layout: page
title: pre-app-boot
---
Run before booting the app container when you call `deploy4j app boot`, or indirectly via `deploy4j deploy`.

With a grouped boot strategy, the hook will be called once for each group, with `DEPLOY4J_HOSTS` containing a list of servers in the group.

The [post-app-deploy]({{ 'hooks/post-app-boot' | relative_url }}) will be called after the boot completes, again once per deployment group.