package dev.deploy4j.deploy;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Version {

  public static int compareVersions(String minimumVersion, String currentVersion) {
    // compare version strings like "1.2.3" and "1.2.3-SNAPSHOT". snapshot is considered lower than release
    String[] minParts = minimumVersion.split("[.-]");
    String[] currParts = currentVersion.split("[.-]");
    int length = Math.max(minParts.length, currParts.length);
    for (int i = 0; i < length; i++) {
      String minPart = i < minParts.length ? minParts[i] : "0";
      String currPart = i < currParts.length ? currParts[i] : "0";
      int minInt, currInt;
      try {
        minInt = Integer.parseInt(minPart);
      } catch (NumberFormatException e) {
        minInt = -1; // non-numeric parts are considered lower
      }
      try {
        currInt = Integer.parseInt(currPart);
      } catch (NumberFormatException e) {
        currInt = -1; // non-numeric parts are considered lower
      }
      if (minInt != currInt) {
        return Integer.compare(minInt, currInt);
      }
    }
    return 0;
  }

  public static final String VERSION = readVersion();

  private static String readVersion() {
    try {
      return IOUtils.resourceToString(".version", StandardCharsets.UTF_8, Version.class.getClassLoader());
    } catch (IOException ignored) {
      throw new RuntimeException("Missing .version file");
    }
  }

  public void version() {
    System.out.println(Version.VERSION);
  }


}
