package org.example.oopca5jfx.Networking;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;

public class ClientController {
    private static final int SERVER_PORT_NUMBER = 49000;
    private PrintWriter socketWriter;
    private BufferedReader socketReader;

    @FXML
    private TextArea outputArea;

    @FXML
    private TextField productIdToFind;

    //For deleting a product
    @FXML
    private TextField productIdField1;

    //inserting new product
    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField categoryIdField;
    @FXML
    private TextField stockField;

    //updating a product
    @FXML
    private TextField nameFieldUpdate;
    @FXML
    private TextField idFieldUpdate;
    @FXML
    private TextField priceFieldUpdate;
    @FXML
    private TextArea descriptionFieldUpdate;
    @FXML
    private TextField catIdFieldUpdate;
    @FXML
    private TextField stockFieldUpdate;
    //Keyword
    @FXML
    private TextField keywordField;
    @FXML
    private TextField idJsonField;
    @FXML
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

    @FXML
    private void viewProducts() {
        sendCommand("view");
    }

    @FXML
    private void findProductById() {
        String productId = productIdToFind.getText();
        if (!productId.isEmpty()) {
            sendCommand("find " + productId);
        } else {
            outputArea.appendText("Please enter a product ID\n");
        }
    }

    @FXML
    private void deleteProductById() {
        String productId = productIdField1.getText();
        if (!productId.isEmpty()) {
            sendCommand("delete " + productId);
        } else {
            outputArea.appendText("Please enter a product ID\n");
        }
    }

    @FXML
    private void handleInsert() {
        try {
            String name = nameField.getText().trim();
            float price = Float.parseFloat(priceField.getText());
            String description = descriptionField.getText().trim();
            int categoryId = Integer.parseInt(categoryIdField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());

            String productString = String.format("insert %s %f %s %d %d", name, price, description, categoryId, stock);

            socketWriter.println(productString);

            String response = socketReader.readLine();
            System.out.println("Response from server: " + response);

            // Show confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Insert Product");
            alert.setHeaderText("Server Response");
            alert.setContentText(response);
            alert.showAndWait();
        } catch (NumberFormatException e) {
            showError("Invalid input. Please make sure price, category ID and stock are numbers.");
        } catch (IOException e) {
            showError("Error communicating with server.");
        }
    }
    @FXML
    private void handleUpdate() {
        try {
            String name = nameFieldUpdate.getText().trim();
            int id = Integer.parseInt(idFieldUpdate.getText());
            float price = Float.parseFloat(priceFieldUpdate.getText());
            String description = descriptionFieldUpdate.getText().trim();
            int categoryId = Integer.parseInt(catIdFieldUpdate.getText());
            int stock = Integer.parseInt(stockFieldUpdate.getText());

            String updateCommand = String.format("update %d|%s|%f|%s|%d|%d",
                    id, name, price, description, categoryId, stock);
            socketWriter.println(updateCommand);

            String response = socketReader.readLine();
            System.out.println("Response from server: " + response);


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Update Product");
            alert.setHeaderText("Server Response");
            alert.setContentText(response);
            alert.showAndWait();
        } catch (NumberFormatException e) {
            showError("Invalid input. Please make sure price, category ID and stock are numbers.");
        } catch (IOException e) {
            showError("Error communicating with server.");
        }
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText("Problem with product input");
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleKeywordSearch() {
        String keyword = keywordField.getText();
        if (!keyword.isEmpty()) {
            sendCommand("search " + keyword);
        } else {
            outputArea.appendText("Please enter a keyword to search\n");
        }
    }
    @FXML
    private void displayAllEntities() {
        sendCommand("GET_ALL_ENTITIES");
    }
    @FXML
    private void handleDisplayById() {
        sendCommand("view json");
    }
    @FXML
    private void handleProductIdJson() {
        String productId = idJsonField.getText();
        if (!productId.isEmpty()) {
            sendCommand("find json " + productId);
        }
        else {
            outputArea.appendText("Please enter a product ID\n");
        }
    }
    @FXML
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

    @FXML
    private void exitClient() {
        if (socketWriter != null) {
            socketWriter.println("exit");
        }
        outputArea.appendText("ClientApp exiting.\n");
        System.exit(0);
    }
}