package dev.deploy4j.deploy.cli;

public class Registry extends Base {

  public Registry(Commander commander) {
    super(commander);
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
