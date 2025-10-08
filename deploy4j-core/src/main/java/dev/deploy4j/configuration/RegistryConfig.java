package dev.deploy4j.configuration;

public class RegistryConfig {

  private final String server;
  private final String username;
  private final String password;

  public RegistryConfig(String server, String username, String password ) {
    this.server = server;
    this.username = username;
    this.password = password;
  }

  public String server() {
    return server;
  }

  public String username() {
    return lookup( username );
  }

  private String lookup(String key) {
    return System.getenv(key); // TODO: implement environment variable lookup
  }

  public String password() {
    return lookup( password );
  }

}
