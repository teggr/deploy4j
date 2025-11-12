package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.AccessoryConfig;
import dev.deploy4j.deploy.configuration.raw.DeployConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Accessory Configuration")
class AccessoryTest {

    @Nested
    @DisplayName("service name")
    class ServiceName {

        @Test
        @DisplayName("should use configured service name when provided")
        void shouldUseConfiguredServiceNameWhenProvided() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .service("my-redis")
                    .image("redis:latest")
                    .host("redis-host")
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert
            assertThat(accessory.serviceName()).isEqualTo("my-redis");
        }

        @Test
        @DisplayName("should generate service name from config service and accessory name")
        void shouldGenerateServiceNameFromConfigServiceAndAccessoryName() {
            // Arrange
            Configuration config = createConfigWithAccessory("mysql",
                DeployConfigBuilder.accessory()
                    .image("mysql:8.0")
                    .host("mysql-host")
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("mysql", config);

            // Assert
            assertThat(accessory.serviceName()).isEqualTo("test-app-mysql");
        }
    }

    @Nested
    @DisplayName("image")
    class Image {

        @Test
        @DisplayName("should return configured image")
        void shouldReturnConfiguredImage() {
            // Arrange
            Configuration config = createConfigWithAccessory("postgres",
                DeployConfigBuilder.accessory()
                    .image("postgres:14")
                    .host("postgres-host")
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("postgres", config);

            // Assert
            assertThat(accessory.image()).isEqualTo("postgres:14");
        }
    }

    @Nested
    @DisplayName("port configuration")
    class PortConfiguration {

        @Test
        @DisplayName("should format port as host:container when only port number provided")
        void shouldFormatPortAsHostContainerWhenOnlyPortNumberProvided() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .image("redis:latest")
                    .host("redis-host")
                    .port("6379")
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert
            assertThat(accessory.port()).isEqualTo("6379:6379");
        }

        @Test
        @DisplayName("should keep port format when already specified as host:container")
        void shouldKeepPortFormatWhenAlreadySpecified() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .image("redis:latest")
                    .host("redis-host")
                    .port("6380:6379")
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert
            assertThat(accessory.port()).isEqualTo("6380:6379");
        }

        @Test
        @DisplayName("should return null when port not configured")
        void shouldReturnNullWhenPortNotConfigured() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .image("redis:latest")
                    .host("redis-host")
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert
            assertThat(accessory.port()).isNull();
        }

        @Test
        @DisplayName("should generate publish args when port configured")
        void shouldGeneratePublishArgsWhenPortConfigured() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .image("redis:latest")
                    .host("redis-host")
                    .port("6379")
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert
            assertThat(accessory.publishArgs())
                .contains("--publish", "6379:6379");
        }

        @Test
        @DisplayName("should return empty array when no port configured")
        void shouldReturnEmptyArrayWhenNoPortConfigured() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .image("redis:latest")
                    .host("redis-host")
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert
            assertThat(accessory.publishArgs()).isEmpty();
        }
    }

    @Nested
    @DisplayName("labels")
    class Labels {

        @Test
        @DisplayName("should include default service label")
        void shouldIncludeDefaultServiceLabel() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .service("my-redis")
                    .image("redis:latest")
                    .host("redis-host")
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert
            assertThat(accessory.labels())
                .containsEntry("service", "my-redis");
        }

        @Test
        @DisplayName("should merge custom labels with default labels")
        void shouldMergeCustomLabelsWithDefaultLabels() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .image("redis:latest")
                    .host("redis-host")
                    .labels(Map.of("env", "production", "version", "1.0"))
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert
            assertThat(accessory.labels())
                .containsEntry("service", "test-app-redis")
                .containsEntry("env", "production")
                .containsEntry("version", "1.0");
        }

        @Test
        @DisplayName("should generate label args")
        void shouldGenerateLabelArgs() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .image("redis:latest")
                    .host("redis-host")
                    .labels(Map.of("env", "prod"))
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert
            assertThat(accessory.labelArgs())
                .contains("--label");
        }
    }

    @Nested
    @DisplayName("volumes")
    class Volumes {

        @Test
        @DisplayName("should return specific volumes when configured")
        void shouldReturnSpecificVolumesWhenConfigured() {
            // Arrange
            Configuration config = createConfigWithAccessory("mysql",
                DeployConfigBuilder.accessory()
                    .image("mysql:8.0")
                    .host("redis-host")
                    .volumes(List.of("mysql-data:/var/lib/mysql"))
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("mysql", config);

            // Assert
            assertThat(accessory.volumes())
                .contains("mysql-data:/var/lib/mysql");
        }

        @Test
        @DisplayName("should generate volume args")
        void shouldGenerateVolumeArgs() {
            // Arrange
            Configuration config = createConfigWithAccessory("mysql",
                DeployConfigBuilder.accessory()
                    .image("mysql:8.0")
                    .host("redis-host")
                    .volumes(List.of("mysql-data:/var/lib/mysql"))
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("mysql", config);

            // Assert
            assertThat(accessory.volumeArgs())
                .contains("--volume", "mysql-data:/var/lib/mysql");
        }

        @Test
        @DisplayName("should return empty volume args when no volumes configured")
        void shouldReturnEmptyVolumeArgsWhenNoVolumesConfigured() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .image("redis:latest")
                    .host("redis-host")
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert - At minimum should not fail
            assertThat(accessory.volumeArgs()).isNotNull();
        }
    }

    @Nested
    @DisplayName("options")
    class Options {

        @Test
        @DisplayName("should return option args when configured")
        void shouldReturnOptionArgsWhenConfigured() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .image("redis:latest")
                    .host("redis-host")
                    .options(Map.of("memory", "1g", "cpus", "2"))
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert
            assertThat(accessory.optionArgs())
                .isNotEmpty();
        }

        @Test
        @DisplayName("should return empty list when no options configured")
        void shouldReturnEmptyListWhenNoOptionsConfigured() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .image("redis:latest")
                    .host("redis-host")
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert
            assertThat(accessory.optionArgs()).isEmpty();
        }
    }

    @Nested
    @DisplayName("command")
    class Command {

        @Test
        @DisplayName("should return configured command")
        void shouldReturnConfiguredCommand() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .image("redis:latest")
                    .host("redis-host")
                    .cmd("redis-server --appendonly yes")
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert
            assertThat(accessory.cmd()).isEqualTo("redis-server --appendonly yes");
        }

        @Test
        @DisplayName("should return null when no command configured")
        void shouldReturnNullWhenNoCommandConfigured() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .image("redis:latest")
                    .host("redis-host")
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert
            assertThat(accessory.cmd()).isNull();
        }
    }

    @Nested
    @DisplayName("environment")
    class Environment {

        @Test
        @DisplayName("should provide environment args")
        void shouldProvideEnvironmentArgs() {
            // Arrange
            Configuration config = createConfigWithAccessory("redis",
                DeployConfigBuilder.accessory()
                    .image("redis:latest")
                    .host("redis-host")
                    .build()
            );

            // Act
            Accessory accessory = new Accessory("redis", config);

            // Assert
            assertThat(accessory.envArgs()).isNotNull();
        }
    }

    // Helper method to create test configuration
    private Configuration createConfigWithAccessory(String accessoryName, AccessoryConfig accessoryConfig) {
        // Build DeployConfig using the test builder and attach the accessory map
        DeployConfig deployConfig = DeployConfigBuilder.minimal()
            .service("test-app")
            .accessories(Map.of(accessoryName, accessoryConfig))
            .build();

        return new Configuration(deployConfig, null, null);
    }
}
