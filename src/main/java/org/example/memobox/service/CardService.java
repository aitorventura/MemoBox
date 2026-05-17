package org.example.memobox.service;

import org.example.memobox.model.Card;
import org.example.memobox.repository.CardRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CardService {

    public List<Card> getByDeck(UUID deckId) throws Exception {
        return CardRepository.getByDeck(deckId);
    }

    public List<Card> getDueToday(UUID deckId) throws Exception {
        return CardRepository.getDueToday(deckId);
    }

    public List<Card> getAllDueToday() throws Exception {
        return CardRepository.getAllDueToday();
    }

    public int countMastered() throws Exception {
        return CardRepository.countMastered();
    }

    public Card create(UUID deckId, String question, String answer,
                       String example, String tag) throws Exception {
        if (question == null || question.isBlank())
            throw new IllegalArgumentException("La pregunta es obligatoria.");
        if (answer == null || answer.isBlank())
            throw new IllegalArgumentException("La respuesta es obligatoria.");

        Card card = new Card();
        card.setDeckId(deckId);
        card.setQuestion(question.trim());
        card.setAnswer(answer.trim());
        card.setExample(example != null && !example.isBlank() ? example.trim() : "");
        card.setTag(tag != null && !tag.isBlank() ? tag.trim() : "");
        card.setNextReviewAt(LocalDateTime.now());   // disponible de inmediato
        return CardRepository.insertar(card);
    }

    public void updateContent(UUID id, String question, String answer,
                              String example, String tag) throws Exception {
        CardRepository.actualizarContenido(id, question, answer, example, tag);
    }

    public void updateAfterReview(Card card) throws Exception {
        CardRepository.actualizarTrasRevision(card);
    }

    public void delete(UUID id) throws Exception {
        CardRepository.borrar(id);
    }
}
