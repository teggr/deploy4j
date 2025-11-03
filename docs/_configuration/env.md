---
layout: page
title: Environment variables
---
Environment variables can be set directly in the deploy4j configuration or loaded from a `.env` file for secrets that should not be checked into Git.

## Reading environment variables from the configuration

Environment variables can be set directly in the configuration file. These are passed to the `docker run` command when deploying.

```yaml
env:
  DATABASE_HOST: mysql-db1
  DATABASE_PORT: 3306
```

## Using a .env file to load secrets

deploy4j uses dotenv to automatically load environment variables set in the `.env` file present in the application root. 

This file can be used to set variables like `DEPLOY4J_REGISTRY_PASSWORD` or database passwords. Ensure `.env` files are not checked into Git or included in your Dockerfile. Example `.env` contents:

```text
DEPLOY4J_REGISTRY_PASSWORD=pw
DB_PASSWORD=secret123
```

To mark secrets in the configuration and keep them out of the clear values, list them under `secret` and move other variables under `clear`:

Unlike clear values, secrets are not passed directly to the container, but are stored in an env file on the host. The file is not updated when deploying, only when running `deploy4j envify` or `deploy4j env push`.

```yaml
env:
  clear:
    DB_USER: app
  secret:
    - DB_PASSWORD
```

## Tags

Tags are used to add extra env variables to specific hosts. Tags are only allowed in the top level env configuration. Example:

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
