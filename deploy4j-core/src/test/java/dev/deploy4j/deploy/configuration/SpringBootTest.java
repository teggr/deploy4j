package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.DeployConfig;
import dev.deploy4j.deploy.configuration.raw.SpringBootConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SpringBoot Configuration")
class SpringBootTest {

    @Test
    @DisplayName("should return configured hosts")
    void shouldReturnConfiguredHosts() {
        // Arrange
        SpringBootConfig springBootConfig = new SpringBootConfig(
            List.of("host1", "host2", "host3"),
            null,
            8080,
            "/actuator"
        );
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(springBootConfig)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);

        // Act
        List<String> hosts = configuration.springBoot().hosts();

        // Assert
        assertThat(hosts).containsExactly("host1", "host2", "host3");
    }

    @Test
    @DisplayName("should return app hosts when no spring boot hosts configured")
    void shouldReturnAppHostsWhenNoSpringBootHostsConfigured() {
        // Arrange
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(null)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);

        // Act
        List<String> hosts = configuration.springBoot().hosts();

        // Assert
        assertThat(hosts).containsExactly("host1");
    }

    @Test
    @DisplayName("should return default actuator port")
    void shouldReturnDefaultActuatorPort() {
        // Arrange
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(null)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);

        // Act
        Integer port = configuration.springBoot().actuatorPort();

        // Assert
        assertThat(port).isEqualTo(8080);
    }

    @Test
    @DisplayName("should return configured actuator port")
    void shouldReturnConfiguredActuatorPort() {
        // Arrange
        SpringBootConfig springBootConfig = new SpringBootConfig(
            null,
            null,
            9999,
            null
        );
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(springBootConfig)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);

        // Act
        Integer port = configuration.springBoot().actuatorPort();

        // Assert
        assertThat(port).isEqualTo(9999);
    }

    @Test
    @DisplayName("should return default actuator base path")
    void shouldReturnDefaultActuatorBasePath() {
        // Arrange
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(null)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);

        // Act
        String basePath = configuration.springBoot().actuatorBasePath();

        // Assert
        assertThat(basePath).isEqualTo("/actuator");
    }

    @Test
    @DisplayName("should return configured actuator base path")
    void shouldReturnConfiguredActuatorBasePath() {
        // Arrange
        SpringBootConfig springBootConfig = new SpringBootConfig(
            null,
            null,
            null,
            "/management"
        );
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(springBootConfig)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);

        // Act
        String basePath = configuration.springBoot().actuatorBasePath();

        // Assert
        assertThat(basePath).isEqualTo("/management");
    }

    @Test
    @DisplayName("should return configured tags")
    void shouldReturnConfiguredTags() {
        // Arrange
        SpringBootConfig springBootConfig = new SpringBootConfig(
            null,
            List.of("web", "api"),
            null,
            null
        );
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(springBootConfig)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);

        // Act
        List<String> tags = configuration.springBoot().tags();

        // Assert
        assertThat(tags).containsExactly("web", "api");
    }

    @Test
    @DisplayName("should return empty tags when not configured")
    void shouldReturnEmptyTagsWhenNotConfigured() {
        // Arrange
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(null)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);

        // Act
        List<String> tags = configuration.springBoot().tags();

        // Assert
        assertThat(tags).isEmpty();
    }

    @Test
    @DisplayName("should generate correct endpoint URL with container name")
    void shouldGenerateCorrectEndpointUrl() {
        // Arrange
        SpringBootConfig springBootConfig = new SpringBootConfig(
            null,
            null,
            8081,
            "/actuator"
        );
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(springBootConfig)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);

        // Act
        String url = configuration.springBoot().endpointUrl("health", "myservice-web-1.0.0");

        // Assert
        assertThat(url).isEqualTo("http://myservice-web-1.0.0:8081/actuator/health");
    }

    @Test
    @DisplayName("should generate correct endpoint URL with trailing slash base path")
    void shouldGenerateCorrectEndpointUrlWithTrailingSlashBasePath() {
        // Arrange
        SpringBootConfig springBootConfig = new SpringBootConfig(
            null,
            null,
            8080,
            "/actuator/"
        );
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(springBootConfig)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);

        // Act
        String url = configuration.springBoot().endpointUrl("health", "myservice-web-1.0.0");

        // Assert
        assertThat(url).isEqualTo("http://myservice-web-1.0.0:8080/actuator/health");
    }

    @Test
    @DisplayName("should generate correct endpoint URL with endpoint starting with slash")
    void shouldGenerateCorrectEndpointUrlWithSlashEndpoint() {
        // Arrange
        SpringBootConfig springBootConfig = new SpringBootConfig(
            null,
            null,
            8080,
            "/actuator"
        );
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(springBootConfig)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);

        // Act
        String url = configuration.springBoot().endpointUrl("/health", "myservice-web-1.0.0");

        // Assert
        assertThat(url).isEqualTo("http://myservice-web-1.0.0:8080/actuator/health");
    }

    @Test
    @DisplayName("should return effective hosts from configured hosts when no tags")
    void shouldReturnEffectiveHostsFromConfiguredHostsWhenNoTags() {
        // Arrange
        SpringBootConfig springBootConfig = new SpringBootConfig(
            List.of("host1", "host2"),
            null,
            null,
            null
        );
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .springBoot(springBootConfig)
            .build();
        Configuration configuration = new Configuration(deployConfig, null, null);

        // Act
        List<String> effectiveHosts = configuration.springBoot().effectiveHosts();

        // Assert
        assertThat(effectiveHosts).containsExactly("host1", "host2");
    }
}
