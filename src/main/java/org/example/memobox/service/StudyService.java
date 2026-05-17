package org.example.memobox.service;

import org.example.memobox.model.Card;
import org.example.memobox.model.Deck;
import org.example.memobox.model.StudySession;
import org.example.memobox.repository.CardRepository;
import org.example.memobox.repository.StatsRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class StudyService {

    private final SpacedRepetitionService sm2 = new SpacedRepetitionService();

    private Deck deck;
    private List<Card> queue;
    private int correctCount;
    private int wrongCount;
    private long startTime;
    private StudySession session;


    public void startSession(UUID deckId) {
        correctCount = 0;
        wrongCount = 0;
        startTime = System.currentTimeMillis();

        session = new StudySession();
        session.setDeckId(deckId);
        session.setSessionDate(LocalDate.now());
    }


    public Card recordReview(Card card, int quality) {
        sm2.applyReview(card, quality);

        if (quality >= 3) correctCount++;
        else              wrongCount++;

        try {
            CardRepository.actualizarTrasRevision(card);
        } catch (Exception e) {
            System.err.println("⚠ Error al guardar revisión: " + e.getMessage());
        }

        return card;
    }


    public void finishSession() {
        int reviewed = correctCount + wrongCount;
        int durationSecs = (int) ((System.currentTimeMillis() - startTime) / 1000);

        session.setReviewedCount(reviewed);
        session.setCorrectCount(correctCount);
        session.setWrongCount(wrongCount);
        session.setDurationSeconds(durationSecs);

        try {
            StatsRepository.guardarSesion(session);
            new StatsService().recordStudySession(reviewed, correctCount, wrongCount);
        } catch (Exception e) {
            System.err.println("⚠ Error al guardar sesión: " + e.getMessage());
        }
    }


    public int getCorrectCount()  { return correctCount; }
    public int getWrongCount()    { return wrongCount;   }
    public int getReviewedCount() { return correctCount + wrongCount; }
}
