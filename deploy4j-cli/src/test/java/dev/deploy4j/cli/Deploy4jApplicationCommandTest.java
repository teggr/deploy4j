package dev.deploy4j.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Deploy4jApplicationCommand")
class Deploy4jApplicationCommandTest {

    @Test
    @DisplayName("should display help when no command is provided")
    void shouldDisplayHelpWithNoCommand() throws Exception {
        // Arrange
        Deploy4jApplicationCommand command = new Deploy4jApplicationCommand();

        // Capture output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        try {
            // Act
            Integer exitCode = command.call();

            // Assert
            assertThat(exitCode).isEqualTo(0);
            System.out.flush();
            String output = baos.toString();
            assertThat(output).contains("Usage:");
            assertThat(output).contains("deploy4j");
        } finally {
            System.setOut(old);
        }
    }

    @Test
    @DisplayName("should have proper command annotation with subcommands")
    void shouldHaveProperCommandAnnotation() {
        // Arrange & Act
        CommandLine cmd = new CommandLine(new Deploy4jApplicationCommand());

        // Assert
        assertThat(cmd.getCommandName()).isEqualTo("deploy4j");
        assertThat(cmd.getSubcommands()).isNotEmpty();
        
        // Verify some key subcommands exist
        assertThat(cmd.getSubcommands()).containsKey("version");
        assertThat(cmd.getSubcommands()).containsKey("deploy");
        assertThat(cmd.getSubcommands()).containsKey("setup");
        assertThat(cmd.getSubcommands()).containsKey("init");
    }

    @Test
    @DisplayName("should execute help option")
    void shouldExecuteHelpOption() {
        // Arrange
        Deploy4jApplicationCommand command = new Deploy4jApplicationCommand();
        CommandLine cmd = new CommandLine(command);

        // Capture output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cmd.setOut(new java.io.PrintWriter(baos, true));

        // Act
        int exitCode = cmd.execute("--help");

        // Assert
        assertThat(exitCode).isEqualTo(0);
        String output = baos.toString();
        assertThat(output).contains("Usage:");
    }

    @Test
    @DisplayName("should execute version subcommand")
    void shouldExecuteVersionSubcommand() {
        // Arrange
        Deploy4jApplicationCommand command = new Deploy4jApplicationCommand();
        CommandLine cmd = new CommandLine(command);

        // Capture output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        try {
            // Act
            int exitCode = cmd.execute("version");

            // Assert
            assertThat(exitCode).isEqualTo(0);
        } finally {
            System.setOut(old);
        }
    }
}
