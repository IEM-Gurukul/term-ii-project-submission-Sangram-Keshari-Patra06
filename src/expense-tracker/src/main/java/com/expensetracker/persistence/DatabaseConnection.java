package com.expensetracker.persistence;

import com.expensetracker.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.io.File;

/**
 * Database connection manager using SQLite
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static final String DB_PATH = "expensetracker_db.db";

    private DatabaseConnection() throws DatabaseException {
        try {
            Class.forName("org.sqlite.JDBC");
            String dbUrl = "jdbc:sqlite:" + DB_PATH;
            this.connection = DriverManager.getConnection(dbUrl);
        } catch (Exception e) {
            throw new DatabaseException("Failed to connect to database", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() throws DatabaseException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() throws DatabaseException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            throw new DatabaseException("Failed to close database connection", e);
        }
    }

    public static String getDatabasePath() {
        return new File(DB_PATH).getAbsolutePath();
    }
}
