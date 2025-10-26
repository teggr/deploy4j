package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Env;
import dev.deploy4j.deploy.configuration.Traefik;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TraefikHostCommandsTest {

    private Configuration mockConfig;
    private Traefik mockTraefik;
    private Env mockEnv;
    private TraefikHostCommands traefikCommands;

    @BeforeEach
    void setUp() {
        mockConfig = mock(Configuration.class);
        mockTraefik = mock(Traefik.class);
        mockEnv = mock(Env.class);
        
        when(mockConfig.traefik()).thenReturn(mockTraefik);
        when(mockTraefik.port()).thenReturn("80:80");
        when(mockTraefik.publish()).thenReturn(true);
        when(mockTraefik.image()).thenReturn("traefik:v2.10");
        when(mockTraefik.labels()).thenReturn(Map.of());
        when(mockTraefik.env()).thenReturn(mockEnv);
        when(mockTraefik.options()).thenReturn(Map.of());
        when(mockTraefik.args()).thenReturn(Map.of());
        when(mockEnv.args()).thenReturn(List.of());
        when(mockConfig.loggingArgs()).thenReturn(new String[]{});
        
        traefikCommands = new TraefikHostCommands(mockConfig);
    }

    @Test
    void shouldGenerateRunCommand() {
        Cmd cmd = traefikCommands.run();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("run");
        assertThat(String.join(" ", cmd.build())).contains("traefik");
        assertThat(cmd.description()).isEqualTo("run traefik");
    }

    @Test
    void shouldGenerateStartCommand() {
        Cmd cmd = traefikCommands.start();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("start");
        assertThat(String.join(" ", cmd.build())).contains("traefik");
        assertThat(cmd.description()).isEqualTo("start traefik");
    }

    @Test
    void shouldGenerateStopCommand() {
        Cmd cmd = traefikCommands.stop();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("stop");
        assertThat(String.join(" ", cmd.build())).contains("traefik");
        assertThat(cmd.description()).isEqualTo("stop traefik");
    }

    @Test
    void shouldGenerateStartOrRunCommand() {
        Cmd cmd = traefikCommands.startOrRun();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.description()).isEqualTo("start or run");
    }

    @Test
    void shouldGenerateInfoCommand() {
        Cmd cmd = traefikCommands.info();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("ps");
        assertThat(String.join(" ", cmd.build())).contains("traefik");
        assertThat(cmd.description()).isEqualTo("info");
    }

    @Test
    void shouldGenerateLogsCommand() {
        Cmd cmd = traefikCommands.logs(null, null, null, null);
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("logs");
        assertThat(cmd.description()).isEqualTo("logs");
    }

    @Test
    void shouldGenerateLogsCommandWithParameters() {
        Cmd cmd = traefikCommands.logs("10m", "100", "error", "-i");
        
        assertThat(cmd).isNotNull();
        assertThat(String.join(" ", cmd.build())).contains("logs");
        assertThat(String.join(" ", cmd.build())).contains("10m");
        assertThat(String.join(" ", cmd.build())).contains("100");
    }

    @Test
    void shouldThrowUnsupportedOperationForFollowLogs() {
        assertThatThrownBy(() -> traefikCommands.followLogs())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldGenerateRemoveContainerCommand() {
        Cmd cmd = traefikCommands.removeContainer();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("prune");
        assertThat(cmd.description()).isEqualTo("remove traefik");
    }

    @Test
    void shouldGenerateRemoveImageCommand() {
        Cmd cmd = traefikCommands.removeImage();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("prune");
        assertThat(cmd.description()).isEqualTo("remove traefik image");
    }

    @Test
    void shouldGenerateMakeEnvDirectoryCommand() {
        when(mockEnv.secretsDirectory()).thenReturn("/opt/traefik/env");
        
        Cmd cmd = traefikCommands.makeEnvDirectory();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("mkdir");
        assertThat(cmd.build()).contains("/opt/traefik/env");
    }

    @Test
    void shouldGenerateRemoveEnvFileCommand() {
        when(mockEnv.secretsFile()).thenReturn("/opt/traefik/env/.env");
        
        Cmd cmd = traefikCommands.removeEnvFile();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("rm");
        assertThat(cmd.build()).contains("/opt/traefik/env/.env");
        assertThat(cmd.description()).isEqualTo("remove traefik env file");
    }

    @Test
    void shouldDelegateToTraefikConfig() {
        assertThat(traefikCommands.port()).isEqualTo("80:80");
        assertThat(traefikCommands.publish()).isTrue();
        assertThat(traefikCommands.image()).isEqualTo("traefik:v2.10");
        assertThat(traefikCommands.env()).isEqualTo(mockEnv);
    }
}
