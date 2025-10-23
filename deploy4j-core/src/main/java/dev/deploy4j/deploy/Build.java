package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.BuilderHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;

import java.util.List;

public class Build extends Base {

  private final BuilderHostCommands builder;

  public Build(SshHosts sshHosts, BuilderHostCommands builder) {
    super(sshHosts);
    this.builder = builder;
  }

  /**
   * Pull app image from registry onto server
   */
  public void pull(Commander commander) {
    // TODO: mirror hosts
    pullOnHosts(commander, commander.hosts());
  }

  // private

  private void pullOnHosts(Commander commander, List<String> hosts) {

    on(hosts, host -> {

      host.execute(commander.auditor().record("Pulled image with version " + commander.config().version()));
      host.execute(builder.clean());
      host.execute(builder.pull());
      host.execute(builder.validateImage());

    });

  }
}
