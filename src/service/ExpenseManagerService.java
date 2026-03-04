package service;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import model.Expense;
import model.ExpenseCategory;

public class ExpenseManagerService {
    private final CategoryManager categoryManager;
    private final ExpenseManager expenseManager;

    public ExpenseManagerService() {
        this.categoryManager = new CategoryManager(Paths.get("data", "categories.csv"));
        this.expenseManager = new ExpenseManager(Paths.get("data", "expenses.csv"), categoryManager);
    }

    public List<ExpenseCategory> getCategories() {
        return categoryManager.getAllCategories();
    }

    public List<Expense> getExpenses() {
        return expenseManager.getAllExpenses();
    }

    public Map<UUID, ExpenseCategory> getCategoryLookup() {
        Map<UUID, ExpenseCategory> map = new HashMap<>();
        for (ExpenseCategory c : getCategories()) {
            map.put(c.getId(), c);
        }
        return map;
    }

    public void createCategory(String name, String description, BigDecimal monthlyLimit) {
        categoryManager.createCategory(name, description, monthlyLimit);
    }

    public void updateCategory(UUID id, String name, String description, BigDecimal monthlyLimit) {
        categoryManager.updateCategory(id, name, description, monthlyLimit);
    }

    public void deleteCategory(UUID id) {
        categoryManager.deleteCategory(id);
    }

    public void createExpense(UUID categoryId, BigDecimal amount, String currency, java.time.LocalDate date, String note) {
        expenseManager.createExpense(categoryId, amount, currency, date, note);
    }

    public void updateExpense(UUID id, UUID categoryId, BigDecimal amount, String currency, java.time.LocalDate date, String note) {
        expenseManager.updateExpense(id, categoryId, amount, currency, date, note);
    }

    public void deleteExpense(UUID id) {
        expenseManager.deleteExpense(id);
    }

    public List<MonthlySummary> getMonthlySummaries() {
        List<Expense> expenses = getExpenses();
        Map<YearMonth, java.math.BigDecimal> totals = new HashMap<>();
        for (Expense e : expenses) {
            YearMonth ym = YearMonth.from(e.getOccurredAt());
            totals.put(ym, totals.getOrDefault(ym, java.math.BigDecimal.ZERO).add(e.getAmount()));
        }

        List<MonthlySummary> summaries = new ArrayList<>();
        for (Map.Entry<YearMonth, java.math.BigDecimal> entry : totals.entrySet()) {
            summaries.add(new MonthlySummary(entry.getKey(), entry.getValue()));
        }

        summaries.sort(Comparator.comparing(MonthlySummary::getMonth).reversed());
        return summaries;
    }
}
