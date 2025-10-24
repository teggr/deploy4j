package dev.deploy4j.deploy.host.ssh;

import dev.deploy4j.deploy.configuration.Ssh;
import dev.rebelcraft.cmd.Cmd;
import dev.rebelcraft.ssh.ExecResult;
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
    return execute(cmd, true);
  }

  public boolean execute(Cmd cmd, boolean raiseOnNonZeroExit) {
    String command = command(cmd);
    ExecResult result = sshTemplate
      .exec(command);
    boolean zeroExit = result.exitStatus() == 0;
    if (!zeroExit && raiseOnNonZeroExit) {
      throw new RuntimeException("Command failed on host " + hostName + ": " + command + " with error " + result.execErrorOutput());
    }
    return zeroExit;
  }

  private String command(Cmd command) {
    log.info("cmd___: {}", command.description());
    return Stream.of(command)
      .flatMap(cmd -> cmd.build().stream())
      .collect(Collectors.joining(" "));
  }

  public String capture(Cmd cmd) {
    return capture(cmd, true);
  }

  public String capture(Cmd cmd, boolean raiseOnNonZeroExit) {
    String command = command(cmd);
    ExecResult result = sshTemplate
      .exec(command);
    boolean zeroExit = result.exitStatus() == 0;
    if (!zeroExit && raiseOnNonZeroExit) {
      throw new RuntimeException("Command failed on host " + hostName + ": " + command + " with error " + result.execErrorOutput());
    }
    return result
      .execOutput();
  }

  public String capture(String cmd) {
    return capture(cmd, true);
  }

  public String capture(String cmd, boolean raiseOnNonZeroExit) {
    ExecResult result = sshTemplate
      .exec(cmd);
    boolean zeroExit = result.exitStatus() == 0;
    if (!zeroExit && raiseOnNonZeroExit) {
      throw new RuntimeException("Command failed on host " + hostName + ": " + cmd + " with error " + result.execErrorOutput());
    }
    return result
      .execOutput();
  }

  public void upload(String content, String remote, int mode) {
    sshTemplate.upload(content, remote);
  }

  public void copy(String local, String remote, int mode) {
    sshTemplate.copy(local, remote);
  }

}
