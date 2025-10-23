package dev.deploy4j.deploy;

import dev.deploy4j.deploy.host.commands.DockerHostCommands;
import dev.deploy4j.deploy.host.commands.ServerHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHost;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Server extends Base {

  private static final Logger log = LoggerFactory.getLogger(Server.class);
  private final LockManager lockManager;
  private final DockerHostCommands docker;
  private final ServerHostCommands server;

  public Server(SshHosts sshHosts, LockManager lockManager, DockerHostCommands docker, ServerHostCommands server) {
    super(sshHosts);
    this.lockManager = lockManager;
    this.docker = docker;
    this.server = server;
  }

  /**
   * Run a custom command on the server (use --help to show options)
   *
   * @param interactive Run the command interactively (use for console/bash)
   */
  public void exec(Commander commander, boolean interactive, String cmd) {

    List<String> hosts = new ArrayList<>();
    hosts.addAll(commander.hosts());
    hosts.addAll(commander.accessoryHosts());

    // TODO: interactive mode
    System.out.println( "Running '"+cmd+"' on " + String.join(",", hosts) +  "..." );

    on(hosts, host -> {

      host.execute( commander.auditor().record( "Executed cmd '" + cmd + "' on " + host.hostName() ) );
      System.out.println( host.capture( cmd ) );

    });

  }

  /**
   * Set up Docker to run Kamal apps
   */
  public void bootstrap(Commander commander) {

    lockManager.withLock(commander, () -> {

      List<SshHost> missing = new ArrayList<>();

      List<String> hosts = new ArrayList<>();
      hosts.addAll(commander.hosts());
      hosts.addAll(commander.accessoryHosts());

      on(hosts, host -> {

        if (!host.execute( docker.installed() ) ) {
          if (host.execute( docker.superUser() ) ) {
            log.info("Missing Docker on {}. Installing...", host.hostName());
            host.execute( docker.install() );
          } else {
            missing.add(host);
          }
        }

        host.execute( server.ensureRunDirectory() );

      });

      if (!missing.isEmpty()) {
        throw new RuntimeException("Docker is not installed on %s and can't be automatically installed without having root access and either `wget` or `curl`. Install Docker manually: https://docs.docker.com/engine/install/".formatted(missing.stream()
          .map(SshHost::hostName)
          .collect(Collectors.joining(", "))));
      }

    });

  }

}
