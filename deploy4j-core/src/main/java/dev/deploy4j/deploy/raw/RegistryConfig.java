package dev.deploy4j.deploy.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistryConfig {

  private final String server;
  private final PlainValueOrSecretKey username;
  private final PlainValueOrSecretKey password;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public RegistryConfig(
    @JsonProperty("server") String server,
    @JsonProperty("username") PlainValueOrSecretKey username,
    @JsonProperty("password") PlainValueOrSecretKey password ) {
    this.server = server;
    this.username = username;
    this.password = password;
  }

  public RegistryConfig() {
    this.server = null;
    this.username = null;
    this.password = null;
  }

  public String server() {
    return server;
  }

  public PlainValueOrSecretKey username() {
    return username;
  }

  public PlainValueOrSecretKey password() {
    return password;
  }

}
