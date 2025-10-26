package dev.deploy4j.deploy.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConfigureArgs")
class ConfigureArgsTest {

    @Test
    @DisplayName("should create instance with all fields")
    void shouldCreateInstanceWithAllFields() {
        // Act
        ConfigureArgs args = new ConfigureArgs("config.yaml", "production", "1.0.0");

        // Assert
        assertThat(args.configFile()).isEqualTo("config.yaml");
        assertThat(args.destination()).isEqualTo("production");
        assertThat(args.version()).isEqualTo("1.0.0");
    }

    @Test
    @DisplayName("should handle null values")
    void shouldHandleNullValues() {
        // Act
        ConfigureArgs args = new ConfigureArgs(null, null, null);

        // Assert
        assertThat(args.configFile()).isNull();
        assertThat(args.destination()).isNull();
        assertThat(args.version()).isNull();
    }

    @Test
    @DisplayName("should implement equals correctly")
    void shouldImplementEqualsCorrectly() {
        // Arrange
        ConfigureArgs args1 = new ConfigureArgs("config.yaml", "prod", "1.0");
        ConfigureArgs args2 = new ConfigureArgs("config.yaml", "prod", "1.0");
        ConfigureArgs args3 = new ConfigureArgs("other.yaml", "prod", "1.0");

        // Assert
        assertThat(args1).isEqualTo(args2);
        assertThat(args1).isNotEqualTo(args3);
    }

    @Test
    @DisplayName("should implement hashCode correctly")
    void shouldImplementHashCodeCorrectly() {
        // Arrange
        ConfigureArgs args1 = new ConfigureArgs("config.yaml", "prod", "1.0");
        ConfigureArgs args2 = new ConfigureArgs("config.yaml", "prod", "1.0");

        // Assert
        assertThat(args1.hashCode()).isEqualTo(args2.hashCode());
    }

    @Test
    @DisplayName("should implement toString correctly")
    void shouldImplementToStringCorrectly() {
        // Arrange
        ConfigureArgs args = new ConfigureArgs("config.yaml", "prod", "1.0");

        // Act
        String result = args.toString();

        // Assert
        assertThat(result)
            .contains("config.yaml")
            .contains("prod")
            .contains("1.0");
    }
}
