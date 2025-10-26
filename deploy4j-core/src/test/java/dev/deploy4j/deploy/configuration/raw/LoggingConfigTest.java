package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoggingConfig")
class LoggingConfigTest {

    @Test
    @DisplayName("should create logging config with driver and options")
    void shouldCreateWithDriverAndOptions() {
        // Arrange
        Map<String, String> options = Map.of("max-size", "10m", "max-file", "3");

        // Act
        LoggingConfig config = new LoggingConfig("json-file", options);

        // Assert
        assertThat(config.driver()).isEqualTo("json-file");
        assertThat(config.options()).isEqualTo(options);
    }

    @Test
    @DisplayName("should create empty logging config with no-args constructor")
    void shouldCreateEmptyConfig() {
        // Act
        LoggingConfig config = new LoggingConfig();

        // Assert
        assertThat(config.driver()).isNull();
        assertThat(config.options()).isNull();
    }

    @Test
    @DisplayName("should merge configs with other taking precedence")
    void shouldDeepMergeConfigs() {
        // Arrange
        LoggingConfig base = new LoggingConfig("json-file", Map.of("max-size", "10m"));
        LoggingConfig override = new LoggingConfig("syslog", Map.of("tag", "myapp"));

        // Act
        LoggingConfig merged = base.deepMerge(override);

        // Assert
        assertThat(merged.driver()).isEqualTo("syslog");
        assertThat(merged.options()).isEqualTo(Map.of("tag", "myapp"));
    }

    @Test
    @DisplayName("should keep base values when other has nulls")
    void shouldKeepBaseValuesWhenOtherIsNull() {
        // Arrange
        LoggingConfig base = new LoggingConfig("json-file", Map.of("max-size", "10m"));
        LoggingConfig override = new LoggingConfig(null, null);

        // Act
        LoggingConfig merged = base.deepMerge(override);

        // Assert
        assertThat(merged.driver()).isEqualTo("json-file");
        assertThat(merged.options()).isEqualTo(Map.of("max-size", "10m"));
    }

    @Test
    @DisplayName("should override only driver when options is null")
    void shouldOverrideOnlyDriver() {
        // Arrange
        LoggingConfig base = new LoggingConfig("json-file", Map.of("max-size", "10m"));
        LoggingConfig override = new LoggingConfig("syslog", null);

        // Act
        LoggingConfig merged = base.deepMerge(override);

        // Assert
        assertThat(merged.driver()).isEqualTo("syslog");
        assertThat(merged.options()).isEqualTo(Map.of("max-size", "10m"));
    }

    @Test
    @DisplayName("should handle both configs having null values")
    void shouldHandleBothConfigsWithNullValues() {
        // Arrange
        LoggingConfig base = new LoggingConfig();
        LoggingConfig override = new LoggingConfig();

        // Act
        LoggingConfig merged = base.deepMerge(override);

        // Assert
        assertThat(merged.driver()).isNull();
        assertThat(merged.options()).isNull();
    }
}
