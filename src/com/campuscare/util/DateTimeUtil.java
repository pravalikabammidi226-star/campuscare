package com.campuscare.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility for modern Date and Time operations using java.time API.
 */
public final class DateTimeUtil {

    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    public static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    public static final DateTimeFormatter TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    private DateTimeUtil() {}

    public static String formatDisplay(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DISPLAY_FORMATTER);
    }

    public static String formatDateOnly(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DATE_ONLY_FORMATTER);
    }

    /**
     * Calculates hours remaining until deadline, or negative if overdue.
     */
    public static long hoursRemaining(LocalDateTime deadline) {
        if (deadline == null) return 0;
        Duration duration = Duration.between(LocalDateTime.now(), deadline);
        return duration.toHours();
    }

    /**
     * Returns a human-friendly relative time string (e.g. "2 hours ago", "in 3 days").
     */
    public static String toRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        if (duration.isNegative()) {
            Duration future = duration.abs();
            if (future.toMinutes() < 60) return "in " + future.toMinutes() + " mins";
            if (future.toHours() < 24) return "in " + future.toHours() + " hours";
            return "in " + future.toDays() + " days";
        }

        long seconds = duration.getSeconds();
        if (seconds < 60) return "just now";
        long minutes = duration.toMinutes();
        if (minutes < 60) return minutes + " min" + (minutes > 1 ? "s" : "") + " ago";
        long hours = duration.toHours();
        if (hours < 24) return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        long days = duration.toDays();
        return days + " day" + (days > 1 ? "s" : "") + " ago";
    }
}
