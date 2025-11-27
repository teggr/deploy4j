---
layout: page
title: Secrets
---
```shell
Usage: secrets [--help] [COMMAND]
Helpers for extracting secrets
      --help   Display help about a command
Commands:
  print  Print the secrets (for debugging)
```

Substituting environment variables is supported in `.kamal/secrets`. Use the `$ENV_VAR_NAME` syntax to reference environment variables.

```shell
# .kamal/secrets

SECRETS=$MY_BIG_SECRET
```