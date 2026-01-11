package dev.deploy4j.cli;

import dev.deploy4j.init.GuidedInitializer;
import dev.deploy4j.init.Initializer;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
  name = "init",
  description = "Create config stub in config/deploy.yml and env stub in .deploy4j/secrets")
public class InitCliCommand implements Callable<Integer> {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @CommandLine.Option(names = "--bundle", description = "Add Deploy4j to the maven file", defaultValue = "false")
  private boolean bundle;

  @CommandLine.Option(names = "--guided", description = "Enter guided interactive mode to configure deploy.yml", defaultValue = "false")
  private boolean guided;

  @Override
  public Integer call() throws Exception {

    if (guided) {
      GuidedInitializer guidedInitializer = new GuidedInitializer();
      guidedInitializer.runGuidedInit(bundle);
    } else {
      Initializer initializer = new Initializer();
      initializer.init(bundle);
    }

    return 0;

  }


}
