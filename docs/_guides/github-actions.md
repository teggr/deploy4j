---
layout: page
title: Deploy with GitHub Actions
---

This guide demonstrates how to automate your deploy4j deployments using GitHub Actions. By integrating deploy4j into your CI/CD pipeline, you can automatically build, test, and deploy your applications whenever you push changes to your repository.

## Overview

GitHub Actions is a powerful CI/CD platform that allows you to automate your software development workflows directly in your GitHub repository. By combining GitHub Actions with deploy4j, you can create a complete automated deployment pipeline that:

* Builds your application
* Pushes Docker images to a registry
* Deploys to your servers using deploy4j
* Manages versioning and releases automatically

## Prerequisites

Before setting up GitHub Actions with deploy4j, ensure you have:

1. **A GitHub repository** with your application code
2. **A deploy4j configuration** (`config/deploy.yml` set up in your repository)
3. **Docker Hub account** (or another container registry)
4. **SSH access to your deployment server** with a private key
5. **deploy4j tested locally** to ensure your deployment configuration works (optional - YOLO)

## GitHub Secrets Configuration

GitHub Actions uses repository secrets to store sensitive information securely. You need to configure the following secrets in your repository:

To add secrets to your repository:
1. Navigate to your repository on GitHub
2. Click on **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret** for each of the following:

| Secret Name | Description | Example Value |
|------------|-------------|---------------|
| `DOCKER_USERNAME` | Docker Hub username | `your-dockerhub-username` |
| `DOCKER_PASSWORD` | Docker Hub password or access token | `your-dockerhub-password` |
| `PRIVATE_KEY` | SSH private key for server access | Contents of `~/.ssh/id_rsa` |
| `PRIVATE_KEY_PASSPHRASE` | Passphrase for SSH private key (if applicable) | `your-passphrase` |

**Important:** Never commit secrets directly to your repository. Always use GitHub's secrets management.

## Complete GitHub Actions Workflow

Create a file named `.github/workflows/release-and-deploy.yml` in your repository with the following content:

```yaml
name: Release and deploy

on:
  workflow_dispatch:

jobs:
  build:

    runs-on: ubuntu-latest

    permissions:
      contents: write  # Grants permission to push tags and commits

    steps:

    # checkout the repository
    - uses: actions/checkout@v4

    # set up JDK 21 and cache Maven dependencies
    - name: Set up JDK 21
      uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'

    # cache Maven packages
    - name: Cache Maven packages
      uses: actions/cache@v4
      with:
        path: |
          ~/.jbang
          ~/.m2/repository
        key: maven-${{ runner.os }}-deps
        restore-keys: |
          maven-${{ runner.os }}-

    # get current version from pom.xml
    - name: Get current Maven version
      id: get_version
      run: |
       CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout --file pom.xml)
       echo "current_version=$CURRENT_VERSION" >> $GITHUB_OUTPUT
       echo "Current version: $CURRENT_VERSION"

    # remove -SNAPSHOT for release
    - name: Set release version
      id: set_release_version
      run: |
        mvn versions:set -DremoveSnapshot
        RELEASE_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout --file pom.xml)
        echo "release_version=$RELEASE_VERSION" >> $GITHUB_OUTPUT
        echo "Release version: $RELEASE_VERSION"
        git config user.name "GitHub Actions"
        git config user.email "actions@github.com"
        git commit -am "Set release version $RELEASE_VERSION"
      shell: bash

    # build with Maven
    - name: Build with Maven
      run: mvn -B package --file pom.xml

    # deploy to Docker Hub
    - name: Log in to Docker Hub
      uses: docker/login-action@v3.1.0
      with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}

    # build and push Docker image
    - name: Push Docker image
      run: mvn -B docker:push --file pom.xml

    # setup JBang
    - name: Setup JBang
      uses: jbangdev/setup-jbang@main

    # configure java versions for jbang
    - name: Configure java
      run: jbang jdk install 21 ${{env.JAVA_HOME_21_X64}}

    # setup SSH for deploy4j
    - name: Write private key to file
      env:
        SSH_PRIVATE_KEY: ${{ secrets.PRIVATE_KEY }}
      run: |
          # Create SSH directory and write the private key
          mkdir -p ~/.ssh && echo "$SSH_PRIVATE_KEY" > ~/.ssh/id_rsa && chmod 600 ~/.ssh/id_rsa

    # create deploy4j secrets file
    - name: Create deploy4j secrets file
      run: |
        mkdir -p .deploy4j
        cat > .deploy4j/secrets << "EOF"
        DOCKER_USERNAME=$DOCKER_USERNAME
        DOCKER_PASSWORD=$DOCKER_PASSWORD
        PRIVATE_KEY_PASSPHRASE=$PRIVATE_KEY_PASSPHRASE
        PRIVATE_KEY=~/.ssh/id_rsa
        EOF

    # deploy application using deploy4j
    - name: Deploy application with deploy4j via jbang
      env:
        DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
        DOCKER_PASSWORD: ${{ secrets.DOCKER_PASSWORD }}
        PRIVATE_KEY_PASSPHRASE: ${{ secrets.PRIVATE_KEY_PASSPHRASE }}
      run: jbang --java 21 dev.deploy4j:deploy4j-cli:0.0.6 deploy --version ${{ steps.set_release_version.outputs.release_version }}

    # tag release
    - name: Tag release
      run: |
        git tag -a "v${{ steps.set_release_version.outputs.release_version }}" -m "Release ${{ steps.set_release_version.outputs.release_version }}"
        git push origin "v${{ steps.set_release_version.outputs.release_version }}"
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

    # move to next -SNAPSHOT
    - name: Set next SNAPSHOT version
      run: |
        mvn versions:set -DnextSnapshot
        NEXT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout --file pom.xml)
        echo "next_version=$NEXT_VERSION" >> $GITHUB_OUTPUT
        echo "Next snapshot version: $NEXT_VERSION"
        git config user.name "GitHub Actions"
        git config user.email "actions@github.com"
        git commit -am "Set snapshot version $NEXT_VERSION"
        git push origin $GITHUB_REF_NAME
      shell: bash
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

    # Optional: Uploads the full dependency graph to GitHub to improve the quality of Dependabot alerts this repository can receive
    - name: Update dependency graph
      uses: advanced-security/maven-dependency-submission-action@571e99aab1055c2e71a1e2309b9691de18d6b7d6

```

