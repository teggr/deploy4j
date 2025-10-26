package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Env;
import dev.deploy4j.deploy.configuration.Traefik;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraefikHostCommands")
class TraefikHostCommandsTest {

    @Mock
    private Configuration config;

    @Mock
    private Traefik traefik;

    @Mock
    private Env env;

    private TraefikHostCommands traefikCommands;

    @BeforeEach
    void setUp() {
        traefikCommands = new TraefikHostCommands(config);
        lenient().when(config.traefik()).thenReturn(traefik);
        lenient().when(traefik.env()).thenReturn(env);
        lenient().when(traefik.image()).thenReturn("traefik:v2.10");
        lenient().when(traefik.publish()).thenReturn(true);
        lenient().when(traefik.port()).thenReturn("80:80");
        lenient().when(traefik.labels()).thenReturn(Map.of());
        lenient().when(traefik.options()).thenReturn(Map.of());
        lenient().when(traefik.args()).thenReturn(Map.of());
        lenient().when(config.loggingArgs()).thenReturn(new String[]{});
        lenient().when(env.args()).thenReturn(List.of());
    }

    @Test
    @DisplayName("should create traefik run command with all options")
    void shouldCreateRunCommand() {
        // Act
        Cmd cmd = traefikCommands.run();

        // Assert
        assertThat(cmd.build())
                .contains("docker", "run")
                .contains("--name", "traefik")
                .contains("--detach")
                .contains("--restart", "unless-stopped")
                .contains("--volume", "/var/run/docker.sock:/var/run/docker.sock")
                .contains("traefik:v2.10")
                .contains("--providers.docker");
        assertThat(cmd.description()).isEqualTo("run traefik");
    }

    @Test
    @DisplayName("should create start command")
    void shouldCreateStartCommand() {
        // Act
        Cmd cmd = traefikCommands.start();

        // Assert
        assertThat(cmd.build())
                .contains("docker", "container", "start", "traefik");
        assertThat(cmd.description()).isEqualTo("start traefik");
    }

    @Test
    @DisplayName("should create stop command")
    void shouldCreateStopCommand() {
        // Act
        Cmd cmd = traefikCommands.stop();

        // Assert
        assertThat(cmd.build())
                .contains("docker", "container", "stop", "traefik");
        assertThat(cmd.description()).isEqualTo("stop traefik");
    }

    @Test
    @DisplayName("should create start or run command")
    void shouldCreateStartOrRunCommand() {
        // Act
        Cmd cmd = traefikCommands.startOrRun();

        // Assert
        assertThat(cmd.description()).isEqualTo("start or run");
        // This is a composite command with fallback
    }

    @Test
    @DisplayName("should create info command")
    void shouldCreateInfoCommand() {
        // Act
        Cmd cmd = traefikCommands.info();

        // Assert
        assertThat(cmd.build())
                .contains("docker", "ps")
                .contains("--filter", "name=^traefik$");
        assertThat(cmd.description()).isEqualTo("info");
    }

    @Test
    @DisplayName("should create logs command with filters")
    void shouldCreateLogsCommandWithFilters() {
        // Act
        Cmd cmd = traefikCommands.logs("5m", "100", "error", "-i");

        // Assert
        String cmdStr = String.join(" ", cmd.build());
        assertThat(cmdStr)
                .contains("docker", "logs", "traefik")
                .contains("--since 5m")
                .contains("--tail 100")
                .contains("--timestamps")
                .contains("grep")
                .contains("error")
                .contains("-i");
        assertThat(cmd.description()).isEqualTo("logs");
    }

    @Test
    @DisplayName("should create logs command without filters")
    void shouldCreateLogsCommandWithoutFilters() {
        // Act
        Cmd cmd = traefikCommands.logs(null, null, null, null);

        // Assert
        String cmdStr = String.join(" ", cmd.build());
        assertThat(cmdStr)
                .contains("docker", "logs", "traefik")
                .contains("--timestamps")
                .doesNotContain("grep");
    }

    @Test
    @DisplayName("should throw exception for followLogs not implemented")
    void shouldThrowExceptionForFollowLogs() {
        // Act & Assert
        assertThatThrownBy(() -> traefikCommands.followLogs())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("should create remove container command")
    void shouldCreateRemoveContainerCommand() {
        // Act
        Cmd cmd = traefikCommands.removeContainer();

        // Assert
        assertThat(cmd.build())
                .contains("docker", "container", "prune")
                .contains("--force")
                .contains("--filter", "label=org.opencontainers.image.title=Traefik");
        assertThat(cmd.description()).isEqualTo("remove traefik");
    }

    @Test
    @DisplayName("should create remove image command")
    void shouldCreateRemoveImageCommand() {
        // Act
        Cmd cmd = traefikCommands.removeImage();

        // Assert
        assertThat(cmd.build())
                .contains("docker", "image", "prune")
                .contains("--force")
                .contains("--filter", "label=org.opencontainers.image.title=Traefik");
        assertThat(cmd.description()).isEqualTo("remove traefik image");
    }

    @Test
    @DisplayName("should create make env directory command")
    void shouldCreateMakeEnvDirectoryCommand() {
        // Arrange
        when(env.secretsDirectory()).thenReturn("/deploy/traefik/secrets");

        // Act
        Cmd cmd = traefikCommands.makeEnvDirectory();

        // Assert
        assertThat(cmd.build())
                .contains("mkdir", "-p", "/deploy/traefik/secrets");
    }

    @Test
    @DisplayName("should create remove env file command")
    void shouldCreateRemoveEnvFileCommand() {
        // Arrange
        when(env.secretsFile()).thenReturn("/deploy/traefik/secrets/.env");

        // Act
        Cmd cmd = traefikCommands.removeEnvFile();

        // Assert
        assertThat(cmd.build())
                .contains("rm", "-f", "/deploy/traefik/secrets/.env");
        assertThat(cmd.description()).isEqualTo("remove traefik env file");
    }

    @Test
    @DisplayName("should include publish args when publish is true")
    void shouldIncludePublishArgsWhenEnabled() {
        // Arrange
        when(traefik.publish()).thenReturn(true);
        when(traefik.port()).thenReturn("8080:80");

        // Act
        Cmd cmd = traefikCommands.run();

        // Assert
        assertThat(cmd.build())
                .contains("--publish", "8080:80");
    }

    @Test
    @DisplayName("should delegate to traefik config for properties")
    void shouldDelegateToTraefikConfig() {
        // Act
        String port = traefikCommands.port();
        boolean publish = traefikCommands.publish();
        String image = traefikCommands.image();
        Env envResult = traefikCommands.env();

        // Assert
        assertThat(port).isEqualTo("80:80");
        assertThat(publish).isTrue();
        assertThat(image).isEqualTo("traefik:v2.10");
        assertThat(envResult).isEqualTo(env);
    }
}
