package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.AuditorHostCommands;
import dev.deploy4j.deploy.host.commands.BuilderHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;

import java.util.List;

public class Build extends Base {

  private final BuilderHostCommands builder;
  private final AuditorHostCommands audit;

  public Build(SshHosts sshHosts, Hooks hooks, LocalHost localHost, BuilderHostCommands builder, AuditorHostCommands audit) {
    super(sshHosts, hooks, localHost);
    this.builder = builder;
    this.audit = audit;
  }

  /**
   * Pull app image from registry onto server
   */
  public void pull(DeployContext deployContext) {
    // TODO: mirror hosts
    pullOnHosts(deployContext, deployContext.hosts());
  }

  // private

  private void pullOnHosts(DeployContext deployContext, List<String> hosts) {

    on(deployContext, hosts, host -> {

      host.execute(audit.record("Pulled image with version " + deployContext.config().version()));
      host.execute(builder.clean(), false);
      host.execute(builder.pull());
      host.execute(builder.validateImage());

    });

  }
}
