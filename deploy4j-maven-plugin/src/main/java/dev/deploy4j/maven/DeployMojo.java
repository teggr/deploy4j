package dev.deploy4j.maven;

import dev.deploy4j.deploy.Commander;
import dev.deploy4j.deploy.DeployApplicationContext;
import dev.deploy4j.deploy.Environment;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.NONE)
public class DeployMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  MavenProject project;

  // TODO: start with the existing config files

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    getLog().info("Deploy4J Deploying");

    String version = project.getVersion();
    String destination = null;
    String configFile = "config/deploy.yml";

    Environment environment = new Environment(destination);

    // TODO: maven logging?
    // local logging
//    if (quiet) {
//      System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");
//    } else if (verbose) {
//      System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
//    } else {
//      System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
//    }

    Configuration configuration = Configuration.createFrom(configFile, destination, version);

    Commander commander = new Commander(configuration, null, null, null); // specific hosts, roles, primary not yet supported

    try (SshHosts sshHosts = new SshHosts(commander.config())) {

      DeployApplicationContext deployApplicationContext = new DeployApplicationContext(environment, sshHosts, commander);

      deployApplicationContext.deploy().deploy(commander, false);

    } catch (Exception e) {

      throw new RuntimeException(e);

    }

  }

}