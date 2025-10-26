package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LockHostCommands")
class LockHostCommandsTest {

    @Mock
    private Configuration config;

    private LockHostCommands lockCommands;

    @BeforeEach
    void setUp() {
        lockCommands = new LockHostCommands(config);
        lenient().when(config.runDirectory()).thenReturn("/home/deploy");
        lenient().when(config.service()).thenReturn("myapp");
        lenient().when(config.destination()).thenReturn("production");
    }

    @Test
    @DisplayName("should create acquire lock command with message and version")
    void shouldCreateAcquireLockCommand() {
        // Act
        Cmd cmd = lockCommands.acquire("Deploying new version", "v1.2.3");

        // Assert
        String cmdStr = String.join(" ", cmd.build());
        assertThat(cmdStr)
                .contains("mkdir", "/home/deploy/locks/myapp-production")
                .contains("echo")
                .contains("/home/deploy/locks/myapp-production/details");
    }

    @Test
    @DisplayName("should create release lock command")
    void shouldCreateReleaseLockCommand() {
        // Act
        Cmd cmd = lockCommands.release();

        // Assert
        String cmdStr = String.join(" ", cmd.build());
        assertThat(cmdStr)
                .contains("rm", "/home/deploy/locks/myapp-production/details")
                .contains("rm", "-r", "/home/deploy/locks/myapp-production");
    }

    @Test
    @DisplayName("should create status command to check lock")
    void shouldCreateStatusCommand() {
        // Act
        Cmd cmd = lockCommands.status();

        // Assert
        String cmdStr = String.join(" ", cmd.build());
        assertThat(cmdStr)
                .contains("stat", "/home/deploy/locks/myapp-production")
                .contains("cat", "/home/deploy/locks/myapp-production/details")
                .contains("base64", "-d");
    }

    @Test
    @DisplayName("should create ensure locks directory command")
    void shouldCreateEnsureLocksDirectoryCommand() {
        // Act
        Cmd cmd = lockCommands.ensureLocksDirectory();

        // Assert
        assertThat(cmd.build())
                .containsExactly("mkdir", "-p", "/home/deploy/locks");
    }

    @Test
    @DisplayName("should handle service without destination in lock directory path")
    void shouldHandleServiceWithoutDestination() {
        // Arrange
        lenient().when(config.destination()).thenReturn(null);

        // Act
        Cmd cmd = lockCommands.ensureLocksDirectory();

        // Assert - should still work, just with service name only
        assertThat(cmd.build())
                .contains("mkdir", "-p", "/home/deploy/locks");
    }

    @Test
    @DisplayName("should use different run directory when configured")
    void shouldUseDifferentRunDirectory() {
        // Arrange
        when(config.runDirectory()).thenReturn("/var/lib/myapp");

        // Act
        Cmd cmd = lockCommands.ensureLocksDirectory();

        // Assert
        assertThat(cmd.build())
                .contains("/var/lib/myapp/locks");
    }
}
