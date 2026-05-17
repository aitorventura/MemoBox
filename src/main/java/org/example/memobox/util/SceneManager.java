package org.example.memobox.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.memobox.controller.MainController;

import java.io.IOException;
import java.net.URL;

public class SceneManager {

    private static Stage primaryStage;
    private static MainController mainController;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void loadLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader(resource("login-view.fxml"));
        Parent root = loader.load();

        Scene scene = buildScene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("MemoBox — Iniciar sesión");
    }

    public static void loadMainView() throws IOException {
        FXMLLoader loader = new FXMLLoader(resource("main-view.fxml"));
        Parent root = loader.load();
        mainController = loader.getController();

        Scene scene = buildScene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("MemoBox — Aprende con tarjetas");
    }

    // Carga vistas parciales en el centro
    public static FXMLLoader navigateTo(String fxmlName) throws IOException {
        FXMLLoader loader = new FXMLLoader(resource(fxmlName));
        Node content = loader.load();
        mainController.setContent(content);
        return loader;
    }


    public static void setMainController(MainController ctrl) {
        mainController = ctrl;
    }

    public static Stage getStage() {
        return primaryStage;
    }

    private static Scene buildScene(Parent root) {
        Scene scene = new Scene(root, 1100, 680);
        URL css = SceneManager.class.getResource("/org/example/memobox/css/styles.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        return scene;
    }

    private static URL resource(String fxmlName) {
        return SceneManager.class.getResource("/org/example/memobox/fxml/" + fxmlName);
    }
}
