package com.expensetracker.ui.panels;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.models.Category;
import com.expensetracker.services.CategoryService;
import com.expensetracker.ui.UIUpdateManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Panel for managing categories with professional monochromatic design
 */
public class CategoryPanel extends JPanel {
    private CategoryService categoryService;
    private JTable categoryTable;
    private DefaultTableModel tableModel;

    // Professional Monochromatic Color Scheme
    private static final Color PRIMARY_DARK = new Color(44, 62, 80);      // #2C3E50
    private static final Color PRIMARY_LIGHT = new Color(236, 240, 241);  // #ECF0F1
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);    // #3498DB
    private static final Color ACCENT_HOVER = new Color(41, 128, 185);    // #2980B9
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);    // #27AE60
    private static final Color CARD_BG = new Color(255, 255, 255);        // #FFFFFF
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141); // #7F8C8D
    private static final Color BORDER_COLOR = new Color(189, 195, 199);   // #BDC3C7
    private static final Color BG_COLOR = new Color(236, 240, 241);       // #ECF0F1
    private static final Color INPUT_BG = new Color(250, 250, 250);       // #FAFAFA

    public CategoryPanel() throws DatabaseException {
        this.categoryService = new CategoryService();
        setupUI();
        loadCategories();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(BG_COLOR);

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.WEST);

        // Table
        String[] columnNames = {"ID", "Name", "Type", "Custom"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = new JTable(tableModel);
        styleTable(categoryTable);
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Manage Categories");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Create and manage expense/income categories");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(PRIMARY_LIGHT);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(PRIMARY_DARK);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    private void styleTable(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_DARK);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 30));

        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(Color.WHITE);

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                return c;
            }
        });
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER_COLOR), "Add New Category", 0, 0, new Font("Segoe UI", Font.BOLD, 12))
        ));
        panel.setBackground(CARD_BG);
        panel.setPreferredSize(new Dimension(180, 300));

        // Category Name
        panel.add(createLabel("Category Name:"));
        JTextField nameField = new JTextField(15);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        nameField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        nameField.setMaximumSize(new Dimension(160, 30));
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));

        // Type
        panel.add(createLabel("Type:"));
        String[] types = {"INCOME", "EXPENSE"};
        JComboBox<String> typeComboBox = new JComboBox<>(types);
        typeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        typeComboBox.setMaximumSize(new Dimension(160, 30));
        panel.add(typeComboBox);
        panel.add(Box.createVerticalStrut(15));

        // Add Button
        JButton addButton = new JButton("✓ Add Category");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setBackground(SUCCESS_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        addButton.setMaximumSize(new Dimension(160, 35));
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        addButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(33, 150, 83));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addButton.setBackground(SUCCESS_COLOR);
            }
        });

        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String type = (String) typeComboBox.getSelectedItem();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Category name cannot be empty", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Category category = new Category(name, type, true);
                categoryService.createCategory(category);
                nameField.setText("");
                loadCategories();
                
                // Notify other panels about the update
                UIUpdateManager.getInstance().notifyCategoryUpdated();
                
                JOptionPane.showMessageDialog(this, "✓ Category added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (DatabaseException ex) {
                JOptionPane.showMessageDialog(this, "Error adding category: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(addButton);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        label.setForeground(new Color(44, 62, 80));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
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
