package dev.deploy4j;

import java.util.List;
import java.util.stream.Stream;

public class App {
  private final Context context;

  public App(Context context) {
    this.context = context;
  }

  /**
   * Detect app stale containers
   */
  public void staleContainers() {
    staleContainers(false);
  }

  public void staleContainers(boolean stop) {

    for(Host host : context.hosts()) {

      AppCommands app = context.appCommands(host);
      List<String> versions = new java.util.ArrayList<>(Stream.of(host.capture(app.listVersions()).split("\n")).toList());
      versions.remove( host.capture( app.currentRunningVersion() ).trim() );

      for(String version : versions) {
        if( stop ) {
          // "Stopping stale container for role #{role} with version #{version}"
          host.execute( app.stop(version) );
        } else {
          // puts_by_host host,  "Detected stale container for role #{role} with version #{version} (use `kamal app stale_containers --stop` to stop)"
        }
      }

    }

  }

  public void boot() {

  }
}
