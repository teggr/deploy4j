# deploy4j

## What?

A Java port of the [Kamal](https://kamal-deploy.org/) project for deploying web applications to self hosted VMs.

Supports both [cli](#command-line) and [maven](#maven-plugin) modes.

## Why?

Deploy4j is designed to make it easy to deploy Java web applications to self hosted VMs using Docker. Deploying a simple web application can be a complex process involving multiple steps and configurations. Deploy4j simplifies this process by automating the deployment steps and providing a consistent deployment experience.

Some advantages:

* Quick setup process (start vm + deploy)
* No build service required. Works locally and also well from a pipeline
* Cheaper to run multiple services on a single vm compared to entry level tiers on some PAAS. $6 per month on Digital Ocean.

## How?

* Connects to server
* Installs docker
* Pushes jar + docker file to server
* Builds image
* Starts traefik proxy
* Starts the service

## Command Line

[View all commands on Kamal docs for now](https://kamal-deploy.org/v1/docs/commands/view-all-commands/).

Requires Java 21.

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

## Maven Plugin

### Configure

```xml
<plugin>
    <groupId>dev.deploy4j</groupId>
    <artifactId>deploy4j-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <sshUsername>root</sshUsername>
        <sshPrivateKeyPath>C:\Users\someuser\.ssh\id_rsa</sshPrivateKeyPath>
        <sshPassPhrase>******</sshPassPhrase>
        <sshKnownHostsPath>C:\Users\someuser\.ssh\known_hosts</sshKnownHostsPath>
        <host>123.123.123.123</host>
    </configuration>
</plugin>
```

See https://deploy4j.dev when it's available for further documentation.

The configuration can also be set on the command line via `-D` arguments or set globally in the maven `settings.xml` file. See the docs for further information.

## Run

```shell
mvn verify deploy4j:deploy

...
[INFO] --- deploy4j:1.0-SNAPSHOT:deploy (default-cli) @ spring-boot-web-application ---
[INFO] Deploy4J Deploying
[INFO] working directory: C:\Users\robin\IdeaProjects\deploy4j\spring-boot-web-application
[INFO] serviceName:       spring-boot-web-application
[INFO] version:           0.0.1-SNAPSHOT
[INFO] jarFilePath:       target\spring-boot-web-application-0.0.1-SNAPSHOT.jar
[INFO] dockerFilePath:    Dockerfile
[INFO] host:              123.123.123.123
[INFO] sshUsername:       root
...
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