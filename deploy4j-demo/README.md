# deploy4j demo

Example repository for deploying a Spring Boot web application using [deploy4j](https://teggr.github.io/deploy4j).

The application is a simple Spring Boot application with a UI (Spring MVC) and database (Postgres).

It can be run locally with Testcontainers or deployed to a remote server using deploy4j. In the steps below we describe how to deploy to a local vm, which can be swapped out for a cloud based host such as [Digital Ocean](https://docs.digitalocean.com/products/droplets/) or [Hetzner](https://www.hetzner.com/cloud/).

## Prerequisites

- Java 24
- Maven
- Docker
- deploy4j (see [installation guide](https://teggr.github.io/deploy4j/installation/))

## What's in the box?

The repository has the following key components:

- an initialised deploy4j configuration file in `config/deploy.yml`
- a sample Spring Boot application that connects to a Postgres database

## Running locally

Assuming that you have the repository cloned and open in a modern IDE (such as IntelliJ IDEA or VS Code), you can run the application locally.

There is a runnable application in the `test` directory that loads up the database dependency using [Testcontainers](https://docs.spring.io/spring-boot/reference/features/dev-services.html#features.dev-services.testcontainers).

`src/test/java/dev/deploy4j/jdemo/Deploy4jDemoApplicationTest.java`

The application will create a `.data/` directory in the root of the project to persist the database data between runs.

On launch the application will spawn a postgres database container and be available on `http://localhost:8080`.

## Deploying to a vm

We are going to be deploying to a local vm for testing purposes. You can swap this out for a cloud based host such as Digital Ocean or Hetzner, update the `config/deploy.yml` accordingly.

### Setting up your secrets

Deploy4j uses ssh keys for authentication to remote servers and needs to be able to pull the docker image from a registry (Docker Hub in this case). In order to do this you will need to setup the following `.env` variables in the root of the project.

**Don't commit your `.env` file to source control!**

```text
DOCKER_PASSWORD=
DOCKER_USERNAME=
PRIVATE_KEY=
PRIVATE_KEY_PASSPHRASE=
```

Once set, deploy4j will be able to connect to the remote server and pull the docker image from Docker Hub.

### Running a local vm

We've created a [deploy4j-docker-droplet image](https://github.com/teggr/deploy4j-docker-droplet) to help with testing. It's contains a minimal Ubuntu image with SSH enabled and support for loading your public key. This is a close representation of what a typical Digital Ocean droplet or Hetzner server would look like.

Spin up a local ssh docker container for testing.

There are a couple of utility scripts for launching and connecting to the droplet via SSH - `droplet-up` and `droplet-ssh`.

```bash
# Running instructions
docker run -d -p 2222:22 --name deploy4j-droplet -v "%USERPROFILE%\.ssh\id_rsa.pub":/tmp/authorized_keys:ro -v /var/run/docker.sock:/var/run/docker.sock teggr/deploy4j-docker-droplet:latest

# connect via ssh
ssh -o StrictHostKeyChecking=no -p 2222 root@localhost 

# connect to shell
docker exec -it deploy4j-droplet /bin/bash
```

### Get deploying!

Now that you have your secrets setup and a droplet to deploy to, you can get deploying!

The first time we deploy with deploy4j we need to tell deploy4j what the version is and it will setup the server. This will install Docker, Traefik, any accessories (such as the Postgres database) and a instance of the application.

Note that the version specified here must match the version of the docker image that has been built and pushed to the registry. If using the demo repository, the version is `0.0.2-SNAPSHOT`.

```shell
deploy4j setup --version 0.0.2-SNAPSHOT
```

You can add the `--no-quiet` flag to see more output during the setup process, including all the commands being run. This really helps with debugging if something goes wrong.

```text
C:\Users\robin\IdeaProjects\deploy4j-demo>droplet-up.bat

C:\Users\robin\IdeaProjects\deploy4j-demo>docker run -d -p 2222:22 --name deploy4j-droplet -v "C:\Users\robin\.ssh\id_rsa.pub":/tmp/authorized_keys:ro -v /var/run/docker.sock:/var/run/docker.sock teggr/deploy4j-docker-droplet:latest 
4321ea1aa7d86193e4d4f856ec784e62a89b59dbf8a3fb4b6fbb68aeca45793d

C:\Users\robin\IdeaProjects\deploy4j-demo>deploy4j setup --version 0.0.2-SNAPSHOT
Acquiring the deploy lock...
Ensure Docker is installed...
Missing Docker on localhost. Installing...
Evaluate and push env files...
Skipping envify (no .env.thyme exists)
Boot accessories...
Log into image registry...
Pull app image...
Ensure Traefik is running...
Detect stale containers...
Get most recent version available as an image...
Start container with version 0.0.2-SNAPSHOT using a nulls readiness delay (or reboot if already running)...
retrying in 1s (attempt 1/7)...
retrying in 2s (attempt 2/7)...
...
retrying in 7s (attempt 7/7)...
Container is healthy
First web container is healthy on localhost, booting any other roles
Prune old containers and images...
Releasing the deploy lock...
=================================
Finished all in 75 seconds
```

That's it! You should now be able to access the application at http://localhost. The traefik reverse proxy is setup to route requests on port 80.

The `config/deploy.yml` configuration also enables the Traefik dashboard, which will be available on http://localhost:8080.

Now that the server is setup, future deployments can be done with:

```shell
deploy4j deploy --version 0.0.2-SNAPSHOT
```
See all the other [deploy4j commands](https://teggr.github.io/deploy4j/commands/) for managing your deployments.

## Building the project

If you want to build and deploy the project yourself, you can use the provided Maven wrapper to do so.

You will need to change the repo name in the `pom.xml` to match your Docker Hub repository and the name of the image in the `config/deploy.yml` file.

```shell
# Build and test the application
./mvnw clean verify

# Deploy the docker image to Docker Hub
./mvw deploy

# or
deploy4j setup --version 0.0.2-SNAPSHOT
```
## Help?

If you have any issues, please raise an issue on the [deploy4j-demo GitHub repository](https://github.com/teggr/deploy4j-demo).

## Having issues with ghost data on windows?

I've seen locally that sometimes when running on Windows, the Postgres data directory can end up with some ghost files that cause issues with the database startup.

Here's some steps to clear out the data directory on the droplet.

SSH onto the droplet and clear out the data directory.

```shell
droplet-ssh.bat

docker run --rm -it \
  -v $PWD/deploy4j-demo-db/data:/var/lib/postgresql/18/docker \
  alpine sh -c "echo 'Contents of /var/lib/postgresql/18/docker:' && ls -la /var/lib/postgresql/18/docker"

docker run --rm -it \
  -v $PWD/deploy4j-demo-db/data:/var/lib/postgresql/18/docker \
  alpine sh -c "echo 'Removing all files...' && rm -rf /var/lib/postgresql/18/docker/* /var/lib/postgresql/18/docker/.[!.]* /var/lib/postgresql/18/docker/..?* && echo 'Cleanup done.' && ls -la /var/lib/postgresql/18/docker"
```