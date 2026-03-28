package com.expensetracker.persistence;

import com.expensetracker.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Initializes database schema and tables
 */
public class DatabaseInitializer {
    private static final String CREATE_CATEGORIES_TABLE = 
        "CREATE TABLE IF NOT EXISTS categories (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "name TEXT NOT NULL," +
        "type TEXT NOT NULL," +
        "is_custom INTEGER NOT NULL" +
        ")";

    private static final String CREATE_TRANSACTIONS_TABLE = 
        "CREATE TABLE IF NOT EXISTS transactions (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
        "type TEXT NOT NULL," +
        "amount REAL NOT NULL," +
        "description TEXT," +
        "date_time TIMESTAMP NOT NULL," +
        "category_id INTEGER NOT NULL," +
        "payment_method TEXT," +
        "source TEXT," +
        "FOREIGN KEY (category_id) REFERENCES categories(id)" +
        ")";

    public static void initializeDatabase() throws DatabaseException {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();

            // Create tables
            statement.execute(CREATE_CATEGORIES_TABLE);
            statement.execute(CREATE_TRANSACTIONS_TABLE);

            // Insert default categories
            insertDefaultCategories(connection);

            statement.close();
        } catch (Exception e) {
            throw new DatabaseException("Failed to initialize database", e);
        }
    }

    private static void insertDefaultCategories(Connection connection) throws Exception {
        // Check if categories already exist
        String checkQuery = "SELECT COUNT(*) FROM categories";
        java.sql.ResultSet resultSet = connection.createStatement().executeQuery(checkQuery);
        resultSet.next();
        int count = resultSet.getInt(1);
        resultSet.close();

        if (count > 0) {
            return; // Categories already exist
        }

        String[] incomeCategories = {"Salary", "Bonus", "Freelance", "Investment"};
        String[] expenseCategories = {"Food", "Transportation", "Shopping", "Utilities", "Entertainment", "Healthcare"};

        String insertQuery = "INSERT INTO categories (name, type, is_custom) VALUES (?, ?, ?)";
        java.sql.PreparedStatement pstmt = connection.prepareStatement(insertQuery);

        for (String category : incomeCategories) {
            pstmt.setString(1, category);
            pstmt.setString(2, "INCOME");
            pstmt.setInt(3, 0);
            pstmt.addBatch();
        }

        for (String category : expenseCategories) {
            pstmt.setString(1, category);
            pstmt.setString(2, "EXPENSE");
            pstmt.setInt(3, 0);
            pstmt.addBatch();
        }

        pstmt.executeBatch();
        pstmt.close();
    }
}
