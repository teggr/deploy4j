package dev.deploy4j.deploy.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RandomHex utility")
class RandomHexTest {

    @Test
    @DisplayName("should generate hex string with correct number of characters")
    void shouldGenerateCorrectLength() {
        String hex = RandomHex.randomHex(8);
        assertThat(hex).hasSize(16); // 8 bytes = 16 hex characters
    }

    @Test
    @DisplayName("should generate different values on subsequent calls")
    void shouldGenerateDifferentValues() {
        String hex1 = RandomHex.randomHex(16);
        String hex2 = RandomHex.randomHex(16);

        assertThat(hex1).isNotEqualTo(hex2);
    }

    @Test
    @DisplayName("should only contain valid hexadecimal characters")
    void shouldContainOnlyHexCharacters() {
        String hex = RandomHex.randomHex(32);

        assertThat(hex)
                .matches("^[0-9a-f]+$")
                .hasSize(64);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 8, 16, 32, 64})
    @DisplayName("should work with various byte lengths")
    void shouldWorkWithVariousByteLengths(int byteLength) {
        String hex = RandomHex.randomHex(byteLength);
        assertThat(hex).hasSize(byteLength * 2);
    }

    @Test
    @DisplayName("should handle zero length")
    void shouldHandleZeroLength() {
        String hex = RandomHex.randomHex(0);
        assertThat(hex).isEmpty();
    }
}
