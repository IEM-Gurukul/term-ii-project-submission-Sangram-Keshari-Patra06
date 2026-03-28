package com.expensetracker.ui.panels;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.models.Transaction;
import com.expensetracker.services.DashboardService;
import com.expensetracker.services.ReportService;
import com.expensetracker.services.TransactionService;
import com.expensetracker.ui.UIUpdateListener;
import com.expensetracker.ui.UIUpdateManager;
import com.expensetracker.utils.CurrencyUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Timer;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * Modern Professional Dashboard Panel with Time-Based Charts
 * Features monochromatic design with accent colors and interactive graphs
 */
public class ModernDashboardPanel extends JPanel implements UIUpdateListener {
    private DashboardService dashboardService;
    private ReportService reportService;
    private TransactionService transactionService;
    private JLabel balanceLabel;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel transactionCountLabel;
    private ChartPanel chartPanel;
    private ChartPanel barChartPanel;
    private JComboBox<String> timePeriodCombo;
    private JComboBox<String> pieTypeCombo;
    private java.util.Timer refreshTimer;

    // Professional Monochromatic Color Scheme
    private static final Color PRIMARY_DARK = new Color(44, 62, 80);      // #2C3E50
    private static final Color PRIMARY_LIGHT = new Color(236, 240, 241);  // #ECF0F1
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);    // #3498DB
    private static final Color ACCENT_HOVER = new Color(41, 128, 185);    // #2980B9
    private static final Color INCOME_COLOR = new Color(39, 174, 96);     // #27AE60
    private static final Color EXPENSE_COLOR = new Color(231, 76, 60);    // #E74C3C
    private static final Color CARD_BG = new Color(255, 255, 255);        // #FFFFFF
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141); // #7F8C8D
    private static final Color BORDER_COLOR = new Color(189, 195, 199);   // #BDC3C7
    private static final Color BG_COLOR = new Color(236, 240, 241);       // #ECF0F1

    public ModernDashboardPanel() throws DatabaseException {
        this.dashboardService = new DashboardService();
        this.reportService = new ReportService();
        this.transactionService = new TransactionService();
        setupUI();
        startAutoRefresh();
        UIUpdateManager.getInstance().registerListener(this);
    }

    private void setupUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(BG_COLOR);

        // Header with Title Only
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(15, 15));
        contentPanel.setBackground(BG_COLOR);

        // Top Stats Panel
        JPanel statsPanel = createStatsPanel();
        contentPanel.add(statsPanel, BorderLayout.NORTH);

        // Period Selector Panel (below stats)
        JPanel periodPanel = createPeriodPanel();
        contentPanel.add(periodPanel, BorderLayout.SOUTH);

        // Charts Panel (center)
        JPanel chartsPanel = createChartsPanel();
        contentPanel.add(chartsPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        updateDashboard();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Left: Title and Subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(PRIMARY_DARK);

        JLabel titleLabel = new JLabel("Financial Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Track your income and expenses with advanced analytics");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(PRIMARY_LIGHT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        panel.add(titlePanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createPeriodPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(BG_COLOR);

        JLabel filterLabel = new JLabel("Time Period: ");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterLabel.setForeground(PRIMARY_DARK);

        String[] timePeriods = {"Today", "Week", "Month", "All Time"};
        timePeriodCombo = new JComboBox<>(timePeriods);
        timePeriodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timePeriodCombo.setBackground(CARD_BG);
        timePeriodCombo.setForeground(PRIMARY_DARK);
        timePeriodCombo.setFocusable(false);
        timePeriodCombo.addActionListener(e -> updateDashboard());

        panel.add(filterLabel);
        panel.add(timePeriodCombo);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 4, 15, 15));
        panel.setBackground(BG_COLOR);

        balanceLabel = createStatCard("Total Balance", "$0.00", PRIMARY_DARK, "●");
        incomeLabel = createStatCard("Total Income", "$0.00", INCOME_COLOR, "↑");
        expenseLabel = createStatCard("Total Expense", "$0.00", EXPENSE_COLOR, "↓");
        transactionCountLabel = createStatCard("Transactions", "0", ACCENT_COLOR, "█");

        panel.add(balanceLabel.getParent());
        panel.add(incomeLabel.getParent());
        panel.add(expenseLabel.getParent());
        panel.add(transactionCountLabel.getParent());

        return panel;
    }

    private JLabel createStatCard(String title, String value, Color accentColor, String symbol) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_BG);

        // Shadow effect
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(3, 3, 5, 5),
            BorderFactory.createLineBorder(BORDER_COLOR, 1)
        ));

        // Set padding inside card
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(3, 3, 5, 5),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            )
        ));

        // Icon and title row
        JPanel titleRow = new JPanel();
        titleRow.setLayout(new BoxLayout(titleRow, BoxLayout.X_AXIS));
        titleRow.setBackground(CARD_BG);

        JLabel iconLabel = new JLabel(symbol);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        iconLabel.setForeground(accentColor);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT_SECONDARY);

        titleRow.add(iconLabel);
        titleRow.add(Box.createHorizontalStrut(8));
        titleRow.add(titleLabel);
        titleRow.add(Box.createHorizontalGlue());

        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(accentColor);

        // Accent bar
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 3));

        cardPanel.add(accentBar);
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(titleRow);
        cardPanel.add(Box.createVerticalStrut(8));
        cardPanel.add(valueLabel);

        valueLabel.setName(title);
        return valueLabel;
    }

    private JPanel createChartsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 15, 15));
        panel.setBackground(BG_COLOR);

        try {
            // Pie Chart Panel with Dropdown
            JPanel pieWrapperPanel = new JPanel();
            pieWrapperPanel.setLayout(new BorderLayout(0, 10));
            pieWrapperPanel.setBackground(BG_COLOR);

            // Dropdown for pie chart type
            JPanel pieControlPanel = new JPanel();
            pieControlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
            pieControlPanel.setBackground(BG_COLOR);

            JLabel pieTypeLabel = new JLabel("View: ");
            pieTypeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
            pieTypeLabel.setForeground(PRIMARY_DARK);

            String[] pieTypes = {"Expenses", "Income"};
            pieTypeCombo = new JComboBox<>(pieTypes);
            pieTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            pieTypeCombo.setBackground(CARD_BG);
            pieTypeCombo.setForeground(PRIMARY_DARK);
            pieTypeCombo.setFocusable(false);
            pieTypeCombo.addActionListener(e -> updateDashboard());

            pieControlPanel.add(pieTypeLabel);
            pieControlPanel.add(pieTypeCombo);

            pieWrapperPanel.add(pieControlPanel, BorderLayout.NORTH);

            // Pie Chart
            JPanel piePanel = createChartCardPanel("Category Distribution", createExpenseChart());
            pieWrapperPanel.add(piePanel, BorderLayout.CENTER);

            panel.add(pieWrapperPanel);

            // Bar Chart - Income vs Expense by Time Period
            JPanel barPanel = createChartCardPanel("Income vs Expense Trend", createBarChart());
            panel.add(barPanel);
        } catch (DatabaseException e) {
            JLabel errorLabel = new JLabel("Error loading charts: " + e.getMessage());
            errorLabel.setForeground(EXPENSE_COLOR);
            panel.add(errorLabel);
        }

        return panel;
    }

    private JPanel createChartCardPanel(String title, ChartPanel chartPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(3, 3, 5, 5),
            BorderFactory.createLineBorder(BORDER_COLOR, 1)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_DARK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private ChartPanel createExpenseChart() throws DatabaseException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        String chartType = (String) pieTypeCombo.getSelectedItem();
        
        Map<String, Double> categoryData = new HashMap<>();
        
        java.util.List<Transaction> allTransactions = transactionService.getAllTransactions();
        
        for (Transaction t : allTransactions) {
            if ((chartType.equals("Expenses") && "EXPENSE".equals(t.getType())) ||
                (chartType.equals("Income") && "INCOME".equals(t.getType()))) {
                String catName = t.getCategory().getName();
                categoryData.put(catName, categoryData.getOrDefault(catName, 0.0) + t.getAmount());
            }
        }

        if (categoryData.isEmpty()) {
            dataset.setValue("No Data", 100);
        } else {
            for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
            null,
            dataset,
            true,
            true,
            false
        );

        // Customize pie chart
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(CARD_BG);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));

        ChartPanel chPanel = new ChartPanel(pieChart);
        chPanel.setBackground(CARD_BG);
        chPanel.setMouseZoomable(false);
        chPanel.setRangeZoomable(false);
        chPanel.setDomainZoomable(false);

        return chPanel;
    }

    private ChartPanel createBarChart() throws DatabaseException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        String period = (String) timePeriodCombo.getSelectedItem();
        Map<String, Double[]> data = getTimeBasedData(period);

        for (Map.Entry<String, Double[]> entry : data.entrySet()) {
            dataset.addValue(entry.getValue()[0], "Income", entry.getKey());
            dataset.addValue(entry.getValue()[1], "Expense", entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
            null,
            "Date",
            "Amount ($)",
            dataset,
            org.jfree.chart.plot.PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        // Customize bar chart
        CategoryPlot plot = (CategoryPlot) barChart.getPlot();
        plot.setBackgroundPaint(CARD_BG);
        plot.setForegroundAlpha(0.85f);

        // Color bars
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, INCOME_COLOR);      // Income - Green
        renderer.setSeriesPaint(1, EXPENSE_COLOR);     // Expense - Red

        // Customize axes
        CategoryAxis axis = plot.getDomainAxis();
        axis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));

        barChart.setBackgroundPaint(CARD_BG);

        ChartPanel chPanel = new ChartPanel(barChart);
        chPanel.setBackground(CARD_BG);
        chPanel.setMouseZoomable(false);
        chPanel.setRangeZoomable(false);
        chPanel.setDomainZoomable(false);

        return chPanel;
    }

    private Map<String, Double[]> getTimeBasedData(String period) throws DatabaseException {
        Map<String, Double[]> data = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();

        if ("Today".equals(period)) {
            // Show hourly data for today
            for (int hour = 0; hour < 24; hour++) {
                String label = String.format("%02d:00", hour);
                data.put(label, new Double[]{0.0, 0.0});
            }
        } else if ("Week".equals(period)) {
            // Show daily data for current week
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int dayOfWeek = today.get(weekFields.dayOfWeek());
            for (int i = 1; i <= 7; i++) {
                LocalDate date = today.plusDays(i - dayOfWeek);
                data.put(date.toString(), new Double[]{0.0, 0.0});
            }
        } else if ("Month".equals(period)) {
            // Show weekly data for current month
            YearMonth month = YearMonth.now();
            LocalDate start = month.atDay(1);
            for (int week = 0; week < 5; week++) {
                LocalDate weekStart = start.plusWeeks(week);
                if (weekStart.getMonth() == month.getMonth()) {
                    String label = "Week " + (week + 1);
                    data.put(label, new Double[]{0.0, 0.0});
                }
            }
        } else {
            // All time - show monthly data for last 12 months
            for (int i = 11; i >= 0; i--) {
                YearMonth month = YearMonth.now().minusMonths(i);
                data.put(month.toString(), new Double[]{0.0, 0.0});
            }
        }

        return data;
    }

    private void startAutoRefresh() {
        refreshTimer = new java.util.Timer("DashboardRefreshTimer", true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(ModernDashboardPanel.this::updateDashboard);
            }
        }, 10000, 10000);
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
