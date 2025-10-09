package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.ssh.SshHost;

public class Registry {

  private final Cli cli;
  private final Commander commander;

  public Registry(Cli cli, Commander commander) {
    this.cli = cli;
    this.commander = commander;
  }

  public void login() {

    for (SshHost host : cli.on(commander.hosts()) ) {

      // TODO: locally?
      host.execute( commander.registry().login() );

    }

  }

  public void logout() {

    for (SshHost host : cli.on(commander.hosts()) ) {

      // TODO: locally?
      host.execute( commander.registry().logout() );

    }

  }

}
