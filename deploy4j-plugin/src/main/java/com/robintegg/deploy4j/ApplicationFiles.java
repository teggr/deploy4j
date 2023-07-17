package com.robintegg.deploy4j;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@ToString
@Getter
public class ApplicationFiles {

  private final String applicationName;
  private final Path workingDirectory;
  private final Path targetDirectory;
  private final Path jarFile;
  private final Path dockerFile;

  @SneakyThrows
  public ApplicationFiles(String applicationName, Path workingDirectory) {

    this.applicationName = applicationName;
    this.workingDirectory = workingDirectory;

    // need to pick up jar files
    this.targetDirectory = workingDirectory.resolve("target");

    this.jarFile = targetDirectory.resolve(applicationName + "-0.0.1-SNAPSHOT.jar");

    log.info("jar file: {}", jarFile.toAbsolutePath());

    log.info("file info: size({})", Files.size(jarFile));

    // and dockerfile

    this.dockerFile = workingDirectory.resolve("Dockerfile");

    log.info("dockerFile file: {}", dockerFile.toAbsolutePath());

  }

}
