package com.robintegg.deploy4j.deploy;

import java.nio.file.Path;
import java.util.List;

public record BuildFiles(Path workingDirectory, Path dockerFilePath, List<Path> buildContext) {
}
