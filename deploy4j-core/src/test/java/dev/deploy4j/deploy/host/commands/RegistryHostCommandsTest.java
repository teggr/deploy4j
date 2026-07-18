package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Registry;
import dev.rebelcraft.cmd.Cmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegistryHostCommandsTest {

    private Configuration mockConfig;
    private Registry mockRegistry;
    private RegistryHostCommands registryCommands;

    @BeforeEach
    void setUp() {
        mockConfig = mock(Configuration.class);
        mockRegistry = mock(Registry.class);
        
        when(mockConfig.registry()).thenReturn(mockRegistry);
        when(mockRegistry.server()).thenReturn("docker.io");
        when(mockRegistry.username()).thenReturn("testuser");
        when(mockRegistry.password()).thenReturn("testpass");
        when(mockRegistry.credentialsConfigured()).thenReturn(true);
        
        registryCommands = new RegistryHostCommands(mockConfig);
    }

    @Test
    void shouldGenerateLoginCommand() {
        Cmd cmd = registryCommands.login();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("login");
        assertThat(String.join(" ", cmd.build())).contains("docker.io");
        assertThat(cmd.description()).isEqualTo("login");
    }

    @Test
    void shouldGenerateLogoutCommand() {
        Cmd cmd = registryCommands.logout();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
        assertThat(String.join(" ", cmd.build())).contains("logout");
        assertThat(String.join(" ", cmd.build())).contains("docker.io");
        assertThat(cmd.description()).isEqualTo("logout");
    }

    @Test
    void shouldDelegateToRegistry() {
        Registry registry = registryCommands.registry();
        
        assertThat(registry).isEqualTo(mockRegistry);
        assertThat(registry.server()).isEqualTo("docker.io");
        assertThat(registry.username()).isEqualTo("testuser");
        assertThat(registry.password()).isEqualTo("testpass");
    }

    @Test
    void shouldHandleNullServer() {
        when(mockRegistry.server()).thenReturn(null);
        
        Cmd cmd = registryCommands.login();
        
        assertThat(cmd).isNotNull();
        assertThat(cmd.build()).contains("docker");
    }

    @Test
    void shouldSkipLoginWhenCredentialsAreMissing() {
        when(mockRegistry.credentialsConfigured()).thenReturn(false);

        Cmd cmd = registryCommands.login();

        assertThat(cmd.build()).containsExactly("true");
        assertThat(cmd.description()).isEqualTo("skip login");
    }

    @Test
    void shouldSkipLogoutWhenCredentialsAreMissing() {
        when(mockRegistry.credentialsConfigured()).thenReturn(false);

        Cmd cmd = registryCommands.logout();

        assertThat(cmd.build()).containsExactly("true");
        assertThat(cmd.description()).isEqualTo("skip logout");
    }
}
