---
layout: page
title: deploy4j lock
---
Manage deployment locks.

Commands that are unsafe to run concurrently will take a deploy lock while they run. The lock is the `deploy4j_lock` directory on the primary server.

You can manage them directly — for example clearing a leftover lock from a failed command or preventing deployments during a maintanence window.

```shell
Usage: lock [--help] [COMMAND]
Manage the deploy lock
      --help   Display help about a command
Commands:
  status   Report lock status
  acquire  Acquire the deploy lock
  release  Release the deploy lock
```

Example:

```shell

```