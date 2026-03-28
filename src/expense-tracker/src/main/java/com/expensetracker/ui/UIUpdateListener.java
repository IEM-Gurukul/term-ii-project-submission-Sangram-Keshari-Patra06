package com.expensetracker.ui;

/**
 * Interface for managing cross-panel UI updates
 * Allows panels to broadcast updates to other panels without manual refresh
 */
public interface UIUpdateListener {
    /**
     * Called when a transaction is added or modified
     */
    void onTransactionUpdated();

    /**
     * Called when a category is added or modified
     */
    void onCategoryUpdated();

    /**
     * Called when data needs to be refreshed from the database
     */
    void onRefreshRequired();
}
