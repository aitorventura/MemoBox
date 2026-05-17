package org.example.memobox.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.example.memobox.model.Card;
import org.example.memobox.model.Deck;
import org.example.memobox.model.UserStats;
import org.example.memobox.service.CardService;
import org.example.memobox.service.DeckService;
import org.example.memobox.service.StatsService;
import org.example.memobox.util.AlertUtil;
import org.example.memobox.util.SceneManager;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label lblStreakCurrent;
    @FXML private Label lblStreakBest;
    @FXML private Label lblDueToday;
    @FXML private Label lblTotalDecks;
    @FXML private Label lblMastered;
    @FXML private Label lblStudiedToday;
    @FXML private Label lblAccuracy;
    @FXML private ProgressBar progressMastery;

    private final DeckService deckService = new DeckService();
    private final CardService cardService = new CardService();
    private final StatsService statsService = new StatsService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadData();
    }

    private void loadData() {
        try {
            UserStats stats = statsService.getStats();
            List<Deck> decks = deckService.getAll();
            List<Card> due = cardService.getAllDueToday();
            int mastered = cardService.countMastered();

            // Calcular progreso global (tarjetas dominadas / total)
            int totalCards = 0;
            for (Deck d : decks) {
                List<Card> cards = cardService.getByDeck(d.getId());
                d.setTotalCards(cards.size());
                totalCards += cards.size();
            }
            int total = totalCards;

            lblStreakCurrent.setText(String.valueOf(stats.getCurrentStreak()));
            lblStreakBest.setText(String.valueOf(stats.getBestStreak()));
            lblDueToday.setText(String.valueOf(due.size()));
            lblTotalDecks.setText(String.valueOf(decks.size()));
            lblMastered.setText(String.valueOf(mastered));
            lblStudiedToday.setText(String.valueOf(stats.getStudiedToday()));
            lblAccuracy.setText(String.format("%.0f%%", stats.getAccuracyPercent()));
            double prog = total > 0 ? (double) mastered / total : 0;
            progressMastery.setProgress(prog);

        } catch (Exception e) {
            AlertUtil.error("Error de conexión",
                    "No se pudieron cargar los datos.\n" + e.getMessage());
        }
    }


    @FXML private void onGoDecks() {
        try { SceneManager.navigateTo("decks-view.fxml"); } catch (Exception e) { alertNav(e); }
    }

    @FXML private void onGoStats() {
        try { SceneManager.navigateTo("stats-view.fxml"); } catch (Exception e) { alertNav(e); }
    }

    @FXML private void onStudyAll() {
        try {
            List<Card> due = cardService.getAllDueToday();
            if (due.isEmpty()) {
                AlertUtil.info("Sin pendientes", "No tienes tarjetas pendientes hoy.");
                return;
            }
            try {
                FXMLLoader loader = SceneManager.navigateTo("study-view.fxml");
                StudyController ctrl = loader.getController();
                ctrl.initStudy(null, due);
            } catch (Exception e) {
                alertNav(e);
            }
        } catch (Exception e) {
            alertNav(e);
        }
    }

    private void alertNav(Exception e) {
        e.printStackTrace(); // ver causa completa en consola de IntelliJ
        // LoadException.getMessage() devuelve el path del FXML, no el error real.
        // Buscamos la causa raíz para mostrar algo útil al usuario.
        Throwable causa = e;
        while (causa.getCause() != null) causa = causa.getCause();
        String msg = causa.getMessage() != null ? causa.getMessage() : causa.getClass().getSimpleName();
        AlertUtil.error("Error de navegación", msg);
    }
}
