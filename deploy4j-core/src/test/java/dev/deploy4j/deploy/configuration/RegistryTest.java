package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.DeployConfig;
import dev.deploy4j.deploy.configuration.raw.PlainValueOrSecretKey;
import dev.deploy4j.deploy.configuration.raw.RegistryConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Registry")
class RegistryTest {

    @Test
    @DisplayName("should use default values when config is null")
    void shouldUseDefaultValuesWhenConfigIsNull() {
        // Arrange
        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.registry()).thenReturn(null);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);

        // Act
        Registry registry = new Registry(config);

        // Assert
        assertThat(registry.server()).isNull();
        assertThat(registry.username()).isNull();
        assertThat(registry.password()).isNull();
    }

    @Test
    @DisplayName("should use config values when provided")
    void shouldUseConfigValuesWhenProvided() {
        // Arrange
        PlainValueOrSecretKey username = new PlainValueOrSecretKey("admin");
        PlainValueOrSecretKey password = new PlainValueOrSecretKey("secret123");
        RegistryConfig registryConfig = new RegistryConfig("docker.io", username, password);

        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.registry()).thenReturn(registryConfig);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);

        // Act
        Registry registry = new Registry(config);

        // Assert
        assertThat(registry.server()).isEqualTo("docker.io");
        assertThat(registry.username()).isEqualTo("admin");
        assertThat(registry.password()).isEqualTo("secret123");
    }

    @Test
    @DisplayName("should lookup username from environment when key is provided")
    void shouldLookupUsernameFromEnvironmentWhenKeyIsProvided() {
        // Arrange
        PlainValueOrSecretKey usernameKey = new PlainValueOrSecretKey(List.of("REGISTRY_USER"));
        PlainValueOrSecretKey password = new PlainValueOrSecretKey("password");
        RegistryConfig registryConfig = new RegistryConfig("docker.io", usernameKey, password);

        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.registry()).thenReturn(registryConfig);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);

        // Mock ENV.fetch
        try (MockedStatic<dev.deploy4j.deploy.env.ENV> envMock = mockStatic(dev.deploy4j.deploy.env.ENV.class)) {
            envMock.when(() -> dev.deploy4j.deploy.env.ENV.fetch("REGISTRY_USER")).thenReturn("env-username");

            // Act
            Registry registry = new Registry(config);

            // Assert
            assertThat(registry.username()).isEqualTo("env-username");
            assertThat(registry.password()).isEqualTo("password");
        }
    }

    @Test
    @DisplayName("should lookup password from environment when key is provided")
    void shouldLookupPasswordFromEnvironmentWhenKeyIsProvided() {
        // Arrange
        PlainValueOrSecretKey username = new PlainValueOrSecretKey("user");
        PlainValueOrSecretKey passwordKey = new PlainValueOrSecretKey(List.of("REGISTRY_PASSWORD"));
        RegistryConfig registryConfig = new RegistryConfig("ghcr.io", username, passwordKey);

        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.registry()).thenReturn(registryConfig);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);

        // Mock ENV.fetch
        try (MockedStatic<dev.deploy4j.deploy.env.ENV> envMock = mockStatic(dev.deploy4j.deploy.env.ENV.class)) {
            envMock.when(() -> dev.deploy4j.deploy.env.ENV.fetch("REGISTRY_PASSWORD")).thenReturn("env-password");

            // Act
            Registry registry = new Registry(config);

            // Assert
            assertThat(registry.username()).isEqualTo("user");
            assertThat(registry.password()).isEqualTo("env-password");
        }
    }

    @Test
    @DisplayName("should handle null username and password")
    void shouldHandleNullUsernameAndPassword() {
        // Arrange
        RegistryConfig registryConfig = new RegistryConfig("registry.example.com", null, null);

        DeployConfig deployConfig = mock(DeployConfig.class);
        when(deployConfig.registry()).thenReturn(registryConfig);
        Configuration config = mock(Configuration.class);
        when(config.rawConfig()).thenReturn(deployConfig);

        // Act
        Registry registry = new Registry(config);

        // Assert
        assertThat(registry.server()).isEqualTo("registry.example.com");
        assertThat(registry.username()).isNull();
        assertThat(registry.password()).isNull();
    }
}
