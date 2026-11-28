package ua.lpnu.kzp;

import java.util.Locale;

/**
 * Immutable auxiliary record holding a student's average grade and scholarship status (Variant 6).
 *
 * @param average     grade point average (non-negative finite number)
 * @param scholarship whether the student receives a scholarship
 */
public record GradeScholarship(double average, boolean scholarship) {

    /**
     * Validates that the grade point average is a non-negative finite number.
     */
    public GradeScholarship {
        if (Double.isNaN(average) || Double.isInfinite(average) || average < 0.0) {
            throw new IllegalArgumentException("Середній бал не може бути від'ємним");
        }
    }

    /**
     * Formats the grade and scholarship status in a locale-independent way.
     *
     * @return formatted text representation
     */
    @Override
    public String toString() {
        return String.format(
                Locale.ROOT,
                "бал=%.2f, стипендія=%s",
                average,
                scholarship ? "так" : "ні");
    }
}