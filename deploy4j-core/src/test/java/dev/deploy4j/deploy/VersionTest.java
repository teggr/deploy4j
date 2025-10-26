package dev.deploy4j.deploy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Version")
class VersionTest {

    @Test
    @DisplayName("should have non-null version constant")
    void shouldHaveVersionConstant() {
        assertThat(Version.VERSION)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    @DisplayName("should print version to stdout")
    void shouldPrintVersion() {
        // Arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            Version version = new Version();
            
            // Act
            version.version();
            
            // Assert
            String output = outputStream.toString().trim();
            assertThat(output).isEqualTo(Version.VERSION);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("should have version string format")
    void shouldHaveProperVersionFormat() {
        // Version should be a non-empty string
        assertThat(Version.VERSION)
                .matches(".+"); // At least one character
    }
}
