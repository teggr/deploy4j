package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.configuration.Accessory;
import dev.deploy4j.configuration.Role;

public class Env extends Base {

  public Env(Cli cli, Commander commander) {
    super(cli, commander);
  }

  /**
   * Push the env file to the remote hosts
   */
  public void push() {

    withLock(() -> {

      on(commander().hosts(), host -> {

        host.execute(commander().auditor().record("Pushed env files"));

        for (Role role : commander().rolesOn(host.hostName())) {

          host.execute(commander().app(role, host.hostName()).makeEnvDirectory());
          host.upload(role.env(host.hostName()).secretsIO(), role.env(host.hostName()).secretsFile(), 400);

        }

      });

      on(commander().traefikHosts(), host -> {

        host.execute(commander().traefik().makeEnvDirectory());
        host.upload(commander().traefik().env().secretsIO(), commander().traefik().env().secretsFile(), 400);

      });

      on(commander().accessoryHosts(), host -> {

        for (String accessory : commander().accessoriesOn(host.hostName())) {

          Accessory accesssoryConfig = commander().config().accessory(accessory);
          host.execute(commander().accessory(accessory).makeEnvDirectory());
          host.upload(accesssoryConfig.env().secretsIO(), accesssoryConfig.env().secretsFile(), 400);

        }

      });

    });

  }

  /**
   * Delete the env file from the remote hosts
   */
  public void delete() {

    withLock(() -> {

      on(commander().hosts(), host -> {

        host.execute(commander().auditor().record("Deleted env files"));

        for (Role role : commander().rolesOn(host.hostName())) {

          host.execute(commander().app(role, host.hostName()).removeEnvFile());

        }

      });

      on(commander().traefikHosts(), host -> {

        host.execute(commander().traefik().removeEnvFile());

      });

      on(commander().accessoryHosts(), host -> {

        for (String accessory : commander().accessoriesOn(host.hostName())) {

          Accessory accessoryCOnfig = commander().config().accessory(accessory);
          host.execute(commander().accessory(accessory).removeEnvFile());

        }

      });

    });

  }

}
