package org.example.memobox.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Modelo de dominio que representa un mazo de tarjetas.
 */
public class Deck {

    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    // Campos calculados para la UI (no persistidos en esta tabla)
    private int totalCards;
    private int dueCards;

    public Deck() {}

    public Deck(UUID id, String name, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getTotalCards() { return totalCards; }
    public void setTotalCards(int totalCards) { this.totalCards = totalCards; }

    public int getDueCards() { return dueCards; }
    public void setDueCards(int dueCards) { this.dueCards = dueCards; }

    @Override
    public String toString() { return name != null ? name : ""; }
}
