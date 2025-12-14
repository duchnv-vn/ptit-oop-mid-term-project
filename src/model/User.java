package model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Person using the application.
 */
public class User {
    private final UUID id;
    private final String username;
    private final String email;
    private final String preferredCurrency;
    private final LocalDate createdDate;

    public User(UUID id, String username, String email, String preferredCurrency, LocalDate createdDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.preferredCurrency = preferredCurrency;
        this.createdDate = createdDate;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }
}
