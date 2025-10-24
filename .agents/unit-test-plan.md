# Unit Test Plan for deploy4j

**Version:** 1.0  
**Date:** 2025-10-23  
**Status:** Planning Phase

## Overview

This document outlines a comprehensive testing strategy for the deploy4j project. It identifies test coverage gaps across the deploy4j-core and deploy4j-ext modules and provides prioritized recommendations for implementing unit tests.

## Current Test Coverage Status

### deploy4j-core Module

**Total Source Files:** 79 classes  
**Existing Test Files:** 7 tests

**Existing Tests:**
- `dev.deploy4j.Deploy4jTest` - Integration-style test (currently commented out)
- `dev.deploy4j.EnvFileTest` - Tests for environment file handling
- `dev.deploy4j.env.ENVTest` - Tests for ENV utility class
- `dev.deploy4j.raw.Deploy4JYamlConfigReaderTest` - Tests for YAML configuration reading
- `dev.deploy4j.raw.EnvironmentVariablesTest` - Tests for environment variable handling
- `dev.deploy4j.raw.ExampleConfigTest` - Tests for example configurations
- `dev.deploy4j.raw.ServersTest` - Tests for server configuration

**Test Coverage:** ~9% (7 tests / 79 classes)

### deploy4j-ext Module

**Total Source Files:** 11 classes  
**Existing Test Files:** 3 tests

**Existing Tests:**
- `dev.rebelcraft.cmd.CmdTest` - Tests for command building
- `dev.rebelcraft.cmd.CmdsTest` - Tests for command utilities
- `dev.rebelcraft.cmd.pkgs.DockerTest` - Tests for Docker package

**Test Coverage:** ~27% (3 tests / 11 classes)

## Testing Strategy

### Priority Levels

- **P0 (Critical):** Core business logic, data transformations, utilities used everywhere
- **P1 (High):** Configuration handling, command generation, SSH operations
- **P2 (Medium):** Host commands, factory classes, specific implementations
- **P3 (Low):** Simple data classes, value objects with minimal logic

### Testing Approach

1. **Unit Tests:** Isolated tests for individual classes and methods
2. **Integration Tests:** Tests for interactions between components
3. **Mock Strategy:** Use mocks for external dependencies (SSH, file I/O, Docker)
4. **Test Data:** Use realistic test fixtures and example configurations

### Test Framework

- **JUnit 5** - Primary test framework
- **AssertJ** - Fluent assertions (already in use)
- **Mockito** - For mocking dependencies
- **TestContainers** - For integration tests requiring Docker (optional)

## Test Gaps by Package

### deploy4j-core

#### Package: `dev.deploy4j.deploy` (20 classes)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `Accessory` | P1 | ❌ | Medium | Core deployment logic |
| `App` | P0 | ❌ | High | Main application orchestration |
| `Audit` | P1 | ❌ | Medium | Auditing functionality |
| `Base` | P1 | ❌ | Medium | Base deployment operations |
| `Build` | P0 | ❌ | High | Build orchestration |
| `Deploy` | P0 | ❌ | High | Main deployment logic |
| `DeployApplicationContext` | P1 | ❌ | Medium | Context management |
| `DeployContext` | P1 | ❌ | Medium | Context handling |
| `Env` | P1 | ❌ | Medium | Environment management |
| `Environment` | P1 | ❌ | Medium | Environment configuration |
| `Lock` | P1 | ❌ | Medium | Lock management |
| `LockContext` | P2 | ❌ | Low | Lock context |
| `LockManager` | P1 | ❌ | High | Lock coordination |
| `Prune` | P2 | ❌ | Medium | Cleanup operations |
| `Registry` | P1 | ❌ | Medium | Registry operations |
| `Server` | P1 | ❌ | Medium | Server management |
| `Specifics` | P2 | ❌ | Low | Specific configurations |
| `Tags` | P2 | ❌ | Low | Tag management |
| `Traefik` | P1 | ❌ | Medium | Traefik integration |
| `Version` | P2 | ❌ | Low | Version handling |

#### Package: `dev.deploy4j.deploy.app` (2 classes)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `Boot` | P1 | ❌ | Medium | Application bootstrapping |
| `PrepareAssets` | P2 | ❌ | Medium | Asset preparation |

