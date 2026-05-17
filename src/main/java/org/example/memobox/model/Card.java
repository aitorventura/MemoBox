package org.example.memobox.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Card {

    private UUID id;
    private UUID deckId;
    private String question;
    private String answer;
    private String example;
    private String tag;
    private LocalDateTime createdAt;
    private LocalDateTime lastReviewedAt;
    private LocalDateTime nextReviewAt;
    private int repetitions;
    private int intervalDays;
    private double easeFactor;
    private String status;       // new | learning | review | mastered
    private int timesCorrect;
    private int timesWrong;
    private int masteryLevel;    // 0-5

    public Card() {
        this.easeFactor = 2.5;
        this.repetitions = 0;
        this.intervalDays = 0;
        this.status = "new";
        this.masteryLevel = 0;
        this.nextReviewAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getDeckId() { return deckId; }
    public void setDeckId(UUID deckId) { this.deckId = deckId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastReviewedAt() { return lastReviewedAt; }
    public void setLastReviewedAt(LocalDateTime lastReviewedAt) { this.lastReviewedAt = lastReviewedAt; }

    public LocalDateTime getNextReviewAt() { return nextReviewAt; }
    public void setNextReviewAt(LocalDateTime nextReviewAt) { this.nextReviewAt = nextReviewAt; }

    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }

    public int getIntervalDays() { return intervalDays; }
    public void setIntervalDays(int intervalDays) { this.intervalDays = intervalDays; }

    public double getEaseFactor() { return easeFactor; }
    public void setEaseFactor(double easeFactor) { this.easeFactor = easeFactor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTimesCorrect() { return timesCorrect; }
    public void setTimesCorrect(int timesCorrect) { this.timesCorrect = timesCorrect; }

    public int getTimesWrong() { return timesWrong; }
    public void setTimesWrong(int timesWrong) { this.timesWrong = timesWrong; }

    public int getMasteryLevel() { return masteryLevel; }
    public void setMasteryLevel(int masteryLevel) { this.masteryLevel = masteryLevel; }

    public String getStatusLabel() {
        return switch (status != null ? status : "new") {
            case "new" -> "Nueva";
            case "learning" -> "Aprendiendo";
            case "review" -> "Revisión";
            case "mastered" -> "Dominada";
            default         -> "Nueva";
        };
    }
}
