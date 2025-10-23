package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.ssh.SshHosts;

public class Audit extends Base {

  public Audit(SshHosts sshHosts) {
    super(sshHosts);
  }

  /**
   * Show audit log from servers
   */
  public void audit(Commander commander) {
    on(commander.hosts(), host -> {
      System.out.println(host.capture(commander.auditor().reveal()));
    });
  }

}
