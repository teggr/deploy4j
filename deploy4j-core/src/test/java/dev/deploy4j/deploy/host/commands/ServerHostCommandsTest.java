package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServerHostCommandsTest {

    private Configuration mockConfig;
    private ServerHostCommands serverCommands;

    @BeforeEach
    void setUp() {
        mockConfig = mock(Configuration.class);
        when(mockConfig.runDirectory()).thenReturn("/opt/deploy4j");
        
        serverCommands = new ServerHostCommands(mockConfig);
    }

    @Test
    void shouldGenerateEnsureRunDirectoryCommand() {
        Cmd cmd = serverCommands.ensureRunDirectory();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("mkdir");
        assertThat(cmd.build()).contains("-p");
        assertThat(cmd.build()).contains("/opt/deploy4j");
        assertThat(cmd.description()).isEqualTo("ensure run directory");
    }

    @Test
    void shouldHandleDifferentRunDirectory() {
        when(mockConfig.runDirectory()).thenReturn("/var/lib/app");
        ServerHostCommands commands = new ServerHostCommands(mockConfig);
        
        Cmd cmd = commands.ensureRunDirectory();
        
        assertThat(cmd.build()).contains("/var/lib/app");
    }

    @Test
    void shouldHandleEmptyRunDirectory() {
        when(mockConfig.runDirectory()).thenReturn("");
        ServerHostCommands commands = new ServerHostCommands(mockConfig);
        
        Cmd cmd = commands.ensureRunDirectory();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("mkdir");
    }
}
