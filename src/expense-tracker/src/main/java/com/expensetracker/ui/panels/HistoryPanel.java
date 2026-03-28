package com.expensetracker.ui.panels;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.models.*;
import com.expensetracker.services.TransactionService;
import com.expensetracker.ui.UIUpdateListener;
import com.expensetracker.ui.UIUpdateManager;
import com.expensetracker.utils.DateUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Panel for viewing and filtering transaction history with auto-refresh
 */
public class HistoryPanel extends JPanel implements UIUpdateListener {
    private TransactionService transactionService;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private Timer refreshTimer;

    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color HEADER_COLOR = new Color(236, 240, 241);

    public HistoryPanel() throws DatabaseException {
        this.transactionService = new TransactionService();
        setupUI();
        loadTransactions();
        startAutoRefresh();
        UIUpdateManager.getInstance().registerListener(this);
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 240, 245));

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Filter Panel
        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.WEST);

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
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("View and filter all your transactions");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(236, 240, 241));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(PRIMARY_COLOR);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.WEST);
        return panel;
    }

    private void styleTable(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 30));

        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(new Color(189, 195, 199));
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : HEADER_COLOR);
                }
                return c;
            }
        });
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)), "Filters", 0, 0, new Font("Segoe UI", Font.BOLD, 12))
        ));
        panel.setBackground(new Color(250, 250, 250));
        panel.setPreferredSize(new Dimension(180, 400));

        // Start Date
        panel.add(createLabel("Start Date:"));
        JTextField startDateField = new JTextField(10);
        panel.add(startDateField);
        panel.add(Box.createVerticalStrut(8));

        // End Date
        panel.add(createLabel("End Date:"));
        JTextField endDateField = new JTextField(10);
        panel.add(endDateField);
        panel.add(Box.createVerticalStrut(8));

        // Min Amount
        panel.add(createLabel("Min Amount:"));
        JTextField minAmountField = new JTextField(10);
        panel.add(minAmountField);
        panel.add(Box.createVerticalStrut(8));

        // Max Amount
        panel.add(createLabel("Max Amount:"));
        JTextField maxAmountField = new JTextField(10);
        panel.add(maxAmountField);
        panel.add(Box.createVerticalStrut(15));

        // Search Button
        JButton searchButton = new JButton("🔍 Search");
        styleButton(searchButton);
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
        panel.add(Box.createVerticalStrut(8));

        // Reset Button
        JButton resetButton = new JButton("↻ Reset");
        styleButton(resetButton);
        resetButton.addActionListener(e -> {
            startDateField.setText("");
            endDateField.setText("");
            minAmountField.setText("");
            maxAmountField.setText("");
            loadTransactions();
        });
        panel.add(resetButton);

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

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(160, 35));
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

    private void startAutoRefresh() {
        refreshTimer = new Timer("HistoryRefreshTimer", true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(HistoryPanel.this::loadTransactions);
            }
        }, 15000, 15000); // Refresh every 15 seconds
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
