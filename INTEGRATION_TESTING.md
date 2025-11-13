# Integration Testing with deploy4j

## Overview

This document describes approaches for end-to-end integration testing of deploy4j.

## Manual Integration Testing

For manual integration testing, you can use the deploy4j Docker droplet container which simulates a deployment target server.

### Setup

1. Start the deploy4j-docker-droplet container:

```bash
docker run -d -p 2222:22 --name deploy4j-droplet \
  -v "$HOME/.ssh/id_rsa.pub":/root/.ssh/authorized_keys \
  -v /var/run/docker.sock:/var/run/docker.sock \
  teggr/deploy4j-docker-droplet:latest
```

2. Test SSH connectivity:

```bash
ssh -o StrictHostKeyChecking=no -p 2222 root@localhost
```

3. Test deployment commands against the container:

```bash
# Run deploy4j commands targeting localhost:2222
deploy4j test --host localhost --port 2222
```

### Cleanup

```bash
docker stop deploy4j-droplet
docker rm deploy4j-droplet
```

## Automated Integration Testing

### Current Status

Automated integration tests using Testcontainers are now **implemented** in the `deploy4j-core` module.

### Running Automated Integration Tests

Integration tests are tagged with `@Tag("integration")` and are skipped by default to keep regular test runs fast.

To run the integration tests, you need to:

1. **Ensure Docker is running** on your machine (Testcontainers requires Docker)

2. **Set the environment variable** to enable integration tests:

```bash
export ENABLE_INTEGRATION_TESTS=true
```

3. **Run the tests** using Maven:

```bash
# Run only integration tests
mvn test -pl deploy4j-core -Dgroups=integration

# Run all tests including integration tests
mvn test -pl deploy4j-core
```

### What the Integration Tests Do

The automated integration tests:

- **Setup Test** (`SetupIntegrationTest`):
  - Spins up a deploy4j-docker-droplet container using Testcontainers
  - Verifies SSH connectivity
  - Executes the full setup flow (bootstrap server, push environment, deploy application)

- **Deploy Test** (`DeployIntegrationTest`):
  - Spins up a deploy4j-docker-droplet container using Testcontainers
  - Verifies SSH connectivity
  - Executes the deployment flow (pull image, boot application, prune old containers)

### Test Configuration

The integration tests use programmatic configuration through `TestConfigurationFactory`, which creates a minimal configuration suitable for testing without relying on config files. This approach:

- Allows for easy customization in tests
- Avoids file I/O dependencies
- Makes tests more maintainable

### Integration Test Structure

```
deploy4j-core/src/test/java/dev/deploy4j/integration/
├── BaseIntegrationTest.java           # Base class with Testcontainers setup
├── TestConfigurationFactory.java     # Factory for creating test configurations
├── SetupIntegrationTest.java         # Tests for setup command
└── DeployIntegrationTest.java        # Tests for deploy command
```

### Implementation Details

1. **Testcontainers Support**: Added dependencies for Testcontainers 1.20.4
2. **SSH Configuration**: Tests use programmatic SSH configuration with port forwarding from the container
3. **Docker-in-Docker**: The test container runs in privileged mode to support Docker operations
4. **Test Image**: Uses `teggr/deploy4j-demo:0.0.2-SNAPSHOT` for deployment tests

### Future Implementation

### Future Enhancements

Additional integration tests that could be implemented:

1. **Rollback Tests**: Test application rollback functionality
2. **Accessory Tests**: Test deployment with accessories (databases, caches, etc.)
3. **Multi-host Tests**: Test deployment across multiple hosts
4. **Health Check Tests**: Test application health checking and readiness
5. **Environment Variable Tests**: Test environment variable management
6. **Volume Tests**: Test volume mounting and persistence

## Testing Approaches

### Unit Testing
- **Status**: ✅ Complete
- **Coverage**: Core logic, CLI commands, Maven plugin
- **Run**: `mvn test`

### Integration Testing
- **Status**: ✅ Implemented with Testcontainers
- **Automated**: Available in deploy4j-core module
- **Manual**: Use deploy4j-docker-droplet container
- **Run**: Set `ENABLE_INTEGRATION_TESTS=true` and run `mvn test -pl deploy4j-core -Dgroups=integration`

### End-to-End Testing
- **Status**: ⚠️ Manual only
- **Approach**: Deploy real applications to test VMs or containers
- **Documentation**: See main README.md for deployment examples

## Contributing

If you're interested in extending the automated integration tests:

1. Add new test classes in `deploy4j-core/src/test/java/dev/deploy4j/integration/`
2. Extend `BaseIntegrationTest` to inherit the Testcontainers setup
3. Use `TestConfigurationFactory` to create test configurations
4. Tag tests with `@Tag("integration")` and `@EnabledIfEnvironmentVariable`
5. Document test scenarios and expected outcomes

## References

- [Testcontainers](https://www.testcontainers.org/)
- [deploy4j-docker-droplet](https://github.com/teggr/deploy4j-docker-droplet)
- [JUnit 5](https://junit.org/junit5/)
