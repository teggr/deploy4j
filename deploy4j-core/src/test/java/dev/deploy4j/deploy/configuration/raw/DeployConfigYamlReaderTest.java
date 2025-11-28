package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DeployConfigYamlReader")
class DeployConfigYamlReaderTest {

    @Test
    @DisplayName("should read basic YAML configuration")
    void shouldReadBasicYamlConfiguration() throws IOException {
        // Arrange
        String yaml = """
            service: myapp
            image: repo/myapp
            servers:
              - 192.168.1.1
              - 192.168.1.2
            """;

        // Act
        DeployConfig config = DeployConfigYamlReader.readYamlFromString(yaml);

        // Assert
        assertThat(config.service()).isEqualTo("myapp");
        assertThat(config.image()).isEqualTo("repo/myapp");
        assertThat(config.servers().list()).hasSize(2);
    }

    @Test
    @DisplayName("should merge multiple YAML files")
    void shouldMergeMultipleYamlFiles(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path file1 = tempDir.resolve("config1.yml");
        Files.writeString(file1, """
            service: myapp
            image: repo/myapp
            servers:
              - 192.168.1.1
            """);

        Path file2 = tempDir.resolve("config2.yml");
        Files.writeString(file2, """
            servers:
              - 192.168.1.2
            registry:
              username: testuser
            """);

        // Act
        DeployConfig config = DeployConfigYamlReader.loadConfigFiles(
            List.of(file1.toString(), file2.toString())
        );

        // Assert
        assertThat(config.service()).isEqualTo("myapp");
        assertThat(config.image()).isEqualTo("repo/myapp");
        assertThat(config.servers().list()).hasSize(1); // Only last file's servers
        assertThat(config.servers().list().get(0).host()).isEqualTo("192.168.1.2");
    }

    @Test
    @DisplayName("should merge nested objects correctly")
    void shouldMergeNestedObjectsCorrectly(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path file1 = tempDir.resolve("base.yml");
        Files.writeString(file1, """
            service: myapp
            registry:
              server: registry.example.com
              username: baseuser
            """);

        Path file2 = tempDir.resolve("override.yml");
        Files.writeString(file2, """
            registry:
              username: overrideuser
              password:
                - REGISTRY_PASSWORD
            """);

        // Act
        DeployConfig config = DeployConfigYamlReader.loadConfigFiles(
            List.of(file1.toString(), file2.toString())
        );

        // Assert
        assertThat(config.registry().server()).isEqualTo("registry.example.com");
        assertThat(config.registry().username().value()).isEqualTo("overrideuser");
        assertThat(config.registry().password().key()).isEqualTo("REGISTRY_PASSWORD");
    }

    @Test
    @DisplayName("should read YAML from file path")
    void shouldReadYamlFromFilePath(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path configFile = tempDir.resolve("deploy.yml");
        Files.writeString(configFile, """
            service: testservice
            image: test/image
            servers:
              - host1.example.com
            """);

        // Act
        DeployConfig config = DeployConfigYamlReader.readYaml(configFile.toString());

        // Assert
        assertThat(config.service()).isEqualTo("testservice");
        assertThat(config.image()).isEqualTo("test/image");
        assertThat(config.servers().list()).hasSize(1);
    }

    @Test
    @DisplayName("should handle complex configuration with env and secrets")
    void shouldHandleComplexConfiguration() throws IOException {
        // Arrange
        String yaml = """
            service: myapp
            image: repo/myapp:latest
            servers:
              - 192.168.1.1
            registry:
              username: user
              password:
                - REGISTRY_PASSWORD
            env:
              clear:
                DB_HOST: localhost
              secret:
                - DATABASE_URL
                - SECRET_KEY
            """;

        // Act
        DeployConfig config = DeployConfigYamlReader.readYamlFromString(yaml);

        // Assert
        assertThat(config.service()).isEqualTo("myapp");
        assertThat(config.env().clear()).containsEntry("DB_HOST", "localhost");
        assertThat(config.env().secrets()).containsExactly("DATABASE_URL", "SECRET_KEY");
    }

    @Test
    @DisplayName("should throw exception for invalid YAML")
    void shouldThrowExceptionForInvalidYaml() {
        // Arrange
        String invalidYaml = """
            service: myapp
            invalid: [unclosed bracket
            """;

        // Act & Assert
        assertThatThrownBy(() -> DeployConfigYamlReader.readYamlFromString(invalidYaml))
            .isInstanceOf(Exception.class); // Jackson throws different exceptions for YAML parse errors
    }

    @Test
    @DisplayName("should throw exception for non-existent file")
    void shouldThrowExceptionForNonExistentFile() {
        // Act & Assert
        assertThatThrownBy(() -> DeployConfigYamlReader.readYaml("/nonexistent/file.yml"))
            .isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("should handle minimal config file with just service name")
    void shouldHandleMinimalConfigFile(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path minimalFile = tempDir.resolve("minimal.yml");
        Files.writeString(minimalFile, "service: myapp\n");

        // Act
        DeployConfig config = DeployConfigYamlReader.readYaml(minimalFile.toString());

        // Assert
        assertThat(config).isNotNull();
        assertThat(config.service()).isEqualTo("myapp");
    }

    @Test
    @DisplayName("should merge three or more files correctly")
    void shouldMergeMultipleFilesCorrectly(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path file1 = tempDir.resolve("base.yml");
        Files.writeString(file1, """
            service: myapp
            image: repo/myapp
            """);

        Path file2 = tempDir.resolve("staging.yml");
        Files.writeString(file2, """
            servers:
              - staging.example.com
            """);

        Path file3 = tempDir.resolve("overrides.yml");
        Files.writeString(file3, """
            image: repo/myapp:v2
            """);

        // Act
        DeployConfig config = DeployConfigYamlReader.loadConfigFiles(
            List.of(file1.toString(), file2.toString(), file3.toString())
        );

        // Assert
        assertThat(config.service()).isEqualTo("myapp");
        assertThat(config.image()).isEqualTo("repo/myapp:v2");
        assertThat(config.servers().list()).hasSize(1);
    }

  @Test
  @DisplayName("should process thymeleaf expressions in YAML files")
  void shouldProcessThymeleafExpressionsInYamlFiles(@TempDir Path tempDir) throws IOException {

      System.setProperty("test_prop", "dynamicServiceName");

    // Arrange
    Path file1 = tempDir.resolve("base.yml");
    Files.writeString(file1, """
            service: [(${props.get('test_prop')})]
            registry:
              server: registry.example.com
              username: baseuser
            """);

    Path file2 = tempDir.resolve("override.yml");
    Files.writeString(file2, """
            registry:
              username: overrideuser
              password:
                - REGISTRY_PASSWORD
            """);

    // Act
    DeployConfig config = DeployConfigYamlReader.loadConfigFiles(
      List.of(file1.toString(), file2.toString())
    );

    // Assert
    assertThat(config.service()).isEqualTo("dynamicServiceName");
    assertThat(config.registry().server()).isEqualTo("registry.example.com");
    assertThat(config.registry().username().value()).isEqualTo("overrideuser");
    assertThat(config.registry().password().key()).isEqualTo("REGISTRY_PASSWORD");
  }

}
