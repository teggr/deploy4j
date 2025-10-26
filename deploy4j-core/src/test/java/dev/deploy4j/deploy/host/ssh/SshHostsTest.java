package dev.deploy4j.deploy.host.ssh;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.ssh.SSHTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
    private SSHTemplate sshTemplate1;

    @Mock
    private SSHTemplate sshTemplate2;

    @Mock
    private SSHTemplate sshTemplate3;

    private SshHosts sshHosts;

    @BeforeEach
    void setUp() {
        sshHosts = new SshHosts(configuration) {
            private int counter = 0;
            
            @Override
            public SshHost host(String host) {
                SshHost existingHost = super.getCachedHost(host);
                if (existingHost != null) {
                    return existingHost;
                }
                
                SSHTemplate template;
                switch (counter++) {
                    case 0: template = sshTemplate1; break;
                    case 1: template = sshTemplate2; break;
                    case 2: template = sshTemplate3; break;
                    default: throw new IllegalStateException("Too many hosts");
                }
                
                SshHost sshHost = new SshHost(host, template);
                super.cacheHost(host, sshHost);
                return sshHost;
            }
        };
    }

    @Test
    @DisplayName("should create and cache SSH host connections")
    void shouldCreateAndCacheSshHostConnections() {
        // Act
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

    @Test
    @DisplayName("should execute action on all provided hosts")
    void shouldExecuteActionOnAllProvidedHosts() {
        // Arrange
        List<String> hosts = List.of("host1.example.com", "host2.example.com", "host3.example.com");
        AtomicInteger callCount = new AtomicInteger(0);

        // Act
        sshHosts.on(hosts, sshHost -> {
            assertThat(sshHost).isNotNull();
            callCount.incrementAndGet();
        });

        // Assert
        assertThat(callCount.get()).isEqualTo(3);
    }

    @Test
    @DisplayName("should call close on all SSH hosts when closing")
    void shouldCallCloseOnAllSshHostsWhenClosing() throws Exception {
        // Arrange
        sshHosts.host("host1.example.com");
        sshHosts.host("host2.example.com");
        sshHosts.host("host3.example.com");

        // Act
        sshHosts.close();

        // Assert
        verify(sshTemplate1).close();
        verify(sshTemplate2).close();
        verify(sshTemplate3).close();
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
