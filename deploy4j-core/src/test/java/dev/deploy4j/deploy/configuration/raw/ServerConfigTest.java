package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ServerConfig")
class ServerConfigTest {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Test
    @DisplayName("should create config with just host")
    void shouldCreateConfigWithJustHost() {
        ServerConfig config = new ServerConfig("192.168.1.10");

        assertThat(config.host()).isEqualTo("192.168.1.10");
        assertThat(config.tags()).isEmpty();
    }

    @Test
    @DisplayName("should deserialize from YAML with single tag as string")
    void shouldDeserializeFromYamlWithSingleTagAsString() throws Exception {
        String yaml = """
            192.168.1.10: web
            """;

        ServerConfig config = mapper.readValue(yaml, ServerConfig.class);

        assertThat(config.host()).isEqualTo("192.168.1.10");
        assertThat(config.tags()).containsExactly("web");
    }

    @Test
    @DisplayName("should deserialize from YAML with multiple tags as list")
    void shouldDeserializeFromYamlWithMultipleTagsAsList() throws Exception {
        String yaml = """
            192.168.1.11:
              - web
              - primary
            """;

        ServerConfig config = mapper.readValue(yaml, ServerConfig.class);

        assertThat(config.host()).isEqualTo("192.168.1.11");
        assertThat(config.tags()).containsExactly("web", "primary");
    }

    @Test
    @DisplayName("should deserialize from YAML with no tags")
    void shouldDeserializeFromYamlWithNoTags() throws Exception {
        String yaml = """
            192.168.1.12:
            """;

        ServerConfig config = mapper.readValue(yaml, ServerConfig.class);

        assertThat(config.host()).isEqualTo("192.168.1.12");
        assertThat(config.tags()).isEmpty();
    }

    @Test
    @DisplayName("should handle host with empty list")
    void shouldHandleHostWithEmptyList() throws Exception {
        String yaml = """
            192.168.1.13: []
            """;

        ServerConfig config = mapper.readValue(yaml, ServerConfig.class);

        assertThat(config.host()).isEqualTo("192.168.1.13");
        assertThat(config.tags()).isEmpty();
    }

    @Test
    @DisplayName("should handle hostname instead of IP")
    void shouldHandleHostnameInsteadOfIp() {
        ServerConfig config = new ServerConfig("server1.example.com");

        assertThat(config.host()).isEqualTo("server1.example.com");
        assertThat(config.tags()).isEmpty();
    }
}
