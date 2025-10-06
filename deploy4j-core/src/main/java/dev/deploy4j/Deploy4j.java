package dev.deploy4j;

import com.jcraft.jsch.JSchException;
import dev.deploy4j.commands.BuilderCommands;
import dev.deploy4j.commands.DockerCommands;
import dev.deploy4j.commands.ServerCommands;
import dev.deploy4j.commands.TraefikCommands;
import dev.deploy4j.config.Traefik;
import dev.deploy4j.configuration.Deploy4jConfig;
import dev.deploy4j.ssh.ExecResult;
import dev.deploy4j.ssh.SSHTemplate;

import java.io.IOException;

import static dev.deploy4j.RandomHex.randomHex;

public class Deploy4j {

  public static void main(String[] args) throws Exception {

//    System.out.println("Hello, Deploy4j!");
//
//
//      long start = System.currentTimeMillis();
//
//      try {
//
//        Role role = new Role("web", config);
//
//        BuilderCommands builderCommands = new BuilderCommands(config);
//        ServerCommands serverCommands = new ServerCommands(config);
//        AppCommands appCommands = new AppCommands(config, role, SSHTemplateSession);
//
//        // push env files?
//
//        // accessory boot
//
//        // deploy
//
//        // "kamal:cli:build:pull"
//
//        // login into registry if needed
//        // pull the image
//        System.out.println("=================================");
//        System.out.println("Pulling image " + config.image() + " ...\n");
//
//        // clean
//        exec = SSHTemplateSession.exec( builderCommands.clean() );
//
//        // pull
//        exec = SSHTemplateSession.exec( builderCommands.pull() );
//
//        // validate
//        exec = SSHTemplateSession.exec( builderCommands.validateImage() );
//
//        // ensure traefik is runnning
//        Traefik traefik = new Traefik();
//        TraefikCommands traefikCommands = new TraefikCommands(traefik);
//
//        exec = SSHTemplateSession.exec(traefikCommands.startOrRun());
//
//        // detect stale containers
//        // "kamal:cli:app:stale_containers"
//
//
//        // "kamal:cli:app:boot"
//        String version = versionOrLatest(config);
//
//        // boot -> host, role, self, version, barrier
//        // Kamal::Cli::App::Boot.new(host, role, self, version, barrier).run
//        String oldVersion = oldVersionRenamedIfClashing(version, appCommands, SSHTemplateSession);
//
//        // wait_at_barrier
//
//        // start_new_version
//
//        // 1. Convert to string & truncate to 51 chars
//        String prefix = config.host().length() > 51 ? config.host().substring(0, 51) : config.host();
//
//        // 2. Remove trailing dots
//        prefix = prefix.replaceAll("\\.+$", "");
//
//        // 3. Append random hex (12 chars = 6 bytes)
//        String suffix = randomHex(6);
//
//        String hostName = prefix + "-" + suffix;
//
//        appCommands.run(hostName);

        // list+versions
//        command =
//          pipe(
//            Cmd.cmd("docker", "ps" )
//              .args( argumentize("--filter", filters()) ),
//            Cmd.cmd(
//              // Extract SHA from "service-role-dest-SHA"
//              "while read line; do echo ${line##"+containerPrefix()+"-}; done"
//            )
//          );
//        exec = exec(session, command);
//        List<String> versions = Stream.of(exec.execOutput.split("\n"))
//          .filter(s -> s != null && !s.isBlank())
//          .distinct()
//          .toList();
//
//        command = pipe(
//          pipe(
//            shell(
//              chain(
//                // latest container
//                Cmd.cmd(
//                  "docker", "ps", "--latest", format,
//                  filterArgs(statuses ACTIVE
//                )
//              )
//            ),
//            Cmd.cmd("head", "-1")
//          ),
//          Cmd.cmd(
//            "while read line; do " +
//              "  id=${line%% *}; " +
//              "  name=${line#* }; " +
//              "  version=${name##"+containerPrefix()+"-}; " +
//              "  if [[ ! \" " + String.join(" ", versions) + " \" =~ \" ${version} \" ]]; then " +
//              "    echo $id; " +
//              "  fi; " +
//              "done"
//          )
//        );
//        )

        // app boot


        // prune old contains and images
//
//      } finally {
//
//        long end = System.currentTimeMillis();
//
//        System.out.println("=================================");
//        System.out.println("Deployed in " + (end - start) / 1000 + " seconds");

//      }


  }
//
//  private static String oldVersionRenamedIfClashing(String version,  AppCommands appCommands, SSHTemplate SSHTemplateSession) throws JSchException, IOException {
//    ExecResult exec = SSHTemplateSession.exec(appCommands.containerIdForVersion(version));
//    String containerIdForVersion = exec.execOutput();
//    if( containerIdForVersion != null ) {
//      String renamedVersion = version + "_replaced_" + randomHex(8);
//      SSHTemplateSession.exec(appCommands.renameContainer(version, renamedVersion));
//    }
//
//    exec = SSHTemplateSession.exec(appCommands.currentRunningVersion());
//    return exec.execOutput();
//  }
//
//  private static String versionOrLatest(Deploy4jConfig configuration) {
//    // can override via the command line
//    return configuration.version() != null ?
//      configuration.version() : configuration.latestTag();
//  }

}
