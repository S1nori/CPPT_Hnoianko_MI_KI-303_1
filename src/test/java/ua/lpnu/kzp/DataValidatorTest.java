package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import ua.lpnu.kzp.DataValidator.ValidationResult;

class DataValidatorTest {

    @Test
    void acceptsValidUkrainianRecord() {
        assertTrue(DataValidator.validateLine("КІ-301;Іван Петренко;87.5;3;true", 1).isEmpty());
    }

    @Test
    void rejectsBlankLine() {
        var error = DataValidator.validateLine("   ", 1);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("Рядок 1"));
    }

    @Test
    void rejectsWrongFieldCount() {
        var error = DataValidator.validateLine("КІ-301;Іван Петренко;87.5;3", 2);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("очікується 5 полів"));
    }

    @Test
    void rejectsBlankMandatoryTextField() {
        var error = DataValidator.validateLine("КІ-303;;81.0;3;false", 6);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("порожнє обов'язкове текстове поле"));
    }

    @Test
    void rejectsNonNumericScore() {
        var error = DataValidator.validateLine("КІ-302;Павло Мельник;помилка;2;true", 5);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("помилковий формат"));
    }

    @Test
    void rejectsNegativeAverageScore() {
        var error = DataValidator.validateLine("КІ-301;Олег Шевченко;-10.5;2;true", 3);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("не може бути від'ємним"));
    }

    @Test
    void rejectsNonPositiveCourse() {
        var error = DataValidator.validateLine("КІ-301;Олег Шевченко;80.0;0;true", 4);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("курс"));
    }

    @Test
    void rejectsInvalidBooleanScholarship() {
        var error = DataValidator.validateLine("КІ-301;Олег Шевченко;80.0;2;так", 7);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("true або false"));
    }

    @Test
    void separatesValidAndInvalidLinesFromSampleDataset() {
        List<String> lines = List.of(
                "КІ-301;Іван Петренко;87.5;3;true",
                "КІ-302;Марія Коваль;92.0;3;false",
                "КІ-301;Олег Шевченко;76.5;2;true",
                "КІ-306;Анастасія Бондар;82.0;3;false",
                "КІ-302;Павло Мельник;помилка;2;true",
                "КІ-303;;81.0;3;false");

        ValidationResult result = DataValidator.validateAll(lines);

        assertEquals(4, result.validLines().size());
        assertEquals(2, result.errors().size());
    }
}