package dev.rebelcraft.cmd.pkgs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static dev.rebelcraft.cmd.pkgs.Curl.curl;

@DisplayName("Curl command wrapper")
class CurlTest {

    @Test
    @DisplayName("should create basic curl command")
    void shouldCreateBasicCurlCommand() {
        // Act
        Curl cmd = curl();

        // Assert
        assertThat(cmd.build()).containsExactly("curl");
    }

    @Test
    @DisplayName("should add URL to curl command")
    void shouldAddUrl() {
        // Act
        Curl cmd = curl().url("https://example.com");

        // Assert
        assertThat(cmd.build())
                .containsExactly("curl", "https://example.com");
    }

    @Test
    @DisplayName("should add options to curl command")
    void shouldAddOptions() {
        // Act
        Curl cmd = curl().options("-fsSL");

        // Assert
        assertThat(cmd.build())
                .containsExactly("curl", "-fsSL");
    }

    @Test
    @DisplayName("should add multiple options to curl command")
    void shouldAddMultipleOptions() {
        // Act
        Curl cmd = curl().options("-s", "-S", "-L");

        // Assert
        assertThat(cmd.build())
                .containsExactly("curl", "-s", "-S", "-L");
    }

    @Test
    @DisplayName("should chain options and URL")
    void shouldChainOptionsAndUrl() {
        // Act
        Curl cmd = curl()
                .options("-fsSL")
                .url("https://example.com/file.tar.gz");

        // Assert
        assertThat(cmd.build())
                .containsExactly("curl", "-fsSL", "https://example.com/file.tar.gz");
    }

    @Test
    @DisplayName("should support fluent interface")
    void shouldSupportFluentInterface() {
        // Act
        Curl cmd = curl()
                .options("-s")
                .options("-L")
                .url("https://api.example.com")
                .description("fetch data");

        // Assert
        assertThat(cmd.build())
                .containsExactly("curl", "-s", "-L", "https://api.example.com");
        assertThat(cmd.description()).isEqualTo("fetch data");
    }
}
