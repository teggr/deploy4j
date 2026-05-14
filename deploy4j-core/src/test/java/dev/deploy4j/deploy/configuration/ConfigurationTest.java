package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Configuration")
class ConfigurationTest {

    @Nested
    @DisplayName("version handling")
    class VersionHandling {

        @Test
        @DisplayName("should return declared version when provided")
        void shouldReturnDeclaredVersion() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.2.3");

            // Act & Assert
            assertThat(configuration.version()).isEqualTo("1.2.3");
        }

        @Test
        @DisplayName("should abbreviate version to 7 characters when not underscore-separated")
        void shouldAbbreviateVersion() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1234567890abc");

            // Act & Assert
            assertThat(configuration.abbreviatedVersion()).isEqualTo("1234567");
        }

        @Test
        @DisplayName("should not abbreviate underscore-separated version")
        void shouldNotAbbreviateUnderscoreVersion() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "v1_2_3");

            // Act & Assert
            assertThat(configuration.abbreviatedVersion()).isEqualTo("v1_2_3");
        }

        @Test
        @DisplayName("should handle version with exactly 7 characters")
        void shouldHandleExactly7CharacterVersion() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1234567");

            // Act & Assert
            assertThat(configuration.abbreviatedVersion()).isEqualTo("1234567");
        }

        @Test
        @DisplayName("should handle version shorter than 7 characters")
        void shouldHandleShortVersion() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "0.0.5");

            // Act & Assert
            assertThat(configuration.abbreviatedVersion()).isEqualTo("0.0.5");
        }

        @Test
        @DisplayName("should handle very short version")
        void shouldHandleVeryShortVersion() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.0");

            // Act & Assert
            assertThat(configuration.abbreviatedVersion()).isEqualTo("1.0");
        }

        @Test
        @DisplayName("should handle single character version")
        void shouldHandleSingleCharacterVersion() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1");

            // Act & Assert
            assertThat(configuration.abbreviatedVersion()).isEqualTo("1");
        }

        @Test
        @DisplayName("should allow updating version after construction")
        void shouldAllowUpdatingVersion() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.0.0");

            // Act
            configuration.version("2.0.0");

            // Assert
            assertThat(configuration.version()).isEqualTo("2.0.0");
        }
    }

    @Nested
    @DisplayName("host management")
    class HostManagement {

        @Test
        @DisplayName("should return all hosts from configuration")
        void shouldReturnAllHostsFromConfiguration() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.0.0");

            // Act
            List<String> hosts = configuration.allHosts();

            // Assert - At minimum, should return hosts from the servers config
            assertThat(hosts).isNotNull();
        }

        @Test
        @DisplayName("should return gateway hosts")
        void shouldReturnGatewayHosts() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.0.0");

            // Act
            List<String> gatewayHosts = configuration.gatewayHosts();

            // Assert
            assertThat(gatewayHosts).isNotNull();
        }
    }

    @Nested
    @DisplayName("image and repository")
    class ImageAndRepository {

        @Test
        @DisplayName("should construct absolute image with version")
        void shouldConstructAbsoluteImageWithVersion() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.2.3");

            // Act
            String absoluteImage = configuration.absoluteImage();

            // Assert
            assertThat(absoluteImage).endsWith(":1.2.3");
        }

        @Test
        @DisplayName("should construct latest image with latest tag")
        void shouldConstructLatestImageWithLatestTag() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.2.3");

            // Act
            String latestImage = configuration.latestImage();

            // Assert
            assertThat(latestImage).endsWith(":latest-prod");
        }

        @Test
        @DisplayName("should construct latest tag with destination")
        void shouldConstructLatestTagWithDestination() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "staging", "1.0.0");

            // Act
            String latestTag = configuration.latestTag();

            // Assert
            assertThat(latestTag).isEqualTo("latest-staging");
        }

        @Test
        @DisplayName("should construct repository from registry server and image")
        void shouldConstructRepositoryFromRegistryServerAndImage() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.0.0");

            // Act
            String repository = configuration.repository();

            // Assert
            assertThat(repository).isNotNull();
        }
    }

    @Nested
    @DisplayName("configuration properties")
    class ConfigurationProperties {

        @Test
        @DisplayName("should return service name from raw config")
        void shouldReturnServiceNameFromRawConfig() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.0.0");

            // Act
            String service = configuration.service();

            // Assert
            assertThat(service).isEqualTo("test-service");
        }

        @Test
        @DisplayName("should return image name from raw config")
        void shouldReturnImageNameFromRawConfig() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.0.0");

            // Act
            String image = configuration.image();

            // Assert
            assertThat(image).isEqualTo("test-image");
        }

        @Test
        @DisplayName("should return destination")
        void shouldReturnDestination() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "production", "1.0.0");

            // Act
            String destination = configuration.destination();

            // Assert
            assertThat(destination).isEqualTo("production");
        }

        @Test
        @DisplayName("should return default retain containers when not specified")
        void shouldReturnDefaultRetainContainersWhenNotSpecified() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal()
              .retainContainers(null)
              .build();

            Configuration configuration = new Configuration(deployConfig, "prod", "1.0.0");

            // Act
            int retainContainer = configuration.retainContainer();

            // Assert
            assertThat(retainContainer).isEqualTo(5);
        }

        @Test
        @DisplayName("should return custom retain containers when specified")
        void shouldReturnCustomRetainContainersWhenSpecified() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().retainContainers(10).build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.0.0");

            // Act
            int retainContainer = configuration.retainContainer();

            // Assert
            assertThat(retainContainer).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("role management")
    class RoleManagement {

        @Test
        @DisplayName("should return all roles")
        void shouldReturnAllRoles() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.0.0");

            // Act
            List<Role> roles = configuration.roles();

            // Assert - Should return at least the default "web" role for a list config
            assertThat(roles).isNotNull();
        }

        @Test
        @DisplayName("should return null for non-existent role")
        void shouldReturnNullForNonExistentRole() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.0.0");

            // Act
            Role role = configuration.role("non-existent-role-12345");

            // Assert
            assertThat(role).isNull();
        }
    }

    @Nested
    @DisplayName("run ID generation")
    class RunIdGeneration {

        @Test
        @DisplayName("should generate run ID")
        void shouldGenerateRunId() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.0.0");

            // Act
            String runId = configuration.runId();

            // Assert
            assertThat(runId)
                .isNotNull()
                .hasSize(32)
                .matches("^[0-9a-f]+$");
        }

        @Test
        @DisplayName("should return same run ID on multiple calls")
        void shouldReturnSameRunIdOnMultipleCalls() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.0.0");

            // Act
            String runId1 = configuration.runId();
            String runId2 = configuration.runId();

            // Assert
            assertThat(runId1).isEqualTo(runId2);
        }
    }

    @Nested
    @DisplayName("healthcheck configuration")
    class HealthcheckConfiguration {

        @Test
        @DisplayName("should construct healthcheck service name")
        void shouldConstructHealthcheckServiceName() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, "prod", "1.0.0");

            // Act
            String healthcheckService = configuration.healthcheckService();

            // Assert
            assertThat(healthcheckService).isEqualTo("healthcheck-test-service-prod");
        }

        @Test
        @DisplayName("should construct healthcheck service name without destination")
        void shouldConstructHealthcheckServiceNameWithoutDestination() {
            // Arrange
            DeployConfig deployConfig = DeployConfigBuilder.minimal().build();
            Configuration configuration = new Configuration(deployConfig, null, "1.0.0");

            // Act
            String healthcheckService = configuration.healthcheckService();

            // Assert
            assertThat(healthcheckService).isEqualTo("healthcheck-test-service");
        }
    }

    @Nested
    @DisplayName("validation")
    class Validation {

        @Test
        @DisplayName("should require destination when requireDestination is true")
        void shouldRequireDestinationWhenRequired() {
            DeployConfig deployConfig = DeployConfigBuilder.minimal()
                .service("svc")
                .image("img")
                .registry(new RegistryConfig("reg", null, null))
                .requireDestination(true)
                .build();

            assertThatThrownBy(() -> new Configuration(deployConfig, null, "1.0.0"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("You must specify a destination");
        }

        @Test
        @DisplayName("should complain when required keys are missing")
        void shouldComplainWhenRequiredKeysMissing() {
            DeployConfig deployConfig = DeployConfigBuilder.minimal()
                .service("svc")
                .image(null) // missing image
                .build();

            assertThatThrownBy(() -> new Configuration(deployConfig, "prod", "1.0.0"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Missing required configuration for")
                .hasMessageContaining("image");
        }

        @Test
        @DisplayName("should validate minimum required version against current version")
        void shouldValidateMinimumVersion() {
            DeployConfig deployConfig = DeployConfigBuilder.minimal()
                .minimumVersion("9999.0.0")
                .build();

            assertThatThrownBy(() -> new Configuration(deployConfig, "prod", "1.0.0"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Current version is");
        }

        @Test
        @DisplayName("should require retainContainers to be at least 1")
        void shouldValidateRetainContainers() {
            DeployConfig deployConfig = DeployConfigBuilder.minimal()
                .retainContainers(0)
                .build();

            assertThatThrownBy(() -> new Configuration(deployConfig, "prod", "1.0.0"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Must retain at least 1 container");
        }

        @Test
        @DisplayName("should validate service name characters")
        void shouldValidateServiceName() {
            DeployConfig deployConfig = DeployConfigBuilder.minimal()
                .service("invalid service!") // invalid characters
                .build();

            assertThatThrownBy(() -> new Configuration(deployConfig, "prod", "1.0.0"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Service name can only include alphanumeric characters, hyphens, and underscores");
        }
    }
}
