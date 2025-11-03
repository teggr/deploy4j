---
layout: page
title: deploy4j traefik
---
Deploy4j uses Traefik to proxy requests to the application containers, allowing us to have zero-downtime deployments.

```shell
Usage: deploy4j traefik [--help] [COMMAND]
Manage Traefik load balancer
      --help   Display help about a command
Commands:
  boot              Boot Traefik on servers
  reboot            Reboot Traefik on servers (stop container, remove
                      container, start new container)
  start             Start existing Traefik container on servers
  stop              Stop existing Traefik container on servers
  restart           Restart existing Traefik container on servers
  details           Show details about Traefik container from servers
  logs              Show log lines from Traefik on servers
  remove            Remove Traefik container and image from servers
  remove_container  Remove Traefik container from servers
  remove_image      Remove Traefik image from servers
```

When you want to upgrade Traefik, or change it’s configuration, you can call `deploy4j traefik reboot`. This is going to cause a small outage on each server and will prompt for confirmation.

You can use a rolling reboot with `deploy4j traefik reboot --rolling` to avoid restarting on all servers simultaneously.