package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.AuditorHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;

public class Audit extends Base {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Audit.class);

  private final AuditorHostCommands audit;

  public Audit(SshHosts sshHosts, Hooks hooks, LocalHost localHost, AuditorHostCommands audit) {
    super(sshHosts, hooks, localHost);
    this.audit = audit;
  }

  /**
   * Show audit log from servers
   */
  public void audit(DeployContext deployContext) {
    on(deployContext, deployContext.hosts(), host -> {
      log.info(host.capture(audit.reveal()));
    });
  }

}
