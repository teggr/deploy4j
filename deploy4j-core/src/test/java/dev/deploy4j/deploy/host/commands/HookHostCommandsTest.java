package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HookHostCommands")
class HookHostCommandsTest {

    @Mock
    private Configuration config;

    private HookHostCommands hookCommands;

    @BeforeEach
    void setUp() {
        hookCommands = new HookHostCommands(config);
        when(config.hooksPath()).thenReturn("/home/deploy/hooks");
    }

    @Test
    @DisplayName("should return hook file path and environment for run")
    void shouldReturnHookFileAndEnv() {
        // Arrange
        Map<String, String> details = Map.of("version", "1.2.3", "deployer", "john");

        // Act
        List<?> result = hookCommands.run("pre-deploy", details);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo("/home/deploy/hooks/pre-deploy");
        // Second element is env tags
    }

    @Test
    @DisplayName("should return correct hook file path")
    void shouldReturnCorrectHookFilePath() {
        // Act
        String hookFile = hookCommands.hookFile("post-deploy");

        // Assert
        assertThat(hookFile).isEqualTo("/home/deploy/hooks/post-deploy");
    }

    @Test
    @DisplayName("should check if hook file exists")
    void shouldCheckIfHookExists() {
        // Note: This test checks the method exists and returns a boolean
        // The actual file existence check depends on the filesystem
        
        // Act
        boolean exists = hookCommands.hookExists("pre-deploy");

        // Assert
        // Will be false since the file doesn't exist in test environment
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("should construct hook file path with different hook names")
    void shouldConstructDifferentHookPaths() {
        // Act
        String preDeploy = hookCommands.hookFile("pre-deploy");
        String postDeploy = hookCommands.hookFile("post-deploy");
        String preBuild = hookCommands.hookFile("pre-build");

        // Assert
        assertThat(preDeploy).isEqualTo("/home/deploy/hooks/pre-deploy");
        assertThat(postDeploy).isEqualTo("/home/deploy/hooks/post-deploy");
        assertThat(preBuild).isEqualTo("/home/deploy/hooks/pre-build");
    }

    @Test
    @DisplayName("should use configured hooks path")
    void shouldUseConfiguredHooksPath() {
        // Arrange
        when(config.hooksPath()).thenReturn("/custom/hooks/path");
        hookCommands = new HookHostCommands(config);

        // Act
        String hookFile = hookCommands.hookFile("my-hook");

        // Assert
        assertThat(hookFile).isEqualTo("/custom/hooks/path/my-hook");
    }
}
