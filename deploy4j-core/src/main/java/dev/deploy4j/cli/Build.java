package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.ssh.SshHost;

import java.util.List;

public class Build {

  private final Cli cli;
  private final Commander commander;

  public Build(Cli cli, Commander commander) {
    this.cli = cli;
    this.commander = commander;
  }

  /**
   * Pull app image from registry onto server
   */
  public void pull() {
    // TODO: mirror hosts
    pullOnHosts(commander.hosts());
  }

  private void pullOnHosts(List<String> hosts) {
    for (SshHost host : cli.on(hosts)) {
      // auditor record
      host.execute(commander.builder().clean());
      host.execute(commander.builder().pull());
      host.execute(commander.builder().validateImage());

    }
  }
}
