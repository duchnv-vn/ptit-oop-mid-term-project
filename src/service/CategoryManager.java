package service;

import model.ExpenseCategory;
import storage.CategoryCsvRepository;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryManager {

    private final CategoryCsvRepository repository;
    private final List<ExpenseCategory> categories;

    public CategoryManager(Path csvFilePath) {
        this.repository = new CategoryCsvRepository(csvFilePath);
        this.categories = new ArrayList<>(repository.load());
    }

    // CRUD cho Category

    public List<ExpenseCategory> getAllCategories() {
        return new ArrayList<>(categories);
    }

    public ExpenseCategory getCategoryById(UUID id) {
        return categories.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

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

    public void deleteCategory(UUID id) {
        ExpenseCategory existing = getCategoryById(id);
        if (existing == null) {
            throw new ValidationException("Category not found with ID: " + id);
        }

        categories.remove(existing);
        saveToStorage();
    }

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

    private void saveToStorage() {
        repository.save(categories);
    }
}
