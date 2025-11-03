---
layout: page
title: deploy4j server
---
```shell
Usage: deploy4j server [--help] [COMMAND]
Boostrap servers with curl and Docker
      --help   Display help about a command
Commands:
  exec       Run a custom command on the server (use --help to show options)
  bootstrap  Set up Docker to run deploy4j apps
```

## Bootstrap server

You can run `deploy4j server bootstrap` to setup Docker on your hosts.

It will check if Docker is installed and if not it will attempt to install it via get.docker.com.

```shell
$ deploy4j server bootstrap
```

## Execute command on all servers

Run a custom command on all servers.

```shell
$ deploy4j server exec "date"
Running 'date' on 867.53.0.9...
INFO [e79c62bb] Running /usr/bin/env date on 867.53.0.9
INFO [e79c62bb] Finished in 0.247 seconds with exit status 0 (successful).
App Host: 867.53.0.9
Thu Jun 13 08:06:19 AM UTC 2024
```

## Execute command on primary server

Run a custom command on the primary server.

```shell
$ deploy4j server exec --primary "date"
Running 'date' on 867.53.0.9...
  INFO [8bbeb21a] Running /usr/bin/env date on 867.53.0.9
  INFO [8bbeb21a] Finished in 0.265 seconds with exit status 0 (successful).
App Host: 867.53.0.9
Thu Jun 13 08:07:09 AM UTC 2024
```