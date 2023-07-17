package com.robintegg.deploy4j;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class Server implements Closeable {

  private final ServerConfiguration serverConfiguration;
  private final SSHConfiguration sshConfiguration;
  private JSch jSch;
  private Session session;

  public Server(ServerConfiguration serverConfiguration, SSHConfiguration sshConfiguration) {
    this.serverConfiguration = serverConfiguration;
    this.sshConfiguration = sshConfiguration;
    setLogger();
  }

  public void installDocker() throws Exception {

    openSession();

    log.info("checking docker installed");
    int dockerInstalled = executeCommand(Docker.installed());

    if (dockerInstalled != 0) {

      log.info("docker not installed");

      List<String> commands = List.of(Docker.install(), Docker.running());
      for (String cmd : commands) {
        executeCommand(cmd);
      }

    } else {

      log.info("docker installed");

    }

  }

  private static void setLogger() {
//    JSch.setLogger(new Logger() {
//      @Override
//      public boolean isEnabled(int level) {
//        return true;
//      }
//
//      @Override
//      public void log(int level, String message) {
//        log.info(message);
//      }
//    });
  }

  private void openSession() throws JSchException {

    init();

    if (session == null) {
      session = jSch.getSession(sshConfiguration.getUsername(), serverConfiguration.getHost(), sshConfiguration.getPort());
      session.setConfig("StrictHostKeyChecking", "no");
      session.connect();
    }
  }

  @SneakyThrows
  private void init() {

    if (jSch == null) {

      jSch = new JSch();
      // https://stackoverflow.com/questions/53134212/invalid-privatekey-when-using-jsch
      String sskKeyPath = Path.of(sshConfiguration.getPrivateKey()).toAbsolutePath().toString();
      log.info("SSH key path : {}", sskKeyPath);
      jSch.addIdentity(sskKeyPath, sshConfiguration.getPassPhrase());

      String knownHostsPath = Path.of(sshConfiguration.getKnownHosts()).toAbsolutePath().toString();
      log.info("SSH known hosts path : {}", knownHostsPath);
      jSch.setKnownHosts(knownHostsPath);

    }

  }

  public int executeCommand(String command) throws Exception {

    try (ChannelWrapper wrapper = new ChannelWrapper((ChannelExec) session.openChannel("exec"))) {

      log.info("cmd: {}", command);

      int exitStatus = -1;

      wrapper.channel().setCommand(command);
      wrapper.channel().setInputStream(null); // this is from the command?

      ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
      wrapper.channel().setErrStream(errorStream);

      ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
      wrapper.channel().setOutputStream(responseStream);

      wrapper.channel().connect(); // flushes the command?

      while (wrapper.channel().isConnected()) { // wait for finish
        Thread.sleep(100);
      }

      String responseString = new String(responseStream.toByteArray()).trim();
      log.info("output: {}", responseString);

      String errorString = new String(errorStream.toByteArray()).trim();
      log.info("error: {}", errorString);

      exitStatus = wrapper.channel().getExitStatus();
      log.info("exit: {}", wrapper.channel().getExitStatus());

      return exitStatus;

    }

  }

  @SneakyThrows
  public void buildApplication() {

      executeCommand( "cd /root/spring-boot-web-application && docker build --force-rm --quiet -t newtag ." );

  }

  private record ChannelWrapper(ChannelExec channel) implements AutoCloseable {
    @Override
    public void close() throws Exception {
      channel().disconnect();
    }
  }

  @SneakyThrows
  public void pushApplication(ApplicationFiles applicationFiles) {

    log.info("pushing application files {}", applicationFiles);

    openSession();

    // jar file
    String from = applicationFiles.getTargetDirectory().toAbsolutePath().toString();
    String to = "/root/" + flip(applicationFiles.getTargetDirectory().toString());
    String fileName = applicationFiles.getJarFile().getFileName().toString();

    ensureDirectoryExists(to);
    copyLocalToRemote(from, to, fileName);

    // docker file
    from = applicationFiles.getWorkingDirectory().toAbsolutePath().toString();
    to = "/root/" + flip(applicationFiles.getWorkingDirectory().toString());
    fileName = applicationFiles.getDockerFile().getFileName().toString();

    ensureDirectoryExists(to);
    copyLocalToRemote(from, to, fileName);

  }

  private void ensureDirectoryExists(String to) throws Exception {
    executeCommand("mkdir -p " + to);
  }

  private String flip(String string) {
    return string.replaceAll("\\\\", "/");
  }

  @Override
  public void close() throws IOException {
    log.info("closing session");
    if (session != null) {
      session.disconnect();
    }
  }

  private void copyLocalToRemote(String from, String to, String fileName) throws Exception {

    log.info("copy local to remote: [from] {} [to] {} [filename] {}", from, to, fileName);

    boolean ptimestamp = true;
    from = from + File.separator + fileName;

    // exec 'scp -t rfile' remotely
    String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + to;

    log.info(command);

    try (ChannelWrapper wrapper = new ChannelWrapper((ChannelExec) session.openChannel("exec"))) {

      wrapper.channel().setCommand(command);

      // get I/O streams for remote scp
      OutputStream out = wrapper.channel().getOutputStream();
      InputStream in = wrapper.channel().getInputStream();

      wrapper.channel().connect();

      if (checkAck(in) != 0) {
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
        if (checkAck(in) != 0) {
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

      if (checkAck(in) != 0) {
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

      if (checkAck(in) != 0) {
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

  public static int checkAck(InputStream in) throws IOException {
    int b = in.read();
    // b may be 0 for success,
    //          1 for error,
    //          2 for fatal error,
    //         -1
    if (b == 0) return b;
    if (b == -1) return b;

    if (b == 1 || b == 2) {
      StringBuffer sb = new StringBuffer();
      int c;
      do {
        c = in.read();
        sb.append((char) c);
      }
      while (c != '\n');
      if (b == 1) { // error
        System.out.print(sb.toString());
      }
      if (b == 2) { // fatal error
        System.out.print(sb.toString());
      }
    }
    return b;
  }


}
