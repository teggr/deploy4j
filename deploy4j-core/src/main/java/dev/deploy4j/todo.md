# Workflow

* setup []
  * server:bootstrap [x]
    * for each host and accessory host [x]
      * docker installed? [x]
        * (no) super user? [x]
          * (yes) install docker [x]
          * (no) missing [x]
      * ensure run directory [x]
    * stop if any missing docker [x]
  * env:envify [?]
  * env:push [?]
  * accessory:boot [?]
  * deploy []
    * registry:login []
    * build:pull []
    * traefik:boot []
    * app:stale_containers []
    * app:boot []
    * prune:all []

# Configuration

## Arguments

* `-d` destination 

## Config

* service []
* image []
* volumes []
* servers []
* envs []
* ssh []
* traefik []

## Notes

Env - https://github.com/cdimascio/dotenv-java

    Manage environment variables in Java applications using .env files. These are read in during the deployment stage.
    The .env files can be templated to go and fetch secrets from vaults or other secret management systems.
    The resulting .env file is pushed to the servers during deployment.
    The .env file is used by docker run --env-file to set environment variables for the containers.
    For host, accessory and traefik the env files are merged with other values