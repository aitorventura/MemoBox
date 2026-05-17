package org.example.memobox.model;

import java.time.LocalDate;
import java.util.UUID;

public class StudySession {

    private UUID id;
    private UUID userId;
    private UUID deckId;
    private LocalDate sessionDate;
    private int reviewedCount;
    private int correctCount;
    private int wrongCount;
    private int durationSeconds;

    public StudySession() {
        this.sessionDate = LocalDate.now();
    }

    public UUID getId()              { return id; }
    public void setId(UUID id)       { this.id = id; }

    public UUID getUserId()              { return userId; }
    public void setUserId(UUID userId)   { this.userId = userId; }

    public UUID getDeckId()              { return deckId; }
    public void setDeckId(UUID deckId)   { this.deckId = deckId; }

    public LocalDate getSessionDate()                  { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate)  { this.sessionDate = sessionDate; }

    public int getReviewedCount()                      { return reviewedCount; }
    public void setReviewedCount(int reviewedCount)    { this.reviewedCount = reviewedCount; }

    public int getCorrectCount()                       { return correctCount; }
    public void setCorrectCount(int correctCount)      { this.correctCount = correctCount; }

    public int getWrongCount()                         { return wrongCount; }
    public void setWrongCount(int wrongCount)          { this.wrongCount = wrongCount; }

    public int getDurationSeconds()                    { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds){ this.durationSeconds = durationSeconds; }
}
