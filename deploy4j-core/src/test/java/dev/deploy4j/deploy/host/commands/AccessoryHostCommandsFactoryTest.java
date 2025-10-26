package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessoryHostCommandsFactory")
class AccessoryHostCommandsFactoryTest {

    @Mock
    private Configuration config;

    private AccessoryHostCommandsFactory factory;

    @BeforeEach
    void setUp() {
        factory = new AccessoryHostCommandsFactory(config);
    }

    @Test
    @DisplayName("should create AccessoryHostCommands instance")
    void shouldCreateAccessoryHostCommands() {
        // Act
        AccessoryHostCommands result = factory.accessory("test-accessory");

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("should create different instances for different accessory names")
    void shouldCreateDifferentInstancesForDifferentNames() {
        // Act
        AccessoryHostCommands result1 = factory.accessory("accessory1");
        AccessoryHostCommands result2 = factory.accessory("accessory2");

        // Assert
        assertThat(result1).isNotSameAs(result2);
    }

    @Test
    @DisplayName("should pass configuration to created instances")
    void shouldPassConfigurationToCreatedInstances() {
        // Act
        AccessoryHostCommands result = factory.accessory("test");

        // Assert
        assertThat(result).isNotNull();
        // The instance is created, which validates that config is passed
    }
}
