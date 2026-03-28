package com.expensetracker.exceptions;

/**
 * Custom exception for transaction-related errors
 */
public class TransactionException extends Exception {
    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
