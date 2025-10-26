package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ServerConfig")
class ServerConfigTest {

    @Test
    @DisplayName("should create server config with just host")
    void shouldCreateWithJustHost() {
        // Act
        ServerConfig config = new ServerConfig("server1.example.com");

        // Assert
        assertThat(config.host()).isEqualTo("server1.example.com");
        assertThat(config.tags()).isEmpty();
    }

    @Test
    @DisplayName("should create server config with host and single tag")
    void shouldCreateWithSingleTag() {
        // Arrange
        Map<String, String> hostWithTags = Map.of("server1.example.com", "web");

        // Act
        ServerConfig config = new ServerConfig(hostWithTags);

        // Assert
        assertThat(config.host()).isEqualTo("server1.example.com");
        assertThat(config.tags()).containsExactly("web");
    }

    @Test
    @DisplayName("should create server config with host and multiple tags")
    void shouldCreateWithMultipleTags() {
        // Arrange
        Map<String, List<String>> hostWithTags = Map.of(
                "server1.example.com", List.of("web", "primary", "production")
        );

        // Act
        ServerConfig config = new ServerConfig(hostWithTags);

        // Assert
        assertThat(config.host()).isEqualTo("server1.example.com");
        assertThat(config.tags())
                .hasSize(3)
                .containsExactly("web", "primary", "production");
    }

    @Test
    @DisplayName("should handle empty tags list")
    void shouldHandleEmptyTagsList() {
        // Arrange
        Map<String, List<String>> hostWithTags = Map.of("server1.example.com", List.of());

        // Act
        ServerConfig config = new ServerConfig(hostWithTags);

        // Assert
        assertThat(config.host()).isEqualTo("server1.example.com");
        assertThat(config.tags()).isEmpty();
    }

    @Test
    @DisplayName("should handle null value for tags")
    void shouldHandleNullValueForTags() {
        // Arrange
        Map<String, Object> hostWithTags = Map.of("server1.example.com", new Object());

        // Act
        ServerConfig config = new ServerConfig(hostWithTags);

        // Assert
        assertThat(config.host()).isEqualTo("server1.example.com");
        assertThat(config.tags()).isEmpty();
    }
}
