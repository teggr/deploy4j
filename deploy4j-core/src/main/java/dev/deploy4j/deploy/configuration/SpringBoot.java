package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.configuration.raw.SpringBootConfig;

import java.util.ArrayList;
import java.util.List;

public class SpringBoot {

  private static final int DEFAULT_ACTUATOR_PORT = 8080;
  private static final String DEFAULT_ACTUATOR_BASE_PATH = "/actuator";

  private final SpringBootConfig springBootConfig;
  private final Configuration configuration;

  public SpringBoot(SpringBootConfig springBootConfig, Configuration configuration) {
    this.springBootConfig = springBootConfig != null ? springBootConfig : new SpringBootConfig();
    this.configuration = configuration;
  }

  public Integer actuatorPort() {
    return springBootConfig.actuatorPort() != null ? springBootConfig.actuatorPort() : DEFAULT_ACTUATOR_PORT;
  }

  public String actuatorBasePath() {
    return springBootConfig.actuatorBasePath() != null ? springBootConfig.actuatorBasePath() : DEFAULT_ACTUATOR_BASE_PATH;
  }

  /**
   * Generates the endpoint URL for the given endpoint path.
   * Uses the container name as the hostname since the container is running in a Docker network.
   * 
   * @param endpoint the endpoint path (e.g., "health")
   * @param containerName the name of the target container
   * @return the full URL to the actuator endpoint
   */
  public String endpointUrl(String endpoint, String containerName) {
    String basePath = actuatorBasePath();
    if (!basePath.startsWith("/")) {
      basePath = "/" + basePath;
    }
    if (basePath.endsWith("/")) {
      basePath = basePath.substring(0, basePath.length() - 1);
    }
    if (!endpoint.startsWith("/")) {
      endpoint = "/" + endpoint;
    }
    return "http://" + containerName + ":" + actuatorPort() + basePath + endpoint;
  }
}
