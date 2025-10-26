package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SshConfig")
class SshConfigTest {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Test
    @DisplayName("should deserialize from YAML with all fields")
    void shouldDeserializeFromYamlWithAllFields() throws Exception {
        String yaml = """
            user: deploy
            port: 2222
            proxy: jump.example.com
            proxy_command: ssh -W %h:%p jump.example.com
            log_level: DEBUG
            key_path: ~/.ssh/id_rsa
            key_passphrase:
              - SSH_PASSPHRASE
            strict_host_key_checking: false
            """;

        SshConfig config = mapper.readValue(yaml, SshConfig.class);

        assertThat(config.user().value()).isEqualTo("deploy");
        assertThat(config.port()).isEqualTo(2222);
        assertThat(config.proxy()).isEqualTo("jump.example.com");
        assertThat(config.proxyCommand()).isEqualTo("ssh -W %h:%p jump.example.com");
        assertThat(config.logLevel()).isEqualTo("DEBUG");
        assertThat(config.keyPath().value()).isEqualTo("~/.ssh/id_rsa");
        assertThat(config.keyPassphrase().key()).isEqualTo("SSH_PASSPHRASE");
        assertThat(config.strictHostKeyChecking()).isFalse();
    }

    @Test
    @DisplayName("should deserialize from YAML with minimal fields")
    void shouldDeserializeFromYamlWithMinimalFields() throws Exception {
        String yaml = """
            user: root
            """;

        SshConfig config = mapper.readValue(yaml, SshConfig.class);

        assertThat(config.user().value()).isEqualTo("root");
        assertThat(config.port()).isNull();
        assertThat(config.proxy()).isNull();
        assertThat(config.proxyCommand()).isNull();
        assertThat(config.logLevel()).isNull();
        assertThat(config.keyPath()).isNull();
        assertThat(config.keyPassphrase()).isNull();
        assertThat(config.strictHostKeyChecking()).isNull();
    }

    @Test
    @DisplayName("should create empty config with default constructor")
    void shouldCreateEmptyConfigWithDefaultConstructor() {
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
    @DisplayName("should create config programmatically with secrets")
    void shouldCreateConfigProgrammaticallyWithSecrets() {
        SshConfig config = new SshConfig(
            new PlainValueOrSecretKey("admin"),
            22,
            null,
            null,
            "INFO",
            new PlainValueOrSecretKey(java.util.List.of("SSH_KEY_PATH")),
            new PlainValueOrSecretKey(java.util.List.of("SSH_PASSPHRASE_KEY")),
            true
        );

        assertThat(config.user().value()).isEqualTo("admin");
        assertThat(config.port()).isEqualTo(22);
        assertThat(config.logLevel()).isEqualTo("INFO");
        assertThat(config.keyPath().key()).isEqualTo("SSH_KEY_PATH");
        assertThat(config.keyPassphrase().key()).isEqualTo("SSH_PASSPHRASE_KEY");
        assertThat(config.strictHostKeyChecking()).isTrue();
    }

    @Test
    @DisplayName("should handle custom port")
    void shouldHandleCustomPort() throws Exception {
        String yaml = """
            port: 8022
            """;

        SshConfig config = mapper.readValue(yaml, SshConfig.class);

        assertThat(config.port()).isEqualTo(8022);
    }

    @Test
    @DisplayName("should handle strict host key checking as true")
    void shouldHandleStrictHostKeyCheckingAsTrue() throws Exception {
        String yaml = """
            strict_host_key_checking: true
            """;

        SshConfig config = mapper.readValue(yaml, SshConfig.class);

        assertThat(config.strictHostKeyChecking()).isTrue();
    }

    @Test
    @DisplayName("should handle empty YAML")
    void shouldHandleEmptyYaml() throws Exception {
        String yaml = "{}";

        SshConfig config = mapper.readValue(yaml, SshConfig.class);

        assertThat(config.user()).isNull();
        assertThat(config.port()).isNull();
        assertThat(config.strictHostKeyChecking()).isNull();
    }
}
