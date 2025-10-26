package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TraefikConfigTest {

    @Test
    void shouldCreateEmptyTraefikConfig() {
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
    void shouldCreateTraefikConfigWithAllFields() {
        Map<String, String> labels = Map.of("app", "traefik");
        Map<String, String> args = Map.of("log.level", "INFO");
        Map<String, String> options = Map.of("memory", "256m");
        EnvironmentConfig env = new EnvironmentConfig();
        
        TraefikConfig config = new TraefikConfig(
            "traefik:v2.10",
            80,
            true,
            labels,
            args,
            options,
            env
        );
        
        assertThat(config.image()).isEqualTo("traefik:v2.10");
        assertThat(config.hostPort()).isEqualTo(80);
        assertThat(config.publish()).isTrue();
        assertThat(config.labels()).isEqualTo(labels);
        assertThat(config.args()).isEqualTo(args);
        assertThat(config.options()).isEqualTo(options);
        assertThat(config.env()).isEqualTo(env);
    }

    @Test
    void shouldHandleNullValues() {
        TraefikConfig config = new TraefikConfig(null, null, null, null, null, null, null);
        
        assertThat(config.image()).isNull();
        assertThat(config.hostPort()).isNull();
        assertThat(config.publish()).isNull();
        assertThat(config.labels()).isNull();
        assertThat(config.args()).isNull();
        assertThat(config.options()).isNull();
        assertThat(config.env()).isNull();
    }

    @Test
    void shouldHandlePublishFalse() {
        TraefikConfig config = new TraefikConfig("traefik:latest", 8080, false, null, null, null, null);
        
        assertThat(config.publish()).isFalse();
        assertThat(config.hostPort()).isEqualTo(8080);
    }

    @Test
    void shouldHandlePartialConfiguration() {
        TraefikConfig config = new TraefikConfig("traefik:v2.10", 80, true, null, null, null, null);
        
        assertThat(config.image()).isEqualTo("traefik:v2.10");
        assertThat(config.hostPort()).isEqualTo(80);
        assertThat(config.publish()).isTrue();
        assertThat(config.labels()).isNull();
        assertThat(config.args()).isNull();
        assertThat(config.options()).isNull();
        assertThat(config.env()).isNull();
    }
}
