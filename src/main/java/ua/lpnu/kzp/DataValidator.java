package ua.lpnu.kzp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Validates student registry CSV records using {@link Student#fromCsv(String)}.
 */
public final class DataValidator {

    private DataValidator() {
    }

    /**
     * Immutable container holding valid {@link Student} objects and validation errors.
     *
     * @param validStudents list of validated student entities
     * @param errors        list of error descriptions with 1-based line numbers
     */
    public record ValidationResult(List<Student> validStudents, List<String> errors) {

        /**
         * Creates defensive immutable copies of the provided lists.
         */
        public ValidationResult {
            validStudents = List.copyOf(validStudents);
            errors = List.copyOf(errors);
        }

        /**
         * Preserves backward compatibility with Lab 01 tests expecting valid line count/list.
         *
         * @return list of formatted string representations for valid students
         */
        public List<String> validLines() {
            return validStudents.stream().map(Student::toString).toList();
        }
    }

    /**
     * Validates all CSV lines and separates valid {@link Student} records from error messages.
     *
     * @param lines raw lines read from the input CSV file
     * @return {@link ValidationResult} containing valid students and validation errors
     */
    public static ValidationResult validateAll(List<String> lines) {
        Objects.requireNonNull(lines, "lines must not be null");
        List<Student> validStudents = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            int lineNumber = i + 1;
            String line = lines.get(i);
            if (line == null) {
                errors.add("Рядок %d: порожній рядок".formatted(lineNumber));
                continue;
            }
            try {
                validStudents.add(Student.fromCsv(line));
            } catch (IllegalArgumentException exception) {
                errors.add("Рядок %d: %s".formatted(lineNumber, exception.getMessage()));
            }
        }
        return new ValidationResult(validStudents, errors);
    }

    /**
     * Validates a single CSV record line.
     *
     * @param line       raw CSV line
     * @param lineNumber 1-based line number in the source file
     * @return {@link Optional} containing the error message if invalid, or empty if valid
     */
    public static Optional<String> validateLine(String line, int lineNumber) {
        if (line == null) {
            return Optional.of("Рядок %d: порожній рядок".formatted(lineNumber));
        }
        try {
            Student.fromCsv(line);
            return Optional.empty();
        } catch (IllegalArgumentException exception) {
            return Optional.of("Рядок %d: %s".formatted(lineNumber, exception.getMessage()));
        }
    }
}