package dev.deploy4j.deploy.configuration;

import dev.deploy4j.deploy.raw.BootConfig;

public class Boot {

  private final BootConfig bootConfig;
  private final int hostCount;

  public Boot(Configuration config) {
    this.bootConfig = config.rawConfig().boot() != null
      ? config.rawConfig().boot()
      : new BootConfig();
    this.hostCount = config.allHosts().size();
    // TODO: validate
  }

  public Integer limit() {

    String limit = bootConfig().limit();
    if(limit.endsWith("%")) {
      return Math.max( hostCount() * Integer.parseInt(limit.substring(0, limit.length()-1)) / 100 , 1 );
    } else {
      return Integer.parseInt( limit );
    }

  }

  public String waiter() {
    return bootConfig().waiter();
  }

  // attributes

  public BootConfig bootConfig() {
    return bootConfig;
  }

  public int hostCount() {
    return hostCount;
  }

}
