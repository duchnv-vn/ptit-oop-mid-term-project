package service;

import model.ExpenseCategory;
import storage.CategoryCsvRepository;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manager for managing expense categories.
 * Handles CRUD operations and business validation for categories.
 */
public class CategoryManager {

    private final CategoryCsvRepository repository;
    private final List<ExpenseCategory> categories;

    public CategoryManager(Path csvFilePath) {
        this.repository = new CategoryCsvRepository(csvFilePath);
        this.categories = new ArrayList<>(repository.load());
    }

    // CRUD cho Category

    /**
     * Gets all categories.
     * @return list of all categories
     */
    public List<ExpenseCategory> getAllCategories() {
        return new ArrayList<>(categories);
    }

    /**
     * Finds a category by ID.
     * @param id the category ID
     * @return the category, or null if not found
     */
    public ExpenseCategory getCategoryById(UUID id) {
        return categories.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Creates a new category.
     * @param name the category name (required, max 100 chars)
     * @param description the category description (optional, max 500 chars)
     * @param monthlyBudgetLimit the monthly budget limit (optional, must be positive if provided)
     * @return the created category
     * @throws ValidationException if validation fails
     */
    public ExpenseCategory createCategory(String name, String description, BigDecimal monthlyBudgetLimit) {
        validateCategoryName(name);
        validateDescription(description);
        validateMonthlyBudget(monthlyBudgetLimit);

        UUID id = UUID.randomUUID();
        ExpenseCategory category = new ExpenseCategory(id, name, description, monthlyBudgetLimit);
        categories.add(category);
        saveToStorage();

        return category;
    }

    /**
     * Updates an existing category.
     * @param id the category ID
     * @param name the new name (required, max 100 chars)
     * @param description the new description (optional, max 500 chars)
     * @param monthlyBudgetLimit the new monthly budget limit (optional, must be positive if provided)
     * @return the updated category
     * @throws ValidationException if validation fails or category not found
     */
    public ExpenseCategory updateCategory(UUID id, String name, String description, BigDecimal monthlyBudgetLimit) {
        ExpenseCategory existing = getCategoryById(id);
        if (existing == null) {
            throw new ValidationException("Category not found with ID: " + id);
        }

        validateCategoryName(name);
        validateDescription(description);
        validateMonthlyBudget(monthlyBudgetLimit);

        ExpenseCategory updated = new ExpenseCategory(id, name, description, monthlyBudgetLimit);

        // Remove old and add updated
        categories.remove(existing);
        categories.add(updated);
        saveToStorage();

        return updated;
    }

    /**
     * Deletes a category.
     * Note: This is a simple implementation. In production, you should check
     * if the category is referenced by any expenses before deleting.
     * @param id the category ID
     * @throws ValidationException if category not found
     */
    public void deleteCategory(UUID id) {
        ExpenseCategory existing = getCategoryById(id);
        if (existing == null) {
            throw new ValidationException("Category not found with ID: " + id);
        }

        categories.remove(existing);
        saveToStorage();
    }

    // Validation methods

    private void validateCategoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Category name is required");
        }
        if (name.length() > 100) {
            throw new ValidationException("Category name must not exceed 100 characters");
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > 500) {
            throw new ValidationException("Description must not exceed 500 characters");
        }
    }

    private void validateMonthlyBudget(BigDecimal limit) {
        if (limit != null && limit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Monthly budget must be positive");
        }
    }

    // Data persistence

    private void saveToStorage() {
        repository.save(categories);
    }
}
