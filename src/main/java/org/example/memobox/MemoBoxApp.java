package org.example.memobox;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.memobox.util.AlertUtil;
import org.example.memobox.util.SceneManager;

public class MemoBoxApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            SceneManager.init(stage);
            SceneManager.loadLoginView();
            stage.show();
        } catch (Exception e) {
            AlertUtil.error("Error al iniciar", e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
