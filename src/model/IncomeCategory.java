package model;

import java.util.UUID;

/**
 * Category for income with a short description of the source (salary, investment, gift, etc).
 */
public class IncomeCategory extends Category {
    private final String sourceType;

    public IncomeCategory(UUID id, UUID userId, String name, String description, String sourceType) {
        super(id, userId, name, description);
        this.sourceType = sourceType;
    }

    public String getSourceType() {
        return sourceType;
    }
}
