package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Tracks a savings goal and current progress.
 */
public class Saving {
    private final UUID id;
    private final UUID userId;
    private final UUID categoryId;
    private final BigDecimal targetAmount;
    private final BigDecimal currentAmount;
    private final LocalDate targetDate;
    private final LocalDateTime lastUpdatedAt;
    private final String note;

    public Saving(UUID id, UUID userId, UUID categoryId, BigDecimal targetAmount, BigDecimal currentAmount, LocalDate targetDate, LocalDateTime lastUpdatedAt, String note) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
        this.lastUpdatedAt = lastUpdatedAt;
        this.note = note;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public String getNote() {
        return note;
    }
}
