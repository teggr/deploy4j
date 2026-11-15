#!/bin/bash

# Build script for deploy4j-docker-droplet
# This script builds the Docker image
# Usage: ./build.sh

IMAGE_NAME="deploy4j-docker-droplet"
IMAGE_TAG="latest"

if [ "$#" -ne 0 ]; then
    echo "Usage: $0"
    exit 1
fi

echo "Building Docker image: ${IMAGE_NAME}:${IMAGE_TAG}"
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo "Build successful!"
echo "Image: ${IMAGE_NAME}:${IMAGE_TAG}"
