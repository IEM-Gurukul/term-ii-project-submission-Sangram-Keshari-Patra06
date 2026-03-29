package com.expensetracker;

import com.expensetracker.ui.MainFrame;
import javax.swing.SwingUtilities;

public class ExpenseTrackerApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Fatal Error: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
