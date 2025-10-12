package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.ssh.SshHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  public void details() {

    for( SshHost host : cli.on( commander.traefikHosts() ) ) {

      System.out.println( host.capture( commander.traefik().info() ) );

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
   * Remove Traefik image from servers
   */
  private void removeImage() {

    for (SshHost host : cli.on( commander.traefikHosts() ) ) {

      host.execute( commander.auditor().record("Removed traefik image") );
      host.execute( commander.traefik().removeImage() );

    }

  }

  /**
   * Remove Traefik container from servers
   */
  private void removeContainer() {

    for(SshHost host : cli.on( commander.traefikHosts() ) ) {

      host.execute( commander.auditor().record("Removed traefik container") );
      host.execute( commander.traefik().removeContainer() );

    }

  }

  /**
   * Stop existing Traefik container on servers
   */
  private void stop() {

    for(SshHost host : cli.on( commander.traefikHosts() ) ) {

      host.execute( commander.auditor().record("Stopped traefik") );
      host.execute( commander.traefik().stop() );

    }

  }
}
