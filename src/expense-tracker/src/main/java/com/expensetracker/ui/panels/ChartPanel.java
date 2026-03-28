package com.expensetracker.ui.panels;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.services.ReportService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Panel for data visualization
 */
public class ChartPanel extends JPanel {
    private ReportService reportService;

    public ChartPanel() throws DatabaseException {
        this.reportService = new ReportService();
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Expense Analysis");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        try {
            JFreeChart pieChart = createExpenseChart();
            ChartPanel chartPanel = new ChartPanel(pieChart);
            add(chartPanel, BorderLayout.CENTER);
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error creating chart: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JFreeChart createExpenseChart() throws DatabaseException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Double> expensesByCategory = reportService.getExpensesByCategory();

        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        return ChartFactory.createPieChart(
            "Expenses by Category (%)",
            dataset,
            true,
            true,
            false
        );
    }
}
