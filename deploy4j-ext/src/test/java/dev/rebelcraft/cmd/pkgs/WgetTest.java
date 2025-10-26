package dev.rebelcraft.cmd.pkgs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static dev.rebelcraft.cmd.pkgs.Wget.wget;

@DisplayName("Wget command wrapper")
class WgetTest {

    @Test
    @DisplayName("should create basic wget command")
    void shouldCreateBasicWgetCommand() {
        // Act
        Wget cmd = wget();

        // Assert
        assertThat(cmd.build()).containsExactly("wget");
    }

    @Test
    @DisplayName("should add URL to wget command")
    void shouldAddUrl() {
        // Act
        Wget cmd = wget().url("https://example.com/file.tar.gz");

        // Assert
        assertThat(cmd.build())
                .containsExactly("wget", "https://example.com/file.tar.gz");
    }

    @Test
    @DisplayName("should add options to wget command")
    void shouldAddOptions() {
        // Act
        Wget cmd = wget().options("-O", "-");

        // Assert
        assertThat(cmd.build())
                .containsExactly("wget", "-O", "-");
    }

    @Test
    @DisplayName("should add multiple options")
    void shouldAddMultipleOptions() {
        // Act
        Wget cmd = wget().options("-q", "--show-progress");

        // Assert
        assertThat(cmd.build())
                .containsExactly("wget", "-q", "--show-progress");
    }

    @Test
    @DisplayName("should chain options and URL")
    void shouldChainOptionsAndUrl() {
        // Act
        Wget cmd = wget()
                .options("-O", "/tmp/file.tar.gz")
                .url("https://example.com/download.tar.gz");

        // Assert
        assertThat(cmd.build())
                .containsExactly("wget", "-O", "/tmp/file.tar.gz", "https://example.com/download.tar.gz");
    }

    @Test
    @DisplayName("should support fluent interface")
    void shouldSupportFluentInterface() {
        // Act
        Wget cmd = wget()
                .options("-q")
                .options("--no-check-certificate")
                .url("https://secure.example.com/file.zip")
                .description("download file");

        // Assert
        assertThat(cmd.build())
                .containsExactly("wget", "-q", "--no-check-certificate", "https://secure.example.com/file.zip");
        assertThat(cmd.description()).isEqualTo("download file");
    }

    @Test
    @DisplayName("should handle output to stdout")
    void shouldHandleOutputToStdout() {
        // Act
        Wget cmd = wget()
                .options("-O", "-")
                .url("https://example.com/script.sh");

        // Assert
        assertThat(cmd.build())
                .contains("-O", "-")
                .contains("https://example.com/script.sh");
    }
}
