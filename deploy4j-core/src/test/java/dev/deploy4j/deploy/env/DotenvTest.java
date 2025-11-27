package dev.deploy4j.deploy.env;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ENVDotenv")
class DotenvTest {

  @TempDir
  Path tempDir;

  @Test
  @DisplayName("should load environment variables from dotenv file")
  void shouldLoadFileEnvironmentVariables() throws IOException {
    // Arrange
    Path envFile = tempDir.resolve(".env");
    Files.writeString(envFile, """
      APP_NAME=myapp
      APP_VERSION=1.0.0
      DATABASE_URL=postgres://localhost/db
      """);

    // Act
    Map<String, String> environment = Dotenv.parse(envFile.toString());

    // Assert
    assertThat(environment.get("APP_NAME")).isEqualTo("myapp");
    assertThat(environment.get("APP_VERSION")).isEqualTo("1.0.0");
    assertThat(environment.get("DATABASE_URL")).isEqualTo("postgres://localhost/db");
  }

  @Test
  @DisplayName("should load environment variables with empty values from dotenv file")
  void shouldLoadFileEnvironmentVariablesWithEmptyValues() throws IOException {
    // Arrange
    Path envFile = tempDir.resolve(".env");
    Files.writeString(envFile, """
      APP_NAME=
      """);

    // Act
    Map<String, String> environment = Dotenv.parse(envFile.toString());

    // Assert
    assertThat(environment.get("APP_NAME")).isEmpty();
  }

  @Test
  @DisplayName("should get existing environment variables if $REFERENCE used")
  void shouldGetExistingVariablesIfNoValueInFile() throws IOException {
    // Arrange
    String existingEnvironmentVariableKey = System.getenv().keySet().stream().findFirst().orElseThrow();
    Path envFile = tempDir.resolve(".env");
    Files.writeString(envFile, existingEnvironmentVariableKey + "=$" + existingEnvironmentVariableKey);

    // Act
    Map<String, String> environment = Dotenv.parse(envFile.toString());

    // Assert
    assertThat(environment.get(existingEnvironmentVariableKey)).isEqualTo(System.getenv().get(existingEnvironmentVariableKey));
  }

  @Test
  @DisplayName("should overwrite existing environment variables")
  void shouldOverwriteExistingVariables() throws IOException {
    // Arrange
    String existingEnvironmentVariableKey = System.getenv().keySet().stream().findFirst().orElseThrow();
    Path envFile = tempDir.resolve(".env");
    Files.writeString(envFile, existingEnvironmentVariableKey + "=newvalue\n");

    // Act
    Map<String, String> environment = Dotenv.parse(envFile.toString());

    // Assert
    assertThat(environment.get(existingEnvironmentVariableKey)).isEqualTo("newvalue");
  }

  @Test
  @DisplayName("should throw exception when file does not exist")
  void shouldThrowExceptionWhenFileDoesNotExist() {
    // Arrange
    String nonExistentFile = tempDir.resolve("nonexistent.env").toString();

    // Act & Assert
    assertThatThrownBy(() -> Dotenv.parse(nonExistentFile))
      .isInstanceOf(RuntimeException.class)
      .hasMessageContaining("Failed to read");
  }

  @Test
  @DisplayName("should handle empty dotenv file")
  void shouldHandleEmptyFile() throws IOException {
    // Arrange
    String existingEnvironmentVariableKey = System.getenv().keySet().stream().findFirst().orElseThrow();
    Path envFile = tempDir.resolve(".env");
    Files.writeString(envFile, "");

    // Act
    Map<String, String> environment = Dotenv.parse(envFile.toString());

    // Assert - should not throw exception
    assertThat(environment.containsKey(existingEnvironmentVariableKey)).isFalse();
    assertThat(environment.containsKey("APP_NAME")).isFalse();
  }

  @Test
  @DisplayName("should handle dotenv file with comments and whitespace")
  void shouldHandleCommentsAndWhitespace() throws IOException {
    // Arrange
    Path envFile = tempDir.resolve(".env");
    Files.writeString(envFile, """
      # This is a comment
      APP_NAME=myapp
      # Another comment
      APP_VERSION=1.0.0
      """);

    // Act
    Map<String, String> parse = Dotenv.parse(envFile.toString());

    // Assert
    assertThat(parse.get("APP_NAME")).isEqualTo("myapp");
    assertThat(parse.get("APP_VERSION")).isEqualTo("1.0.0");
  }
}
