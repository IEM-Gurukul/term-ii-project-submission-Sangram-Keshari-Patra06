package com.expensetracker.ui.panels;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.exceptions.ValidationException;
import com.expensetracker.models.*;
import com.expensetracker.services.CategoryService;
import com.expensetracker.services.TransactionService;
import com.expensetracker.ui.UIUpdateListener;
import com.expensetracker.ui.UIUpdateManager;
import com.expensetracker.utils.DateUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Modern Merged Transaction & History Panel
 * Android-style clean, interactive interface combining transaction input and history viewing
 */
public class MergedTransactionPanel extends JPanel implements UIUpdateListener {
    private TransactionService transactionService;
    private CategoryService categoryService;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private Timer refreshTimer;

    // Professional Monochromatic Color Scheme
    private static final Color PRIMARY_DARK = new Color(44, 62, 80);      // #2C3E50
    private static final Color PRIMARY_LIGHT = new Color(236, 240, 241);  // #ECF0F1
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);    // #3498DB
    private static final Color ACCENT_HOVER = new Color(41, 128, 185);    // #2980B9
    private static final Color INCOME_COLOR = new Color(39, 174, 96);     // #27AE60
    private static final Color EXPENSE_COLOR = new Color(231, 76, 60);    // #E74C3C
    private static final Color CARD_BG = new Color(255, 255, 255);        // #FFFFFF
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);      // #2C3E50
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141); // #7F8C8D
    private static final Color BORDER_COLOR = new Color(189, 195, 199);   // #BDC3C7
    private static final Color BG_COLOR = new Color(236, 240, 241);       // #ECF0F1
    private static final Color INPUT_BG = new Color(250, 250, 250);       // #FAFAFA
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);    // #27AE60

    public MergedTransactionPanel() throws DatabaseException {
        this.transactionService = new TransactionService();
        this.categoryService = new CategoryService();
        setupUI();
        startAutoRefresh();
        UIUpdateManager.getInstance().registerListener(this);
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_COLOR);

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content with split view
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(320);
        splitPane.setDividerSize(8);
        splitPane.setBackground(BG_COLOR);

        // Top: Transaction Form
        JPanel formPanel = createFormPanel();
        splitPane.setTopComponent(formPanel);

        // Bottom: Transaction History
        JPanel historyPanel = createHistoryPanel();
        splitPane.setBottomComponent(historyPanel);

        add(splitPane, BorderLayout.CENTER);

        loadTransactions();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(PRIMARY_DARK);

        JLabel titleLabel = new JLabel("Transactions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Add new transactions and view your transaction history");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(PRIMARY_LIGHT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        panel.add(titlePanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form Title
        JLabel formTitleLabel = new JLabel("Add New Transaction");
        formTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitleLabel.setForeground(PRIMARY_DARK);
        panel.add(formTitleLabel);
        panel.add(Box.createVerticalStrut(15));

        // Type selector
        JComboBox<String> typeComboBox = createStyledComboBox(new String[]{"INCOME", "EXPENSE"});

        // Amount field
        JTextField amountField = createStyledTextField("0.00");

        // Category selector
        JComboBox<Category> categoryComboBox = createStyledComboBox();
        updateCategories(categoryComboBox, "INCOME");

        // First row: Type, Amount, Category (thin design)
        JPanel firstRowContainer = new JPanel();
        firstRowContainer.setLayout(new GridLayout(1, 3, 12, 0));
        firstRowContainer.setBackground(CARD_BG);

        firstRowContainer.add(createLabeledComponent("Type:", typeComboBox));
        firstRowContainer.add(createLabeledComponent("Amount ($):", amountField));
        firstRowContainer.add(createLabeledComponent("Category:", categoryComboBox));

        panel.add(firstRowContainer);
        panel.add(Box.createVerticalStrut(15));

        // Description field (larger space)
        JTextArea descriptionField = createStyledTextArea("Enter detailed description...");

        // Second row: Description (with larger space)
        JPanel secondRowContainer = new JPanel();
        secondRowContainer.setLayout(new BorderLayout());
        secondRowContainer.setBackground(CARD_BG);

        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(PRIMARY_DARK);
        secondRowContainer.add(descLabel, BorderLayout.NORTH);
        secondRowContainer.add(descriptionField, BorderLayout.CENTER);

        panel.add(secondRowContainer);
        panel.add(Box.createVerticalStrut(15));

        // Action buttons container
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(CARD_BG);

        JButton addButton = createStyledButton("✓ Add Transaction", SUCCESS_COLOR);
        JButton clearButton = createStyledButton("⟳ Clear", TEXT_SECONDARY);

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);

        panel.add(buttonPanel);

        // Update category when type changes
        typeComboBox.addActionListener(e -> updateCategories(categoryComboBox, (String) typeComboBox.getSelectedItem()));

        // Add button action
        addButton.addActionListener(e -> {
            try {
                String type = (String) typeComboBox.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                String description = descriptionField.getText().trim();
                Category category = (Category) categoryComboBox.getSelectedItem();

                if (amount <= 0) {
                    showMessage("Amount must be greater than 0", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (description.isEmpty()) {
                    showMessage("Description cannot be empty", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Transaction transaction;
                if ("INCOME".equals(type)) {
                    transaction = new Income(amount, description, LocalDateTime.now(), category, "");
                } else {
                    transaction = new Expense(amount, description, LocalDateTime.now(), category, "");
                }

                transactionService.createTransaction(transaction);
                UIUpdateManager.getInstance().notifyTransactionUpdated();
                showMessage("✓ Transaction added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear fields
                amountField.setText("0.00");
                descriptionField.setText("");
                typeComboBox.setSelectedIndex(0);
                loadTransactions();
            } catch (NumberFormatException ex) {
                showMessage("Invalid amount. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ValidationException | DatabaseException ex) {
                showMessage(ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clear button action
        clearButton.addActionListener(e -> {
            amountField.setText("0.00");
            descriptionField.setText("");
            typeComboBox.setSelectedIndex(0);
        });

        return panel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        // Header
        JLabel historyTitle = new JLabel("Recent Transactions");
        historyTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        historyTitle.setForeground(PRIMARY_DARK);
        panel.add(historyTitle, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Type", "Amount", "Description", "Date", "Category"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(tableModel);
        styleTable(transactionTable);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBackground(BG_COLOR);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(CARD_BG);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void styleTable(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_DARK);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 32));

        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setBackground(CARD_BG);

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : new Color(248, 249, 250));
                    c.setForeground(TEXT_PRIMARY);
                } else {
                    c.setBackground(ACCENT_COLOR);
                    c.setForeground(Color.WHITE);
                }

                // Color code the Type column
                if (column == 1 && !isSelected) {
                    if ("INCOME".equals(value)) {
                        c.setForeground(INCOME_COLOR);
                    } else if ("EXPENSE".equals(value)) {
                        c.setForeground(EXPENSE_COLOR);
                    }
                }

                return c;
            }
        });
    }

    private JPanel createLabeledComponent(String label, Component component) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelComp.setForeground(TEXT_SECONDARY);

        panel.add(labelComp);
        panel.add(Box.createVerticalStrut(4));
        panel.add(component);

        return panel;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBackground(INPUT_BG);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        // Placeholder effect
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(TEXT_SECONDARY);
                    field.setText(placeholder);
                }
            }
        });

        return field;
    }

    private JTextArea createStyledTextArea(String placeholder) {
        JTextArea area = new JTextArea(4, 30);
        area.setText(placeholder);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        area.setBackground(INPUT_BG);
        area.setForeground(TEXT_SECONDARY);
        area.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new Insets(8, 8, 8, 8));

        // Placeholder effect
        area.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (area.getText().equals(placeholder)) {
                    area.setText("");
                    area.setForeground(TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (area.getText().isEmpty()) {
                    area.setForeground(TEXT_SECONDARY);
                    area.setText(placeholder);
                }
            }
        });

        return area;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboBox.setBackground(INPUT_BG);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        comboBox.setFocusable(false);
        return comboBox;
    }

    private JComboBox<Category> createStyledComboBox() {
        JComboBox<Category> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboBox.setBackground(INPUT_BG);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        comboBox.setFocusable(false);
        return comboBox;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        button.setBackground(backgroundColor);
        button.setForeground(backgroundColor.equals(SUCCESS_COLOR) ? Color.WHITE : Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.equals(SUCCESS_COLOR) ? new Color(33, 150, 83) : new Color(108, 117, 125));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private void updateCategories(JComboBox<Category> comboBox, String type) {
        comboBox.removeAllItems();
        try {
            for (Category category : categoryService.getCategoriesByType(type)) {
                comboBox.addItem(category);
            }
        } catch (DatabaseException e) {
            showMessage("Error loading categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTransactions() {
        try {
            tableModel.setRowCount(0);
            List<Transaction> transactions = transactionService.getAllTransactions();
            // Show most recent first
            for (int i = transactions.size() - 1; i >= 0; i--) {
                Transaction t = transactions.get(i);
                tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getType(),
                    String.format("$%.2f", t.getAmount()),
                    t.getDescription(),
                    t.getDateTime(),
                    t.getCategory().getName()
                });
            }
        } catch (DatabaseException e) {
            showMessage("Error loading transactions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer("TransactionRefreshTimer", true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(MergedTransactionPanel.this::loadTransactions);
            }
        }, 5000, 5000);
    }

    @Override
    public void onTransactionUpdated() {
        loadTransactions();
    }

    @Override
    public void onCategoryUpdated() {
        loadTransactions();
    }

    @Override
    public void onRefreshRequired() {
        loadTransactions();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
        UIUpdateManager.getInstance().unregisterListener(this);
    }
}
