package com.expensetracker.utils;

/**
 * Utility class for validation operations
 */
public class ValidationUtils {
    public static boolean isValidAmount(String amountString) {
        try {
            double amount = Double.parseDouble(amountString);
            return amount > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDescription(String description) {
        return description != null && !description.trim().isEmpty() && description.length() <= 255;
    }

    public static boolean isValidCategoryName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 50;
    }

    public static boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
