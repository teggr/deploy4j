package dev.deploy4j.cli;

import dev.deploy4j.Cmd;
import dev.deploy4j.Commander;
import dev.deploy4j.ssh.SshHost;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Accessory {

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

  private Cmd makeDirectoryFor(String remoteFile) {
    return makeDirectory( new File(remoteFile).getParent() );
  }

  private Cmd makeDirectory(String path) {
    return Cmd.cmd("mkdir", "-p", path );
  }

  /**
   * Create accessory directories on host
   */
  private void directories(String name) {
    AccessoryHosts accessoryHosts = withAccessory(name);
    for(SshHost host : cli.on(accessoryHosts.hosts()) ) {
      for( String hostPath : accessoryHosts.accessory().directories().keySet() ) {
          host.execute(accessoryHosts.accessory().makeDirectory(hostPath));
      }
    }
  }

  private AccessoryHosts withAccessory(String name) {
    dev.deploy4j.configuration.Accessory accessory = commander.getConfig().accessory(name);
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
