#!/bin/bash

# Build script for deploy4j-docker-droplet
# This script builds the Docker image
# Usage: ./build.sh [--deploy]
#   --deploy: Also push the image to DockerHub (teggr repository)

IMAGE_NAME="deploy4j-docker-droplet"
IMAGE_TAG="latest"
DOCKERHUB_REPO="teggr"
DEPLOY=false

# Parse arguments
for arg in "$@"; do
    case $arg in
        --deploy)
            DEPLOY=true
            shift
            ;;
        *)
            echo "Unknown argument: $arg"
            echo "Usage: $0 [--deploy]"
            exit 1
            ;;
    esac
done

echo "Building Docker image: ${IMAGE_NAME}:${IMAGE_TAG}"
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo "Build successful!"
echo "Image: ${IMAGE_NAME}:${IMAGE_TAG}"

# Deploy to DockerHub if requested
if [ "$DEPLOY" = true ]; then
    echo ""
    echo "Deploying to DockerHub..."
    REMOTE_IMAGE="${DOCKERHUB_REPO}/${IMAGE_NAME}:${IMAGE_TAG}"
    
    echo "Tagging image as ${REMOTE_IMAGE}"
    docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${REMOTE_IMAGE}
    
    if [ $? -ne 0 ]; then
        echo "Tagging failed!"
        exit 1
    fi
    
    echo "Pushing image to DockerHub: ${REMOTE_IMAGE}"
    docker push ${REMOTE_IMAGE}
    
    if [ $? -eq 0 ]; then
        echo "Deploy successful!"
        echo "Image available at: ${REMOTE_IMAGE}"
    else
        echo "Deploy failed!"
        echo "Make sure you are logged in to DockerHub (docker login)"
        exit 1
    fi
fi
