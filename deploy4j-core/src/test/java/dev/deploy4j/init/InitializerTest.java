package dev.deploy4j.init;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Initializer")
class InitializerTest {

  @TempDir
  Path tempDir;

  @Test
  @DisplayName("should not overwrite existing deploy.yml config file")
  void shouldNotOverwriteExistingDeployYml() throws Exception {
    // Arrange
    Initializer initializer = new Initializer();

    // Change to temp directory
    String originalUserDir = System.getProperty("user.dir");
    System.setProperty("user.dir", tempDir.toString());

    try {
      Path configDir = tempDir.resolve("config");
      Files.createDirectories(configDir);
      Path deployFile = configDir.resolve("deploy.yml");
      String existingContent = "existing content";
      Files.writeString(deployFile, existingContent);

      // Act
      initializer.init(false);

      // Assert - content should not be changed
      String content = Files.readString(deployFile);
      assertThat(content).isEqualTo(existingContent);
    } finally {
      // Restore original user.dir
      System.setProperty("user.dir", originalUserDir);
    }
  }

  @Test
  @DisplayName("should not overwrite existing .deploy4j/secrets file")
  void shouldNotOverwriteExistingEnvFile() throws Exception {
    // Arrange
    Initializer initializer = new Initializer();

    // Change to temp directory
    String originalUserDir = System.getProperty("user.dir");
    System.setProperty("user.dir", tempDir.toString());

    try {
      Path configDir = tempDir.resolve(".deploy4j");
      Files.createDirectories(configDir);
      Path envFile = configDir.resolve("secrets");
      String existingContent = "EXISTING_VAR=value";
      Files.writeString(envFile, existingContent);

      // Act
      initializer.init(false);

      // Assert - content should not be changed
      String content = Files.readString(envFile);
      assertThat(content).isEqualTo(existingContent);
    } finally {
      // Restore original user.dir
      System.setProperty("user.dir", originalUserDir);
    }
  }
}
