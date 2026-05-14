package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.DeployConfig;
import dev.deploy4j.deploy.configuration.raw.FlexibleValue;
import dev.deploy4j.deploy.configuration.raw.GatewayConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Gateway")
class GatewayTest {

    @Test
    @DisplayName("should use default values when config is null")
    void shouldUseDefaultValuesWhenConfigIsNull() {
        // Arrange
        DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Gateway gateway = new Gateway(config);

        // Assert
        assertThat(gateway.image()).isEqualTo("springcloud/spring-cloud-gateway:latest");
        assertThat(gateway.hostPort()).isEqualTo(80);
        assertThat(gateway.publish()).isTrue();
        assertThat(gateway.options()).isEmpty();
    }

    @Test
    @DisplayName("should use config values when provided")
    void shouldUseConfigValuesWhenProvided() {
        // Arrange
        Map<String, String> labels = Map.of("custom.label", "value");
        Map<String, String> args = Map.of("api.insecure", "true");
        Map<String, FlexibleValue> options = Map.of("network", FlexibleValue.from("host"));
        GatewayConfig gatewayConfig = new GatewayConfig(
                "gateway:v3.0",
                8080,
                false,
                labels,
                args,
                options,
                null
        );

        DeployConfig deployConfig = DeployConfigBuilder.minimal().gateway(gatewayConfig).build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Gateway gateway = new Gateway(config);

        // Assert
        assertThat(gateway.image()).isEqualTo("gateway:v3.0");
        assertThat(gateway.hostPort()).isEqualTo(8080);
        assertThat(gateway.publish()).isFalse();
        assertThat(gateway.options()).containsEntry("network", "host");
    }

    @Test
    @DisplayName("should default publish to true when not specified")
    void shouldDefaultPublishToTrueWhenNotSpecified() {
        // Arrange
        GatewayConfig gatewayConfig = new GatewayConfig(null, null, null, null, null, null, null);

        DeployConfig deployConfig = DeployConfigBuilder.minimal().gateway(gatewayConfig).build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Gateway gateway = new Gateway(config);

        // Assert
        assertThat(gateway.publish()).isTrue();
    }

    @Test
    @DisplayName("should default labels to empty map")
    void shouldDefaultLabelsToEmptyMap() {
        // Arrange
        DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Gateway gateway = new Gateway(config);
        Map<String, String> labels = gateway.labels();

        // Assert
        assertThat(labels).isEmpty();
    }

    @Test
    @DisplayName("should return custom labels")
    void shouldReturnCustomLabels() {
        // Arrange
        Map<String, String> customLabels = Map.of("custom.label", "value", "another.label", "another-value");
        GatewayConfig gatewayConfig = new GatewayConfig(null, null, null, customLabels, null, null, null);

        DeployConfig deployConfig = DeployConfigBuilder.minimal().gateway(gatewayConfig).build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Gateway gateway = new Gateway(config);
        Map<String, String> labels = gateway.labels();

        // Assert
        assertThat(labels)
                .containsEntry("custom.label", "value")
                .containsEntry("another.label", "another-value");
    }

    @Test
    @DisplayName("should include default args in args map")
    void shouldIncludeDefaultArgsInArgsMap() {
        // Arrange
        DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Gateway gateway = new Gateway(config);
        Map<String, String> args = gateway.args();

        // Assert
        assertThat(args)
                .containsEntry("management.endpoint.gateway.enabled", "true")
                .containsEntry("management.endpoints.web.exposure.include", "gateway,health,info");
    }

    @Test
    @DisplayName("should merge custom args with default args")
    void shouldMergeCustomArgsWithDefaultArgs() {
        // Arrange
        Map<String, String> customArgs = Map.of("server.port", "8080");
        GatewayConfig gatewayConfig = new GatewayConfig(null, null, null, null, customArgs, null, null);

        DeployConfig deployConfig = DeployConfigBuilder.minimal().gateway(gatewayConfig).build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Gateway gateway = new Gateway(config);
        Map<String, String> args = gateway.args();

        // Assert
        assertThat(args)
                .containsEntry("server.port", "8080")
                .containsEntry("management.endpoint.gateway.enabled", "true");
    }

    @Test
    @DisplayName("should format port correctly")
    void shouldFormatPortCorrectly() {
        // Arrange
        GatewayConfig gatewayConfig = new GatewayConfig(null, 8080, null, null, null, null, null);

        DeployConfig deployConfig = DeployConfigBuilder.minimal().gateway(gatewayConfig).build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Gateway gateway = new Gateway(config);

        // Assert
        assertThat(gateway.port()).isEqualTo("8080:8080");
    }

    @Test
    @DisplayName("should format default port correctly")
    void shouldFormatDefaultPortCorrectly() {
        // Arrange
        DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Gateway gateway = new Gateway(config);

        // Assert
        assertThat(gateway.port()).isEqualTo("80:8080");
    }

    @Test
    @DisplayName("should create Env with correct parameters")
    void shouldCreateEnvWithCorrectParameters() {
        // Arrange
        DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Gateway gateway = new Gateway(config);
        Env env = gateway.env();

        // Assert
        assertThat(env).isNotNull();
        assertThat(env.context()).isEqualTo("gateway/env");
    }
}
