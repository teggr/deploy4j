package dev.deploy4j.deploy;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Version {

    public static final String VERSION = readVersion();

    private static final String readVersion() {
        try{
          return IOUtils.resourceToString(".version", StandardCharsets.UTF_8, Version.class.getClassLoader());
        } catch (IOException ignored) {
          throw new RuntimeException( "Missing .version file" );
        }
    }

}
