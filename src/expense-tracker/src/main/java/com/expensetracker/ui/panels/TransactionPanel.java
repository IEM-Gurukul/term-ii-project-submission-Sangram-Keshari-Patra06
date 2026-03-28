package com.expensetracker.ui.panels;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.exceptions.ValidationException;
import com.expensetracker.models.*;
import com.expensetracker.services.CategoryService;
import com.expensetracker.services.TransactionService;
import com.expensetracker.ui.UIUpdateManager;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

/**
 * Panel for creating and managing transactions
 */
public class TransactionPanel extends JPanel {
    private TransactionService transactionService;
    private CategoryService categoryService;

    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);

    public TransactionPanel() throws DatabaseException {
        this.transactionService = new TransactionService();
        this.categoryService = new CategoryService();
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 240, 245));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Add New Transaction");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Record your income or expense");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(236, 240, 241));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(PRIMARY_COLOR);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);

        headerPanel.add(textPanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(getBackground());

        // Transaction Type
        JPanel typePanel = createFieldPanel("Type:", Color.WHITE);
        String[] types = {"INCOME", "EXPENSE"};
        JComboBox<String> typeComboBox = new JComboBox<>(types);
        styleComboBox(typeComboBox);
        typePanel.add(typeComboBox, BorderLayout.CENTER);
        panel.add(typePanel);

        // Amount
        JPanel amountPanel = createFieldPanel("Amount ($):", Color.WHITE);
        JTextField amountField = new JTextField();
        styleTextField(amountField);
        amountPanel.add(amountField, BorderLayout.CENTER);
        panel.add(amountPanel);

        // Description
        JPanel descriptionPanel = createFieldPanel("Description:", Color.WHITE);
        JTextField descriptionField = new JTextField();
        styleTextField(descriptionField);
        descriptionPanel.add(descriptionField, BorderLayout.CENTER);
        panel.add(descriptionPanel);

        // Category
        JPanel categoryPanel = createFieldPanel("Category:", Color.WHITE);
        JComboBox<Category> categoryComboBox = new JComboBox<>();
        styleComboBox(categoryComboBox);
        updateCategories(categoryComboBox, "INCOME");
        typeComboBox.addActionListener(e -> updateCategories(categoryComboBox, (String) typeComboBox.getSelectedItem()));
        categoryPanel.add(categoryComboBox, BorderLayout.CENTER);
        panel.add(categoryPanel);

        // Additional field based on type
        JPanel additionalPanel = createFieldPanel("Source:", Color.WHITE);
        JTextField additionalField = new JTextField();
        styleTextField(additionalField);
        additionalPanel.add(additionalField, BorderLayout.CENTER);
        panel.add(additionalPanel);

        typeComboBox.addActionListener(e -> {
            JLabel label = (JLabel) additionalPanel.getComponent(0);
            if ("INCOME".equals(typeComboBox.getSelectedItem())) {
                label.setText("Source:");
            } else {
                label.setText("Payment Method:");
            }
        });

        panel.add(Box.createVerticalStrut(20));

        // Submit button
        JButton submitButton = new JButton("✓ Add Transaction");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setBackground(SUCCESS_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        submitButton.setMaximumSize(new Dimension(200, 45));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(SUCCESS_COLOR);
            }
        });

        submitButton.addActionListener(e -> {
            try {
                String type = (String) typeComboBox.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText();
                Category category = (Category) categoryComboBox.getSelectedItem();
                String additional = additionalField.getText();

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than 0", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (description.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Description cannot be empty", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Transaction transaction;
                if ("INCOME".equals(type)) {
                    transaction = new Income(amount, description, LocalDateTime.now(), category, additional);
                } else {
                    transaction = new Expense(amount, description, LocalDateTime.now(), category, additional);
                }

                transactionService.createTransaction(transaction);

                // Notify other panels about the update
                UIUpdateManager.getInstance().notifyTransactionUpdated();

                JOptionPane.showMessageDialog(this, "✓ Transaction added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields(amountField, descriptionField, additionalField);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ValidationException | DatabaseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(getBackground());
        buttonPanel.add(submitButton);
        panel.add(buttonPanel);
        panel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(getBackground());
        scrollPane.getViewport().setBackground(getBackground());
        scrollPane.setBorder(null);

        // Create outer panel to hold scrollpane
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(getBackground());
        outerPanel.add(scrollPane, BorderLayout.CENTER);

        return outerPanel;
    }

    private JPanel createFieldPanel(String labelText, Color backgroundColor) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        panel.setBackground(backgroundColor);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(44, 62, 80));
        label.setPreferredSize(new Dimension(120, 30));

        panel.add(label, BorderLayout.WEST);
        return panel;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        field.setPreferredSize(new Dimension(150, 30));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        comboBox.setPreferredSize(new Dimension(150, 30));
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
