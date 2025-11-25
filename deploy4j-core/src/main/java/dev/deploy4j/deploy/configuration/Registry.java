package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.Secrets;
import dev.deploy4j.deploy.configuration.raw.PlainValueOrSecretKey;
import dev.deploy4j.deploy.configuration.raw.RegistryConfig;

public class Registry {

  private final Secrets secrets;
  private final RegistryConfig registryConfig;

  public Registry(Secrets secrets, Configuration config) {
    this.secrets = secrets;
    this.registryConfig = config.rawConfig().registry() != null
      ? config.rawConfig().registry()
      : new RegistryConfig();
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
    if( key == null ) {
      return null;
    }
    if (key.isKey()) {
      return secrets.get(key.key());
    } else {
      return key.value();
    }
  }

  // attributes

  public RegistryConfig registryConfig() {
    return registryConfig;
  }

}
