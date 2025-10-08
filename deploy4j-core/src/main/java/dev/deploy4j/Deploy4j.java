package dev.deploy4j;

public class Deploy4j implements AutoCloseable {

  private final Context context;

  public Deploy4j(Context context) {
    this.context = context;
  }

  /**
   * Setup all accessories, push the env, and deploy app to servers
   */
  public void setup() {

    long start = System.currentTimeMillis();

    try {

      context.server().bootstrap();

      // envify()
      // context.getEnv().push();
      // context.accesssory().boot();

      deploy();

    } finally {

      long end = System.currentTimeMillis();

      System.out.println("=================================");
      System.out.println("Deployed in " + (end - start) / 1000 + " seconds");

    }

  }

  /**
   * Deploy the app to servers
   */
  public void deploy() {

    context.registry().login();
    context.build().pull();
    // ensure Traefik is running...
    context.traefik().boot();
    // Detect stale containers...
//    context.app().staleContainers();
//    context.app().boot();
//    context.prune().all();

  }

  public static void main(String[] args) throws Exception {

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


  @Override
  public void close() throws Exception {

    context.close();

  }
}
