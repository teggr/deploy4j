package dev.deploy4j.deploy.cli;

import java.util.List;

public class Build extends Base {

  public Build(Commander commander) {
    super(commander);
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
      host.execute(commander().builder().clean());
      host.execute(commander().builder().pull());
      host.execute(commander().builder().validateImage());

    });

  }
}
