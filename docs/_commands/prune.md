---
layout: page
title: deploy4j prune
---
Prune old containers and images.

Deploy4j keeps the last 5 deployed containers and the images they are using. Pruning deletes all older containers and images.

```shell
Usage: deploy4j prune [--help] [COMMAND]
Prune old application images and containers
      --help   Display help about a command
Commands:
  all         Prune unused images and stopped containers
  images      Prune unused images
  containers  Prune all stopped containers, except the last n (default 5)
```