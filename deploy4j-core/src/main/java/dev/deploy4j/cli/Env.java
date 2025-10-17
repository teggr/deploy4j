package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.configuration.Role;
import dev.deploy4j.ssh.SshHost;

public class Env {
  private final Cli cli;
  private final Commander commander;

  public Env(Cli cli, Commander commander) {
    this.cli = cli;
    this.commander = commander;
  }

  public void push() {

    for (SshHost host : cli.on(commander.hosts())) {

      host.execute( commander.auditor().record("Pushed env files") );

      for( Role role:  commander.rolesOn( host.hostName() ) ) {

        host.execute( commander.app(role, host.hostName()).makeEnvDirectory() );

      }

    }


  }
}
