package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.env.ENV;
import dev.deploy4j.deploy.raw.PlainValueOrSecretKey;
import dev.deploy4j.deploy.raw.RegistryConfig;

public class Registry {

  private final RegistryConfig registryConfig;

  public Registry(Configuration config) {
    this.registryConfig = config.rawConfig().registry() != null
      ? config.rawConfig().registry()
      : new RegistryConfig();
    // TODO: validate
  }

  public String server() {
    return registryConfig().server();
  }

  public String username() {
    return lookup(registryConfig.username());
  }

  public String password() {
    return lookup(registryConfig.password());
  }

  // private

  private String lookup(PlainValueOrSecretKey key) {
    if (key.isKey()) {
      return ENV.fetch(key.key());
    } else {
      return key.value();
    }
  }

  // attributes

  public RegistryConfig registryConfig() {
    return registryConfig;
  }

}
