package com.expensetracker.services;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.models.Transaction;
import java.util.List;

/**
 * Service layer for dashboard calculations
 * Provides real-time balance and statistics
 */
public class DashboardService {
    private TransactionService transactionService;

    public DashboardService() throws DatabaseException {
        this.transactionService = new TransactionService();
    }

    public double getTotalBalance() throws DatabaseException {
        double totalIncome = getTotalIncome();
        double totalExpense = getTotalExpense();
        return totalIncome - totalExpense;
    }

    public double getTotalIncome() throws DatabaseException {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return transactions.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpense() throws DatabaseException {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public int getTransactionCount() throws DatabaseException {
        return transactionService.getAllTransactions().size();
    }
}
