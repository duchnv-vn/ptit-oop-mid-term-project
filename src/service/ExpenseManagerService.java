package service;

import model.Expense;
import model.ExpenseCategory;
import storage.CategoryCsvRepository;
import storage.ExpenseCsvRepository;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ExpenseManagerService {
    private final CategoryCsvRepository categoryRepository;
    private final ExpenseCsvRepository expenseRepository;

    private final List<ExpenseCategory> categories;
    private final List<Expense> expenses;

    public ExpenseManagerService(Path dataDirectory) {
        this.categoryRepository = new CategoryCsvRepository(dataDirectory.resolve("categories.csv"));
        this.expenseRepository = new ExpenseCsvRepository(dataDirectory.resolve("expenses.csv"));
        this.categories = new ArrayList<>(categoryRepository.load());
        this.expenses = new ArrayList<>(expenseRepository.load());
    }

    public synchronized List<ExpenseCategory> getCategories() {
        List<ExpenseCategory> sorted = new ArrayList<>(categories);
        sorted.sort(
                Comparator.comparing(ExpenseCategory::getName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(category -> category.getId().toString())
        );
        return sorted;
    }

    public synchronized ExpenseCategory createCategory(String name, String description, BigDecimal monthlyBudgetLimit) {
        String normalizedName = normalizeRequired(name, "Category name");
        String normalizedDescription = normalizeOptional(description);
        BigDecimal normalizedLimit = normalizeLimit(monthlyBudgetLimit);
        ensureUniqueCategoryName(normalizedName, null);

        ExpenseCategory category = new ExpenseCategory(UUID.randomUUID(), normalizedName, normalizedDescription, normalizedLimit);
        categories.add(category);
        saveCategories();
        return category;
    }

    public synchronized ExpenseCategory updateCategory(UUID categoryId, String name, String description, BigDecimal monthlyBudgetLimit) {
        ExpenseCategory existing = findCategoryOrThrow(categoryId);

        String normalizedName = normalizeRequired(name, "Category name");
        String normalizedDescription = normalizeOptional(description);
        BigDecimal normalizedLimit = normalizeLimit(monthlyBudgetLimit);
        ensureUniqueCategoryName(normalizedName, existing.getId());

        ExpenseCategory updated = new ExpenseCategory(existing.getId(), normalizedName, normalizedDescription, normalizedLimit);
        replaceCategory(updated);
        saveCategories();
        return updated;
    }

    public synchronized void deleteCategory(UUID categoryId) {
        findCategoryOrThrow(categoryId);
        for (Expense expense : expenses) {
            if (expense.getCategoryId().equals(categoryId)) {
                throw new IllegalStateException("Cannot delete category because it is used by existing expenses.");
            }
        }

        categories.removeIf(category -> category.getId().equals(categoryId));
        saveCategories();
    }

    public synchronized List<Expense> getExpenses() {
        List<Expense> sorted = new ArrayList<>(expenses);
        sorted.sort(
                Comparator.comparing(Expense::getOccurredAt).reversed()
                        .thenComparing(expense -> expense.getId().toString())
        );
        return sorted;
    }

    public synchronized Expense createExpense(UUID categoryId, BigDecimal amount, String currency, LocalDate occurredAt, String note) {
        validateCategoryExists(categoryId);
        BigDecimal normalizedAmount = normalizePositiveAmount(amount);
        String normalizedCurrency = normalizeCurrency(currency);
        LocalDate normalizedDate = Objects.requireNonNull(occurredAt, "Expense date is required.");
        String normalizedNote = normalizeOptional(note);

        Expense expense = new Expense(
                UUID.randomUUID(),
                categoryId,
                normalizedAmount,
                normalizedCurrency,
                normalizedDate,
                normalizedNote
        );

        expenses.add(expense);
        saveExpenses();
        return expense;
    }

    public synchronized Expense updateExpense(UUID expenseId, UUID categoryId, BigDecimal amount, String currency, LocalDate occurredAt, String note) {
        Expense existing = findExpenseOrThrow(expenseId);

        validateCategoryExists(categoryId);
        BigDecimal normalizedAmount = normalizePositiveAmount(amount);
        String normalizedCurrency = normalizeCurrency(currency);
        LocalDate normalizedDate = Objects.requireNonNull(occurredAt, "Expense date is required.");
        String normalizedNote = normalizeOptional(note);

        Expense updated = new Expense(
                existing.getId(),
                categoryId,
                normalizedAmount,
                normalizedCurrency,
                normalizedDate,
                normalizedNote
        );

        replaceExpense(updated);
        saveExpenses();
        return updated;
    }

    public synchronized void deleteExpense(UUID expenseId) {
        findExpenseOrThrow(expenseId);
        expenses.removeIf(expense -> expense.getId().equals(expenseId));
        saveExpenses();
    }

    public synchronized Map<UUID, ExpenseCategory> getCategoryLookup() {
        Map<UUID, ExpenseCategory> lookup = new HashMap<>();
        for (ExpenseCategory category : categories) {
            lookup.put(category.getId(), category);
        }
        return lookup;
    }

    public synchronized List<MonthlySummary> getMonthlySummaries() {
        Map<YearMonth, BigDecimal> totalsByMonth = new HashMap<>();
        for (Expense expense : expenses) {
            YearMonth month = YearMonth.from(expense.getOccurredAt());
            totalsByMonth.merge(month, expense.getAmount(), BigDecimal::add);
        }

        List<MonthlySummary> summaries = new ArrayList<>();
        for (Map.Entry<YearMonth, BigDecimal> entry : totalsByMonth.entrySet()) {
            summaries.add(new MonthlySummary(entry.getKey(), entry.getValue()));
        }

        summaries.sort(Comparator.comparing(MonthlySummary::getMonth).reversed());
        return summaries;
    }

    private ExpenseCategory findCategoryOrThrow(UUID categoryId) {
        for (ExpenseCategory category : categories) {
            if (category.getId().equals(categoryId)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Category not found.");
    }

    private Expense findExpenseOrThrow(UUID expenseId) {
        for (Expense expense : expenses) {
            if (expense.getId().equals(expenseId)) {
                return expense;
            }
        }
        throw new IllegalArgumentException("Expense not found.");
    }

    private void replaceCategory(ExpenseCategory updatedCategory) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(updatedCategory.getId())) {
                categories.set(i, updatedCategory);
                return;
            }
        }
    }

    private void replaceExpense(Expense updatedExpense) {
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId().equals(updatedExpense.getId())) {
                expenses.set(i, updatedExpense);
                return;
            }
        }
    }

    private void validateCategoryExists(UUID categoryId) {
        for (ExpenseCategory category : categories) {
            if (category.getId().equals(categoryId)) {
                return;
            }
        }
        throw new IllegalArgumentException("Selected category does not exist.");
    }

    private String normalizeRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private String normalizeOptional(String value) {
        return value == null ? "" : value.trim();
    }

    private BigDecimal normalizeLimit(BigDecimal limit) {
        if (limit == null) {
            return null;
        }
        if (limit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Monthly budget limit must be zero or positive.");
        }
        return limit.stripTrailingZeros();
    }

    private BigDecimal normalizePositiveAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Expense amount is required.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero.");
        }
        return amount.stripTrailingZeros();
    }

    private String normalizeCurrency(String currency) {
        String normalized = currency == null ? "" : currency.trim().toUpperCase();
        if (normalized.isEmpty()) {
            return "VND";
        }
        return normalized;
    }

    private void ensureUniqueCategoryName(String categoryName, UUID ignoredCategoryId) {
        for (ExpenseCategory category : categories) {
            if (ignoredCategoryId != null && category.getId().equals(ignoredCategoryId)) {
                continue;
            }
            if (category.getName().equalsIgnoreCase(categoryName)) {
                throw new IllegalArgumentException("Category name already exists.");
            }
        }
    }

    private void saveCategories() {
        categoryRepository.save(categories);
    }

    private void saveExpenses() {
        expenseRepository.save(expenses);
    }
}
