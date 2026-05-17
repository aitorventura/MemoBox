package org.example.memobox.controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.memobox.model.Deck;
import org.example.memobox.service.DeckService;
import org.example.memobox.util.AlertUtil;
import org.example.memobox.util.SceneManager;

import java.net.URL;
import java.util.ResourceBundle;

public class DeckFormController implements Initializable {

    @FXML private Label lblTitulo;
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnGuardar;
    @FXML private Label lblError;

    private Deck deckToEdit;
    private DecksController caller;
    private final DeckService service = new DeckService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblError.setVisible(false);
    }

    /** Modo creación: sin mazo previo. */
    public void initCreate(DecksController caller) {
        this.caller = caller;
        lblTitulo.setText("➕ Nuevo mazo");
        btnGuardar.setText("Crear mazo");
    }

    /** Modo edición: precarga los datos del mazo existente. */
    public void initEdit(Deck deck, DecksController caller) {
        this.deckToEdit = deck;
        this.caller = caller;
        lblTitulo.setText("✏️ Editar mazo");
        btnGuardar.setText("Guardar cambios");
        txtNombre.setText(deck.getName());
        txtDescripcion.setText(deck.getDescription() != null ? deck.getDescription() : "");
    }

    @FXML private void onGuardar() {
        String nombre = txtNombre.getText().trim();
        String desc = txtDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            showError("El nombre del mazo es obligatorio.");
            return;
        }
        lblError.setVisible(false);
        btnGuardar.setDisable(true);

        try {
            if (deckToEdit == null) {
                service.create(nombre, desc);
            } else {
                service.update(deckToEdit.getId(), nombre, desc);
            }
            if (caller != null) caller.loadDecks();
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
        try { SceneManager.navigateTo("decks-view.fxml"); }
        catch (Exception e) { AlertUtil.error("Error", e.getMessage()); }
    }

    private void showError(String msg) {
        lblError.setText("⚠ " + msg);
        lblError.setVisible(true);
    }
}
