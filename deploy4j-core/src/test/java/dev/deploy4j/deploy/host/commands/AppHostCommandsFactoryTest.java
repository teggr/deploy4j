package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppHostCommandsFactory")
class AppHostCommandsFactoryTest {

    @Mock
    private Configuration config;

    @Mock
    private Role role;

    private AppHostCommandsFactory factory;

    @BeforeEach
    void setUp() {
        factory = new AppHostCommandsFactory(config);
    }

    @Test
    @DisplayName("should create AppHostCommands instance")
    void shouldCreateAppHostCommands() {
        // Act
        AppHostCommands result = factory.app(role, "test-host");

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("should create different instances for different roles")
    void shouldCreateDifferentInstancesForDifferentRoles() {
        // Arrange
        Role role2 = org.mockito.Mockito.mock(Role.class);

        // Act
        AppHostCommands result1 = factory.app(role, "host1");
        AppHostCommands result2 = factory.app(role2, "host2");

        // Assert
        assertThat(result1).isNotSameAs(result2);
    }

    @Test
    @DisplayName("should create different instances for different hosts")
    void shouldCreateDifferentInstancesForDifferentHosts() {
        // Act
        AppHostCommands result1 = factory.app(role, "host1");
        AppHostCommands result2 = factory.app(role, "host2");

        // Assert
        assertThat(result1).isNotSameAs(result2);
    }
}
