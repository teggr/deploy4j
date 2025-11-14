package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.ssh.SshHost;
import dev.deploy4j.deploy.host.ssh.SshHosts;

import java.util.List;
import java.util.function.Consumer;

public class Base {

  private final SshHosts sshHosts;
  private final Hooks hooks;

  public Base(SshHosts sshHosts, Hooks hooks) {
    this.sshHosts = sshHosts;
    this.hooks = hooks;
  }

  // private

  public void on(List<String> hosts, Consumer<SshHost> block) {
    runHook("pre-connect");
    sshHosts.on(hosts, block);
  }

  public void runHook(String hookName) {
    hooks.runHook(hookName);
  }

}
