package org.example.oopca5jfx.Networking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.BufferedReader;

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

        // Receive the response from the server
        BufferedReader socketReader = null;
        String response = socketReader.readLine();
        JSONObject jsonResponse = new JSONObject(response);

        if ("success".equals(jsonResponse.getString("status"))) {
            // Display the new product details with the auto-generated ID
            int id = jsonResponse.getInt("id");
            String name = jsonResponse.getString("name");
            float price = jsonResponse.getFloat("price");
            String description = jsonResponse.getString("description");
            int categoryId = jsonResponse.getInt("categoryId");
            int stock = jsonResponse.getInt("stock");
            String imageFilename = jsonResponse.getString("imageFilename");

            // Display the product information
            System.out.println("Product added successfully! ID: " + id);
            System.out.println("Name: " + name);
            System.out.println("Price: " + price);
            System.out.println("Description: " + description);
            System.out.println("Category ID: " + categoryId);
            System.out.println("Stock: " + stock);
            System.out.println("Image Filename: " + imageFilename);
        } else {
            String errorMessage = jsonResponse.getString("message");
            System.out.println("Error: " + errorMessage);

        }

    }
}
