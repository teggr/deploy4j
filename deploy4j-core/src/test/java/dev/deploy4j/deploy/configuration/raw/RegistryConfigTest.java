package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistryConfigTest {

    @Test
    void shouldCreateConfigWithServerUsernameAndPassword() {
        PlainValueOrSecretKey username = new PlainValueOrSecretKey("myuser");
        PlainValueOrSecretKey password = new PlainValueOrSecretKey("mypass");
        
        RegistryConfig config = new RegistryConfig("docker.io", username, password);
        
        assertThat(config.server()).isEqualTo("docker.io");
        assertThat(config.username()).isEqualTo(username);
        assertThat(config.password()).isEqualTo(password);
    }

    @Test
    void shouldCreateEmptyConfig() {
        RegistryConfig config = new RegistryConfig();
        
        assertThat(config.server()).isNull();
        assertThat(config.username()).isNull();
        assertThat(config.password()).isNull();
    }

    @Test
    void shouldHandleNullValues() {
        RegistryConfig config = new RegistryConfig(null, null, null);
        
        assertThat(config.server()).isNull();
        assertThat(config.username()).isNull();
        assertThat(config.password()).isNull();
    }

    @Test
    void shouldHandleSecretKeyInUsername() {
        PlainValueOrSecretKey username = new PlainValueOrSecretKey(java.util.List.of("DOCKER_USER"));
        PlainValueOrSecretKey password = new PlainValueOrSecretKey("mypass");
        
        RegistryConfig config = new RegistryConfig("docker.io", username, password);
        
        assertThat(config.username().key()).isEqualTo("DOCKER_USER");
        assertThat(config.password().value()).isEqualTo("mypass");
    }

    @Test
    void shouldHandleSecretKeyInPassword() {
        PlainValueOrSecretKey username = new PlainValueOrSecretKey("myuser");
        PlainValueOrSecretKey password = new PlainValueOrSecretKey(java.util.List.of("DOCKER_PASS"));
        
        RegistryConfig config = new RegistryConfig("docker.io", username, password);
        
        assertThat(config.username().value()).isEqualTo("myuser");
        assertThat(config.password().key()).isEqualTo("DOCKER_PASS");
    }
}
