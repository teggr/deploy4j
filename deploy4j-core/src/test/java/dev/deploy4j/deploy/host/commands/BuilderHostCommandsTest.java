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
@DisplayName("BuilderHostCommands")
class BuilderHostCommandsTest {

    @Mock
    private Configuration configuration;

    private BuilderHostCommands builderCommands;

    @BeforeEach
    void setUp() {
        when(configuration.absoluteImage()).thenReturn("registry.example.com/myapp:1.0.0");
        builderCommands = new BuilderHostCommands(configuration);
    }

    @Test
    @DisplayName("should create clean command to remove image")
    void shouldCreateCleanCommand() {
        // Act
        Cmd cmd = builderCommands.clean();

        // Assert
        assertThat(cmd.build())
                .contains("docker", "image", "rm")
                .contains("--force")
                .contains("registry.example.com/myapp:1.0.0");
        assertThat(cmd.description()).isEqualTo("clean");
    }

    @Test
    @DisplayName("should create pull command for image")
    void shouldCreatePullCommand() {
        // Act
        Cmd cmd = builderCommands.pull();

        // Assert
        assertThat(cmd.build())
                .contains("docker", "pull")
                .contains("registry.example.com/myapp:1.0.0");
        assertThat(cmd.description()).isEqualTo("pull");
    }

    @Test
    @DisplayName("should create validateImage command with service label check")
    void shouldCreateValidateImageCommand() {
        // Act
        Cmd cmd = builderCommands.validateImage();

        // Assert
        // The command is complex with pipes and greps, so we just verify key parts
        assertThat(cmd.build())
                .contains("docker", "inspect")
                .contains("registry.example.com/myapp:1.0.0");
        assertThat(cmd.description()).isEqualTo("validate image");
    }
}
