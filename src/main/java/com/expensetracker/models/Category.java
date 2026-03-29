package com.expensetracker.models;

public class Category {
    private int id;
    private String name;
    private String type; 
    private boolean isCustom;

    public Category(int id, String name, String type, boolean isCustom) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isCustom = isCustom;
    }

    public Category(String name, String type, boolean isCustom) {
        this.name = name;
        this.type = type;
        this.isCustom = isCustom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    @Override
    public String toString() {
        return name;
    }
}
