package com.expensetracker;

import com.expensetracker.models.*;
import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private static final String DB_PATH = System.getProperty("user.home") + File.separator + "expensetracker_db.db";

    private DatabaseManager() throws AppException {
        try {
            File dbFile = new File(DB_PATH);
            String absolutePath = dbFile.getAbsolutePath();
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            Class.forName("org.sqlite.JDBC");
            String dbUrl = "jdbc:sqlite:" + absolutePath;
            this.connection = DriverManager.getConnection(dbUrl);
            initializeDatabase();
        } catch (Exception e) {
            throw new AppException("Database connection failed", e);
        }
    }

    public static synchronized DatabaseManager getInstance() throws AppException {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() throws AppException {
        try {
            String createCategories = "CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "type TEXT NOT NULL," +
                "is_custom INTEGER NOT NULL)";

            String createTransactions = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "description TEXT," +
                "date_time TIMESTAMP NOT NULL," +
                "category_id INTEGER NOT NULL," +
                "payment_method TEXT," +
                "source TEXT," +
                "FOREIGN KEY (category_id) REFERENCES categories(id))";

            Statement stmt = connection.createStatement();
            stmt.execute(createCategories);
            stmt.execute(createTransactions);
            stmt.close();

            if (getCategoryCount() == 0) {
                insertDefaultCategories();
            }
        } catch (SQLException e) {
            throw new AppException("Database initialization failed", e);
        }
    }

    private int getCategoryCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM categories";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        rs.next();
        int count = rs.getInt(1);
        rs.close();
        stmt.close();
        return count;
    }

    private void insertDefaultCategories() throws SQLException {
        String[] income = {"Salary", "Bonus", "Freelance", "Investment"};
        String[] expense = {"Food", "Transportation", "Shopping", "Utilities", "Entertainment", "Healthcare"};
        String query = "INSERT INTO categories (name, type, is_custom) VALUES (?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(query);

        for (String cat : income) {
            pstmt.setString(1, cat);
            pstmt.setString(2, "INCOME");
            pstmt.setInt(3, 0);
            pstmt.addBatch();
        }

        for (String cat : expense) {
            pstmt.setString(1, cat);
            pstmt.setString(2, "EXPENSE");
            pstmt.setInt(3, 0);
            pstmt.addBatch();
        }

        pstmt.executeBatch();
        pstmt.close();
    }

    public Category getCategory(int id) throws AppException {
        try {
            String query = "SELECT * FROM categories WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            Category category = null;
            if (rs.next()) {
                category = new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type"), rs.getInt("is_custom") == 1);
            }
            rs.close();
            pstmt.close();
            return category;
        } catch (SQLException e) {
            throw new AppException("Failed to get category", e);
        }
    }

    public List<Category> getAllCategories() throws AppException {
        List<Category> categories = new ArrayList<>();
        try {
            String query = "SELECT * FROM categories";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type"), rs.getInt("is_custom") == 1));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new AppException("Failed to get categories", e);
        }
        return categories;
    }

    public List<Category> getCategoriesByType(String type) throws AppException {
        List<Category> categories = new ArrayList<>();
        try {
            String query = "SELECT * FROM categories WHERE type = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                categories.add(new Category(rs.getInt("id"), rs.getString("name"), rs.getString("type"), rs.getInt("is_custom") == 1));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new AppException("Failed to get categories by type", e);
        }
        return categories;
    }

    public void addCategory(Category category) throws AppException {
        try {
            String query = "INSERT INTO categories (name, type, is_custom) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getType());
            pstmt.setInt(3, category.isCustom() ? 1 : 0);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new AppException("Failed to add category", e);
        }
    }

    public void updateCategory(Category category) throws AppException {
        try {
            String query = "UPDATE categories SET name = ?, type = ?, is_custom = ? WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getType());
            pstmt.setInt(3, category.isCustom() ? 1 : 0);
            pstmt.setInt(4, category.getId());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new AppException("Failed to update category", e);
        }
    }

    public void deleteCategory(int id) throws AppException {
        try {
            String query = "DELETE FROM categories WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new AppException("Failed to delete category", e);
        }
    }

    public void addTransaction(Transaction transaction) throws AppException {
        try {
            String query = "INSERT INTO transactions (type, amount, description, date_time, category_id, payment_method, source) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, transaction.getType());
            pstmt.setDouble(2, transaction.getAmount());
            pstmt.setString(3, transaction.getDescription());
            pstmt.setString(4, transaction.getDateTime().toString());
            pstmt.setInt(5, transaction.getCategory().getId());

            if (transaction instanceof Expense) {
                pstmt.setString(6, ((Expense) transaction).getPaymentMethod());
                pstmt.setNull(7, Types.VARCHAR);
            } else if (transaction instanceof Income) {
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setString(7, ((Income) transaction).getSource());
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new AppException("Failed to add transaction", e);
        }
    }

    public Transaction getTransaction(int id) throws AppException {
        try {
            String query = "SELECT * FROM transactions WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            Transaction transaction = null;
            if (rs.next()) {
                transaction = createTransactionFromResultSet(rs);
            }
            rs.close();
            pstmt.close();
            return transaction;
        } catch (SQLException e) {
            throw new AppException("Failed to get transaction", e);
        }
    }

    public List<Transaction> getAllTransactions() throws AppException {
        List<Transaction> transactions = new ArrayList<>();
        try {
            String query = "SELECT * FROM transactions ORDER BY date_time DESC";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                transactions.add(createTransactionFromResultSet(rs));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new AppException("Failed to get transactions", e);
        }
        return transactions;
    }

    public void updateTransaction(Transaction transaction) throws AppException {
        try {
            String query = "UPDATE transactions SET type = ?, amount = ?, description = ?, date_time = ?, category_id = ?, payment_method = ?, source = ? WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, transaction.getType());
            pstmt.setDouble(2, transaction.getAmount());
            pstmt.setString(3, transaction.getDescription());
            pstmt.setString(4, transaction.getDateTime().toString());
            pstmt.setInt(5, transaction.getCategory().getId());

            if (transaction instanceof Expense) {
                pstmt.setString(6, ((Expense) transaction).getPaymentMethod());
                pstmt.setNull(7, Types.VARCHAR);
            } else if (transaction instanceof Income) {
                pstmt.setNull(6, Types.VARCHAR);
                pstmt.setString(7, ((Income) transaction).getSource());
            }

            pstmt.setInt(8, transaction.getId());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new AppException("Failed to update transaction", e);
        }
    }

    public void deleteTransaction(int id) throws AppException {
        try {
            String query = "DELETE FROM transactions WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new AppException("Failed to delete transaction", e);
        }
    }

    private Transaction createTransactionFromResultSet(ResultSet rs) throws SQLException, AppException {
        int id = rs.getInt("id");
        String type = rs.getString("type");
        double amount = rs.getDouble("amount");
        String description = rs.getString("description");
        LocalDateTime dateTime = LocalDateTime.parse(rs.getString("date_time"));
        int categoryId = rs.getInt("category_id");

        Category category = getCategory(categoryId);

        if ("INCOME".equals(type)) {
            String source = rs.getString("source");
            return new Income(id, amount, description, dateTime, category, source);
        } else {
            String paymentMethod = rs.getString("payment_method");
            return new Expense(id, amount, description, dateTime, category, paymentMethod);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
