package dev.deploy4j.deploy;

import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.local.LocalHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Hooks {

  private static final Logger log = LoggerFactory.getLogger(Hooks.class);

  private final LocalHost localHost;
  private final Configuration config;
  private final boolean skipHooks;

  public Hooks(LocalHost localHost, Configuration config, boolean skipHooks) {
    this.localHost = localHost;
    this.config = config;
    this.skipHooks = skipHooks;
  }

  public void runHook(DeployContext deployContext, String hook) {
    log.info("Hook {} started", hook);
    if( !skipHooks && hookExists(hook) ) {

      Map<String, String> details = Map.of(
        "details", String.join(",", deployContext.hosts())
      );

      log.info("Running the {} hook...", hook);
      try {
        localHost.execute( hookFile( hook ), Tags.fromConfig(config, details).env().tags() );
      } catch (Exception e) {
        throw new RuntimeException( "Hook " + hook + " failed: "+ e.getMessage(), e );
      }
    }

  }

  public boolean hookExists(String hook) {
    return localHost.hasFile( hookFile( hook ) );
  }

  public String hookFile(String hook) {
    return localHost.resolveScriptFile( config.hooksPath(), hook );
  }

}
