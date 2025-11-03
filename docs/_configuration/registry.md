---
layout: page
title: Registry
---
The default registry is Docker Hub, but you can change it using `registry.server` and credentials.

A reference to a secret (e.g. `DOCKER_REGISTRY_TOKEN`) will look up the secret in the local environment.

```yaml
registry:
  server: registry.digitalocean.com
  username:
    - DOCKER_REGISTRY_TOKEN
  password:
    - DOCKER_REGISTRY_TOKEN
```

## Using AWS ECR as the container registry

You will need to have the aws CLI installed locally for this to work.

AWS ECR’s access token is only valid for 12hrs. In order to not have to manually regenerate the token every time, you can use ERB in the deploy.yml file to shell out to the aws cli command, and obtain the token:

```yaml
registry:
server: <your aws account id>.dkr.ecr.<your aws region id>.amazonaws.com
username: AWS
password: <%= %x(aws ecr get-login-password) %>
```

## Using GCP Artifact Registry as the container registry

To sign into Artifact Registry, you would need to [create a service account](https://cloud.google.com/iam/docs/service-accounts-create#creating) and [set up roles and permissions](https://cloud.google.com/artifact-registry/docs/access-control#permissions).

Normally, assigning a roles/artifactregistry.writer role should be sufficient.

Once the service account is ready, you need to generate and download a JSON key, base64 encode it and add to .env:

```shell
echo "DEPLOY4J_REGISTRY_PASSWORD=$(base64 -i /path/to/key.json)" | tr -d "\\n"  >> .env
```

Use the env variable as password along with _json_key_base64 as username.

Here’s the final configuration:

```yaml
registry:
server: <your registry region>-docker.pkg.dev
username: _json_key_base64
password:
  - DEPLOY4J_REGISTRY_PASSWORD
```

## Validating the configuration

You can validate the configuration by running:

```shell
deploy4j registry login
```
