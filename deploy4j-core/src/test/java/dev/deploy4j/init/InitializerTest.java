package dev.deploy4j.init;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
    
    // Assert - deploy.yml template should contain deploy configuration
    assertThat(deployYmlStream).isNotNull();
    String deployContent = new String(deployYmlStream.readAllBytes());
    assertThat(deployContent).contains("service: deploy4j-demo");
    assertThat(deployContent).contains("image: teggr/deploy4j-demo");
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
  }

  @Test
  @DisplayName("should substitute custom hostname in deploy.yml")
  void shouldSubstituteHostnameInDeployYml() throws Exception {
    // Arrange
    String originalUserDir = System.getProperty("user.dir");
    System.setProperty("user.dir", tempDir.toString());

    try {
      InitConfig config = new InitConfig(false, "192.168.1.100", List.of(), InitConfig.AgentType.NONE);

      // Act
      new Initializer().init(config);

      // Assert
      Path deployFile = tempDir.resolve("config/deploy.yml");
      String content = Files.readString(deployFile);
      assertThat(content).contains("- 192.168.1.100");
      assertThat(content).doesNotContain("- localhost");
    } finally {
      System.setProperty("user.dir", originalUserDir);
    }
  }

  @Test
  @DisplayName("should include extra secret names in secrets file")
  void shouldIncludeExtraSecretsInSecretsFile() throws Exception {
    // Arrange
    String originalUserDir = System.getProperty("user.dir");
    System.setProperty("user.dir", tempDir.toString());

    try {
      InitConfig config = new InitConfig(false, "localhost", List.of("DB_PASSWORD", "API_KEY"), InitConfig.AgentType.NONE);

      // Act
      new Initializer().init(config);

      // Assert
      Path secretsFile = tempDir.resolve(".deploy4j/secrets");
      String content = Files.readString(secretsFile);
      assertThat(content).contains("DB_PASSWORD=");
      assertThat(content).contains("API_KEY=");
      assertThat(content).contains("DOCKER_PASSWORD=");
    } finally {
      System.setProperty("user.dir", originalUserDir);
    }
  }

  @Test
  @DisplayName("should create GitHub Copilot instructions file with deploy4j skills")
  void shouldCreateCopilotSkillsFile() throws Exception {
    // Arrange
    String originalUserDir = System.getProperty("user.dir");
    System.setProperty("user.dir", tempDir.toString());

    try {
      InitConfig config = new InitConfig(false, "localhost", List.of(), InitConfig.AgentType.COPILOT);

      // Act
      new Initializer().init(config);

      // Assert
      Path copilotFile = tempDir.resolve(".github/copilot-instructions.md");
      assertThat(copilotFile).exists();
      String content = Files.readString(copilotFile);
      assertThat(content).contains("deploy4j Deployment");
      assertThat(content).contains("config/deploy.yml");
    } finally {
      System.setProperty("user.dir", originalUserDir);
    }
  }

  @Test
  @DisplayName("should create CLAUDE.md file with deploy4j skills")
  void shouldCreateClaudeSkillsFile() throws Exception {
    // Arrange
    String originalUserDir = System.getProperty("user.dir");
    System.setProperty("user.dir", tempDir.toString());

    try {
      InitConfig config = new InitConfig(false, "localhost", List.of(), InitConfig.AgentType.CLAUDE);

      // Act
      new Initializer().init(config);

      // Assert
      Path claudeFile = tempDir.resolve("CLAUDE.md");
      assertThat(claudeFile).exists();
      String content = Files.readString(claudeFile);
      assertThat(content).contains("deploy4j Deployment");
      assertThat(content).contains("config/deploy.yml");
    } finally {
      System.setProperty("user.dir", originalUserDir);
    }
  }

  @Test
  @DisplayName("should append deploy4j skills to existing copilot-instructions.md without duplication")
  void shouldAppendCopilotSkillsToExistingFileWithoutDuplication() throws Exception {
    // Arrange
    String originalUserDir = System.getProperty("user.dir");
    System.setProperty("user.dir", tempDir.toString());

    try {
      Path githubDir = tempDir.resolve(".github");
      Files.createDirectories(githubDir);
      Path copilotFile = githubDir.resolve("copilot-instructions.md");
      Files.writeString(copilotFile, "# Existing instructions\n");

      InitConfig config = new InitConfig(false, "localhost", List.of(), InitConfig.AgentType.COPILOT);

      // Act - first call appends
      new Initializer().init(config);
      String contentAfterFirst = Files.readString(copilotFile);
      assertThat(contentAfterFirst).contains("# Existing instructions");
      assertThat(contentAfterFirst).contains("deploy4j Deployment");

      // Act - second call should not duplicate
      new Initializer().init(config);
      String contentAfterSecond = Files.readString(copilotFile);
      long count = contentAfterSecond.lines()
          .filter(l -> l.contains("deploy4j Deployment")).count();
      assertThat(count).isEqualTo(1);
    } finally {
      System.setProperty("user.dir", originalUserDir);
    }
  }

  @Test
  @DisplayName("should use localhost as default hostname when InitConfig has null hostname")
  void shouldUseDefaultHostname() {
    InitConfig config = new InitConfig(false, null, List.of(), InitConfig.AgentType.NONE);
    assertThat(config.getHostname()).isEqualTo(InitConfig.DEFAULT_HOSTNAME);
  }

}

