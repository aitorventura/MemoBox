package org.example.memobox.service;

import org.example.memobox.model.Card;

import java.time.LocalDateTime;

public class SpacedRepetitionService {

    public static final int QUALITY_AGAIN = 0;
    public static final int QUALITY_HARD = 2;
    public static final int QUALITY_GOOD = 3;
    public static final int QUALITY_EASY = 4;

    // Logica SM-2 para actualizar la tarjeta
    public Card applyReview(Card card, int quality) {
        double ef = card.getEaseFactor();
        int    reps = card.getRepetitions();
        int    interval = card.getIntervalDays();

        if (quality < 3) {
            reps = 0;
            interval = 1;
            card.setTimesWrong(card.getTimesWrong() + 1);
        } else {
            if (reps == 0) {
                interval = 1;
            } else if (reps == 1) {
                interval = 6;
            } else {
                interval = (int) Math.round(interval * ef);
            }
            reps++;
            card.setTimesCorrect(card.getTimesCorrect() + 1);
        }

        ef = ef + 0.1 - (4 - quality) * (0.08 + (4 - quality) * 0.02);
        if (ef < 1.3) ef = 1.3;

        String status;
        int mastery;
        if (reps == 0) {
            status = "new";
            mastery = 0;
        } else if (reps <= 2) {
            status = "learning";
            mastery = reps;
        } else if (reps <= 5) {
            status = "review";
            mastery = Math.min(4, reps);
        } else {
            status = "mastered";
            mastery = 5;
        }

        card.setRepetitions(reps);
        card.setIntervalDays(interval);
        card.setEaseFactor(ef);
        card.setStatus(status);
        card.setMasteryLevel(mastery);
        card.setLastReviewedAt(LocalDateTime.now());
        card.setNextReviewAt(LocalDateTime.now().plusDays(interval));

        return card;
    }
}
