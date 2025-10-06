package dev.deploy4j.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import dev.deploy4j.Cmd;
import dev.deploy4j.configuration.SshConfig;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SSHTemplate {

  private static final Logger log = LoggerFactory.getLogger(SSHTemplate.class);

  private final String host;
  private final SshConfig ssh;

  private Session session;

  public SSHTemplate(String host, SshConfig ssh) {
    this.host = host;
    this.ssh = ssh;
  }

  private Session getSession() throws JSchException {
    if(session == null) {

      log.info("Creating session for {}@{}:{}", ssh.userName(), host, ssh.port());

      // setup ssh environment for host
      JSch jsch = new JSch();
      jsch.addIdentity(ssh.privateKey(), ssh.privateKeyPassphrase());

      // create a session
      session = jsch.getSession(ssh.userName(), host, ssh.port());
      if(!ssh.strictHostChecking()) {
        session.setConfig("StrictHostKeyChecking", "no");
      }
    }
    return session;
  }

  public ExecResult exec(Cmd command) {

    int exitStatus = -1;
    ByteArrayOutputStream capturedInputStream = new ByteArrayOutputStream();
    ByteArrayOutputStream capturedErrorStream = new ByteArrayOutputStream();

    ChannelExec channel = null;
    try {

      Session session = getSession();

      if(!session.isConnected()) {
        session.connect();
      }

      channel = (ChannelExec) session.openChannel("exec");
      String collect = Stream.of(command).flatMap(cmd -> cmd.build().stream()).collect(Collectors.joining(" "));
      log.info("exec__: {}", collect);
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

    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } finally {
      if (channel != null && channel.isConnected()) {
        try {
          channel.disconnect();
        } catch (Exception e) {
        }
      }
    }

    ExecResult exec = new ExecResult(
      exitStatus,
      capturedInputStream.toString(StandardCharsets.UTF_8),
      capturedErrorStream.toString(StandardCharsets.UTF_8)
    );

    log.info("result: {}", exec.exitStatus());
    log.info("stdout: {}", exec.execOutput());
    log.info("stderr: {}", exec.execErrorOutput());

    return exec;

  }

  public void close() {
    if(session != null && session.isConnected()) {
      try {
        session.disconnect();
      } catch (Exception e) {
      }
    }
  }
}
