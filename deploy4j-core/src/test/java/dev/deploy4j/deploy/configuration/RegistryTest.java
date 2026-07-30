package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.Secrets;
import dev.deploy4j.deploy.configuration.raw.DeployConfig;
import dev.deploy4j.deploy.configuration.raw.PlainValueOrSecretKey;
import dev.deploy4j.deploy.configuration.raw.RegistryConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Registry")
class RegistryTest {

  @Test
  @DisplayName("should use default values when config is null")
  void shouldUseDefaultValuesWhenConfigIsNull() {
    // Arrange
    DeployConfig deployConfig = DeployConfigBuilder.minimal().registry(null).build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    // Act
    Registry registry = new Registry(secrets, config);

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

    DeployConfig deployConfig = DeployConfigBuilder.minimal().registry(registryConfig).build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    // Act
    Registry registry = new Registry(secrets, config);

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

    DeployConfig deployConfig = DeployConfigBuilder.minimal().registry(registryConfig).build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    when(secrets.get("REGISTRY_USER")).thenReturn("env-username");

    // Act
    Registry registry = new Registry(secrets, config);

    // Assert
    assertThat(registry.username()).isEqualTo("env-username");
    assertThat(registry.password()).isEqualTo("password");
  }

  @Test
  @DisplayName("should lookup password from environment when key is provided")
  void shouldLookupPasswordFromEnvironmentWhenKeyIsProvided() {
    // Arrange
    PlainValueOrSecretKey username = new PlainValueOrSecretKey("user");
    PlainValueOrSecretKey passwordKey = new PlainValueOrSecretKey(List.of("REGISTRY_PASSWORD"));
    RegistryConfig registryConfig = new RegistryConfig("ghcr.io", username, passwordKey);

    DeployConfig deployConfig = DeployConfigBuilder.minimal().registry(registryConfig).build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    when(secrets.get("REGISTRY_PASSWORD")).thenReturn("env-password");

    // Act
    Registry registry = new Registry(secrets, config);

    // Assert
    assertThat(registry.username()).isEqualTo("user");
    assertThat(registry.password()).isEqualTo("env-password");
  }

  @Test
  @DisplayName("should handle null username and password")
  void shouldHandleNullUsernameAndPassword() {
    // Arrange
    RegistryConfig registryConfig = new RegistryConfig("registry.example.com", null, null);

    DeployConfig deployConfig = DeployConfigBuilder.minimal().registry(registryConfig).build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    // Act
    Registry registry = new Registry(secrets, config);

    // Assert
    assertThat(registry.server()).isEqualTo("registry.example.com");
    assertThat(registry.username()).isNull();
    assertThat(registry.password()).isNull();
    assertThat(registry.credentialsConfigured()).isFalse();
  }

  @Test
  @DisplayName("should report credentials configured when username and password are present")
  void shouldReportCredentialsConfigured() {
    RegistryConfig registryConfig = new RegistryConfig(
      "docker.io",
      new PlainValueOrSecretKey("user"),
      new PlainValueOrSecretKey("password")
    );

    DeployConfig deployConfig = DeployConfigBuilder.minimal().registry(registryConfig).build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    Registry registry = new Registry(secrets, config);

    assertThat(registry.credentialsConfigured()).isTrue();
  }
}