## Workflow Breakdown

Let's examine each section of the workflow to understand what it does:

### Workflow Trigger

```yaml
on:
  workflow_dispatch:
```

**What it does:** Configures the workflow to run manually. You trigger it by going to the **Actions** tab in your repository, selecting the workflow, and clicking "Run workflow". This gives you control over when deployments happen.

**Alternative triggers:** You could also trigger on `push` to specific branches or `pull_request` events for automated deployments.

### Job Configuration

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    permissions:
      contents: write
```

**What it does:** 
* Defines a job named `build` that runs on the latest Ubuntu runner
* Grants `contents: write` permission, which allows the workflow to push commits and tags back to the repository

### Repository Checkout

```yaml
- uses: actions/checkout@v4
```

**What it does:** Checks out your repository code so the workflow can access your source files, `pom.xml`, and deployment configuration.

### Java Setup and Caching

```yaml
- name: Set up JDK 21
  uses: actions/setup-java@v4
  with:
    java-version: '21'
    distribution: 'temurin'

- name: Cache Maven packages
  uses: actions/cache@v4
  with:
    path: |
      ~/.jbang
      ~/.m2/repository
    key: maven-${{ runner.os }}-deps
```

**What it does:**
* Installs JDK 21 (Temurin distribution) required for building your application
* Caches Maven dependencies and JBang installations to speed up subsequent workflow runs
* The cache key is based on the operating system, so each OS gets its own cache

### Version Management

```yaml
- name: Get current Maven version
  id: get_version
  run: |
    CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout --file pom.xml)
    echo "current_version=$CURRENT_VERSION" >> $GITHUB_OUTPUT
    echo "Current version: $CURRENT_VERSION"

- name: Set release version
  id: set_release_version
  run: |
    mvn versions:set -DremoveSnapshot
    RELEASE_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout --file pom.xml)
    echo "release_version=$RELEASE_VERSION" >> $GITHUB_OUTPUT
    echo "Release version: $RELEASE_VERSION"
    git config user.name "GitHub Actions"
    git config user.email "actions@github.com"
    git commit -am "Set release version $RELEASE_VERSION"
