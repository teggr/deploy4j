package dev.deploy4j.deploy.utils;

import dev.deploy4j.deploy.configuration.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Utils")
class UtilsTest {

    @Test
    @DisplayName("should filter string items by matching filter")
    void shouldFilterStringItemsByMatchingFilter() {
        // Arrange
        List<String> items = List.of("web", "db", "worker", "api");
        String[] filters = {"web", "db"};

        // Act
        List<String> filtered = Utils.filterSpecificItems(filters, items);

        // Assert
        assertThat(filtered)
                .hasSize(2)
                .containsExactlyInAnyOrder("web", "db");
    }

    @Test
    @DisplayName("should be case insensitive when filtering strings")
    void shouldBeCaseInsensitiveForStrings() {
        // Arrange
        List<String> items = List.of("Web", "DB", "Worker");
        String[] filters = {"web", "db"};

        // Act
        List<String> filtered = Utils.filterSpecificItems(filters, items);

        // Assert
        assertThat(filtered)
                .hasSize(2)
                .containsExactlyInAnyOrder("Web", "DB");
    }

    @Test
    @DisplayName("should return empty list when no filters match")
    void shouldReturnEmptyListWhenNoMatch() {
        // Arrange
        List<String> items = List.of("web", "db", "worker");
        String[] filters = {"nonexistent"};

        // Act
        List<String> filtered = Utils.filterSpecificItems(filters, items);

        // Assert
        assertThat(filtered).isEmpty();
    }

    @Test
    @DisplayName("should return distinct items when filter matches multiple times")
    void shouldReturnDistinctItems() {
        // Arrange
        List<String> items = List.of("web", "db", "web");
        String[] filters = {"web"};

        // Act
        List<String> filtered = Utils.filterSpecificItems(filters, items);

        // Assert
        assertThat(filtered)
                .hasSize(1)
                .containsExactly("web");
    }

    @Test
    @DisplayName("should handle empty filter array")
    void shouldHandleEmptyFilters() {
        // Arrange
        List<String> items = List.of("web", "db");
        String[] filters = {};

        // Act
        List<String> filtered = Utils.filterSpecificItems(filters, items);

        // Assert
        assertThat(filtered).isEmpty();
    }

    @Test
    @DisplayName("should handle empty items list")
    void shouldHandleEmptyItems() {
        // Arrange
        List<String> items = List.of();
        String[] filters = {"web"};

        // Act
        List<String> filtered = Utils.filterSpecificItems(filters, items);

        // Assert
        assertThat(filtered).isEmpty();
    }
}
