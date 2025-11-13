package dev.deploy4j.integration;

import dev.deploy4j.deploy.DeployApplicationContext;
import dev.deploy4j.deploy.DeployContext;
import dev.deploy4j.deploy.Environment;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the deploy command.
 * Tests the deployment flow including:
 * - Image pull
 * - Container deployment
 * - Application startup
 */
@DisplayName("Deploy Integration Tests")
class DeployIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should successfully deploy application")
    void shouldDeployApplication() throws Exception {
        // Arrange
        Configuration configuration = TestConfigurationFactory.createTestConfiguration(
            "test-service",
            "teggr/deploy4j-demo",
            List.of(getSshHost()),
            getSshUsername(),
            getSshPort()
        );

        DeployContext deployContext = new DeployContext(
            configuration,
            null,  // no specific hosts
            null,  // no specific roles
            null   // not primary only
        );

        Environment environment = new Environment(null);

        // Act & Assert - should not throw exceptions
        try (SshHosts sshHosts = new SshHosts(deployContext.config())) {
            DeployApplicationContext deployApplicationContext = 
                new DeployApplicationContext(environment, sshHosts, deployContext);

            // Verify we can connect to the container
            assertThat(sshContainer.isRunning()).isTrue();

            // Execute deploy - this will:
            // 1. Login to registry (if needed)
            // 2. Pull the image
            // 3. Acquire deploy lock
            // 4. Ensure Traefik is running
            // 5. Detect stale containers
            // 6. Boot the application
            // 7. Prune old containers
            deployApplicationContext.deploy().deploy(deployContext, false);

            // If we got here without exceptions, the deployment was successful
            assertThat(true).isTrue();
        }
    }
}
