# Test Implementation Checklist

This checklist is a quick reference guide derived from the main [unit-test-plan.md](./unit-test-plan.md). Use this to track progress as tests are implemented.

## Legend

- ⬜ Not started
- 🔄 In progress
- ✅ Completed
- ⏭️ Skipped/deferred

---

## Phase 1: Critical Foundation (P0) - Weeks 1-2

### Utilities (Quick Wins)
- ✅ `RandomHex` - Simple utility class for generating random hex strings
- ✅ `ENV` - Environment variable utilities (expand existing tests)
- ✅ `Cmd` - Command builder (verify completeness of existing tests)

### Core Configuration
- ⬜ `Configuration` - Main configuration class (complex, break into test suites)
- ⬜ `DeployConfigYamlReader` - YAML config reader (expand existing tests)

### Command Generation
- ✅ `DockerHostCommands` - Docker command generation
- ✅ `BaseHostCommands` - Base class for all host commands

### SSH Operations
- ⬜ `SSHTemplate` - SSH execution template (use mocks or TestContainers)

**Phase 1 Progress:** 5/8 classes

---

## Phase 2: High Priority Business Logic (P1) - Weeks 3-4

### Deployment Operations
- ⬜ `Deploy` - Main deployment orchestration
- ⬜ `App` - Application management
- ⬜ `Build` - Build orchestration
- ⬜ `LockManager` - Lock coordination

### Configuration Classes
- ⬜ `Role` - Role management (complex)
- ⬜ `HealthCheck` - Health check configuration
- ⬜ `Registry` (configuration) - Registry configuration
- ⬜ `Traefik` (configuration) - Traefik configuration
- ⬜ `Ssh` (configuration) - SSH configuration
- ⬜ `Boot` (configuration) - Boot configuration
- ⬜ `Builder` - Builder configuration
- ⬜ `Env` (configuration) - Environment configuration
- ⬜ `Logging` - Logging configuration
- ⬜ `Accessory` (configuration) - Accessory configuration

### Environment & Files
- ✅ `ENVDotenv` - Dotenv file handling
- ✅ `File` - File utilities
- ⬜ `ERB` - Template processing

### Host Commands
- ⬜ `AppHostCommands` - Application-specific commands
- ⬜ `BuilderHostCommands` - Builder commands

### SSH Management
- ⬜ `SshHost` - SSH host wrapper
- ⬜ `SshHosts` - Multiple SSH host management

### Ext Module Priority Classes
- ✅ `FluentCmd` - Fluent command API
- ✅ `ExecResult` - SSH execution result holder
- ✅ `Cmds` - Command collection utilities

### Other P1 Classes
- ⬜ `Accessory` (deploy) - Accessory deployment logic
- ⬜ `Audit` - Audit operations
- ⬜ `Base` - Base deployment operations
- ⬜ `DeployApplicationContext` - Application context
- ⬜ `DeployContext` - Deployment context
- ⬜ `Env` (deploy) - Environment deployment
- ⬜ `Environment` - Environment management
- ⬜ `Lock` - Lock operations
- ⬜ `Registry` (deploy) - Registry operations
- ⬜ `Server` - Server management
- ⬜ `Traefik` (deploy) - Traefik deployment
- ✅ `PlainValueOrSecretKey` - Secret handling
- ✅ `Utils` - General utilities
- ⬜ `Initializer` - Application initializer
- ✅ `Barrier` - Health check barrier
- ⬜ `Poller` - Health check poller

**Phase 2 Progress:** 8/41 classes

---

## Phase 3: Medium Priority (P2) - Weeks 5-6

### Remaining Deploy Operations
- ⬜ `Prune` - Cleanup operations
- ⬜ `Version` - Version handling
- ✅ `Tags` - Tag management
- ⬜ `Specifics` - Specific configurations
- ⬜ `LockContext` - Lock context

### Configuration Data Classes
- ⬜ `ConfigureArgs` - Configuration arguments
- ✅ `Volume` - Volume configuration
- ✅ `Tag` (env) - Environment tag
- ⬜ `AccessoryConfig` - Accessory raw config
- ⬜ `BootConfig` - Boot raw config
- ⬜ `CustomRoleConfig` - Custom role raw config
- ⬜ `HealthCheckConfig` - Health check raw config
- ⬜ `LoggingConfig` - Logging raw config
- ⬜ `RegistryConfig` - Registry raw config
- ⬜ `RoleConfig` - Role raw config
- ⬜ `ServerConfig` - Server raw config
- ⬜ `SshConfig` - SSH raw config
- ⬜ `TraefikConfig` - Traefik raw config

### Host Command Implementations
- ⬜ `AccessoryHostCommands` - Accessory commands
- ⬜ `AccessoryHostCommandsFactory` - Accessory command factory
- ⬜ `AppHostCommandsFactory` - App command factory
- ⬜ `AuditorHostCommands` - Audit commands
- ⬜ `HealthcheckHostCommands` - Health check commands
- ⬜ `HookHostCommands` - Hook execution commands
- ⬜ `LockHostCommands` - Lock commands
- ⬜ `PruneHostCommands` - Prune commands
- ⬜ `RegistryHostCommands` - Registry commands
- ⬜ `ServerHostCommands` - Server commands
- ⬜ `TraefikHostCommands` - Traefik commands

### Application Bootstrap
- ⬜ `Boot` (deploy.app) - Application bootstrapping
- ⬜ `PrepareAssets` - Asset preparation

### Ext Module P2 Classes
- ✅ `Curl` - Curl command wrapper

**Phase 3 Progress:** 4/29 classes

---

## Phase 4: Low Priority (P3) - Week 7

### Simple Command Wrappers
- ✅ `Echo` - Echo command wrapper
- ✅ `Grep` - Grep command wrapper
- ✅ `Wget` - Wget command wrapper

**Phase 4 Progress:** 3/3 classes

---

## Overall Progress Summary

| Phase | Total Classes | Completed | In Progress | Not Started | Progress % |
|-------|--------------|-----------|-------------|-------------|------------|
| Phase 1 (P0) | 8 | 5 | 0 | 3 | 63% |
| Phase 2 (P1) | 41 | 8 | 0 | 33 | 20% |
| Phase 3 (P2) | 29 | 4 | 0 | 25 | 14% |
| Phase 4 (P3) | 3 | 3 | 0 | 0 | 100% |
| **Total** | **81** | **20** | **0** | **61** | **25%** |

*Note: This excludes the 10 classes that already have tests*

---

## Quick Reference: Priority Definitions

- **P0 (Critical):** Core business logic, data transformations, utilities used everywhere
- **P1 (High):** Configuration handling, command generation, SSH operations  
- **P2 (Medium):** Host commands, factory classes, specific implementations
- **P3 (Low):** Simple data classes, value objects with minimal logic

---

## Notes

- Update this checklist as tests are implemented
- Mark items as 🔄 when work begins
- Mark items as ✅ when tests are complete and reviewed
- Use ⏭️ for items that are intentionally skipped or deferred
- Link to specific test file commits/PRs for tracking

---

## See Also

- [unit-test-plan.md](./unit-test-plan.md) - Complete detailed test plan
- Project README for build and test instructions
- CI/CD configuration for automated test execution
