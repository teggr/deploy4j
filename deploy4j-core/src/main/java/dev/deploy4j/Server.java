package dev.deploy4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Server {

  private static final Logger log = LoggerFactory.getLogger(Server.class);

  private final Context context;

  public Server(Context context) {
    this.context = context;
  }

  /**
   * Set up Docker to run Kamal apps
   */
  public void bootstrap() {

    List<Host> missing = new ArrayList<>();

    for (Host host : context.hosts()) {

      if (!host.execute( context.dockerCmds().installed() ) ) {
        if (host.execute( context.dockerCmds().superUser() ) ) {
          log.info("Missing Docker on {}. Installing...", host.name());
          host.execute( context.dockerCmds().install() );
        } else {
          missing.add(host);
        }
      }

      host.execute( context.serverCmds().ensureRunDirectory() );

    }

    if (!missing.isEmpty()) {
      throw new RuntimeException("Docker is not installed on %s and can't be automatically installed without having root access and either `wget` or `curl`. Install Docker manually: https://docs.docker.com/engine/install/".formatted(missing.stream()
        .map(Host::name)
        .collect(Collectors.joining(", "))));
    }

  }

}
