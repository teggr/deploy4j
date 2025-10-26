package dev.deploy4j.deploy.host.commands;

import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DockerHostCommands")
class DockerHostCommandsTest {

    private DockerHostCommands dockerCommands;

    @BeforeEach
    void setUp() {
        // Use null config since these methods don't use it
        dockerCommands = new DockerHostCommands(null);
    }

    @Test
    @DisplayName("should create install command")
    void shouldCreateInstallCommand() {
        // Act
        Cmd cmd = dockerCommands.install();

        // Assert
        assertThat(cmd.build())
                .contains("sh")
                .contains("-c");
        assertThat(cmd.description()).isEqualTo("install");
    }

    @Test
    @DisplayName("should create installed check command")
    void shouldCreateInstalledCheckCommand() {
        // Act
        Cmd cmd = dockerCommands.installed();

        // Assert
        assertThat(cmd.build())
                .containsExactly("docker", "-v");
        assertThat(cmd.description()).isEqualTo("installed");
    }

    @Test
    @DisplayName("should create running check command")
    void shouldCreateRunningCheckCommand() {
        // Act
        Cmd cmd = dockerCommands.running();

        // Assert
        assertThat(cmd.build())
                .containsExactly("docker", "version");
        assertThat(cmd.description()).isEqualTo("running");
    }

    @Test
    @DisplayName("should create superuser check command")
    void shouldCreateSuperuserCheckCommand() {
        // Act
        Cmd cmd = dockerCommands.superUser();

        // Assert
        assertThat(cmd.build())
                .hasSize(1);
        assertThat(cmd.build().get(0))
                .contains("EUID")
                .contains("id -u")
                .contains("sudo")
                .contains("su");
        assertThat(cmd.description()).isEqualTo("superuser");
    }

    @Test
    @DisplayName("install command should use curl or wget")
    void installCommandShouldUseCurlOrWget() {
        // Act
        Cmd cmd = dockerCommands.install();

        // Assert
        String cmdString = String.join(" ", cmd.build());
        assertThat(cmdString)
                .containsAnyOf("curl", "wget")
                .contains("https://get.docker.com");
    }
}
