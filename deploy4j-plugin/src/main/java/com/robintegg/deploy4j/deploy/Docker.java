package com.robintegg.deploy4j.deploy;

import com.robintegg.deploy4j.ssh.SshCommandResult;
import com.robintegg.deploy4j.ssh.SshConnection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Docker {

  public static void install(SshConnection sshConnection) {

    SshCommandResult versionResult = sshConnection.executeCommand("docker -v");

    if (versionResult.status() != 0) {

      // unable to get version indicates that it's not installed
      log.info("docker not installed: {}", versionResult.err());

      SshCommandResult installResult = sshConnection.executeCommand("curl -fsSL https://get.docker.com | sh");

      if (installResult.status() != 0) {
        log.info("could not install docker: {}", installResult.err());
        throw new RuntimeException("could not installed docker");
      } else {
        log.info("docker installed: {}", installResult.out());
      }

    } else {

      log.info("docker already installed: {}", versionResult.out());

    }

  }

}
