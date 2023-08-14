package com.robintegg.deploy4j.deploy;

import com.robintegg.deploy4j.ssh.SshCommandResult;
import com.robintegg.deploy4j.ssh.SshConnection;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class Traefik {

  public static void startTraefik(SshConnection sshConnection) {

    SshCommandResult runCmdResult = sshConnection.executeCommand(
        Stream.of(
            "docker",
            "run",
            "--name", "traefik",
            "--restart", "unless-stopped",
            "--publish", "80",
            "--volume", "/var/run/docker.sock:/var/run/docker.sock",
            "traefik:v2.9",
            "--providers.docker"
        ).collect(Collectors.joining(" "))
    );

    if (runCmdResult.status() != 0) {
      log.info("failed to start traefik: {}", runCmdResult.err());
    } else {
      log.info("start traefik: {}", runCmdResult.out());
    }

  }

}
