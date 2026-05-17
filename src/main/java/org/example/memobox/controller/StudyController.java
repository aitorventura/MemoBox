package org.example.memobox.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.memobox.model.Card;
import org.example.memobox.model.Deck;
import org.example.memobox.service.SpacedRepetitionService;
import org.example.memobox.service.StudyService;
import org.example.memobox.util.AlertUtil;
import org.example.memobox.util.SceneManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class StudyController implements Initializable {

    @FXML private Label lblDeckName;
    @FXML private Label lblProgress;      // "3 / 10"
    @FXML private ProgressBar progressBar;

    @FXML private Label lblQuestion;
    @FXML private VBox answerPane;       // oculto hasta revelar
    @FXML private Label lblAnswer;
    @FXML private Label lblExample;

    @FXML private Button btnReveal;
    @FXML private HBox ratingPanel;     // HBox en el FXML (contiene los 4 botones de calificación)

    @FXML private VBox finishedPane;
    @FXML private Label lblFinResult;
    @FXML private VBox studyPane;        // oculto al finalizar

    private List<Card> queue = new ArrayList<>();
    private int index = 0;
    private int total = 0;
    private final StudyService service = new StudyService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    /**
     * Punto de entrada: inyecta el mazo y la lista de tarjetas pendientes.
     * @param deck  mazo (puede ser null para sesión global)
     * @param cards tarjetas a estudiar en orden
     */
    public void initStudy(Deck deck, List<Card> cards) {
        lblDeckName.setText(deck != null ? "📖 " + deck.getName() : "📖 Revisión global");
        this.queue = new ArrayList<>(cards);
        this.total = cards.size();
        this.index = 0;
        service.startSession(deck != null ? deck.getId() : null);

        if (finishedPane != null) finishedPane.setVisible(false);
        if (studyPane    != null) studyPane.setVisible(true);

        showNextCard();
    }


    private void showNextCard() {
        if (index >= queue.size()) {
            showFinished();
            return;
        }
        Card card = queue.get(index);
        // Labels
        lblQuestion.setText(card.getQuestion());
        lblAnswer.setText(card.getAnswer());
        lblExample .setText(card.getExample() != null && !card.getExample().isBlank()
                ? "📝 " + card.getExample() : "");
        // Resetear visibilidad
        answerPane .setVisible(false);
        answerPane .setManaged(false);
        ratingPanel.setVisible(false);
        ratingPanel.setManaged(false);
        btnReveal.setVisible(true);
        btnReveal.setManaged(true);
        // Progreso
        lblProgress.setText((index + 1) + " / " + total);
        progressBar.setProgress((double) index / total);
    }

    @FXML private void onReveal() {
        answerPane .setVisible(true);
        answerPane .setManaged(true);
        ratingPanel.setVisible(true);
        ratingPanel.setManaged(true);
        btnReveal.setVisible(false);
        btnReveal.setManaged(false);
    }


    @FXML private void onAgain() { rate(SpacedRepetitionService.QUALITY_AGAIN); }
    @FXML private void onHard()  { rate(SpacedRepetitionService.QUALITY_HARD);  }
    @FXML private void onGood()  { rate(SpacedRepetitionService.QUALITY_GOOD);  }
    @FXML private void onEasy()  { rate(SpacedRepetitionService.QUALITY_EASY);  }

    private void rate(int quality) {
        Card card = queue.get(index);
        // "Otra vez" → añadir al final de la cola para rervisar en la misma sesión
        if (quality == SpacedRepetitionService.QUALITY_AGAIN) {
            queue.add(card);  // se revisará de nuevo al final
        }
        service.recordReview(card, quality);
        index++;
        showNextCard();
    }


    private void showFinished() {
        service.finishSession();

        // Ocultar zona de estudio (visible + managed = false para liberar espacio en StackPane)
        if (studyPane != null) {
            studyPane.setVisible(false);
            studyPane.setManaged(false);
        }

        // Mostrar pantalla de resumen (managed = true para que ocupe el espacio completo)
        if (finishedPane != null) {
            finishedPane.setManaged(true);
            finishedPane.setVisible(true);
            String resumen = String.format(
                    "Revisadas: %d\nCorrectas: %d   Incorrectas: %d",
                    service.getReviewedCount(),
                    service.getCorrectCount(),
                    service.getWrongCount());
            if (lblFinResult != null) lblFinResult.setText(resumen);
        }
    }

    @FXML private void onVolver() {
        try { SceneManager.navigateTo("decks-view.fxml"); }
        catch (Exception e) { AlertUtil.error("Error", e.getMessage()); }
    }

    @FXML private void onVolverDashboard() {
        try { SceneManager.navigateTo("dashboard-view.fxml"); }
        catch (Exception e) { AlertUtil.error("Error", e.getMessage()); }
    }
}
