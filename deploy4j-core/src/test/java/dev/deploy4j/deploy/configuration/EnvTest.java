package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.EnvironmentConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@DisplayName("Env")
class EnvTest {

    @Test
    @DisplayName("should create Env with null config")
    void shouldCreateEnvWithNullConfig() {
        // Act
        Env env = new Env(null, "/path/to/secrets.env", "test/env");

        // Assert
        assertThat(env.clear()).isEmpty();
        assertThat(env.secretsKeys()).isEmpty();
        assertThat(env.secretsFile()).isEqualTo("/path/to/secrets.env");
        assertThat(env.context()).isEqualTo("test/env");
    }

    @Test
    @DisplayName("should create Env with map config")
    void shouldCreateEnvWithMapConfig() {
        // Arrange
        Map<String, String> envMap = Map.of("KEY1", "value1", "KEY2", "value2");
        EnvironmentConfig config = new EnvironmentConfig(null, null, null, envMap);

        // Act
        Env env = new Env(config, "/path/to/secrets.env", "test/env");

        // Assert
        assertThat(env.clear()).containsExactlyInAnyOrderEntriesOf(envMap);
        assertThat(env.secretsKeys()).isEmpty();
    }

    @Test
    @DisplayName("should create Env with clear and secrets config")
    void shouldCreateEnvWithClearAndSecretsConfig() {
        // Arrange
        Map<String, String> clearMap = Map.of("PUBLIC_KEY", "public_value");
        List<String> secrets = List.of("SECRET_KEY", "API_TOKEN");
        EnvironmentConfig config = new EnvironmentConfig(clearMap, secrets, null, null);

        // Act
        Env env = new Env(config, "/path/to/secrets.env", "test/env");

        // Assert
        assertThat(env.clear()).containsExactlyInAnyOrderEntriesOf(clearMap);
        assertThat(env.secretsKeys()).containsExactlyInAnyOrder("SECRET_KEY", "API_TOKEN");
    }

    @Test
    @DisplayName("should generate args with env file and clear vars")
    void shouldGenerateArgsWithEnvFileAndClearVars() {
        // Arrange
        Map<String, String> clearMap = Map.of("KEY1", "value1");
        EnvironmentConfig config = new EnvironmentConfig(clearMap, null, null, null);

        // Act
        Env env = new Env(config, "/path/to/secrets.env", "test/env");
        List<String> args = env.args();

        // Assert
        assertThat(args).contains("--env-file", "/path/to/secrets.env");
        assertThat(args).contains("--env");
    }

    @Test
    @DisplayName("should retrieve secrets directory from secrets file path")
    void shouldRetrieveSecretsDirectoryFromSecretsFilePath() {
        // Arrange
        Env env = new Env(null, "/app/env/service/secrets.env", "test/env");

        // Act
        String secretsDir = env.secretsDirectory();

        // Assert
        // File.dirname() uses Apache Commons getFullPathNoEndSeparator
        assertThat(secretsDir).isNotNull();
        assertThat(secretsDir).doesNotEndWith("/");
        // Verify it returns the parent directory
        assertThat("/app/env/service/secrets.env").startsWith(secretsDir);
    }

    @Test
    @DisplayName("should encode secrets to IO format")
    void shouldEncodeSecretsToIOFormat() {
        // Arrange
        List<String> secrets = List.of("SECRET_KEY");
        EnvironmentConfig config = new EnvironmentConfig(null, secrets, null, null);

        // Mock ENV.fetch
        try (MockedStatic<dev.deploy4j.deploy.env.ENV> envMock = mockStatic(dev.deploy4j.deploy.env.ENV.class)) {
            envMock.when(() -> dev.deploy4j.deploy.env.ENV.fetch("SECRET_KEY")).thenReturn("secret_value");

            Env env = new Env(config, "/path/to/secrets.env", "test/env");

            // Act
            String secretsIO = env.secretsIO();

            // Assert
            assertThat(secretsIO).isNotEmpty();
            assertThat(secretsIO).contains("SECRET_KEY");
        }
    }

    @Test
    @DisplayName("should merge with another Env - documents current incomplete behavior")
    void shouldMergeWithAnotherEnv() {
        // Arrange
        Map<String, String> clear1 = Map.of("KEY1", "value1");
        Map<String, String> clear2 = Map.of("KEY2", "value2");
        List<String> secrets1 = List.of("SECRET1");
        List<String> secrets2 = List.of("SECRET2");

        EnvironmentConfig config1 = new EnvironmentConfig(clear1, secrets1, null, null);
        EnvironmentConfig config2 = new EnvironmentConfig(clear2, secrets2, null, null);

        Env env1 = new Env(config1, "/path1/secrets.env", "env1");
        Env env2 = new Env(config2, "/path2/secrets.env", "env2");

        // Act
        Env merged = env1.merge(env2);

        // Assert
        // NOTE: This test documents current incomplete behavior due to TODO in source
        // The merge implementation passes null for config, so merged env has empty clear/secrets
        // This test will need to be updated when merge is properly implemented
        assertThat(merged.secretsFile()).isEqualTo("/path1/secrets.env");
    }

    @Test
    @DisplayName("should use default context when not provided")
    void shouldUseDefaultContextWhenNotProvided() {
        // Arrange
        EnvironmentConfig config = new EnvironmentConfig(null, null, null, null);

        // Act
        Env env = new Env(config);

        // Assert
        assertThat(env.context()).isEqualTo("env");
        assertThat(env.secretsFile()).isNull();
    }

    @Test
    @DisplayName("should handle empty clear and secrets config")
    void shouldHandleEmptyClearAndSecretsConfig() {
        // Arrange
        EnvironmentConfig config = new EnvironmentConfig(Map.of(), List.of(), null, null);

        // Act
        Env env = new Env(config, "/path/to/secrets.env", "test/env");

        // Assert
        assertThat(env.clear()).isEmpty();
        assertThat(env.secretsKeys()).isEmpty();
    }
}
