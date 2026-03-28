package com.expensetracker.ui.panels;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.services.DashboardService;
import com.expensetracker.services.ReportService;
import com.expensetracker.ui.UIUpdateListener;
import com.expensetracker.ui.UIUpdateManager;
import com.expensetracker.utils.CurrencyUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Integrated Dashboard Panel combining statistics and charts
 * Features auto-refresh and modern UI design
 */
public class IntegratedDashboardPanel extends JPanel implements UIUpdateListener {
    private DashboardService dashboardService;
    private ReportService reportService;
    private JLabel balanceLabel;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel transactionCountLabel;
    private ChartPanel chartPanel;
    private Timer refreshTimer;

    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color INCOME_COLOR = new Color(46, 204, 113);
    private static final Color EXPENSE_COLOR = new Color(231, 76, 60);
    private static final Color BALANCE_COLOR = new Color(155, 89, 182);
    private static final Color CARD_BACKGROUND = new Color(250, 250, 250);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    public IntegratedDashboardPanel() throws DatabaseException {
        this.dashboardService = new DashboardService();
        this.reportService = new ReportService();
        setupUI();
        startAutoRefresh();
        UIUpdateManager.getInstance().registerListener(this);
    }

    private void setupUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 240, 245));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(1, 2, 20, 20));
        contentPanel.setBackground(getBackground());

        // Statistics Panel (Left)
        JPanel statsPanel = createStatsPanel();
        contentPanel.add(statsPanel);

        // Charts Panel (Right)
        JPanel chartsPanel = new JPanel();
        chartsPanel.setLayout(new BorderLayout());
        chartsPanel.setBackground(getBackground());
        try {
            updateChart();
            chartsPanel.add(chartPanel, BorderLayout.CENTER);
        } catch (DatabaseException e) {
            JLabel errorLabel = new JLabel("Error loading chart: " + e.getMessage());
            errorLabel.setForeground(EXPENSE_COLOR);
            chartsPanel.add(errorLabel, BorderLayout.CENTER);
        }

        contentPanel.add(chartsPanel);
        add(contentPanel, BorderLayout.CENTER);

        // Footer Panel with Refresh Button
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);

        updateDashboard();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Financial Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Real-time expense tracking and analysis");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 15, 15));
        panel.setBackground(getBackground());

        balanceLabel = createStatCard("Total Balance", "$0.00", BALANCE_COLOR);
        incomeLabel = createStatCard("Total Income", "$0.00", INCOME_COLOR);
        expenseLabel = createStatCard("Total Expense", "$0.00", EXPENSE_COLOR);
        transactionCountLabel = createStatCard("Transactions", "0", PRIMARY_COLOR);

        panel.add(balanceLabel.getParent());
        panel.add(incomeLabel.getParent());
        panel.add(expenseLabel.getParent());
        panel.add(transactionCountLabel.getParent());

        return panel;
    }

    private JLabel createStatCard(String title, String value, Color color) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardPanel.setBackground(CARD_BACKGROUND);
        cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Add shadow effect
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createRaisedBevelBorder()
        ));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(127, 140, 141));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Top colored bar
        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 4));
        colorBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardPanel.add(colorBar);
        cardPanel.add(Box.createVerticalStrut(10));
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(valueLabel);

        valueLabel.setName(title);
        return valueLabel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(getBackground());

        JButton refreshButton = new JButton("🔄 Refresh Now");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        refreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(PRIMARY_COLOR);
            }
        });
        refreshButton.addActionListener(e -> updateDashboard());

        JLabel autoRefreshLabel = new JLabel("(Auto-refreshes every 10 seconds)");
        autoRefreshLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        autoRefreshLabel.setForeground(new Color(127, 140, 141));

        panel.add(autoRefreshLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(refreshButton);

        return panel;
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer("DashboardRefreshTimer", true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(IntegratedDashboardPanel.this::updateDashboard);
            }
        }, 10000, 10000); // Refresh every 10 seconds
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

            updateChart();
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error updating dashboard: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateChart() throws DatabaseException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Double> expensesByCategory = reportService.getExpensesByCategory();

        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
            "Expenses by Category",
            dataset,
            true,
            true,
            false
        );

        // Customize chart appearance
        pieChart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 16));
        pieChart.setBackgroundPaint(CARD_BACKGROUND);

        if (chartPanel != null) {
            chartPanel.setChart(pieChart);
        } else {
            chartPanel = new ChartPanel(pieChart);
            chartPanel.setBackground(CARD_BACKGROUND);
        }
    }

    @Override
    public void onTransactionUpdated() {
        updateDashboard();
    }

    @Override
    public void onCategoryUpdated() {
        updateDashboard();
    }

    @Override
    public void onRefreshRequired() {
        updateDashboard();
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
