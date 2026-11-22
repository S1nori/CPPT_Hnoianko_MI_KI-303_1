package ua.lpnu.kzp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Provides application version and build metadata injected by Maven.
 */
public final class BuildInfo {

    private static final String RESOURCE = "/version.properties";

    private BuildInfo() {
    }

    /**
     * Returns the semantic version combined with the build identifier (e.g. {@code 1.0.0+local}).
     *
     * @return formatted version string
     * @throws IllegalStateException if the resource or required properties are missing
     * @throws UncheckedIOException  if the resource cannot be read
     */
    public static String version() {
        Properties properties = new Properties();
        try (InputStream input = BuildInfo.class.getResourceAsStream(RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException("Ресурс %s не знайдено".formatted(RESOURCE));
            }
            try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }

        String version = properties.getProperty("version");
        String build = properties.getProperty("build");
        if (version == null || build == null || version.startsWith("${")) {
            throw new IllegalStateException("У %s не підставлено version або build".formatted(RESOURCE));
        }
        return "%s+%s".formatted(version, build);
    }
}