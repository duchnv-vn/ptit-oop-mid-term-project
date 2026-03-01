package model;

import java.util.UUID;

/**
 * Base type for user-owned categories such as expenses, income streams, and savings goals.
 */
public class Category {
    private final UUID id;
    private final String name;
    private final String description;

    public Category(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}
