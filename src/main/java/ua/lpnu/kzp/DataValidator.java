package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Validates student registry CSV records according to Variant 6 rules.
 * Expected format: {@code group;name;average;course;scholarship}.
 */
public final class DataValidator {

    private static final String DELIMITER = ";";
    private static final int EXPECTED_FIELDS = 5;

    private DataValidator() {
    }

    /**
     * Immutable container holding valid CSV lines and human-readable validation errors.
     *
     * @param validLines list of lines that passed all validation checks
     * @param errors     list of error descriptions with 1-based line numbers
     */
    public record ValidationResult(List<String> validLines, List<String> errors) {

        /**
         * Creates defensive immutable copies of the provided lists.
         */
        public ValidationResult {
            validLines = List.copyOf(validLines);
            errors = List.copyOf(errors);
        }
    }

    /**
     * Validates all CSV lines and separates valid records from error messages.
     *
     * @param lines raw lines read from the input CSV file
     * @return {@link ValidationResult} containing valid lines and validation errors
     */
    public static ValidationResult validateAll(List<String> lines) {
        Objects.requireNonNull(lines, "lines must not be null");
        List<String> validLines = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            int lineNumber = i + 1;
            String line = lines.get(i);
            Optional<String> error = validateLine(line, lineNumber);
            if (error.isPresent()) {
                errors.add(error.get());
            } else {
                validLines.add(line);
            }
        }
        return new ValidationResult(validLines, errors);
    }

    /**
     * Validates a single CSV record line.
     *
     * @param line       raw CSV line
     * @param lineNumber 1-based line number in the source file
     * @return {@link Optional} containing the error message if invalid, or empty if valid
     */
    public static Optional<String> validateLine(String line, int lineNumber) {
        if (line == null || line.isBlank()) {
            return Optional.of("Рядок %d: порожній рядок".formatted(lineNumber));
        }

        String[] fields = line.split(DELIMITER, -1);
        if (fields.length != EXPECTED_FIELDS) {
            return Optional.of(
                    "Рядок %d: очікується %d полів, отримано %d"
                            .formatted(lineNumber, EXPECTED_FIELDS, fields.length));
        }

        String group = fields[0].trim();
        String name = fields[1].trim();
        if (group.isEmpty() || name.isEmpty()) {
            return Optional.of(
                    "Рядок %d: порожнє обов'язкове текстове поле (група або ПІБ)"
                            .formatted(lineNumber));
        }

        try {
            double average = Double.parseDouble(fields[2].trim());
            int course = Integer.parseInt(fields[3].trim());

            if (Double.isNaN(average) || Double.isInfinite(average) || average < 0.0) {
                return Optional.of(
                        "Рядок %d: середній бал не може бути від'ємним".formatted(lineNumber));
            }
            if (course <= 0) {
                return Optional.of(
                        "Рядок %d: номер курсу має бути додатним цілим числом"
                                .formatted(lineNumber));
            }
        } catch (NumberFormatException exception) {
            return Optional.of(
                    "Рядок %d: числове поле має помилковий формат".formatted(lineNumber));
        }

        String scholarshipRaw = fields[4].trim();
        if (!"true".equalsIgnoreCase(scholarshipRaw)
                && !"false".equalsIgnoreCase(scholarshipRaw)) {
            return Optional.of(
                    "Рядок %d: поле стипендії має бути true або false".formatted(lineNumber));
        }

        return Optional.empty();
    }
}