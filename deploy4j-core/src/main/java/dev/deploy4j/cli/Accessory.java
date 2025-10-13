package dev.deploy4j.cli;

import dev.deploy4j.Cmd;
import dev.deploy4j.Commander;
import dev.deploy4j.configuration.Role;
import dev.deploy4j.ssh.SshHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Accessory {

  private static final Logger log = LoggerFactory.getLogger(Accessory.class);


  private record AccessoryHosts(dev.deploy4j.configuration.Accessory accessory, List<String> hosts) {}

  private final Cli cli;
  private final Commander commander;

  public Accessory(Cli cli, Commander commander) {
    this.cli = cli;
    this.commander = commander;
  }

  public void boot() {

    boot(null, true);

  }

  /**
   * Boot new accessory service on host
   *
   * @param name (use NAME=all to boot all accessories)
   * @param login
   */
  public void boot(String name, boolean login) {

    if( "all".equalsIgnoreCase( name ) ) {

      commander.accessoryNames()
        .forEach(  accessoryName -> boot(accessoryName, login) );

    } else {

      AccessoryHosts accessoryHosts = withAccessory(name);

      directories(name);
      upload(name);

      for(SshHost host : cli.on(accessoryHosts.hosts()) ) {

        if(login) {
          host.execute(commander.registry().login());
        }
        // *KAMAL.auditor.record("Booted #{name} accessory"), verbosity: :debug
        host.execute(accessoryHosts.accessory().run());

      }

    }

  }

  /**
   * Upload accessory files to host
   */
  public void upload(String name) {
    AccessoryHosts accessoryHosts = withAccessory(name);
    for(SshHost host : cli.on(accessoryHosts.hosts()) ) {
      accessoryHosts.accessory().files().forEach((local, remote) -> {
        accessoryHosts.accessory().ensureLocalFilePresent(local);

        host.execute( cli.accesssory().makeDirectoryFor(remote) );
        host.upload( local, remote );
        host.execute( Cmd.cmd("chmod", "755", remote ) );

      });
    }
  }


  /**
   * Create accessory directories on host
   */
  public void directories(String name) {
    AccessoryHosts accessoryHosts = withAccessory(name);
    for(SshHost host : cli.on(accessoryHosts.hosts()) ) {
      for( String hostPath : accessoryHosts.accessory().directories().keySet() ) {
        host.execute(accessoryHosts.accessory().makeDirectory(hostPath));
      }
    }
  }

  /**
   * Reboot existing accessory on host (stop container, remove container, start new container; use NAME=all to boot all accessories)
   */
  private void reboot(String name) {

    if( "all".equalsIgnoreCase( name ) ) {

      commander.accessoryNames()
        .forEach(  accessoryName -> reboot(accessoryName) );

    } else {

      AccessoryHosts accessoryHosts = withAccessory(name);

      for(SshHost host : cli.on(accessoryHosts.hosts()) ) {

        host.execute(commander.registry().login() );

      }

      stop(name);
      removeContainer(name);
      boot(name, false);

    }

  }


  /**
   * Start existing accessory container on host
   */
  private void start(String name) {

    AccessoryHosts accessoryHosts = withAccessory(name);

    for(SshHost host : cli.on(accessoryHosts.hosts()) ) {

      host.execute(commander.auditor().record("Started " + name + " accessory"));
      host.execute(accessoryHosts.accessory().start());

    }

  }


  /**
   * Stop existing accessory container on host
   */
  private void stop(String name) {

    AccessoryHosts accessoryHosts = withAccessory(name);

    for(SshHost host : cli.on(accessoryHosts.hosts()) ) {

      host.execute(commander.auditor().record("Stopped " + name + " accessory"));
      host.execute(accessoryHosts.accessory().stop());

    }

  }

  /**
   * Restart existing accessory container on host
   */
  private void restart(String name) {

    AccessoryHosts accessoryHosts = withAccessory(name);

    stop(name);
    start(name);

  }

  /**
   * Show details about accessory on host (use NAME=all to show all accessories)
   */
  public void details(String name) {

    if( "all".equalsIgnoreCase( name ) ) {

      commander.accessoryNames()
        .forEach(  accessoryName -> details(accessoryName) );

    } else {

      String type = "Accessory " + name;
      AccessoryHosts accessoryHosts = withAccessory(name);

      for(SshHost host : cli.on(accessoryHosts.hosts()) ) {

        System.out.println( host.capture( accessoryHosts.accessory().info() ) );

      }

    }

  }

  /**
   * Execute a custom command on servers (use --help to show options)
   *
   * @param interactive Execute command over ssh for an interactive shell (use for console/bash)
   * @param reuse Reuse currently running container instead of starting a new one
   * @param name
   * @param cmd
   */
  public void exec(boolean interactive, boolean reuse, String name, String cmd) {

    // TODO: interactive and not reuse

    AccessoryHosts accessoryHosts = withAccessory(name);


    System.out.println( "Launching command from existing container..." );
    for (SshHost host : cli.on( accessoryHosts.hosts() ) ) {

     host.execute( commander.auditor().record( "Executed cmd '" + cmd + "' on "+name+" accessory" ) );
     host.capture( accessoryHosts.accessory().executeInExistingContainer( cmd ) );

    }

  }

  /**
   * Show log lines from accessory on host (use --help to show options)
   *
   * @param since Show logs since timestamp (e.g. 2013-01-02T13:23:37Z) or relative (e.g. 42m for 42 minutes)
   * @param lines Number of log lines to pull from each server
   * @param grep Show lines with grep match only (use this to fetch specific requests by id)
   * @param grepOptions Additional options supplied to grep
   * @param follow Follow logs on primary server (or specific host set by --hosts)
   */
  public void logs(
    String since,
    Integer lines,
    String grep,
    String grepOptions,
    boolean follow,
    String name
  ) {

    AccessoryHosts accessoryHosts = withAccessory(name);

    // TODO: follow
    if(lines != null || ( since != null || grep != null )) {

    } else {
      lines = 100;
    }

    for (SshHost host : cli.on( accessoryHosts.hosts() ) ) {

        System.out.println(host.capture(accessoryHosts.accessory().logs(since, lines != null ? lines.toString() : null, grep, grepOptions)));

    }

  }

  /**
   * Remove accessory container, image and data directory from host (use NAME=all to remove all accessories)
   */
  public void remove(String name) {

    if( "all".equalsIgnoreCase( name ) ) {

      commander.accessoryNames()
        .forEach(  accessoryName -> remove(accessoryName) );

    } else {

      removeAccessory(name);

    }

  }


  /**
   * Remove accessory container from host
   */
  private void removeContainer(String name) {

    AccessoryHosts accessoryHosts = withAccessory(name);

    for(SshHost host : cli.on(accessoryHosts.hosts()) ) {

      host.execute(commander.auditor().record("Remove " + name + " accessory container"));
      host.execute(accessoryHosts.accessory().removeContainer());

    }

  }


  /**
   * Remove accessory image from host
   */
  private void removeImage(String name) {

    AccessoryHosts accessoryHosts = withAccessory(name);

    for(SshHost host : cli.on(accessoryHosts.hosts()) ) {

      host.execute(commander.auditor().record("Removed " + name + " accessory image"));
      host.execute(accessoryHosts.accessory().removeImage());

    }

  }


  /**
   * Remove accessory directory used for uploaded files and data directories from host
   */
  private void removeServiceDirectory(String name) {

    AccessoryHosts accessoryHosts = withAccessory(name);

    for(SshHost host : cli.on(accessoryHosts.hosts()) ) {

      host.execute(commander.auditor().record("Removed " + name + " accessory image"));
      host.execute(accessoryHosts.accessory().removeServiceDirectory());

    }

  }

  private void removeAccessory(String name) {

    AccessoryHosts accessoryHosts = withAccessory(name);
    stop(name);
    removeContainer(name);
    removeImage(name);
    removeServiceDirectory(name);

  }

  private Cmd makeDirectoryFor(String remoteFile) {
    return makeDirectory( new File(remoteFile).getParent() );
  }

  private Cmd makeDirectory(String path) {
    return Cmd.cmd("mkdir", "-p", path );
  }

  private AccessoryHosts withAccessory(String name) {
    dev.deploy4j.configuration.Accessory accessory = commander.config().accessory(name);
    if( accessory != null ) {
      return new AccessoryHosts(accessory, accessoryHosts(accessory) );
    } else {
      errorOnMissingAccessory(name);
      return null;
    }
  }

  private List<String> accessoryHosts(dev.deploy4j.configuration.Accessory accessory) {
    if( !commander.specificHosts().isEmpty() ) {
      List<String> intersection = new ArrayList<>( commander.specificHosts() );
      intersection.retainAll( accessory.hosts() );
      return intersection;
    } else {
      return accessory.hosts();
    }
  }

  private void errorOnMissingAccessory(String name) {
    throw new RuntimeException( "No accessory by the name of '" + name + "'" );
  }

}
