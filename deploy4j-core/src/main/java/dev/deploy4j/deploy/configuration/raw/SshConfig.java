package dev.deploy4j.deploy.configuration.raw;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SshConfig {

  private final PlainValueOrSecretKey user;
  private final Integer port;
  private final String proxy;
  private final String proxyCommand;
  private final String logLevel;
  private final PlainValueOrSecretKey keyPath;
  private final PlainValueOrSecretKey keyPassphrase;
  private final Boolean strictHostKeyChecking;
  private final PlainValueOrSecretKey knownHostsPath;

  @JsonCreator
  public SshConfig(
    @JsonProperty("user") PlainValueOrSecretKey user,
    @JsonProperty("port") Integer port,
    @JsonProperty("proxy") String proxy,
    @JsonProperty("proxy_command") String proxyCommand,
    @JsonProperty("log_level") String logLevel,
    @JsonProperty("key_path") PlainValueOrSecretKey keyPath,
    @JsonProperty("key_passphrase") PlainValueOrSecretKey keyPassphrase,
    @JsonProperty("strict_host_key_checking") Boolean strictHostKeyChecking,
    @JsonProperty("known_hosts_path") PlainValueOrSecretKey knownHostsPath
  ) {
    this.user = user;
    this.port = port;
    this.proxy = proxy;
    this.proxyCommand = proxyCommand;
    this.logLevel = logLevel;
    this.keyPath = keyPath;
    this.keyPassphrase = keyPassphrase;
    this.strictHostKeyChecking = strictHostKeyChecking;
    this.knownHostsPath = knownHostsPath;
  }

  public SshConfig() {
    this.user = null;
    this.port = null;
    this.proxy = null;
    this.proxyCommand = null;
    this.logLevel = null;
    this.keyPath = null;
    this.keyPassphrase = null;
    this.strictHostKeyChecking = null;
    this.knownHostsPath = null;
  }

  public PlainValueOrSecretKey user() {
    return user;
  }

  public Integer port() {
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

  public PlainValueOrSecretKey keyPath() {
    return keyPath;
  }

  public PlainValueOrSecretKey keyPassphrase() {
    return keyPassphrase;
  }

  public Boolean strictHostKeyChecking() {
    return strictHostKeyChecking;
  }

  public PlainValueOrSecretKey knownHostsPath() {
    return knownHostsPath;
  }

}
