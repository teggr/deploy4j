package dev.deploy4j.deploy.host.commands;

import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BaseHostCommands")
class BaseHostCommandsTest {

    // Concrete implementation for testing
    static class TestHostCommands extends BaseHostCommands {
        public TestHostCommands() {
            super(null);
        }
    }

    private TestHostCommands hostCommands;

    @BeforeEach
    void setUp() {
        hostCommands = new TestHostCommands();
    }

    @Test
    @DisplayName("should create container ID command for running containers")
    void shouldCreateContainerIdCommandForRunning() {
        // Act
        Cmd cmd = hostCommands.containerIdFor("my-app", true);

        // Assert
        assertThat(cmd.build())
                .contains("docker", "container", "ls")
                .contains("--filter", "name=^my-app$")
                .contains("--quiet")
                .doesNotContain("--all");
        assertThat(cmd.description()).isEqualTo("container id for");
    }

    @Test
    @DisplayName("should create container ID command for all containers")
    void shouldCreateContainerIdCommandForAll() {
        // Act
        Cmd cmd = hostCommands.containerIdFor("my-app", false);

        // Assert
        assertThat(cmd.build())
                .contains("docker", "container", "ls")
                .contains("--all")
                .contains("--filter", "name=^my-app$")
                .contains("--quiet");
    }

    @Test
    @DisplayName("should create makeDirectoryFor command")
    void shouldCreateMakeDirectoryForCommand() {
        // Act
        Cmd cmd = hostCommands.makeDirectoryFor("/path/to/file.txt");

        // Assert
        assertThat(cmd.build())
                .containsExactly("mkdir", "-p", "/path/to");
    }

    @Test
    @DisplayName("should create makeDirectory command")
    void shouldCreateMakeDirectoryCommand() {
        // Act
        Cmd cmd = hostCommands.makeDirectory("/path/to/dir");

        // Assert
        assertThat(cmd.build())
                .containsExactly("mkdir", "-p", "/path/to/dir");
    }

    @Test
    @DisplayName("should create removeDirectory command")
    void shouldCreateRemoveDirectoryCommand() {
        // Act
        Cmd cmd = hostCommands.removeDirectory("/path/to/dir");

        // Assert
        assertThat(cmd.build())
                .containsExactly("rm", "-r", "/path/to/dir");
    }

    @Test
    @DisplayName("should create git command without path")
    void shouldCreateGitCommandWithoutPath() {
        // Arrange
        String[] args = {"status"};

        // Act
        Cmd cmd = hostCommands.git(args, null);

        // Assert
        assertThat(cmd.build())
                .containsExactly("git", "status");
    }

    @Test
    @DisplayName("should create git command with path")
    void shouldCreateGitCommandWithPath() {
        // Arrange
        String[] args = {"status"};

        // Act
        Cmd cmd = hostCommands.git(args, "/repo/path");

        // Assert
        assertThat(cmd.build())
                .containsExactly("git", "-C", "/repo/path", "status");
    }

    @Test
    @DisplayName("should return sensitive string unchanged")
    void shouldReturnSensitiveStringUnchanged() {
        // Arrange
        String secret = "my-secret-key";

        // Act
        String result = hostCommands.sensitive(secret);

        // Assert
        assertThat(result).isEqualTo(secret);
    }
}
