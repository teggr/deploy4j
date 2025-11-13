package dev.deploy4j.integration;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.raw.*;

import java.util.List;
import java.util.Map;

/**
 * Factory class for creating test configurations programmatically.
 * This allows integration tests to configure deploy4j without relying on config files.
 */
public class TestConfigurationFactory {

    /**
     * Creates a minimal configuration suitable for integration testing.
     * 
     * @param serviceName the service name
     * @param imageName the Docker image name
     * @param hosts the list of hosts to deploy to
     * @param sshUsername SSH username
     * @param sshPort SSH port
     * @return a Configuration object ready for testing
     */
    public static Configuration createTestConfiguration(
            String serviceName,
            String imageName,
            List<String> hosts,
            String sshUsername,
            int sshPort
    ) {
        // Create SSH configuration
        SshConfig sshConfig = new SshConfig(
            new PlainValueOrSecretKey(sshUsername),  // user
            sshPort,                                  // port
            null,                                     // no proxy
            null,                                     // no proxy_command
            null,                                     // no log_level
            null,                                     // no key_path (uses default ~/.ssh/id_rsa)
            null,                                     // no key_passphrase
            false,                                    // disable strict_host_key_checking
            new PlainValueOrSecretKey("/dev/null")   // known_hosts_path (no host checking)
        );

        // Create registry configuration (no authentication for test)
        RegistryConfig registryConfig = new RegistryConfig(null, null, null);

        // Create servers configuration
        ServersConfig serversConfig = new ServersConfig(hosts);

        // Create environment configuration
        EnvironmentConfig envConfig = new EnvironmentConfig(
            Map.of(),  // clear env vars
            null,      // no secrets
            null,      // no tags
            null       // no environment variables map
        );

        // Create deploy configuration
        DeployConfig deployConfig = new DeployConfig(
            serviceName,
            imageName,
            null,  // no labels
            null,  // no volumes
            registryConfig,
            serversConfig,
            envConfig,
            null,  // no asset path
            null,  // no hooks path
            false, // require destination
            null,  // no primary role
            false, // allow empty roles
            null,  // no stop wait time
            null,  // no retain containers
            null,  // no minimum version
            null,  // no readiness delay
            null,  // no run directory
            sshConfig,
            null,  // no accessories
            null,  // no traefik config
            null,  // no boot config
            null,  // no health check config
            null   // no logging config
        );

        return new Configuration(deployConfig, null, "0.0.2-SNAPSHOT");
    }
}
