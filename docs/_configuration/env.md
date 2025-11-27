---
layout: page
title: Environment variables
---
Environment variables can be set directly in the deploy4j configuration or read from `.deploy4j/secrets`. Those secrets should not be checked into Git!

## Reading environment variables from the configuration

Environment variables can be set directly in the configuration file. 

These are passed to the `docker run` command when deploying.

```yaml
env:
  DATABASE_HOST: mysql-db1
  DATABASE_PORT: 3306
```

## Secrets

deploy4j automatically loads environment variables set in the `.deploy4j/secrets` file. 

If you are using destinations, secrets will instead be read from `.deploy4j/secrets.<DESTINATION>` if it exists.

Common secrets across all destinations can be set in `.deploy4j/secrets-common`.

Secrets can also be set in the environment where deploy4j is run.

This file can be used to set variables like `DEPLOY4J_REGISTRY_PASSWORD` or database passwords. 

Ensure `secret` files are not checked into Git or included in your Dockerfile. Example `.deploy4j/secrets` contents:

```text
DEPLOY4J_REGISTRY_PASSWORD=pw
DB_PASSWORD=secret123
```

To mark secrets in the configuration and keep them out of the clear values, list them under `secret` and move other variables under `clear`:

Unlike clear values, secrets are not passed directly to the container, but are stored in an env file on the host.

```yaml
env:
  clear:
    DB_USER: app
  secret:
    - DB_PASSWORD
```

## Aliased secrets

You can also alias secrets to other secrets using a `:` separator.

This is useful when the ENV name is different from the secret name. For example, if you have two places where you need to define the ENV variable `DB_PASSSWORD`, but the value is different depending on the context.

```properties
MAIN_DB_PASSWORD=***
SECONDARY_DB_PASSWORD=***
```

```yaml
env:
  secret:
    - DB_PASSWORD:MAIN_DB_PASSWORD
accessories:
  main_db_accessory:
    env:
      secret:
        DB_PASSWORD:MAIN_DB_PASSWORD
  secondary_db_password:
    env:
      secret:
        DB_PASSWORD:SECONDARY_DB_PASSWORD
```

## Tags

Tags are used to add extra env variables to specific hosts. See [servers]({{ 'servers' | relative_url }}) for how to tag hosts.

Tags are only allowed in the top level env configuration (i.e not under a role specific env).

The env variables can be specified with secret and clear values as explained above.

```yaml
env:
  tags:
    <tag1>:
      MYSQL_USER: monitoring
    <tag2>:
      clear:
        MYSQL_USER: readonly
      secret:
        - MYSQL_PASSWORD
```

## Example configuration

```yaml
env:
  clear:
    MYSQL_USER: app
  secret:
    - MYSQL_PASSWORD
  tags:
    monitoring:
      MYSQL_USER: monitoring
    replica:
      clear:
        MYSQL_USER: readonly
      secret:
        - READONLY_PASSWORD
```
