package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.cli.app.Boot;
import dev.deploy4j.configuration.Role;
import dev.deploy4j.ssh.SshHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
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

  /**
   * Start existing app container on servers
   */
  public void start() {

    for (SshHost host : cli.on( commander.hosts() ) ) {

      for(Role role : commander.rolesOn(host.hostName())) {

        host.execute( commander.auditor().record("Started app version " + commander.config().version() ) );
        host.execute( commander.app(role, host.hostName()).start() );

      }

    }

  }

  /**
   * Stop app container on servers
   */
  public void stop() {

    for (SshHost host : cli.on( commander.hosts() ) ) {

      for(Role role : commander.rolesOn(host.hostName())) {

        host.execute( commander.auditor().record("Stopped app") );
        host.execute( commander.app(role, host.hostName()).stop() );

      }

    }

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
   * Execute a custom command on servers within the app container (use --help to show options)
   *
   * @param interactive Execute command over ssh for an interactive shell (use for console/bash)
   * @param reuse Reuse currently running container instead of starting a new one
   * @param env Set environment variables for the command
   */
  public void exec(boolean interactive, boolean reuse, Map<String, String> env, String cmd) {

    // TODO: all the interactive stuff. we are reusing

    System.out.println( "Get most recent version available as an image...");
    String version = versionOrLatest();

    System.out.println("Launching command with version "+version+ " from existing container...");

    for (SshHost host : cli.on( commander.hosts() ) ) {

      for(Role role : commander.rolesOn(host.hostName())) {

        host.execute( commander.auditor().record("Executed cmd '"+cmd+"' on app version " + version ) );
        System.out.println( host.capture( commander.app(role, host.hostName()).executeInExistingContainer(cmd ,env) ) );

      }

    }
  }

  /**
   * Show app containers on servers
   */
  public void containers() {

    for (SshHost host : cli.on( commander.hosts() ) ) {

      System.out.println( host.capture( commander.app( null, host.hostName() ).listContainers() ) );

    }

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
   * Show app images on servers
   */
  public void images() {

    for (SshHost host : cli.on( commander.hosts() ) ) {

      System.out.println( host.capture( commander.app( null, host.hostName() ).listImages() ) );

    }

  }

  /**
   * Show log lines from app on servers (use --help to show options)
   *
   * @param since Show logs since timestamp (e.g. 2013-01-02T13:23:37Z) or relative (e.g. 42m for 42 minutes)
   * @param lines Number of log lines to pull from each server
   * @param grep Show lines with grep match only (use this to fetch specific requests by id)
   * @param grepOptions Additional options supplied to grep
   * @param follow Follow logs on primary server (or specific host set by --hosts)
   */
  public void logs(
    String since,
    Integer lines,
    String grep,
    String grepOptions,
    boolean follow
  ) {

    // TODO: follow
    if(lines != null || ( since != null || grep != null )) {

    } else {
      lines = 100;
    }

    for (SshHost host : cli.on( commander.hosts() ) ) {

      for (Role role : commander.rolesOn(host.hostName())) {

        System.out.println(host.capture(commander.app(role, host.hostName()).logs(null, since, lines != null ? lines.toString() : null, grep, grepOptions)));

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
   * Remove app container with given version from servers
   */
  public void removeContainer(String version) {

    for (SshHost host : cli.on( commander.hosts() ) ) {

      for(Role role : commander.rolesOn(host.hostName())) {

        host.execute( commander.auditor().record("Removed app container with version " + version ) );
        host.execute( commander.app(role, host.hostName()).removeContainer(version) );

      }

    }

  }


  /**
   * Remove all app containers from servers
   */
  public void removeContainers() {

    for (SshHost host : cli.on( commander.hosts() ) ) {

      for(Role role : commander.rolesOn(host.hostName())) {

        host.execute( commander.auditor().record("Removed all app containers") );
        host.execute( commander.app(role, host.hostName()).removeContainers() );

      }

    }

  }


  /**
   * Remove all app images from servers
   */
  public void removeImages() {

    for (SshHost host : cli.on( commander.hosts() ) ) {

      for(Role role : commander.rolesOn(host.hostName())) {

        host.execute( commander.auditor().record("Removed all app images") );
        host.execute( commander.app(role, host.hostName()).removeImages() );

      }

    }

  }

  /**
   * Show app version currently running on servers
   */
  public void version() {

    for (SshHost host : cli.on( commander.hosts() ) ) {

      Role role = commander.rolesOn(host.hostName()).getFirst();

      System.out.println( host.capture( commander.app(role, host.hostName()).currentRunningVersion() ) );

    }

  }

  private String versionOrLatest() {
    return commander.config().version() != null ? commander.config().version() : commander.config().latestTag();
  }

}
