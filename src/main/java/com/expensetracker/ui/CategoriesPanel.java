package com.expensetracker.ui;

import com.expensetracker.*;
import com.expensetracker.models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CategoriesPanel extends JPanel {
    private AppService service;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JTextField categoryNameField;
    private JComboBox<String> typeCombo;

    public CategoriesPanel() throws AppException {
        service = new AppService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        formPanel.add(new JLabel("Name:"));
        
        categoryNameField = new JTextField(15);
        formPanel.add(categoryNameField);
        
        formPanel.add(new JLabel("Type:"));
        typeCombo = new JComboBox<>(new String[]{"INCOME", "EXPENSE"});
        formPanel.add(typeCombo);
        
        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> addCategory());
        formPanel.add(addBtn);
        
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteCategory());
        formPanel.add(deleteBtn);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadCategories());
        formPanel.add(refreshBtn);
        
        add(formPanel, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Name", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        add(scrollPane, BorderLayout.CENTER);
        
        loadCategories();
    }

    private void addCategory() {
        try {
            String name = categoryNameField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name required");
                return;
            }

            Category category = new Category(name, type, true);
            service.createCategory(category);
            JOptionPane.showMessageDialog(this, "Category added");
            categoryNameField.setText("");
            loadCategories();
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteCategory() {
        int row = categoryTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a category");
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(row, 0);
            service.deleteCategory(id);
            JOptionPane.showMessageDialog(this, "Category deleted");
            loadCategories();
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadCategories() {
        try {
            tableModel.setRowCount(0);
            for (Category cat : service.getAllCategories()) {
                tableModel.addRow(new Object[]{
                    cat.getId(),
                    cat.getName(),
                    cat.getType()
                });
            }
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
        }
    }
}
