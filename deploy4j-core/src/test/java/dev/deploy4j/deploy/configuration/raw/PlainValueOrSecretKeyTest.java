package dev.deploy4j.deploy.configuration.raw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PlainValueOrSecretKey")
class PlainValueOrSecretKeyTest {

    @Test
    @DisplayName("should create plain value from string")
    void shouldCreatePlainValueFromString() {
        // Arrange & Act
        PlainValueOrSecretKey plainValue = new PlainValueOrSecretKey("mypassword");

        // Assert
        assertThat(plainValue.isKey()).isFalse();
        assertThat(plainValue.value()).isEqualTo("mypassword");
        assertThat(plainValue.key()).isNull();
    }

    @Test
    @DisplayName("should create secret key from list")
    void shouldCreateSecretKeyFromList() {
        // Arrange & Act
        PlainValueOrSecretKey secretKey = new PlainValueOrSecretKey(List.of("SECRET_", "PASSWORD"));

        // Assert
        assertThat(secretKey.isKey()).isTrue();
        assertThat(secretKey.key()).isEqualTo("SECRET_PASSWORD");
        assertThat(secretKey.value()).isNull();
    }

    @Test
    @DisplayName("should handle single element list")
    void shouldHandleSingleElementList() {
        // Arrange & Act
        PlainValueOrSecretKey secretKey = new PlainValueOrSecretKey(List.of("SECRET_KEY"));

        // Assert
        assertThat(secretKey.isKey()).isTrue();
        assertThat(secretKey.key()).isEqualTo("SECRET_KEY");
        assertThat(secretKey.value()).isNull();
    }

    @Test
    @DisplayName("should handle empty list")
    void shouldHandleEmptyList() {
        // Arrange & Act
        PlainValueOrSecretKey secretKey = new PlainValueOrSecretKey(List.of());

        // Assert
        assertThat(secretKey.isKey()).isTrue();
        assertThat(secretKey.key()).isEmpty();
        assertThat(secretKey.value()).isNull();
    }

    @Test
    @DisplayName("should handle null input")
    void shouldHandleNullInput() {
        // Arrange & Act
        PlainValueOrSecretKey plainValue = new PlainValueOrSecretKey(null);

        // Assert
        assertThat(plainValue.isKey()).isFalse();
        assertThat(plainValue.key()).isNull();
        assertThat(plainValue.value()).isNull();
    }

    @Test
    @DisplayName("should handle empty string")
    void shouldHandleEmptyString() {
        // Arrange & Act
        PlainValueOrSecretKey plainValue = new PlainValueOrSecretKey("");

        // Assert
        assertThat(plainValue.isKey()).isFalse();
        assertThat(plainValue.value()).isEmpty();
        assertThat(plainValue.key()).isNull();
    }
}
