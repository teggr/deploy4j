package dev.deploy4j.deploy;

import dev.deploy4j.deploy.configuration.Accessory;
import dev.deploy4j.deploy.configuration.Role;
import dev.deploy4j.deploy.host.commands.*;
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
  private final AppHostCommandsFactory apps;
  private final AccessoryHostCommandsFactory accessories;

  public Env(SshHosts sshHosts, LockManager lockManager, TraefikHostCommands traefik, Environment environment, AuditorHostCommands audit, AppHostCommandsFactory apps, AccessoryHostCommandsFactory accessories) {
    super(sshHosts);
    this.lockManager = lockManager;
    this.traefik = traefik;
    this.environment = environment;
    this.audit = audit;
    this.apps = apps;
    this.accessories = accessories;
  }

  /**
   * Push the env file to the remote hosts
   */
  public void push(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext.hosts(), host -> {

        host.execute(audit.record("Pushed env files"));

        for (Role role : deployContext.rolesOn(host.hostName())) {

          host.execute(apps.app(role, host.hostName()).makeEnvDirectory());
          host.upload(role.env(host.hostName()).secretsIO(), role.env(host.hostName()).secretsFile(), 400);

        }

      });

      on(deployContext.traefikHosts(), host -> {

        host.execute(traefik.makeEnvDirectory());
        host.upload(traefik.env().secretsIO(), traefik.env().secretsFile(), 400);

      });

      on(deployContext.accessoryHosts(), host -> {

        for (String accessory : deployContext.accessoriesOn(host.hostName())) {

          Accessory accesssoryConfig = deployContext.config().accessory(accessory);
          host.execute(accessories.accessory(accessory).makeEnvDirectory());
          host.upload(accesssoryConfig.env().secretsIO(), accesssoryConfig.env().secretsFile(), 400);

        }

      });

    });

  }

  /**
   * Delete the env file from the remote hosts
   */
  public void delete(DeployContext deployContext) {

    lockManager.withLock(deployContext, () -> {

      on(deployContext.hosts(), host -> {

        host.execute(audit.record("Deleted env files"));

        for (Role role : deployContext.rolesOn(host.hostName())) {

          host.execute(apps.app(role, host.hostName()).removeEnvFile());

        }

      });

      on(deployContext.traefikHosts(), host -> {

        host.execute(traefik.removeEnvFile());

      });

      on(deployContext.accessoryHosts(), host -> {

        for (String accessory : deployContext.accessoriesOn(host.hostName())) {

          Accessory accessoryConfig = deployContext.config().accessory(accessory);
          host.execute(accessories.accessory(accessory).removeEnvFile());

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
  public void envify(DeployContext deployContext, boolean skipPush, String destination) {

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
        push(deployContext);
      }

    } else {
      System.out.println("Skipping envify (no " + envTemplatePath + " exists)");
    }

  }

}
