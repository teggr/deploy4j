# Workflow

* setup []
  * server:bootstrap []
    * for each host and accessory host
      * docker installed?
        * (no) super user?
          * (yes) install docker []
          * (no) missing
      * ensure run directory
    * stop if any missing docker
  * env:push []
  * accessory:boot []
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