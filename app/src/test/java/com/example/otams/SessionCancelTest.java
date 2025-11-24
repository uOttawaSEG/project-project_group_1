package com.example.otams;

import static org.junit.Assert.*;

import org.junit.Test;
import java.time.LocalDateTime;

public class SessionCancelTest {

    @Test
    public void cancelAllowed_moreThan24Hours() {
        boolean result = StudentSessionsActivity.testCanCancel(
                "2025-01-10",
                "10:00",
                LocalDateTime.of(2025, 1, 9, 9, 0)
        );
        assertTrue(result);
    }

    @Test
    public void cancelNotAllowed_lessThan24Hours() {
        boolean result = StudentSessionsActivity.testCanCancel(
                "2025-01-10",
                "10:00",
                LocalDateTime.of(2025, 1, 9, 20, 0) // 14 hours
        );
        assertFalse(result);
    }

    @Test
    public void cancelAllowed_exactly24Hours() {
        boolean result = StudentSessionsActivity.testCanCancel(
                "2025-01-10",
                "10:00",
                LocalDateTime.of(2025, 1, 9, 10, 0) // exactly 24
        );
        assertTrue(result);
    }
}
