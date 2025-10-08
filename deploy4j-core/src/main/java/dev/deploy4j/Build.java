package dev.deploy4j;

import java.util.List;

public class Build {
  private final Context context;

  public Build(Context context) {
    this.context = context;
  }

  public void pull() {
    pullOnHosts(context.hosts());
  }

  private void pullOnHosts(List<Host> hosts) {
    for(Host host : hosts) {

      host.execute( context.builderCommands().clean() );
      host.execute( context.builderCommands().pull()  );
      host.execute( context.builderCommands().validateImage() );

    }
  }
}
