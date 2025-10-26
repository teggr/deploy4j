package dev.rebelcraft.cmd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FluentCmd")
class FluentCmdTest {

    // Concrete implementation for testing
    static class TestFluentCmd extends FluentCmd<TestFluentCmd> {
        TestFluentCmd(String... base) {
            super(base);
        }

        TestFluentCmd(List<String> base) {
            super(base);
        }

        TestFluentCmd(Cmd cmd) {
            super(cmd);
        }
    }

    @Test
    @DisplayName("should create fluent command with base arguments")
    void shouldCreateWithBaseArguments() {
        // Act
        TestFluentCmd cmd = new TestFluentCmd("echo", "hello");

        // Assert
        assertThat(cmd.build())
                .containsExactly("echo", "hello");
    }

    @Test
    @DisplayName("should create fluent command from list")
    void shouldCreateFromList() {
        // Arrange
        List<String> base = List.of("ls", "-la");

        // Act
        TestFluentCmd cmd = new TestFluentCmd(base);

        // Assert
        assertThat(cmd.build())
                .containsExactly("ls", "-la");
    }

    @Test
    @DisplayName("should create fluent command from existing Cmd")
    void shouldCreateFromExistingCmd() {
        // Arrange
        Cmd baseCmd = Cmd.cmd("cat", "file.txt");

        // Act
        TestFluentCmd cmd = new TestFluentCmd(baseCmd);

        // Assert
        assertThat(cmd.build())
                .containsExactly("cat", "file.txt");
    }

    @Test
    @DisplayName("should chain args method and return self")
    void shouldChainArgsMethodWithVarargs() {
        // Act
        TestFluentCmd cmd = new TestFluentCmd("docker")
                .args("ps")
                .args("-a");

        // Assert
        assertThat(cmd.build())
                .containsExactly("docker", "ps", "-a");
    }

    @Test
    @DisplayName("should chain args method with list and return self")
    void shouldChainArgsMethodWithList() {
        // Arrange
        List<String> args = List.of("--format", "json");

        // Act
        TestFluentCmd cmd = new TestFluentCmd("docker", "ps")
                .args(args);

        // Assert
        assertThat(cmd.build())
                .containsExactly("docker", "ps", "--format", "json");
    }

    @Test
    @DisplayName("should set description and return self")
    void shouldSetDescriptionAndReturnSelf() {
        // Act
        TestFluentCmd cmd = new TestFluentCmd("echo", "test")
                .description("test command");

        // Assert
        assertThat(cmd.description()).isEqualTo("test command");
    }

    @Test
    @DisplayName("should support method chaining")
    void shouldSupportMethodChaining() {
        // Act
        TestFluentCmd cmd = new TestFluentCmd("docker")
                .args("container")
                .args("ls")
                .args("--all")
                .description("list all containers");

        // Assert
        assertThat(cmd.build())
                .containsExactly("docker", "container", "ls", "--all");
        assertThat(cmd.description()).isEqualTo("list all containers");
    }

    @Test
    @DisplayName("should maintain fluent interface across multiple operations")
    void shouldMaintainFluentInterface() {
        // Act
        TestFluentCmd cmd = new TestFluentCmd("git");
        cmd = cmd.args("log");
        cmd = cmd.args("--oneline");
        cmd = cmd.args("-n", "10");

        // Assert
        assertThat(cmd.build())
                .containsExactly("git", "log", "--oneline", "-n", "10");
    }
}
