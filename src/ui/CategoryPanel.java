package ui;

import model.ExpenseCategory;
import service.ExpenseManagerService;

import javax.swing.JButton;
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
import java.util.ArrayList;
import java.util.List;

public class CategoryPanel extends JPanel {
    private final ExpenseManagerService service;
    private final Runnable onDataChanged;

    private final DefaultTableModel tableModel;
    private final JTable table;

    private List<ExpenseCategory> displayedCategories = new ArrayList<>();

    public CategoryPanel(ExpenseManagerService service, Runnable onDataChanged) {
        this.service = service;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(8, 8));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Description", "Monthly Limit"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        actions.add(refreshButton);
        add(actions, BorderLayout.NORTH);

        addButton.addActionListener(e -> createCategory());
        editButton.addActionListener(e -> editCategory());
        deleteButton.addActionListener(e -> deleteCategory());
        refreshButton.addActionListener(e -> reloadData());
    }

    public void reloadData() {
        displayedCategories = service.getCategories();
        tableModel.setRowCount(0);
        for (ExpenseCategory category : displayedCategories) {
            String monthlyLimit = category.getMonthlyBudgetLimit() == null
                    ? ""
                    : category.getMonthlyBudgetLimit().stripTrailingZeros().toPlainString();

            tableModel.addRow(new Object[]{
                    category.getId(),
                    category.getName(),
                    category.getDescription(),
                    monthlyLimit
            });
        }
    }

    private void createCategory() {
        CategoryInput input = promptCategoryInput(null);
        if (input == null) {
            return;
        }

        try {
            service.createCategory(input.name, input.description, input.monthlyLimit);
            onDataChanged.run();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void editCategory() {
        ExpenseCategory selected = getSelectedCategory();
        if (selected == null) {
            showError("Please select a category to edit.");
            return;
        }

        CategoryInput input = promptCategoryInput(selected);
        if (input == null) {
            return;
        }

        try {
            service.updateCategory(selected.getId(), input.name, input.description, input.monthlyLimit);
            onDataChanged.run();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteCategory() {
        ExpenseCategory selected = getSelectedCategory();
        if (selected == null) {
            showError("Please select a category to delete.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Delete selected category \"" + selected.getName() + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            service.deleteCategory(selected.getId());
            onDataChanged.run();
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private ExpenseCategory getSelectedCategory() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= displayedCategories.size()) {
            return null;
        }
        return displayedCategories.get(row);
    }

    private CategoryInput promptCategoryInput(ExpenseCategory existing) {
        JTextField nameField = new JTextField(existing == null ? "" : existing.getName());
        JTextField descriptionField = new JTextField(existing == null ? "" : existing.getDescription());
        JTextField monthlyLimitField = new JTextField(
                existing == null || existing.getMonthlyBudgetLimit() == null
                        ? ""
                        : existing.getMonthlyBudgetLimit().stripTrailingZeros().toPlainString()
        );

        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Name"));
        panel.add(nameField);
        panel.add(new JLabel("Description"));
        panel.add(descriptionField);
        panel.add(new JLabel("Monthly limit (optional)"));
        panel.add(monthlyLimitField);

        String title = existing == null ? "Create Category" : "Edit Category";
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

            String limitText = monthlyLimitField.getText().trim();
            BigDecimal limit = null;
            if (!limitText.isEmpty()) {
                try {
                    limit = new BigDecimal(limitText);
                } catch (NumberFormatException ex) {
                    showError("Monthly limit must be a valid decimal number.");
                    continue;
                }
            }

            return new CategoryInput(nameField.getText(), descriptionField.getText(), limit);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class CategoryInput {
        private final String name;
        private final String description;
        private final BigDecimal monthlyLimit;

        private CategoryInput(String name, String description, BigDecimal monthlyLimit) {
            this.name = name;
            this.description = description;
            this.monthlyLimit = monthlyLimit;
        }
    }
}
