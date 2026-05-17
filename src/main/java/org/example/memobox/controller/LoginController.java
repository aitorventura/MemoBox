package org.example.memobox.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.memobox.model.User;
import org.example.memobox.repository.UserRepository;
import org.example.memobox.util.AlertUtil;
import org.example.memobox.util.SceneManager;
import org.example.memobox.util.UserSession;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private Label lblTitle;
    @FXML private Label lblError;

    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirm;

    @FXML private VBox emailBox;
    @FXML private VBox confirmBox;

    @FXML private Button btnAction;
    @FXML private Button btnToggle;
    @FXML private Label lblToggleHint;

    private boolean modoRegistro = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }


    @FXML
    private void onAction() {
        ocultarError();
        if (modoRegistro) {
            ejecutarRegistro();
        } else {
            ejecutarLogin();
        }
    }


    @FXML
    private void onToggle() {
        modoRegistro = !modoRegistro;
        ocultarError();
        limpiarCampos();

        if (modoRegistro) {
            lblTitle.setText("Crear cuenta");
            btnAction.setText("Registrarse");
            lblToggleHint.setText("¿Ya tienes cuenta?");
            btnToggle.setText("Iniciar sesión");
            mostrarCampo(emailBox,   true);
            mostrarCampo(confirmBox, true);
        } else {
            lblTitle.setText("Iniciar sesión");
            btnAction.setText("Iniciar sesión");
            lblToggleHint.setText("¿No tienes cuenta?");
            btnToggle.setText("Crear cuenta");
            mostrarCampo(emailBox,   false);
            mostrarCampo(confirmBox, false);
        }
    }


    private void ejecutarLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarError("Introduce usuario y contraseña.");
            return;
        }

        try {
            User user = UserRepository.login(username, password);
            if (user != null) {
                UserSession.login(user);
                SceneManager.loadMainView();
            } else {
                mostrarError("Usuario o contraseña incorrectos.");
            }
        } catch (Exception e) {
            mostrarError("Error al conectar: " + e.getMessage());
        }
    }



    private void ejecutarRegistro() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String confirm = txtConfirm.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return;
        }
        if (username.length() < 3) {
            mostrarError("El usuario debe tener al menos 3 caracteres.");
            return;
        }
        if (!password.equals(confirm)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }
        if (password.length() < 6) {
            mostrarError("La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        try {
            User user = UserRepository.register(username, email, password);
            if (user != null) {
                UserSession.login(user);
                SceneManager.loadMainView();
            } else {
                mostrarError("No se pudo crear la cuenta. Inténtalo de nuevo.");
            }
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("unique")) {
                mostrarError("Ese usuario o email ya está en uso.");
            } else {
                mostrarError("Error al registrar: " + e.getMessage());
            }
        } catch (Exception e) {
            mostrarError("Error al conectar: " + e.getMessage());
        }
    }


    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void ocultarError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
    }

    private void mostrarCampo(VBox box, boolean visible) {
        box.setVisible(visible);
        box.setManaged(visible);
    }

    private void limpiarCampos() {
        txtUsername.clear();
        txtEmail.clear();
        txtPassword.clear();
        txtConfirm.clear();
    }
}
