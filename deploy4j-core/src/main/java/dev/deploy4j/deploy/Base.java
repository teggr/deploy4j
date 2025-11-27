package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.ssh.SshHost;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Base {

  private final SshHosts sshHosts;
  private final Hooks hooks;
  private final LocalHost localHost;

  public Base(SshHosts sshHosts, Hooks hooks, LocalHost localHost) {
    this.sshHosts = sshHosts;
    this.hooks = hooks;
    this.localHost = localHost;
  }

  public void on(DeployContext deployContext, List<String> hosts, Consumer<SshHost> block) {
    // need to check if connected
    if(!deployContext.connected()) {
      runHook(deployContext, "pre-connect");
      deployContext.connected(true);
    }
    sshHosts.on(hosts, block);
  }

  public void runHook(DeployContext deployContext, String hookName) {
    hooks.runHook(deployContext, hookName);
  }


  public void runHook(DeployContext deployContext, String hookName, Map<String,String> extraDetails) {
    runHook(deployContext, hookName);
  }

  public void runLocally(Consumer<LocalHost> block) {
    localHost.on(block);
  }

}
