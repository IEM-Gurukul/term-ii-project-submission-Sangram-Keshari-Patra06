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
    private static final String DB_PATH = System.getProperty("user.home") + File.separator + "expensetracker_db.db";

    private DatabaseConnection() throws DatabaseException {
        try {
            File dbFile = new File(DB_PATH);
            String absolutePath = dbFile.getAbsolutePath();
            System.out.println("Database file absolute path: " + absolutePath);

            // Ensure parent directory exists
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                System.out.println("Creating parent directory: " + parentDir.getAbsolutePath());
                if (!parentDir.mkdirs()) {
                    System.err.println("Failed to create parent directory");
                }
            }

            if (!dbFile.exists()) {
                System.out.println("Database file does not exist. It will be created automatically.");
            }

            Class.forName("org.sqlite.JDBC");
            String dbUrl = "jdbc:sqlite:" + absolutePath;
            System.out.println("Connecting to database at: " + dbUrl);
            this.connection = DriverManager.getConnection(dbUrl);
            System.out.println("Database connection established successfully.");
        } catch (Exception e) {
            System.err.println("Error during database initialization: " + e.getMessage());
            e.printStackTrace();
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
