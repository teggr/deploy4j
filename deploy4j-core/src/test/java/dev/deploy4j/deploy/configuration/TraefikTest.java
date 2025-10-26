package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.DeployConfig;
import dev.deploy4j.deploy.configuration.raw.TraefikConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Traefik")
class TraefikTest {

    @Test
    @DisplayName("should use default values when config is null")
    void shouldUseDefaultValuesWhenConfigIsNull() {
        // Arrange
        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.traefik()).thenReturn(null);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Traefik traefik = new Traefik(config);

        // Assert
        assertThat(traefik.image()).isEqualTo("traefik:v2.11");
        assertThat(traefik.hostPort()).isEqualTo(80);
        assertThat(traefik.publish()).isTrue();
        assertThat(traefik.options()).isEmpty();
    }

    @Test
    @DisplayName("should use config values when provided")
    void shouldUseConfigValuesWhenProvided() {
        // Arrange
        Map<String, String> labels = Map.of("custom.label", "value");
        Map<String, String> args = Map.of("api.insecure", "true");
        Map<String, String> options = Map.of("network", "host");
        TraefikConfig traefikConfig = new TraefikConfig(
                "traefik:v3.0",
                8080,
                false,
                labels,
                args,
                options,
                null
        );

        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.traefik()).thenReturn(traefikConfig);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Traefik traefik = new Traefik(config);

        // Assert
        assertThat(traefik.image()).isEqualTo("traefik:v3.0");
        assertThat(traefik.hostPort()).isEqualTo(8080);
        assertThat(traefik.publish()).isFalse();
        assertThat(traefik.options()).containsExactlyInAnyOrderEntriesOf(options);
    }

    @Test
    @DisplayName("should default publish to true when not specified")
    void shouldDefaultPublishToTrueWhenNotSpecified() {
        // Arrange
        TraefikConfig traefikConfig = new TraefikConfig(null, null, null, null, null, null, null);

        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.traefik()).thenReturn(traefikConfig);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Traefik traefik = new Traefik(config);

        // Assert
        assertThat(traefik.publish()).isTrue();
    }

    @Test
    @DisplayName("should include default labels in labels map")
    void shouldIncludeDefaultLabelsInLabelsMap() {
        // Arrange
        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.traefik()).thenReturn(null);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Traefik traefik = new Traefik(config);
        Map<String, String> labels = traefik.labels();

        // Assert
        assertThat(labels)
                .containsEntry("traefik.http.routers.catchall.entryPoints", "http")
                .containsEntry("traefik.http.routers.catchall.rule", "PathPrefix(`/`)")
                .containsEntry("traefik.http.routers.catchall.service", "unavailable")
                .containsEntry("traefik.http.routers.catchall.priority", "1")
                .containsEntry("traefik.http.services.unavailable.loadbalancer.server.port", "0");
    }

    @Test
    @DisplayName("should merge custom labels with default labels")
    void shouldMergeCustomLabelsWithDefaultLabels() {
        // Arrange
        Map<String, String> customLabels = Map.of("custom.label", "value", "another.label", "another-value");
        TraefikConfig traefikConfig = new TraefikConfig(null, null, null, customLabels, null, null, null);

        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.traefik()).thenReturn(traefikConfig);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Traefik traefik = new Traefik(config);
        Map<String, String> labels = traefik.labels();

        // Assert
        assertThat(labels)
                .containsEntry("custom.label", "value")
                .containsEntry("another.label", "another-value")
                .containsEntry("traefik.http.routers.catchall.entryPoints", "http");
    }

    @Test
    @DisplayName("should include default args in args map")
    void shouldIncludeDefaultArgsInArgsMap() {
        // Arrange
        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.traefik()).thenReturn(null);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Traefik traefik = new Traefik(config);
        Map<String, String> args = traefik.args();

        // Assert
        assertThat(args).containsEntry("log.level", "DEBUG");
    }

    @Test
    @DisplayName("should merge custom args with default args")
    void shouldMergeCustomArgsWithDefaultArgs() {
        // Arrange
        Map<String, String> customArgs = Map.of("api.dashboard", "true", "entrypoints.web.address", ":80");
        TraefikConfig traefikConfig = new TraefikConfig(null, null, null, null, customArgs, null, null);

        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.traefik()).thenReturn(traefikConfig);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Traefik traefik = new Traefik(config);
        Map<String, String> args = traefik.args();

        // Assert
        assertThat(args)
                .containsEntry("api.dashboard", "true")
                .containsEntry("entrypoints.web.address", ":80")
                .containsEntry("log.level", "DEBUG");
    }

    @Test
    @DisplayName("should format port correctly")
    void shouldFormatPortCorrectly() {
        // Arrange
        TraefikConfig traefikConfig = new TraefikConfig(null, 8080, null, null, null, null, null);

        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.traefik()).thenReturn(traefikConfig);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Traefik traefik = new Traefik(config);

        // Assert
        assertThat(traefik.port()).isEqualTo("8080:80");
    }

    @Test
    @DisplayName("should format default port correctly")
    void shouldFormatDefaultPortCorrectly() {
        // Arrange
        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.traefik()).thenReturn(null);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Traefik traefik = new Traefik(config);

        // Assert
        assertThat(traefik.port()).isEqualTo("80:80");
    }

    @Test
    @DisplayName("should create Env with correct parameters")
    void shouldCreateEnvWithCorrectParameters() {
        // Arrange
        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.traefik()).thenReturn(null);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);
        when(config.hostEnvDirectory()).thenReturn("/app/env");

        // Act
        Traefik traefik = new Traefik(config);
        Env env = traefik.env();

        // Assert
        assertThat(env).isNotNull();
        assertThat(env.secretsFile()).isEqualTo("/app/env/traefik/traefik.env");
        assertThat(env.context()).isEqualTo("traefik/env");
    }
}
