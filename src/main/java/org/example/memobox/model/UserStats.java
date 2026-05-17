package org.example.memobox.model;

import java.time.LocalDate;
import java.util.UUID;

public class UserStats {

    private UUID userId;
    private int currentStreak;
    private int bestStreak;
    private LocalDate lastStudyDate;
    private int studiedToday;
    private int totalReviews;
    private int totalCorrect;
    private int totalWrong;

    public UserStats() {}

    public UUID getUserId()              { return userId; }
    public void setUserId(UUID userId)   { this.userId = userId; }

    public int getCurrentStreak()                      { return currentStreak; }
    public void setCurrentStreak(int currentStreak)    { this.currentStreak = currentStreak; }

    public int getBestStreak()                         { return bestStreak; }
    public void setBestStreak(int bestStreak)           { this.bestStreak = bestStreak; }

    public LocalDate getLastStudyDate()                { return lastStudyDate; }
    public void setLastStudyDate(LocalDate d)          { this.lastStudyDate = d; }

    public int getStudiedToday()                       { return studiedToday; }
    public void setStudiedToday(int studiedToday)      { this.studiedToday = studiedToday; }

    public int getTotalReviews()                       { return totalReviews; }
    public void setTotalReviews(int totalReviews)      { this.totalReviews = totalReviews; }

    public int getTotalCorrect()                       { return totalCorrect; }
    public void setTotalCorrect(int totalCorrect)      { this.totalCorrect = totalCorrect; }

    public int getTotalWrong()                         { return totalWrong; }
    public void setTotalWrong(int totalWrong)          { this.totalWrong = totalWrong; }

    public double getAccuracyPercent() {
        if (totalReviews == 0) return 0.0;
        return (double) totalCorrect / totalReviews * 100.0;
    }
}
