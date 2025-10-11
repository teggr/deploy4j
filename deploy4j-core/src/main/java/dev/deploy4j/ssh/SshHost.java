package dev.deploy4j.ssh;

import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.Ssh;
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
      ssh.privateKeyPath(),
      ssh.passphrase(),
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

}