#### Package: `dev.deploy4j.deploy.configuration` (14 classes)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `Accessory` | P1 | ❌ | Medium | Accessory configuration |
| `Boot` | P1 | ❌ | Medium | Boot configuration |
| `Builder` | P1 | ❌ | Medium | Builder configuration |
| `Configuration` | P0 | ❌ | High | Main configuration class - complex |
| `ConfigureArgs` | P2 | ❌ | Low | Simple argument holder |
| `Env` | P1 | ❌ | Medium | Environment configuration |
| `HealthCheck` | P1 | ❌ | Medium | Health check configuration |
| `Logging` | P1 | ❌ | Medium | Logging configuration |
| `Registry` | P1 | ❌ | Medium | Registry configuration |
| `Role` | P1 | ❌ | High | Role management - complex |
| `Servers` | P1 | ✅ | High | Has tests already |
| `Ssh` | P1 | ❌ | Medium | SSH configuration |
| `Traefik` | P1 | ❌ | Medium | Traefik configuration |
| `Volume` | P2 | ❌ | Low | Volume configuration |

#### Package: `dev.deploy4j.deploy.configuration.env` (1 class)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `Tag` | P2 | ❌ | Low | Environment tag |

#### Package: `dev.deploy4j.deploy.configuration.raw` (15 classes)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `AccessoryConfig` | P2 | ❌ | Low | Config data class |
| `BootConfig` | P2 | ❌ | Low | Config data class |
| `CustomRoleConfig` | P2 | ❌ | Low | Config data class |
| `DeployConfig` | P1 | ✅ | Medium | Has partial tests |
| `DeployConfigYamlReader` | P0 | ✅ | High | Has tests - YAML parsing |
| `EnvironmentConfig` | P1 | ✅ | Medium | Has tests |
| `HealthCheckConfig` | P2 | ❌ | Low | Config data class |
| `LoggingConfig` | P2 | ❌ | Low | Config data class |
| `PlainValueOrSecretKey` | P1 | ❌ | Medium | Secret handling logic |
| `RegistryConfig` | P2 | ❌ | Low | Config data class |
| `RoleConfig` | P2 | ❌ | Low | Config data class |
| `ServerConfig` | P2 | ❌ | Low | Config data class |
| `ServersConfig` | P1 | ✅ | Medium | Has tests |
| `SshConfig` | P2 | ❌ | Low | Config data class |
| `TraefikConfig` | P2 | ❌ | Low | Config data class |

#### Package: `dev.deploy4j.deploy.env` (3 classes)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `ENV` | P0 | ✅ | Medium | Has tests - environment utilities |
| `ENVDotenv` | P1 | ❌ | Medium | Dotenv file handling |
| `EnvFile` | P1 | ✅ | Medium | Has tests - environment file |

#### Package: `dev.deploy4j.deploy.healthcheck` (2 classes)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `Barrier` | P1 | ❌ | High | Synchronization logic |
| `Poller` | P1 | ❌ | High | Polling logic - needs mocking |

#### Package: `dev.deploy4j.deploy.host.commands` (15 classes)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `AccessoryHostCommands` | P2 | ❌ | Medium | Command generation |
| `AccessoryHostCommandsFactory` | P2 | ❌ | Low | Factory class |
| `AppHostCommands` | P1 | ❌ | High | Core app commands |
| `AppHostCommandsFactory` | P2 | ❌ | Low | Factory class |
| `AuditorHostCommands` | P2 | ❌ | Medium | Audit commands |
| `BaseHostCommands` | P1 | ❌ | High | Base command utilities |
| `BuilderHostCommands` | P1 | ❌ | High | Build commands |
| `DockerHostCommands` | P0 | ❌ | High | Docker command generation |
| `HealthcheckHostCommands` | P2 | ❌ | Medium | Health check commands |
| `HookHostCommands` | P2 | ❌ | Medium | Hook execution commands |
| `LockHostCommands` | P2 | ❌ | Medium | Lock commands |
| `PruneHostCommands` | P2 | ❌ | Medium | Prune commands |
| `RegistryHostCommands` | P2 | ❌ | Medium | Registry commands |
| `ServerHostCommands` | P2 | ❌ | Medium | Server commands |
| `TraefikHostCommands` | P2 | ❌ | Medium | Traefik commands |

