package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.Secrets;
import dev.deploy4j.deploy.configuration.raw.PlainValueOrSecretKey;
import dev.deploy4j.deploy.configuration.raw.SshConfig;

import java.util.HashMap;
import java.util.Map;

public class Ssh {

  private final SshConfig sshConfig;
  private final Secrets secrets;

  public Ssh(Configuration config, Secrets secrets) {
    this.sshConfig = config.rawConfig().ssh() != null ?
      config.rawConfig().ssh() : new SshConfig();
    this.secrets = secrets;
  }

  public String user() {
    return sshConfig.user() != null ?
      lookup( sshConfig.user() ) : "root";
  }

  public Integer port() {
    return sshConfig.port() != null ?
      sshConfig.port() : 22;
  }

  public String proxy() {
    return sshConfig().proxy();
  }

  public String keyPath() {
    return lookup( sshConfig().keyPath() );
  }

  public String keyPassphrase() {
    return lookup( sshConfig().keyPassphrase() );
  }

  public Boolean strictHostKeyChecking() {
    return sshConfig().strictHostKeyChecking() != null ?
      sshConfig().strictHostKeyChecking() : true;
  }

  public String knownHostsPath() {
    return lookup( sshConfig().knownHostsPath() );
  }

  public Map<String, String> options() {
    Map<String, String> options = new HashMap<String, String>();
    if (user() != null) {
      options.put("user", user());
    }
    if (port() != null) {
      options.put("port", port().toString());
    }
    // TODO: proxy
    options.put("keepalive", "true");
    options.put("keepalive_interval", "30");
    if(knownHostsPath() != null) {
      options.put("knownHostsPath", knownHostsPath());
    }
    return options;
  }

  // private

  private String logger() {
    // TODO: LOGGER
    return null;
  }

  private String logLevel() {
    return sshConfig().logLevel();
  }

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

  public SshConfig sshConfig() {
    return sshConfig;
  }

  public Map<String, Object> resolve() {
    Map<String, Object> map = new HashMap<>();
    map.put("user", user());
    map.put("port", port());
    map.put("proxy", proxy());
    map.put("keyPath", keyPath());
    map.put("keyPassphrase", keyPassphrase());
    map.put("strictHostKeyChecking", strictHostKeyChecking());
    map.put("knownHostsPath", knownHostsPath());
    map.put("options", options());
    return map;
  }
}
