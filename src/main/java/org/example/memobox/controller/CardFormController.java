package org.example.memobox.controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.memobox.model.Card;
import org.example.memobox.model.Deck;
import org.example.memobox.service.CardService;
import org.example.memobox.util.AlertUtil;
import org.example.memobox.util.SceneManager;

import java.net.URL;
import java.util.ResourceBundle;

public class CardFormController implements Initializable {

    @FXML private Label lblTitulo;
    @FXML private TextField txtPregunta;
    @FXML private TextArea txtRespuesta;
    @FXML private TextArea txtEjemplo;
    @FXML private TextField txtEtiqueta;
    @FXML private Button btnGuardar;
    @FXML private Label lblError;

    private Card cardToEdit;
    private Deck deck;
    private CardsController caller;
    private final CardService service = new CardService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblError.setVisible(false);
    }

    public void initCreate(Deck deck, CardsController caller) {
        this.deck = deck;
        this.caller = caller;
        lblTitulo.setText("➕ Nueva tarjeta");
        btnGuardar.setText("Crear tarjeta");
    }

    public void initEdit(Card card, Deck deck, CardsController caller) {
        this.cardToEdit = card;
        this.deck = deck;
        this.caller = caller;
        lblTitulo.setText("✏️ Editar tarjeta");
        btnGuardar.setText("Guardar cambios");
        txtPregunta .setText(card.getQuestion());
        txtRespuesta.setText(card.getAnswer());
        txtEjemplo.setText(card.getExample()  != null ? card.getExample()  : "");
        txtEtiqueta .setText(card.getTag()       != null ? card.getTag()      : "");
    }

    @FXML private void onGuardar() {
        String pregunta = txtPregunta .getText().trim();
        String respuesta = txtRespuesta.getText().trim();
        String ejemplo = txtEjemplo.getText().trim();
        String etiqueta = txtEtiqueta .getText().trim();

        if (pregunta.isEmpty())  { showError("La pregunta es obligatoria.");  return; }
        if (respuesta.isEmpty()) { showError("La respuesta es obligatoria."); return; }
        lblError.setVisible(false);
        btnGuardar.setDisable(true);

        try {
            if (cardToEdit == null) {
                service.create(deck.getId(), pregunta, respuesta,
                        ejemplo.isEmpty() ? null : ejemplo,
                        etiqueta.isEmpty() ? null : etiqueta);
            } else {
                service.updateContent(cardToEdit.getId(), pregunta, respuesta,
                        ejemplo.isEmpty() ? null : ejemplo,
                        etiqueta.isEmpty() ? null : etiqueta);
            }
            if (caller != null) caller.loadCards();
            navigateBack();
        } catch (Exception e) {
            showError(e.getMessage());
            btnGuardar.setDisable(false);
        }
    }

    @FXML private void onCancelar() {
        navigateBack();
    }

    private void navigateBack() {
        try {
            if (deck != null) {
                javafx.fxml.FXMLLoader loader = SceneManager.navigateTo("cards-view.fxml");
                CardsController ctrl = loader.getController();
                ctrl.initDeck(deck);
            } else {
                SceneManager.navigateTo("decks-view.fxml");
            }
        } catch (Exception e) { AlertUtil.error("Error", e.getMessage()); }
    }

    private void showError(String msg) {
        lblError.setText("⚠ " + msg);
        lblError.setVisible(true);
    }
}
