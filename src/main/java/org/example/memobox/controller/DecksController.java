package org.example.memobox.controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.memobox.model.Card;
import org.example.memobox.model.Deck;
import org.example.memobox.service.CardService;
import org.example.memobox.service.DeckService;
import org.example.memobox.util.AlertUtil;
import org.example.memobox.util.SceneManager;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DecksController implements Initializable {

    @FXML private TableView<Deck> tableMazos;
    @FXML private TableColumn<Deck, String> colNombre;
    @FXML private TableColumn<Deck, String> colDescripcion;
    @FXML private TableColumn<Deck, Number> colTotal;
    @FXML private TableColumn<Deck, Number> colPendientes;
    @FXML private Label lblStatus;

    private final ObservableList<Deck> decks = FXCollections.observableArrayList();
    private final DeckService deckService = new DeckService();
    private final CardService cardService = new CardService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        loadDecks();
    }

    private void setupTable() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalCards"));
        colPendientes .setCellValueFactory(new PropertyValueFactory<>("dueCards"));
        tableMazos.setItems(decks);
        tableMazos.setPlaceholder(new Label("No hay mazos aún. ¡Crea tu primer mazo!"));
    }

    void loadDecks() {
        lblStatus.setText("Cargando mazos…");
        try {
            List<Deck> list = deckService.getAll();
            for (Deck d : list) {
                try {
                    d.setTotalCards(cardService.getByDeck(d.getId()).size());
                    d.setDueCards(cardService.getDueToday(d.getId()).size());
                } catch (Exception ignored) {}
            }
            decks.setAll(list);
            lblStatus.setText(list.size() + " mazo(s)");
        } catch (Exception e) {
            lblStatus.setText("Error al cargar");
            AlertUtil.error("Error", e.getMessage());
        }
    }


    @FXML private void onNuevoMazo() {
        try {
            FXMLLoader loader = SceneManager.navigateTo("deck-form-view.fxml");
            DeckFormController ctrl = loader.getController();
            ctrl.initCreate(this);
        } catch (Exception e) { AlertUtil.error("Error", e.getMessage()); }
    }

    @FXML private void onEditarMazo() {
        Deck sel = tableMazos.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.warning("Selección", "Selecciona un mazo para editar."); return; }
        try {
            FXMLLoader loader = SceneManager.navigateTo("deck-form-view.fxml");
            DeckFormController ctrl = loader.getController();
            ctrl.initEdit(sel, this);
        } catch (Exception e) { AlertUtil.error("Error", e.getMessage()); }
    }

    @FXML private void onBorrarMazo() {
        Deck sel = tableMazos.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.warning("Selección", "Selecciona un mazo para borrar."); return; }
        if (!AlertUtil.confirm("Borrar mazo",
                "¿Borrar el mazo \"" + sel.getName() + "\" y todas sus tarjetas?")) return;
        
        try {
            deckService.delete(sel.getId());
            decks.remove(sel);
            lblStatus.setText(decks.size() + " mazo(s)");
        } catch (Exception e) {
            AlertUtil.error("Error al borrar", e.getMessage());
        }
    }

    @FXML private void onVerTarjetas() {
        Deck sel = tableMazos.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.warning("Selección", "Selecciona un mazo."); return; }
        try {
            FXMLLoader loader = SceneManager.navigateTo("cards-view.fxml");
            CardsController ctrl = loader.getController();
            ctrl.initDeck(sel);
        } catch (Exception e) { AlertUtil.error("Error", e.getMessage()); }
    }

    @FXML private void onEstudiar() {
        Deck sel = tableMazos.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.warning("Selección", "Selecciona un mazo para estudiar."); return; }

        try {
            List<Card> due = cardService.getDueToday(sel.getId());
            if (due.isEmpty()) {
                AlertUtil.info("Sin pendientes", "No hay tarjetas pendientes en \"" + sel.getName() + "\". Vuelve manana.");
                return;
            }
            try {
                FXMLLoader loader = SceneManager.navigateTo("study-view.fxml");
                StudyController ctrl = loader.getController();
                ctrl.initStudy(sel, due);
            } catch (Exception e) {
                AlertUtil.error("Error al navegar", e.getMessage());
            }
        } catch (Exception e) {
            AlertUtil.error("Error al cargar tarjetas", e.getMessage());
        }
    }
}
