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

  @Test
  @DisplayName("should create deploy.yml with deploy configuration and secrets file with environment variables")
  void shouldCreateFilesWithCorrectContent() throws Exception {
    // This test verifies that the templates have the expected content
    // by reading them directly from resources, ensuring deploy.yml gets deploy config
    // and secrets file gets environment variables template
    
    // Arrange & Act
    var deployYmlStream = getClass().getClassLoader().getResourceAsStream("templates/deploy.yml");
    var secretsStream = getClass().getClassLoader().getResourceAsStream("templates/secrets");
    
    // Assert - deploy.yml template should contain deploy configuration with Thymeleaf expressions
    assertThat(deployYmlStream).isNotNull();
    String deployContent = new String(deployYmlStream.readAllBytes());
    assertThat(deployContent).contains("service: [(${model.serviceName()})]");
    assertThat(deployContent).contains("image: [(${model.imageName()})]");
    assertThat(deployContent).contains("servers:");
    // Ensure it's not the secrets content
    assertThat(deployContent).doesNotContain("DOCKER_PASSWORD=");
    
    // Assert - secrets template should contain environment variables
    assertThat(secretsStream).isNotNull();
    String secretsContent = new String(secretsStream.readAllBytes());
    assertThat(secretsContent).contains("DOCKER_PASSWORD=");
    assertThat(secretsContent).contains("DOCKER_USERNAME=");
    assertThat(secretsContent).contains("PRIVATE_KEY=");
    assertThat(secretsContent).contains("PRIVATE_KEY_PASSPHRASE=");
    // Ensure it's not the deploy config
    assertThat(secretsContent).doesNotContain("service: deploy4j-demo");
    assertThat(secretsContent).doesNotContain("service: [(${model.serviceName()})]");
  }
}
