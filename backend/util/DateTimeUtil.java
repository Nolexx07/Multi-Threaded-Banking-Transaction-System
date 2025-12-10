package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations
 */
public class DateTimeUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * Get current timestamp as formatted string
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(FORMATTER);
    }
    
    /**
     * Get current LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    
    /**
     * Format LocalDateTime to string
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}

