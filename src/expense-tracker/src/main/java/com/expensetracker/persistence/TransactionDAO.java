package com.expensetracker.persistence;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.models.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Transaction operations
 * Handles CRUD operations for Income and Expense transactions
 */
public class TransactionDAO {
    private Connection connection;
    private CategoryDAO categoryDAO;

    public TransactionDAO() throws DatabaseException {
        this.connection = DatabaseConnection.getInstance().getConnection();
        this.categoryDAO = new CategoryDAO();
    }

    public void addTransaction(Transaction transaction) throws DatabaseException {
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
            throw new DatabaseException("Failed to add transaction", e);
        }
    }

    public Transaction getTransactionById(int id) throws DatabaseException {
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
            throw new DatabaseException("Failed to get transaction", e);
        }
    }

    public List<Transaction> getAllTransactions() throws DatabaseException {
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
            throw new DatabaseException("Failed to get all transactions", e);
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByFilter(TransactionFilter filter) throws DatabaseException {
        List<Transaction> transactions = new ArrayList<>();
        try {
            StringBuilder query = new StringBuilder("SELECT * FROM transactions WHERE 1=1");
            ArrayList<Object> parameters = new ArrayList<>();

            if (filter.getStartDate() != null) {
                query.append(" AND date_time >= ?");
                parameters.add(filter.getStartDate().toString());
            }
            if (filter.getEndDate() != null) {
                query.append(" AND date_time <= ?");
                parameters.add(filter.getEndDate().toString());
            }
            if (filter.getMinAmount() != null) {
                query.append(" AND amount >= ?");
                parameters.add(filter.getMinAmount());
            }
            if (filter.getMaxAmount() != null) {
                query.append(" AND amount <= ?");
                parameters.add(filter.getMaxAmount());
            }
            if (filter.getCategory() != null) {
                query.append(" AND category_id = ?");
                parameters.add(filter.getCategory().getId());
            }
            if (filter.getType() != null) {
                query.append(" AND type = ?");
                parameters.add(filter.getType());
            }

            query.append(" ORDER BY date_time DESC");

            PreparedStatement pstmt = connection.prepareStatement(query.toString());
            for (int i = 0; i < parameters.size(); i++) {
                pstmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(createTransactionFromResultSet(rs));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to filter transactions", e);
        }
        return transactions;
    }

    public void updateTransaction(Transaction transaction) throws DatabaseException {
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
            throw new DatabaseException("Failed to update transaction", e);
        }
    }

    public void deleteTransaction(int id) throws DatabaseException {
        try {
            String query = "DELETE FROM transactions WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete transaction", e);
        }
    }

    private Transaction createTransactionFromResultSet(ResultSet rs) throws SQLException, DatabaseException {
        int id = rs.getInt("id");
        String type = rs.getString("type");
        double amount = rs.getDouble("amount");
        String description = rs.getString("description");
        LocalDateTime dateTime = LocalDateTime.parse(rs.getString("date_time"));
        int categoryId = rs.getInt("category_id");
        
        Category category = categoryDAO.getCategoryById(categoryId);
        
        if ("INCOME".equals(type)) {
            String source = rs.getString("source");
            return new Income(id, amount, description, dateTime, category, source);
        } else {
            String paymentMethod = rs.getString("payment_method");
            return new Expense(id, amount, description, dateTime, category, paymentMethod);
        }
    }
}
