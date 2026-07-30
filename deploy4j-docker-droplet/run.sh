#!/bin/bash

# Run script for deploy4j-docker-droplet
# This script runs the Docker container with SSH public key mounting

IMAGE_NAME="deploy4j-docker-droplet"
IMAGE_TAG="latest"
CONTAINER_NAME="deploy4j-droplet"
SSH_PORT="2222"

# Check if public key path is provided
if [ -z "$1" ]; then
    echo "Usage: $0 <path-to-public-key>"
    echo "Example: $0 ~/.ssh/id_rsa.pub"
    exit 1
fi

PUBLIC_KEY_PATH="$1"

# Check if public key file exists
if [ ! -f "$PUBLIC_KEY_PATH" ]; then
    echo "Error: Public key file not found: $PUBLIC_KEY_PATH"
    exit 1
fi

# Get the absolute path
PUBLIC_KEY_PATH=$(realpath "$PUBLIC_KEY_PATH")

# Stop and remove existing container if it exists
docker stop ${CONTAINER_NAME} 2>/dev/null
docker rm ${CONTAINER_NAME} 2>/dev/null

echo "Starting Docker container: ${CONTAINER_NAME}"
echo "Mounting public key: ${PUBLIC_KEY_PATH}"
echo "SSH port: ${SSH_PORT}"

docker run -d \
    --name ${CONTAINER_NAME} \
    -p ${SSH_PORT}:22 \
    -v ${PUBLIC_KEY_PATH}:/tmp/authorized_keys:ro \
    -v /var/run/docker.sock:/var/run/docker.sock \
    ${IMAGE_NAME}:${IMAGE_TAG}

if [ $? -eq 0 ]; then
    echo "Container started successfully!"
    echo "Connect via SSH: ssh -p ${SSH_PORT} root@localhost"
else
    echo "Failed to start container!"
    exit 1
fi
