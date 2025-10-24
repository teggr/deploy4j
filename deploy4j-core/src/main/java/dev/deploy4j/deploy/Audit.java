package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.AuditorHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;

public class Audit extends Base {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Audit.class);

  private final AuditorHostCommands audit;

  public Audit(SshHosts sshHosts, AuditorHostCommands audit) {
    super(sshHosts);
    this.audit = audit;
  }

  /**
   * Show audit log from servers
   */
  public void audit(DeployContext deployContext) {
    on(deployContext.hosts(), host -> {
      log.info(host.capture(audit.reveal()));
    });
  }

}
