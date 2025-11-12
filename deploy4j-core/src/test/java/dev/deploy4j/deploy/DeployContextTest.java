package dev.deploy4j.deploy;

import dev.deploy4j.deploy.configuration.Accessory;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Role;
import dev.deploy4j.deploy.configuration.raw.DeployConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("DeployContext")
class DeployContextTest {

    @Test
    @DisplayName("should initialize with configuration and no specific filters")
    void shouldInitializeWithConfig() {
        // Arrange
        Configuration config = mock(Configuration.class);
        when(config.allHosts()).thenReturn(List.of("host1", "host2"));
        when(config.primaryHost()).thenReturn("host1");
        when(config.roles()).thenReturn(List.of(mock(Role.class)));

        // Act
        DeployContext context = new DeployContext(config, null, null, null);

        // Assert
        assertThat(context.config()).isEqualTo(config);
        assertThat(context.specificHosts()).isNull();
        assertThat(context.specificRoles()).isNull();
        assertThat(context.holdingLock()).isFalse();
        assertThat(context.connected()).isFalse();
    }

    @Test
    @DisplayName("should filter specific hosts when provided")
    void shouldFilterSpecificHosts() {
        // Arrange
        Configuration config = mock(Configuration.class);
        when(config.allHosts()).thenReturn(List.of("host1", "host2", "host3"));
        when(config.primaryHost()).thenReturn("host1");
        when(config.roles()).thenReturn(List.of(mock(Role.class)));

        // Act
        DeployContext context = new DeployContext(config, new String[]{"host1", "host2"}, null, null);

        // Assert
        assertThat(context.specificHosts()).containsExactly("host1", "host2");
    }

    @Test
    @DisplayName("should throw exception when no hosts match filter")
    void shouldThrowWhenNoHostsMatch() {
        // Arrange
        Configuration config = mock(Configuration.class);
        when(config.allHosts()).thenReturn(List.of("host1", "host2"));
        when(config.primaryHost()).thenReturn("host1");

        // Act & Assert
        assertThatThrownBy(() -> new DeployContext(config, new String[]{"host3"}, null, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No --hosts match for host3");
    }

    @Test
    @DisplayName("should use primary host when primary flag is true")
    void shouldUsePrimaryHostWhenPrimaryFlagSet() {
        // Arrange
        Configuration config = mock(Configuration.class);
        when(config.allHosts()).thenReturn(List.of("host1", "host2"));
        when(config.primaryHost()).thenReturn("host1");
        when(config.roles()).thenReturn(List.of(mock(Role.class)));

        // Act
        DeployContext context = new DeployContext(config, null, null, true);

        // Assert
        assertThat(context.specificHosts()).containsExactly("host1");
    }

    @Test
    @DisplayName("should filter specific roles when provided")
    void shouldFilterSpecificRoles() {
        // Arrange
        Configuration config = mock(Configuration.class);
        when(config.allHosts()).thenReturn(List.of("host1", "host2"));
        when(config.primaryHost()).thenReturn("host1");
        
        Role role1 = mock(Role.class);
        when(role1.name()).thenReturn("web");
        Role role2 = mock(Role.class);
        when(role2.name()).thenReturn("worker");
        
        when(config.roles()).thenReturn(List.of(role1, role2));

        // Act
        DeployContext context = new DeployContext(config, null, new String[]{"web"}, null);

        // Assert
        assertThat(context.specificRoles()).hasSize(1);
        assertThat(context.specificRoles().get(0).name()).isEqualTo("web");
    }

    @Test
    @DisplayName("should throw exception when no roles match filter")
    void shouldThrowWhenNoRolesMatch() {
        // Arrange
        Configuration config = mock(Configuration.class);
        when(config.allHosts()).thenReturn(List.of("host1", "host2"));
        when(config.primaryHost()).thenReturn("host1");
        
        Role role1 = mock(Role.class);
        when(role1.name()).thenReturn("web");
        when(config.roles()).thenReturn(List.of(role1));

        // Act & Assert
        assertThatThrownBy(() -> new DeployContext(config, null, new String[]{"worker"}, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No --roles match for worker");
    }

    @Test
    @DisplayName("should return accessory names from configuration")
    void shouldReturnAccessoryNames() {
        // Arrange
        Configuration config = mock(Configuration.class);
        when(config.allHosts()).thenReturn(List.of("host1"));
        when(config.primaryHost()).thenReturn("host1");
        when(config.roles()).thenReturn(List.of(mock(Role.class)));
        
        Accessory acc1 = mock(Accessory.class);
        when(acc1.name()).thenReturn("db");
        Accessory acc2 = mock(Accessory.class);
        when(acc2.name()).thenReturn("redis");
        
        when(config.accessories()).thenReturn(List.of(acc1, acc2));

        // Act
        DeployContext context = new DeployContext(config, null, null, null);
        List<String> names = context.accessoryNames();

        // Assert
        assertThat(names).containsExactly("db", "redis");
    }

    @Test
    @DisplayName("should return accessories on specific host")
    void shouldReturnAccessoriesOnHost() {
        // Arrange
        Configuration config = mock(Configuration.class);
        when(config.allHosts()).thenReturn(List.of("host1", "host2"));
        when(config.primaryHost()).thenReturn("host1");
        when(config.roles()).thenReturn(List.of(mock(Role.class)));
        
        Accessory acc1 = mock(Accessory.class);
        when(acc1.name()).thenReturn("db");
        when(acc1.hosts()).thenReturn(List.of("host1"));
        
        Accessory acc2 = mock(Accessory.class);
        when(acc2.name()).thenReturn("redis");
        when(acc2.hosts()).thenReturn(List.of("host2"));
        
        when(config.accessories()).thenReturn(List.of(acc1, acc2));

        // Act
        DeployContext context = new DeployContext(config, null, null, null);
        List<String> accessoriesOnHost1 = context.accessoriesOn("host1");

        // Assert
        assertThat(accessoriesOnHost1).containsExactly("db");
    }

    @Test
    @DisplayName("should manage lock state")
    void shouldManageLockState() {
        // Arrange
        Configuration config = mock(Configuration.class);
        when(config.allHosts()).thenReturn(List.of("host1"));
        when(config.primaryHost()).thenReturn("host1");
        when(config.roles()).thenReturn(List.of(mock(Role.class)));
        
        DeployContext context = new DeployContext(config, null, null, null);

        // Act & Assert
        assertThat(context.holdingLock()).isFalse();
        
        context.holdingLock(true);
        assertThat(context.holdingLock()).isTrue();
        
        context.holdingLock(false);
        assertThat(context.holdingLock()).isFalse();
    }
}
