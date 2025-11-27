---
layout: page
title: deploy4j Configuration
default: true
short_title: Hooks Overview
date: 2025-11-03
---
You can run custom scripts at specific points with hooks.

Hooks should be stored in the **.deploy4j/hooks** folder. Running `deploy4j init` will build that folder and add some sample scripts.

You can change their location by setting `hooks_path` in the configuration file.

If the script returns a non-zero exit code the command will be aborted.

`DEPLOY4J_* environment` variables are available to the hooks command for fine-grained audit reporting, e.g. for triggering deployment reports or firing a JSON webhook. These variables include:

* `DEPLOY4J_RECORDED_AT` — UTC timestamp in ISO 8601 format, e.g. 2023-04-14T17:07:31Z
* `DEPLOY4J_PERFORMER` — The local user performing the command (from whoami)
* `DEPLOY4J_SERVICE` — The service name, e.g. app
* `DEPLOY4J_SERVICE_VERSION` — An abbreviated service and version for use in messages, e.g. app@150b24f
* `DEPLOY4J_VERSION` — The full version being deployed
* `DEPLOY4J_HOSTS` — A comma-separated list of the hosts targeted by the command
* `DEPLOY4J_COMMAND` — The command we are running
* `DEPLOY4J_SUBCOMMAND` — Optional: The subcommand we are running
* `DEPLOY4J_DESTINATION` — Optional: Destination, e.g. “staging”
* `DEPLOY4J_ROLE` — Optional: Role targeted, e.g. “web”

The available hooks are:

* [docker-setup]({{ 'hooks/docker-setup' | relative_url }})
* [pre-connect]({{ 'hooks/pre-connect' | relative_url }})
* [pre-deploy]({{ 'hooks/pre-deploy' | relative_url }})
* [post-deploy]({{ 'hooks/post-deploy' | relative_url }})
* [pre-app-boot]({{ 'hooks/pre-app-boot' | relative_url }})
* [post-app-deploy]({{ 'hooks/post-app-boot' | relative_url }})
* [pre-traefik-reboot]({{ 'hooks/pre-traefik-reboot' | relative_url }})
* [post-traefik-reboot]({{ 'hooks/post-traefik-reboot' | relative_url }})

You can pass `--skip_hooks` to avoid running the hooks.

Note: The hook filename must be the hook name without any extension (might need .bat for windows). For example, the pre-deploy hook should be named “pre-deploy” (without any file extension such as .sh or .bat).