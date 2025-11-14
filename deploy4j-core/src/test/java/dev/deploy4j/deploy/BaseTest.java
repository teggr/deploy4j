package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.ssh.SshHost;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Base")
class BaseTest {

    @Mock
    private SshHosts sshHosts;

    @Mock
    private Hooks hooks;

    private Base base;

    @BeforeEach
    void setUp() {
        base = new Base(sshHosts, hooks);
    }

    @Test
    @DisplayName("should delegate to SshHosts for on method")
    void shouldDelegateToSshHosts() {
        // Arrange
        List<String> hosts = Arrays.asList("host1", "host2");
        Consumer<SshHost> block = host -> {};

        // Act
        base.on(hosts, block);

        // Assert
        verify(sshHosts).on(eq(hosts), any());
    }

    @Test
    @DisplayName("should pass correct hosts list to SshHosts")
    void shouldPassCorrectHostsList() {
        // Arrange
        List<String> hosts = Arrays.asList("server1.example.com", "server2.example.com");
        
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> hostsCaptor = ArgumentCaptor.forClass(List.class);
        Consumer<SshHost> block = host -> {};

        // Act
        base.on(hosts, block);

        // Assert
        verify(sshHosts).on(hostsCaptor.capture(), any());
        assertThat(hostsCaptor.getValue()).isEqualTo(hosts);
    }
}
