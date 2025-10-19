package dev.deploy4j.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SshConfig {

  private final String user;
  private final String port;
  private final String proxy;
  private final String proxyCommand;
  private final String logLevel;
  private final Boolean keysOnly;
  private final List<String> keys;
  private final List<String> keyData;
  private final List<PlainValueOrSecretKey> keyPassphrase;

  @JsonCreator
  public SshConfig(
    @JsonProperty("user") String user,
    @JsonProperty("port") String port,
    @JsonProperty("proxy") String proxy,
    @JsonProperty("proxy_command") String proxyCommand,
    @JsonProperty("log_level") String logLevel,
    @JsonProperty("keys_only") Boolean keysOnly,
    @JsonProperty("keys") List<String> keys,
    @JsonProperty("key_data") List<String> keyData,
    @JsonProperty("key_passphrases") List<PlainValueOrSecretKey> keyPassphrase
  ) {
    this.user = user;
    this.port = port;
    this.proxy = proxy;
    this.proxyCommand = proxyCommand;
    this.logLevel = logLevel;
    this.keysOnly = keysOnly;
    this.keys = keys;
    this.keyData = keyData;
    this.keyPassphrase = keyPassphrase;
  }

  public String user() {
    return user;
  }

  public String port() {
    return port;
  }

  public String proxy() {
    return proxy;
  }

  public String proxyCommand() {
    return proxyCommand;
  }

  public String logLevel() {
    return logLevel;
  }

  public Boolean keysOnly() {
    return keysOnly;
  }

  public List<String> keys() {
    return keys;
  }

  public List<String> keyData() {
    return keyData;
  }

  public List<PlainValueOrSecretKey> keyPassphrase() {
    return keyPassphrase;
  }

}
