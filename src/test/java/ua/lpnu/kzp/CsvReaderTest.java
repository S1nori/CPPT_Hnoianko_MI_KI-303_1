
package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests CSV file reading functionality.
 */
class CsvReaderTest {

    /**
     * Checks that all six records are read from the input CSV file.
     *
     * @throws IOException if the input file cannot be read
     */
    @Test
    void readsAllLinesFromInputFile() throws IOException {
        Path input = Path.of("data", "input.csv");

        List<String> lines = CsvReader.readLines(input);

        assertEquals(6, lines.size());
        assertTrue(lines.get(0).contains("Іван Петренко"));
    }
}
