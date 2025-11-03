---
layout: page
title: deploy4j app
---
Run `deploy4j app` to manage your running apps.

To deploy new versions of the app, see `deploy4j deploy` and `deploy4j rollback`.

You can use `deploy4j app exec` to [run commands on servers](/commands/running-commands-on-servers/).

```shell
Usage: app [--help] [COMMAND]
Manage application
      --help   Display help about a command
Commands:
  boot               Boot app on servers (or reboot app if already running)
  start              Start existing app container on servers
  stop               Stop app container on servers
  details            Show details about app containers
  exec               Execute a custom command on servers within the app
                       container (use --help to show options)
  containers         Show app containers on servers
  stale_containers   Detect app stale containers
  images             Show app images on servers
  logs               Show log lines from app on servers (use --help to show
                       options)
  remove             Remove app containers and images from servers
  remove_container   Remove app container with given version from servers
  remove_containers  Remove all app containers from servers
  remove_images      Remove all app images from servers
  version            Show app version currently running on servers
```