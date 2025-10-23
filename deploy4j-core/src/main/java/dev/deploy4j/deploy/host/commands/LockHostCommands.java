package dev.deploy4j.deploy.host.commands;

import dev.rebelcraft.cmd.Cmd;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.rebelcraft.cmd.Cmds;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.rebelcraft.cmd.Cmds.*;

public class LockHostCommands extends BaseHostCommands {

  private final Base64.Encoder encoder = Base64.getEncoder();

  public LockHostCommands(Configuration config) {
    super(config);
  }

  public Cmd acquire(String message, String version) {
    return combine(
       Cmd.cmd("mkdir", lockDir() ),
        writeLockDetais(message,version)
    );
  }

  public Cmd release() {
    return combine(
      Cmd.cmd("rm", lockDetailsFile() ),
      Cmd.cmd("rm", "-r", lockDir())
    );
  }

  public Cmd status() {
    return combine(
      statLockDir(),
      readLockDetails()
    );
  }

  public Cmd ensureLocksDirectory() {
    return Cmd.cmd( "mkdir", "-p", locksDir() );
  }

  // private

  private Cmd writeLockDetais(String message, String version) {
    return write(
      Cmd.cmd("echo", "\"" + encode64( lockDetails(message,version) )  +  "\""),
      Cmd.cmd( lockDetailsFile() )
    );
  }

  private Cmd readLockDetails() {
    return pipe(
      Cmd.cmd("cat", lockDetailsFile() ),
      Cmd.cmd("base64", "-d")
    );
  }

  private Cmd statLockDir() {
    return write(
      Cmd.cmd("stat", lockDir()),
      Cmd.cmd("/dev/null")
    );
  }

  private String locksDir() {
    return config.runDirectory() + "/locks";
  }

  private String lockDir() {
    String dirName = Stream.of(
        config.service(),
        config.destination()
      ).filter(Objects::nonNull)
      .collect(Collectors.joining("-"));
    return locksDir() + "/" + dirName;
  }

  private String lockDetailsFile() {
    return lockDir() + "/details";
  }

  private String lockDetails(String message, String version) {
    return """
      Locked by: %s at %s
      Version: %s
      Message: %s
      """.formatted(
        lockedBy(),
      Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME),
      version,
      message
    );
  }

  private String lockedBy() {
    // TODO: git user name
    return "Unknown";
  }

  private String encode64(String text) {
    return encoder.encodeToString(text.getBytes());
  }

}
