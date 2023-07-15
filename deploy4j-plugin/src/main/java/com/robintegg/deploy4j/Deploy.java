package com.robintegg.deploy4j;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class Deploy {

  @SneakyThrows
  public static void main(String[] args) {

    // TODO: whilst multi-module in Intellij - must set workdirectory to the module root, unless we set it to absolute?
    var workingDirectory = Paths.get(System.getProperty("workingDirectory", ""));
    log.info("working directory: {}", workingDirectory.toAbsolutePath());

    // need to pick up jar files
    var targetDirectorty = workingDirectory.resolve("target/");

    var applicationJarFile = targetDirectorty.resolve("spring-boot-web-application-sample-0.0.1-SNAPSHOT.jar");

    log.info("jar file: {}", applicationJarFile.toAbsolutePath());

    log.info("file info: size({})", Files.size(applicationJarFile));

    try( Server server = new Server( new ServerConfiguration(), new SSHConfiguration() ) ) {
      // connect to the SSH servers over SSH
      server.installDocker();
      server.pushApplication(applicationJarFile);
    }

  }



}
