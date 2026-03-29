package com.expensetracker.ui;

import com.expensetracker.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        try {
            setupUI();
        } catch (AppException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private void setupUI() throws AppException {
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", new DashboardPanel());
        tabbedPane.addTab("Transactions", new TransactionsPanel());
        tabbedPane.addTab("Categories", new CategoriesPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}
