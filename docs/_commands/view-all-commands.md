---
layout: page
title: View all commands
default: true
date: 2025-11-03
---
You can view all of the commands by running `deploy4j --help`

```shell
Usage: deploy4j [--help] [COMMAND]
Deploy web apps anywhere. From bare metal to cloud VMs.
      --help   Display help about a command
Commands:
  accessory  Manage accessories (db/redis/search)
  app        Manage application
  audit      Show audit log from servers
  build      Build application image
  config     Show combined config (including secrets!)
  deploy     Deploy app to servers
  details    Show details about all containers
  init       Create config stub in config/deploy.yml and env stub in .
               deploy4j/secrets
  lock       Manage the deploy lock
  prune      Prune old application images and containers
  redeploy   Deploy app to servers without bootstrapping servers, starting
               Traefik, pruning, and registry login
  registry   Login and out of the image registry
  remove     Remove Traefik, app, accessories, and registry session from servers
  rollback   Rollback app to VERSION
  server     Boostrap servers with curl and Docker
  setup      Setup all accessories, push the env, and deploy app to servers
  test       Test connectivity to servers
  traefik    Manage Traefik load balancer
  version    Show Deploy4j version
```