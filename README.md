# deploy4j

Caught between the inspiration of the [MRSK](https://mrsk.dev/) project for deploy web applications and the closure of the heroku free tier the aim of this project is to enable the low cost deployment of (mainly java) web applications onto self hosted VMs such as droplets on [Digital Ocean](https://www.digitalocean.com/products/droplets).

## What does the project do?

* Connects to server
* Installs docker
* Pushes jar + docker file to server
* Builds image
* Starts traefik proxy
* Starts the service

TODO:// maven plugin deploy4j:deploy

Some advantages:

* Quick setup process (start vm + deploy)
* No build service required. Works locally and also well from a pipeline
* Cheaper to run multiple services on a single vm compared to entry level tiers on some PAAS. $6 per month on Digital Ocean.

# Configuration

## System Properties
TODO:// replace with config file
```properties
# server properties
server.host

# ssh properties
server.ssh.username
server.ssh.privateKey
server.ssh.passPhrase
server.ssh.knownHosts
```