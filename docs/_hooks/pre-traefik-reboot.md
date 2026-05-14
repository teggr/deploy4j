---
layout: page
title: pre-gateway-reboot
---
Run before rebooting the Gateway container, when you call `deploy4j gateway reboot`.

If you have the hook disable the current server in an external load balancer and use the –rolling flag, you can use this for a zero-downtime Gateway reboot.

With a rolling reboot hook will be called once for each server, with `DEPLOY4J_HOSTS` containing the current server. With a non-rolling reboot it will be called just once.

Use the [post-gateway-reboot hook]({{ 'hooks/post-gateway-reboot' | relative_url }}) to re-enable the server.