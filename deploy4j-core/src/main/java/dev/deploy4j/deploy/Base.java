package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.ssh.SshHost;
import dev.deploy4j.deploy.host.ssh.SshHosts;

import java.util.List;
import java.util.function.Consumer;

public class Base {

  private final SshHosts sshHosts;

  public Base(SshHosts sshHosts) {
    this.sshHosts = sshHosts;
  }

  // private

  public void on(List<String> hosts, Consumer<SshHost> block) {
    sshHosts.on(hosts, block);
  }

}
