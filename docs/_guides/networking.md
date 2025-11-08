---
layout: page
title: Networking
---

This guide demonstrates some networking tips.

## Referencing accessories

When referencing accessories such as databases, Redis, or search services from your application, there are a couple of approaches you can adopt.

### Using the ip address of the host

Use the host's IP address in your environment variables to connect to the accessory services.

```yaml
servers:
  - 138.68.182.132
# Environment variables for the Spring Boot application.
# We are configuring the database connection here.
env:
  clear:
    SPRING_DATASOURCE_URL: jdbc:postgresql://138.68.182.132:5432/testdb
    SPRING_DATASOURCE_USERNAME: testuser
    SPRING_DATASOURCE_PASSWORD: testpass
# Accessories configuration for the Postgres database.
accessories:
  db:
    image: postgres:18-alpine
    host: 138.68.182.132
    port: 5432
    env:
      clear:
        POSTGRES_USER: testuser
        POSTGRES_PASSWORD: testpass
        POSTGRES_DB: testdb
    directories:
      - data:/var/lib/postgresql/18/docker
```

### Use roles to add the internal docker host

Add the `add-host` option to the server configuration to map `host.docker.internal` to the host gateway. This allows containers to reference services running on the host machine.

```yaml
servers:
  web:
    hosts:
      - 138.68.182.132
    options:
      "add-host": host.docker.internal:host-gateway
  
# Environment variables for the Spring Boot application.
# We are configuring the database connection here.
env:
  clear:
    SPRING_DATASOURCE_URL: jdbc:postgresql://host.docker.internal:5432/testdb
    SPRING_DATASOURCE_USERNAME: testuser
    SPRING_DATASOURCE_PASSWORD: testpass
# Accessories configuration for the Postgres database.
accessories:
  db:
    image: postgres:18-alpine
    host: 138.68.182.132
    port: 5432
    env:
      clear:
        POSTGRES_USER: testuser
        POSTGRES_PASSWORD: testpass
        POSTGRES_DB: testdb
    directories:
      - data:/var/lib/postgresql/18/docker
```
### Create a user-defined network

You can create a user-defined network and attach both the application and accessories to that network. This allows you to reference accessories by their container name.

```yaml
servers:
  web:
    hosts:
      - 138.68.182.132
    options:
      "network": "my-network"
# traefik options
traefik:
  options:
    "network": "my-network"
# Environment variables for the Spring Boot application.
# We are configuring the database connection here.
env:
  clear:
    SPRING_DATASOURCE_URL: jdbc:postgresql://<app-name>-db:5432/testdb
    SPRING_DATASOURCE_USERNAME: testuser
    SPRING_DATASOURCE_PASSWORD: testpass
# Accessories configuration for the Postgres database.
accessories:
  db:
    image: postgres:18-alpine
    host: 138.68.182.132
    port: 5432
    env:
      clear:
        POSTGRES_USER: testuser
        POSTGRES_PASSWORD: testpass
        POSTGRES_DB: testdb
    directories:
      - data:/var/lib/postgresql/18/docker
    options:
      "network": "my-network"
```

The network can be attached use the pre-deploy hook `.deploy4j/hooks/pre-deploy`:

```shell
#!/usr/bin/env bash

REMOTE_HOST="user@host"
NETWORK_NAME="my-network"

# SSH into the remote host and execute Docker commands
ssh "$REMOTE_HOST" << EOF
    # Check if the Docker network already exists
    if ! docker network inspect "$NETWORK_NAME" &>/dev/null; then
        # If it doesn't exist, create it
        docker network create "$NETWORK_NAME"
        echo "Created Docker network: $NETWORK_NAME"
    else
        echo "Docker network $NETWORK_NAME already exists, skipping creation."
    fi
EOF
```