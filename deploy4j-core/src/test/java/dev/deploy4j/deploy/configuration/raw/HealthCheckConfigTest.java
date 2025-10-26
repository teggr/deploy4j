package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HealthCheckConfig")
class HealthCheckConfigTest {

    @Nested
    @DisplayName("construction")
    class Construction {

        @Test
        @DisplayName("should construct with all parameters")
        void shouldConstructWithAllParameters() {
            // Act
            HealthCheckConfig config = new HealthCheckConfig(
                "curl -f http://localhost/health",
                "10s",
                3,
                8080,
                "/health",
                "health.cord",
                100
            );

            // Assert
            assertThat(config.cmd()).isEqualTo("curl -f http://localhost/health");
            assertThat(config.interval()).isEqualTo("10s");
            assertThat(config.maxAttempts()).isEqualTo(3);
            assertThat(config.port()).isEqualTo(8080);
            assertThat(config.path()).isEqualTo("/health");
            assertThat(config.cord()).isEqualTo("health.cord");
            assertThat(config.logLines()).isEqualTo(100);
        }

        @Test
        @DisplayName("should construct with default values when using no-arg constructor")
        void shouldConstructWithDefaultValues() {
            // Act
            HealthCheckConfig config = new HealthCheckConfig();

            // Assert
            assertThat(config.cmd()).isNull();
            assertThat(config.interval()).isNull();
            assertThat(config.maxAttempts()).isEqualTo(0);
            assertThat(config.port()).isNull();
            assertThat(config.path()).isNull();
            assertThat(config.cord()).isNull();
            assertThat(config.logLines()).isNull();
        }
    }

    @Nested
    @DisplayName("deep merge")
    class DeepMerge {

        @Test
        @DisplayName("should override all values when other config has all non-null values")
        void shouldOverrideAllValuesWhenOtherConfigHasAllNonNullValues() {
            // Arrange
            HealthCheckConfig base = new HealthCheckConfig(
                "base-cmd",
                "5s",
                5,
                3000,
                "/base",
                "base.cord",
                50
            );

            HealthCheckConfig other = new HealthCheckConfig(
                "other-cmd",
                "15s",
                10,
                9000,
                "/other",
                "other.cord",
                200
            );

            // Act
            HealthCheckConfig merged = base.deepMerge(other);

            // Assert
            assertThat(merged.cmd()).isEqualTo("other-cmd");
            assertThat(merged.interval()).isEqualTo("15s");
            assertThat(merged.maxAttempts()).isEqualTo(10);
            assertThat(merged.port()).isEqualTo(9000);
            assertThat(merged.path()).isEqualTo("/other");
            assertThat(merged.cord()).isEqualTo("other.cord");
            assertThat(merged.logLines()).isEqualTo(200);
        }

        @Test
        @DisplayName("should keep base values when other config has null values")
        void shouldKeepBaseValuesWhenOtherConfigHasNullValues() {
            // Arrange
            HealthCheckConfig base = new HealthCheckConfig(
                "base-cmd",
                "5s",
                5,
                3000,
                "/base",
                "base.cord",
                50
            );

            HealthCheckConfig other = new HealthCheckConfig();

            // Act
            HealthCheckConfig merged = base.deepMerge(other);

            // Assert
            assertThat(merged.cmd()).isEqualTo("base-cmd");
            assertThat(merged.interval()).isEqualTo("5s");
            assertThat(merged.maxAttempts()).isEqualTo(5);
            assertThat(merged.port()).isEqualTo(3000);
            assertThat(merged.path()).isEqualTo("/base");
            assertThat(merged.cord()).isEqualTo("base.cord");
            assertThat(merged.logLines()).isEqualTo(50);
        }

