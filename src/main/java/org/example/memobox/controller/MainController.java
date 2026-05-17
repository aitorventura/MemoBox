package org.example.memobox.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.example.memobox.util.AlertUtil;
import org.example.memobox.util.SceneManager;
import org.example.memobox.util.UserSession;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private StackPane contentPane;
    @FXML private Button btnDashboard;
    @FXML private Button btnDecks;
    @FXML private Button btnStats;
    @FXML private Label lblUsername;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SceneManager.setMainController(this);

        // Mostrar el nombre del usuario activo en el sidebar
        if (UserSession.isLoggedIn()) {
            lblUsername.setText("@" + UserSession.getCurrentUser().getUsername());
        }

        navigateSafe("dashboard-view.fxml");
    }

    // Navegacion central
    public void setContent(Node node) {
        contentPane.getChildren().setAll(node);
    }

    @FXML private void onDashboard() {
        setActive(btnDashboard);
        navigateSafe("dashboard-view.fxml");
    }

    @FXML private void onDecks() {
        setActive(btnDecks);
        navigateSafe("decks-view.fxml");
    }

    @FXML private void onStats() {
        setActive(btnStats);
        navigateSafe("stats-view.fxml");
    }

    @FXML private void onLogout() {
        UserSession.logout();
        try {
            SceneManager.loadLoginView();
        } catch (Exception e) {
            AlertUtil.error("Error", e.getMessage());
        }
    }

    private void navigateSafe(String fxml) {
        try {
            SceneManager.navigateTo(fxml);
        } catch (Exception e) {
            AlertUtil.error("Error", "No se pudo cargar: " + fxml);
        }
    }

    private void setActive(Button active) {
        btnDashboard.getStyleClass().remove("nav-active");
        btnDecks.getStyleClass().remove("nav-active");
        btnStats.getStyleClass().remove("nav-active");
        if (active != null) active.getStyleClass().add("nav-active");
    }
}
