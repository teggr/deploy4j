package dev.deploy4j.deploy.cli;

import dev.deploy4j.deploy.host.commands.RegistryHostCommands;

public class Registry extends Base {

  private final RegistryHostCommands registry;

  public Registry(Commander commander, RegistryHostCommands registry) {
    super(commander);
    this.registry = registry;
  }

  /**
   * Log in to registry locally and remotely
   */
  public void login() {

    // TODO: locally?

    on(commander().hosts(), host -> {

      host.execute(registry.login());

    });

  }

  /**
   * Log out of registry locally and remotely
   */
  public void logout() {

    // TODO: locally?

    on(commander().hosts(), host -> {

      host.execute(registry.logout());

    });

  }

}
