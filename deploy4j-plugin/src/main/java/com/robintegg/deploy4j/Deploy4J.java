package com.robintegg.deploy4j;

import com.robintegg.deploy4j.deploy.BuildFiles;
import com.robintegg.deploy4j.deploy.Deployable;
import com.robintegg.deploy4j.deploy.Deployer;
import com.robintegg.deploy4j.ssh.SshConfig;
import com.robintegg.deploy4j.ssh.SshConnectionFactory;

import java.nio.file.Path;
import java.util.List;

public class Deploy4J {

  public static void main(String[] args) {

    // get program arguments
    String workingDirectoryProperty = System.getProperty("workingDirectory", "");
    String sshUsername = System.getProperty("sshUsername");
    String sshPrivateKeyPath = System.getProperty("sshPrivateKeyPath");
    String sshPassPhrase = System.getProperty("sshPassPhrase");
    String sshKnownHostsPath = System.getProperty("sshKnownHostsPath");

    // setup program
    Path workingDirectory = Path.of(workingDirectoryProperty);

    // what am i deploying?
    String serviceName = "spring-boot-web-application";
    String version = "0.0.1-SNAPSHOT";
    Path jarFilePath = Path.of("target/spring-boot-web-application-0.0.1-SNAPSHOT.jar");
    Path dockerFilePath = Path.of("Dockerfile");
    BuildFiles buildFiles = new BuildFiles(workingDirectory, dockerFilePath, List.of(jarFilePath));
    Deployable deployable = new Deployable(
      serviceName,
      version,
        buildFiles
    );

    // where am i deploying?
    SshConfig sshConfig = new SshConfig(
        sshUsername,
        sshPrivateKeyPath,
        sshPassPhrase,
        sshKnownHostsPath
    );

    SshConnectionFactory sshConnectionFactory = new SshConnectionFactory(
        sshConfig,
        System.getProperty("host")
    );

    // deploy
    Deployer.deploy(deployable, sshConnectionFactory);

  }

}
