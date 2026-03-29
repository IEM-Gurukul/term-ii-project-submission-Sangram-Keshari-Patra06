package com.expensetracker;

import com.expensetracker.models.*;
import java.util.*;

public class AppService {
    private DatabaseManager db;

    public AppService() throws AppException {
        db = DatabaseManager.getInstance();
    }

    public void createTransaction(Transaction transaction) throws AppException {
        validateTransaction(transaction);
        db.addTransaction(transaction);
    }

    public Transaction getTransaction(int id) throws AppException {
        return db.getTransaction(id);
    }

    public List<Transaction> getAllTransactions() throws AppException {
        return db.getAllTransactions();
    }

    public List<Transaction> getTransactionsByFilter(TransactionFilter filter) throws AppException {
        return db.getTransactionsByFilter(filter);
    }

    public void updateTransaction(Transaction transaction) throws AppException {
        validateTransaction(transaction);
        db.updateTransaction(transaction);
    }

    public void deleteTransaction(int id) throws AppException {
        db.deleteTransaction(id);
    }

    public void createCategory(Category category) throws AppException {
        db.addCategory(category);
    }

    public Category getCategory(int id) throws AppException {
        return db.getCategory(id);
    }

    public List<Category> getAllCategories() throws AppException {
        return db.getAllCategories();
    }

    public List<Category> getCategoriesByType(String type) throws AppException {
        return db.getCategoriesByType(type);
    }

    public void updateCategory(Category category) throws AppException {
        db.updateCategory(category);
    }

    public void deleteCategory(int id) throws AppException {
        db.deleteCategory(id);
    }

    public double getTotalBalance() throws AppException {
        return getTotalIncome() - getTotalExpense();
    }

    public double getTotalIncome() throws AppException {
        return db.getAllTransactions().stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpense() throws AppException {
        return db.getAllTransactions().stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public int getTransactionCount() throws AppException {
        return db.getAllTransactions().size();
    }

    public Map<String, Double> getExpensesByCategory() throws AppException {
        Map<String, Double> expenses = new LinkedHashMap<>();
        for (Transaction t : db.getAllTransactions()) {
            if ("EXPENSE".equals(t.getType())) {
                String cat = t.getCategory().getName();
                expenses.put(cat, expenses.getOrDefault(cat, 0.0) + t.getAmount());
            }
        }
        return expenses;
    }

    public Map<String, Double> getIncomeByCategory() throws AppException {
        Map<String, Double> income = new LinkedHashMap<>();
        for (Transaction t : db.getAllTransactions()) {
            if ("INCOME".equals(t.getType())) {
                String cat = t.getCategory().getName();
                income.put(cat, income.getOrDefault(cat, 0.0) + t.getAmount());
            }
        }
        return income;
    }

    private void validateTransaction(Transaction transaction) throws AppException {
        if (transaction.getAmount() <= 0) {
            throw new AppException("Amount must be greater than zero");
        }
        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
            throw new AppException("Description cannot be empty");
        }
        if (transaction.getCategory() == null) {
            throw new AppException("Category must be selected");
        }
        if (transaction.getDateTime() == null) {
            throw new AppException("Date and time must be specified");
        }
    }
}
