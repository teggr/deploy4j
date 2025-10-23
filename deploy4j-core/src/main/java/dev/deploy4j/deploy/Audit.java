package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.AuditorHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;

public class Audit extends Base {

  private final AuditorHostCommands audit;

  public Audit(SshHosts sshHosts, AuditorHostCommands audit) {
    super(sshHosts);
    this.audit = audit;
  }

  /**
   * Show audit log from servers
   */
  public void audit(Commander commander) {
    on(commander.hosts(), host -> {
      System.out.println(host.capture(audit.reveal()));
    });
  }

}
