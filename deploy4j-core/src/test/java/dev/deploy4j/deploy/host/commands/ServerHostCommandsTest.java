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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServerHostCommands")
class ServerHostCommandsTest {

    @Mock
    private Configuration config;

    private ServerHostCommands serverCommands;

    @BeforeEach
    void setUp() {
        serverCommands = new ServerHostCommands(config);
    }

    @Test
    @DisplayName("should create command to ensure run directory exists")
    void shouldCreateEnsureRunDirectoryCommand() {
        // Arrange
        when(config.runDirectory()).thenReturn("/home/deploy/myapp");

        // Act
        Cmd cmd = serverCommands.ensureRunDirectory();

        // Assert
        assertThat(cmd.build())
                .containsExactly("mkdir", "-p", "/home/deploy/myapp");
        assertThat(cmd.description()).isEqualTo("ensure run directory");
    }

    @Test
    @DisplayName("should handle different run directory paths")
    void shouldHandleDifferentRunDirectoryPaths() {
        // Arrange
        when(config.runDirectory()).thenReturn("/var/lib/deploy");

        // Act
        Cmd cmd = serverCommands.ensureRunDirectory();

        // Assert
        assertThat(cmd.build())
                .contains("/var/lib/deploy");
    }
}
