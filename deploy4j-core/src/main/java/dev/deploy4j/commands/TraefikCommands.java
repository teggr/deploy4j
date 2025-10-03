package dev.deploy4j.commands;

import dev.deploy4j.Cmd;
import dev.deploy4j.Commands;
import dev.deploy4j.config.Traefik;

import java.util.List;
import java.util.Map;

import static dev.deploy4j.Commands.*;

public class TraefikCommands {

  private final Traefik traefik;

  public TraefikCommands (Traefik traefik) {
    this.traefik = traefik;
  }

  public Cmd run() {
//         Cmd.cmd( "docker", "run" )
//           .arg( "--name traefik" )
//           .arg( "--detach" )
//           .arg( "--restart", "unless-stopped" )
//           .arg( traefik.publish ? argumentize("--publish", List.of( "" + traefik.port ) ) : null )
//           .arg( "--volume", "/var/run/docker.sock:/var/run/docker.sock" )
//           // --env-file
//            // --env
//            //  --log-opt { max-size => 10m
//           .arg( argumentize( "--label", traefik.labels ) )
//           .arg( optionize( traefik.options, null ) )
//           .arg( traefik.image )
//           .arg( "--providers.docker" )
//           .arg( optionize( traefik.args, "=" ) ) //          *cmd_option_args
//        );
    return Cmd.cmd("docker", "run")
      .args("--detach")
      .args("--name", "traefik")
      .args("--restart", "unless-stopped")
      .args(Commands.argumentize("--publish", List.of(
        "80:80",
        "8080:8080"
      )))
      .args("--volume", "/var/run/docker.sock:/var/run/docker.sock")
      .args(traefik.image)
      .args(optionize(Map.of(
          "api.insecure", "true",
          "providers.docker", "true",
          "entrypoints.web.address", ":80"
        ), "=")
      );
  }

  public Cmd start() {
    return Cmd.cmd("docker", "container", "start", "traefik");
  }

  public Cmd stop() {
    return Cmd.cmd("docker", "container", "stop", "traefik");
  }

  public Cmd startOrRun() {
    return any(
      start(),
      run());
  }

  public Cmd info() {
    return Cmd.cmd("docker", "ps", "--filter", "name=^traefik$");
  }

  public Cmd logs(String since, String lines, String grep, String grepOptions) {
    return pipe(
      Cmd.cmd("docker", "logs", "traefik", since != null ? "--since " + since : null, lines != null ? "--tail " + lines : null, "--timestamps", "2>&1"),
      grep != null ? Cmd.cmd("grep", "\"" + grep + "\"" + ( grepOptions != null ? " " + grepOptions : "" ) ) : null
    );
  }

  public Cmd followLogs() {
    throw new UnsupportedOperationException();
  }

  public Cmd removeContainer() {
    return Cmd.cmd("docker", "container", "prune", "--force", "--filter", "label=org.opencontainers.image.title=Traefik");
  }

  public Cmd removeImage() {
    return Cmd.cmd("docker", "image", "prune", "--force", "--filter", "label=org.opencontainers.image.title=Traefik");
  }

}
