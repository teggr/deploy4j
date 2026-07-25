# deploy4j-docker-droplet
A Docker container that can be used to test deploy4j as it's close to a Digital Ocean droplet after initialisation

## Overview

This project provides a simple Docker image based on Ubuntu with SSH support. It's designed to simulate a Digital Ocean droplet environment for testing deployment tools like deploy4j.

## Features

- Based on latest Ubuntu LTS image
- SSH server configured and ready to use
- Support for SSH public key authentication
- Root login enabled with SSH keys
- Scripts for building and running on both Linux and Windows

## Prerequisites

- Docker installed on your system
- SSH key pair (public and private keys)

## Running the Container

The container requires you to mount your SSH public key for authentication and the Docker socket for Docker-in-Docker capabilities.

```bash
# Running instructions
docker run -d -p 2222:22 --name deploy4j-droplet -v "C:\Users\YOUR_USER\.ssh\id_rsa.pub":/tmp/authorized_keys:ro -v /var/run/docker.sock:/var/run/docker.sock teggr/deploy4j-docker-droplet:latest

# connect via ssh
ssh -o StrictHostKeyChecking=no -p 2222 root@localhost 

# connect to shell
docker exec -it deploy4j-droplet /bin/bash
```

The container will:
- Start in detached mode
- Map SSH port 22 to host port 2222
- Mount your public key to `/root/.ssh/authorized_keys`
- Be named `deploy4j-droplet`

## Technical Details

### Dockerfile

- Base image: `ubuntu:24.04`
- Installed packages: `openssh-server`
- SSH configuration:
  - Root login with public key authentication enabled
  - Password authentication disabled
  - Public key authentication enabled
- Exposed port: 22
- Command: SSH daemon in foreground mode

### Scripts

- `build.sh` / `build.bat`: Build the Docker image. \Use '--deploy' option to push to DockerHub
- `run.sh` / `run.bat`: Run the container with public key mounting

## Troubleshooting

### Cannot connect via SSH

1. Ensure the container is running: `docker ps`
2. Check the container logs: `docker logs deploy4j-droplet`
3. Verify your public key is correctly mounted: `docker exec deploy4j-droplet cat /root/.ssh/authorized_keys`

### Permission denied (publickey)

1. Ensure you're using the correct private key that matches the mounted public key
2. Check SSH key permissions: `chmod 600 ~/.ssh/id_rsa` (private key should be readable only by owner)

## License

See LICENSE file for details.

