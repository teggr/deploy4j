package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Role;
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
@DisplayName("AppHostCommands")
class AppHostCommandsTest {

    @Mock
    private Configuration config;

    @Mock
    private Role role;

    private AppHostCommands appHostCommands;

    private final String testHost = "test-host.example.com";

    @BeforeEach
    void setUp() {
        lenient().when(config.service()).thenReturn("test-service");
        lenient().when(config.version()).thenReturn("1.0.0");
        lenient().when(config.destination()).thenReturn("prod");
        lenient().when(config.absoluteImage()).thenReturn("registry/test-service:1.0.0");
        lenient().when(config.volumeArgs()).thenReturn(new String[]{});
        
        lenient().when(role.name()).thenReturn("web");
        lenient().when(role.envArgs(testHost)).thenReturn(java.util.List.of()); // Returns List<String>
        lenient().when(role.loggingArgs()).thenReturn(new String[]{}); // Returns String[]
        lenient().when(role.assetVolumeArgs()).thenReturn(new String[]{}); // Returns String[]
        lenient().when(role.labelArgs()).thenReturn(new String[]{}); // Returns String[]
        lenient().when(role.optionArgs()).thenReturn(java.util.List.of()); // Returns List<String>
        lenient().when(role.cmd()).thenReturn(null);

        appHostCommands = new AppHostCommands(config, role, testHost);
    }

    @Test
    @DisplayName("should generate docker run command with basic options")
    void shouldGenerateDockerRunCommandWithBasicOptions() {
        // Act
        Cmd runCmd = appHostCommands.run(null);

        // Assert
        assertThat(runCmd).isNotNull();
        assertThat(runCmd.build())
            .contains("docker", "run")
            .contains("--detach")
            .contains("--restart", "unless-stopped")
            .contains("--name");
    }

    @Test
    @DisplayName("should generate docker run command with hostname when provided")
    void shouldGenerateDockerRunCommandWithHostname() {
        // Arrange
        String hostName = "custom-hostname";

        // Act
        Cmd runCmd = appHostCommands.run(hostName);

        // Assert
        assertThat(runCmd).isNotNull();
        assertThat(runCmd.build())
            .contains("--hostname", hostName);
    }

    @Test
    @DisplayName("should generate docker run command with environment variables")
    void shouldGenerateDockerRunCommandWithEnvVars() {
        // Act
        Cmd runCmd = appHostCommands.run(null);

        // Assert
        assertThat(runCmd).isNotNull();
        assertThat(runCmd.build())
            .anyMatch(arg -> arg.contains("DEPLOY4J_CONTAINER_NAME"))
            .anyMatch(arg -> arg.contains("DEPLOY4J_VERSION"));
    }

    @Test
    @DisplayName("should generate docker run command with image")
    void shouldGenerateDockerRunCommandWithImage() {
        // Act
        Cmd runCmd = appHostCommands.run(null);

        // Assert
        assertThat(runCmd).isNotNull();
        assertThat(runCmd.build())
            .contains("registry/test-service:1.0.0");
    }

    @Test
    @DisplayName("should generate docker start command")
    void shouldGenerateDockerStartCommand() {
        // Act
        Cmd startCmd = appHostCommands.start();

        // Assert
        assertThat(startCmd).isNotNull();
        assertThat(startCmd.build())
            .contains("docker", "start");
    }

    @Test
    @DisplayName("should generate docker stop command")
    void shouldGenerateDockerStopCommand() {
        // Act
        Cmd stopCmd = appHostCommands.stop();

        // Assert
        assertThat(stopCmd).isNotNull();
        assertThat(stopCmd.description()).isEqualTo("stop container");
    }

    @Test
    @DisplayName("should generate docker stop command with custom wait time")
    void shouldGenerateDockerStopCommandWithWaitTime() {
        // Arrange
        when(config.stopWaitTime()).thenReturn(30);

        // Act
        Cmd stopCmd = appHostCommands.stop();

        // Assert
        assertThat(stopCmd).isNotNull();
        assertThat(stopCmd.description()).isEqualTo("stop container");
    }

    @Test
    @DisplayName("should generate info command")
    void shouldGenerateInfoCommand() {
        // Act
        Cmd infoCmd = appHostCommands.info();

        // Assert
        assertThat(infoCmd).isNotNull();
        assertThat(infoCmd.build()).contains("docker", "ps");
    }

    @Test
    @DisplayName("should generate current running container ID command")
    void shouldGenerateCurrentRunningContainerIdCommand() {
        // Act
        Cmd containerIdCmd = appHostCommands.currentRunningContainerId();

        // Assert
        assertThat(containerIdCmd).isNotNull();
        assertThat(String.join(" ", containerIdCmd.build())).contains("--quiet");
    }

    @Test
    @DisplayName("should generate container ID for version command")
    void shouldGenerateContainerIdForVersionCommand() {
        // Arrange
        String version = "1.2.3";

        // Act
        Cmd containerIdCmd = appHostCommands.containerIdForVersion(version);

        // Assert
        assertThat(containerIdCmd).isNotNull();
        assertThat(containerIdCmd.description()).isEqualTo("container id for version");
    }

    @Test
    @DisplayName("should generate current running version command")
    void shouldGenerateCurrentRunningVersionCommand() {
        // Act
        Cmd versionCmd = appHostCommands.currentRunningVersion();

        // Assert
        assertThat(versionCmd).isNotNull();
        assertThat(versionCmd.description()).isEqualTo("current running version");
    }

    @Test
    @DisplayName("should generate list versions command")
    void shouldGenerateListVersionsCommand() {
        // Act
        Cmd listCmd = appHostCommands.listVersions();

        // Assert
        assertThat(listCmd).isNotNull();
        assertThat(listCmd.description()).isEqualTo("list versions");
    }

    @Test
    @DisplayName("should generate status command for version")
    void shouldGenerateStatusCommandForVersion() {
        // Arrange
        String version = "1.0.0";

        // Act
        Cmd statusCmd = appHostCommands.status(version);

        // Assert
        assertThat(statusCmd).isNotNull();
        assertThat(statusCmd.build()).contains("docker", "inspect");
    }
}
