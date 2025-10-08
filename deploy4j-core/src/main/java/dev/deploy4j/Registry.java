package dev.deploy4j;

public class Registry {
  private final Context context;

  public Registry(Context context) {
    this.context = context;
  }

  public void login() {

    for (Host host : context.hosts()) {

      host.execute( context.registryCommands().login() );

    }

  }
}
