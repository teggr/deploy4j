package dev.deploy4j.deploy.configuration.env;

import dev.deploy4j.deploy.Secrets;
import dev.deploy4j.deploy.configuration.Env;
import dev.deploy4j.deploy.configuration.raw.EnvironmentConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Tag")
class TagTest {

    @Test
    @DisplayName("should create tag with name and config")
    void shouldCreateTagWithNameAndConfig() {
        // Arrange
        EnvironmentConfig config = new EnvironmentConfig(Map.of("KEY", "value"), null, null, null);
      Secrets secrets = mock(Secrets.class);

        // Act
        Tag tag = new Tag("production", config, secrets);

        // Assert
        assertThat(tag.name()).isEqualTo("production");
        assertThat(tag.config()).isEqualTo(config);
    }

    @Test
    @DisplayName("should create env from config")
    void shouldCreateEnvFromConfig() {
        // Arrange
        EnvironmentConfig config = new EnvironmentConfig(Map.of("APP_NAME", "myapp"), null, null, null);
      Secrets secrets = mock(Secrets.class);
        Tag tag = new Tag("staging", config, secrets);

        // Act
        Env env = tag.env();

        // Assert
        assertThat(env).isNotNull();
        assertThat(env.clear()).containsEntry("APP_NAME", "myapp");
    }

    @Test
    @DisplayName("should handle null config")
    void shouldHandleNullConfig() {
        // Arrange & Act
      Secrets secrets = mock(Secrets.class);
        Tag tag = new Tag("test", null, secrets);

        // Assert
        assertThat(tag.name()).isEqualTo("test");
        assertThat(tag.config()).isNull();
        
        // Verify env and args don't error with null config
        Env env = tag.env();
        assertThat(env).isNotNull();
        assertThat(env.clear()).isEmpty();
       // assertThat(env.clearArgs()).isNotEmpty(); // Should have --env-file arg
    }

    @Test
    @DisplayName("should handle empty tag name")
    void shouldHandleEmptyTagName() {
        // Arrange
      Secrets secrets = mock(Secrets.class);
        EnvironmentConfig config = new EnvironmentConfig(Map.of(), null, null, null);

        // Act
        Tag tag = new Tag("", config, secrets);

        // Assert
        assertThat(tag.name()).isEmpty();
        assertThat(tag.config()).isEqualTo(config);
    }
}
