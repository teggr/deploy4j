package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoggingConfig")
class LoggingConfigTest {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Test
    @DisplayName("should deserialize from YAML with all fields")
    void shouldDeserializeFromYamlWithAllFields() throws Exception {
        String yaml = """
            driver: json-file
            options:
              max-size: "10m"
              max-file: "3"
              labels: "production"
            """;

        LoggingConfig config = mapper.readValue(yaml, LoggingConfig.class);

        assertThat(config.driver()).isEqualTo("json-file");
        assertThat(config.options()).containsEntry("max-size", "10m")
                                    .containsEntry("max-file", "3")
                                    .containsEntry("labels", "production");
    }

    @Test
    @DisplayName("should deserialize from YAML with only driver")
    void shouldDeserializeFromYamlWithOnlyDriver() throws Exception {
        String yaml = """
            driver: syslog
            """;

        LoggingConfig config = mapper.readValue(yaml, LoggingConfig.class);

        assertThat(config.driver()).isEqualTo("syslog");
        assertThat(config.options()).isNull();
    }

    @Test
    @DisplayName("should deserialize from YAML with only options")
    void shouldDeserializeFromYamlWithOnlyOptions() throws Exception {
        String yaml = """
            options:
              mode: non-blocking
            """;

        LoggingConfig config = mapper.readValue(yaml, LoggingConfig.class);

        assertThat(config.driver()).isNull();
        assertThat(config.options()).containsEntry("mode", "non-blocking");
    }

    @Test
    @DisplayName("should create empty config with default constructor")
    void shouldCreateEmptyConfigWithDefaultConstructor() {
        LoggingConfig config = new LoggingConfig();

        assertThat(config.driver()).isNull();
        assertThat(config.options()).isNull();
    }

    @Test
    @DisplayName("should create config programmatically")
    void shouldCreateConfigProgrammatically() {
        Map<String, String> options = Map.of(
            "max-size", "20m",
            "max-file", "5"
        );
        LoggingConfig config = new LoggingConfig("fluentd", options);

        assertThat(config.driver()).isEqualTo("fluentd");
        assertThat(config.options()).containsEntry("max-size", "20m")
                                    .containsEntry("max-file", "5");
    }

    @Test
    @DisplayName("should deep merge with other config - driver override")
    void shouldDeepMergeWithOtherConfigDriverOverride() {
        LoggingConfig base = new LoggingConfig("json-file", Map.of("max-size", "10m"));
        LoggingConfig override = new LoggingConfig("syslog", null);

        LoggingConfig merged = base.deepMerge(override);

        assertThat(merged.driver()).isEqualTo("syslog");
        assertThat(merged.options()).containsEntry("max-size", "10m");
    }

    @Test
    @DisplayName("should deep merge with other config - options override")
    void shouldDeepMergeWithOtherConfigOptionsOverride() {
        LoggingConfig base = new LoggingConfig("json-file", Map.of("max-size", "10m"));
        LoggingConfig override = new LoggingConfig(null, Map.of("max-size", "50m", "max-file", "10"));

        LoggingConfig merged = base.deepMerge(override);

        assertThat(merged.driver()).isEqualTo("json-file");
        assertThat(merged.options()).containsEntry("max-size", "50m")
                                    .containsEntry("max-file", "10");
    }

    @Test
    @DisplayName("should deep merge with other config - both override")
    void shouldDeepMergeWithOtherConfigBothOverride() {
        LoggingConfig base = new LoggingConfig("json-file", Map.of("max-size", "10m"));
        LoggingConfig override = new LoggingConfig("fluentd", Map.of("tag", "app.logs"));

        LoggingConfig merged = base.deepMerge(override);

        assertThat(merged.driver()).isEqualTo("fluentd");
        assertThat(merged.options()).containsEntry("tag", "app.logs");
    }

    @Test
    @DisplayName("should deep merge with empty config - keep original")
    void shouldDeepMergeWithEmptyConfigKeepOriginal() {
        LoggingConfig base = new LoggingConfig("json-file", Map.of("max-size", "10m"));
        LoggingConfig override = new LoggingConfig();

        LoggingConfig merged = base.deepMerge(override);

        assertThat(merged.driver()).isEqualTo("json-file");
        assertThat(merged.options()).containsEntry("max-size", "10m");
    }

    @Test
    @DisplayName("should handle empty YAML")
    void shouldHandleEmptyYaml() throws Exception {
        String yaml = "{}";

        LoggingConfig config = mapper.readValue(yaml, LoggingConfig.class);

        assertThat(config.driver()).isNull();
        assertThat(config.options()).isNull();
    }
}
