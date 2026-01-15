package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;

/**
 * Factory for creating SpringBootHostCommands instances with the appropriate container name.
 */
public class SpringBootHostCommandsFactory {

  private final Configuration config;

  public SpringBootHostCommandsFactory(Configuration config) {
    this.config = config;
  }

  /**
   * Creates a SpringBootHostCommands instance for the given container name.
   * 
   * @param containerName the name of the target container (e.g., "myservice-web-1.0.0")
   * @return a new SpringBootHostCommands instance
   */
  public SpringBootHostCommands forContainer(String containerName) {
    return new SpringBootHostCommands(config, containerName);
  }
}
