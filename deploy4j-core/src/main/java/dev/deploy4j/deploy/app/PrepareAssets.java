package dev.deploy4j.deploy.app;

import dev.deploy4j.deploy.configuration.Role;
import dev.deploy4j.deploy.host.commands.AppHostCommands;
import dev.deploy4j.deploy.host.commands.AppHostCommandsFactory;
import dev.deploy4j.deploy.host.ssh.SshHost;

public class PrepareAssets {

  private final String host;
  private final Role role;
  private final SshHost sshHost;
  private final AppHostCommandsFactory apps;

  private AppHostCommands app;

  public PrepareAssets(String host, Role role, SshHost sshHost, AppHostCommandsFactory apps) {
    this.host = host;
    this.role = role;
    this.sshHost = sshHost;
    this.apps = apps;
  }

  public void run() {
    if(assets()) {

      sshHost.execute( app().extractAssets() );
      String oldVersion = sshHost.capture( app().currentRunningVersion() );
      sshHost.execute( app().syncAssetVolumes(oldVersion) );

    }
  }

  // private

  public AppHostCommands app() {
    if( app == null ) {
      app = apps.app(role(), host());
    }
    return app;
  }


  // attributes

  public String host() {
    return host;
  }

  public Role role() {
    return role;
  }

  public SshHost sshHost() {
    return sshHost;
  }

  // delegates

  public boolean assets() {
    return role().assets();
  }

}
