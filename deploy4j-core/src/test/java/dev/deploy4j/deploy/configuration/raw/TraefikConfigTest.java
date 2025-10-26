package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TraefikConfig")
class TraefikConfigTest {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Test
    @DisplayName("should deserialize from YAML with all fields")
    void shouldDeserializeFromYamlWithAllFields() throws Exception {
        String yaml = """
            image: traefik:v3.0
            host_port: 8080
            publish: true
            labels:
              environment: production
              tier: proxy
            args:
              api.insecure: "true"
              providers.docker: "true"
            options:
              network: web
              restart: unless-stopped
            env:
              clear:
                TRAEFIK_LOG_LEVEL: INFO
            """;

        TraefikConfig config = mapper.readValue(yaml, TraefikConfig.class);

        assertThat(config.image()).isEqualTo("traefik:v3.0");
        assertThat(config.hostPort()).isEqualTo(8080);
        assertThat(config.publish()).isTrue();
        assertThat(config.labels()).containsEntry("environment", "production").containsEntry("tier", "proxy");
        assertThat(config.args()).containsEntry("api.insecure", "true").containsEntry("providers.docker", "true");
        assertThat(config.options()).containsEntry("network", "web").containsEntry("restart", "unless-stopped");
        assertThat(config.env().clear()).containsEntry("TRAEFIK_LOG_LEVEL", "INFO");
    }

    @Test
    @DisplayName("should deserialize from YAML with minimal fields")
    void shouldDeserializeFromYamlWithMinimalFields() throws Exception {
        String yaml = """
            image: traefik:v2.10
            """;

        TraefikConfig config = mapper.readValue(yaml, TraefikConfig.class);

        assertThat(config.image()).isEqualTo("traefik:v2.10");
        assertThat(config.hostPort()).isNull();
        assertThat(config.publish()).isNull();
        assertThat(config.labels()).isNull();
        assertThat(config.args()).isNull();
        assertThat(config.options()).isNull();
        assertThat(config.env()).isNull();
    }

    @Test
    @DisplayName("should create empty config with default constructor")
    void shouldCreateEmptyConfigWithDefaultConstructor() {
        TraefikConfig config = new TraefikConfig();

        assertThat(config.image()).isNull();
        assertThat(config.hostPort()).isNull();
        assertThat(config.publish()).isNull();
        assertThat(config.labels()).isNull();
        assertThat(config.args()).isNull();
        assertThat(config.options()).isNull();
        assertThat(config.env()).isNull();
    }

    @Test
    @DisplayName("should create config programmatically")
    void shouldCreateConfigProgrammatically() {
        TraefikConfig config = new TraefikConfig(
            "traefik:latest",
            443,
            false,
            Map.of("app", "reverse-proxy"),
            Map.of("api.dashboard", "true"),
            Map.of("memory", "512m"),
            new EnvironmentConfig(Map.of("TZ", "UTC"), null, null, null)
        );

        assertThat(config.image()).isEqualTo("traefik:latest");
        assertThat(config.hostPort()).isEqualTo(443);
        assertThat(config.publish()).isFalse();
        assertThat(config.labels()).containsEntry("app", "reverse-proxy");
        assertThat(config.args()).containsEntry("api.dashboard", "true");
        assertThat(config.options()).containsEntry("memory", "512m");
        assertThat(config.env().clear()).containsEntry("TZ", "UTC");
    }

    @Test
    @DisplayName("should handle custom host port")
    void shouldHandleCustomHostPort() throws Exception {
        String yaml = """
            image: traefik:v2.9
            host_port: 9090
            """;

        TraefikConfig config = mapper.readValue(yaml, TraefikConfig.class);

        assertThat(config.hostPort()).isEqualTo(9090);
    }

    @Test
    @DisplayName("should handle publish flag as false")
    void shouldHandlePublishFlagAsFalse() throws Exception {
        String yaml = """
            image: traefik:v2.9
            publish: false
            """;

        TraefikConfig config = mapper.readValue(yaml, TraefikConfig.class);

        assertThat(config.publish()).isFalse();
    }

    @Test
    @DisplayName("should handle empty YAML")
    void shouldHandleEmptyYaml() throws Exception {
        String yaml = "{}";

        TraefikConfig config = mapper.readValue(yaml, TraefikConfig.class);

        assertThat(config.image()).isNull();
        assertThat(config.hostPort()).isNull();
        assertThat(config.publish()).isNull();
    }

    @Test
    @DisplayName("should handle multiple args for traefik configuration")
    void shouldHandleMultipleArgsForTraefikConfiguration() throws Exception {
        String yaml = """
            image: traefik:v3.0
            args:
              providers.docker: "true"
              providers.docker.exposedbydefault: "false"
              entrypoints.web.address: ":80"
              entrypoints.websecure.address: ":443"
            """;

        TraefikConfig config = mapper.readValue(yaml, TraefikConfig.class);

        assertThat(config.args()).hasSize(4)
            .containsEntry("providers.docker", "true")
            .containsEntry("providers.docker.exposedbydefault", "false")
            .containsEntry("entrypoints.web.address", ":80")
            .containsEntry("entrypoints.websecure.address", ":443");
    }
}
