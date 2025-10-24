package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import picocli.CommandLine;

@CommandLine.Command(
  name = "audit",
  description = "Show audit log from servers")
public class AuditCliCommand extends BaseCliCommand {

  @Override
  protected void execute(DeployApplicationContext deployApplicationContext) {
    deployApplicationContext.audit().audit(deployApplicationContext.commander());
  }

}
