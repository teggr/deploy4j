package dev.deploy4j.deploy.configuration.env;

import dev.deploy4j.deploy.configuration.Env;
import dev.deploy4j.deploy.configuration.raw.EnvironmentConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tag")
class TagTest {

    @Test
    @DisplayName("should create tag with name and config")
    void shouldCreateTagWithNameAndConfig() {
        // Arrange
        EnvironmentConfig config = new EnvironmentConfig(Map.of("KEY", "value"), null, null, null);

        // Act
        Tag tag = new Tag("production", config);

        // Assert
        assertThat(tag.name()).isEqualTo("production");
        assertThat(tag.config()).isEqualTo(config);
    }

    @Test
    @DisplayName("should create env from config")
    void shouldCreateEnvFromConfig() {
        // Arrange
        EnvironmentConfig config = new EnvironmentConfig(Map.of("APP_NAME", "myapp"), null, null, null);
        Tag tag = new Tag("staging", config);

        // Act
        Env env = tag.env();

        // Assert
        assertThat(env).isNotNull();
    }

    @Test
    @DisplayName("should handle null config")
    void shouldHandleNullConfig() {
        // Arrange & Act
        Tag tag = new Tag("test", null);

        // Assert
        assertThat(tag.name()).isEqualTo("test");
        assertThat(tag.config()).isNull();
    }

    @Test
    @DisplayName("should handle empty tag name")
    void shouldHandleEmptyTagName() {
        // Arrange
        EnvironmentConfig config = new EnvironmentConfig(Map.of(), null, null, null);

        // Act
        Tag tag = new Tag("", config);

        // Assert
        assertThat(tag.name()).isEmpty();
        assertThat(tag.config()).isEqualTo(config);
    }
}
