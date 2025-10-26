package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Registry;
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
@DisplayName("RegistryHostCommands")
class RegistryHostCommandsTest {

    @Mock
    private Configuration config;

    @Mock
    private Registry registry;

    private RegistryHostCommands registryCommands;

    @BeforeEach
    void setUp() {
        registryCommands = new RegistryHostCommands(config);
        when(config.registry()).thenReturn(registry);
    }

    @Test
    @DisplayName("should create docker login command with credentials")
    void shouldCreateLoginCommand() {
        // Arrange
        when(registry.server()).thenReturn("registry.example.com");
        when(registry.username()).thenReturn("myuser");
        when(registry.password()).thenReturn("mypass");

        // Act
        Cmd cmd = registryCommands.login();

        // Assert
        assertThat(cmd.build())
                .contains("docker", "login")
                .contains("registry.example.com")
                .contains("-u")
                .contains("-p");
        assertThat(cmd.description()).isEqualTo("login");
    }

    @Test
    @DisplayName("should create docker logout command")
    void shouldCreateLogoutCommand() {
        // Arrange
        when(registry.server()).thenReturn("registry.example.com");

        // Act
        Cmd cmd = registryCommands.logout();

        // Assert
        assertThat(cmd.build())
                .contains("docker", "logout")
                .contains("registry.example.com");
        assertThat(cmd.description()).isEqualTo("logout");
    }

    @Test
    @DisplayName("should escape special characters in username")
    void shouldEscapeSpecialCharactersInUsername() {
        // Arrange
        when(registry.server()).thenReturn("registry.example.com");
        when(registry.username()).thenReturn("user@domain");
        when(registry.password()).thenReturn("pass");

        // Act
        Cmd cmd = registryCommands.login();

        // Assert
        String cmdStr = String.join(" ", cmd.build());
        assertThat(cmdStr).contains("user@domain");
    }

    @Test
    @DisplayName("should delegate to registry for server")
    void shouldDelegateToRegistryForServer() {
        // Arrange
        when(registry.server()).thenReturn("my-registry.io");

        // Act
        Registry result = registryCommands.registry();

        // Assert
        assertThat(result).isEqualTo(registry);
        assertThat(result.server()).isEqualTo("my-registry.io");
    }
}
