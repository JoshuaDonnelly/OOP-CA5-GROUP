package org.example.oopca5jfx.Networking;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientApp extends Application {
    //TODO: add styling AND incorporate the rest of the methods into the gui
    final static int SERVER_PORT_NUMBER = 49000;
    private PrintWriter socketWriter;
    private BufferedReader socketReader;
    private TextArea outputArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        outputArea = new TextArea();
        outputArea.setEditable(false);

        Button connectButton = new Button("Connect to Server");
        connectButton.setOnAction(e -> connectToServer());

        Button viewProductsButton = new Button("View All Products");
        viewProductsButton.setOnAction(e -> sendCommand("view"));

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> exitClient());

        root.getChildren().addAll(connectButton, viewProductsButton, exitButton, outputArea);

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(String.valueOf(getClass().getResource("/styling.css")));

        primaryStage.setTitle("Music Ecommerce Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", SERVER_PORT_NUMBER);
            socketWriter = new PrintWriter(socket.getOutputStream(), true);
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputArea.appendText("Connected to server.\n");
        } catch (IOException e) {
            outputArea.appendText("Connection error: " + e.getMessage() + "\n");
        }
    }

    private void sendCommand(String command) {
        if (socketWriter != null && socketReader != null) {
            socketWriter.println(command);
            try {
                String response = socketReader.readLine();
                outputArea.appendText("Server response: \n" + response + "\n");
            } catch (IOException e) {
                outputArea.appendText("Read error: " + e.getMessage() + "\n");
            }
        } else {
            outputArea.appendText("Not connected to server.\n");
        }
    }

    private void exitClient() {
        if (socketWriter != null) {
            socketWriter.println("exit");
        }
        outputArea.appendText("ClientApp exiting.\n");
        System.exit(0);
    }
}
