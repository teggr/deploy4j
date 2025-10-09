package dev.deploy4j.ssh;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Ssh;

public class SshHost {

  private final String hostName;
  private final SSHTemplate sshTemplate;

  public SshHost(String hostName, Ssh ssh) {
    this.hostName = hostName;
    this.sshTemplate = new SSHTemplate(
      hostName,
      ssh.user(),
      ssh.port(),
      ssh.privateKeyPath(),
      ssh.passphrase(),
      ssh.strictHostKeyChecking()
    );
  }

  public String hostName() {
    return hostName;
  }

  public void close() {
    sshTemplate.close();
  }

  public boolean execute(Cmd cmd) {
    return sshTemplate.exec(cmd).exitStatus() == 0;
  }

  public String capture(Cmd cmd) {
    return sshTemplate.exec(cmd).execOutput();
  }

}
