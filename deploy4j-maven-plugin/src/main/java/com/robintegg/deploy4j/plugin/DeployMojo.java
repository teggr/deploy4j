package com.robintegg.deploy4j.plugin;

import deploy4j.deploy.BuildFiles;
import deploy4j.deploy.Deployable;
import deploy4j.deploy.Deployer;
import deploy4j.ssh.SshConfig;
import deploy4j.ssh.SshConnectionFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.nio.file.Path;
import java.util.List;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.NONE)
public class DeployMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  MavenProject project;

  // where am i deploying

  @Parameter(property = "host")
  String host;

  @Parameter(property = "sshUsername")
  String sshUsername;

  @Parameter(property = "sshPrivateKeyPath")
  String sshPrivateKeyPath;

  @Parameter(property = "sshPassPhrase")
  String sshPassPhrase;

  @Parameter(property = "sshKnownHostsPath")
  String sshKnownHostsPath;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    getLog().info("Deploy4J Deploying");

    // setup program
    Path workingDirectory = Path.of(project.getBasedir().getAbsolutePath());

    String serviceName = project.getArtifactId();
    String version = project.getVersion();
    Path jarFilePath = workingDirectory.relativize(Path.of(project.getBuild().getDirectory() + "/" + project.getArtifactId() + "-" + project.getVersion() + ".jar"));
    Path dockerFilePath = Path.of("Dockerfile");

    getLog().info("working directory: " + workingDirectory);
    getLog().info("serviceName:       " + serviceName);
    getLog().info("version:           " + version);
    getLog().info("jarFilePath:       " + jarFilePath);
    getLog().info("dockerFilePath:    " + dockerFilePath);

    getLog().info("host:              " + host);
    getLog().info("sshUsername:       " + sshUsername);

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
        host
    );

    // deploy
    Deployer.deploy(deployable, sshConnectionFactory);

  }

}
