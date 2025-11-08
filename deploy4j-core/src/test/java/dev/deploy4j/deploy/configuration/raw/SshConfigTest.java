package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SshConfigTest {

    @Test
    void shouldCreateConfigWithAllFields() {
        PlainValueOrSecretKey user = new PlainValueOrSecretKey("deploy");
        PlainValueOrSecretKey keyPath = new PlainValueOrSecretKey("/home/user/.ssh/id_rsa");
        PlainValueOrSecretKey keyPassphrase = new PlainValueOrSecretKey("secret");
        PlainValueOrSecretKey knownHostsPath = new PlainValueOrSecretKey("/home/user/.ssh/known_hosts");
        
        SshConfig config = new SshConfig(
            user,
            2222,
            "proxy.example.com",
            "ssh -W %h:%p proxy.example.com",
            "INFO",
            keyPath,
            keyPassphrase,
            true,
            knownHostsPath
        );
        
        assertThat(config.user()).isEqualTo(user);
        assertThat(config.port()).isEqualTo(2222);
        assertThat(config.proxy()).isEqualTo("proxy.example.com");
        assertThat(config.proxyCommand()).isEqualTo("ssh -W %h:%p proxy.example.com");
        assertThat(config.logLevel()).isEqualTo("INFO");
        assertThat(config.keyPath()).isEqualTo(keyPath);
        assertThat(config.keyPassphrase()).isEqualTo(keyPassphrase);
        assertThat(config.strictHostKeyChecking()).isTrue();
        assertThat(config.knownHostsPath()).isEqualTo(knownHostsPath);
    }

    @Test
    void shouldCreateEmptyConfig() {
        SshConfig config = new SshConfig();
        
        assertThat(config.user()).isNull();
        assertThat(config.port()).isNull();
        assertThat(config.proxy()).isNull();
        assertThat(config.proxyCommand()).isNull();
        assertThat(config.logLevel()).isNull();
        assertThat(config.keyPath()).isNull();
        assertThat(config.keyPassphrase()).isNull();
        assertThat(config.strictHostKeyChecking()).isNull();
    }

    @Test
    void shouldHandleNullValues() {
        SshConfig config = new SshConfig(null, null, null, null, null, null, null, null, null);
        
        assertThat(config.user()).isNull();
        assertThat(config.port()).isNull();
        assertThat(config.proxy()).isNull();
        assertThat(config.proxyCommand()).isNull();
        assertThat(config.logLevel()).isNull();
        assertThat(config.keyPath()).isNull();
        assertThat(config.keyPassphrase()).isNull();
        assertThat(config.strictHostKeyChecking()).isNull();
        assertThat(config.knownHostsPath()).isNull();
    }

    @Test
    void shouldHandlePartialConfiguration() {
        PlainValueOrSecretKey user = new PlainValueOrSecretKey("deploy");
        
        SshConfig config = new SshConfig(user, 22, null, null, null, null, null, false, null);
        
        assertThat(config.user()).isEqualTo(user);
        assertThat(config.port()).isEqualTo(22);
        assertThat(config.proxy()).isNull();
        assertThat(config.strictHostKeyChecking()).isFalse();
        assertThat(config.knownHostsPath()).isNull();
    }

    @Test
    void shouldHandleSecretKeysForSensitiveFields() {
        PlainValueOrSecretKey user = new PlainValueOrSecretKey(java.util.List.of("SSH_USER"));
        PlainValueOrSecretKey keyPath = new PlainValueOrSecretKey(java.util.List.of("SSH_KEY_PATH"));
        PlainValueOrSecretKey keyPassphrase = new PlainValueOrSecretKey(java.util.List.of("SSH_KEY_PASS"));
      PlainValueOrSecretKey knownHostsPath = new PlainValueOrSecretKey(java.util.List.of("SSH_KNOWN_HOSTS_PATH"));
        
        SshConfig config = new SshConfig(user, 22, null, null, null, keyPath, keyPassphrase, true, knownHostsPath);
        
        assertThat(config.user().key()).isEqualTo("SSH_USER");
        assertThat(config.keyPath().key()).isEqualTo("SSH_KEY_PATH");
        assertThat(config.keyPassphrase().key()).isEqualTo("SSH_KEY_PASS");
        assertThat(config.knownHostsPath().key()).isEqualTo("SSH_KNOWN_HOSTS_PATH");
    }
}
