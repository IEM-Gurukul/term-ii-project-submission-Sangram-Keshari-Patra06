package com.expensetracker.services;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.models.*;
import java.util.*;

/**
 * Service layer for generating reports and statistics
 * Provides data visualization information
 */
public class ReportService {
    private TransactionService transactionService;

    public ReportService() throws DatabaseException {
        this.transactionService = new TransactionService();
    }

    public Map<String, Double> getExpensesByCategory() throws DatabaseException {
        Map<String, Double> expensesByCategory = new LinkedHashMap<>();
        List<Transaction> transactions = transactionService.getAllTransactions();

        for (Transaction transaction : transactions) {
            if ("EXPENSE".equals(transaction.getType())) {
                String categoryName = transaction.getCategory().getName();
                expensesByCategory.put(categoryName, 
                    expensesByCategory.getOrDefault(categoryName, 0.0) + transaction.getAmount());
            }
        }
        return expensesByCategory;
    }

    public Map<String, Double> getIncomeByCategory() throws DatabaseException {
        Map<String, Double> incomeByCategory = new LinkedHashMap<>();
        List<Transaction> transactions = transactionService.getAllTransactions();

        for (Transaction transaction : transactions) {
            if ("INCOME".equals(transaction.getType())) {
                String categoryName = transaction.getCategory().getName();
                incomeByCategory.put(categoryName, 
                    incomeByCategory.getOrDefault(categoryName, 0.0) + transaction.getAmount());
            }
        }
        return incomeByCategory;
    }

    public Map<String, Double> getCategoryExpensePercentage() throws DatabaseException {
        Map<String, Double> expensesByCategory = getExpensesByCategory();
        double totalExpense = expensesByCategory.values().stream().mapToDouble(Double::doubleValue).sum();

        Map<String, Double> percentages = new LinkedHashMap<>();
        if (totalExpense > 0) {
            for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
                percentages.put(entry.getKey(), (entry.getValue() / totalExpense) * 100);
            }
        }
        return percentages;
    }
}
