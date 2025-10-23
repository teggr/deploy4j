package dev.deploy4j.deploy;

import dev.deploy4j.deploy.configuration.Accessory;
import dev.deploy4j.deploy.configuration.Role;
import dev.deploy4j.deploy.host.commands.AuditorHostCommands;
import dev.deploy4j.deploy.host.commands.TraefikHostCommands;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.utils.erb.ERB;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Env extends Base {

  private final LockManager lockManager;
  private final TraefikHostCommands traefik;
  private final Environment environment;
  private final AuditorHostCommands audit;

  public Env(SshHosts sshHosts, LockManager lockManager, TraefikHostCommands traefik, Environment environment, AuditorHostCommands audit) {
    super(sshHosts);
    this.lockManager = lockManager;
    this.traefik = traefik;
    this.environment = environment;
    this.audit = audit;
  }

  /**
   * Push the env file to the remote hosts
   */
  public void push(Commander commander) {

    lockManager.withLock(commander, () -> {

      on(commander.hosts(), host -> {

        host.execute(audit.record("Pushed env files"));

        for (Role role : commander.rolesOn(host.hostName())) {

          host.execute(commander.app(role, host.hostName()).makeEnvDirectory());
          host.upload(role.env(host.hostName()).secretsIO(), role.env(host.hostName()).secretsFile(), 400);

        }

      });

      on(commander.traefikHosts(), host -> {

        host.execute(traefik.makeEnvDirectory());
        host.upload(traefik.env().secretsIO(), traefik.env().secretsFile(), 400);

      });

      on(commander.accessoryHosts(), host -> {

        for (String accessory : commander.accessoriesOn(host.hostName())) {

          Accessory accesssoryConfig = commander.config().accessory(accessory);
          host.execute(commander.accessory(accessory).makeEnvDirectory());
          host.upload(accesssoryConfig.env().secretsIO(), accesssoryConfig.env().secretsFile(), 400);

        }

      });

    });

  }

  /**
   * Delete the env file from the remote hosts
   */
  public void delete(Commander commander) {

    lockManager.withLock(commander, () -> {

      on(commander.hosts(), host -> {

        host.execute(audit.record("Deleted env files"));

        for (Role role : commander.rolesOn(host.hostName())) {

          host.execute(commander.app(role, host.hostName()).removeEnvFile());

        }

      });

      on(commander.traefikHosts(), host -> {

        host.execute(traefik.removeEnvFile());

      });

      on(commander.accessoryHosts(), host -> {

        for (String accessory : commander.accessoriesOn(host.hostName())) {

          Accessory accessoryCOnfig = commander.config().accessory(accessory);
          host.execute(commander.accessory(accessory).removeEnvFile());

        }

      });

    });

  }

  /**
   * Create .env by evaluating .env.thyme (or .env.staging.thyme -> .env.staging when using -d staging)
   *
   * @param skipPush    Skip .env file push
   * @param destination
   */
  public void envify(Commander commander, boolean skipPush, String destination) {

    String envTemplatePath;
    String envPath;
    if (destination != null) {
      envTemplatePath = ".env.%s.thyme".formatted(destination);
      envPath = ".env.%s".formatted(destination);
    } else {
      envTemplatePath = ".env.thyme";
      envPath = ".env";
    }

    File envTemplateFile = new File(envTemplatePath);
    if (envTemplateFile.exists()) {
      String content = environment
        .withOriginalEnv(() -> new ERB(envTemplateFile).result());
      try {
        FileUtils.writeStringToFile(new File(envPath), content, StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      if (skipPush) {
        environment.reloadEnv();
        push(commander);
      }

    } else {
      System.out.println("Skipping envify (no " + envTemplatePath + " exists)");
    }

  }

}