#### Package: `dev.deploy4j.deploy.host.ssh` (2 classes)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `SshHost` | P1 | ❌ | High | SSH host management |
| `SshHosts` | P1 | ❌ | Medium | Multiple host management |

#### Package: `dev.deploy4j.deploy.utils` (2 classes)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `RandomHex` | P0 | ❌ | Low | Utility - easy to test |
| `Utils` | P1 | ❌ | Medium | General utilities |

#### Package: `dev.deploy4j.deploy.utils.erb` (1 class)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `ERB` | P1 | ❌ | High | Template processing |

#### Package: `dev.deploy4j.deploy.utils.file` (1 class)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `File` | P1 | ❌ | Medium | File utilities |

#### Package: `dev.deploy4j.init` (1 class)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `Initializer` | P1 | ❌ | Medium | Initialization logic |

### deploy4j-ext

#### Package: `dev.rebelcraft.cmd` (4 classes)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `Cmd` | P0 | ✅ | Low | Has tests - command builder |
| `CmdUtils` | P0 | ✅ | Medium | Has tests - command utilities |
| `Cmds` | P1 | ❌ | Medium | Command collection utilities |
| `FluentCmd` | P1 | ❌ | Medium | Fluent command API |

#### Package: `dev.rebelcraft.cmd.pkgs` (5 classes)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `Curl` | P2 | ❌ | Low | Curl command wrapper |
| `Docker` | P1 | ✅ | Medium | Has tests - Docker commands |
| `Echo` | P3 | ❌ | Low | Echo command wrapper |
| `Grep` | P3 | ❌ | Low | Grep command wrapper |
| `Wget` | P3 | ❌ | Low | Wget command wrapper |

#### Package: `dev.rebelcraft.ssh` (2 classes)

| Class | Priority | Test Exists | Test Complexity | Notes |
|-------|----------|-------------|-----------------|-------|
| `ExecResult` | P1 | ❌ | Low | Simple result holder |
| `SSHTemplate` | P0 | ❌ | High | SSH execution - needs integration test |

## Implementation Recommendations

### Phase 1: Critical Foundation (P0 - Weeks 1-2)

**Goal:** Test the most critical utilities and core logic

1. **Utilities** (Quick wins)
   - `RandomHex` - Simple utility, easy to test
   - `ENV` - Already has tests, ensure comprehensive
   - `Cmd` - Already has tests, verify completeness

2. **Core Configuration**
   - `Configuration` - Complex but essential, break into smaller test suites
   - `DeployConfigYamlReader` - Already has tests, expand coverage

3. **Command Generation**
   - `DockerHostCommands` - Critical for Docker operations
   - `BaseHostCommands` - Base class for command generation

4. **SSH Operations**
   - `SSHTemplate` - Critical infrastructure, use mocks or TestContainers

### Phase 2: High Priority Business Logic (P1 - Weeks 3-4)

**Goal:** Cover primary business logic and configurations

1. **Deployment Operations**
   - `Deploy` - Main deployment orchestration
   - `App` - Application management
   - `Build` - Build orchestration
   - `LockManager` - Lock coordination

2. **Configuration Classes**
   - `Role` - Complex role management
   - `HealthCheck` - Health check configuration
   - `Registry` - Registry operations
   - `Traefik` - Traefik configuration
   - `Ssh` - SSH configuration

3. **Environment & Files**
   - `ENVDotenv` - Dotenv handling
   - `File` - File utilities
   - `ERB` - Template processing

4. **Host Commands**
   - `AppHostCommands` - Application commands
   - `BuilderHostCommands` - Builder commands

5. **SSH Management**
   - `SshHost` - SSH host management
   - `SshHosts` - Multiple host coordination

6. **Ext Module**
   - `FluentCmd` - Fluent command API
   - `ExecResult` - Result handling

### Phase 3: Medium Priority (P2 - Weeks 5-6)

**Goal:** Fill in gaps for secondary operations

1. **Remaining Deploy Operations**
   - `Accessory`, `Server`, `Prune`, `Version`, `Tags`, `Specifics`
   - `Lock`, `LockContext`, `DeployContext`, `DeployApplicationContext`

2. **Configuration Data Classes**
   - All config data classes in `configuration.raw` package
   - `ConfigureArgs`, `Volume`, `Tag`

