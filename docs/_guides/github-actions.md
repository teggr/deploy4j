---
layout: page
title: GitHub Actions
---

This guide demonstrates how to set up a GitHub Actions workflow to deploy your application using deploy4j.

Using GitHub Actions with deploy4j allows you to automate your deployment process, ensuring that your application is deployed consistently every time you push changes to your repository.

## Prerequisites

- A working deploy4j configuration in your repository (see [Spring Boot guide]({{ '/guides/spring-boot' | relative_url }}))
- A Docker image of your application pushed to a registry (e.g., Docker Hub, GitHub Container Registry)
- SSH access to your deployment server
- GitHub repository secrets configured for sensitive values

## Setting Up GitHub Secrets

Before creating the workflow, you need to store your sensitive deployment credentials as GitHub repository secrets. Go to your repository **Settings > Secrets and variables > Actions** and add the following secrets:

| Secret Name | Description |
|-------------|-------------|
| `SSH_PRIVATE_KEY` | Your SSH private key for connecting to the server |
| `SSH_PRIVATE_KEY_PASSPHRASE` | Passphrase for your SSH key (if applicable) |
| `SSH_KNOWN_HOSTS` | Contents of your known_hosts file for the target server |
| `DOCKER_USERNAME` | Your Docker registry username |
| `DOCKER_PASSWORD` | Your Docker registry password or access token |

To get the known_hosts entry for your server, run:

```shell
ssh-keyscan -H your-server-ip
```

## Creating the Workflow File

Create a new workflow file at `.github/workflows/deploy.yml` in your repository:

```yaml
name: Deploy with deploy4j

on:
  push:
    branches:
      - main
  workflow_dispatch:

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Build with Maven
        run: mvn -B package -DskipTests

      - name: Build and push Docker image
        run: |
          echo "${{ secrets.DOCKER_PASSWORD }}" | docker login -u "${{ secrets.DOCKER_USERNAME }}" --password-stdin
          mvn -B docker:build docker:push -DskipTests

      - name: Set up deploy4j secrets
        run: |
          mkdir -p .deploy4j
          cat > .deploy4j/secrets << EOF
          DOCKER_USERNAME=${{ secrets.DOCKER_USERNAME }}
          DOCKER_PASSWORD=${{ secrets.DOCKER_PASSWORD }}
          PRIVATE_KEY=/tmp/ssh_key
          PRIVATE_KEY_PASSPHRASE=${{ secrets.SSH_PRIVATE_KEY_PASSPHRASE }}
          KNOWN_HOSTS_PATH=/tmp/known_hosts
          EOF

      - name: Set up SSH credentials
        run: |
          echo "${{ secrets.SSH_PRIVATE_KEY }}" > /tmp/ssh_key
          chmod 600 /tmp/ssh_key
          echo "${{ secrets.SSH_KNOWN_HOSTS }}" > /tmp/known_hosts

      - name: Install deploy4j
        run: |
          curl -sL https://github.com/teggr/deploy4j/releases/latest/download/deploy4j-cli.jar -o deploy4j-cli.jar
          echo '#!/bin/bash' > deploy4j
          echo 'java -jar '"$(pwd)"'/deploy4j-cli.jar "$@"' >> deploy4j
          chmod +x deploy4j
          sudo mv deploy4j /usr/local/bin/

      - name: Deploy application
        run: deploy4j deploy --version ${{ github.sha }}
```

## Workflow Breakdown

### Triggers

The workflow is configured to run on:
- **Push to main**: Automatically deploys when changes are pushed to the main branch
- **Manual dispatch**: Allows you to trigger the deployment manually from the GitHub Actions UI

```yaml
on:
  push:
    branches:
      - main
  workflow_dispatch:
```

### Build Steps

The workflow performs the following steps:

1. **Checkout code**: Clones your repository
2. **Set up JDK**: Configures Java 21 with Maven caching
3. **Build with Maven**: Compiles and packages your application
4. **Build and push Docker image**: Creates and pushes the Docker image to your registry

