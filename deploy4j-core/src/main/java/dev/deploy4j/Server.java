package dev.deploy4j;

import dev.deploy4j.ssh.SSHTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Server {

  private static final Logger log  = LoggerFactory.getLogger(Server.class);

  /**
   * Set up Docker to run Kamal apps
   */
  public void bootstrap( List<Host> hosts ) {

    List<Host> missing = new ArrayList<>();

    for (Host host : hosts) {

      if (!host.isDockerInstalled()) {
        if (host.isSuperUser()) {
          log.info("Missing Docker on " + host.name() + ". Installing...");
          host.installDocker();
        } else {
          missing.add(host);
        }
      }

      host.ensureRunDirectory();

    }

    if( !missing.isEmpty() ) {
      throw new RuntimeException("Docker is not installed on " + missing.stream()
        .map(Host::name)
        .collect(Collectors.joining(", ")) + " and can't be automatically installed without having root access and either `wget` or `curl`. Install Docker manually: https://docs.docker.com/engine/install/" );
    }


  }

}
