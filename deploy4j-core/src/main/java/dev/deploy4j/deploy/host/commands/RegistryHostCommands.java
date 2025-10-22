package dev.deploy4j.deploy.host.commands;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.configuration.Registry;
import dev.rebelcraft.cmd.Cmd;

import static dev.rebelcraft.cmd.CmdUtils.escapeShellValue;
import static dev.rebelcraft.cmd.CmdUtils.sensitive;
import static dev.rebelcraft.cmd.pkgs.Docker.docker;

public class RegistryHostCommands extends BaseHostCommands {

  public RegistryHostCommands(Configuration config) {
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

  public Registry registry() {
    return config().registry();
  }

}
