package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DeployConfigYamlReader")
class DeployConfigYamlReaderTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("should read YAML from string")
    void shouldReadYamlFromString() throws Exception {
        String yaml = """
            service: myapp
            image: myorg/myapp
            servers:
              - 192.168.0.1
              - 192.168.0.2
            registry:
              username: registry-user
              password:
                - REGISTRY_PASSWORD
            env:
              secret:
                - APP_SECRET
            """;

        DeployConfig config = DeployConfigYamlReader.readYamlFromString(yaml);

        assertThat(config.service()).isEqualTo("myapp");
        assertThat(config.image()).isEqualTo("myorg/myapp");
        assertThat(config.servers().list()).hasSize(2);
        assertThat(config.registry().username().value()).isEqualTo("registry-user");
        assertThat(config.registry().password().key()).isEqualTo("REGISTRY_PASSWORD");
        assertThat(config.env().secrets()).containsExactly("APP_SECRET");
    }

    @Test
    @DisplayName("should read YAML from file")
    void shouldReadYamlFromFile() throws Exception {
        String yaml = """
            service: webapp
            image: webapp:1.0
            servers:
              - web1.example.com
            """;

        Path yamlFile = tempDir.resolve("config.yml");
        Files.writeString(yamlFile, yaml);

        DeployConfig config = DeployConfigYamlReader.readYaml(yamlFile.toString());

        assertThat(config.service()).isEqualTo("webapp");
        assertThat(config.image()).isEqualTo("webapp:1.0");
        assertThat(config.servers().list()).hasSize(1);
        assertThat(config.servers().list().get(0).host()).isEqualTo("web1.example.com");
    }

    @Test
    @DisplayName("should merge multiple YAML files in order")
    void shouldMergeMultipleYamlFilesInOrder() throws Exception {
        // Base config
        String baseYaml = """
            service: myapp
            image: myorg/myapp:latest
            servers:
              - 192.168.1.10
            env:
              clear:
                BASE_VAR: base_value
                SHARED_VAR: base_shared
            """;

        // Override config
        String overrideYaml = """
            image: myorg/myapp:v2.0
            servers:
              - 192.168.1.11
              - 192.168.1.12
            env:
              clear:
                SHARED_VAR: override_shared
                OVERRIDE_VAR: override_value
            """;

        Path baseFile = tempDir.resolve("base.yml");
        Path overrideFile = tempDir.resolve("override.yml");
        Files.writeString(baseFile, baseYaml);
        Files.writeString(overrideFile, overrideYaml);

        DeployConfig config = DeployConfigYamlReader.loadConfigFiles(
            List.of(baseFile.toString(), overrideFile.toString())
        );

        assertThat(config.service()).isEqualTo("myapp"); // From base
        assertThat(config.image()).isEqualTo("myorg/myapp:v2.0"); // Overridden
        assertThat(config.servers().list()).hasSize(2); // Overridden
        assertThat(config.servers().list().get(0).host()).isEqualTo("192.168.1.11");
        assertThat(config.servers().list().get(1).host()).isEqualTo("192.168.1.12");
        assertThat(config.env().clear()).containsEntry("BASE_VAR", "base_value"); // From base
        assertThat(config.env().clear()).containsEntry("OVERRIDE_VAR", "override_value"); // From override
        assertThat(config.env().clear()).containsEntry("SHARED_VAR", "override_shared"); // Overridden
    }

    @Test
    @DisplayName("should merge nested objects correctly")
    void shouldMergeNestedObjectsCorrectly() throws Exception {
        String baseYaml = """
            service: testapp
            registry:
              server: registry.example.com
              username: baseuser
            ssh:
              port: 22
            """;

        String overrideYaml = """
            registry:
              username: overrideuser
              password: secret123
            ssh:
              user: deploy
            """;

        Path baseFile = tempDir.resolve("base.yml");
        Path overrideFile = tempDir.resolve("override.yml");
        Files.writeString(baseFile, baseYaml);
        Files.writeString(overrideFile, overrideYaml);

        DeployConfig config = DeployConfigYamlReader.loadConfigFiles(
            List.of(baseFile.toString(), overrideFile.toString())
        );

        assertThat(config.service()).isEqualTo("testapp"); // From base
        assertThat(config.registry().server()).isEqualTo("registry.example.com"); // From base
        assertThat(config.registry().username().value()).isEqualTo("overrideuser"); // Overridden
        assertThat(config.registry().password().value()).isEqualTo("secret123"); // From override
        assertThat(config.ssh().port()).isEqualTo(22); // From base
        assertThat(config.ssh().user().value()).isEqualTo("deploy"); // From override
    }

    @Test
    @DisplayName("should handle loading single file")
    void shouldHandleLoadingSingleFile() throws Exception {
        String yaml = """
            service: singleapp
            image: singleapp:1.0
            servers:
              - single.example.com
            """;

        Path yamlFile = tempDir.resolve("single.yml");
        Files.writeString(yamlFile, yaml);

        DeployConfig config = DeployConfigYamlReader.loadConfigFiles(
            List.of(yamlFile.toString())
        );

        assertThat(config.service()).isEqualTo("singleapp");
        assertThat(config.image()).isEqualTo("singleapp:1.0");
        assertThat(config.servers().list()).hasSize(1);
    }

    @Test
    @DisplayName("should throw exception for non-existent file")
    void shouldThrowExceptionForNonExistentFile() {
        assertThatThrownBy(() -> 
            DeployConfigYamlReader.readYaml("/non/existent/file.yml")
        ).isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("should throw RuntimeException when loadConfigFiles fails")
    void shouldThrowRuntimeExceptionWhenLoadConfigFilesFails() {
        assertThatThrownBy(() -> 
            DeployConfigYamlReader.loadConfigFiles(List.of("/non/existent/file.yml"))
        ).isInstanceOf(RuntimeException.class)
         .hasMessageContaining("Failed to load or merge YAML config files");
    }

    @Test
    @DisplayName("should handle complex merge with three files")
    void shouldHandleComplexMergeWithThreeFiles() throws Exception {
        String file1 = """
            service: app
            image: app:1.0
            env:
              clear:
                VAR1: value1
            """;

        String file2 = """
            image: app:2.0
            env:
              clear:
                VAR2: value2
            """;

        String file3 = """
            env:
              clear:
                VAR3: value3
            """;

        Path path1 = tempDir.resolve("file1.yml");
        Path path2 = tempDir.resolve("file2.yml");
        Path path3 = tempDir.resolve("file3.yml");
        Files.writeString(path1, file1);
        Files.writeString(path2, file2);
        Files.writeString(path3, file3);

        DeployConfig config = DeployConfigYamlReader.loadConfigFiles(
            List.of(path1.toString(), path2.toString(), path3.toString())
        );

        assertThat(config.service()).isEqualTo("app"); // From file1
        assertThat(config.image()).isEqualTo("app:2.0"); // Overridden by file2
        assertThat(config.env().clear()).hasSize(3)
            .containsEntry("VAR1", "value1")
            .containsEntry("VAR2", "value2")
            .containsEntry("VAR3", "value3");
    }
}
