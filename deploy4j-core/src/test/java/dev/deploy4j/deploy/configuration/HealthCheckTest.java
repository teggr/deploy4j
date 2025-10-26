package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.HealthCheckConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HealthCheck")
class HealthCheckTest {

    @Test
    @DisplayName("should use default values when config is null")
    void shouldUseDefaultValuesWhenConfigIsNull() {
        // Arrange
        HealthCheck healthCheck = new HealthCheck(null, "production");

        // Act & Assert
        assertThat(healthCheck.port()).isEqualTo(8080);
        assertThat(healthCheck.path()).isEqualTo("/actuator/health");
        // Note: empty HealthCheckConfig returns 0 for maxAttempts, not null
        assertThat(healthCheck.maxAttempts()).isEqualTo(0);
        assertThat(healthCheck.interval()).isEqualTo("1s");
        assertThat(healthCheck.cord()).isEqualTo("/tmp/deploy4j-cord");
        assertThat(healthCheck.logLines()).isEqualTo(50);
    }

    @Test
    @DisplayName("should override default values with config values")
    void shouldOverrideDefaultValuesWithConfigValues() {
        // Arrange
        HealthCheckConfig config = new HealthCheckConfig(
            null,
            "2s",
            10,
            9090,
            "/health",
            "/custom/cord",
            100
        );

        HealthCheck healthCheck = new HealthCheck(config, "production");

        // Act & Assert
        assertThat(healthCheck.port()).isEqualTo(9090);
        assertThat(healthCheck.path()).isEqualTo("/health");
        assertThat(healthCheck.maxAttempts()).isEqualTo(10);
        assertThat(healthCheck.interval()).isEqualTo("2s");
        assertThat(healthCheck.cord()).isEqualTo("/custom/cord");
        assertThat(healthCheck.logLines()).isEqualTo(100);
    }

    @Test
    @DisplayName("should generate default HTTP health check command")
    void shouldGenerateDefaultHttpHealthCheckCommand() {
        // Arrange
        HealthCheck healthCheck = new HealthCheck(null, "production");

        // Act
        String cmd = healthCheck.cmd();

        // Assert
        assertThat(cmd)
                .contains("curl")
                .contains("-f")
                .contains("http://localhost:8080")
                .contains("/actuator/health")
                .contains("exit 1");
    }

    @Test
    @DisplayName("should use custom cmd when provided")
    void shouldUseCustomCmdWhenProvided() {
        // Arrange
        HealthCheckConfig config = new HealthCheckConfig(
            "custom-health-check",
            null,
            null,
            null,
            null,
            null,
            null
        );

        HealthCheck healthCheck = new HealthCheck(config, "production");

        // Act
        String cmd = healthCheck.cmd();

        // Assert
        assertThat(cmd).isEqualTo("custom-health-check");
    }

    @Test
    @DisplayName("should generate HTTP health check with custom port")
    void shouldGenerateHttpHealthCheckWithCustomPort() {
        // Arrange
        HealthCheckConfig config = new HealthCheckConfig(
            null,
            null,
            null,
            3000,
            null,
            null,
            null
        );

        HealthCheck healthCheck = new HealthCheck(config, "production");

        // Act
        String cmd = healthCheck.cmd();

        // Assert
        assertThat(cmd)
                .contains("http://localhost:3000")
                .contains("/actuator/health");
    }

    @Test
    @DisplayName("should generate HTTP health check with custom path")
    void shouldGenerateHttpHealthCheckWithCustomPath() {
        // Arrange
        HealthCheckConfig config = new HealthCheckConfig(
            null,
            null,
            null,
            null,
            "/api/status",
            null,
            null
        );

        HealthCheck healthCheck = new HealthCheck(config, "production");

        // Act
        String cmd = healthCheck.cmd();

        // Assert
        assertThat(cmd)
                .contains("http://localhost:8080")
                .contains("/api/status");
    }

    @Test
    @DisplayName("should merge with another HealthCheck")
    void shouldMergeWithAnotherHealthCheck() {
        // Arrange
        HealthCheckConfig config1 = new HealthCheckConfig(
            null,
            null,
            5,
            8080,
            null,
            null,
            null
        );

        HealthCheckConfig config2 = new HealthCheckConfig(
            null,
            "3s",
            0, // Use 0 instead of null for int
            null,
            "/custom",
            null,
            null
        );

        HealthCheck healthCheck1 = new HealthCheck(config1, "prod");
        HealthCheck healthCheck2 = new HealthCheck(config2, "stage");

        // Act
        HealthCheck merged = healthCheck1.merge(healthCheck2);

        // Assert
        assertThat(merged.port()).isEqualTo(8080);
        assertThat(merged.path()).isEqualTo("/custom");
        assertThat(merged.maxAttempts()).isEqualTo(5);
        assertThat(merged.interval()).isEqualTo("3s");
    }

    @Test
    @DisplayName("should return true when port or path is set")
    void shouldReturnTrueWhenPortOrPathIsSet() {
        // Arrange
        HealthCheckConfig configWithPort = new HealthCheckConfig(
            null, null, null, 9090, null, null, null
        );

        HealthCheckConfig configWithPath = new HealthCheckConfig(
            null, null, null, null, "/custom", null, null
        );

        // Act & Assert
        assertThat(new HealthCheck(configWithPort, "prod").setPortOrPath()).isTrue();
        assertThat(new HealthCheck(configWithPath, "prod").setPortOrPath()).isTrue();
        assertThat(new HealthCheck(null, "prod").setPortOrPath()).isFalse();
    }

    @Test
    @DisplayName("should resolve to map with key values")
    void shouldResolveToMapWithKeyValues() {
        // Arrange
        HealthCheckConfig config = new HealthCheckConfig(
            "custom cmd",
            null,
            null,
            9000,
            "/status",
            null,
            null
        );

        HealthCheck healthCheck = new HealthCheck(config, "prod");

        // Act
        Map<String, Object> resolved = healthCheck.resolve();

        // Assert
        assertThat(resolved).containsEntry("cmd", "custom cmd");
        assertThat(resolved).containsEntry("port", 9000);
        assertThat(resolved).containsEntry("path", "/status");
    }
}
