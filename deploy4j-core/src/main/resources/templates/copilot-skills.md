## deploy4j Deployment

This project is deployed using [deploy4j](https://deploy4j.dev/).

### Deployment Configuration

- Configuration: `config/deploy.yml`
- Secrets: `.deploy4j/secrets`
- Hooks: `.deploy4j/hooks/`

### Common Deployment Commands

```shell
# Deploy a new version
deploy4j deploy --version <version>

# First-time setup (bootstraps server, starts Traefik, deploys)
deploy4j setup <version>

# Redeploy without bootstrapping
deploy4j redeploy --version <version>

# Roll back to a previous version
deploy4j rollback <version>

# View running containers
deploy4j app details

# Push updated environment variables
deploy4j env push

# View Traefik status
deploy4j traefik details
```

### Deployment Workflow

1. Update version in `pom.xml`
2. Edit `config/deploy.yml` for any infrastructure changes
3. Update `.deploy4j/secrets` for new secret values, then run `deploy4j env push`
4. Run `deploy4j deploy --version <version>` to deploy
