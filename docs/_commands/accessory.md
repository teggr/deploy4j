---
layout: page
title: deploy4j accessory
---
Accessories are long-lived services that your app depends on. They are not updated when you deploy.

They are not proxied, so rebooting will have a small period of downtime. You can map volumes from the host server into your container for persistence across reboots.

Run `deploy4j accessory` to view and manage your accessories.

```shell
Usage: accessory [--help] [COMMAND]
Manage accessories (db/redis/search)
      --help   Display help about a command
Commands:
  boot         Boot new accessory service on host (use NAME=all to boot all
                 accessories)
  upload       Upload accessory files to host
  directories  Create accessory directories on host
  reboot       Reboot existing accessory on host (stop container, remove
                 container, start new container; use NAME=all to boot all
                 accessories)
  start        Start existing accessory container on host
  stop         Stop existing accessory container on host
  restart      Restart existing accessory container on host
  details      Show details about accessory on host (use NAME=all to show all
                 accessories)
  exec         Execute a custom command on servers (use --help to show options)
  logs         Show log lines from app on servers (use --help to show options)
  remove       Remove accessory container, image and data directory from host
                 (use NAME=all to remove all accessories)
```

To update an accessory, update the image in your config and run `deploy4j accessory reboot [NAME]`.

Example:

```shell

```