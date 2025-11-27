package dev.deploy4j.maven;

import dev.deploy4j.deploy.DeployApplicationContext;
import dev.deploy4j.deploy.DeployContext;
import dev.deploy4j.deploy.Hooks;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.NONE)
public class DeployMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    getLog().info("Deploy4J Deploying");

    String version = project.getVersion();
    String destination = null;
    String configFile = new File(project.getBasedir(), "config/deploy.yml").getAbsolutePath();
    boolean skipHooks = false;
    boolean skipPull = false;

    Configuration configuration = Configuration.createFrom(configFile, destination, version);

    DeployContext deployContext = new DeployContext(configuration, null, null, null); // specific hosts, roles, primary not yet supported

    LocalHost localHost = new LocalHost();

    Hooks hooks = new Hooks(localHost, deployContext.config(), skipHooks);

    try (SshHosts sshHosts = new SshHosts(deployContext.config())) {

      DeployApplicationContext deployApplicationContext = new DeployApplicationContext(sshHosts, hooks, localHost, deployContext);

      deployApplicationContext.deploy().deploy(deployContext, skipPull, false);

    } catch (Exception e) {

      throw new RuntimeException(e);

    }

  }

}