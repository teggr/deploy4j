package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Env;
import dev.deploy4j.deploy.configuration.Gateway;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GatewayHostCommandsTest {

    private Configuration mockConfig;
    private Gateway mockGateway;
    private Env mockEnv;
    private GatewayHostCommands gatewayCommands;

    @BeforeEach
    void setUp() {
        mockConfig = mock(Configuration.class);
        mockGateway = mock(Gateway.class);
        mockEnv = mock(Env.class);
        
        when(mockConfig.gateway()).thenReturn(mockGateway);
        when(mockGateway.port()).thenReturn("80:80");
        when(mockGateway.publish()).thenReturn(true);
        when(mockGateway.image()).thenReturn("gateway:v2.10");
        when(mockGateway.labels()).thenReturn(Map.of());
        when(mockGateway.env()).thenReturn(mockEnv);
        when(mockGateway.options()).thenReturn(Map.of());
        when(mockGateway.args()).thenReturn(Map.of());
        when(mockEnv.clearArgs()).thenReturn(List.of());
        when(mockConfig.loggingArgs()).thenReturn(new String[]{});
        
        gatewayCommands = new GatewayHostCommands(mockConfig);
    }

    @Test
    void shouldGenerateRunCommand() {
        Cmd cmd = gatewayCommands.run();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("run");
        assertThat(String.join(" ", cmd.build())).contains("gateway");
        assertThat(cmd.description()).isEqualTo("run gateway");
    }

    @Test
    void shouldGenerateStartCommand() {
        Cmd cmd = gatewayCommands.start();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("start");
        assertThat(String.join(" ", cmd.build())).contains("gateway");
        assertThat(cmd.description()).isEqualTo("start gateway");
    }

    @Test
    void shouldGenerateStopCommand() {
        Cmd cmd = gatewayCommands.stop();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("stop");
        assertThat(String.join(" ", cmd.build())).contains("gateway");
        assertThat(cmd.description()).isEqualTo("stop gateway");
    }

    @Test
    void shouldGenerateStartOrRunCommand() {
        Cmd cmd = gatewayCommands.startOrRun();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.description()).isEqualTo("start or run");
    }

    @Test
    void shouldGenerateInfoCommand() {
        Cmd cmd = gatewayCommands.info();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("ps");
        assertThat(String.join(" ", cmd.build())).contains("gateway");
        assertThat(cmd.description()).isEqualTo("info");
    }

    @Test
    void shouldGenerateLogsCommand() {
        Cmd cmd = gatewayCommands.logs(null, null, null, null);
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("logs");
        assertThat(cmd.description()).isEqualTo("logs");
    }

    @Test
    void shouldGenerateLogsCommandWithParameters() {
        Cmd cmd = gatewayCommands.logs("10m", "100", "error", "-i");
        
        assertThat(cmd).isNotNull();
        assertThat(String.join(" ", cmd.build())).contains("logs");
        assertThat(String.join(" ", cmd.build())).contains("10m");
        assertThat(String.join(" ", cmd.build())).contains("100");
    }

    @Test
    void shouldThrowUnsupportedOperationForFollowLogs() {
        assertThatThrownBy(() -> gatewayCommands.followLogs())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldGenerateRemoveContainerCommand() {
        Cmd cmd = gatewayCommands.removeContainer();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("prune");
        assertThat(cmd.description()).isEqualTo("remove gateway");
    }

    @Test
    void shouldGenerateRemoveImageCommand() {
        Cmd cmd = gatewayCommands.removeImage();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("prune");
        assertThat(cmd.description()).isEqualTo("remove gateway image");
    }

//    @Test
//    void shouldGenerateMakeEnvDirectoryCommand() {
//
//        Cmd cmd = gatewayCommands.ensureEnvDirectory();
//
//        assertThat(cmd).isNotNull();
//        assertThat(cmd.build()).contains("mkdir");
//        assertThat(cmd.build()).contains("/opt/gateway/env");
//    }

    @Test
    void shouldDelegateToGatewayConfig() {
        assertThat(gatewayCommands.port()).isEqualTo("80:80");
        assertThat(gatewayCommands.publish()).isTrue();
        assertThat(gatewayCommands.image()).isEqualTo("gateway:v2.10");
        assertThat(gatewayCommands.env()).isEqualTo(mockEnv);
    }
}
