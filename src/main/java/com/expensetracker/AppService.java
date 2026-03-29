package com.expensetracker;

import com.expensetracker.models.*;
import java.util.*;

public class AppService {
    private DatabaseManager db;

    public AppService() throws AppException {
        db = DatabaseManager.getInstance();
    }

    public void createTransaction(Transaction transaction) throws AppException {
        if (transaction.getAmount() <= 0) {
            throw new AppException("Amount must be > 0");
        }
        db.addTransaction(transaction);
    }

    public List<Transaction> getAllTransactions() throws AppException {
        return db.getAllTransactions();
    }

    public void deleteTransaction(int id) throws AppException {
        db.deleteTransaction(id);
    }

    public void createCategory(Category category) throws AppException {
        db.addCategory(category);
    }

    public List<Category> getAllCategories() throws AppException {
        return db.getAllCategories();
    }

    public List<Category> getCategoriesByType(String type) throws AppException {
        return db.getCategoriesByType(type);
    }

    public void deleteCategory(int id) throws AppException {
        db.deleteCategory(id);
    }

    public double getTotalBalance() throws AppException {
        double income = 0, expense = 0;
        for (Transaction t : getAllTransactions()) {
            if ("INCOME".equals(t.getType())) {
                income += t.getAmount();
            } else {
                expense += t.getAmount();
            }
        }
        return income - expense;
    }

    public Map<String, Double> getExpensesByCategory() throws AppException {
        Map<String, Double> expenses = new HashMap<>();
        for (Transaction t : getAllTransactions()) {
            if ("EXPENSE".equals(t.getType())) {
                String cat = t.getCategory().getName();
                expenses.put(cat, expenses.getOrDefault(cat, 0.0) + t.getAmount());
            }
        }
        return expenses;
    }
}
