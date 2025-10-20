package dev.deploy4j.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SSHTemplate {

  private static final Logger log = LoggerFactory.getLogger(SSHTemplate.class);

  private final String host;
  private final String username;
  private final Integer port;
  private final String privateKey;
  private final String privateKeyPassphrase;
  private final boolean strictHostChecking;

  private Session session;

  public SSHTemplate(String host, String username, Integer port, String privateKey, String privateKeyPassphrase, boolean strictHostChecking) {
    this.host = host;
    this.username = username;
    this.port = port;
    this.privateKey = privateKey;
    this.privateKeyPassphrase = privateKeyPassphrase;
    this.strictHostChecking = strictHostChecking;
  }

  private Session getSession() throws JSchException {
    if (session == null) {

      log.info("Creating session for {}@{}:{}", username, host, port);

      // setup ssh environment for host
      JSch jsch = new JSch();
      jsch.addIdentity(privateKey, privateKeyPassphrase);

      // create a session
      session = jsch.getSession(username, host, port);
      if (!strictHostChecking) {
        session.setConfig("StrictHostKeyChecking", "no");
      }
    }
    return session;
  }

  public ExecResult exec(String command) {

    int exitStatus = -1;
    ByteArrayOutputStream capturedInputStream = new ByteArrayOutputStream();
    ByteArrayOutputStream capturedErrorStream = new ByteArrayOutputStream();

    ChannelExec channel = null;
    try {

      Session session = getSession();

      if (!session.isConnected()) {
        session.connect();
      }

      channel = (ChannelExec) session.openChannel("exec");
      log.info("exec__: {}", command);
      channel.setCommand(command);
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
    if (session != null && session.isConnected()) {
      try {
        session.disconnect();
      } catch (Exception e) {
      }
    }
  }

  public void upload(String local, String remote) {
    ChannelExec channel = null;
    try {
      Session session = getSession();
      if (!session.isConnected()) {
        session.connect();
      }
      // Use SFTP channel for file upload
      com.jcraft.jsch.ChannelSftp sftp = (com.jcraft.jsch.ChannelSftp) session.openChannel("sftp");
      sftp.connect();
      sftp.put(local, remote);
      sftp.disconnect();
      log.info("Uploaded file from {} to {}", local, remote);
    } catch (Exception e) {
      log.error("Failed to upload file from {} to {}", local, remote, e);
      throw new RuntimeException(e);
    }
  }
}
