---
layout: page
title: deploy4j rollback
---
You can rollback a deployment with `deploy4j rollback`.

If you’ve discovered a bad deploy, you can quickly rollback to a previous image. You can see what old containers are available for rollback by running `deploy4j app containers -q`. It’ll give you a presentation similar to `deploy4j app details`, but include all the old containers as well.

```shell

```

From the example above, we can see that e5d9d7c2b898289dfbc5f7f1334140d984eedae4 was the last version, so it’s available as a rollback target. We can perform this rollback by running `deploy4j rollback e5d9d7c2b898289dfbc5f7f1334140d984eedae4`.

That’ll stop 6ef8a6a84c525b123c5245345a8483f86d05a123 and then start a new container running the same image as e5d9d7c2b898289dfbc5f7f1334140d984eedae4. Nothing to download from the registry.

Note: By default old containers are pruned after 3 days when you run `deploy4j deploy`.