package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PruneHostCommandsTest {

    private Configuration mockConfig;
    private PruneHostCommands pruneCommands;

    @BeforeEach
    void setUp() {
        mockConfig = mock(Configuration.class);
        when(mockConfig.service()).thenReturn("test-service");
        when(mockConfig.healthcheckService()).thenReturn("test-service-healthcheck");
        when(mockConfig.repository()).thenReturn("myrepo/test-service");
        when(mockConfig.latestImage()).thenReturn("myrepo/test-service:latest");
        
        pruneCommands = new PruneHostCommands(mockConfig);
    }

    @Test
    void shouldGenerateDanglingImagesCommand() {
        Cmd cmd = pruneCommands.danglingImages();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("prune");
        assertThat(String.join(" ", cmd.build())).contains("label=service=test-service");
    }

    @Test
    void shouldGenerateTaggedImagesCommand() {
        Cmd cmd = pruneCommands.taggedImages();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).isNotEmpty();
        assertThat(String.join(" ", cmd.build())).contains("docker");
    }

    @Test
    void shouldGenerateAppContainersCommand() {
        Cmd cmd = pruneCommands.appContainers(5);
        
        assertThat(cmd).isNotNull();
        assertThat(String.join(" ", cmd.build())).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("ps");
        assertThat(String.join(" ", cmd.build())).contains("tail");
    }

    @Test
    void shouldGenerateHealthcheckContainersCommand() {
        Cmd cmd = pruneCommands.healthcheckContainers();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("prune");
        assertThat(String.join(" ", cmd.build())).contains("label=service=test-service-healthcheck");
    }

    @Test
    void shouldHandleDifferentRetainCount() {
        Cmd cmd = pruneCommands.appContainers(10);
        
        assertThat(String.join(" ", cmd.build())).contains("+11"); // retain + 1
    }

    @Test
    void shouldHandleZeroRetainCount() {
        Cmd cmd = pruneCommands.appContainers(0);
        
        assertThat(String.join(" ", cmd.build())).contains("+1");
    }
}
