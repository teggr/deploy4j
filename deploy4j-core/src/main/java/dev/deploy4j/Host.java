package dev.deploy4j;

import dev.deploy4j.configuration.Deploy4jConfig;
import dev.deploy4j.configuration.SshConfig;
import dev.deploy4j.ssh.SSHTemplate;

public class Host {

  private final String host;
  private final String role;
  private final Deploy4jConfig config;
  private final SSHTemplate sshTemplate;

  public Host(String host, String role, Deploy4jConfig config ) {
    this.host = host;
    this.role = role;
    this.config = config;
    this.sshTemplate = new SSHTemplate(host, config.ssh());
  }

  public String name() {
    return host;
  }

  public void close() {
    sshTemplate.close();
  }

  public boolean execute(Cmd cmd) {
    return sshTemplate.exec(cmd).exitStatus() == 0;
  }

  public boolean runningTraefik() {
    // if !role.traefik then is primary?
    // else return traefik
    return isPrimary();
  }

  private boolean isPrimary() {
    return role.equals(config.primaryRole());
  }

}
