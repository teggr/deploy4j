package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.Secrets;
import dev.deploy4j.deploy.configuration.raw.DeployConfig;
import dev.deploy4j.deploy.configuration.raw.PlainValueOrSecretKey;
import dev.deploy4j.deploy.configuration.raw.SshConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Ssh")
class SshTest {

  @Test
  @DisplayName("should use default values when config is null")
  void shouldUseDefaultValuesWhenConfigIsNull() {
    // Arrange
    DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    // Act
    Ssh ssh = new Ssh(config, secrets);

    // Assert
    assertThat(ssh.user()).isEqualTo("root");
    assertThat(ssh.port()).isEqualTo(22);
    assertThat(ssh.proxy()).isNull();
    assertThat(ssh.keyPath()).isNull();
    assertThat(ssh.keyPassphrase()).isNull();
    assertThat(ssh.strictHostKeyChecking()).isTrue();
  }

  @Test
  @DisplayName("should use config values when provided")
  void shouldUseConfigValuesWhenProvided() {
    // Arrange
    PlainValueOrSecretKey user = new PlainValueOrSecretKey("deploy");
    PlainValueOrSecretKey keyPath = new PlainValueOrSecretKey("/path/to/key");
    PlainValueOrSecretKey keyPassphrase = new PlainValueOrSecretKey("passphrase");
    PlainValueOrSecretKey knownHostsPath = new PlainValueOrSecretKey("/path/to/known_hosts");
    SshConfig sshConfig = new SshConfig(
      user,
      2222,
      "proxy.example.com",
      "ssh -W %h:%p proxy",
      "debug",
      keyPath,
      keyPassphrase,
      true,
      knownHostsPath
    );

    DeployConfig deployConfig = DeployConfigBuilder.minimal().ssh(sshConfig).build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    // Act
    Ssh ssh = new Ssh(config, secrets);

    // Assert
    assertThat(ssh.user()).isEqualTo("deploy");
    assertThat(ssh.port()).isEqualTo(2222);
    assertThat(ssh.proxy()).isEqualTo("proxy.example.com");
    assertThat(ssh.keyPath()).isEqualTo("/path/to/key");
    assertThat(ssh.keyPassphrase()).isEqualTo("passphrase");
    assertThat(ssh.strictHostKeyChecking()).isTrue();
    assertThat(ssh.knownHostsPath()).isEqualTo("/path/to/known_hosts");
  }

  @Test
  @DisplayName("should lookup user from environment when key is provided")
  void shouldLookupUserFromEnvironmentWhenKeyIsProvided() {
    // Arrange
    PlainValueOrSecretKey userKey = new PlainValueOrSecretKey(List.of("SSH_USER"));
    SshConfig sshConfig = new SshConfig(userKey, null, null, null, null, null, null, null, null);

    DeployConfig deployConfig = DeployConfigBuilder.minimal().ssh(sshConfig).build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    when(secrets.get("SSH_USER")).thenReturn("ubuntu");

    // Act
    Ssh ssh = new Ssh(config, secrets);

    // Assert
    assertThat(ssh.user()).isEqualTo("ubuntu");
  }

  @Test
  @DisplayName("should lookup keyPath from environment when key is provided")
  void shouldLookupKeyPathFromEnvironmentWhenKeyIsProvided() {
    // Arrange
    PlainValueOrSecretKey keyPathKey = new PlainValueOrSecretKey(List.of("SSH_KEY_PATH"));
    SshConfig sshConfig = new SshConfig(null, null, null, null, null, keyPathKey, null, null, null);

    DeployConfig deployConfig = DeployConfigBuilder.minimal().ssh(sshConfig).build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    when(secrets.get("SSH_KEY_PATH")).thenReturn("/home/user/.ssh/id_rsa");

    // Act
    Ssh ssh = new Ssh(config, secrets);

    // Assert
    assertThat(ssh.keyPath()).isEqualTo("/home/user/.ssh/id_rsa");
  }

