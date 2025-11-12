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

Automated integration tests using Testcontainers are currently **not implemented** due to dependency conflicts between Testcontainers and Jackson libraries used by deploy4j.

### Future Implementation

To implement automated integration tests:

1. **Resolve Dependency Conflicts**: Update Jackson dependencies to versions compatible with Testcontainers, or use dependency management to isolate versions.

2. **Add Testcontainers Support**: Add the following dependencies to `deploy4j-core/pom.xml`:

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
</dependency>
```

3. **Create Integration Tests**: Create tests in `src/test/java/dev/deploy4j/integration/` that:
   - Spin up the deploy4j-docker-droplet container
   - Test SSH connectivity
   - Test deployment commands
   - Test application lifecycle (deploy, rollback, etc.)

### Example Integration Test Pattern

```java
@Testcontainers
@Tag("integration")
@EnabledIfEnvironmentVariable(named = "ENABLE_INTEGRATION_TESTS", matches = "true")
class DeploymentIntegrationTest {

    @Container
    private final GenericContainer<?> sshContainer = 
        new GenericContainer<>("teggr/deploy4j-docker-droplet:latest")
            .withExposedPorts(22)
            .withPrivilegedMode(true);

    @Test
    void shouldDeployApplication() {
        // Test full deployment flow
    }
}
```

## Testing Approaches

### Unit Testing
- **Status**: ✅ Complete
- **Coverage**: Core logic, CLI commands, Maven plugin
- **Run**: `mvn test`

### Integration Testing
- **Status**: ⚠️ Manual only (Docker container available)
- **Automated**: Not yet implemented (dependency conflicts)
- **Manual**: Use deploy4j-docker-droplet container

### End-to-End Testing
- **Status**: ⚠️ Manual only
- **Approach**: Deploy real applications to test VMs or containers
- **Documentation**: See main README.md for deployment examples

## Contributing

If you're interested in helping implement automated integration tests:

1. Investigate Jackson version conflicts with Testcontainers
2. Propose dependency management solutions
3. Create integration test suite using Testcontainers
4. Document test scenarios and expected outcomes

## References

- [Testcontainers](https://www.testcontainers.org/)
- [deploy4j-docker-droplet](https://github.com/teggr/deploy4j-docker-droplet)
- [JUnit 5](https://junit.org/junit5/)
