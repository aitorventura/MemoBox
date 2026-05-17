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
import org.example.memobox.util.AlertUtil;
import org.example.memobox.util.DateUtil;
import org.example.memobox.util.SceneManager;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class CardsController implements Initializable {

    @FXML private Label lblDeckName;
    @FXML private Label lblStatus;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cboFiltro;
    @FXML private TableView<Card> tableCards;
    @FXML private TableColumn<Card, String> colPregunta;
    @FXML private TableColumn<Card, String> colEtiqueta;
    @FXML private TableColumn<Card, String> colEstado;
    @FXML private TableColumn<Card, String> colProxima;
    @FXML private TableColumn<Card, Number> colDominio;

    private Deck deck;
    private List<Card> allCards;
    private final ObservableList<Card> displayed = FXCollections.observableArrayList();
    private final CardService cardService = new CardService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupFiltro();
    }

    /** Inyecta el mazo y carga sus tarjetas. Llamado desde DecksController. */
    public void initDeck(Deck deck) {
        this.deck = deck;
        lblDeckName.setText("📂 " + deck.getName());
        loadCards();
    }

    private void setupTable() {
        colPregunta.setCellValueFactory(new PropertyValueFactory<>("question"));
        colEtiqueta.setCellValueFactory(new PropertyValueFactory<>("tag"));
        colEstado.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getStatusLabel()));
        colProxima .setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        DateUtil.relativeDate(c.getValue().getNextReviewAt())));
        colDominio .setCellValueFactory(new PropertyValueFactory<>("masteryLevel"));
        tableCards.setItems(displayed);
        tableCards.setPlaceholder(new Label("No hay tarjetas. ¡Crea la primera!"));
    }

    private void setupFiltro() {
        cboFiltro.setItems(FXCollections.observableArrayList(
                "Todas", "Nuevas", "Aprendiendo", "Revisión", "Dominadas"));
        cboFiltro.setValue("Todas");
        cboFiltro.setOnAction(e -> applyFilter());
        txtBuscar.textProperty().addListener((obs, o, n) -> applyFilter());
    }

    void loadCards() {
        lblStatus.setText("Cargando…");
        try {
            List<Card> cards = cardService.getByDeck(deck.getId());
            allCards = cards;
            applyFilter();
            lblStatus.setText(cards.size() + " tarjeta(s)");
        } catch (Exception e) {
            AlertUtil.error("Error", e.getMessage());
        }
    }

    private void applyFilter() {
        if (allCards == null) return;
        String filtro = cboFiltro.getValue();
        String buscar = txtBuscar.getText().toLowerCase().trim();

        List<Card> filtered = new java.util.ArrayList<>();
        for (Card c : allCards) {
            if (matchFiltro(c, filtro)) {
                if (buscar.isEmpty() || 
                    c.getQuestion().toLowerCase().contains(buscar) || 
                    (c.getTag() != null && c.getTag().toLowerCase().contains(buscar))) {
                    filtered.add(c);
                }
            }
        }
        displayed.setAll(filtered);
    }

    private boolean matchFiltro(Card c, String f) {
        return switch (f) {
            case "Nuevas" -> "new".equals(c.getStatus());
            case "Aprendiendo" -> "learning".equals(c.getStatus());
            case "Revisión" -> "review".equals(c.getStatus());
            case "Dominadas" -> "mastered".equals(c.getStatus());
            default            -> true;
        };
    }


    @FXML private void onNuevaTarjeta() {
        try {
            FXMLLoader loader = SceneManager.navigateTo("card-form-view.fxml");
            CardFormController ctrl = loader.getController();
            ctrl.initCreate(deck, this);
        } catch (Exception e) { AlertUtil.error("Error", e.getMessage()); }
    }

    @FXML private void onEditar() {
        Card sel = tableCards.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.warning("Selección", "Selecciona una tarjeta para editar."); return; }
        try {
            FXMLLoader loader = SceneManager.navigateTo("card-form-view.fxml");
            CardFormController ctrl = loader.getController();
            ctrl.initEdit(sel, deck, this);
        } catch (Exception e) { AlertUtil.error("Error", e.getMessage()); }
    }

    @FXML private void onBorrar() {
        Card sel = tableCards.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertUtil.warning("Selección", "Selecciona una tarjeta para borrar."); return; }
        if (!AlertUtil.confirm("Borrar tarjeta", "¿Borrar esta tarjeta permanentemente?")) return;
        
        try {
            cardService.delete(sel.getId());
            allCards.remove(sel);
            applyFilter();
        } catch (Exception e) {
            AlertUtil.error("Error", e.getMessage());
        }
    }

    @FXML private void onEstudiar() {
        if (deck == null) return;
        try {
            List<Card> due = cardService.getDueToday(deck.getId());
            if (due.isEmpty()) {
                AlertUtil.info("Sin pendientes", "No hay tarjetas pendientes en este mazo.");
                return;
            }
            try {
                FXMLLoader loader = SceneManager.navigateTo("study-view.fxml");
                StudyController ctrl = loader.getController();
                ctrl.initStudy(deck, due);
            } catch (Exception e) {
                AlertUtil.error("Error al navegar", e.getMessage());
            }
        } catch (Exception e) {
            AlertUtil.error("Error al cargar tarjetas", e.getMessage());
        }
    }

    @FXML private void onVolver() {
        try { SceneManager.navigateTo("decks-view.fxml"); }
        catch (Exception e) { AlertUtil.error("Error", e.getMessage()); }
    }
}
