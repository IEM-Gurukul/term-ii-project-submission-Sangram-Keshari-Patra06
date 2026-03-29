package com.expensetracker.ui;

import com.expensetracker.*;
import com.expensetracker.models.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private AppService service;
    private JLabel balanceLabel;

    public DashboardPanel() throws AppException {
        service = new AppService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(balanceLabel);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshData());
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.add(createPieChart(), BorderLayout.CENTER);
        add(chartPanel, BorderLayout.CENTER);
        
        refreshData();
    }

    private ChartPanel createPieChart() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        
        try {
            List<Transaction> transactions = service.getAllTransactions();
            Map<String, Double> expenses = new java.util.HashMap<>();
            
            for (Transaction t : transactions) {
                if ("EXPENSE".equals(t.getType())) {
                    String cat = t.getCategory().getName();
                    expenses.put(cat, expenses.getOrDefault(cat, 0.0) + t.getAmount());
                }
            }
            
            for (String category : expenses.keySet()) {
                dataset.setValue(category, expenses.get(category));
            }
        } catch (AppException e) {
            e.printStackTrace();
        }
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Expenses by Category",
            dataset,
            true, true, false
        );
        
        return new ChartPanel(chart);
    }

    private void refreshData() {
        try {
            double totalIncome = 0;
            double totalExpense = 0;
            
            for (Transaction t : service.getAllTransactions()) {
                if ("INCOME".equals(t.getType())) {
                    totalIncome += t.getAmount();
                } else {
                    totalExpense += t.getAmount();
                }
            }
            
            double balance = totalIncome - totalExpense;
            balanceLabel.setText(String.format("Balance: $%.2f", balance));
            
            removeAll();
            revalidate();
            repaint();
            
            JPanel topPanel = new JPanel();
            topPanel.setLayout(new FlowLayout());
            balanceLabel = new JLabel(String.format("Balance: $%.2f", balance));
            balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
            topPanel.add(balanceLabel);
            
            JButton refreshBtn = new JButton("Refresh");
            refreshBtn.addActionListener(e -> refreshData());
            topPanel.add(refreshBtn);
            add(topPanel, BorderLayout.NORTH);
            
            JPanel chartPanel = new JPanel(new BorderLayout());
            chartPanel.add(createPieChart(), BorderLayout.CENTER);
            add(chartPanel, BorderLayout.CENTER);
            
            revalidate();
            repaint();
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
