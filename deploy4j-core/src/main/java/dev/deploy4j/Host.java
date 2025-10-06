package dev.deploy4j;

import dev.deploy4j.commands.DockerCommands;
import dev.deploy4j.commands.ServerCommands;
import dev.deploy4j.configuration.SshConfig;
import dev.deploy4j.ssh.ExecResult;
import dev.deploy4j.ssh.SSHTemplate;

public class Host {

  public static final String DEFAULT_RUN_DIRECTORY = ".deploy4j";
  private final String host;
  private final SSHTemplate sshTemplate;

  public Host(String host, SshConfig ssh) {
    this.host = host;
    this.sshTemplate = new SSHTemplate(host, ssh);
  }

  public boolean isDockerInstalled() {
    ExecResult result = sshTemplate.exec(DockerCommands.installed());
    return result.exitStatus() == 0;
  }

  public boolean isSuperUser() {
    ExecResult result = sshTemplate.exec(DockerCommands.isSuperUser());
    return result.exitStatus() == 0;
  }

  public String name() {
    return host;
  }

  public void installDocker() {
    ExecResult result = sshTemplate.exec(DockerCommands.install());
  }

  public void ensureRunDirectory() {
    sshTemplate.exec(ServerCommands.ensureRunDirectory(DEFAULT_RUN_DIRECTORY));
  }

  public void close() {
    sshTemplate.close();
  }
}
