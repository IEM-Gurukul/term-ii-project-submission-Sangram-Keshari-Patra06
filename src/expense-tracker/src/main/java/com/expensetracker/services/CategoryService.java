package com.expensetracker.services;

import com.expensetracker.exceptions.DatabaseException;
import com.expensetracker.models.Category;
import com.expensetracker.persistence.CategoryDAO;
import java.util.List;

/**
 * Service layer for category operations
 */
public class CategoryService {
    private CategoryDAO categoryDAO;

    public CategoryService() throws DatabaseException {
        this.categoryDAO = new CategoryDAO();
    }

    public void createCategory(Category category) throws DatabaseException {
        categoryDAO.addCategory(category);
    }

    public Category getCategory(int id) throws DatabaseException {
        return categoryDAO.getCategoryById(id);
    }

    public List<Category> getAllCategories() throws DatabaseException {
        return categoryDAO.getAllCategories();
    }

    public List<Category> getCategoriesByType(String type) throws DatabaseException {
        return categoryDAO.getCategoriesByType(type);
    }

    public void updateCategory(Category category) throws DatabaseException {
        categoryDAO.updateCategory(category);
    }

    public void deleteCategory(int id) throws DatabaseException {
        categoryDAO.deleteCategory(id);
    }
}
