package com.expensetracker.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class to manage UI updates across panels
 * Implements observer pattern for cross-panel communication
 */
public class UIUpdateManager {
    private static UIUpdateManager instance;
    private final List<UIUpdateListener> listeners;

    private UIUpdateManager() {
        this.listeners = new ArrayList<>();
    }

    /**
     * Get singleton instance
     */
    public static synchronized UIUpdateManager getInstance() {
        if (instance == null) {
            instance = new UIUpdateManager();
        }
        return instance;
    }

    /**
     * Register a listener for UI updates
     */
    public void registerListener(UIUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Unregister a listener
     */
    public void unregisterListener(UIUpdateListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notify all listeners about transaction updates
     */
    public void notifyTransactionUpdated() {
        for (UIUpdateListener listener : new ArrayList<>(listeners)) {
            listener.onTransactionUpdated();
        }
    }

    /**
     * Notify all listeners about category updates
     */
    public void notifyCategoryUpdated() {
        for (UIUpdateListener listener : new ArrayList<>(listeners)) {
            listener.onCategoryUpdated();
        }
    }

    /**
     * Notify all listeners to refresh data
     */
    public void notifyRefreshRequired() {
        for (UIUpdateListener listener : new ArrayList<>(listeners)) {
            listener.onRefreshRequired();
        }
    }
}
