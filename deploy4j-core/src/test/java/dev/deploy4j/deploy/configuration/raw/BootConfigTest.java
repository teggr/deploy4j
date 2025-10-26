package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BootConfig")
class BootConfigTest {

    @Test
    @DisplayName("should create boot config with limit and wait")
    void shouldCreateWithLimitAndWait() {
        // Act
        BootConfig config = new BootConfig("10", "5");

        // Assert
        assertThat(config.limit()).isEqualTo("10");
        assertThat(config.waiter()).isEqualTo("5");
    }

    @Test
    @DisplayName("should create empty boot config with no-args constructor")
    void shouldCreateEmptyConfig() {
        // Act
        BootConfig config = new BootConfig();

        // Assert
        assertThat(config.limit()).isNull();
        assertThat(config.waiter()).isNull();
    }

    @Test
    @DisplayName("should handle null limit")
    void shouldHandleNullLimit() {
        // Act
        BootConfig config = new BootConfig(null, "5");

        // Assert
        assertThat(config.limit()).isNull();
        assertThat(config.waiter()).isEqualTo("5");
    }

    @Test
    @DisplayName("should handle null wait")
    void shouldHandleNullWait() {
        // Act
        BootConfig config = new BootConfig("10", null);

        // Assert
        assertThat(config.limit()).isEqualTo("10");
        assertThat(config.waiter()).isNull();
    }
}
