package com.expensetracker.ui.panels;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.models.*;
import com.expensetracker.services.TransactionService;
import com.expensetracker.utils.DateUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel for viewing and filtering transaction history
 */
public class HistoryPanel extends JPanel {
    private TransactionService transactionService;
    private JTable transactionTable;
    private DefaultTableModel tableModel;

    public HistoryPanel() throws DatabaseException {
        this.transactionService = new TransactionService();
        setupUI();
        loadTransactions();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Filter Panel
        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.WEST);

        // Table
        String[] columnNames = {"ID", "Type", "Amount", "Description", "Date", "Category"};
        tableModel = new DefaultTableModel(columnNames, 0);
        transactionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Filters"));

        // Start Date
        panel.add(new JLabel("Start Date:"));
        JTextField startDateField = new JTextField(10);
        panel.add(startDateField);

        // End Date
        panel.add(new JLabel("End Date:"));
        JTextField endDateField = new JTextField(10);
        panel.add(endDateField);

        // Min Amount
        panel.add(new JLabel("Min Amount:"));
        JTextField minAmountField = new JTextField(10);
        panel.add(minAmountField);

        // Max Amount
        panel.add(new JLabel("Max Amount:"));
        JTextField maxAmountField = new JTextField(10);
        panel.add(maxAmountField);

        // Search Button
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            try {
                TransactionFilter filter = new TransactionFilter();
                if (!startDateField.getText().isEmpty()) {
                    filter.setStartDate(DateUtils.getStartOfDay(DateUtils.parseDate(startDateField.getText())));
                }
                if (!endDateField.getText().isEmpty()) {
                    filter.setEndDate(DateUtils.getEndOfDay(DateUtils.parseDate(endDateField.getText())));
                }
                if (!minAmountField.getText().isEmpty()) {
                    filter.setMinAmount(Double.parseDouble(minAmountField.getText()));
                }
                if (!maxAmountField.getText().isEmpty()) {
                    filter.setMaxAmount(Double.parseDouble(maxAmountField.getText()));
                }

                loadFilteredTransactions(filter);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(searchButton);

        // Reset Button
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            startDateField.setText("");
            endDateField.setText("");
            minAmountField.setText("");
            maxAmountField.setText("");
            loadTransactions();
        });
        panel.add(resetButton);

        return panel;
    }

    private void loadTransactions() {
        try {
            tableModel.setRowCount(0);
            for (Transaction transaction : transactionService.getAllTransactions()) {
                tableModel.addRow(new Object[]{
                    transaction.getId(),
                    transaction.getType(),
                    String.format("%.2f", transaction.getAmount()),
                    transaction.getDescription(),
                    transaction.getDateTime(),
                    transaction.getCategory().getName()
                });
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFilteredTransactions(TransactionFilter filter) {
        try {
            tableModel.setRowCount(0);
            for (Transaction transaction : transactionService.getTransactionsByFilter(filter)) {
                tableModel.addRow(new Object[]{
                    transaction.getId(),
                    transaction.getType(),
                    String.format("%.2f", transaction.getAmount()),
                    transaction.getDescription(),
                    transaction.getDateTime(),
                    transaction.getCategory().getName()
                });
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error filtering transactions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
