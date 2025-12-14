package model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Category for expenses with an optional monthly budget cap.
 */
public class ExpenseCategory extends Category {
    private final BigDecimal monthlyBudgetLimit;

    public ExpenseCategory(UUID id, UUID userId, String name, String description, BigDecimal monthlyBudgetLimit) {
        super(id, userId, name, description);
        this.monthlyBudgetLimit = monthlyBudgetLimit;
    }

    public BigDecimal getMonthlyBudgetLimit() {
        return monthlyBudgetLimit;
    }
}
