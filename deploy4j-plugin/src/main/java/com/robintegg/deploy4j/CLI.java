package com.robintegg.deploy4j;

import com.robintegg.deploy4j.cli.CliCommand;
import com.robintegg.deploy4j.ssh.SSHClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class CLI implements AutoCloseable {

  private final SSHClient sshClient;

  @Override
  public void close() throws Exception {
    sshClient.close();
  }

  @SneakyThrows
  public int executeCommandForStatus(CliCommand cmd) {
    return sshClient.executeCommandForStatus(cmd.toCli());
  }

  @SneakyThrows
  public void executeCommand(CliCommand cmd) {
    sshClient.executeCommand(cmd.toCli());
  }

  @SneakyThrows
  public void copyFile(String from, String to, String fileName) {
    sshClient.scpLocalToRemote(from, to, fileName);
  }

}
