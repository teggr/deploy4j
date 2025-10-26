package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BootConfig")
class BootConfigTest {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Test
    @DisplayName("should deserialize from YAML with all fields")
    void shouldDeserializeFromYamlWithAllFields() throws Exception {
        String yaml = """
            limit: "5"
            wait: "30"
            """;

        BootConfig config = mapper.readValue(yaml, BootConfig.class);

        assertThat(config.limit()).isEqualTo("5");
        assertThat(config.waiter()).isEqualTo("30");
    }

    @Test
    @DisplayName("should deserialize from YAML with only limit")
    void shouldDeserializeFromYamlWithOnlyLimit() throws Exception {
        String yaml = """
            limit: "10"
            """;

        BootConfig config = mapper.readValue(yaml, BootConfig.class);

        assertThat(config.limit()).isEqualTo("10");
        assertThat(config.waiter()).isNull();
    }

    @Test
    @DisplayName("should deserialize from YAML with only wait")
    void shouldDeserializeFromYamlWithOnlyWait() throws Exception {
        String yaml = """
            wait: "60"
            """;

        BootConfig config = mapper.readValue(yaml, BootConfig.class);

        assertThat(config.limit()).isNull();
        assertThat(config.waiter()).isEqualTo("60");
    }

    @Test
    @DisplayName("should create empty config with default constructor")
    void shouldCreateEmptyConfigWithDefaultConstructor() {
        BootConfig config = new BootConfig();

        assertThat(config.limit()).isNull();
        assertThat(config.waiter()).isNull();
    }

    @Test
    @DisplayName("should create config programmatically")
    void shouldCreateConfigProgrammatically() {
        BootConfig config = new BootConfig("3", "45");

        assertThat(config.limit()).isEqualTo("3");
        assertThat(config.waiter()).isEqualTo("45");
    }

    @Test
    @DisplayName("should handle empty YAML")
    void shouldHandleEmptyYaml() throws Exception {
        String yaml = "{}";

        BootConfig config = mapper.readValue(yaml, BootConfig.class);

        assertThat(config.limit()).isNull();
        assertThat(config.waiter()).isNull();
    }
}
