package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.Utils;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.raw.RegistryConfig;

import static dev.deploy4j.Utils.escapeShellValue;
import static dev.deploy4j.Utils.sensitive;

public class Registry extends Base {

  public Registry(Configuration config) {
    super(config);
  }

  public Cmd login() {
    return Cmd.cmd("docker login")
      .args(registry().server())
      .args("-u", sensitive(escapeShellValue(registry().username())))
      .args("-p", sensitive(escapeShellValue(registry().password())))
      .description("login");
  }

  public Cmd logout() {
    return Cmd.cmd("docker logout")
      .args(registry().server())
      .description("logout");
  }

  // delegate

  public dev.deploy4j.configuration.Registry registry() {
    return config().registry();
  }

}
