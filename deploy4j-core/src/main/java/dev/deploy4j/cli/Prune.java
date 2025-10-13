package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.ssh.SshHost;

public class Prune {

  private final Cli cli;
  private final Commander commander;

  public Prune(Cli cli, Commander commander) {
    this.cli = cli;
    this.commander = commander;
  }

  /**
   * Prune unused images and stopped containers
   */
  public void all() {
    containers();
    images();
  }

  /**
   * Prune unused images
   */
  public void images() {

    for (SshHost host : cli.on( commander.hosts() ) ) {
      host.execute( commander.auditor().record("Pruned images") );
      host.execute( commander.prune().danglingImages() );
      host.execute( commander.prune().taggedImages() );
    }

  }

  public void containers() {
    containers(null);
  }

  /**
   * Prune all stopped containers, except the last n (default 5)
   *
   * @param retain Number of containers to retain
   */
  public void containers( Integer retain ) {

    if(retain == null) {
      retain =  commander.config().retainContainer();
    }

    if(retain < 1) {
      throw new RuntimeException("retain must be at least 1");
    }

    for (SshHost host : cli.on( commander.hosts() ) ) {
      host.execute( commander.auditor().record("Pruned containers") );
      host.execute( commander.prune().appContainers(retain) );
      host.execute( commander.prune().healthcheckContainers() );
    }

  }
}
