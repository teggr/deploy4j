package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayConfigTest {

    @Test
    void shouldCreateEmptyGatewayConfig() {
        GatewayConfig config = new GatewayConfig();
        
        assertThat(config.image()).isNull();
        assertThat(config.hostPort()).isNull();
        assertThat(config.publish()).isNull();
        assertThat(config.labels()).isNull();
        assertThat(config.args()).isNull();
        assertThat(config.options()).isNull();
        assertThat(config.env()).isNull();
    }

    @Test
    void shouldCreateGatewayConfigWithAllFields() {
        Map<String, String> labels = Map.of("app", "gateway");
        Map<String, String> args = Map.of("log.level", "INFO");
        Map<String, FlexibleValue> options = Map.of("memory", FlexibleValue.from("256m"));
        EnvironmentConfig env = new EnvironmentConfig();
        
        GatewayConfig config = new GatewayConfig(
            "gateway:v2.10",
            80,
            true,
            labels,
            args,
            options,
            env
        );
        
        assertThat(config.image()).isEqualTo("gateway:v2.10");
        assertThat(config.hostPort()).isEqualTo(80);
        assertThat(config.publish()).isTrue();
        assertThat(config.labels()).isEqualTo(labels);
        assertThat(config.args()).isEqualTo(args);
        assertThat(config.options()).isEqualTo(options);
        assertThat(config.env()).isEqualTo(env);
    }

    @Test
    void shouldHandleNullValues() {
        GatewayConfig config = new GatewayConfig(null, null, null, null, null, null, null);
        
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
        GatewayConfig config = new GatewayConfig("gateway:latest", 8080, false, null, null, null, null);
        
        assertThat(config.publish()).isFalse();
        assertThat(config.hostPort()).isEqualTo(8080);
    }

    @Test
    void shouldHandlePartialConfiguration() {
        GatewayConfig config = new GatewayConfig("gateway:v2.10", 80, true, null, null, null, null);
        
        assertThat(config.image()).isEqualTo("gateway:v2.10");
        assertThat(config.hostPort()).isEqualTo(80);
        assertThat(config.publish()).isTrue();
        assertThat(config.labels()).isNull();
        assertThat(config.args()).isNull();
        assertThat(config.options()).isNull();
        assertThat(config.env()).isNull();
    }
}
