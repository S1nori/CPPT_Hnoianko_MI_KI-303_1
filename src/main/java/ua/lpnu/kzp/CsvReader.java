
package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Reads CSV input files using UTF-8 encoding.
 */
public final class CsvReader {

    /**
     * Private constructor prevents creating instances of this utility class.
     */
    private CsvReader() {
    }

    /**
     * Reads all lines from the specified CSV file.
     *
     * @param input path to the CSV file
     * @return list of lines read from the file
     * @throws IOException if the file cannot be read
     */
    public static List<String> readLines(Path input) throws IOException {
        return Files.readAllLines(input, StandardCharsets.UTF_8);
    }
}
