package ui;

import model.Expense;
import model.ExpenseCategory;
import service.ExpenseManagerService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class ExpensePanel extends JPanel {
    private final ExpenseManagerService service;
    private final Runnable onDataChanged;

    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JComboBox<FilterOption<UUID>> categoryFilterBox;
    private final JComboBox<FilterOption<YearMonth>> monthFilterBox;

    private List<Expense> allExpenses = new ArrayList<>();
    private List<Expense> displayedExpenses = new ArrayList<>();
    private Map<UUID, ExpenseCategory> categoryLookup = new HashMap<>();
    private boolean suppressFilterEvents;

    public ExpensePanel(ExpenseManagerService service, Runnable onDataChanged) {
        this.service = service;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(8, 8));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Date", "Category", "Amount", "Currency", "Note"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        categoryFilterBox = new JComboBox<>();
        monthFilterBox = new JComboBox<>();

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Category"));
        filterPanel.add(categoryFilterBox);
        filterPanel.add(new JLabel("Month"));
        filterPanel.add(monthFilterBox);

        JButton applyFilterButton = new JButton("Apply Filter");
        JButton clearFilterButton = new JButton("Clear Filter");
        filterPanel.add(applyFilterButton);
        filterPanel.add(clearFilterButton);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        actions.add(refreshButton);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(filterPanel, BorderLayout.NORTH);
        northPanel.add(actions, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        addButton.addActionListener(e -> createExpense());
        editButton.addActionListener(e -> editExpense());
        deleteButton.addActionListener(e -> deleteExpense());
        refreshButton.addActionListener(e -> reloadData());
        applyFilterButton.addActionListener(e -> applyFilters());
        clearFilterButton.addActionListener(e -> clearFilters());
        categoryFilterBox.addActionListener(e -> {
            if (!suppressFilterEvents) {
                applyFilters();
            }
        });
        monthFilterBox.addActionListener(e -> {
            if (!suppressFilterEvents) {
                applyFilters();
            }
        });
    }

    public void reloadData() {
        UUID selectedCategoryId = getSelectedCategoryFilter();
        YearMonth selectedMonth = getSelectedMonthFilter();

        List<ExpenseCategory> categories = service.getCategories();
        allExpenses = service.getExpenses();
        categoryLookup = service.getCategoryLookup();

        populateCategoryFilter(categories, selectedCategoryId);
        populateMonthFilter(allExpenses, selectedMonth);
        applyFilters();
    }

    private void populateCategoryFilter(List<ExpenseCategory> categories, UUID selectedCategoryId) {
        suppressFilterEvents = true;
        try {
            categoryFilterBox.removeAllItems();
            categoryFilterBox.addItem(new FilterOption<>("All Categories", null));

            int selectedIndex = 0;
            int index = 1;
            for (ExpenseCategory category : categories) {
                categoryFilterBox.addItem(new FilterOption<>(category.getName(), category.getId()));
                if (selectedCategoryId != null && selectedCategoryId.equals(category.getId())) {
                    selectedIndex = index;
                }
                index++;
            }

            categoryFilterBox.setSelectedIndex(selectedIndex);
        } finally {
            suppressFilterEvents = false;
        }
    }

    private void populateMonthFilter(List<Expense> expenses, YearMonth selectedMonth) {
        Set<YearMonth> months = new TreeSet<>((left, right) -> right.compareTo(left));
        for (Expense expense : expenses) {
            months.add(YearMonth.from(expense.getOccurredAt()));
        }

        suppressFilterEvents = true;
        try {
            monthFilterBox.removeAllItems();
            monthFilterBox.addItem(new FilterOption<>("All Months", null));

            int selectedIndex = 0;
            int index = 1;
            for (YearMonth month : months) {
                monthFilterBox.addItem(new FilterOption<>(month.toString(), month));
                if (selectedMonth != null && selectedMonth.equals(month)) {
                    selectedIndex = index;
                }
                index++;
            }

            monthFilterBox.setSelectedIndex(selectedIndex);
        } finally {
            suppressFilterEvents = false;
        }
    }

    private void applyFilters() {
        UUID selectedCategoryId = getSelectedCategoryFilter();
        YearMonth selectedMonth = getSelectedMonthFilter();

        displayedExpenses = new ArrayList<>();
        for (Expense expense : allExpenses) {
            if (selectedCategoryId != null && !selectedCategoryId.equals(expense.getCategoryId())) {
                continue;
            }

            if (selectedMonth != null && !selectedMonth.equals(YearMonth.from(expense.getOccurredAt()))) {
                continue;
            }

            displayedExpenses.add(expense);
        }

        renderTable();
    }

    private void clearFilters() {
        suppressFilterEvents = true;
        try {
            if (categoryFilterBox.getItemCount() > 0) {
                categoryFilterBox.setSelectedIndex(0);
            }
            if (monthFilterBox.getItemCount() > 0) {
                monthFilterBox.setSelectedIndex(0);
            }
        } finally {
            suppressFilterEvents = false;
        }
        applyFilters();
    }

    private void renderTable() {
        tableModel.setRowCount(0);
        for (Expense expense : displayedExpenses) {
            ExpenseCategory category = categoryLookup.get(expense.getCategoryId());
            String categoryName = category == null ? "<Missing Category>" : category.getName();

            tableModel.addRow(new Object[]{
                    expense.getId(),
                    expense.getOccurredAt(),
                    categoryName,
                    expense.getAmount().stripTrailingZeros().toPlainString(),
                    expense.getCurrency(),
                    expense.getNote()
            });
        }
    }

    private void createExpense() {
        List<ExpenseCategory> categories = service.getCategories();
        if (categories.isEmpty()) {
            showError("Please create at least one category before adding expenses.");
            return;
        }

        ExpenseInput input = promptExpenseInput(null, categories);
        if (input == null) {
            return;
        }

        try {
            service.createExpense(input.categoryId, input.amount, input.currency, input.date, input.note);
            onDataChanged.run();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void editExpense() {
        Expense selected = getSelectedExpense();
        if (selected == null) {
            showError("Please select an expense to edit.");
            return;
        }

        List<ExpenseCategory> categories = service.getCategories();
        if (categories.isEmpty()) {
            showError("No categories found.");
            return;
        }

        ExpenseInput input = promptExpenseInput(selected, categories);
        if (input == null) {
            return;
        }

        try {
            service.updateExpense(selected.getId(), input.categoryId, input.amount, input.currency, input.date, input.note);
            onDataChanged.run();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteExpense() {
        Expense selected = getSelectedExpense();
        if (selected == null) {
            showError("Please select an expense to delete.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Delete selected expense?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            service.deleteExpense(selected.getId());
            onDataChanged.run();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private Expense getSelectedExpense() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= displayedExpenses.size()) {
            return null;
        }
        return displayedExpenses.get(row);
    }

    private ExpenseInput promptExpenseInput(Expense existing, List<ExpenseCategory> categories) {
        JComboBox<ExpenseCategory> categoryBox = new JComboBox<>(categories.toArray(new ExpenseCategory[0]));
        JTextField amountField = new JTextField(existing == null ? "" : existing.getAmount().stripTrailingZeros().toPlainString());
        JTextField currencyField = new JTextField(existing == null ? "VND" : existing.getCurrency());
        JTextField dateField = new JTextField(existing == null ? LocalDate.now().toString() : existing.getOccurredAt().toString());
        JTextField noteField = new JTextField(existing == null ? "" : existing.getNote());

        if (existing != null) {
            for (ExpenseCategory category : categories) {
                if (category.getId().equals(existing.getCategoryId())) {
                    categoryBox.setSelectedItem(category);
                    break;
                }
            }
        }

        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Category"));
        panel.add(categoryBox);
        panel.add(new JLabel("Amount"));
        panel.add(amountField);
        panel.add(new JLabel("Currency (default VND)"));
        panel.add(currencyField);
        panel.add(new JLabel("Date (yyyy-MM-dd)"));
        panel.add(dateField);
        panel.add(new JLabel("Note"));
        panel.add(noteField);

        String title = existing == null ? "Create Expense" : "Edit Expense";
        while (true) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    title,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return null;
            }

            ExpenseCategory category = (ExpenseCategory) categoryBox.getSelectedItem();
            if (category == null) {
                showError("Please select a category.");
                continue;
            }

            BigDecimal amount;
            try {
                amount = new BigDecimal(amountField.getText().trim());
            } catch (NumberFormatException ex) {
                showError("Amount must be a valid decimal number.");
                continue;
            }

            LocalDate date;
            try {
                date = LocalDate.parse(dateField.getText().trim());
            } catch (DateTimeParseException ex) {
                showError("Date must follow format yyyy-MM-dd.");
                continue;
            }

            return new ExpenseInput(
                    category.getId(),
                    amount,
                    currencyField.getText(),
                    date,
                    noteField.getText()
            );
        }
    }

    private UUID getSelectedCategoryFilter() {
        int index = categoryFilterBox.getSelectedIndex();
        if (index < 0) {
            return null;
        }
        return categoryFilterBox.getItemAt(index).getValue();
    }

    private YearMonth getSelectedMonthFilter() {
        int index = monthFilterBox.getSelectedIndex();
        if (index < 0) {
            return null;
        }
        return monthFilterBox.getItemAt(index).getValue();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class FilterOption<T> {
        private final String label;
        private final T value;

        private FilterOption(String label, T value) {
            this.label = label;
            this.value = value;
        }

        private T getValue() {
            return value;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static class ExpenseInput {
        private final UUID categoryId;
        private final BigDecimal amount;
        private final String currency;
        private final LocalDate date;
        private final String note;

        private ExpenseInput(UUID categoryId, BigDecimal amount, String currency, LocalDate date, String note) {
            this.categoryId = categoryId;
            this.amount = amount;
            this.currency = currency;
            this.date = date;
            this.note = note;
        }
    }
}
