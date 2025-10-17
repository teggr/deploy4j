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
    return lookup( registryConfig.username() );
  }

  public String password() {
    return lookup( registryConfig.password() );
  }

  private String lookup(String key) {
    // array of lookups or direct value
    String fetched = ENV.fetch(key);
    if(fetched == null) {
      return key;
    } else {
      return fetched;
    }
  }

}
