package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.ssh.SshHost;

public class Traefik {

  private final Cli cli;
  private final Commander commander;

  public Traefik(Cli cli, Commander commander) {
    this.cli = cli;
    this.commander = commander;
  }

  /**
   * Boot Traefik on servers
   */
  public void boot() {

    for(SshHost host : cli.on( commander.traefikHosts() ) ) {

      host.execute( commander.registry().login() );
      host.execute( commander.traefik().startOrRun() );

    }

  }

}
