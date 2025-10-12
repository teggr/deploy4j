package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.cli.app.Boot;
import dev.deploy4j.configuration.Role;
import dev.deploy4j.ssh.SshHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

public class App {

  private static final Logger log = LoggerFactory.getLogger(App.class);

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
    return commander.config().version() != null ? commander.config().version() : commander.config().latestTag();
  }

  /**
   * Show details about app containers
   */
  public void details() {
    for (SshHost host : cli.on( commander.hosts() ) ) {

      for(Role role : commander.rolesOn(host.hostName())) {

        System.out.println( host.capture( commander.app(role, host.hostName()).info() ) );

      }

    }
  }

  /**
   * Remove app containers and images from servers
   */
  public void remove() {
    stop();
    removeContainers();
    removeImages();
  }

  /**
   * Remove all app images from servers
   */
  private void removeImages() {

    for (SshHost host : cli.on( commander.hosts() ) ) {

      for(Role role : commander.rolesOn(host.hostName())) {

        host.execute( commander.auditor().record("Removed all app images") );
        host.execute( commander.app(role, host.hostName()).removeImages() );

      }

    }

  }

  /**
   * Remove all app containers from servers
   */
  private void removeContainers() {

    for (SshHost host : cli.on( commander.hosts() ) ) {

      for(Role role : commander.rolesOn(host.hostName())) {

        host.execute( commander.auditor().record("Removed all app containers") );
        host.execute( commander.app(role, host.hostName()).removeContainers() );

      }

    }

  }

  /**
   * Stop app container on servers
   */
  private void stop() {

    for (SshHost host : cli.on( commander.hosts() ) ) {

      for(Role role : commander.rolesOn(host.hostName())) {

        host.execute( commander.auditor().record("Stopped app") );
        host.execute( commander.app(role, host.hostName()).stop() );

      }

    }

  }

}
