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

    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    public Expense getExpenseById(UUID id) {
        return expenses.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

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

    public void deleteExpense(UUID id) {
        Expense existing = getExpenseById(id);
        if (existing == null) {
            throw new ValidationException("Expense not found with ID: " + id);
        }

        expenses.remove(existing);
        saveToStorage();
    }

    // Lọc (Yêu cầu Task 1) - theo UML dùng "filter*"

    public List<Expense> filterExpensesByMonth(int year, int month) {
        return expenses.stream()
                .filter(e -> {
                    LocalDate date = e.getOccurredAt();
                    return date.getYear() == year && date.getMonthValue() == month;
                })
                .collect(Collectors.toList());
    }

    public List<Expense> filterExpensesByCategory(UUID categoryId) {
        return expenses.stream()
                .filter(e -> e.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

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

    public BigDecimal calculateMonthlyTotal(int year, int month) {
        return expenses.stream()
                .filter(e -> {
                    LocalDate date = e.getOccurredAt();
                    return date.getYear() == year && date.getMonthValue() == month;
                })
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<UUID, BigDecimal> calculateCategoryTotalsByMonth(int year, int month) {
        Map<UUID, BigDecimal> totals = new HashMap<>();

        for (Expense expense : filterExpensesByMonth(year, month)) {
            UUID categoryId = expense.getCategoryId();
            BigDecimal current = totals.getOrDefault(categoryId, BigDecimal.ZERO);
            totals.put(categoryId, current.add(expense.getAmount()));
        }

        return totals;
    }

    public String getCategoryName(UUID categoryId) {
        ExpenseCategory category = categoryManager.getCategoryById(categoryId);
        return category != null ? category.getName() : "Unknown";
    }

    public List<ExpenseCategory> getAllCategories() {
        return categoryManager.getAllCategories();
    }

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

    private void saveToStorage() {
        repository.save(expenses);
    }
}
