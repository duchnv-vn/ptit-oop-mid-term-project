package model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Category for expenses with an optional monthly budget cap.
 */
public class ExpenseCategory extends Category {
    private final BigDecimal monthlyBudgetLimit;

    public ExpenseCategory(UUID id, String name, String description, BigDecimal monthlyBudgetLimit) {
        super(id, name, description);
        this.monthlyBudgetLimit = monthlyBudgetLimit;
    }

    public BigDecimal getMonthlyBudgetLimit() {
        return monthlyBudgetLimit;
    }
}
