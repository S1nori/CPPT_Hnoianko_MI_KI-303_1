package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StudentTest {

    @Test
    void constructorCreatesValidStudentAndTrimsStrings() {
        Student student = new Student("  Іван Петренко  ", " КІ-301 ", 3, 87.5, true);

        assertEquals("Іван Петренко", student.getName());
        assertEquals("КІ-301", student.getGroup());
        assertEquals(3, student.getCourse());
        assertEquals(87.5, student.getAverage(), 0.0001);
        assertTrue(student.isScholarship());
    }

    @Test
    void constructorAllowsZeroAverageBoundary() {
        Student student = new Student("Олег Шевченко", "КІ-301", 1, 0.0, false);

        assertEquals(0.0, student.getAverage(), 0.0001);
        assertFalse(student.isScholarship());
    }

    @Test
    void constructorRejectsNullBlankAndInvalidNumericValues() {
        assertThrows(NullPointerException.class,
                () -> new Student(null, "КІ-301", 3, 85.0, true));
        assertThrows(NullPointerException.class,
                () -> new Student("Іван Петренко", null, 3, 85.0, true));
        assertThrows(IllegalArgumentException.class,
                () -> new Student("   ", "КІ-301", 3, 85.0, true));
        assertThrows(IllegalArgumentException.class,
                () -> new Student("Іван Петренко", "", 3, 85.0, true));
        assertThrows(IllegalArgumentException.class,
                () -> new Student("Іван Петренко", "КІ-301", 0, 85.0, true));
        assertThrows(IllegalArgumentException.class,
                () -> new Student("Іван Петренко", "КІ-301", -2, 85.0, true));
        assertThrows(IllegalArgumentException.class,
                () -> new Student("Іван Петренко", "КІ-301", 3, -0.1, true));
        assertThrows(IllegalArgumentException.class,
                () -> new Student("Іван Петренко", "КІ-301", 3, Double.NaN, true));
        assertThrows(IllegalArgumentException.class,
                () -> new Student("Іван Петренко", "КІ-301", 3, Double.POSITIVE_INFINITY, true));
    }

    @Test
    void fromCsvParsesValidRecordAndRejectsInvalidOnes() {
        Student parsed = Student.fromCsv("Марія Коваль;КІ-302;3;92.0;false");
        assertEquals("Марія Коваль", parsed.getName());
        assertEquals("КІ-302", parsed.getGroup());
        assertEquals(3, parsed.getCourse());
        assertEquals(92.0, parsed.getAverage(), 0.0001);
        assertFalse(parsed.isScholarship());

        assertThrows(NullPointerException.class, () -> Student.fromCsv(null));
        assertThrows(IllegalArgumentException.class, () -> Student.fromCsv("   "));
        assertThrows(IllegalArgumentException.class,
                () -> Student.fromCsv("Марія Коваль;КІ-302;3"));
        assertThrows(IllegalArgumentException.class,
                () -> Student.fromCsv("Марія Коваль;КІ-302;три;92.0;false"));
        assertThrows(IllegalArgumentException.class,
                () -> Student.fromCsv("Марія Коваль;КІ-302;3;-5.0;false"));
        assertThrows(IllegalArgumentException.class,
                () -> Student.fromCsv("Марія Коваль;КІ-302;3;92.0;так"));
    }

    @Test
    void toStringUsesRootLocaleDecimalPoint() {
        Student student = new Student("Іван Петренко", "КІ-301", 3, 87.5, true);
        String text = student.toString();

        assertTrue(text.contains("Іван Петренко"));
        assertTrue(text.contains("87.50"));
    }

    @Test
    void gradeScholarshipRecordSupportsValueEqualityAndValidation() {
        Student student = new Student("Іван Петренко", "КІ-301", 3, 87.5, true);
        GradeScholarship fromStudent = student.toGradeScholarship();
        GradeScholarship direct = new GradeScholarship(87.5, true);
        GradeScholarship different = new GradeScholarship(90.0, false);

        assertEquals(fromStudent, direct);
        assertNotEquals(direct, different);
        assertEquals(87.5, direct.average(), 0.0001);
        assertTrue(direct.scholarship());
        assertTrue(direct.toString().contains("87.50"));

        assertThrows(IllegalArgumentException.class,
                () -> new GradeScholarship(-1.0, false));
        assertThrows(IllegalArgumentException.class,
                () -> new GradeScholarship(Double.NaN, true));
    }
}