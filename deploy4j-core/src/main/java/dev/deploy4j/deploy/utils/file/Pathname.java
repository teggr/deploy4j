package dev.deploy4j.deploy.utils.file;

public class Pathname {

  public static boolean isAbsolute(String path) {
    return path.startsWith("/");
  }

}
