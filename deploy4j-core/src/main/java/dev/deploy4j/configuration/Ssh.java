package dev.deploy4j.configuration;

import dev.deploy4j.env.ENV;
import dev.deploy4j.raw.SshConfig;
import org.apache.commons.lang.StringUtils;

public class Ssh {

  private final SshConfig sshConfig;

  public Ssh(Configuration config) {
    this.sshConfig = config.rawConfig().ssh();
  }

  public SshConfig sshConfig() {
    return sshConfig;
  }

  public String user() {
    return StringUtils.isNotBlank(sshConfig.user()) ? lookup( sshConfig.user() ) : "root";
  }

  public String port() {
    return StringUtils.isNotBlank(sshConfig.port()) ? sshConfig.port() : "22";
  }

  public String privateKeyPath() {
    return lookup( sshConfig.privateKey() );
  }

  public String passphrase() {
    return lookup( sshConfig.privateKeyPassphrase() );
  }

  public boolean strictHostKeyChecking() {
    return sshConfig.strictHostChecking();
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
