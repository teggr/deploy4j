package dev.deploy4j.deploy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Version")
class VersionTest {

    @Test
    @DisplayName("should have non-null version constant")
    void shouldHaveVersionConstant() {
        assertThat(Version.VERSION)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    @DisplayName("should print version to stdout")
    void shouldPrintVersion() {
        // Arrange
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            Version version = new Version();
            
            // Act
            version.version();
            
            // Assert
            String output = outputStream.toString().trim();
            assertThat(output).isEqualTo(Version.VERSION);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("should have version string format")
    void shouldHaveProperVersionFormat() {
        // Version should be a non-empty string
        assertThat(Version.VERSION)
                .matches(".+"); // At least one character
    }

    @Test
    @DisplayName("compareVersions returns 0 for equal versions")
    void compareVersionsEqual() {
        assertThat(Version.compareVersions("1.2.3", "1.2.3")).isEqualTo(0);
    }

    @Test
    @DisplayName("compareVersions returns negative when current is newer than minimum")
    void compareVersionsCurrentNewer() {
        int result = Version.compareVersions("1.2.3", "1.2.4");
        assertThat(result).isLessThan(0); // min < current
    }

    @Test
    @DisplayName("compareVersions returns positive when current is older than minimum")
    void compareVersionsCurrentOlder() {
        int result = Version.compareVersions("1.3.0", "1.2.9");
        assertThat(result).isGreaterThan(0); // min > current
    }

    @Test
    @DisplayName("compareVersions treats SNAPSHOT (or non-numeric suffix) as lower than release")
    void compareVersionsSnapshotIsLower() {
        int result = Version.compareVersions("1.2.3", "1.2.3-SNAPSHOT");
        assertThat(result).isGreaterThan(0); // min (release) > current (snapshot)
    }

    @Test
    @DisplayName("compareVersions treats different lengths like 1.2 == 1.2.0")
    void compareVersionsDifferentLengths() {
        assertThat(Version.compareVersions("1.2", "1.2.0")).isEqualTo(0);
    }

    @Test
    @DisplayName("compareVersions treats non-numeric parts as lower than numeric parts")
    void compareVersionsNonNumericParts() {
        // "a" parsed as -1, numeric "0" parsed as 0 => -1 < 0 so min < current
        int result = Version.compareVersions("1.a.3", "1.0.3");
        assertThat(result).isLessThan(0);
    }

    @Test
    @DisplayName("compareVersions returns 0 for equal snapshot versions")
    void compareSnapshotsEqual() {
        assertThat(Version.compareVersions("1.2.3-SNAPSHOT", "1.2.3-SNAPSHOT")).isEqualTo(0);
    }

    @Test
    @DisplayName("compareVersions treats snapshot as older than corresponding release")
    void compareSnapshotOlderThanRelease() {
        int result = Version.compareVersions("1.2.3-SNAPSHOT", "1.2.3");
        assertThat(result).isLessThan(0); // snapshot < release
    }

    @Test
    @DisplayName("compareVersions orders snapshot versions by numeric parts (older/younger)")
    void compareSnapshotsNumericOrder() {
        assertThat(Version.compareVersions("1.2.3-SNAPSHOT", "1.2.4-SNAPSHOT")).isLessThan(0);
        assertThat(Version.compareVersions("1.2.5-SNAPSHOT", "1.2.4-SNAPSHOT")).isGreaterThan(0);
    }

}