### Deploy4j Setup

The workflow sets up deploy4j by:

1. **Creating secrets file**: Writes deployment secrets to `.deploy4j/secrets`
2. **Setting up SSH credentials**: Writes SSH key and known_hosts to temporary files
3. **Installing deploy4j**: Downloads and installs the deploy4j CLI

### Deployment

Finally, the workflow runs `deploy4j deploy` with the Git commit SHA as the version tag, ensuring each deployment is traceable to a specific commit.

## Advanced Configuration

### Deploying to Multiple Environments

You can deploy to different environments (staging, production) by using environment-specific secrets and workflow inputs:

```yaml
name: Deploy with deploy4j

on:
  workflow_dispatch:
    inputs:
      environment:
        description: 'Deployment environment'
        required: true
        default: 'staging'
        type: choice
        options:
          - staging
          - production

jobs:
  deploy:
    runs-on: ubuntu-latest
    environment: ${{ github.event.inputs.environment }}
    
    steps:
      # ... previous steps ...
      
      - name: Deploy application
        run: |
          if [ "${{ github.event.inputs.environment }}" = "production" ]; then
            deploy4j deploy --version ${{ github.sha }} -d config/deploy.production.yml
          else
            deploy4j deploy --version ${{ github.sha }} -d config/deploy.staging.yml
          fi
```

### Using GitHub Container Registry

If you prefer to use GitHub Container Registry (ghcr.io) instead of Docker Hub:

1. Update your `config/deploy.yml`:

```yaml
image: ghcr.io/your-username/your-app
registry:
  server: ghcr.io
  username:
    - DOCKER_USERNAME
  password:
    - DOCKER_PASSWORD
```

2. Update the workflow to use `GITHUB_TOKEN`:

```yaml
      - name: Build and push Docker image
        run: |
          echo "${{ secrets.GITHUB_TOKEN }}" | docker login ghcr.io -u ${{ github.actor }} --password-stdin
          mvn -B docker:build docker:push -DskipTests

      - name: Set up deploy4j secrets
        run: |
          mkdir -p .deploy4j
          cat > .deploy4j/secrets << EOF
          DOCKER_USERNAME=${{ github.actor }}
          DOCKER_PASSWORD=${{ secrets.GITHUB_TOKEN }}
          # ... rest of secrets ...
          EOF
```

### Running on Tags

To deploy only when a release tag is created:

```yaml
on:
  push:
    tags:
      - 'v*'

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
      # ... previous steps ...
      
      - name: Deploy application
        run: deploy4j deploy --version ${{ github.ref_name }}
```

## Security Considerations

- **Never commit secrets**: Always use GitHub Secrets for sensitive values
- **Use environment protection rules**: Configure [deployment protection rules](https://docs.github.com/en/actions/deployment/targeting-different-environments/using-environments-for-deployment) for production environments
- **Limit SSH key permissions**: Use a dedicated deployment key with minimal required permissions
- **Review workflow changes**: Require pull request reviews for changes to workflow files

## Troubleshooting

### SSH Connection Failures

If you encounter SSH connection issues:

1. Verify your SSH key format (should be OpenSSH format)
2. Check that the known_hosts entry matches your server
3. Ensure the SSH key has appropriate permissions (600)

```yaml
      - name: Debug SSH connection
        run: |
          ssh -vvv -i /tmp/ssh_key -o UserKnownHostsFile=/tmp/known_hosts root@your-server-ip "echo 'Connection successful'"
```

### Docker Registry Authentication

If Docker push fails:

1. Verify your Docker credentials
2. Check that your Docker username has push permissions to the repository
3. For GitHub Container Registry, ensure the package visibility settings are correct

### Deploy4j Command Errors

If deploy4j commands fail:

1. Check that `config/deploy.yml` exists and is valid
2. Verify all required secrets are set
3. Run with verbose output for more details:

```yaml
      - name: Deploy application
        run: deploy4j deploy --version ${{ github.sha }} --verbose
```
