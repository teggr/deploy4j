package dev.deploy4j.deploy.env;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ENVDotenv")
class ENVDotenvTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ENV.clear();
    }

    @Test
    @DisplayName("should load environment variables from dotenv file")
    void shouldLoadEnvironmentVariables() throws IOException {
        // Arrange
        Path envFile = tempDir.resolve(".env");
        Files.writeString(envFile, """
                APP_NAME=myapp
                APP_VERSION=1.0.0
                DATABASE_URL=postgres://localhost/db
                """);

        // Act
        ENVDotenv.overload(envFile.toString());

        // Assert
        assertThat(ENV.fetch("APP_NAME")).isEqualTo("myapp");
        assertThat(ENV.fetch("APP_VERSION")).isEqualTo("1.0.0");
        assertThat(ENV.fetch("DATABASE_URL")).isEqualTo("postgres://localhost/db");
    }

    @Test
    @DisplayName("should overwrite existing environment variables")
    void shouldOverwriteExistingVariables() throws IOException {
        // Arrange
        ENV.update(java.util.Map.of("APP_NAME", "oldvalue"));
        Path envFile = tempDir.resolve(".env");
        Files.writeString(envFile, "APP_NAME=newvalue\n");

        // Act
        ENVDotenv.overload(envFile.toString());

        // Assert
        assertThat(ENV.fetch("APP_NAME")).isEqualTo("newvalue");
    }

    @Test
    @DisplayName("should load from multiple dotenv files")
    void shouldLoadFromMultipleFiles() throws IOException {
        // Arrange
        Path envFile1 = tempDir.resolve(".env.1");
        Files.writeString(envFile1, "VAR1=value1\n");

        Path envFile2 = tempDir.resolve(".env.2");
        Files.writeString(envFile2, "VAR2=value2\n");

        // Act
        ENVDotenv.overload(envFile1.toString(), envFile2.toString());

        // Assert
        assertThat(ENV.fetch("VAR1")).isEqualTo("value1");
        assertThat(ENV.fetch("VAR2")).isEqualTo("value2");
    }

    @Test
    @DisplayName("should throw exception when file does not exist")
    void shouldThrowExceptionWhenFileDoesNotExist() {
        // Arrange
        String nonExistentFile = tempDir.resolve("nonexistent.env").toString();

        // Act & Assert
        assertThatThrownBy(() -> ENVDotenv.overload(nonExistentFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to read");
    }

    @Test
    @DisplayName("should handle empty dotenv file")
    void shouldHandleEmptyFile() throws IOException {
        // Arrange
        Path envFile = tempDir.resolve(".env");
        Files.writeString(envFile, "");

        // Act
        ENVDotenv.overload(envFile.toString());

        // Assert - should not throw exception
        assertThat(true).isTrue();
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
        ENVDotenv.overload(envFile.toString());

        // Assert
        assertThat(ENV.fetch("APP_NAME")).isEqualTo("myapp");
        assertThat(ENV.fetch("APP_VERSION")).isEqualTo("1.0.0");
    }
}
