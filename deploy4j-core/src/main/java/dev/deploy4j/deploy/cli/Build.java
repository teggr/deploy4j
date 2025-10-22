package dev.deploy4j.deploy.cli;

import dev.deploy4j.deploy.host.commands.BuilderHostCommands;

import java.util.List;

public class Build extends Base {

  private final BuilderHostCommands builder;

  public Build(Commander commander, BuilderHostCommands builder) {
    super(commander);
    this.builder = builder;
  }

  /**
   * Pull app image from registry onto server
   */
  public void pull() {
    // TODO: mirror hosts
    pullOnHosts(commander().hosts());
  }

  // private

  private void pullOnHosts(List<String> hosts) {

    on(hosts, host -> {

      host.execute(commander().auditor().record("Pulled image with version " + commander().config().version()));
      host.execute(builder.clean());
      host.execute(builder.pull());
      host.execute(builder.validateImage());

    });

  }
}
