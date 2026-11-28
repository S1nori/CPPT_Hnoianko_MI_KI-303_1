package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void calculateComputesVariant6MetricsAccurately() {
        List<Student> students = List.of(
                Student.fromCsv("Іван Петренко;КІ-301;3;87.5;true"),
                Student.fromCsv("Марія Коваль;КІ-302;3;92.0;false"),
                Student.fromCsv("Олег Шевченко;КІ-301;2;76.5;true"),
                Student.fromCsv("Анастасія Бондар;КІ-306;3;82.0;false"));

        StudentMetrics metrics = StudentReportService.calculate(students);
        assertEquals(4, metrics.validCount());
        assertEquals(84.50, metrics.averageScore(), 0.0001);
        assertEquals(92.00, metrics.maxScore(), 0.0001);
        assertEquals(2, metrics.scholarshipCount());
    }

    @Test
    void calculateHandlesEmptyValidListWithoutDivisionByZero() {
        StudentMetrics metrics = StudentReportService.calculate(List.of());
        assertEquals(0, metrics.validCount());
        assertEquals(0.0, metrics.averageScore(), 0.0001);
        assertEquals(0.0, metrics.maxScore(), 0.0001);
        assertEquals(0, metrics.scholarshipCount());

        String report = StudentReportService.formatReport(
                metrics, List.of("Рядок 1: порожній рядок"));
        assertTrue(report.contains("Увага: коректних записів не знайдено."));
    }

    @Test
    void calculateRejectsNullStudentList() {
        assertThrows(NullPointerException.class, () -> StudentReportService.calculate(null));
    }

    @Test
    void writeReportCreatesParentDirectoriesAndSavesUtf8(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("nested").resolve("report.txt");
        StudentMetrics metrics = new StudentMetrics(1, 90.0, 90.0, 1);
        String report = StudentReportService.formatReport(metrics, List.of());

        StudentReportService.writeReport(outputFile, report);

        assertTrue(Files.exists(outputFile));
        String content = Files.readString(outputFile, StandardCharsets.UTF_8);
        assertTrue(content.contains("Реєстр студентів"));
        assertTrue(content.contains("90.00"));
    }
}