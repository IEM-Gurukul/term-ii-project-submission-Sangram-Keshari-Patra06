package com.expensetracker.ui.panels;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.exceptions.ValidationException;
import com.expensetracker.models.*;
import com.expensetracker.services.CategoryService;
import com.expensetracker.services.TransactionService;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

/**
 * Panel for creating and managing transactions
 */
public class TransactionPanel extends JPanel {
    private TransactionService transactionService;
    private CategoryService categoryService;

    public TransactionPanel() throws DatabaseException {
        this.transactionService = new TransactionService();
        this.categoryService = new CategoryService();
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Add Transaction");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 10, 10));

        // Transaction Type
        JLabel typeLabel = new JLabel("Type:");
        String[] types = {"INCOME", "EXPENSE"};
        JComboBox<String> typeComboBox = new JComboBox<>(types);
        panel.add(typeLabel);
        panel.add(typeComboBox);

        // Amount
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();
        panel.add(amountLabel);
        panel.add(amountField);

        // Description
        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();
        panel.add(descriptionLabel);
        panel.add(descriptionField);

        // Category
        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<Category> categoryComboBox = new JComboBox<>();
        updateCategories(categoryComboBox, "INCOME");
        typeComboBox.addActionListener(e -> updateCategories(categoryComboBox, (String) typeComboBox.getSelectedItem()));
        panel.add(categoryLabel);
        panel.add(categoryComboBox);

        // Additional field based on type
        JLabel additionalLabel = new JLabel("Source:");
        JTextField additionalField = new JTextField();
        panel.add(additionalLabel);
        panel.add(additionalField);

        typeComboBox.addActionListener(e -> {
            if ("INCOME".equals(typeComboBox.getSelectedItem())) {
                additionalLabel.setText("Source:");
            } else {
                additionalLabel.setText("Payment Method:");
            }
        });

        // Submit button
        JButton submitButton = new JButton("Add Transaction");
        submitButton.addActionListener(e -> {
            try {
                String type = (String) typeComboBox.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText();
                Category category = (Category) categoryComboBox.getSelectedItem();
                String additional = additionalField.getText();

                Transaction transaction;
                if ("INCOME".equals(type)) {
                    transaction = new Income(amount, description, LocalDateTime.now(), category, additional);
                } else {
                    transaction = new Expense(amount, description, LocalDateTime.now(), category, additional);
                }

                transactionService.createTransaction(transaction);
                JOptionPane.showMessageDialog(this, "Transaction added successfully!");
                clearFields(amountField, descriptionField, additionalField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ValidationException | DatabaseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        panel.add(buttonPanel);

        return panel;
    }

    private void updateCategories(JComboBox<Category> comboBox, String type) {
        comboBox.removeAllItems();
        try {
            for (Category category : categoryService.getCategoriesByType(type)) {
                comboBox.addItem(category);
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }
}
