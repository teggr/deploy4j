package dev.deploy4j.cli;

import dev.deploy4j.Commander;
import dev.deploy4j.ssh.SshHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Server {

  private static final Logger log = LoggerFactory.getLogger(Server.class);

  private final Cli cli;
  private final Commander commander;

  public Server(Cli cli, Commander commander) {
    this.cli = cli;
    this.commander = commander;
  }

  /**
   * Run a custom command on the server (use --help to show options)
   *
   * @param interactive Run the command interactively (use for console/bash)
   */
  public void exec(boolean interactive, String cmd) {

    List<String> hosts = new ArrayList<>();
    hosts.addAll(commander.hosts());
    hosts.addAll(commander.accessoryHosts());

    // TODO: interactive mode
    System.out.println( "Running '"+cmd+"' on " + String.join(",", hosts) +  "..." );

    for(SshHost host : cli.on(hosts)) {
      host.execute( commander.auditor().record( "Executed cmd '" + cmd + "' on " + host.hostName() ) );
      System.out.println( host.capture( cmd ) );
    }

  }

  /**
   * Set up Docker to run Kamal apps
   */
  public void bootstrap() {

    List<SshHost> missing = new ArrayList<>();

    List<String> hosts = new ArrayList<>();
    hosts.addAll(commander.hosts());
    hosts.addAll(commander.accessoryHosts());

    for (SshHost host : cli.on(hosts)) {

      if (!host.execute( commander.docker().installed() ) ) {
        if (host.execute( commander.docker().superUser() ) ) {
          log.info("Missing Docker on {}. Installing...", host.hostName());
          host.execute( commander.docker().install() );
        } else {
          missing.add(host);
        }
      }

      host.execute( commander.server().ensureRunDirectory() );

    }

    if (!missing.isEmpty()) {
      throw new RuntimeException("Docker is not installed on %s and can't be automatically installed without having root access and either `wget` or `curl`. Install Docker manually: https://docs.docker.com/engine/install/".formatted(missing.stream()
        .map(SshHost::hostName)
        .collect(Collectors.joining(", "))));
    }

  }

}
