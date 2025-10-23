package dev.deploy4j.deploy.app;

import dev.deploy4j.deploy.DeployContext;
import dev.deploy4j.deploy.host.commands.AppHostCommandsFactory;
import dev.deploy4j.deploy.utils.RandomHex;
import dev.deploy4j.deploy.healthcheck.Barrier;
import dev.deploy4j.deploy.healthcheck.Poller;
import dev.deploy4j.deploy.host.commands.AppHostCommands;
import dev.deploy4j.deploy.host.commands.AuditorHostCommands;
import dev.deploy4j.deploy.configuration.Role;
import dev.deploy4j.deploy.host.ssh.SshHost;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Boot {

  private static final Logger log = LoggerFactory.getLogger(Boot.class);

  private final String host;
  private final Role role;
  private final SshHost sshHost;
  private final String version;
  private final Barrier barrier;
  private final DeployContext deployContext;
  private final AuditorHostCommands audit;
  private final AppHostCommandsFactory apps;

  private AppHostCommands app;

  public Boot(String host, Role role, SshHost sshHost, String version, Barrier barrier, DeployContext deployContext, AuditorHostCommands audit, AppHostCommandsFactory apps) {
    this.host = host;
    this.role = role;
    this.sshHost = sshHost;
    this.version = version;
    this.barrier = barrier;
    this.deployContext = deployContext;
    this.audit = audit;
    this.apps = apps;
  }

  public void run() {

    String oldVersion = oldVersionRenamedIfClashing();

    if (queuer()) {
      waitAtBarrier();
    }

    // start_new_version
    try {
      startNewVersion();
    } catch (RuntimeException e) {
      if (gatekeeper()) {
        closeBarrier();
      }
      stopNewVersion();
      throw e;
    }

    if (gatekeeper()) {
      releaseBarrier();
    }

    // release barrier
    if (StringUtils.isNotBlank(oldVersion)) {
      stopOldVersion(oldVersion);
    }

  }

  // private

  private String oldVersionRenamedIfClashing() {
    String containerIdForVersion = sshHost().capture(app().containerIdForVersion(version()));
    if (StringUtils.isNotBlank(containerIdForVersion)) {
      String renamedVersion = version() + "_replaced_" + RandomHex.randomHex(8);
      log.info( "Renaming container {} to {} as already deployed on {}", version(), renamedVersion, host() );
      audit("Renaming container " + version() + " to " + renamedVersion);
      sshHost().execute(app().renameContainer(version, renamedVersion));
    }

    return sshHost().capture(app().currentRunningVersion());
  }


  private void startNewVersion() {

    audit("Booted app version " + version());

    if(usesCord()) {
      sshHost().execute( app().tieCord( role().cordHostFile() ) );
    }

    // 1. Convert to string & truncate to 51 chars
    String prefix = host().length() > 51 ? host().substring(0, 51) : host();

    // 2. Remove trailing dots
    prefix = prefix.replaceAll("\\.+$", "");

    // 3. Append random hex (12 chars = 6 bytes)
    String suffix = RandomHex.randomHex(6);

    String hostName = prefix + "-" + suffix;

    sshHost().execute(app().run(hostName));

    new Poller(deployContext).waitForHealthy(true, () -> sshHost().capture( app().status(version()) ) );

  }

  private void stopNewVersion() {
    sshHost().execute(app().stop(version()));
  }

  private void stopOldVersion(String version) {

    if(usesCord()) {
      String cord = sshHost().capture(app().cord(version));
      if(StringUtils.isNotBlank(cord)) {
        sshHost().execute( app().cutCord(cord) );
        new Poller(deployContext).waitForUnhealthy(true, () -> sshHost().capture( app().status(version()) ) );
      }
    }

    sshHost().execute(app().stop(version));

    if(assets()) {
      sshHost().execute(app().cleanUpAssets());
    }

  }

  private void releaseBarrier() {
    if (barrier().open()) {
      log.info("First " + deployContext.primaryRole() + " container is healthy on " + host + ", booting any other roles");
    }
  }

  private void waitAtBarrier() {

    try {
      log.info("Waiting for the first healthy " + deployContext.primaryRole() + " container before booting " + role() + " on " + host + "...");
      barrier().waitFor();
      log.info("First " + deployContext.primaryRole() + " container is healthy, booting " + role() + " on " + host + "...");
    } catch (RuntimeException e) {
      log.info("First " + deployContext.primaryRole() + " container is unhealthy, not booting " + role() + " on " + host + "...");
      throw e;
    }

  }

  private void closeBarrier() {

    if (barrier().close()) {

      log.info("First " + deployContext.primaryRole() + " container is unhealthy on " + host + ", not booting any other roles");
      log.error(sshHost.capture(app().logs(version(), null, null, null, null)));
      log.error(sshHost.capture(app().containerHealthLog(version())));
    }

  }

  private boolean barrierRole() {
    return role() == deployContext.primaryRole();
  }

  private AppHostCommands app() {
    if (app == null) {
      app = apps.app(role(), host());
    }
    return app;
  }

  private void audit(String message) {
    sshHost().execute( audit.record(message) );
  }

  private boolean gatekeeper() {
    return barrier() != null && barrierRole();
  }

  public boolean queuer() {
    return barrier() != null && !barrierRole();
  }

  // attributes

  public String host() {
    return host;
  }

  public Role role() {
    return role;
  }

  public String version() {
    return version;
  }

  public Barrier barrier() {
    return barrier;
  }

  public SshHost sshHost() {
    return sshHost;
  }

  // delegates

  public boolean usesCord() {
    return role().usesCord();
  }

  public boolean assets() {
    return role().assets();
  }

  public boolean runningTraefik() {
    return role().runningTraefik();
  }

}
