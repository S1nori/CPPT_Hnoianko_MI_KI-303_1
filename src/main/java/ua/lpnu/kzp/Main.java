package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import ua.lpnu.kzp.DataValidator.ValidationResult;
import ua.lpnu.kzp.StudentReportService.StudentMetrics;

/**
 * Entry point of the KZP console application (Variant 6: Student Registry).
 */
public final class Main {

    private static final Path DEFAULT_INPUT = Path.of("data", "input.csv");
    private static final Path DEFAULT_OUTPUT = Path.of("out", "report.txt");

    private Main() {
    }

    /**
     * Parses CLI arguments, reads input records, converts them into {@link Student} entities,
     * computes metrics, and outputs the report to both the console and the destination file.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        if (args.length == 1 && "--version".equals(args[0])) {
            System.out.printf("%s%n", BuildInfo.version());
            return;
        }

        if (args.length == 1 && "--help".equals(args[0])) {
            printHelp();
            return;
        }

        Path inputPath = DEFAULT_INPUT;
        Path outputPath = DEFAULT_OUTPUT;

        int i = 0;
        while (i < args.length) {
            switch (args[i]) {
                case "--help" -> {
                    printHelp();
                    return;
                }
                case "--version" -> {
                    System.out.printf("%s%n", BuildInfo.version());
                    return;
                }
                case "--input" -> {
                    if (i + 1 >= args.length) {
                        System.err.printf("Помилка: після --input потрібно вказати шлях до файла.%n");
                        return;
                    }
                    inputPath = Path.of(args[i + 1]);
                    i += 2;
                }
                case "--output" -> {
                    if (i + 1 >= args.length) {
                        System.err.printf("Помилка: після --output потрібно вказати шлях до файла.%n");
                        return;
                    }
                    outputPath = Path.of(args[i + 1]);
                    i += 2;
                }
                default -> {
                    System.err.printf("Невідомий аргумент: %s%n", args[i]);
                    printHelp();
                    return;
                }
            }
        }

        try {
            List<String> rawLines = CsvReader.readLines(inputPath);
            ValidationResult validation = DataValidator.validateAll(rawLines);
            StudentMetrics metrics = StudentReportService.calculate(validation.validStudents());
            String report = StudentReportService.formatReport(metrics, validation.errors());

            System.out.printf(Locale.ROOT, "%s", report);
            StudentReportService.writeReport(outputPath, report);
            System.out.printf(Locale.ROOT, "Звіт збережено у файл: %s%n", outputPath);
        } catch (IOException exception) {
            System.err.printf(
                    Locale.ROOT,
                    "Помилка вводу-виводу під час обробки файлів: %s%n",
                    exception.getMessage());
        }
    }

    private static void printHelp() {
        System.out.printf(
                "Використання: java -jar target/lab01-1.1.0.jar [--help] [--version] [--input <файл>] [--output <файл>]%n"
                        + "  --help             показати довідку%n"
                        + "  --version          вивести версію програми та номер збірки%n"
                        + "  --input <файл>     шлях до вхідного CSV-файла (за замовчуванням: data/input.csv)%n"
                        + "  --output <файл>    шлях до вихідного файла звіту (за замовчуванням: out/report.txt)%n");
    }
}