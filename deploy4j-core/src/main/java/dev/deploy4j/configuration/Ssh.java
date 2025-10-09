package dev.deploy4j.configuration;

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
    return StringUtils.isNotBlank(sshConfig.user()) ? sshConfig.user() : "root";
  }

  public String port() {
    return StringUtils.isNotBlank(sshConfig.port()) ? sshConfig.port() : "22";
  }

  public String privateKeyPath() {
    return sshConfig.privateKey();
  }

  public String passphrase() {
    return sshConfig.privateKeyPassphrase();
  }


  public boolean strictHostKeyChecking() {
    return sshConfig.strictHostChecking();
  }

}
