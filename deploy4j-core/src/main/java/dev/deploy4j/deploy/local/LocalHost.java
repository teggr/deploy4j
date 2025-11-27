package dev.deploy4j.deploy.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Consumer;

public class LocalHost {

  private static final Logger log = LoggerFactory.getLogger(LocalHost.class);

  public void on(Consumer<LocalHost> block) {
    block.accept(this);
  }

  public void execute(String scriptFile, Map<String,String> environmentVariables) {

    // if windows then wrap in cmd /c and make a batch file
    // else wrap in bash -c
    if (System.getProperty("os.name").toLowerCase().contains("win")) {
      scriptFile = "cmd /c " + scriptFile;
    } else {
      scriptFile = "bash -c '" + scriptFile + "'";
    }

    log.debug("Executing cmd {}...", scriptFile);

    // create an object for executing the command, set env vars, execute the command, capturing the output to the debug log
    try {
      ProcessBuilder processBuilder = new ProcessBuilder();
      processBuilder.command(scriptFile.split(" "));
      if (environmentVariables != null) {
        processBuilder.environment().putAll(environmentVariables);
      }

      Process process = processBuilder.start();

      StringBuilder stdout = new StringBuilder();
      StringBuilder stderr = new StringBuilder();

      Thread stdoutReader = new Thread(() -> {
        try (var is = process.getInputStream();
             var reader = new java.io.BufferedReader(new java.io.InputStreamReader(is))) {
          String line;
          while ((line = reader.readLine()) != null) {
            stdout.append(line).append(System.lineSeparator());
            log.info(line);
          }
        } catch (Exception e) {
          log.error("Failed to read stdout: {}", e.getMessage());
        }
      }, "process-stdout-reader");

      Thread stderrReader = new Thread(() -> {
        try (var is = process.getErrorStream();
             var reader = new java.io.BufferedReader(new java.io.InputStreamReader(is))) {
          String line;
          while ((line = reader.readLine()) != null) {
            stderr.append(line).append(System.lineSeparator());
            log.info(line);
          }
        } catch (Exception e) {
          log.error("Failed to read stderr: {}", e.getMessage());
        }
      }, "process-stderr-reader");

      stdoutReader.start();
      stderrReader.start();

      int exitCode = process.waitFor();

      stdoutReader.join();
      stderrReader.join();

      if (exitCode != 0) {
        String msg = "Command failed with exit code " + exitCode + "\nstdout:\n" + stdout + "\nstderr:\n" + stderr;
        throw new RuntimeException(msg);
      }

    } catch (Exception e) {
      throw new RuntimeException("Failed to execute command: " + e.getMessage(), e);
    }
  }

  public boolean hasFile(String file) {
    return Paths.get(file).toFile().exists();
  }

  public String resolveScriptFile(String path, String name) {
    // if win then return path and name with bat extension
    if (System.getProperty("os.name").toLowerCase().contains("win")) {
      return Paths.get(path, name + ".bat").toString();
    } else {
      return Paths.get(path, name).toString();
    }
  }
}
