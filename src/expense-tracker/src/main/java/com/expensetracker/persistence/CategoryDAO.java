package com.expensetracker.persistence;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.models.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Category operations
 */
public class CategoryDAO {
    private Connection connection;

    public CategoryDAO() throws DatabaseException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public void addCategory(Category category) throws DatabaseException {
        try {
            String query = "INSERT INTO categories (name, type, is_custom) VALUES (?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getType());
            pstmt.setInt(3, category.isCustom() ? 1 : 0);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add category", e);
        }
    }

    public Category getCategoryById(int id) throws DatabaseException {
        try {
            String query = "SELECT * FROM categories WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            Category category = null;
            if (rs.next()) {
                category = new Category(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getInt("is_custom") == 1
                );
            }
            rs.close();
            pstmt.close();
            return category;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get category", e);
        }
    }

    public List<Category> getAllCategories() throws DatabaseException {
        List<Category> categories = new ArrayList<>();
        try {
            String query = "SELECT * FROM categories";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getInt("is_custom") == 1
                ));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get all categories", e);
        }
        return categories;
    }

    public List<Category> getCategoriesByType(String type) throws DatabaseException {
        List<Category> categories = new ArrayList<>();
        try {
            String query = "SELECT * FROM categories WHERE type = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("type"),
                    rs.getInt("is_custom") == 1
                ));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get categories by type", e);
        }
        return categories;
    }

    public void updateCategory(Category category) throws DatabaseException {
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
            throw new DatabaseException("Failed to update category", e);
        }
    }

    public void deleteCategory(int id) throws DatabaseException {
        try {
            String query = "DELETE FROM categories WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete category", e);
        }
    }
}
