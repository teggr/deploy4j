package dev.deploy4j.configuration;

import dev.deploy4j.env.ENV;
import dev.deploy4j.raw.RegistryConfig;

public class Registry {

  private final RegistryConfig registryConfig;

  public Registry(Configuration config) {
    this.registryConfig = config.rawConfig().registry();
  }

  public String server() {
    return registryConfig.server();
  }

  public String username() {
    return ENV.lookup( registryConfig.username() );
  }

  public String password() {
    return ENV.lookup( registryConfig.password() );
  }

}
