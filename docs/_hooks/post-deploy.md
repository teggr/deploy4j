---
layout: page
title: post-deploy
---
Run after a deploy, redeploy or rollback. This hook is also passed a `DEPLOY4J_RUNTIME` env variable set to the total seconds the deploy took.

This could be used to broadcast a deployment message, or register the new version with an APM.

The command could look something like:

```shell
#!/usr/bin/env bash
curl -q -d content="[My App] ${DEPLOY4J_PERFORMER} Rolled back to version ${DEPLOY4J_VERSION}" https://3.basecamp.com/XXXXX/integrations/XXXXX/buckets/XXXXX/chats/XXXXX/lines
```

That will post a line like the following to a preconfigured chatbot in Basecamp:

```text
[My App] [dhh] Rolled back to version d264c4e92470ad1bd18590f04466787262f605de
```