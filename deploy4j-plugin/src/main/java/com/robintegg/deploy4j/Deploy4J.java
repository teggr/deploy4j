package com.robintegg.deploy4j;

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
    Path jarFilePath = workingDirectory.resolve("target/spring-boot-web-application-0.0.1-SNAPSHOT.jar");
    Path dockerFilePath = workingDirectory.resolve("Dockerfile");

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
    Deployer.deploy(List.of(jarFilePath,dockerFilePath), sshConnectionFactory);

  }

}
