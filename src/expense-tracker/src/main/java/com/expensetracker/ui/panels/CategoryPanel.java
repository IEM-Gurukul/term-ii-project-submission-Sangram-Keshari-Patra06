package com.expensetracker.ui.panels;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.models.Category;
import com.expensetracker.services.CategoryService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel for managing categories
 */
public class CategoryPanel extends JPanel {
    private CategoryService categoryService;
    private JTable categoryTable;
    private DefaultTableModel tableModel;

    public CategoryPanel() throws DatabaseException {
        this.categoryService = new CategoryService();
        setupUI();
        loadCategories();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Manage Categories");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.WEST);

        // Table
        String[] columnNames = {"ID", "Name", "Type", "Custom"};
        tableModel = new DefaultTableModel(columnNames, 0);
        categoryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Add Category"));

        // Category Name
        panel.add(new JLabel("Category Name:"));
        JTextField nameField = new JTextField(15);
        panel.add(nameField);

        // Type
        panel.add(new JLabel("Type:"));
        String[] types = {"INCOME", "EXPENSE"};
        JComboBox<String> typeComboBox = new JComboBox<>(types);
        panel.add(typeComboBox);

        // Add Button
        JButton addButton = new JButton("Add Category");
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String type = (String) typeComboBox.getSelectedItem();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Category name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Category category = new Category(name, type, true);
                categoryService.createCategory(category);
                nameField.setText("");
                loadCategories();
                JOptionPane.showMessageDialog(this, "Category added successfully!");
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(this, "Error adding category: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(addButton);

        return panel;
    }

    private void loadCategories() {
        try {
            tableModel.setRowCount(0);
            for (Category category : categoryService.getAllCategories()) {
                tableModel.addRow(new Object[]{
                    category.getId(),
                    category.getName(),
                    category.getType(),
                    category.isCustom() ? "Yes" : "No"
                });
            }
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
