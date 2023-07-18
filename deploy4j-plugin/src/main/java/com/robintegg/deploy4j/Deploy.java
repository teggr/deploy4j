package com.robintegg.deploy4j;

import com.robintegg.deploy4j.ssh.SSHClient;
import com.robintegg.deploy4j.ssh.SSHConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;

@Slf4j
public class Deploy {

  @SneakyThrows
  public static void main(String[] args) {

    // TODO: whilst multi-module in Intellij - must set workdirectory to the module root, unless we set it to absolute?
    var workingDirectory = Paths.get(System.getProperty("workingDirectory", ""));
    log.info("working directory: {}", workingDirectory.toAbsolutePath());

    ApplicationFiles applicationFiles = new ApplicationFiles("spring-boot-web-application", workingDirectory);

    // connect to the SSH servers over SSH
    ServerConfiguration serverConfiguration = new ServerConfiguration();
    SSHConfiguration sshConfiguration = new SSHConfiguration();

    try (CLI cli = new CLI(new SSHClient(serverConfiguration.getHost(), sshConfiguration))) {
      Server server = new Server(serverConfiguration, cli);
      server.installDocker();
      server.pushApplication(applicationFiles);
      server.runTraefik();
      server.bootApplication();
    }

  }


}
