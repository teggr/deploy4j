package dev.deploy4j.deploy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tags")
class TagsTest {

    @Test
    @DisplayName("should create tags from map")
    void shouldCreateTagsFromMap() {
        // Arrange
        Map<String, String> tagMap = Map.of("key1", "value1", "key2", "value2");

        // Act
        Tags tags = new Tags(tagMap);

        // Assert
        assertThat(tags.tags()).isEqualTo(tagMap);
    }

    @Test
    @DisplayName("should transform tags to environment variable format")
    void shouldTransformTagsToEnvFormat() {
        // Arrange
        Map<String, String> tagMap = Map.of("service", "myapp", "version", "1.0.0");
        Tags tags = new Tags(tagMap);

        // Act
        Tags envTags = tags.env();

        // Assert
        assertThat(envTags.tags())
                .containsEntry("DEPLOY4J_SERVICE", "myapp")
                .containsEntry("DEPLOY4J_VERSION", "1.0.0")
                .hasSize(2);
    }

    @Test
    @DisplayName("should convert tags to tag string format")
    void shouldConvertToTagString() {
        // Arrange
        Map<String, String> tagMap = Map.of("service", "myapp", "version", "1.0");
        Tags tags = new Tags(tagMap);

        // Act
        String tagString = tags.toTagString();

        // Assert
        assertThat(tagString)
                .contains("[myapp]")
                .contains("[1.0]");
    }

    @Test
    @DisplayName("should exclude specified tags")
    void shouldExcludeSpecifiedTags() {
        // Arrange
        Map<String, String> tagMap = Map.of(
                "service", "myapp",
                "version", "1.0",
                "destination", "production"
        );
        Tags tags = new Tags(tagMap);

        // Act
        Tags filtered = tags.except("version");

        // Assert
        assertThat(filtered.tags())
                .containsEntry("service", "myapp")
                .containsEntry("destination", "production")
                .doesNotContainKey("version")
                .hasSize(2);
    }

    @Test
    @DisplayName("should exclude multiple tags")
    void shouldExcludeMultipleTags() {
        // Arrange
        Map<String, String> tagMap = Map.of(
                "service", "myapp",
                "version", "1.0",
                "destination", "production"
        );
        Tags tags = new Tags(tagMap);

        // Act
        Tags filtered = tags.except("version", "destination");

        // Assert
        assertThat(filtered.tags())
                .containsEntry("service", "myapp")
                .doesNotContainKey("version")
                .doesNotContainKey("destination")
                .hasSize(1);
    }

    @Test
    @DisplayName("should handle empty tags map")
    void shouldHandleEmptyTagsMap() {
        // Arrange & Act
        Tags tags = new Tags(Map.of());

        // Assert
        assertThat(tags.tags()).isEmpty();
        assertThat(tags.toTagString()).isEmpty();
    }

    @Test
    @DisplayName("should handle except with non-existent keys")
    void shouldHandleExceptWithNonExistentKeys() {
        // Arrange
        Map<String, String> tagMap = Map.of("service", "myapp");
        Tags tags = new Tags(tagMap);

        // Act
        Tags filtered = tags.except("nonexistent");

        // Assert
        assertThat(filtered.tags())
                .containsEntry("service", "myapp")
                .hasSize(1);
    }

    @Test
    @DisplayName("should preserve original tags when creating env tags")
    void shouldPreserveOriginalTagsWhenCreatingEnvTags() {
        // Arrange
        Map<String, String> tagMap = Map.of("service", "myapp");
        Tags tags = new Tags(tagMap);

        // Act
        Tags envTags = tags.env();

        // Assert
        assertThat(tags.tags()).containsEntry("service", "myapp");
        assertThat(envTags.tags()).containsEntry("DEPLOY4J_SERVICE", "myapp");
    }

    @Test
    @DisplayName("should ignore null values in tags")
    void shouldIgnoreNullValuesInTags() {
        // Arrange
        Map<String, String> tagMap = new HashMap<>();
        tagMap.put("service", "myapp");
        tagMap.put("version", null);
        tagMap.put("destination", "production");

        // Act
        Tags tags = new Tags(tagMap);

        // Assert
        assertThat(tags.tags())
                .containsEntry("service", "myapp")
                .containsEntry("destination", "production")
                .doesNotContainKey("version")
                .hasSize(2);
    }
}
