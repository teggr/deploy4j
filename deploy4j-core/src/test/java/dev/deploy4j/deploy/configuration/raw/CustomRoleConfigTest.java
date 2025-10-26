package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CustomRoleConfig")
class CustomRoleConfigTest {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Test
    @DisplayName("should deserialize from YAML with all fields")
    void shouldDeserializeFromYamlWithAllFields() throws Exception {
        String yaml = """
            hosts:
              - 192.168.1.10
              - 192.168.1.11: web
            traefik: true
            cmd: ./custom-entrypoint.sh
            env:
              clear:
                APP_MODE: production
            logging:
              driver: json-file
              options:
                max-size: "10m"
            healthcheck:
              path: /health
              port: 8080
            options:
              restart: always
              network: custom-net
            asset_path: /app/assets
            labels:
              tier: frontend
              version: v2
            """;

        CustomRoleConfig config = mapper.readValue(yaml, CustomRoleConfig.class);

        assertThat(config.hosts()).hasSize(2);
        assertThat(config.hosts().get(0).host()).isEqualTo("192.168.1.10");
        assertThat(config.hosts().get(1).host()).isEqualTo("192.168.1.11");
        assertThat(config.hosts().get(1).tags()).containsExactly("web");
        assertThat(config.traefik()).isTrue();
        assertThat(config.cmd()).isEqualTo("./custom-entrypoint.sh");
        assertThat(config.env().clear()).containsEntry("APP_MODE", "production");
        assertThat(config.logging().driver()).isEqualTo("json-file");
        assertThat(config.logging().options()).containsEntry("max-size", "10m");
        assertThat(config.healthcheck().path()).isEqualTo("/health");
        assertThat(config.healthcheck().port()).isEqualTo(8080);
        assertThat(config.options()).containsEntry("restart", "always").containsEntry("network", "custom-net");
        assertThat(config.assetPath()).isEqualTo("/app/assets");
        assertThat(config.labels()).containsEntry("tier", "frontend").containsEntry("version", "v2");
    }

    @Test
    @DisplayName("should deserialize from YAML with minimal fields")
    void shouldDeserializeFromYamlWithMinimalFields() throws Exception {
        String yaml = """
            hosts:
              - 192.168.1.20
            """;

        CustomRoleConfig config = mapper.readValue(yaml, CustomRoleConfig.class);

        assertThat(config.hosts()).hasSize(1);
        assertThat(config.hosts().get(0).host()).isEqualTo("192.168.1.20");
        assertThat(config.traefik()).isNull();
        assertThat(config.cmd()).isNull();
        assertThat(config.env()).isNull();
        assertThat(config.logging()).isNull();
        assertThat(config.healthcheck()).isNull();
        assertThat(config.options()).isNull();
        assertThat(config.assetPath()).isNull();
        assertThat(config.labels()).isNull();
    }

    @Test
    @DisplayName("should create empty config with default constructor")
    void shouldCreateEmptyConfigWithDefaultConstructor() {
        CustomRoleConfig config = new CustomRoleConfig();

        assertThat(config.hosts()).isEmpty();
        assertThat(config.traefik()).isNull();
        assertThat(config.cmd()).isNull();
        assertThat(config.env()).isNull();
        assertThat(config.logging()).isNull();
        assertThat(config.healthcheck()).isNull();
        assertThat(config.options()).isNull();
        assertThat(config.assetPath()).isNull();
        assertThat(config.labels()).isNull();
    }

    @Test
    @DisplayName("should handle traefik disabled")
    void shouldHandleTraefikDisabled() throws Exception {
        String yaml = """
            hosts:
              - 192.168.1.30
            traefik: false
            """;

        CustomRoleConfig config = mapper.readValue(yaml, CustomRoleConfig.class);

        assertThat(config.traefik()).isFalse();
    }

    @Test
    @DisplayName("should handle multiple hosts with mixed tags")
    void shouldHandleMultipleHostsWithMixedTags() throws Exception {
        String yaml = """
            hosts:
              - server1.example.com
              - server2.example.com: primary
              - server3.example.com:
                  - web
                  - cache
            """;

        CustomRoleConfig config = mapper.readValue(yaml, CustomRoleConfig.class);

        assertThat(config.hosts()).hasSize(3);
        assertThat(config.hosts().get(0).host()).isEqualTo("server1.example.com");
        assertThat(config.hosts().get(0).tags()).isEmpty();
        assertThat(config.hosts().get(1).host()).isEqualTo("server2.example.com");
        assertThat(config.hosts().get(1).tags()).containsExactly("primary");
        assertThat(config.hosts().get(2).host()).isEqualTo("server3.example.com");
        assertThat(config.hosts().get(2).tags()).containsExactly("web", "cache");
    }

    @Test
    @DisplayName("should programmatically create config")
    void shouldProgrammaticallyCreateConfig() {
        CustomRoleConfig config = new CustomRoleConfig(
            List.of("192.168.1.100"),
            false,
            "npm start",
            new EnvironmentConfig(Map.of("NODE_ENV", "production"), List.of(), null, null),
            new LoggingConfig("syslog", null),
            new HealthCheckConfig(null, "10s", 3, 3000, "/api/health", null, null),
            Map.of("memory", "2g"),
            "/static",
            Map.of("app", "frontend")
        );

        assertThat(config.hosts()).hasSize(1);
        assertThat(config.hosts().get(0).host()).isEqualTo("192.168.1.100");
        assertThat(config.traefik()).isFalse();
        assertThat(config.cmd()).isEqualTo("npm start");
        assertThat(config.env().clear()).containsEntry("NODE_ENV", "production");
        assertThat(config.logging().driver()).isEqualTo("syslog");
        assertThat(config.healthcheck().path()).isEqualTo("/api/health");
        assertThat(config.options()).containsEntry("memory", "2g");
        assertThat(config.assetPath()).isEqualTo("/static");
        assertThat(config.labels()).containsEntry("app", "frontend");
    }
}
