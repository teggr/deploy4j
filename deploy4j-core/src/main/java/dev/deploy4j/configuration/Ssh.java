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
    return StringUtils.isNotBlank(sshConfig.user()) ? ENV.lookup( sshConfig.user() ) : "root";
  }

  public String port() {
    return StringUtils.isNotBlank(sshConfig.port()) ? sshConfig.port() : "22";
  }

  public String privateKeyPath() {
    return ENV.lookup( sshConfig.privateKey() );
  }

  public String passphrase() {
    return ENV.lookup( sshConfig.privateKeyPassphrase() );
  }

  public boolean strictHostKeyChecking() {
    return sshConfig.strictHostChecking();
  }

}
