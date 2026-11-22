package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Calculates student registry metrics for Variant 6 and formats/writes reports.
 */
public final class StudentReportService {

    private static final String DELIMITER = ";";

    private StudentReportService() {
    }

    /**
     * Aggregated statistics for valid student records.
     *
     * @param validCount       number of valid student records
     * @param averageScore     arithmetic mean of student average scores (0.0 if none)
     * @param maxScore         highest student average score (0.0 if none)
     * @param scholarshipCount number of students receiving a scholarship
     */
    public record StudentMetrics(
            int validCount,
            double averageScore,
            double maxScore,
            int scholarshipCount) {
    }

    /**
     * Computes the four Variant 6 metrics from validated CSV lines.
     *
     * @param validLines list of validated records in format {@code group;name;average;course;scholarship}
     * @return computed {@link StudentMetrics}
     */
    public static StudentMetrics calculate(List<String> validLines) {
        Objects.requireNonNull(validLines, "validLines must not be null");

        int validCount = 0;
        double totalScore = 0.0;
        double maxScore = Double.NEGATIVE_INFINITY;
        int scholarshipCount = 0;

        for (String line : validLines) {
            String[] fields = line.split(DELIMITER, -1);
            double score = Double.parseDouble(fields[2].trim());
            boolean scholarship = Boolean.parseBoolean(fields[4].trim());

            validCount++;
            totalScore += score;
            maxScore = Math.max(maxScore, score);
            if (scholarship) {
                scholarshipCount++;
            }
        }

        if (validCount == 0) {
            return new StudentMetrics(0, 0.0, 0.0, 0);
        }

        double averageScore = totalScore / validCount;
        return new StudentMetrics(validCount, averageScore, maxScore, scholarshipCount);
    }

    /**
     * Formats the summary report in a locale-independent way.
     *
     * @param metrics calculated metrics
     * @param errors  validation error messages with line numbers
     * @return formatted report text ready for console and file output
     */
    public static String formatReport(StudentMetrics metrics, List<String> errors) {
        Objects.requireNonNull(metrics, "metrics must not be null");
        Objects.requireNonNull(errors, "errors must not be null");

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.ROOT, "=== Звіт: Реєстр студентів ===%n"));
        sb.append(String.format(Locale.ROOT, "%-28s %10s%n", "Показник", "Значення"));
        sb.append(String.format(Locale.ROOT, "%-28s %10d%n", "Коректних записів:", metrics.validCount()));
        sb.append(String.format(Locale.ROOT, "%-28s %10.2f%n", "Середній бал:", metrics.averageScore()));
        sb.append(String.format(Locale.ROOT, "%-28s %10.2f%n", "Найбільший бал:", metrics.maxScore()));
        sb.append(String.format(Locale.ROOT, "%-28s %10d%n", "Кількість стипендіатів:", metrics.scholarshipCount()));

        if (metrics.validCount() == 0) {
            sb.append(String.format(Locale.ROOT, "Увага: коректних записів не знайдено.%n"));
        }

        sb.append(String.format(Locale.ROOT, "Помилок обробки: %d%n", errors.size()));
        for (String error : errors) {
            sb.append(String.format(Locale.ROOT, " - %s%n", error));
        }
        return sb.toString();
    }

    /**
     * Writes the formatted report text to the specified path in UTF-8.
     *
     * @param output destination file path
     * @param report formatted report content
     * @throws IOException if the directory or file cannot be written
     */
    public static void writeReport(Path output, String report) throws IOException {
        Objects.requireNonNull(output, "output must not be null");
        Objects.requireNonNull(report, "report must not be null");

        Path parent = output.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(output, report, StandardCharsets.UTF_8);
    }
}