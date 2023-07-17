package com.robintegg.deploy4j;

import com.robintegg.deploy4j.ssh.SSHClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static java.lang.String.format;

@Slf4j
public class Server {

  private final SSHClient sshClient;

  public Server(ServerConfiguration serverConfiguration, SSHClient sshClient) {
    this.sshClient = sshClient;
  }

  public void installDocker() throws Exception {

    log.info("checking docker installed");
    int dockerInstalled = sshClient.executeCommandForStatus(Docker.installed());

    if (dockerInstalled != 0) {

      log.info("docker not installed");

      List<String> commands = List.of(Docker.install(), Docker.running());
      for (String cmd : commands) {
        sshClient.executeCommand(cmd);
      }

    } else {

      log.info("docker installed");

    }

  }

  @SneakyThrows
  public void pushApplication(ApplicationFiles applicationFiles) {

    log.info("pushing application files {}", applicationFiles);

    // jar file
    String from = applicationFiles.getTargetDirectory().toAbsolutePath().toString();
    String to = "/root/" + flip(applicationFiles.getTargetDirectory().toString());
    String fileName = applicationFiles.getJarFile().getFileName().toString();

    sshClient.executeCommand(ensureDirectoryExists(to));
    sshClient.scpLocalToRemote(from, to, fileName);

    // docker file
    from = applicationFiles.getWorkingDirectory().toAbsolutePath().toString();
    to = "/root/" + flip(applicationFiles.getWorkingDirectory().toString());
    fileName = applicationFiles.getDockerFile().getFileName().toString();

    sshClient.executeCommand(ensureDirectoryExists(to));
    sshClient.scpLocalToRemote(from, to, fileName);

    sshClient.executeCommand(format("docker build --force-rm --quiet -t %s --file %s %s", applicationFiles.getApplicationName(), to + "/" + fileName, to));

  }

  private String ensureDirectoryExists(String to) throws Exception {
    return "mkdir -p " + to;
  }

  private String flip(String string) {
    return string.replaceAll("\\\\", "/");
  }

}
