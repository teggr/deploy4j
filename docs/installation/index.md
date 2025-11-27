---
layout: page
title: Installation
aside: 
  group: installation
  index: true
---

If you have JBang installed, you can install `deploy4j` as an application.

```shell
jbang app install --name deploy4j --force --fresh --repos jitpack=https://jitpack.io com.github.teggr.deploy4j:deploy4j-cli:0.0.2
```

Then, inside your app directory, run `deploy4j init`. Now edit the new file config/deploy.yml. It could look as simple as this:

```yaml
service: hey
image: 37s/hey
servers:
  - 192.168.0.1
  - 192.168.0.2
registry:
  username: registry-user-name
  password:
    - DEPLOY4J_REGISTRY_PASSWORD
env:
  secret:
    - DB_PASSWORD
```

Edit your `.deploy4j/secrets` file to declare the `DEPLOY4J_REGISTRY_PASSWORD`. You may also set the variable in environment.

```properties
DEPLOY4J_REGISTRY_PASSWORD=***
DB_PASSWORD=***
```

Now you’re ready to deploy to the servers:

```shell
deploy4j setup
```

This will:

1. Connect to the servers over SSH (using root by default, authenticated by your ssh key).
2. Install Docker and curl on any server that might be missing it (using get.docker.com): root access is needed via ssh for this.
3. Log into the registry remotely.
4. Pull the image from the registry onto the servers.
5. Ensure Traefik is running and accepting traffic on port 80.
6. Ensure your app responds with 200 OK to GET /up (you must have curl installed inside your app image!). 
7. Start a new container with the version of the app that matches the current Git version hash. 
8. Stop the old container running the previous version of the app. 
9. Prune unused images and stopped containers to ensure servers don’t fill up.

Voila! All the servers are now serving the app on port 80. If you’re just running a single server, you’re ready to go. If you’re running multiple servers, you need to put a load balancer in front of them. For subsequent deploys, or if your servers already have Docker and curl installed, you can just run `deploy4j deploy`.