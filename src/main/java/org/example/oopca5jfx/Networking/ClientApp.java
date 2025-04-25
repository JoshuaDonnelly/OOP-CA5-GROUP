package org.example.oopca5jfx.Networking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/oopca5jfx/Networking/ClientApp.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/org/example/oopca5jfx/styling.css").toExternalForm());
        primaryStage.setTitle("Music Ecommerce Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
