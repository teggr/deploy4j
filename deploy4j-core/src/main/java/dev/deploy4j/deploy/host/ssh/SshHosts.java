package dev.deploy4j.deploy.host.ssh;

import dev.deploy4j.deploy.configuration.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SshHosts implements AutoCloseable {

  private final Configuration config;

  private final Map<String, SshHost> sshHosts = new HashMap<>();

  public SshHosts(Configuration config) {
    this.config = config;
  }

  public SshHost host(String host) {
    SshHost sshHost = sshHosts.get(host);
    if (sshHost == null) {
      sshHost = new SshHost(host, config.ssh());
      sshHosts.put(host, sshHost);
    }
    return sshHost;
  }

  public void on(List<String> hosts, Consumer<SshHost> block) {

    hosts.stream()
      .map(this::host)
      .forEach(block);

  }

  @Override
  public void close() throws Exception {

    // shutdown
    sshHosts.values().forEach(SshHost::close);

  }

}
