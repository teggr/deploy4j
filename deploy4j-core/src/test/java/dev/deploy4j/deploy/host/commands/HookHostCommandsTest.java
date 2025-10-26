package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HookHostCommandsTest {

    private Configuration mockConfig;
    private HookHostCommands hookCommands;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        mockConfig = mock(Configuration.class);
        when(mockConfig.hooksPath()).thenReturn(tempDir.toString());
        
        hookCommands = new HookHostCommands(mockConfig);
    }

    @Test
    void shouldGenerateRunCommand() {
        Map<String, String> details = Map.of("version", "1.0", "environment", "prod");
        
        List<?> result = hookCommands.run("pre-deploy", details);
        
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
    }

    @Test
    void shouldCheckIfHookExistsWhenFileDoesNotExist() {
        boolean exists = hookCommands.hookExists("non-existent-hook");
        
        assertThat(exists).isFalse();
    }

    @Test
    void shouldCheckIfHookExistsWhenFileExists() throws IOException {
        Path hookFile = tempDir.resolve("my-hook");
        Files.createFile(hookFile);
        
        boolean exists = hookCommands.hookExists("my-hook");
        
        assertThat(exists).isTrue();
    }

    @Test
    void shouldGenerateHookFilePath() {
        String hookFile = hookCommands.hookFile("post-deploy");
        
        assertThat(hookFile).contains("post-deploy");
        assertThat(hookFile).startsWith(tempDir.toString());
    }

    @Test
    void shouldHandleEmptyDetails() {
        Map<String, String> emptyDetails = Map.of();
        
        List<?> result = hookCommands.run("hook", emptyDetails);
        
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
    }

    @Test
    void shouldHandleDifferentHookNames() {
        String hookFile1 = hookCommands.hookFile("pre-deploy");
        String hookFile2 = hookCommands.hookFile("post-deploy");
        
        assertThat(hookFile1).isNotEqualTo(hookFile2);
        assertThat(hookFile1).contains("pre-deploy");
        assertThat(hookFile2).contains("post-deploy");
    }
}