  @Test
  @DisplayName("should lookup knownHostsPath from environment when key is provided")
  void shouldLookupKnownHostsPathFromEnvironmentWhenKeyIsProvided() {
    // Arrange
    PlainValueOrSecretKey knownHostsPath = new PlainValueOrSecretKey(List.of("SSH_KNOWN_HOSTS_PATH"));
    SshConfig sshConfig = new SshConfig(null, null, null, null, null, null, null, null, knownHostsPath);

    DeployConfig deployConfig = DeployConfigBuilder.minimal().ssh(sshConfig).build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    when(secrets.get("SSH_KNOWN_HOSTS_PATH")).thenReturn("/home/user/.ssh/known_hosts");

    // Act
    Ssh ssh = new Ssh(config, secrets);

    // Assert
    assertThat(ssh.knownHostsPath()).isEqualTo("/home/user/.ssh/known_hosts");

  }

  @Test
  @DisplayName("should generate options map with user and port")
  void shouldGenerateOptionsMapWithUserAndPort() {
    // Arrange
    PlainValueOrSecretKey user = new PlainValueOrSecretKey("deploy");
    PlainValueOrSecretKey knownHostsPath = new PlainValueOrSecretKey("/path/to/known_hosts");
    SshConfig sshConfig = new SshConfig(user, 2222, null, null, null, null, null, null, knownHostsPath);

    DeployConfig deployConfig = DeployConfigBuilder.minimal().ssh(sshConfig).build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    // Act
    Ssh ssh = new Ssh(config, secrets);
    Map<String, String> options = ssh.options();

    // Assert
    assertThat(options).containsEntry("user", "deploy");
    assertThat(options).containsEntry("port", "2222");
    assertThat(options).containsEntry("keepalive", "true");
    assertThat(options).containsEntry("keepalive_interval", "30");
    assertThat(options).containsEntry("knownHostsPath", "/path/to/known_hosts");
  }

  @Test
  @DisplayName("should resolve to map with all key values")
  void shouldResolveToMapWithAllKeyValues() {
    // Arrange
    PlainValueOrSecretKey user = new PlainValueOrSecretKey("admin");
    PlainValueOrSecretKey keyPath = new PlainValueOrSecretKey("/path/to/key");
    PlainValueOrSecretKey knownHostsPath = new PlainValueOrSecretKey("/path/to/known_hosts");
    SshConfig sshConfig = new SshConfig(
      user,
      3000,
      "proxy",
      null,
      null,
      keyPath,
      null,
      false,
      knownHostsPath
    );

    DeployConfig deployConfig = DeployConfigBuilder.minimal().ssh(sshConfig).build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    // Act
    Ssh ssh = new Ssh(config, secrets);
    Map<String, Object> resolved = ssh.resolve();

    // Assert
    assertThat(resolved).containsEntry("user", "admin");
    assertThat(resolved).containsEntry("port", 3000);
    assertThat(resolved).containsEntry("proxy", "proxy");
    assertThat(resolved).containsEntry("keyPath", "/path/to/key");
    assertThat(resolved).containsEntry("keyPassphrase", null);
    assertThat(resolved).containsEntry("strictHostKeyChecking", false);
    assertThat(resolved).containsKey("options");
    assertThat(resolved).containsEntry("knownHostsPath", "/path/to/known_hosts");
  }

  @Test
  @DisplayName("should use default root user when user is not provided")
  void shouldUseDefaultRootUserWhenUserIsNotProvided() {
    // Arrange
    SshConfig sshConfig = new SshConfig(null, null, null, null, null, null, null, null, null);
    DeployConfig deployConfig = DeployConfigBuilder.minimal().ssh(sshConfig).build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    // Act
    Ssh ssh = new Ssh(config, secrets);

    // Assert
    assertThat(ssh.user()).isEqualTo("root");
  }

  @Test
  @DisplayName("should use default port 22 when port is not provided")
  void shouldUseDefaultPort22WhenPortIsNotProvided() {
    // Arrange
    SshConfig sshConfig = new SshConfig(null, null, null, null, null, null, null, null, null);
    DeployConfig deployConfig = DeployConfigBuilder.minimal().ssh(sshConfig).build();
    Configuration config = mock(Configuration.class);
    when(config.rawConfig()).thenReturn(deployConfig);
    Secrets secrets = mock(Secrets.class);

    // Act
    Ssh ssh = new Ssh(config, secrets);

    // Assert
    assertThat(ssh.port()).isEqualTo(22);
  }
}
