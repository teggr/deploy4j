package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.cli.app.Boot;
import dev.deploy4j.configuration.Role;
import dev.deploy4j.ssh.SshHost;

import java.util.List;
import java.util.stream.Stream;

public class App {

  private final Cli cli;
  private final Commander commander;

  public App(Cli cli, Commander commander) {
    this.cli = cli;
    this.commander = commander;
  }

  /**
   * Detect app stale containers
   */
  public void staleContainers() {
    staleContainers(false);
  }

  public void staleContainers(boolean stop) {

    for(SshHost host : cli.on( commander.hosts() ) ) {

      List<Role> roles = commander.rolesOn(host.hostName());

      for (Role role : roles) {

        dev.deploy4j.commands.App app = commander.app(role, host.hostName());
        List<String> versions = new java.util.ArrayList<>(Stream.of(host.capture(app.listVersions()).split("\n")).toList());
        versions.remove( host.capture( app.currentRunningVersion() ).trim() );

        for(String version : versions) {
          if( stop ) {
            // "Stopping stale container for role #{role} with version #{version}"
            host.execute( app.stop(version) );
          } else {
            // puts_by_host host,  "Detected stale container for role #{role} with version #{version} (use `kamal app stale_containers --stop` to stop)"
          }
        }

      }

    }

  }

  /**
   * Boot app on servers (or reboot app if already running)
   */
  public void boot() {

    // "Get most recent version available as an image..."
    String version = versionOrLatest();

    // say "Start container with version #{version} using a #{KAMAL.config.readiness_delay}s readiness delay (or reboot if already running)..."
    // do assets

    // barrier
    for(SshHost host : cli.on( commander.hosts() ) ) {

      List<Role> roles = commander.rolesOn(host.hostName());

      for (Role role : roles) {

        Boot appBoot = commander.boot(host, role, version);
        appBoot.run();

      }

    }

    for(SshHost host : cli.on( commander.hosts() ) ) {

      // execute audit tagging
      host.execute( commander.app(null, null)
        .tagLatestImage() );

    }

  }

  private String versionOrLatest() {
    return commander.getConfig().version() != null ? commander.getConfig().version() : commander.getConfig().latestTag();
  }
}
