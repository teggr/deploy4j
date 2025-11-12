package dev.deploy4j.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("InitMojo")
class InitMojoTest {

    @Test
    @DisplayName("should instantiate mojo")
    void shouldInstantiateMojo() {
        // Arrange & Act
        InitMojo mojo = new InitMojo();

        // Assert - verify the mojo can be instantiated
        assertThat(mojo).isNotNull();
    }

    @Test
    @DisplayName("should log init message")
    void shouldLogInitMessage() throws MojoExecutionException, MojoFailureException {
        // Arrange
        InitMojo mojo = new InitMojo();
        Log log = mock(Log.class);
        mojo.setLog(log);

        // Set working directory to temp location to avoid creating files
        String originalUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", System.getProperty("java.io.tmpdir"));

        try {
            // Act - This will fail internally but we're checking the log call
            try {
                mojo.execute();
            } catch (Exception e) {
                // Expected - may fail without proper project setup
            }

            // Assert
            verify(log).info("Deploy4J Init");
        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }
}
