---
layout: page
title: deploy4j gateway
---
Deploy4j uses Gateway to proxy requests to the application containers, allowing us to have zero-downtime deployments.

```shell
Usage: deploy4j gateway [--help] [COMMAND]
Manage Gateway load balancer
      --help   Display help about a command
Commands:
  boot              Boot Gateway on servers
  reboot            Reboot Gateway on servers (stop container, remove
                      container, start new container)
  start             Start existing Gateway container on servers
  stop              Stop existing Gateway container on servers
  restart           Restart existing Gateway container on servers
  details           Show details about Gateway container from servers
  logs              Show log lines from Gateway on servers
  remove            Remove Gateway container and image from servers
  remove_container  Remove Gateway container from servers
  remove_image      Remove Gateway image from servers
```

When you want to upgrade Gateway, or change it’s configuration, you can call `deploy4j gateway reboot`. This is going to cause a small outage on each server and will prompt for confirmation.

You can use a rolling reboot with `deploy4j gateway reboot --rolling` to avoid restarting on all servers simultaneously.