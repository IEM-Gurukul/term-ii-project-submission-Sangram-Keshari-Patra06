package com.expensetracker.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class for currency formatting
 */
public class CurrencyUtils {
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    public static String formatCurrency(double amount) {
        return currencyFormat.format(amount);
    }

    public static String formatCurrencyWithDollar(double amount) {
        return "$" + String.format("%.2f", amount);
    }

    public static double parseAmount(String amountString) throws NumberFormatException {
        return Double.parseDouble(amountString.replaceAll("[^\\d.-]", ""));
    }
}
