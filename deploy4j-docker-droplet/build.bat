@echo off
REM Build script for deploy4j-docker-droplet
REM This script builds the Docker image
REM Usage: build.bat

set IMAGE_NAME=deploy4j-docker-droplet
set IMAGE_TAG=latest

if not "%~1"=="" (
    echo Usage: %0
    exit /b 1
)

:build
echo Building Docker image: %IMAGE_NAME%:%IMAGE_TAG%
docker build -t %IMAGE_NAME%:%IMAGE_TAG% .

if not %errorlevel% equ 0 (
    echo Build failed!
    exit /b 1
)

echo Build successful!
echo Image: %IMAGE_NAME%:%IMAGE_TAG%
