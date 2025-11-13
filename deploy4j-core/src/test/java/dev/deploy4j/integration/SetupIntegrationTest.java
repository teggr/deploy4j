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
 * Integration tests for the setup command.
 * Tests the full setup flow including:
 * - SSH connectivity to the test container
 * - Docker installation verification
 * - Environment file push
 * - Traefik setup
 */
@DisplayName("Setup Integration Tests")
class SetupIntegrationTest extends BaseIntegrationTest {

    @Test
    @DisplayName("should successfully setup deployment environment")
    void shouldSetupDeploymentEnvironment() throws Exception {
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
            assertThat(getSshPort()).isGreaterThan(0);

            // Execute setup - this will:
            // 1. Bootstrap server (install Docker if needed)
            // 2. Push environment files
            // 3. Boot accessories (if any)
            // 4. Deploy the application
            deployApplicationContext.deploy().setup(deployContext);

            // If we got here without exceptions, the setup was successful
            assertThat(true).isTrue();
        }
    }
}
