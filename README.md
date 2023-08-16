# deploy4j

Caught between the inspiration of the [MRSK](https://mrsk.dev/) project for deploy web applications and the closure of the heroku free tier the aim of this project is to enable the low cost deployment of (mainly java) web applications onto self hosted VMs such as droplets on [Digital Ocean](https://www.digitalocean.com/products/droplets).

Some advantages:

* Quick setup process (start vm + deploy)
* No build service required. Works locally and also well from a pipeline
* Cheaper to run multiple services on a single vm compared to entry level tiers on some PAAS. $6 per month on Digital Ocean.

## What does the project do?

* Connects to server
* Installs docker
* Pushes jar + docker file to server
* Builds image
* Starts traefik proxy
* Starts the service

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
