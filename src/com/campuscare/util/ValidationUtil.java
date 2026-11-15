package com.campuscare.util;

import com.campuscare.exception.ValidationException;
import java.util.regex.Pattern;

/**
 * Validation utilities for student credentials, complaints, and form fields.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s-]{8,15}$");
    private static final Pattern ROLL_PATTERN = Pattern.compile("^[0-9]{2}[A-Za-z]{2,4}[0-9]{2,5}$");

    private ValidationUtil() {}

    public static void validateNotEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, fieldName + " cannot be blank.");
        }
    }

    public static void validateEmail(String email) throws ValidationException {
        validateNotEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Email", "Please provide a valid email address (e.g., student@campus.edu).");
        }
    }

    public static void validatePhone(String phone) throws ValidationException {
        if (phone != null && !phone.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                throw new ValidationException("Phone", "Phone number must be between 8 and 15 digits.");
            }
        }
    }

    public static void validateRollNumber(String rollNumber) throws ValidationException {
        validateNotEmpty(rollNumber, "Roll Number");
        if (rollNumber.trim().length() < 5) {
            throw new ValidationException("Roll Number", "Roll number must be at least 5 characters.");
        }
    }

    public static void validatePasswordStrength(String password) throws ValidationException {
        validateNotEmpty(password, "Password");
        if (password.length() < 6) {
            throw new ValidationException("Password", "Password must be at least 6 characters long.");
        }
    }
}
