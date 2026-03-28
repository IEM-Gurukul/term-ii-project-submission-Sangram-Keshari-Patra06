package com.expensetracker;

import com.expensetracker.ui.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the Expense Tracker Application
 * Initializes and launches the Swing GUI application
 */
public class ExpenseTrackerApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
