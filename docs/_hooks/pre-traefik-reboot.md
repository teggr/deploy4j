---
layout: page
title: pre-traefik-reboot
---
Run before rebooting the Traefik container, when you call `deploy4j traefik reboot`.

If you have the hook disable the current server in an external load balancer and use the –rolling flag, you can use this for a zero-downtime Traefik reboot.

With a rolling reboot hook will be called once for each server, with `DEPLOY4J_HOSTS` containing the current server. With a non-rolling reboot it will be called just once.

Use the [post-traefik-reboot hook]({{ 'hooks/post-traefik-reboot' | relative_url }}) to re-enable the server.