---
layout: page
title: deploy4j deploy
---
Deploy your app to all servers.

Deploy4j will use the Gateway proxy to seamlessly move requests from the old version of the app to new without downtime.

The deployment process is:

1. Login into the Docker registry on all servers
2. Pull the app version onto the servers
3. Ensure Gateway is booted
4. Check the image boots on one server
5. Detect and stop any stale containers
6. Boot the new container and stop the old one
7. Prune old containers and images

In some cases, you may want to skip the pull step. Use the `-P` flag to skip pulling the image on the servers. Use this option when:

1. You have manually pushed a development version onto the server and don't want to pull from the registry.
2. You have created a local version and are sharing your local Docker daemon with a local container for testing purposes.

```shell
Usage: deploy4j deploy [-HpPqv] [--help] [-c=CONFIG_FILE] [-d=DESTINATION]
                       [--version=VERSION] [-h=HOSTS]... [-r=ROLES]...
Deploy app to servers
  -c, --config-file=CONFIG_FILE
                          Path to config file. Default: config/deploy.yml
  -d, --destination=DESTINATION
                          Specify destination to be used for config file
                            (staging -> deploy.staging.yml)
  -h, --hosts=HOSTS       Run commands on these hosts instead of all (separate
                            by comma, supports wildcards with *)
  -H, --skip-hooks        Don't run hooks, Default: false
      --help              Display help about a command
  -p, --[no-]primary      Run commands only on primary host instead of all
  -P                      Skip image pull
  -q, --[no-]quiet        Minimal logging
  -r, --roles=ROLES       Run commands on these roles instead of all (separate
                            by comma, supports wildcards with *)
  -v, --[no-]verbose      Detailed logging
      --version=VERSION   Run commands against a specific app version
```