```

**What it does:**
* Extracts the current version from `pom.xml` (e.g., `1.0.0-SNAPSHOT`)
* Removes the `-SNAPSHOT` suffix to create a release version (e.g., `1.0.0`)
* Commits the version change to the repository
* Stores the release version in `$GITHUB_OUTPUT` for use in later steps

### Build Application

```yaml
- name: Build with Maven
  run: mvn -B package --file pom.xml
```

**What it does:**
* Builds your application using Maven
* The `-B` flag runs in batch mode (non-interactive)
* Creates the JAR/WAR file and Docker image (if configured in your Maven build)

### Docker Hub Authentication and Push

```yaml
- name: Log in to Docker Hub
  uses: docker/login-action@v3.1.0
  with:
    username: ${{ secrets.DOCKER_USERNAME }}
    password: ${{ secrets.DOCKER_PASSWORD }}

- name: Push Docker image
  run: mvn -B docker:push --file pom.xml
```

**What it does:**
* Authenticates to Docker Hub using credentials stored in GitHub Secrets
* Pushes the Docker image built during the Maven build to Docker Hub
* The image is tagged with the release version

### JBang Setup

```yaml
- name: Setup JBang
  uses: jbangdev/setup-jbang@main

- name: Configure java
  run: jbang jdk install 21 ${{env.JAVA_HOME_21_X64}}
```

**What it does:**
* Installs JBang, a tool for running Java applications without manual setup
* Configures JBang to use JDK 21
* This allows us to run deploy4j directly from Maven Central without pre-installing it

### SSH Configuration

```yaml
- name: Write private key to file
  env:
    SSH_PRIVATE_KEY: ${{ secrets.PRIVATE_KEY }}
  run: |
    mkdir -p ~/.ssh && echo "$SSH_PRIVATE_KEY" > ~/.ssh/id_rsa && chmod 600 ~/.ssh/id_rsa
```

**What it does:**
* Creates the SSH directory if it doesn't exist
* Writes your private SSH key from GitHub Secrets to `~/.ssh/id_rsa`
* Sets appropriate permissions (600) on the private key for security

**Important:** The private key should include the header and footer lines (`-----BEGIN ... KEY-----` and `-----END ... KEY-----`).

### Deploy4j Secrets File

```yaml
- name: Create deploy4j secrets file
  run: |
    mkdir -p .deploy4j
    cat > .deploy4j/secrets << "EOF"
    DOCKER_USERNAME=$DOCKER_USERNAME
    DOCKER_PASSWORD=$DOCKER_PASSWORD
    PRIVATE_KEY_PASSPHRASE=$PRIVATE_KEY_PASSPHRASE
    PRIVATE_KEY=~/.ssh/id_rsa
    EOF
```

**What it does:**
* Creates the `.deploy4j` directory
* Generates a secrets file that deploy4j will use for configuration
* The secrets file uses environment variable placeholders that will be resolved in the next step

### Deploy Application

```yaml
- name: Deploy application with deploy4j via jbang
  env:
    DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
    DOCKER_PASSWORD: ${{ secrets.DOCKER_PASSWORD }}
    PRIVATE_KEY_PASSPHRASE: ${{ secrets.PRIVATE_KEY_PASSPHRASE }}
  run: jbang --java 21 dev.deploy4j:deploy4j-cli:0.0.6 deploy --version ${{ steps.set_release_version.outputs.release_version }}
```

**What it does:**
* Sets environment variables from GitHub Secrets
* Uses JBang to run deploy4j-cli directly from Maven Central (version 0.0.6)
* Executes the `deploy` command with the release version
* deploy4j connects to your server via SSH, pulls the Docker image, and starts the containers

**Note:** Update the deploy4j-cli version (`0.0.6`) to the latest version available.

### Release Tagging

```yaml
- name: Tag release
  run: |
    git tag -a "v${{ steps.set_release_version.outputs.release_version }}" -m "Release ${{ steps.set_release_version.outputs.release_version }}"
    git push origin "v${{ steps.set_release_version.outputs.release_version }}"
