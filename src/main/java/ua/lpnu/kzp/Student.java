package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Immutable domain entity representing a student registry record (Variant 6).
 */
public final class Student {

    private static final String DELIMITER = ";";
    private static final int EXPECTED_FIELDS = 5;

    private final String name;
    private final String group;
    private final int course;
    private final double average;
    private final boolean scholarship;

    /**
     * Creates a validated student registry record.
     *
     * @param name        student full name (non-null, non-blank)
     * @param group       academic group code (non-null, non-blank)
     * @param course      study year/course (strictly positive)
     * @param average     grade point average (non-negative finite number)
     * @param scholarship whether the student receives a scholarship
     */
    public Student(
            String name,
            String group,
            int course,
            double average,
            boolean scholarship) {
        Objects.requireNonNull(name, "ПІБ студента не може бути null");
        Objects.requireNonNull(group, "Шифр групи не може бути null");

        String trimmedName = name.trim();
        String trimmedGroup = group.trim();

        if (trimmedName.isEmpty() || trimmedGroup.isEmpty()) {
            throw new IllegalArgumentException(
                    "порожнє обов'язкове текстове поле (ПІБ або група)");
        }
        if (course <= 0) {
            throw new IllegalArgumentException(
                    "номер курсу має бути додатним цілим числом");
        }
        if (Double.isNaN(average) || Double.isInfinite(average) || average < 0.0) {
            throw new IllegalArgumentException(
                    "середній бал не може бути від'ємним");
        }

        this.name = trimmedName;
        this.group = trimmedGroup;
        this.course = course;
        this.average = average;
        this.scholarship = scholarship;
    }

    /**
     * Parses a CSV line in format {@code name;group;course;average;scholarship} into a {@link Student}.
     *
     * @param line raw CSV line
     * @return validated {@link Student} instance
     * @throws IllegalArgumentException if the structure, field types, or invariants are invalid
     */
    public static Student fromCsv(String line) {
        Objects.requireNonNull(line, "Рядок не може бути null");
        if (line.isBlank()) {
            throw new IllegalArgumentException("порожній рядок");
        }

        String[] fields = line.split(DELIMITER, -1);
        if (fields.length != EXPECTED_FIELDS) {
            throw new IllegalArgumentException(
                    "очікується %d полів, отримано %d"
                            .formatted(EXPECTED_FIELDS, fields.length));
        }

        String name = fields[0].trim();
        String group = fields[1].trim();
        if (name.isEmpty() || group.isEmpty()) {
            throw new IllegalArgumentException(
                    "порожнє обов'язкове текстове поле (ПІБ або група)");
        }

        int course;
        double average;
        try {
            course = Integer.parseInt(fields[2].trim());
            average = Double.parseDouble(fields[3].trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "числове поле має помилковий формат", exception);
        }

        String scholarshipRaw = fields[4].trim();
        if (!"true".equalsIgnoreCase(scholarshipRaw)
                && !"false".equalsIgnoreCase(scholarshipRaw)) {
            throw new IllegalArgumentException(
                    "поле стипендії має бути true або false");
        }
        boolean scholarship = Boolean.parseBoolean(scholarshipRaw);

        return new Student(name, group, course, average, scholarship);
    }

    /**
     * Returns the student's full name.
     *
     * @return full name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the student's academic group.
     *
     * @return group code
     */
    public String getGroup() {
        return group;
    }

    /**
     * Returns the student's study course.
     *
     * @return positive course number
     */
    public int getCourse() {
        return course;
    }

    /**
     * Returns the student's average grade.
     *
     * @return non-negative average score
     */
    public double getAverage() {
        return average;
    }

    /**
     * Returns whether the student receives a scholarship.
     *
     * @return {@code true} if receiving a scholarship, {@code false} otherwise
     */
    public boolean isScholarship() {
        return scholarship;
    }

    /**
     * Returns an immutable {@link GradeScholarship} record for this student.
     *
     * @return grade and scholarship pair
     */
    public GradeScholarship toGradeScholarship() {
        return new GradeScholarship(average, scholarship);
    }

    /**
     * Returns a formatted, locale-independent string representation of the student.
     *
     * @return formatted student description
     */
    @Override
    public String toString() {
        return String.format(
                Locale.ROOT,
                "%s (%s, %d курс): середній бал %.2f, стипендія: %s",
                name,
                group,
                course,
                average,
                scholarship ? "так" : "ні");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Student student)) {
            return false;
        }
        return course == student.course
                && Double.compare(average, student.average) == 0
                && scholarship == student.scholarship
                && name.equals(student.name)
                && group.equals(student.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, group, course, average, scholarship);
    }
}