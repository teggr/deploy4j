package dev.deploy4j.maven;

import dev.deploy4j.Commander;
import dev.deploy4j.cli.Cli;
import dev.deploy4j.configuration.Configuration;
import dev.deploy4j.raw.Deploy4jConfig;
import dev.deploy4j.raw.Deploy4jConfigReader;
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

    long start = System.currentTimeMillis();

    try {

      Deploy4jConfig deploy4jConfig = Deploy4jConfigReader.readYaml(configFile);

      Configuration config = new Configuration(
        deploy4jConfig,
        destination,
        version
      );

      try (Commander commander = new Commander(config)) {

//    commander.setVerbosity();
//    commander.configure( configFile, destination, version );
//    commander.specificHosts();
//    commander.specificRoles();
//    commander.specificPrimary();

        Cli cli = new Cli(commander);

        cli.main().deploy(false);

      } catch (Exception e) {

        throw new RuntimeException(e);

      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {

      long end = System.currentTimeMillis();

      System.out.println("=================================");
      System.out.println("Finished all in  in " + (end - start) / 1000 + " seconds");

    }

  }

}