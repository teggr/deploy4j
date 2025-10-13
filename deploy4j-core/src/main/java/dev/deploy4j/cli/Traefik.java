package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.ssh.SshHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Traefik {

  private static final Logger log = LoggerFactory.getLogger(Traefik.class);

  private final Cli cli;
  private final Commander commander;

  public Traefik(Cli cli, Commander commander) {
    this.cli = cli;
    this.commander = commander;
  }

  /**
   * Boot Traefik on servers
   */
  public void boot() {

    for(SshHost host : cli.on( commander.traefikHosts() ) ) {

      host.execute( commander.registry().login() );
      host.execute( commander.traefik().startOrRun() );

    }

  }

  /**
   * Reboot Traefik on servers (stop container, remove container, start new container)
   *
   * @param rolling Reboot traefik on hosts in sequence, rather than in parallel
   */
  public void reboot(boolean rolling) {

    // TODO: seq vs parallel
    List<String> hosts = rolling ? commander.traefikHosts() : commander.traefikHosts();

    for(SshHost host : cli.on( hosts ) ) {

      host.execute( commander.auditor().record( "Rebooted traefik" ) );
      host.execute( commander.registry().login() );
      host.execute( commander.traefik().stop() );
      host.execute( commander.traefik().removeContainer() );
      host.execute( commander.traefik().run() );

    }

  }

  /**
   * Start existing Traefik container on servers
   */
  public void start() {

    for(SshHost host : cli.on( commander.traefikHosts() ) ) {

      host.execute( commander.auditor().record( "Started traefik" ) );
      host.execute( commander.traefik().start() );

    }

  }

  /**
   * Stop existing Traefik container on servers
   */
  public void stop() {

    for(SshHost host : cli.on( commander.traefikHosts() ) ) {

      host.execute( commander.auditor().record( "Stopped traefik" ) );
      host.execute( commander.traefik().stop() );

    }

  }

  /**
   * Restart existing Traefik container on servers
   */
  public void restart() {

    stop();
    start();

  }

  /**
   * Show details about Traefik container from servers
   */
  public void details() {

    for( SshHost host : cli.on( commander.traefikHosts() ) ) {

      System.out.println( host.capture( commander.traefik().info() ) );

    }

  }

  /**
   * Show log lines from Traefik on servers
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

    for (SshHost host : cli.on( commander.traefikHosts() ) ) {

      System.out.println( host.capture( commander.traefik().logs( since, lines != null ? lines.toString() : null, grep, grepOptions ) ) );

    }

  }

  /**
   * Remove Traefik container and image from servers
   */
  public void remove() {

    stop();
    removeContainer();
    removeImage();

  }


  /**
   * Remove Traefik container from servers
   */
  public void removeContainer() {

    for(SshHost host : cli.on( commander.traefikHosts() ) ) {

      host.execute( commander.auditor().record("Removed traefik container") );
      host.execute( commander.traefik().removeContainer() );

    }

  }

  /**
   * Remove Traefik image from servers
   */
  public void removeImage() {

    for (SshHost host : cli.on( commander.traefikHosts() ) ) {

      host.execute( commander.auditor().record("Removed traefik image") );
      host.execute( commander.traefik().removeImage() );

    }

  }

}
