package com.expensetracker.ui;

import com.expensetracker.*;
import com.expensetracker.models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class AppPanel extends JPanel {
    private AppService service;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeCombo;
    private JTextField amountField, descField;
    private JComboBox<Category> categoryCombo;

    public AppPanel() throws AppException {
        service = new AppService();
        setupUI();
        loadTransactions();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 240, 240));

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Add Transaction"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Type:"), gbc);
        typeCombo = new JComboBox<>(new String[]{"INCOME", "EXPENSE"});
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Amount:"), gbc);
        amountField = new JTextField("0.00", 10);
        gbc.gridx = 3;
        panel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Category:"), gbc);
        categoryCombo = new JComboBox<>();
        gbc.gridx = 1;
        panel.add(categoryCombo, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Description:"), gbc);
        descField = new JTextField(15);
        gbc.gridx = 3;
        panel.add(descField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> addTransaction());
        panel.add(addBtn, gbc);

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> clearFields());
        gbc.gridx = 1;
        panel.add(clearBtn, gbc);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteTransaction());
        gbc.gridx = 2;
        panel.add(deleteBtn, gbc);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadTransactions());
        gbc.gridx = 3;
        panel.add(refreshBtn, gbc);

        typeCombo.addActionListener(e -> updateCategories());
        updateCategories();

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Transactions"));

        String[] columns = {"ID", "Type", "Amount", "Category", "Description", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
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
                JOptionPane.showMessageDialog(this, "Amount must be > 0", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (desc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Description required", "Error", JOptionPane.WARNING_MESSAGE);
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
            clearFields();
            loadTransactions();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTransaction() {
        int row = transactionTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a transaction", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(row, 0);
            service.deleteTransaction(id);
            JOptionPane.showMessageDialog(this, "Transaction deleted");
            loadTransactions();
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        amountField.setText("0.00");
        descField.setText("");
        typeCombo.setSelectedIndex(0);
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