3. **Host Command Implementations**
   - All remaining `*HostCommands` classes
   - Factory classes

4. **Application Bootstrap**
   - `Boot` (deploy.app)
   - `PrepareAssets`

### Phase 4: Low Priority (P3 - Week 7)

**Goal:** Complete coverage for simple wrappers

1. **Simple Command Wrappers**
   - `Echo`, `Grep`, `Wget`, `Curl`

## Test Templates

### Unit Test Template

```java
package dev.deploy4j.deploy.utils;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class RandomHexTest {
    
    @Test
    void shouldGenerateHexStringOfCorrectLength() {
        String hex = RandomHex.randomHex(8);
        assertThat(hex).hasSize(16); // 8 bytes = 16 hex chars
    }
    
    @Test
    void shouldGenerateUniqueValues() {
        String hex1 = RandomHex.randomHex(8);
        String hex2 = RandomHex.randomHex(8);
        assertThat(hex1).isNotEqualTo(hex2);
    }
    
    @Test
    void shouldContainOnlyHexCharacters() {
        String hex = RandomHex.randomHex(16);
        assertThat(hex).matches("^[0-9a-f]+$");
    }
}
```

### Mock-Based Test Template

```java
package dev.deploy4j.deploy.host.ssh;

import dev.rebelcraft.ssh.SSHTemplate;
import dev.rebelcraft.ssh.ExecResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class SshHostTest {
    
    @Mock
    private SSHTemplate sshTemplate;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void shouldExecuteCommand() {
        ExecResult expectedResult = new ExecResult(0, "output", "");
        when(sshTemplate.exec(anyString())).thenReturn(expectedResult);
        
        // Test implementation
    }
}
```

## Testing Guidelines

### General Principles

1. **Test Behavior, Not Implementation**
   - Focus on public API contracts
   - Test expected outcomes and edge cases

2. **Keep Tests Independent**
   - Each test should be runnable in isolation
   - Use `@BeforeEach` for setup, avoid shared state

3. **Use Descriptive Test Names**
   - Follow pattern: `should<ExpectedBehavior>When<Condition>`
   - Example: `shouldReturnEmptyListWhenNoServersConfigured`

4. **Mock External Dependencies**
   - Mock SSH connections, file I/O, Docker calls
   - Use real objects for POJOs and simple utilities

5. **Test Edge Cases**
   - Null inputs, empty collections, invalid configurations
   - Boundary conditions for numeric values

### Coverage Goals

- **Target:** 80% line coverage for new tests
- **Priority:** 100% coverage for P0 classes
- **Minimum:** 60% coverage across all modules

## Dependencies & Setup

### Required Test Dependencies

Add to parent `pom.xml`:

