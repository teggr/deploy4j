package dev.deploy4j.cli;

import dev.deploy4j.Commander;

public class Registry extends Base {

  public Registry(Cli cli, Commander commander) {
    super(cli, commander);
  }

  /**
   * Log in to registry locally and remotely
   */
  public void login() {

    // TODO: locally?

    on(commander().hosts(), host -> {

      host.execute(commander().registry().login());

    });

  }

  /**
   * Log out of registry locally and remotely
   */
  public void logout() {

    // TODO: locally?

    on(commander().hosts(), host -> {

      host.execute(commander().registry().logout());

    });

  }

}
