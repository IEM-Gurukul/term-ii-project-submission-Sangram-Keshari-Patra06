package com.expensetracker.models;

import java.time.LocalDateTime;

public class Expense extends Transaction {
    private String paymentMethod;

    public Expense(double amount, String description, LocalDateTime dateTime, Category category, String paymentMethod) {
        super(amount, description, dateTime, category);
        this.paymentMethod = paymentMethod;
    }

    public Expense(int id, double amount, String description, LocalDateTime dateTime, Category category, String paymentMethod) {
        super(id, amount, description, dateTime, category);
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String getType() {
        return "EXPENSE";
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return super.toString() + " | Method: " + paymentMethod;
    }
}
