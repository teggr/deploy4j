package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.ssh.SshHost;

public class Prune {

  private final Cli cli;
  private final Commander commander;

  public Prune(Cli cli, Commander commander) {
    this.cli = cli;
    this.commander = commander;
  }

  public void all() {
    containers();
//    images();
  }

  private void containers() {

    int retain = commander.getConfig().retainContainer();

    for (SshHost host : cli.on( commander.hosts() ) ) {
      // audit records
      host.execute( commander.prune().appContainers(retain) );
      host.execute( commander.prune().healthcheckContainers() );
    }

  }
}
