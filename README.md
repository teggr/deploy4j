# deploy4j

[![](https://jitpack.io/v/teggr/deploy4j.svg)](https://jitpack.io/#teggr/deploy4j/deploy)

## What?

A Java port of the [Kamal](https://kamal-deploy.org/) [v1](https://kamal-deploy.org/v1/docs/installation/) project for deploying web applications to self hosted VMs.

Supports both [cli](#command-line) and [maven](#maven-plugin) modes.

## Why?

Deploy4j is designed to make it easy to deploy Java web applications to self hosted VMs using Docker. Deploying a simple web application can be a complex process involving multiple steps and configurations. Deploy4j simplifies this process by automating the deployment steps and providing a consistent deployment experience.

Some advantages:

* Quick setup process (start vm + deploy)
* No build service required. Works locally and also well from a pipeline
* Cheaper to run multiple services on a single vm compared to entry level tiers on some PAAS. $6 per month on Digital Ocean.

## How?

See [Kamal v1](https://kamal-deploy.org/v1/docs/installation/) for further documentation whilst deploy4j documentation is being put together.

* Connects to server
* Installs docker
* Pushes jar + docker file to server
* Builds image
* Starts traefik proxy
* Starts the service

## Pre-reqs

Requires Java 21.

## Command Line

[View all commands on Kamal docs for now](https://kamal-deploy.org/v1/docs/commands/view-all-commands/).

Install via JBang:

`deploy4j` is currently available via JitPack.

```shell
jbang app install --name deploy4j --repos jitpack=https://jitpack.io com.github.teggr.deploy4j:deploy4j-cli:-SNAPSHOT
```

```shell
Usage: deploy4j [--help] [COMMAND]
Deploy web apps anywhere. From bare metal to cloud VMs.
      --help   Display help about a command
Commands:
  accessory  Manage accessories (db/redis/search)
  app        Manage application
  audit      Show audit log from servers
  build      Build application image
  config     Show combined config (including secrets!)
  deploy     Deploy app to servers
  details    Show details about all containers
  env        Manage environment files
  envify     Create .env by evaluating .env.thyme (or .env.staging.thyme -> .
               env.staging when using -d staging)
  init       Create config stub in config/deploy.yml and env stub in .env
  lock       Manage the deploy lock
  prune      Prune old application images and containers
  redeploy   Deploy app to servers without bootstrapping servers, starting
               Traefik, pruning, and registry login
  registry   Login and out of the image registry
  remove     Remove Traefik, app, accessories, and registry session from servers
  rollback   Rollback app to VERSION
  server     Boostrap servers with curl and Docker
  setup      Setup all accessories, push the env, and deploy app to servers
  test       Test connectivity to servers
  traefik    Manage Traefik load balancer
  version    Show Deploy4j version
```

### Typical Usage

```shell
# initialise the project for the first time
deploy4j init

# edit config/deploy.yml and .env

# setup the servers and deploy the app for the first time
deploy4j setup 0.0.1

# deploy a new version of the app
deploy4j deploy --version 0.0.2
```

## Maven Plugin

### Configure

```xml
<pluginRepositories>
    <pluginRepository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </pluginRepository>
</pluginRepositories>

<build>
    <plugin>
        <groupId>com.github.teggr.deploy4j</groupId>
        <artifactId>deploy4j-maven-plugin</artifactId>
        <version>-SNAPSHOT</version>
    </plugin>
</build>
```

### Run

```shell
mvn verify deploy4j:deploy

[INFO] ---------------------< dev.deploy4j:deploy4j-demo >---------------------
[INFO] Building deploy4j-demo 0.0.1-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- deploy4j:0.0.1-SNAPSHOT:deploy (default-cli) @ deploy4j-demo ---
```

```shell
# add the plugin to your build

# initialise the project for the first time
mvn deploy4j:init

# edit config/deploy.yml and .env

# setup the servers and deploy the app for the first time
mvn deploy4j:setup

# deploy a new version of the app
mvn deploy4j:deploy
```

## Testing

Spin up a local ssh docker container for testing:

```bash
# Running instructions
docker run -d -p 2222:22 --name deploy4j-droplet -v "C:\Users\YOUR_USER\.ssh\id_rsa.pub":/tmp/authorized_keys:ro -v /var/run/docker.sock:/var/run/docker.sock teggr/deploy4j-docker-droplet:latest

# connect via ssh
ssh -o StrictHostKeyChecking=no -p 2222 root@localhost 

# connect to shell
docker exec -it deploy4j-droplet /bin/bash
```

## Notes on the Kamal Port

The current port is based on Kamal v1. Once the v2 changes have been assessed then a decision can be made on whether to port those changes over.

It's likely that v1 will remain the baseline for future enhancements as we start to look at introducing more Java focussed enhancements.