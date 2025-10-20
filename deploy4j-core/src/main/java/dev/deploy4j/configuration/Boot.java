package dev.deploy4j.configuration;

import dev.deploy4j.raw.BootConfig;

public class Boot {

  private final BootConfig bootConfig;
  private final int hostCount;

  public Boot(Configuration config) {
    this.bootConfig = config.rawConfig().boot() != null
      ? config.rawConfig().boot()
      : new dev.deploy4j.raw.BootConfig();
    this.hostCount = config.allHosts().size();
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
