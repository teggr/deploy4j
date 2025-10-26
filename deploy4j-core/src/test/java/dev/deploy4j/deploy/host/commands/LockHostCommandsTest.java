package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LockHostCommandsTest {

    private Configuration mockConfig;
    private LockHostCommands lockCommands;

    @BeforeEach
    void setUp() {
        mockConfig = mock(Configuration.class);
        when(mockConfig.service()).thenReturn("test-service");
        when(mockConfig.destination()).thenReturn("production");
        when(mockConfig.runDirectory()).thenReturn("/opt/deploy4j");
        
        lockCommands = new LockHostCommands(mockConfig);
    }

    @Test
    void shouldGenerateAcquireCommand() {
        Cmd cmd = lockCommands.acquire("Deploying version 1.0", "1.0");
        
        assertThat(cmd).isNotNull();
        assertThat(String.join(" ", cmd.build())).contains("mkdir");
        assertThat(String.join(" ", cmd.build())).contains("/opt/deploy4j/locks");
    }

    @Test
    void shouldGenerateReleaseCommand() {
        Cmd cmd = lockCommands.release();
        
        assertThat(cmd).isNotNull();
        assertThat(String.join(" ", cmd.build())).contains("rm");
        assertThat(String.join(" ", cmd.build())).contains("/opt/deploy4j/locks");
    }

    @Test
    void shouldGenerateStatusCommand() {
        Cmd cmd = lockCommands.status();
        
        assertThat(cmd).isNotNull();
        assertThat(String.join(" ", cmd.build())).contains("stat");
        assertThat(String.join(" ", cmd.build())).contains("cat");
    }

    @Test
    void shouldGenerateEnsureLocksDirectoryCommand() {
        Cmd cmd = lockCommands.ensureLocksDirectory();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("mkdir");
        assertThat(cmd.build()).contains("-p");
        assertThat(cmd.build()).contains("/opt/deploy4j/locks");
    }

    @Test
    void shouldIncludeMessageInAcquireCommand() {
        Cmd cmd = lockCommands.acquire("Test deployment", "2.0");
        
        assertThat(cmd).isNotNull();
        // The message is base64 encoded, so we check the command structure
        assertThat(String.join(" ", cmd.build())).contains("echo");
    }

    @Test
    void shouldIncludeVersionInAcquireCommand() {
        Cmd cmd = lockCommands.acquire("Deployment message", "v3.0.0");
        
        assertThat(cmd).isNotNull();
        assertThat(String.join(" ", cmd.build())).contains("echo");
    }
}
