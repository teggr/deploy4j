# Next

* unit tests
* need to remove config() dependency
* configuration validation

# Notes

Env - https://github.com/cdimascio/dotenv-java

    Manage environment variables in Java applications using .env files. These are read in during the deployment stage.
    The .env files can be templated to go and fetch secrets from vaults or other secret management systems.
    The resulting .env file is pushed to the servers during deployment.
    The .env file is used by docker run --env-file to set environment variables for the containers.
    For host, accessory and traefik the env files are merged with other values
