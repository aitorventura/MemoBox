package org.example.memobox.controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.example.memobox.model.Deck;
import org.example.memobox.model.UserStats;
import org.example.memobox.service.CardService;
import org.example.memobox.service.DeckService;
import org.example.memobox.service.StatsService;
import org.example.memobox.util.AlertUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StatsController implements Initializable {

    @FXML private Label lblRachaActual;
    @FXML private Label lblRachaMejor;
    @FXML private Label lblEstudiadasHoy;
    @FXML private Label lblPendientesHoy;
    @FXML private Label lblTotalTarjetas;
    @FXML private Label lblDominadas;
    @FXML private Label lblPrecision;
    @FXML private Label lblTotalRevisiones;
    @FXML private Label lblCorrectas;
    @FXML private Label lblIncorrectas;
    @FXML private ProgressBar progressDominio;
    @FXML private Label lblProgressPct;

    private final StatsService statsService = new StatsService();
    private final CardService cardService = new CardService();
    private final DeckService deckService = new DeckService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadStats();
    }

    private void loadStats() {
        try {
            UserStats stats = statsService.getStats();
            int due = cardService.getAllDueToday().size();
            int mastered = cardService.countMastered();
            List<Deck> decks = deckService.getAll();
            int totalCards = 0;
            for (Deck d : decks) {
                totalCards += cardService.getByDeck(d.getId()).size();
            }
            int total = totalCards;
            double prog = total > 0 ? (double) mastered / total : 0;

            lblRachaActual.setText(stats.getCurrentStreak() + " 🔥");
            lblRachaMejor.setText(String.valueOf(stats.getBestStreak()));
            lblEstudiadasHoy.setText(String.valueOf(stats.getStudiedToday()));
            lblPendientesHoy.setText(String.valueOf(due));
            lblTotalTarjetas.setText(String.valueOf(total));
            lblDominadas.setText(String.valueOf(mastered));
            lblPrecision.setText(String.format("%.1f%%", stats.getAccuracyPercent()));
            lblTotalRevisiones.setText(String.valueOf(stats.getTotalReviews()));
            lblCorrectas.setText(String.valueOf(stats.getTotalCorrect()));
            lblIncorrectas.setText(String.valueOf(stats.getTotalWrong()));
            progressDominio.setProgress(prog);
            lblProgressPct.setText(String.format("%.0f%% dominado", prog * 100));

        } catch (Exception e) {
            AlertUtil.error("Error de estadísticas", e.getMessage());
        }
    }

    @FXML private void onRefresh() {
        loadStats();
    }
}
