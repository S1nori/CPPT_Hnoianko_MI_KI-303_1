package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainTest {

    @Test
    void versionMatchesSemanticVersionWithBuildMetadata() {
        String version = BuildInfo.version();
        assertTrue(
                version.matches("\\d+\\.\\d+\\.\\d+\\+\\w+"),
                "Неочікуваний формат версії: %s".formatted(version));
    }

    @Test
    void mainProcessesInputCsvAndWritesReportFile(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("students.csv");
        Path output = tempDir.resolve("out").resolve("report.txt");

        String csvContent = String.join(
                System.lineSeparator(),
                "КІ-301;Іван Петренко;87.5;3;true",
                "КІ-302;Марія Коваль;92.0;3;false",
                "КІ-302;Павло Мельник;помилка;2;true");
        Files.writeString(input, csvContent, StandardCharsets.UTF_8);

        Main.main(new String[] {"--input", input.toString(), "--output", output.toString()});

        assertTrue(Files.exists(output));
        String report = Files.readString(output, StandardCharsets.UTF_8);
        assertTrue(report.contains("Коректних записів:"));
        assertTrue(report.contains("89.75"));
        assertTrue(report.contains("92.00"));
        assertTrue(report.contains("Рядок 3"));
    }
}