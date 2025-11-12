package dev.deploy4j.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VersionCliCommand")
class VersionCliCommandTest {

    @Test
    @DisplayName("should execute and return success exit code")
    void shouldExecuteSuccessfully() throws Exception {
        // Arrange
        VersionCliCommand command = new VersionCliCommand();

        // Act
        Integer exitCode = command.call();

        // Assert
        assertThat(exitCode).isEqualTo(0);
    }

    @Test
    @DisplayName("should execute via CommandLine")
    void shouldExecuteViaCommandLine() {
        // Arrange
        VersionCliCommand command = new VersionCliCommand();
        CommandLine cmd = new CommandLine(command);

        // Capture output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        try {
            // Act
            int exitCode = cmd.execute();

            // Assert
            assertThat(exitCode).isEqualTo(0);
            System.out.flush();
            String output = baos.toString();
            // Version output should contain something (not empty)
            assertThat(output).isNotEmpty();
        } finally {
            System.setOut(old);
        }
    }

    @Test
    @DisplayName("should have proper command annotation")
    void shouldHaveProperCommandAnnotation() {
        // Arrange & Act
        CommandLine cmd = new CommandLine(new VersionCliCommand());

        // Assert
        assertThat(cmd.getCommandName()).isEqualTo("version");
    }
}
