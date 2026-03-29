package com.expensetracker.models;

import java.time.LocalDateTime;

public abstract class Transaction {
    protected int id;
    protected double amount;
    protected String description;
    protected LocalDateTime dateTime;
    protected Category category;

    public Transaction(double amount, String description, LocalDateTime dateTime, Category category) {
        this.amount = amount;
        this.description = description;
        this.dateTime = dateTime;
        this.category = category;
    }

    public Transaction(int id, double amount, String description, LocalDateTime dateTime, Category category) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.dateTime = dateTime;
        this.category = category;
    }

    public abstract String getType();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return String.format("%s - %.2f | %s | %s", getType(), amount, description, dateTime);
    }
}
