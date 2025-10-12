package dev.deploy4j.cli.app;

import dev.deploy4j.Commander;
import dev.deploy4j.RandomHex;
import dev.deploy4j.commands.App;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.configuration.Role;
import dev.deploy4j.ssh.SshHost;
import org.apache.commons.lang.StringUtils;

public class Boot {

  private final Configuration config;
  private final SshHost host;
  private final Role role;
  private final String version;
  private final App app;

  public Boot(Commander commander, Configuration config, SshHost host, Role role, String version) {
    this.config = config;
    this.host = host;
    this.role = role;
    this.version = version;

    this.app = commander.app(role, host.hostName());

  }

  public void run() {

    // boot -> host, role, self, version, barrier
    // Kamal::Cli::App::Boot.new(host, role, self, version, barrier).run
    String oldVersion = oldVersionRenamedIfClashing();

    // wait_at_barrier

    // start_new_version
    try {
      startNewVersion();
    } catch (Exception e) {
      e.printStackTrace();
      stopNewVersion();
      throw e;
    }

    // release barrier
    if (StringUtils.isNotBlank(oldVersion)) {
      stopOldVersion(oldVersion);
    }

  }

  private void stopOldVersion(String version) {

    // cord///
    host.execute(app.stop(version));

    // app clean up assets

  }

  private void stopNewVersion() {
    host.execute(app.stop(version));
  }

  private void startNewVersion() {
    // 1. Convert to string & truncate to 51 chars
    String prefix = host.hostName().length() > 51 ? host.hostName().substring(0, 51) : host.hostName();

    // 2. Remove trailing dots
    prefix = prefix.replaceAll("\\.+$", "");

    // 3. Append random hex (12 chars = 6 bytes)
    String suffix = RandomHex.randomHex(6);

    String hostName = prefix + "-" + suffix;

    host.execute(app.run(hostName));

    // poller

  }

  private String oldVersionRenamedIfClashing() {
    String containerIdForVersion = host.capture(app.containerIdForVersion(version));
    if (StringUtils.isNotBlank(containerIdForVersion)) {
      String renamedVersion = version + "_replaced_" + RandomHex.randomHex(8);
      host.execute(app.renameContainer(version, renamedVersion));
    }

    // TODO: not yet returning the renamed version
    return host.capture(app.currentRunningVersion());
  }

}
