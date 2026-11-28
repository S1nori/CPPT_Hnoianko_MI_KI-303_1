package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import ua.lpnu.kzp.DataValidator.ValidationResult;

class DataValidatorTest {

    @Test
    void validRecordWithUkrainianCharactersPassesValidation() {
        Optional<String> error = DataValidator.validateLine(
                "Іван Петренко;КІ-301;3;87.5;true", 1);
        assertTrue(error.isEmpty());
    }

    @Test
    void blankLineReturnsError() {
        Optional<String> error = DataValidator.validateLine("   ", 2);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("Рядок 2"));
    }

    @Test
    void wrongFieldCountReturnsError() {
        Optional<String> error = DataValidator.validateLine("Іван Петренко;КІ-301;3;87.5", 3);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("очікується 5 полів"));
    }

    @Test
    void emptyNameOrGroupReturnsError() {
        Optional<String> emptyName = DataValidator.validateLine(";КІ-303;3;81.0;false", 4);
        Optional<String> emptyGroup = DataValidator.validateLine("Олег Шевченко;;2;76.5;true", 5);
        assertTrue(emptyName.isPresent());
        assertTrue(emptyGroup.isPresent());
    }

    @Test
    void nonNumericCourseOrAverageReturnsError() {
        Optional<String> badCourse = DataValidator.validateLine(
                "Марія Коваль;КІ-302;третій;92.0;false", 6);
        Optional<String> badAverage = DataValidator.validateLine(
                "Павло Мельник;КІ-302;2;помилка;true", 7);
        assertTrue(badCourse.isPresent());
        assertTrue(badAverage.isPresent());
    }

    @Test
    void negativeAverageOrNonPositiveCourseReturnsError() {
        Optional<String> negativeAverage = DataValidator.validateLine(
                "Анастасія Бондар;КІ-306;3;-5.0;false", 8);
        Optional<String> zeroCourse = DataValidator.validateLine(
                "Анастасія Бондар;КІ-306;0;82.0;false", 9);
        assertTrue(negativeAverage.isPresent());
        assertTrue(zeroCourse.isPresent());
    }

    @Test
    void invalidScholarshipBooleanReturnsError() {
        Optional<String> error = DataValidator.validateLine(
                "Іван Петренко;КІ-301;3;87.5;yes", 10);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("true або false"));
    }

    @Test
    void validateAllSeparatesValidAndInvalidLines() {
        List<String> lines = List.of(
                "Іван Петренко;КІ-301;3;87.5;true",
                "Павло Мельник;КІ-302;2;помилка;true",
                "Марія Коваль;КІ-302;3;92.0;false");

        ValidationResult result = DataValidator.validateAll(lines);
        assertEquals(2, result.validLines().size());
        assertEquals(1, result.errors().size());
    }
}