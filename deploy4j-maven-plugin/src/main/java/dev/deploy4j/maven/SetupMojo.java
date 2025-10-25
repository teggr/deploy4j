package dev.deploy4j.maven;

import dev.deploy4j.deploy.DeployApplicationContext;
import dev.deploy4j.deploy.DeployContext;
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

import java.io.File;

@Mojo(name = "setup", defaultPhase = LifecyclePhase.NONE)
public class SetupMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    getLog().info("Deploy4J Setup");

    String version = project.getVersion();
    String destination = null;
    String configFile = new File( project.getBasedir(), "config/deploy.yml" ).getAbsolutePath();

    Environment environment = new Environment(destination);

    Configuration configuration = Configuration.createFrom(configFile, destination, version);

    DeployContext deployContext = new DeployContext(configuration, null, null, null); // specific hosts, roles, primary not yet supported

    try (SshHosts sshHosts = new SshHosts(deployContext.config())) {

      DeployApplicationContext deployApplicationContext = new DeployApplicationContext(environment, sshHosts, deployContext);

      deployApplicationContext.deploy().setup(deployContext, false);

    } catch (Exception e) {

      throw new RuntimeException(e);

    }

  }

}