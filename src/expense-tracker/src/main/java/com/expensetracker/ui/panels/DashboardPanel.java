package com.expensetracker.ui.panels;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.services.DashboardService;
import com.expensetracker.utils.CurrencyUtils;
import javax.swing.*;
import java.awt.*;

/**
 * Dashboard panel showing real-time balance and statistics
 */
public class DashboardPanel extends JPanel {
    private DashboardService dashboardService;
    private JLabel balanceLabel;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel transactionCountLabel;

    public DashboardPanel() throws DatabaseException {
        this.dashboardService = new DashboardService();
        setupUI();
        updateDashboard();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Financial Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Statistics Panel
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.CENTER);

        // Refresh Button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> updateDashboard());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 20, 20));

        balanceLabel = createStatCard("Total Balance", "$0.00");
        incomeLabel = createStatCard("Total Income", "$0.00");
        expenseLabel = createStatCard("Total Expense", "$0.00");
        transactionCountLabel = createStatCard("Transactions", "0");

        panel.add(balanceLabel.getParent());
        panel.add(incomeLabel.getParent());
        panel.add(expenseLabel.getParent());
        panel.add(transactionCountLabel.getParent());

        return panel;
    }

    private JLabel createStatCard(String title, String value) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        cardPanel.setBackground(new Color(240, 240, 240));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(Box.createVerticalGlue());
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(valueLabel);
        cardPanel.add(Box.createVerticalGlue());

        valueLabel.setName(title);
        return valueLabel;
    }

    public void updateDashboard() {
        try {
            double balance = dashboardService.getTotalBalance();
            double income = dashboardService.getTotalIncome();
            double expense = dashboardService.getTotalExpense();
            int count = dashboardService.getTransactionCount();

            balanceLabel.setText(CurrencyUtils.formatCurrencyWithDollar(balance));
            incomeLabel.setText(CurrencyUtils.formatCurrencyWithDollar(income));
            expenseLabel.setText(CurrencyUtils.formatCurrencyWithDollar(expense));
            transactionCountLabel.setText(String.valueOf(count));
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error updating dashboard: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
