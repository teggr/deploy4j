package dev.deploy4j.integration;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

/**
 * Base class for integration tests using Testcontainers.
 * Provides a test SSH container that simulates a deployment target server.
 */
@Testcontainers
@Tag("integration")
@EnabledIfEnvironmentVariable(named = "ENABLE_INTEGRATION_TESTS", matches = "true")
public abstract class BaseIntegrationTest {

    /**
     * The deploy4j Docker droplet container that simulates a deployment target.
     * This container:
     * - Runs SSH server on port 22
     * - Has Docker installed and running
     * - Accepts public key authentication
     */
    @Container
    protected static final GenericContainer<?> sshContainer = 
        new GenericContainer<>(DockerImageName.parse("teggr/deploy4j-docker-droplet:latest"))
            .withExposedPorts(22)
            .withPrivilegedMode(true)
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(2)));

    /**
     * Gets the mapped SSH port on the host machine.
     * 
     * @return the host port mapped to container port 22
     */
    protected static int getSshPort() {
        return sshContainer.getMappedPort(22);
    }

    /**
     * Gets the SSH host address.
     * 
     * @return "localhost" for accessing the container
     */
    protected static String getSshHost() {
        return "localhost";
    }

    /**
     * Gets the SSH username for the container.
     * 
     * @return "root" as the default user
     */
    protected static String getSshUsername() {
        return "root";
    }
}
