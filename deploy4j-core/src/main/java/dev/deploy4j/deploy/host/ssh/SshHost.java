package dev.deploy4j.deploy.host.ssh;

import dev.rebelcraft.cmd.Cmd;
import dev.deploy4j.deploy.configuration.Ssh;
import dev.rebelcraft.ssh.SSHTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SshHost {

  private static final Logger log = LoggerFactory.getLogger(SshHost.class);

  private final String hostName;
  private final SSHTemplate sshTemplate;

  public SshHost(String hostName, Ssh ssh) {
    this.hostName = hostName;
    this.sshTemplate = new SSHTemplate(
      hostName,
      ssh.user(),
      ssh.port(),
      ssh.keyPath(),
      ssh.keyPassphrase(),
      ssh.strictHostKeyChecking()
    );
  }

  public String hostName() {
    return hostName;
  }

  public void close() {
    sshTemplate.close();
  }

  public boolean execute(Cmd cmd) {
    return sshTemplate
             .exec(command(cmd)).exitStatus() == 0;
  }

  private String command(Cmd command) {
    log.info("cmd___: {}", command.description());
    return Stream.of(command)
      .flatMap(cmd -> cmd.build().stream())
      .collect(Collectors.joining(" "));
  }

  public String capture(Cmd cmd) {
    return sshTemplate
      .exec(command(cmd))
      .execOutput();
  }

  public String capture(String cmd) {
    return sshTemplate
      .exec(cmd)
      .execOutput();
  }

  public void upload(String local, String remote, int mode) {
    sshTemplate.upload(local, remote);
  }
}
