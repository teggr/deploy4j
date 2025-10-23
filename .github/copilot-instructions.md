# GitHub Copilot Instructions for deploy4j

## Project Overview

deploy4j is a deployment tool for Java web applications, inspired by MRSK, designed to enable low-cost deployment of Java applications onto self-hosted VMs (e.g., Digital Ocean droplets). It provides a Maven plugin and CLI tool for deploying containerized Java applications.

## Key Components

- **deploy4j-core**: Core deployment logic and functionality
- **deploy4j-cli**: Command-line interface for deployment
- **deploy4j-maven-plugin**: Maven plugin for integration with Maven builds
- **deploy4j-ext**: Extensions and additional functionality

## Technology Stack

- **Language**: Java 21
- **Build Tool**: Maven
- **Containerization**: Docker
- **SSH**: For remote server connections
- **Proxy**: Traefik for routing

## Development Guidelines

### Building the Project

```bash
mvn clean install
```

To skip tests during build:
```bash
mvn clean install -DskipTests
```

### Running Tests

```bash
mvn test
```

### Code Style

- Use Lombok annotations where appropriate for reducing boilerplate
- Follow standard Java naming conventions
- Keep methods focused and concise
- Add meaningful comments for complex logic

### Project Structure

Each module follows standard Maven project structure:
```
module-name/
  ├── pom.xml
  └── src/
      ├── main/java/
      └── test/java/
```

## Key Concepts

### Deployment Process

1. Connects to target server via SSH
2. Installs Docker if not present
3. Pushes JAR file and Dockerfile to server
4. Builds Docker image
5. Starts Traefik proxy
6. Starts the service container

### Configuration

The Maven plugin accepts configuration via:
- Plugin configuration in `pom.xml`
- Command-line arguments with `-D` flags
- Maven `settings.xml` file

Required configuration parameters:
- `sshUsername`: SSH user for server connection
- `sshPrivateKeyPath`: Path to SSH private key
- `sshPassPhrase`: SSH key passphrase (if applicable)
- `sshKnownHostsPath`: Path to SSH known_hosts file
- `host`: Target server IP or hostname

### Environment Variables

The project uses `.env` files for environment variable management:
- Read during deployment stage
- Can be templated to fetch secrets from vaults
- Pushed to servers during deployment
- Used by Docker with `--env-file` flag

## Testing

### Local Testing Setup

Use the Docker-based SSH container for local testing:

```bash
# Start test container
docker run -d -p 2222:22 --name deploy4j-droplet \
  -v "$HOME/.ssh/id_rsa.pub":/root/.ssh/authorized_keys \
  teggr/deploy4j-docker-droplet:latest

# Connect via SSH
ssh -o StrictHostKeyChecking=no -p 2222 root@localhost

# Access container shell
docker exec -it deploy4j-droplet /bin/bash
```

## Common Tasks

### Adding a New Feature

1. Identify the appropriate module (core, cli, maven-plugin, or ext)
2. Create feature branch
3. Implement feature with unit tests
4. Update relevant documentation
5. Test with local Docker container
6. Submit pull request

### Adding Dependencies

Add dependencies to the module's `pom.xml` file. Common dependencies are managed in the parent `pom.xml`.

### Debugging Deployment Issues

1. Check SSH connection and credentials
2. Verify Docker is installed on target server
3. Review logs for error messages
4. Test locally with the Docker SSH container

## Important Files

- `pom.xml`: Parent POM with shared configuration
- `.gitignore`: Excludes build artifacts, IDE files, and local config
- `README.md`: User-facing documentation
- `todo.md`: Development tasks and notes

## Additional Notes

- The project requires Java 21 or higher
- Maven 3.9+ is recommended
- SSH key-based authentication is used for server access
- Traefik is used as a reverse proxy for deployed services
- The project targets low-cost deployment scenarios ($6/month VMs)

## Future Development

See `todo.md` for current development priorities:
- Unit test coverage improvements
- Configuration dependency refactoring
- Enhanced secret management integration
