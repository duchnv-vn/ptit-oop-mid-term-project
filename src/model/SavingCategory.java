package model;

import java.util.UUID;

/**
 * Category for savings goals (emergency fund, education, vacation, etc).
 */
public class SavingCategory extends Category {
    private final String goalType;

    public SavingCategory(UUID id, UUID userId, String name, String description, String goalType) {
        super(id, userId, name, description);
        this.goalType = goalType;
    }

    public String getGoalType() {
        return goalType;
    }
}
