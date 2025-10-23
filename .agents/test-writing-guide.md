# Test Writing Guide for deploy4j

This guide provides practical instructions and examples for implementing unit tests for the deploy4j project.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Test Structure](#test-structure)
3. [Common Testing Patterns](#common-testing-patterns)
4. [Mocking Guidelines](#mocking-guidelines)
5. [Best Practices](#best-practices)
6. [Example Tests](#example-tests)

---

## Getting Started

### Required Dependencies

The project uses:
- **JUnit 5** for test framework
- **AssertJ** for fluent assertions
- **Mockito** for mocking (to be added)

### Test File Location

Tests should mirror the source file structure:
- Source: `deploy4j-core/src/main/java/dev/deploy4j/deploy/Utils.java`
- Test: `deploy4j-core/src/test/java/dev/deploy4j/deploy/UtilsTest.java`

### Naming Convention

- Test class: `{ClassName}Test`
- Test method: `should{ExpectedBehavior}When{Condition}` or `should{ExpectedBehavior}`

---

## Test Structure

### Basic Test Template

```java
package dev.deploy4j.deploy.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RandomHex")
class RandomHexTest {
    
    @BeforeEach
    void setUp() {
        // Initialize test data or mocks
    }
    
    @Test
    @DisplayName("should generate hex string of correct length")
    void shouldGenerateHexStringOfCorrectLength() {
        // Arrange
        int byteLength = 8;
        
        // Act
        String hex = RandomHex.randomHex(byteLength);
        
        // Assert
        assertThat(hex)
            .hasSize(16)  // 8 bytes = 16 hex chars
            .matches("^[0-9a-f]+$");
    }
    
    @Test
    @DisplayName("should generate unique values on each call")
    void shouldGenerateUniqueValues() {
        // Arrange & Act
        String hex1 = RandomHex.randomHex(8);
        String hex2 = RandomHex.randomHex(8);
        
        // Assert
        assertThat(hex1).isNotEqualTo(hex2);
    }
    
    @Test
    @DisplayName("should handle edge case of zero length")
    void shouldHandleZeroLength() {
        // Act
        String hex = RandomHex.randomHex(0);
        
        // Assert
        assertThat(hex).isEmpty();
    }
}
```

### Test with Setup and Teardown

```java
@DisplayName("Configuration")
class ConfigurationTest {
    
    private DeployConfig deployConfig;
    private Configuration configuration;
    
    @BeforeEach
    void setUp() {
        // Common setup for all tests
        deployConfig = createTestDeployConfig();
        configuration = new Configuration(deployConfig, "production", "1.0.0");
    }
    
    @AfterEach
    void tearDown() {
        // Cleanup if needed
    }
    
    @Test
    void shouldReturnCorrectVersion() {
        assertThat(configuration.version()).isEqualTo("1.0.0");
    }
    
    private DeployConfig createTestDeployConfig() {
        // Helper method to create test data
        return new DeployConfig(/* ... */);
    }
}
```

---

## Common Testing Patterns

### Testing Simple Utilities

```java
class FileTest {
    
    @Test
    void shouldJoinPathSegments() {
        String result = File.join("home", "user", "documents");
        assertThat(result).isEqualTo("home/user/documents");
    }
    
    @Test
    void shouldHandleNullSegments() {
        String result = File.join("home", null, "documents");
        assertThat(result).isEqualTo("home/documents");
    }
    
    @Test
    void shouldHandleEmptySegments() {
        String result = File.join("home", "", "documents");
        assertThat(result).isEqualTo("home/documents");
    }
}
```

### Testing with Collections

```java
@Test
void shouldReturnAllHostsFromRoles() {
    // Arrange
    Configuration config = createConfigWithMultipleRoles();
    
    // Act
    List<String> hosts = config.allHosts();
    
    // Assert
    assertThat(hosts)
        .hasSize(5)
        .contains("host1.example.com", "host2.example.com")
        .doesNotHaveDuplicates();
}

@Test
void shouldReturnEmptyListWhenNoRoles() {
    Configuration config = createConfigWithNoRoles();
    
    assertThat(config.allHosts()).isEmpty();
}
```

### Testing Null Safety

```java
@Test
void shouldHandleNullInput() {
    assertThatThrownBy(() -> Utils.process(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("input cannot be null");
}

@Test
void shouldReturnDefaultWhenValueIsNull() {
    Configuration config = createConfigWithNullVersion();
    assertThat(config.version()).isEqualTo("latest");
}
```

### Testing Conditional Logic

```java
@Test
void shouldUseEnvironmentVersionWhenDeclaredVersionIsNull() {
    // Arrange
    Configuration config = new Configuration(deployConfig, "prod", null);
    
    // Act
    String version = config.version();
    
    // Assert - assuming ENV.fetch("VERSION") returns "env-version"
    assertThat(version).isNotNull();
}

@Test
void shouldPreferDeclaredVersionOverEnvironmentVersion() {
    Configuration config = new Configuration(deployConfig, "prod", "1.2.3");
    assertThat(config.version()).isEqualTo("1.2.3");
}
```

---

## Mocking Guidelines

### When to Mock

Mock external dependencies:
- File system operations
- SSH connections
- Docker API calls
- External HTTP requests
- Database connections

Don't mock:
- Simple POJOs and data classes
- Value objects
- Your own domain objects (prefer real objects)

### Basic Mocking with Mockito

```java
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class SshHostTest {
    
    @Mock
    private SSHTemplate sshTemplate;
    
    @Mock
    private Configuration configuration;
    
    @Test
    void shouldExecuteCommandSuccessfully() {
        // Arrange
        ExecResult expectedResult = new ExecResult(0, "success output", "");
        when(sshTemplate.exec("docker ps")).thenReturn(expectedResult);
        
        SshHost sshHost = new SshHost("host1", sshTemplate);
        
        // Act
        ExecResult result = sshHost.execute("docker ps");
        
        // Assert
        assertThat(result.exitStatus()).isEqualTo(0);
        assertThat(result.execOutput()).contains("success");
        
        // Verify interaction
        verify(sshTemplate).exec("docker ps");
        verify(sshTemplate, times(1)).exec(anyString());
    }
}
```

### Mocking with ArgumentCaptor

```java
import org.mockito.ArgumentCaptor;

@Test
void shouldExecuteDockerCommandWithCorrectArguments() {
    // Arrange
    ArgumentCaptor<String> commandCaptor = ArgumentCaptor.forClass(String.class);
    when(sshTemplate.exec(anyString())).thenReturn(new ExecResult(0, "", ""));
    
    // Act
    dockerCommands.pullImage("myapp:latest");
    
    // Assert
    verify(sshTemplate).exec(commandCaptor.capture());
    String executedCommand = commandCaptor.getValue();
    assertThat(executedCommand)
        .contains("docker pull")
        .contains("myapp:latest");
}
```

### Partial Mocking with Spy

```java
@Test
void shouldCallInternalMethodWhenProcessing() {
    // Arrange
    Configuration realConfig = new Configuration(deployConfig, "prod", "1.0");
    Configuration spyConfig = spy(realConfig);
    
    doReturn("custom-value").when(spyConfig).someInternalMethod();
    
    // Act & Assert
    String result = spyConfig.process();
    assertThat(result).contains("custom-value");
    verify(spyConfig).someInternalMethod();
}
```

---

## Best Practices

### 1. Arrange-Act-Assert Pattern

Always structure tests clearly:

```java
@Test
void testExample() {
    // Arrange - Set up test data and expectations
    Configuration config = createTestConfig();
    String expectedVersion = "1.0.0";
    
    // Act - Execute the code under test
    String actualVersion = config.version();
    
    // Assert - Verify the results
    assertThat(actualVersion).isEqualTo(expectedVersion);
}
```

### 2. Test One Thing Per Test

```java
// ❌ Bad - testing multiple things
@Test
void shouldWorkCorrectly() {
    assertThat(config.version()).isEqualTo("1.0");
    assertThat(config.service()).isEqualTo("myapp");
    assertThat(config.destination()).isEqualTo("prod");
}

// ✅ Good - separate tests
@Test
void shouldReturnCorrectVersion() {
    assertThat(config.version()).isEqualTo("1.0");
}

@Test
void shouldReturnCorrectService() {
    assertThat(config.service()).isEqualTo("myapp");
}

@Test
void shouldReturnCorrectDestination() {
    assertThat(config.destination()).isEqualTo("prod");
}
```

### 3. Use Descriptive Test Names

```java
// ❌ Bad
@Test
void test1() { }

// ✅ Good
@Test
void shouldReturnPrimaryHostFromWebRoleWhenConfigured() { }

// ✅ Also good with @DisplayName
@Test
@DisplayName("should return primary host from web role when configured")
void testPrimaryHost() { }
```

### 4. Test Edge Cases

Always test:
- Null inputs
- Empty collections
- Boundary values (0, -1, max values)
- Invalid inputs
- Error conditions

```java
@Test
void shouldThrowExceptionWhenServiceNameIsEmpty() {
    assertThatThrownBy(() -> new Configuration(emptyServiceConfig, "prod", "1.0"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("service name cannot be empty");
}

@Test
void shouldHandleEmptyHostList() {
    Configuration config = createConfigWithEmptyHosts();
    assertThat(config.allHosts()).isEmpty();
}

@Test
void shouldHandleMaximumRetainContainers() {
    Configuration config = createConfigWithRetain(Integer.MAX_VALUE);
    assertThat(config.retainContainer()).isEqualTo(Integer.MAX_VALUE);
}
```

### 5. Use AssertJ Fluent Assertions

```java
// String assertions
assertThat(version)
    .isNotNull()
    .isNotEmpty()
    .startsWith("v")
    .hasSize(5)
    .matches("v\\d+\\.\\d+\\.\\d+");

// Collection assertions
assertThat(hosts)
    .isNotEmpty()
    .hasSize(3)
    .contains("host1", "host2")
    .doesNotContain("invalid-host")
    .allMatch(host -> host.contains(".example.com"));

// Exception assertions
assertThatThrownBy(() -> config.invalidOperation())
    .isInstanceOf(IllegalStateException.class)
    .hasMessage("Cannot perform operation")
    .hasNoCause();

// Object assertions
assertThat(config)
    .isNotNull()
    .hasFieldOrPropertyWithValue("destination", "prod")
    .extracting("version", "service")
    .containsExactly("1.0", "myapp");
```

---

## Example Tests

### Example 1: Testing a Utility Class

```java
package dev.deploy4j.deploy.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RandomHex utility")
class RandomHexTest {
    
    @Test
    @DisplayName("should generate hex string with correct number of characters")
    void shouldGenerateCorrectLength() {
        String hex = RandomHex.randomHex(8);
        assertThat(hex).hasSize(16); // 8 bytes = 16 hex characters
    }
    
    @Test
    @DisplayName("should generate different values on subsequent calls")
    void shouldGenerateDifferentValues() {
        String hex1 = RandomHex.randomHex(16);
        String hex2 = RandomHex.randomHex(16);
        
        assertThat(hex1).isNotEqualTo(hex2);
    }
    
    @Test
    @DisplayName("should only contain valid hexadecimal characters")
    void shouldContainOnlyHexCharacters() {
        String hex = RandomHex.randomHex(32);
        
        assertThat(hex)
            .matches("^[0-9a-f]+$")
            .hasSize(64);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 8, 16, 32, 64})
    @DisplayName("should work with various byte lengths")
    void shouldWorkWithVariousByteLengths(int byteLength) {
        String hex = RandomHex.randomHex(byteLength);
        assertThat(hex).hasSize(byteLength * 2);
    }
}
```

### Example 2: Testing a Configuration Class

```java
package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.DeployConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Configuration")
class ConfigurationTest {
    
    @Nested
    @DisplayName("version handling")
    class VersionHandling {
        
        private Configuration configuration;
        
        @Test
        @DisplayName("should return declared version when provided")
        void shouldReturnDeclaredVersion() {
            DeployConfig deployConfig = createMinimalConfig();
            configuration = new Configuration(deployConfig, "prod", "1.2.3");
            
            assertThat(configuration.version()).isEqualTo("1.2.3");
        }
        
        @Test
        @DisplayName("should abbreviate version to 7 characters when not underscore-separated")
        void shouldAbbreviateVersion() {
            DeployConfig deployConfig = createMinimalConfig();
            configuration = new Configuration(deployConfig, "prod", "1234567890abc");
            
            assertThat(configuration.abbreviatedVersion()).isEqualTo("1234567");
        }
        
        @Test
        @DisplayName("should not abbreviate underscore-separated version")
        void shouldNotAbbreviateUnderscoreVersion() {
            DeployConfig deployConfig = createMinimalConfig();
            configuration = new Configuration(deployConfig, "prod", "v1_2_3");
            
            assertThat(configuration.abbreviatedVersion()).isEqualTo("v1_2_3");
        }
    }
    
    @Nested
    @DisplayName("host management")
    class HostManagement {
        
        @Test
        @DisplayName("should return all unique hosts from all roles")
        void shouldReturnAllUniqueHosts() {
            // Test implementation
        }
        
        @Test
        @DisplayName("should return primary host from primary role")
        void shouldReturnPrimaryHost() {
            // Test implementation
        }
    }
    
    private DeployConfig createMinimalConfig() {
        // Helper method to create minimal valid config
        return new DeployConfig(/* ... */);
    }
}
```

### Example 3: Testing with Mocks

```java
package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.ssh.SSHTemplate;
import dev.rebelcraft.ssh.ExecResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DockerHostCommands")
class DockerHostCommandsTest {
    
    @Mock
    private SSHTemplate sshTemplate;
    
    @Mock
    private Configuration configuration;
    
    private DockerHostCommands dockerCommands;
    
    @BeforeEach
    void setUp() {
        dockerCommands = new DockerHostCommands(sshTemplate, configuration);
    }
    
    @Test
    @DisplayName("should generate correct pull command for image")
    void shouldGenerateCorrectPullCommand() {
        // Arrange
        when(configuration.absoluteImage()).thenReturn("myregistry/myapp:1.0");
        when(sshTemplate.exec(anyString())).thenReturn(new ExecResult(0, "Pull complete", ""));
        
        // Act
        ExecResult result = dockerCommands.pullImage();
        
        // Assert
        verify(sshTemplate).exec(contains("docker pull"));
        verify(sshTemplate).exec(contains("myregistry/myapp:1.0"));
        assertThat(result.exitStatus()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("should handle failed pull command")
    void shouldHandleFailedPull() {
        // Arrange
        when(configuration.absoluteImage()).thenReturn("invalid/image:tag");
        when(sshTemplate.exec(anyString()))
            .thenReturn(new ExecResult(1, "", "Error: image not found"));
        
        // Act
        ExecResult result = dockerCommands.pullImage();
        
        // Assert
        assertThat(result.exitStatus()).isEqualTo(1);
        assertThat(result.execErrorOutput()).contains("Error");
    }
}
```

---

## Running Tests

### Run all tests
```bash
mvn test
```

### Run tests for specific module
```bash
mvn test -pl deploy4j-core
mvn test -pl deploy4j-ext
```

### Run specific test class
```bash
mvn test -Dtest=RandomHexTest
```

### Run with coverage report
```bash
mvn test jacoco:report
```

---

## Tips for Success

1. **Start Small:** Begin with simple utility classes before tackling complex orchestration classes
2. **Write Tests First (TDD):** When fixing bugs, write a failing test first, then fix the code
3. **Keep Tests Fast:** Avoid Thread.sleep(), use mocks instead of real external services
4. **Maintain Tests:** Update tests when refactoring code
5. **Review Coverage:** Use JaCoCo reports to identify untested code paths
6. **Share Knowledge:** Document complex test setups for team reference

---

## Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

---

## Questions?

If you encounter challenges while writing tests:
1. Check existing tests in the project for patterns
2. Refer to this guide and the main test plan
3. Consult with the team
4. Document new patterns you discover

Happy testing! 🧪
