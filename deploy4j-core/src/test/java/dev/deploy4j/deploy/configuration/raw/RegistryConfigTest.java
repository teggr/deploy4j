package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RegistryConfig")
class RegistryConfigTest {

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @Test
    @DisplayName("should deserialize from YAML with all fields")
    void shouldDeserializeFromYamlWithAllFields() throws Exception {
        String yaml = """
            server: registry.example.com
            username: deploy-user
            password:
              - REGISTRY_PASSWORD
            """;

        RegistryConfig config = mapper.readValue(yaml, RegistryConfig.class);

        assertThat(config.server()).isEqualTo("registry.example.com");
        assertThat(config.username().value()).isEqualTo("deploy-user");
        assertThat(config.password().key()).isEqualTo("REGISTRY_PASSWORD");
    }

    @Test
    @DisplayName("should deserialize from YAML with plain password")
    void shouldDeserializeFromYamlWithPlainPassword() throws Exception {
        String yaml = """
            server: ghcr.io
            username: myuser
            password: mypassword
            """;

        RegistryConfig config = mapper.readValue(yaml, RegistryConfig.class);

        assertThat(config.server()).isEqualTo("ghcr.io");
        assertThat(config.username().value()).isEqualTo("myuser");
        assertThat(config.password().value()).isEqualTo("mypassword");
    }

    @Test
    @DisplayName("should deserialize from YAML with only server")
    void shouldDeserializeFromYamlWithOnlyServer() throws Exception {
        String yaml = """
            server: docker.io
            """;

        RegistryConfig config = mapper.readValue(yaml, RegistryConfig.class);

        assertThat(config.server()).isEqualTo("docker.io");
        assertThat(config.username()).isNull();
        assertThat(config.password()).isNull();
    }

    @Test
    @DisplayName("should create empty config with default constructor")
    void shouldCreateEmptyConfigWithDefaultConstructor() {
        RegistryConfig config = new RegistryConfig();

        assertThat(config.server()).isNull();
        assertThat(config.username()).isNull();
        assertThat(config.password()).isNull();
    }

    @Test
    @DisplayName("should create config programmatically with secrets")
    void shouldCreateConfigProgrammaticallyWithSecrets() {
        RegistryConfig config = new RegistryConfig(
            "registry.gitlab.com",
            new PlainValueOrSecretKey(java.util.List.of("GITLAB_USER")),
            new PlainValueOrSecretKey(java.util.List.of("GITLAB_TOKEN"))
        );

        assertThat(config.server()).isEqualTo("registry.gitlab.com");
        assertThat(config.username().key()).isEqualTo("GITLAB_USER");
        assertThat(config.password().key()).isEqualTo("GITLAB_TOKEN");
    }

    @Test
    @DisplayName("should create config programmatically with plain values")
    void shouldCreateConfigProgrammaticallyWithPlainValues() {
        RegistryConfig config = new RegistryConfig(
            "localhost:5000",
            new PlainValueOrSecretKey("testuser"),
            new PlainValueOrSecretKey("testpass")
        );

        assertThat(config.server()).isEqualTo("localhost:5000");
        assertThat(config.username().value()).isEqualTo("testuser");
        assertThat(config.password().value()).isEqualTo("testpass");
    }

    @Test
    @DisplayName("should handle empty YAML")
    void shouldHandleEmptyYaml() throws Exception {
        String yaml = "{}";

        RegistryConfig config = mapper.readValue(yaml, RegistryConfig.class);

        assertThat(config.server()).isNull();
        assertThat(config.username()).isNull();
        assertThat(config.password()).isNull();
    }

    @Test
    @DisplayName("should handle Docker Hub as default registry")
    void shouldHandleDockerHubAsDefaultRegistry() throws Exception {
        String yaml = """
            username: dockerhubuser
            password: dockerhubpassword
            """;

        RegistryConfig config = mapper.readValue(yaml, RegistryConfig.class);

        assertThat(config.server()).isNull(); // Docker Hub is default when server is null
        assertThat(config.username().value()).isEqualTo("dockerhubuser");
        assertThat(config.password().value()).isEqualTo("dockerhubpassword");
    }
}
