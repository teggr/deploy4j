package dev.deploy4j.deploy.host.ssh;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Ssh;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SshHosts")
class SshHostsTest {

    @Mock
    private Configuration configuration;

    @Mock
    private Ssh ssh;

    private SshHosts sshHosts;

    @BeforeEach
    void setUp() {
        lenient().when(configuration.ssh()).thenReturn(ssh);
        lenient().when(ssh.user()).thenReturn("testuser");
        lenient().when(ssh.port()).thenReturn(22);
        lenient().when(ssh.keyPath()).thenReturn("/path/to/key");
        lenient().when(ssh.keyPassphrase()).thenReturn("passphrase");
        lenient().when(ssh.strictHostKeyChecking()).thenReturn(true);
        sshHosts = new SshHosts(configuration);
    }

    @Test
    @DisplayName("should create and cache SSH host connections")
    void shouldCreateAndCacheSshHostConnections() {
        // Arrange & Act
        try (var mockedSshTemplate = mockConstruction(dev.rebelcraft.ssh.SSHTemplate.class)) {
            SshHost host1 = sshHosts.host("host1.example.com");
            SshHost host2 = sshHosts.host("host1.example.com"); // Same host
            SshHost host3 = sshHosts.host("host2.example.com"); // Different host

            // Assert
            assertThat(host1).isNotNull();
            assertThat(host1).isSameAs(host2); // Should return cached instance
            assertThat(host3).isNotNull();
            assertThat(host3).isNotSameAs(host1); // Different host, different instance

            assertThat(host1.hostName()).isEqualTo("host1.example.com");
            assertThat(host3.hostName()).isEqualTo("host2.example.com");
        }
    }

    @Test
    @DisplayName("should execute action on all provided hosts")
    void shouldExecuteActionOnAllProvidedHosts() {
        // Arrange
        List<String> hosts = List.of("host1.example.com", "host2.example.com", "host3.example.com");
        AtomicInteger callCount = new AtomicInteger(0);

        // Act
        try (var mockedSshTemplate = mockConstruction(dev.rebelcraft.ssh.SSHTemplate.class)) {
            sshHosts.on(hosts, sshHost -> {
                assertThat(sshHost).isNotNull();
                callCount.incrementAndGet();
            });

            // Assert
            assertThat(callCount.get()).isEqualTo(3);
        }
    }

    @Test
    @DisplayName("should call close on all SSH hosts when closing")
    void shouldCallCloseOnAllSshHostsWhenClosing() throws Exception {
        // Arrange
        try (var mockedSshTemplate = mockConstruction(dev.rebelcraft.ssh.SSHTemplate.class)) {
            sshHosts.host("host1.example.com");
            sshHosts.host("host2.example.com");
            sshHosts.host("host3.example.com");

            // Act
            sshHosts.close();

            // Assert
            assertThat(mockedSshTemplate.constructed()).hasSize(3);
            for (dev.rebelcraft.ssh.SSHTemplate template : mockedSshTemplate.constructed()) {
                verify(template).close();
            }
        }
    }

    @Test
    @DisplayName("should handle empty host list")
    void shouldHandleEmptyHostList() {
        // Arrange
        List<String> hosts = List.of();
        AtomicInteger callCount = new AtomicInteger(0);

        // Act
        sshHosts.on(hosts, sshHost -> callCount.incrementAndGet());

        // Assert
        assertThat(callCount.get()).isZero();
    }
}