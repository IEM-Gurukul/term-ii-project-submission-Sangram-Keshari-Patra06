package com.expensetracker.models;

import java.time.LocalDateTime;

/**
 * Income transaction class
 * Demonstrates inheritance from Transaction base class
 */
public class Income extends Transaction {
    private String source;

    public Income(double amount, String description, LocalDateTime dateTime, Category category, String source) {
        super(amount, description, dateTime, category);
        this.source = source;
    }

    public Income(int id, double amount, String description, LocalDateTime dateTime, Category category, String source) {
        super(id, amount, description, dateTime, category);
        this.source = source;
    }

    @Override
    public String getType() {
        return "INCOME";
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return super.toString() + " | Source: " + source;
    }
}