```

**What it does:**
* Creates an annotated Git tag for the release (e.g., `v1.0.0`)
* Pushes the tag to GitHub
* This creates a release marker in your repository history

### Next SNAPSHOT Version

```yaml
- name: Set next SNAPSHOT version
  run: |
    mvn versions:set -DnextSnapshot
    NEXT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout --file pom.xml)
    echo "next_version=$NEXT_VERSION" >> $GITHUB_OUTPUT
    echo "Next snapshot version: $NEXT_VERSION"
    git config user.name "GitHub Actions"
    git config user.email "actions@github.com"
    git commit -am "Set snapshot version $NEXT_VERSION"
    git push origin $GITHUB_REF_NAME
```

**What it does:**
* Increments the version to the next SNAPSHOT (e.g., `1.0.0` → `1.0.1-SNAPSHOT`)
* Commits the new SNAPSHOT version
* Pushes the commit back to the branch
* This prepares your repository for the next development cycle

### Dependency Graph Update

```yaml
- name: Update dependency graph
  uses: advanced-security/maven-dependency-submission-action@571e99aab1055c2e71a1e2309b9691de18d6b7d6
```

**What it does:**
* Uploads the dependency graph to GitHub
* Enables Dependabot to provide security alerts for your dependencies
* This is optional but highly recommended for security monitoring

## Running the Workflow

Once you've created the workflow file and configured your secrets:

1. **Commit and push** the workflow file to your repository
2. Navigate to the **Actions** tab in your GitHub repository
3. Select the **"Release and deploy"** workflow from the left sidebar
4. Click the **"Run workflow"** button
5. Select the branch you want to deploy from
6. Click **"Run workflow"** to start the deployment

The workflow will:
* Build your application
* Push to Docker Hub
* Deploy to your server using deploy4j
* Create a release tag
* Prepare the next development version

## Monitoring Workflow Execution

GitHub Actions provides detailed logs for each workflow run:

1. Go to the **Actions** tab
2. Click on the workflow run you want to inspect
3. Click on the **"build"** job to see detailed logs
4. Each step can be expanded to view its output

If a step fails, GitHub Actions will highlight it in red, and you can click on it to see the error details.

## Customizing the Workflow

### Deploying on Push

To automatically deploy when pushing to a specific branch:

```yaml
on:
  push:
    branches:
      - main
```

### Deploying Multiple Environments

You can create separate workflows for different environments (staging, production) or use workflow inputs to select the environment:

```yaml
on:
  workflow_dispatch:
    inputs:
      environment:
        description: 'Deployment environment'
        required: true
        type: choice
        options:
          - staging
          - production
```

Then reference the environment in your deploy4j command:

```yaml
run: jbang --java 21 dev.deploy4j:deploy4j-cli:0.0.6 deploy --version ${{ steps.set_release_version.outputs.release_version }} --destination ${{ inputs.environment }}
```

### Skipping Version Management

If you don't want automatic version management, you can remove the version-related steps and specify the version manually.

## Troubleshooting

### SSH Connection Issues

If deploy4j fails to connect to your server:

1. Verify your `PRIVATE_KEY` secret includes the complete key with headers
2. Ensure the server's SSH fingerprint is in your known_hosts (you may need to add `StrictHostKeyChecking=no` temporarily)
3. Check that your server allows SSH access from GitHub Actions IPs

### Docker Push Failures

If pushing to Docker Hub fails:

1. Verify your Docker credentials are correct
2. Ensure the Docker image name in your `pom.xml` matches your Docker Hub repository
3. Check that you have write permissions to the Docker Hub repository

### Deploy4j Errors

If deploy4j fails during deployment:

1. Review the workflow logs for specific error messages
2. Test your deploy4j configuration locally first
3. Ensure your `config/deploy.yml` is properly configured
4. Verify all secrets are set correctly in GitHub

## Best Practices

1. **Test locally first**: Always test your deploy4j configuration locally before setting up the GitHub Actions workflow
2. **Use deployment environments**: Configure GitHub deployment environments for production deployments with protection rules
3. **Enable branch protection**: Require pull request reviews before merging to branches that trigger deployments
4. **Monitor deployments**: Set up notifications for workflow failures
5. **Keep secrets secure**: Never log secrets or expose them in workflow outputs
6. **Update regularly**: Keep your action versions and deploy4j version up to date

## References

* [GitHub Actions Documentation](https://docs.github.com/en/actions)
* [deploy4j CLI Documentation](https://github.com/teggr/deploy4j)
* [JBang GitHub Action](https://github.com/jbangdev/setup-jbang)
* [Docker Login Action](https://github.com/docker/login-action)
