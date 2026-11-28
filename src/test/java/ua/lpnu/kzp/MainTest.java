package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainTest {

    @Test
    void versionContainsProjectVersionAndBuildNumber() {
        String version = BuildInfo.version();
        assertTrue(
                version.matches("\\d+\\.\\d+\\.\\d+\\+\\w+"),
                "Неочікуваний формат версії: %s".formatted(version));
    }

    @Test
    void mainProcessesInputCsvAndWritesReport(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("input.csv");
        Path output = tempDir.resolve("out").resolve("report.txt");

        Files.write(
                input,
                List.of(
                        "Іван Петренко;КІ-301;3;87.5;true",
                        "Марія Коваль;КІ-302;3;92.0;false",
                        "Павло Мельник;КІ-302;2;помилка;true"),
                StandardCharsets.UTF_8);

        Main.main(new String[] {"--input", input.toString(), "--output", output.toString()});

        assertTrue(Files.exists(output));
        String report = Files.readString(output, StandardCharsets.UTF_8);
        assertTrue(report.contains(String.format(Locale.ROOT, "%-28s %10d", "Коректних записів:", 2)));
        assertTrue(report.contains(String.format(Locale.ROOT, "%-28s %10.2f", "Середній бал:", 89.75)));
        assertTrue(report.contains(String.format(Locale.ROOT, "%-28s %10.2f", "Найбільший бал:", 92.00)));
        assertTrue(report.contains(String.format(Locale.ROOT, "%-28s %10d", "Кількість стипендіатів:", 1)));
        assertTrue(report.contains("Помилок обробки: 1"));
    }
}