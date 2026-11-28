package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvReaderTest {

    @Test
    void readLinesPreservesUkrainianUtf8Text(@TempDir Path tempDir) throws IOException {
        Path csvFile = tempDir.resolve("students.csv");
        List<String> expected = List.of(
                "Іван Петренко;КІ-301;3;87.5;true",
                "Марія Коваль;КІ-302;3;92.0;false");
        Files.write(csvFile, expected, StandardCharsets.UTF_8);

        List<String> actual = CsvReader.readLines(csvFile);
        assertEquals(expected, actual);
    }
}