```xml
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.1</version>
        <scope>test</scope>
    </dependency>
    
    <!-- AssertJ (already present) -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>3.24.2</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.8.0</version>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>5.8.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Next Steps

1. **Review & Approval**
   - Review this test plan with the team
   - Adjust priorities based on project needs
   - Identify any critical missing areas

2. **Resource Allocation**
   - Assign developers to test implementation phases
   - Allocate time for writing tests alongside feature work

3. **Test Infrastructure**
   - Set up CI/CD pipeline to run tests
   - Configure code coverage reporting (JaCoCo)
   - Establish coverage thresholds

4. **Implementation**
   - Start with Phase 1 (P0 classes)
   - Follow the phased approach outlined above
   - Regular review and adjustment as needed

## Appendix: Complete Class List

### deploy4j-core Classes Without Tests (72 classes)

```
dev.deploy4j.deploy.Accessory
dev.deploy4j.deploy.App
dev.deploy4j.deploy.Audit
dev.deploy4j.deploy.Base
dev.deploy4j.deploy.Build
dev.deploy4j.deploy.Deploy
dev.deploy4j.deploy.DeployApplicationContext
dev.deploy4j.deploy.DeployContext
dev.deploy4j.deploy.Env
dev.deploy4j.deploy.Environment
dev.deploy4j.deploy.Lock
dev.deploy4j.deploy.LockContext
dev.deploy4j.deploy.LockManager
dev.deploy4j.deploy.Prune
dev.deploy4j.deploy.Registry
dev.deploy4j.deploy.Server
dev.deploy4j.deploy.Specifics
dev.deploy4j.deploy.Tags
dev.deploy4j.deploy.Traefik
dev.deploy4j.deploy.Version
dev.deploy4j.deploy.app.Boot
dev.deploy4j.deploy.app.PrepareAssets
dev.deploy4j.deploy.configuration.Accessory
dev.deploy4j.deploy.configuration.Boot
dev.deploy4j.deploy.configuration.Builder
dev.deploy4j.deploy.configuration.Configuration
dev.deploy4j.deploy.configuration.ConfigureArgs
dev.deploy4j.deploy.configuration.Env
dev.deploy4j.deploy.configuration.HealthCheck
dev.deploy4j.deploy.configuration.Logging
dev.deploy4j.deploy.configuration.Registry
dev.deploy4j.deploy.configuration.Role
dev.deploy4j.deploy.configuration.Ssh
dev.deploy4j.deploy.configuration.Traefik
dev.deploy4j.deploy.configuration.Volume
dev.deploy4j.deploy.configuration.env.Tag
dev.deploy4j.deploy.configuration.raw.AccessoryConfig
dev.deploy4j.deploy.configuration.raw.BootConfig
dev.deploy4j.deploy.configuration.raw.CustomRoleConfig
dev.deploy4j.deploy.configuration.raw.HealthCheckConfig
dev.deploy4j.deploy.configuration.raw.LoggingConfig
dev.deploy4j.deploy.configuration.raw.PlainValueOrSecretKey
dev.deploy4j.deploy.configuration.raw.RegistryConfig
dev.deploy4j.deploy.configuration.raw.RoleConfig
dev.deploy4j.deploy.configuration.raw.ServerConfig
dev.deploy4j.deploy.configuration.raw.SshConfig
dev.deploy4j.deploy.configuration.raw.TraefikConfig
dev.deploy4j.deploy.env.ENVDotenv
dev.deploy4j.deploy.healthcheck.Barrier
dev.deploy4j.deploy.healthcheck.Poller
dev.deploy4j.deploy.host.commands.AccessoryHostCommands
dev.deploy4j.deploy.host.commands.AccessoryHostCommandsFactory
dev.deploy4j.deploy.host.commands.AppHostCommands
dev.deploy4j.deploy.host.commands.AppHostCommandsFactory
dev.deploy4j.deploy.host.commands.AuditorHostCommands
dev.deploy4j.deploy.host.commands.BaseHostCommands
dev.deploy4j.deploy.host.commands.BuilderHostCommands
dev.deploy4j.deploy.host.commands.DockerHostCommands
dev.deploy4j.deploy.host.commands.HealthcheckHostCommands
dev.deploy4j.deploy.host.commands.HookHostCommands
dev.deploy4j.deploy.host.commands.LockHostCommands
dev.deploy4j.deploy.host.commands.PruneHostCommands
dev.deploy4j.deploy.host.commands.RegistryHostCommands
dev.deploy4j.deploy.host.commands.ServerHostCommands
dev.deploy4j.deploy.host.commands.TraefikHostCommands
dev.deploy4j.deploy.host.ssh.SshHost
dev.deploy4j.deploy.host.ssh.SshHosts
dev.deploy4j.deploy.utils.RandomHex
dev.deploy4j.deploy.utils.Utils
dev.deploy4j.deploy.utils.erb.ERB
dev.deploy4j.deploy.utils.file.File
dev.deploy4j.init.Initializer
```

### deploy4j-ext Classes Without Tests (8 classes)

```
dev.rebelcraft.cmd.Cmds
dev.rebelcraft.cmd.FluentCmd
dev.rebelcraft.cmd.pkgs.Curl
dev.rebelcraft.cmd.pkgs.Echo
dev.rebelcraft.cmd.pkgs.Grep
dev.rebelcraft.cmd.pkgs.Wget
dev.rebelcraft.ssh.ExecResult
dev.rebelcraft.ssh.SSHTemplate
```

## Summary Statistics

- **Total Classes:** 90
- **Classes with Tests:** 10 (11%)
- **Classes without Tests:** 80 (89%)
- **P0 (Critical) Classes:** 8
- **P1 (High) Classes:** 41
- **P2 (Medium) Classes:** 35
- **P3 (Low) Classes:** 6

**Estimated Implementation Effort:** 7-8 weeks with 1-2 developers dedicated to test implementation
