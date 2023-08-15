package com.robintegg.deploy4j.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

@Slf4j
@RequiredArgsConstructor
public class SshConnection implements AutoCloseable {

  private final Session session;

  @Override
  public void close() throws Exception {
    session.disconnect();
  }

  @SneakyThrows
  public SshCommandResult executeCommand(String command) {

    log.info("executing command: {}", command);

    ChannelExec channel = null;

    try {
      channel = (ChannelExec) session.openChannel("exec");

      channel.setCommand(command);
      channel.setInputStream(null); // this is from the command

      ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
      channel.setErrStream(errorStream);

      ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
      channel.setOutputStream(responseStream);

      channel.connect(); // flushes the command?

      while (channel.isConnected()) { // wait for finish
        Thread.sleep(100);
      }

      String responseString = new String(responseStream.toByteArray()).trim();
      log.info("output: {}", responseString);

      String errorString = new String(errorStream.toByteArray()).trim();
      log.info("error: {}", errorString);

      int exitStatus = channel.getExitStatus();
      log.info("exit: {}", channel.getExitStatus());

      return new SshCommandResult(exitStatus, responseString, errorString);

    } finally {
      if (channel != null) {
        channel.disconnect();
      }
    }

  }

  @SneakyThrows
  public void pushFile(String from, String to, String fileName) {
    log.info("pushing file {} from {} to {}", fileName, from, to);
    Scp.copyLocalToRemote(session, from, to, fileName);
  }
}