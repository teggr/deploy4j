package dev.deploy4j;

import dev.deploy4j.ssh.SshHost;

public class AppInstance {
  private final SshHost host;

  public AppInstance(SshHost host) {
    this.host = host;
  }
}
