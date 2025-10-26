package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.LoggingConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Logging")
class LoggingTest {

    @Test
    @DisplayName("should use default values when config is null")
    void shouldUseDefaultValuesWhenConfigIsNull() {
        // Arrange
        Logging logging = new Logging(null, "production");

        // Act & Assert
        assertThat(logging.driver()).isNull();
        assertThat(logging.options()).isEmpty();
    }

    @Test
    @DisplayName("should use config values when provided")
    void shouldUseConfigValuesWhenProvided() {
        // Arrange
        Map<String, String> options = Map.of("max-size", "20m", "max-file", "3");
        LoggingConfig config = new LoggingConfig("json-file", options);

        Logging logging = new Logging(config, "production");

        // Act & Assert
        assertThat(logging.driver()).isEqualTo("json-file");
        assertThat(logging.options()).containsExactlyInAnyOrderEntriesOf(options);
    }

    @Test
    @DisplayName("should generate args with empty options when no driver or options set")
    void shouldGenerateArgsWithEmptyOptionsWhenNoDriverOrOptions() {
        // Arrange
        Logging logging = new Logging(null, "production");

        // Act
        String[] args = logging.args();

        // Assert
        // When driver is null and options() returns empty map (not null),
        // the condition (driver() != null || options() != null) is true
        // So it won't use the default. It will return empty array.
        assertThat(args).isEmpty();
    }

    @Test
    @DisplayName("should generate args with driver")
    void shouldGenerateArgsWithDriver() {
        // Arrange
        LoggingConfig config = new LoggingConfig("syslog", null);
        Logging logging = new Logging(config, "production");

        // Act
        String[] args = logging.args();

        // Assert
        // Note: optionize adds quotes around values
        assertThat(args).containsExactly("--log-driver", "\"syslog\"");
    }

    @Test
    @DisplayName("should generate args with options")
    void shouldGenerateArgsWithOptions() {
        // Arrange
        Map<String, String> options = Map.of("max-size", "50m", "max-file", "5");
        LoggingConfig config = new LoggingConfig(null, options);
        Logging logging = new Logging(config, "production");

        // Act
        String[] args = logging.args();

        // Assert
        assertThat(args)
                .hasSize(4) // --log-opt key=value --log-opt key=value
                .contains("--log-opt");
    }

    @Test
    @DisplayName("should generate args with driver and options")
    void shouldGenerateArgsWithDriverAndOptions() {
        // Arrange
        Map<String, String> options = Map.of("syslog-address", "tcp://127.0.0.1:514");
        LoggingConfig config = new LoggingConfig("syslog", options);
        Logging logging = new Logging(config, "production");

        // Act
        String[] args = logging.args();

        // Assert
        // Note: opt optionize adds quotes
        assertThat(args)
                .hasSize(4)
                .contains("--log-driver", "\"syslog\"", "--log-opt");
    }

    @Test
    @DisplayName("should merge with another Logging")
    void shouldMergeWithAnotherLogging() {
        // Arrange
        LoggingConfig config1 = new LoggingConfig("json-file", Map.of("max-size", "10m"));
        LoggingConfig config2 = new LoggingConfig(null, Map.of("max-file", "3"));

        Logging logging1 = new Logging(config1, "prod");
        Logging logging2 = new Logging(config2, "stage");

        // Act
        Logging merged = logging1.merge(logging2);

        // Assert
        // deepMerge picks other's value if non-null, otherwise this value
        // So other's options (max-file=3) override this options (max-size=10m)
        assertThat(merged.driver()).isEqualTo("json-file");
        assertThat(merged.options()).containsEntry("max-file", "3");
    }
}
