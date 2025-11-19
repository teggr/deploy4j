package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.RegistryHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;

public class Registry extends Base {

  private final RegistryHostCommands registry;

  public Registry(SshHosts sshHosts, Hooks hooks, LocalHost localHost, RegistryHostCommands registry) {
    super(sshHosts, hooks, localHost);
    this.registry = registry;
  }

  /**
   * Log in to registry locally and remotely
   */
  public void login(DeployContext deployContext) {

    // TODO: locally?

    on(deployContext, deployContext.hosts(), host -> {

      host.execute(registry.login());

    });

  }

  /**
   * Log out of registry locally and remotely
   */
  public void logout(DeployContext deployContext) {

    // TODO: locally?

    on(deployContext, deployContext.hosts(), host -> {

      host.execute(registry.logout());

    });

  }

}
