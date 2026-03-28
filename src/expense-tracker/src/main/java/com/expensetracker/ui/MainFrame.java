package com.expensetracker.ui;

import com.expensetracker.persistence.DatabaseInitializer;
import com.expensetracker.ui.panels.*;
import com.expensetracker.exceptions.DatabaseException;
import javax.swing.*;
import java.awt.*;

/**
 * Main application window frame
 */
public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;

    public MainFrame() {
        try {
            // Initialize database
            DatabaseInitializer.initializeDatabase();

            setupUI();
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void setupUI() {
        setTitle("Expense Tracker Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        try {
            tabbedPane.addTab("Dashboard", new DashboardPanel());
            tabbedPane.addTab("Transactions", new TransactionPanel());
            tabbedPane.addTab("History", new HistoryPanel());
            tabbedPane.addTab("Categories", new CategoryPanel());
            tabbedPane.addTab("Charts", new ExpenseChartPanel());
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error loading panels: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        add(tabbedPane, BorderLayout.CENTER);
    }
}
