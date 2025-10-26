package dev.rebelcraft.cmd.pkgs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static dev.rebelcraft.cmd.pkgs.Echo.echo;

@DisplayName("Echo command wrapper")
class EchoTest {

    @Test
    @DisplayName("should create basic echo command")
    void shouldCreateBasicEchoCommand() {
        // Act
        Echo cmd = echo();

        // Assert
        assertThat(cmd.build()).containsExactly("echo");
    }

    @Test
    @DisplayName("should add message to echo command")
    void shouldAddMessage() {
        // Act
        Echo cmd = echo().message("hello world");

        // Assert
        assertThat(cmd.build())
                .containsExactly("echo", "\"hello world\"");
    }

    @Test
    @DisplayName("should quote message in echo command")
    void shouldQuoteMessage() {
        // Act
        Echo cmd = echo().message("test message");

        // Assert
        assertThat(cmd.build().get(1))
                .startsWith("\"")
                .endsWith("\"");
    }

    @Test
    @DisplayName("should handle empty message")
    void shouldHandleEmptyMessage() {
        // Act
        Echo cmd = echo().message("");

        // Assert
        assertThat(cmd.build())
                .containsExactly("echo", "\"\"");
    }

    @Test
    @DisplayName("should support fluent interface with description")
    void shouldSupportFluentInterface() {
        // Act
        Echo cmd = echo()
                .message("deployment started")
                .description("log message");

        // Assert
        assertThat(cmd.build())
                .containsExactly("echo", "\"deployment started\"");
        assertThat(cmd.description()).isEqualTo("log message");
    }
}
