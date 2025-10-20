package dev.deploy4j.configuration;

import dev.deploy4j.raw.SshConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ssh {

  private final SshConfig sshConfig;

  public Ssh(Configuration config) {
    this.sshConfig = config.rawConfig().ssh() != null ?
      config.rawConfig().ssh() : new SshConfig();
  }

  public String user() {
    return sshConfig.user() != null ?
      sshConfig.user() : "root";
  }

  public Integer port() {
    return sshConfig.port() != null ?
      sshConfig.port() : 22;
  }

  // TODO: proxy

  public Boolean keysOnly() {
    return sshConfig().keysOnly();
  }

  public List<String> keys() {
    return sshConfig().keys();
  }

  public List<String> keyData() {
    return sshConfig().keyData();
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
    if (keysOnly() != null) {
      options.put("keys_only", keysOnly().toString());
    }
    if (keys() != null) {
      options.put("keys", keys().toString());
    }
    if (keyData() != null) {
      options.put("key_data", keyData().toString());
    }
    return options;
  }

//  public String privateKeyPath() {
//    return lookup(sshConfig.privateKey());
//  }
//
//  public String passphrase() {
//    return lookup(sshConfig.privateKeyPassphrase());
//  }
//
//  public boolean strictHostKeyChecking() {
//    return sshConfig.strictHostChecking();
//  }
//
//  private String lookup(String key) {
//    // array of lookups or direct value
//    String fetched = ENV.fetch(key);
//    if (fetched == null) {
//      return key;
//    } else {
//      return fetched;
//    }
//  }

  // private

  private String logger() {
    // TODO: LOGGER
    return null;
  }

  private String logLevel() {
    return sshConfig().logLevel();
  }

  // attributes

  public SshConfig sshConfig() {
    return sshConfig;
  }

}
