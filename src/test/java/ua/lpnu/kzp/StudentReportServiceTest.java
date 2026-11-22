package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ua.lpnu.kzp.StudentReportService.StudentMetrics;

class StudentReportServiceTest {

    @Test
    void calculatesMetricsForValidRecords() {
        List<String> validLines = List.of(
                "КІ-301;Іван Петренко;87.5;3;true",
                "КІ-302;Марія Коваль;92.0;3;false",
                "КІ-301;Олег Шевченко;76.5;2;true",
                "КІ-306;Анастасія Бондар;82.0;3;false");

        StudentMetrics metrics = StudentReportService.calculate(validLines);

        assertEquals(4, metrics.validCount());
        assertEquals(84.50, metrics.averageScore(), 0.0001);
        assertEquals(92.00, metrics.maxScore(), 0.0001);
        assertEquals(2, metrics.scholarshipCount());
    }

    @Test
    void handlesEmptyValidRecordsWithoutDivisionByZero() {
        StudentMetrics metrics = StudentReportService.calculate(List.of());

        assertEquals(0, metrics.validCount());
        assertEquals(0.0, metrics.averageScore(), 0.0001);
        assertEquals(0.0, metrics.maxScore(), 0.0001);
        assertEquals(0, metrics.scholarshipCount());

        String report = StudentReportService.formatReport(metrics, List.of("Рядок 1: порожній рядок"));
        assertTrue(report.contains("коректних записів не знайдено"));
        assertTrue(report.contains("Рядок 1: порожній рядок"));
    }

    @Test
    void formatsNumbersWithDotSeparatorAndWritesUtf8File(@TempDir Path tempDir) throws IOException {
        StudentMetrics metrics = new StudentMetrics(2, 85.25, 90.50, 1);
        String report = StudentReportService.formatReport(metrics, List.of());

        assertTrue(report.contains("85.25"));
        assertTrue(report.contains("90.50"));

        Path outputFile = tempDir.resolve("out").resolve("report.txt");
        StudentReportService.writeReport(outputFile, report);

        assertTrue(Files.exists(outputFile));
        assertEquals(report, Files.readString(outputFile, StandardCharsets.UTF_8));
    }
}