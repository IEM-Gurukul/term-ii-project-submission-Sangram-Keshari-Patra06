package com.expensetracker.ui;

import com.expensetracker.*;
import com.expensetracker.models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;

public class TransactionsPanel extends JPanel {
    private AppService service;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeCombo;
    private JTextField amountField, descField;
    private JComboBox<Category> categoryCombo;

    public TransactionsPanel() throws AppException {
        service = new AppService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        formPanel.add(new JLabel("Type:"));
        
        typeCombo = new JComboBox<>(new String[]{"INCOME", "EXPENSE"});
        formPanel.add(typeCombo);
        
        formPanel.add(new JLabel("Amount:"));
        amountField = new JTextField(8);
        formPanel.add(amountField);
        
        formPanel.add(new JLabel("Category:"));
        categoryCombo = new JComboBox<>();
        formPanel.add(categoryCombo);
        
        formPanel.add(new JLabel("Description:"));
        descField = new JTextField(15);
        formPanel.add(descField);
        
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> addTransaction());
        formPanel.add(addBtn);
        
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteTransaction());
        formPanel.add(deleteBtn);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadTransactions());
        formPanel.add(refreshBtn);
        
        add(formPanel, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Type", "Amount", "Category", "Description", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);
        
        typeCombo.addActionListener(e -> updateCategories());
        updateCategories();
        loadTransactions();
    }

    private void updateCategories() {
        try {
            categoryCombo.removeAllItems();
            String type = (String) typeCombo.getSelectedItem();
            for (Category cat : service.getCategoriesByType(type)) {
                categoryCombo.addItem(cat);
            }
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addTransaction() {
        try {
            String type = (String) typeCombo.getSelectedItem();
            double amount = Double.parseDouble(amountField.getText());
            String desc = descField.getText().trim();
            Category cat = (Category) categoryCombo.getSelectedItem();

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive");
                return;
            }
            if (desc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Description required");
                return;
            }

            Transaction trans;
            if ("INCOME".equals(type)) {
                trans = new Income(amount, desc, LocalDateTime.now(), cat, "");
            } else {
                trans = new Expense(amount, desc, LocalDateTime.now(), cat, "");
            }

            service.createTransaction(trans);
            JOptionPane.showMessageDialog(this, "Transaction added");
            amountField.setText("");
            descField.setText("");
            loadTransactions();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount");
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteTransaction() {
        int row = transactionTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a transaction");
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(row, 0);
            service.deleteTransaction(id);
            JOptionPane.showMessageDialog(this, "Transaction deleted");
            loadTransactions();
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadTransactions() {
        try {
            tableModel.setRowCount(0);
            for (Transaction t : service.getAllTransactions()) {
                tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getType(),
                    String.format("%.2f", t.getAmount()),
                    t.getCategory().getName(),
                    t.getDescription(),
                    AppUtils.formatDateTime(t.getDateTime())
                });
            }
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage());
        }
    }
}
