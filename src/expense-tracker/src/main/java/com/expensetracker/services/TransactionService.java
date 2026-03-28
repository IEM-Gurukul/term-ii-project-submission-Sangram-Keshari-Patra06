package com.expensetracker.services;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.exceptions.ValidationException;
import com.expensetracker.models.*;
import com.expensetracker.persistence.TransactionDAO;
import java.util.List;

/**
 * Service layer for transaction operations
 * Handles business logic and validation
 */
public class TransactionService {
    private TransactionDAO transactionDAO;

    public TransactionService() throws DatabaseException {
        this.transactionDAO = new TransactionDAO();
    }

    public void createTransaction(Transaction transaction) throws ValidationException, DatabaseException {
        validateTransaction(transaction);
        transactionDAO.addTransaction(transaction);
    }

    public Transaction getTransaction(int id) throws DatabaseException {
        return transactionDAO.getTransactionById(id);
    }

    public List<Transaction> getAllTransactions() throws DatabaseException {
        return transactionDAO.getAllTransactions();
    }

    public List<Transaction> getTransactionsByFilter(TransactionFilter filter) throws DatabaseException {
        return transactionDAO.getTransactionsByFilter(filter);
    }

    public void updateTransaction(Transaction transaction) throws ValidationException, DatabaseException {
        validateTransaction(transaction);
        transactionDAO.updateTransaction(transaction);
    }

    public void deleteTransaction(int id) throws DatabaseException {
        transactionDAO.deleteTransaction(id);
    }

    private void validateTransaction(Transaction transaction) throws ValidationException {
        if (transaction.getAmount() <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }
        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
            throw new ValidationException("Description cannot be empty");
        }
        if (transaction.getCategory() == null) {
            throw new ValidationException("Category must be selected");
        }
        if (transaction.getDateTime() == null) {
            throw new ValidationException("Date and time must be specified");
        }
    }
}
