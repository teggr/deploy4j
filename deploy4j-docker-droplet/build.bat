@echo off
REM Build script for deploy4j-docker-droplet
REM This script builds the Docker image
REM Usage: build.bat [--deploy]
REM   --deploy: Also push the image to DockerHub (teggr repository)

set IMAGE_NAME=deploy4j-docker-droplet
set IMAGE_TAG=latest
set DOCKERHUB_REPO=teggr
set DEPLOY=false

REM Parse arguments
:parse_args
if "%~1"=="" goto build
if "%~1"=="--deploy" (
    set DEPLOY=true
    shift
    goto parse_args
)
echo Unknown argument: %~1
echo Usage: %0 [--deploy]
exit /b 1

:build
echo Building Docker image: %IMAGE_NAME%:%IMAGE_TAG%
docker build -t %IMAGE_NAME%:%IMAGE_TAG% .

if not %errorlevel% equ 0 (
    echo Build failed!
    exit /b 1
)

echo Build successful!
echo Image: %IMAGE_NAME%:%IMAGE_TAG%

REM Deploy to DockerHub if requested
set REMOTE_IMAGE=%DOCKERHUB_REPO%/%IMAGE_NAME%:%IMAGE_TAG%
if "%DEPLOY%"=="true" (
    echo.
    echo Deploying to DockerHub...

    echo Tagging image as %REMOTE_IMAGE%
    docker tag %IMAGE_NAME%:%IMAGE_TAG% %REMOTE_IMAGE%
    
    if not %errorlevel% equ 0 (
        echo Tagging failed!
        exit /b 1
    )
    
    echo Pushing image to DockerHub: %REMOTE_IMAGE%
    docker push %REMOTE_IMAGE%
    
    if %errorlevel% equ 0 (
        echo Deploy successful!
        echo Image available at: %REMOTE_IMAGE%
    ) else (
        echo Deploy failed!
        echo Make sure you are logged in to DockerHub ^(docker login^)
        exit /b 1
    )
)
