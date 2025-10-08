package dev.deploy4j.config;

import dev.deploy4j.Context;
import dev.deploy4j.Host;

import java.util.Map;

public class Traefik {

  private final Context context;

  public Traefik(Context context) {
    this.context = context;
  }

  /**
   * Boot Traefik on servers
   */
  public void boot() {

    for(Host host : context.traefikHosts()) {

      host.execute( context.registryCommands().login() );
      host.execute( context.traefikCommands().startOrRun() );

    }

  }

}
