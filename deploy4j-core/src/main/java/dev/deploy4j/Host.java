package dev.deploy4j;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Host {

  public record ExecResult(int exitStatus, String execOutput, String execErrorOutput) {
  }

  private final Session session;

  public Host(Session session) {
    this.session = session;
  }

  public ExecResult exec(Cmd command) throws JSchException, IOException {

    int exitStatus;
    ByteArrayOutputStream capturedErrorStream = new ByteArrayOutputStream();
    ByteArrayOutputStream capturedInputStream = new ByteArrayOutputStream();

    ChannelExec channel = null;
    try {

      channel = (ChannelExec) session.openChannel("exec");
      String collect = Stream.of(command).flatMap(cmd -> cmd.build().stream()).collect(Collectors.joining(" "));
      System.out.println("Executing: " + collect + "\n");
      channel.setCommand(collect);
      channel.setInputStream(null);
      channel.setErrStream(capturedErrorStream);

      InputStream in = channel.getInputStream();
      channel.connect();
      IOUtils.copy(in, capturedInputStream);

      // Wait until the channel is closed
      while (!channel.isClosed()) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
        }
      }

      exitStatus = channel.getExitStatus();

    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (channel != null) {
        try {
          channel.disconnect();
        } catch (Exception e) {
        }
      }
    }

    ExecResult exec = new ExecResult(exitStatus, capturedInputStream.toString(StandardCharsets.UTF_8), capturedErrorStream.toString(StandardCharsets.UTF_8));

    System.out.println("Output: " + exec.execOutput);
    System.out.println("Error Output: " + exec.execErrorOutput);
    System.out.println("Exit status: " + exec.exitStatus);

    return exec;

  }

}
