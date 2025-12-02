---
layout: page
title: deploy4j spring_boot_admin
---
Deploy4j can manage the Spring Boot Admin dashboard for monitoring and managing your Spring Boot applications.

```shell
Usage: deploy4j spring_boot_admin [--help] [COMMAND]
Manage Spring Boot Admin dashboard
      --help   Display help about a command
Commands:
  boot              Boot Spring Boot Admin on servers
  reboot            Reboot Spring Boot Admin on servers (stop container, remove
                      container, start new container)
  start             Start existing Spring Boot Admin container on servers
  stop              Stop existing Spring Boot Admin container on servers
  restart           Restart existing Spring Boot Admin container on servers
  details           Show details about Spring Boot Admin container from servers
  logs              Show log lines from Spring Boot Admin on servers
  remove            Remove Spring Boot Admin container and image from servers
  remove_container  Remove Spring Boot Admin container from servers
  remove_image      Remove Spring Boot Admin image from servers
```

When you want to upgrade Spring Boot Admin, or change its configuration, you can call `deploy4j spring_boot_admin reboot`. This is going to cause a small outage on each server and will prompt for confirmation.

You can use a rolling reboot with `deploy4j spring_boot_admin reboot --rolling` to avoid restarting on all servers simultaneously.
