package model;

import java.util.UUID;

/**
 * Base type for user-owned categories such as expenses, income streams, and savings goals.
 */
public class Category {
    private final UUID id;
    private final UUID userId;
    private final String name;
    private final String description;

    public Category(UUID id, UUID userId, String name, String description) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
