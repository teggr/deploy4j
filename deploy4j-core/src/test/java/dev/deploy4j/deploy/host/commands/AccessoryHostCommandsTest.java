package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Accessory;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccessoryHostCommandsTest {

    private Configuration mockConfig;
    private Accessory mockAccessory;
    private AccessoryHostCommands accessoryCommands;

    @BeforeEach
    void setUp() {
        mockConfig = mock(Configuration.class);
        mockAccessory = mock(Accessory.class);
        
        when(mockConfig.accessory("redis")).thenReturn(mockAccessory);
        when(mockAccessory.serviceName()).thenReturn("redis-service");
        when(mockAccessory.image()).thenReturn("redis:7.0");
        when(mockAccessory.hosts()).thenReturn(List.of("host1"));
        when(mockAccessory.port()).thenReturn("6379");
        when(mockAccessory.cmd()).thenReturn(null);
        when(mockAccessory.publishArgs()).thenReturn(new String[]{});
        when(mockAccessory.envArgs()).thenReturn(List.of());
        when(mockAccessory.volumeArgs()).thenReturn(new String[]{});
        when(mockAccessory.labelArgs()).thenReturn(new String[]{});
        when(mockAccessory.optionArgs()).thenReturn(List.of());
        when(mockConfig.loggingArgs()).thenReturn(new String[]{});
        
        accessoryCommands = new AccessoryHostCommands(mockConfig, "redis");
    }

    @Test
    void shouldGenerateRunCommand() {
        Cmd cmd = accessoryCommands.run("redis-host");
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("run");
        assertThat(String.join(" ", cmd.build())).contains("redis-service");
        assertThat(cmd.description()).isEqualTo("Run accessory");
    }

    @Test
    void shouldGenerateStartCommand() {
        Cmd cmd = accessoryCommands.start();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("start");
        assertThat(String.join(" ", cmd.build())).contains("redis-service");
    }

    @Test
    void shouldGenerateStopCommand() {
        Cmd cmd = accessoryCommands.stop();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("stop");
        assertThat(String.join(" ", cmd.build())).contains("redis-service");
    }

    @Test
    void shouldGenerateInfoCommand() {
        Cmd cmd = accessoryCommands.info(false, false);
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("ps");
    }

    @Test
    void shouldGenerateLogsCommand() {
        Cmd cmd = accessoryCommands.logs(true, null,null, null, null);
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("logs");
        assertThat(String.join(" ", cmd.build())).contains("redis-service");
    }

    @Test
    void shouldGenerateLogsCommandWithParameters() {
        Cmd cmd = accessoryCommands.logs(true, "5m", "50", "ERROR", "-i");
        
        assertThat(cmd).isNotNull();
        assertThat(String.join(" ", cmd.build())).contains("logs");
        assertThat(String.join(" ", cmd.build())).contains("5m");
        assertThat(String.join(" ", cmd.build())).contains("50");
    }

    @Test
    void shouldGenerateExecuteInExistingContainerCommand() {
        Cmd cmd = accessoryCommands.executeInExistingContainer("redis-cli");
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("exec");
        assertThat(String.join(" ", cmd.build())).contains("redis-service");
        assertThat(String.join(" ", cmd.build())).contains("redis-cli");
    }

    @Test
    void shouldGenerateRemoveServiceDirectoryCommand() {
        Cmd cmd = accessoryCommands.removeServiceDirectory();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("rm");
        assertThat(cmd.build()).contains("-rf");
        assertThat(cmd.build()).contains("redis-service");
    }

    @Test
    void shouldGenerateRemoveContainerCommand() {
        Cmd cmd = accessoryCommands.removeContainer();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("prune");
    }

    @Test
    void shouldGenerateRemoveImageCommand() {
        Cmd cmd = accessoryCommands.removeImage();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("rm");
        assertThat(String.join(" ", cmd.build())).contains("redis:7.0");
    }

    @Test
    void shouldThrowExceptionWhenLocalFileMissing() {
        assertThatThrownBy(() -> accessoryCommands.ensureLocalFilePresent("/non/existent/file.txt"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Missing file");
    }

    @Test
    void shouldDelegateToAccessoryConfig() {
        assertThat(accessoryCommands.serviceName()).isEqualTo("redis-service");
        assertThat(accessoryCommands.image()).isEqualTo("redis:7.0");
        assertThat(accessoryCommands.hosts()).containsExactly("host1");
        assertThat(accessoryCommands.port()).isEqualTo("6379");
    }

    @Test
    void shouldHandleNullCmd() {
        when(mockAccessory.cmd()).thenReturn(null);
        
        Cmd cmd = accessoryCommands.run("redis-host");
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
    }

    @Test
    void shouldHandleNonNullCmd() {
        when(mockAccessory.cmd()).thenReturn("redis-server --appendonly yes");
        
        Cmd cmd = accessoryCommands.run("redis-host");
        
        assertThat(cmd).isNotNull();
        assertThat(String.join(" ", cmd.build())).contains("redis-server --appendonly yes");
    }
}
