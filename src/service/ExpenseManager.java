package service;

import model.Expense;
import model.ExpenseCategory;
import storage.ExpenseCsvRepository;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manager for managing expenses.
 * Handles CRUD operations, filtering, calculations, and business validation.
 */
public class ExpenseManager {

    private final ExpenseCsvRepository repository;
    private final CategoryManager categoryManager;
    private final List<Expense> expenses;

    public ExpenseManager(Path csvFilePath, CategoryManager categoryManager) {
        this.repository = new ExpenseCsvRepository(csvFilePath);
        this.categoryManager = categoryManager;
        this.expenses = new ArrayList<>(repository.load());
    }

    // CRUD cho Expense

    /**
     * Gets all expenses.
     * @return list of all expenses
     */
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    /**
     * Finds an expense by ID.
     * @param id the expense ID
     * @return the expense, or null if not found
     */
    public Expense getExpenseById(UUID id) {
        return expenses.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Creates a new expense.
     * @param categoryId the category ID (must exist)
     * @param amount the expense amount (must be positive)
     * @param currency the currency code (required)
     * @param occurredAt the date of expense (required, cannot be in future)
     * @param note the expense note (optional)
     * @return the created expense
     * @throws ValidationException if validation fails
     */
    public Expense createExpense(UUID categoryId, BigDecimal amount, String currency,
                                  LocalDate occurredAt, String note) {
        validateCategoryExists(categoryId);
        validateExpenseAmount(amount);
        validateCurrency(currency);
        validateDate(occurredAt);
        validateNote(note);

        UUID id = UUID.randomUUID();
        Expense expense = new Expense(id, categoryId, amount, currency, occurredAt, note);
        expenses.add(expense);
        saveToStorage();

        return expense;
    }

    /**
     * Updates an existing expense.
     * @param id the expense ID
     * @param categoryId the new category ID (must exist)
     * @param amount the new amount (must be positive)
     * @param currency the new currency code (required)
     * @param occurredAt the new date (required, cannot be in future)
     * @param note the new note (optional)
     * @return the updated expense
     * @throws ValidationException if validation fails or expense not found
     */
    public Expense updateExpense(UUID id, UUID categoryId, BigDecimal amount, String currency,
                                  LocalDate occurredAt, String note) {
        Expense existing = getExpenseById(id);
        if (existing == null) {
            throw new ValidationException("Expense not found with ID: " + id);
        }

        validateCategoryExists(categoryId);
        validateExpenseAmount(amount);
        validateCurrency(currency);
        validateDate(occurredAt);
        validateNote(note);

        Expense updated = new Expense(id, categoryId, amount, currency, occurredAt, note);

        // Remove old and add updated
        expenses.remove(existing);
        expenses.add(updated);
        saveToStorage();

        return updated;
    }

    /**
     * Deletes an expense.
     * @param id the expense ID
     * @throws ValidationException if expense not found
     */
    public void deleteExpense(UUID id) {
        Expense existing = getExpenseById(id);
        if (existing == null) {
            throw new ValidationException("Expense not found with ID: " + id);
        }

        expenses.remove(existing);
        saveToStorage();
    }

    // Lọc (Yêu cầu Task 1) - theo UML dùng "filter*"

    /**
     * Filters all expenses for a specific month.
     * @param year the year
     * @param month the month (1-12)
     * @return list of expenses in the specified month
     */
    public List<Expense> filterExpensesByMonth(int year, int month) {
        return expenses.stream()
                .filter(e -> {
                    LocalDate date = e.getOccurredAt();
                    return date.getYear() == year && date.getMonthValue() == month;
                })
                .collect(Collectors.toList());
    }

    /**
     * Filters all expenses for a specific category.
     * @param categoryId the category ID
     * @return list of expenses in the category
     */
    public List<Expense> filterExpensesByCategory(UUID categoryId) {
        return expenses.stream()
                .filter(e -> e.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    /**
     * Filters all expenses for a specific category and month.
     * @param categoryId the category ID
     * @param year the year
     * @param month the month (1-12)
     * @return list of expenses matching both criteria
     */
    public List<Expense> filterExpensesByCategoryAndMonth(UUID categoryId, int year, int month) {
        return expenses.stream()
                .filter(e -> {
                    LocalDate date = e.getOccurredAt();
                    return e.getCategoryId().equals(categoryId)
                            && date.getYear() == year
                            && date.getMonthValue() == month;
                })
                .collect(Collectors.toList());
    }

    // Tính toán (Yêu cầu Task 1) - theo UML dùng "calculate*"

    /**
     * Calculates total expenses for a specific month.
     * @param year the year
     * @param month the month (1-12)
     * @return total amount of expenses in the month
     */
    public BigDecimal calculateMonthlyTotal(int year, int month) {
        return expenses.stream()
                .filter(e -> {
                    LocalDate date = e.getOccurredAt();
                    return date.getYear() == year && date.getMonthValue() == month;
                })
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculates total expenses per category for a specific month.
     * @param year the year
     * @param month the month (1-12)
     * @return map of category ID to total amount
     */
    public Map<UUID, BigDecimal> calculateCategoryTotalsByMonth(int year, int month) {
        Map<UUID, BigDecimal> totals = new HashMap<>();

        for (Expense expense : filterExpensesByMonth(year, month)) {
            UUID categoryId = expense.getCategoryId();
            BigDecimal current = totals.getOrDefault(categoryId, BigDecimal.ZERO);
            totals.put(categoryId, current.add(expense.getAmount()));
        }

        return totals;
    }

    /**
     * Gets the category name for a given category ID.
     * Helper method for UI to display category names instead of IDs.
     * @param categoryId the category ID
     * @return the category name, or "Unknown" if not found
     */
    public String getCategoryName(UUID categoryId) {
        ExpenseCategory category = categoryManager.getCategoryById(categoryId);
        return category != null ? category.getName() : "Unknown";
    }

    /**
     * Gets all categories.
     * Convenience method for UI to access categories.
     * @return list of all categories
     */
    public List<ExpenseCategory> getAllCategories() {
        return categoryManager.getAllCategories();
    }

    // Validation methods

    private void validateExpenseAmount(BigDecimal amount) {
        if (amount == null) {
            throw new ValidationException("Expense amount is required");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Expense amount must be positive");
        }
    }

    private void validateCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new ValidationException("Currency is required");
        }
    }

    private void validateDate(LocalDate occurredAt) {
        if (occurredAt == null) {
            throw new ValidationException("Date is required");
        }
        if (occurredAt.isAfter(LocalDate.now())) {
            throw new ValidationException("Date cannot be in the future");
        }
    }

    private void validateCategoryExists(UUID categoryId) {
        if (categoryId == null) {
            throw new ValidationException("Category ID is required");
        }
        if (categoryManager.getCategoryById(categoryId) == null) {
            throw new ValidationException("Category not found with ID: " + categoryId);
        }
    }

    private void validateNote(String note) {
        if (note != null && note.length() > 1000) {
            throw new ValidationException("Note must not exceed 1000 characters");
        }
    }

    // Data persistence

    private void saveToStorage() {
        repository.save(expenses);
    }
}
