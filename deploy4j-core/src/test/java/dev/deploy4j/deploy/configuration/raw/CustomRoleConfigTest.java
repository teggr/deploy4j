package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CustomRoleConfigTest {

    @Test
    void shouldCreateEmptyCustomRoleConfig() {
        CustomRoleConfig config = new CustomRoleConfig();
        
        assertThat(config.hosts()).isEmpty();
        assertThat(config.traefik()).isNull();
        assertThat(config.cmd()).isNull();
        assertThat(config.env()).isNull();
        assertThat(config.logging()).isNull();
        assertThat(config.healthcheck()).isNull();
        assertThat(config.options()).isNull();
        assertThat(config.labels()).isNull();
    }

    @Test
    void shouldCreateCustomRoleConfigWithAllFields() {
        EnvironmentConfig env = new EnvironmentConfig();
        LoggingConfig logging = new LoggingConfig("json-file", Map.of("max-size", "10m"));
        HealthCheckConfig healthcheck = new HealthCheckConfig();
        Map<String, String> options = Map.of("memory", "512m");
        Map<String, String> labels = Map.of("app", "myapp");
        
        CustomRoleConfig config = new CustomRoleConfig(
            List.of("192.168.1.10", "192.168.1.11"),
            true,
            "java -jar app.jar",
            env,
            logging,
            healthcheck,
            options,
            labels
        );
        
        assertThat(config.hosts()).hasSize(2);
        assertThat(config.hosts().get(0).host()).isEqualTo("192.168.1.10");
        assertThat(config.traefik()).isTrue();
        assertThat(config.cmd()).isEqualTo("java -jar app.jar");
        assertThat(config.env()).isEqualTo(env);
        assertThat(config.logging()).isEqualTo(logging);
        assertThat(config.healthcheck()).isEqualTo(healthcheck);
        assertThat(config.options()).isEqualTo(options);
        assertThat(config.labels()).isEqualTo(labels);
    }

    @Test
    void shouldHandleHostsWithTags() {
        List<Object> hosts = List.of(
            "192.168.1.10",
            Map.of("192.168.1.11", List.of("web", "api"))
        );
        
        CustomRoleConfig config = new CustomRoleConfig(
            hosts,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
        
        assertThat(config.hosts()).hasSize(2);
        assertThat(config.hosts().get(0).host()).isEqualTo("192.168.1.10");
        assertThat(config.hosts().get(0).tags()).isEmpty();
        assertThat(config.hosts().get(1).host()).isEqualTo("192.168.1.11");
        assertThat(config.hosts().get(1).tags()).containsExactly("web", "api");
    }

    @Test
    void shouldHandleNullOptionalFields() {
        CustomRoleConfig config = new CustomRoleConfig(
            List.of("192.168.1.10"),
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
        
        assertThat(config.hosts()).hasSize(1);
        assertThat(config.traefik()).isNull();
        assertThat(config.cmd()).isNull();
        assertThat(config.env()).isNull();
        assertThat(config.logging()).isNull();
        assertThat(config.healthcheck()).isNull();
        assertThat(config.options()).isNull();
        assertThat(config.labels()).isNull();
    }

    @Test
    void shouldSetTraefikFlagToFalse() {
        CustomRoleConfig config = new CustomRoleConfig(
            List.of("192.168.1.10"),
            false,
            null,
            null,
            null,
            null,
            null,
            null
        );
        
        assertThat(config.traefik()).isFalse();
    }
}