        @Test
        @DisplayName("should selectively override values from other config")
        void shouldSelectivelyOverrideValuesFromOtherConfig() {
            // Arrange
            HealthCheckConfig base = new HealthCheckConfig(
                "base-cmd",
                "5s",
                5,
                3000,
                "/base",
                "base.cord",
                50
            );

            HealthCheckConfig other = new HealthCheckConfig(
                null,           // cmd - keep base
                "15s",          // interval - override
                0,              // maxAttempts - keep base (0 is considered "not set")
                9000,           // port - override
                null,           // path - keep base
                "other.cord",   // cord - override
                null            // logLines - keep base
            );

            // Act
            HealthCheckConfig merged = base.deepMerge(other);

            // Assert
            assertThat(merged.cmd()).isEqualTo("base-cmd");
            assertThat(merged.interval()).isEqualTo("15s");
            assertThat(merged.maxAttempts()).isEqualTo(5);
            assertThat(merged.port()).isEqualTo(9000);
            assertThat(merged.path()).isEqualTo("/base");
            assertThat(merged.cord()).isEqualTo("other.cord");
            assertThat(merged.logLines()).isEqualTo(50);
        }

        @Test
        @DisplayName("should handle merging empty configs")
        void shouldHandleMergingEmptyConfigs() {
            // Arrange
            HealthCheckConfig base = new HealthCheckConfig();
            HealthCheckConfig other = new HealthCheckConfig();

            // Act
            HealthCheckConfig merged = base.deepMerge(other);

            // Assert
            assertThat(merged.cmd()).isNull();
            assertThat(merged.interval()).isNull();
            assertThat(merged.maxAttempts()).isEqualTo(0);
            assertThat(merged.port()).isNull();
            assertThat(merged.path()).isNull();
            assertThat(merged.cord()).isNull();
            assertThat(merged.logLines()).isNull();
        }

        @Test
        @DisplayName("should handle merging when base is empty and other has values")
        void shouldHandleMergingWhenBaseIsEmptyAndOtherHasValues() {
            // Arrange
            HealthCheckConfig base = new HealthCheckConfig();
            HealthCheckConfig other = new HealthCheckConfig(
                "cmd",
                "10s",
                3,
                8080,
                "/health",
                "cord",
                100
            );

            // Act
            HealthCheckConfig merged = base.deepMerge(other);

            // Assert
            assertThat(merged.cmd()).isEqualTo("cmd");
            assertThat(merged.interval()).isEqualTo("10s");
            assertThat(merged.maxAttempts()).isEqualTo(3);
            assertThat(merged.port()).isEqualTo(8080);
            assertThat(merged.path()).isEqualTo("/health");
            assertThat(merged.cord()).isEqualTo("cord");
            assertThat(merged.logLines()).isEqualTo(100);
        }

        @Test
        @DisplayName("should treat zero max attempts as not set in merge")
        void shouldTreatZeroMaxAttemptsAsNotSetInMerge() {
            // Arrange
            HealthCheckConfig base = new HealthCheckConfig(
                "cmd",
                "10s",
                5,
                8080,
                "/health",
                "cord",
                100
            );

            HealthCheckConfig other = new HealthCheckConfig(
                null,
                null,
                0,  // Zero means "not set", should keep base value
                null,
                null,
                null,
                null
            );

            // Act
            HealthCheckConfig merged = base.deepMerge(other);

            // Assert
            assertThat(merged.maxAttempts()).isEqualTo(5);
        }

        @Test
        @DisplayName("should override non-zero max attempts")
        void shouldOverrideNonZeroMaxAttempts() {
            // Arrange
            HealthCheckConfig base = new HealthCheckConfig(
                "cmd",
                "10s",
                5,
                8080,
                "/health",
                "cord",
                100
            );

            HealthCheckConfig other = new HealthCheckConfig(
                null,
                null,
                10,  // Non-zero, should override
                null,
                null,
                null,
                null
            );

            // Act
            HealthCheckConfig merged = base.deepMerge(other);

            // Assert
            assertThat(merged.maxAttempts()).isEqualTo(10);
        }
    }
}
