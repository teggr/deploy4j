@echo off
REM Run script for deploy4j-docker-droplet
REM This script runs the Docker container with SSH public key mounting

set IMAGE_NAME=deploy4j-docker-droplet
set IMAGE_TAG=latest
set CONTAINER_NAME=deploy4j-droplet
set SSH_PORT=2222

REM Check if public key path is provided
if "%~1"=="" (
    echo Usage: %0 ^<path-to-public-key^>
    echo Example: %0 C:\Users\YourUser\.ssh\id_rsa.pub
    exit /b 1
)

set PUBLIC_KEY_PATH=%~1

REM Check if public key file exists
if not exist "%PUBLIC_KEY_PATH%" (
    echo Error: Public key file not found: %PUBLIC_KEY_PATH%
    exit /b 1
)

REM Stop and remove existing container if it exists
docker stop %CONTAINER_NAME% 2>nul
docker rm %CONTAINER_NAME% 2>nul

echo Starting Docker container: %CONTAINER_NAME%
echo Mounting public key: %PUBLIC_KEY_PATH%
echo SSH port: %SSH_PORT%

docker run -d ^
    --name %CONTAINER_NAME% ^
    -p %SSH_PORT%:22 ^
    -v "%PUBLIC_KEY_PATH%":/tmp/authorized_keys:ro ^
    -v /var/run/docker.sock:/var/run/docker.sock ^
    %IMAGE_NAME%:%IMAGE_TAG%

if %errorlevel% equ 0 (
    echo Container started successfully!
    echo Connect via SSH: ssh -p %SSH_PORT% root@localhost
) else (
    echo Failed to start container!
    exit /b 1
)
