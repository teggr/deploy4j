package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Env;
import dev.deploy4j.deploy.configuration.SpringBootAdmin;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpringBootAdminHostCommandsTest {

    private Configuration mockConfig;
    private SpringBootAdmin mockSpringBootAdmin;
    private Env mockEnv;
    private SpringBootAdminHostCommands springBootAdminCommands;

    @BeforeEach
    void setUp() {
        mockConfig = mock(Configuration.class);
        mockSpringBootAdmin = mock(SpringBootAdmin.class);
        mockEnv = mock(Env.class);
        
        when(mockConfig.springBootAdmin()).thenReturn(mockSpringBootAdmin);
        when(mockSpringBootAdmin.port()).thenReturn("8080:8080");
        when(mockSpringBootAdmin.publish()).thenReturn(true);
        when(mockSpringBootAdmin.image()).thenReturn("teggr/deploy4j-spring-boot-admin:latest");
        when(mockSpringBootAdmin.labels()).thenReturn(Map.of());
        when(mockSpringBootAdmin.env()).thenReturn(mockEnv);
        when(mockSpringBootAdmin.options()).thenReturn(Map.of());
        when(mockSpringBootAdmin.args()).thenReturn(Map.of());
        when(mockEnv.clearArgs()).thenReturn(List.of());
        when(mockConfig.loggingArgs()).thenReturn(new String[]{});
        
        springBootAdminCommands = new SpringBootAdminHostCommands(mockConfig);
    }

    @Test
    void shouldGenerateRunCommand() {
        Cmd cmd = springBootAdminCommands.run();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("run");
        assertThat(String.join(" ", cmd.build())).contains("spring-boot-admin");
        assertThat(cmd.description()).isEqualTo("run spring-boot-admin");
    }

    @Test
    void shouldGenerateStartCommand() {
        Cmd cmd = springBootAdminCommands.start();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("start");
        assertThat(String.join(" ", cmd.build())).contains("spring-boot-admin");
        assertThat(cmd.description()).isEqualTo("start spring-boot-admin");
    }

    @Test
    void shouldGenerateStopCommand() {
        Cmd cmd = springBootAdminCommands.stop();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("stop");
        assertThat(String.join(" ", cmd.build())).contains("spring-boot-admin");
        assertThat(cmd.description()).isEqualTo("stop spring-boot-admin");
    }

    @Test
    void shouldGenerateStartOrRunCommand() {
        Cmd cmd = springBootAdminCommands.startOrRun();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.description()).isEqualTo("start or run");
    }

    @Test
    void shouldGenerateInfoCommand() {
        Cmd cmd = springBootAdminCommands.info();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("ps");
        assertThat(String.join(" ", cmd.build())).contains("spring-boot-admin");
        assertThat(cmd.description()).isEqualTo("info");
    }

    @Test
    void shouldGenerateLogsCommand() {
        Cmd cmd = springBootAdminCommands.logs(null, null, null, null);
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("logs");
        assertThat(cmd.description()).isEqualTo("logs");
    }

    @Test
    void shouldGenerateLogsCommandWithParameters() {
        Cmd cmd = springBootAdminCommands.logs("10m", "100", "error", "-i");
        
        assertThat(cmd).isNotNull();
        assertThat(String.join(" ", cmd.build())).contains("logs");
        assertThat(String.join(" ", cmd.build())).contains("10m");
        assertThat(String.join(" ", cmd.build())).contains("100");
    }

    @Test
    void shouldThrowUnsupportedOperationForFollowLogs() {
        assertThatThrownBy(() -> springBootAdminCommands.followLogs())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldGenerateRemoveContainerCommand() {
        Cmd cmd = springBootAdminCommands.removeContainer();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("prune");
        assertThat(cmd.description()).isEqualTo("remove spring-boot-admin");
    }

    @Test
    void shouldGenerateRemoveImageCommand() {
        Cmd cmd = springBootAdminCommands.removeImage();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("prune");
        assertThat(cmd.description()).isEqualTo("remove spring-boot-admin image");
    }

    @Test
    void shouldDelegateToSpringBootAdminConfig() {
        assertThat(springBootAdminCommands.port()).isEqualTo("8080:8080");
        assertThat(springBootAdminCommands.publish()).isTrue();
        assertThat(springBootAdminCommands.image()).isEqualTo("teggr/deploy4j-spring-boot-admin:latest");
        assertThat(springBootAdminCommands.env()).isEqualTo(mockEnv);
    }
}
