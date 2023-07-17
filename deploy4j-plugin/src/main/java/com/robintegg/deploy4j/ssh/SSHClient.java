package com.robintegg.deploy4j.ssh;

import com.jcraft.jsch.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Path;

@Slf4j
public class SSHClient implements AutoCloseable {

  private final String host;
  private final SSHConfiguration sshConfiguration;
  private final JSch jSch;
  private Session session;

  @SneakyThrows
  public SSHClient(String host, SSHConfiguration sshConfiguration) {

    this.host = host;
    this.sshConfiguration = sshConfiguration;

    if (sshConfiguration.isLogging()) {
      enableLogging();
    }

    jSch = new JSch();

    // https://stackoverflow.com/questions/53134212/invalid-privatekey-when-using-jsch
    String sskKeyPath = Path.of(sshConfiguration.getPrivateKey()).toAbsolutePath().toString();
    log.info("SSH key path : {}", sskKeyPath);
    jSch.addIdentity(sskKeyPath, sshConfiguration.getPassPhrase());

    String knownHostsPath = Path.of(sshConfiguration.getKnownHosts()).toAbsolutePath().toString();
    log.info("SSH known hosts path : {}", knownHostsPath);
    jSch.setKnownHosts(knownHostsPath);

  }

  private void openSession() throws JSchException {
    if (session == null) {
      log.info("opening ssh client session");
      session = jSch.getSession(sshConfiguration.getUsername(), host, sshConfiguration.getPort());
      session.setConfig("StrictHostKeyChecking", "no");
      session.connect();
    }
  }

  public int executeCommandForStatus(String command) {
    return executeCommand(command).status();
  }

  @SneakyThrows
  public CommandResult executeCommand(String command) {

    log.info("executing command: {}", command);


    openSession();

    try (ChannelWrapper wrapper = new ChannelWrapper((ChannelExec) session.openChannel("exec"))) {

      ChannelExec channel = wrapper.getChannel();

      int exitStatus = -1;

      channel.setCommand(command);
      channel.setInputStream(null); // this is from the command?

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

      exitStatus = channel.getExitStatus();
      log.info("exit: {}", channel.getExitStatus());

      return new CommandResult(responseString, errorString, exitStatus);

    }

  }

  @Override
  public void close() {
    if (session != null) {
      session.disconnect();
    }
  }

  public void scpLocalToRemote(String from, String to, String fileName) throws Exception {

    openSession();

    log.info("copy local to remote: [from] {} [to] {} [filename] {}", from, to, fileName);

    boolean ptimestamp = true;
    from = from + File.separator + fileName;

    // exec 'scp -t rfile' remotely
    String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + to;

    log.info(command);

    try (ChannelWrapper wrapper = new ChannelWrapper((ChannelExec) session.openChannel("exec"))) {

      ChannelExec channel = wrapper.getChannel();

      channel.setCommand(command);

      // get I/O streams for remote scp
      OutputStream out = channel.getOutputStream();
      InputStream in = channel.getInputStream();

      channel.connect();

      if (SSHUtils.checkAck(in) != 0) {
        throw new RuntimeException("initial ack failed");
      }

      File _lfile = new File(from);

      if (ptimestamp) {
        command = "T" + (_lfile.lastModified() / 1000) + " 0";
        // The access time should be sent here,
        // but it is not accessible with JavaAPI ;-<
        command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
        out.write(command.getBytes());
        out.flush();
        if (SSHUtils.checkAck(in) != 0) {
          throw new RuntimeException("ptimestamp ack failed");
        }
      }

      // send "C0644 filesize filename", where filename should not include '/'
      long filesize = _lfile.length();
      command = "C0644 " + filesize + " " + fileName;
//      if (from.lastIndexOf('/') > 0) {
//        command += from.substring(from.lastIndexOf('/') + 1);
//      } else {
//        command += from;
//      }

      command += "\n";
      out.write(command.getBytes());
      out.flush();

      if (SSHUtils.checkAck(in) != 0) {
        throw new RuntimeException("post command ack failed");
      }

      // send a content of lfile
      FileInputStream fis = new FileInputStream(from);
      byte[] buf = new byte[1024];
      while (true) {
        int len = fis.read(buf, 0, buf.length);
        if (len <= 0) break;
        out.write(buf, 0, len); //out.flush();
      }

      // send '\0'
      buf[0] = 0;
      out.write(buf, 0, 1);
      out.flush();

      if (SSHUtils.checkAck(in) != 0) {
        throw new RuntimeException("post content ack failed");
      }
      out.close();

      try {
        if (fis != null) fis.close();
      } catch (Exception ex) {
        System.out.println(ex);
      }

    }

  }

  private static void enableLogging() {
    JSch.setLogger(new Logger() {
      @Override
      public boolean isEnabled(int level) {
        return true;
      }

      @Override
      public void log(int level, String message) {
        log.info(message);
      }
    });
  }

}
