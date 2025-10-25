package dev.deploy4j.deploy.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Volume")
class VolumeTest {

    @Test
    @DisplayName("should create volume with absolute host path")
    void shouldCreateVolumeWithAbsoluteHostPath() {
        // Arrange & Act
        Volume volume = new Volume("/var/data", "/app/data");

        // Assert
        assertThat(volume.hostPath()).isEqualTo("/var/data");
        assertThat(volume.containerPath()).isEqualTo("/app/data");
    }

    @Test
    @DisplayName("should create volume with relative host path")
    void shouldCreateVolumeWithRelativeHostPath() {
        // Arrange & Act
        Volume volume = new Volume("data", "/app/data");

        // Assert
        assertThat(volume.hostPath()).isEqualTo("data");
        assertThat(volume.containerPath()).isEqualTo("/app/data");
    }

    @Test
    @DisplayName("should generate docker args for absolute host path")
    void shouldGenerateDockerArgsForAbsolutePath() {
        // Arrange
        Volume volume = new Volume("/var/data", "/app/data");

        // Act
        String[] dockerArgs = volume.dockerArgs();

        // Assert
        assertThat(dockerArgs)
                .hasSize(2)
                .containsExactly("--volume", "/var/data:/app/data");
    }

    @Test
    @DisplayName("should generate docker args for relative host path")
    void shouldGenerateDockerArgsForRelativePath() {
        // Arrange
        Volume volume = new Volume("data", "/app/data");

        // Act
        String[] dockerArgs = volume.dockerArgs();

        // Assert
        assertThat(dockerArgs)
                .hasSize(2)
                .contains("--volume")
                .anyMatch(arg -> arg.contains("$(pwd)/data:/app/data"));
    }

    @Test
    @DisplayName("should handle dot relative path")
    void shouldHandleDotRelativePath() {
        // Arrange
        Volume volume = new Volume("./data", "/app/data");

        // Act
        String[] dockerArgs = volume.dockerArgs();

        // Assert
        assertThat(dockerArgs)
                .hasSize(2)
                .contains("--volume")
                .anyMatch(arg -> arg.contains("$(pwd)/./data:/app/data"));
    }

    @Test
    @DisplayName("should handle nested container path")
    void shouldHandleNestedContainerPath() {
        // Arrange
        Volume volume = new Volume("/var/data", "/app/config/data");

        // Act
        String[] dockerArgs = volume.dockerArgs();

        // Assert
        assertThat(dockerArgs)
                .hasSize(2)
                .containsExactly("--volume", "/var/data:/app/config/data");
    }
}
