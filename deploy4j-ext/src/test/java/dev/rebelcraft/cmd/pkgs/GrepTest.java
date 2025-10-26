package dev.rebelcraft.cmd.pkgs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static dev.rebelcraft.cmd.pkgs.Grep.grep;

@DisplayName("Grep command wrapper")
class GrepTest {

    @Test
    @DisplayName("should create basic grep command")
    void shouldCreateBasicGrepCommand() {
        // Act
        Grep cmd = grep();

        // Assert
        assertThat(cmd.build()).containsExactly("grep");
    }

    @Test
    @DisplayName("should add search pattern to grep command")
    void shouldAddSearchPattern() {
        // Act
        Grep cmd = grep().search("pattern");

        // Assert
        assertThat(cmd.build())
                .containsExactly("grep", "'pattern'");
    }

    @Test
    @DisplayName("should quote search pattern in grep command")
    void shouldQuoteSearchPattern() {
        // Act
        Grep cmd = grep().search("error");

        // Assert
        assertThat(cmd.build().get(1))
                .startsWith("'")
                .endsWith("'");
    }

    @Test
    @DisplayName("should handle regex pattern")
    void shouldHandleRegexPattern() {
        // Act
        Grep cmd = grep().search("^ERROR.*");

        // Assert
        assertThat(cmd.build())
                .containsExactly("grep", "'^ERROR.*'");
    }

    @Test
    @DisplayName("should support fluent interface with args")
    void shouldSupportFluentInterface() {
        // Act
        Grep cmd = grep()
                .args("-v")
                .search("exclude")
                .description("filter output");

        // Assert
        assertThat(cmd.build())
                .containsExactly("grep", "-v", "'exclude'");
        assertThat(cmd.description()).isEqualTo("filter output");
    }

    @Test
    @DisplayName("should handle multiple grep options")
    void shouldHandleMultipleOptions() {
        // Act
        Grep cmd = grep()
                .args("-i")
                .args("-n")
                .search("search term");

        // Assert
        assertThat(cmd.build())
                .containsExactly("grep", "-i", "-n", "'search term'");
    }
}
