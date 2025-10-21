package dev.deploy4j.commands;

import dev.deploy4j.configuration.Configuration;
import dev.rebelcraft.cmd.Cmd;

import static dev.deploy4j.Utils.escapeShellValue;
import static dev.deploy4j.Utils.sensitive;
import static dev.rebelcraft.cmd.pkgs.Docker.docker;

public class RegistryCommands extends BaseCommands {

  public RegistryCommands(Configuration config) {
    super(config);
  }

  public Cmd login() {
    return docker().login()
      .args(registry().server())
      .args("-u", sensitive(escapeShellValue(registry().username())))
      .args("-p", sensitive(escapeShellValue(registry().password())))
      .description("login");
  }

  public Cmd logout() {
    return docker().logout()
      .args(registry().server())
      .description("logout");
  }

  // delegate

  public dev.deploy4j.configuration.Registry registry() {
    return config().registry();
  }

}
