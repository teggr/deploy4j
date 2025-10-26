package dev.deploy4j.deploy.utils.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("File utility")
class FileTest {

    @Test
    @DisplayName("should join path segments with forward slashes")
    void shouldJoinPathSegments() {
        String result = File.join("home", "user", "documents");
        assertThat(result).isEqualTo("home/user/documents");
    }

    @Test
    @DisplayName("should filter out null segments when joining")
    void shouldFilterOutNullSegments() {
        String result = File.join("home", null, "documents");
        assertThat(result).isEqualTo("home/documents");
    }

    @Test
    @DisplayName("should handle empty string segments")
    void shouldHandleEmptySegments() {
        String result = File.join("home", "", "documents");
        assertThat(result).isEqualTo("home//documents");
    }

    @Test
    @DisplayName("should handle single path segment")
    void shouldHandleSingleSegment() {
        String result = File.join("home");
        assertThat(result).isEqualTo("home");
    }

    @Test
    @DisplayName("should handle no path segments")
    void shouldHandleNoSegments() {
        String result = File.join();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should handle all null segments")
    void shouldHandleAllNullSegments() {
        String result = File.join(null, null, null);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should get directory name from file path")
    void shouldGetDirectoryName() {
        String result = File.dirname("/home/user/documents/file.txt");
        assertThat(result).isEqualTo("/home/user/documents");
    }

    @Test
    @DisplayName("should get directory name from path without file")
    void shouldGetDirectoryNameFromPath() {
        String result = File.dirname("/home/user/documents");
        assertThat(result).isEqualTo("/home/user");
    }

    @Test
    @DisplayName("should handle root directory")
    void shouldHandleRootDirectory() {
        String result = File.dirname("/file.txt");
        assertThat(result).isEqualTo("/");
    }

    @Test
    @DisplayName("should handle relative path")
    void shouldHandleRelativePath() {
        String result = File.dirname("documents/file.txt");
        assertThat(result).isEqualTo("documents");
    }
}
