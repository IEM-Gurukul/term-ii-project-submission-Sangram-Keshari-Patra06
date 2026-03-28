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
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        // Create tabbed pane with custom UI
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));

        try {
            tabbedPane.addTab("📊 Dashboard", new ModernDashboardPanel());
            tabbedPane.addTab("💳 Transactions", new MergedTransactionPanel());
            tabbedPane.addTab("🏷️ Categories", new CategoryPanel());
        } catch (DatabaseException e) {
            JOptionPane.showMessageDialog(this, "Error loading panels: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        add(tabbedPane, BorderLayout.CENTER);
    }
}
