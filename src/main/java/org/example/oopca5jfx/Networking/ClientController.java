package org.example.oopca5jfx.Networking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class ClientController {
    private static final int SERVER_PORT_NUMBER = 49000;
    private PrintWriter socketWriter;
    private BufferedReader socketReader;
    private Socket clientSocket;

    @FXML
    private TextArea outputArea;

    @FXML
    private TextField productIdToFind;

    @FXML
    private TextField productIdField1;

    @FXML
    private TextField productIdField2;

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

    @FXML
    private TextField nameFieldAdd;
    @FXML
    private TextField idFieldAdd;
    @FXML
    private TextField priceFieldAdd;
    @FXML
    private TextArea descriptionFieldAdd;
    @FXML
    private TextField catIdFieldAdd;
    @FXML
    private TextField stockFieldAdd;

    @FXML
    private TextField keywordField;
    @FXML
    private TextField idJsonField;
    @FXML
    private ImageView imageView;
    @FXML
    private ListView<String> imageListView;

    @FXML
    private void connectToServer() {
        try {
            clientSocket = new Socket("localhost", SERVER_PORT_NUMBER);
            socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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

            String updateCommand = String.format("update %d|%s|%f|%s|%d|%d", id, name, price, description, categoryId, stock);
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

    @FXML
    private void handleAdd() {
        try {
            String name = nameFieldAdd.getText().trim();
            float price = Float.parseFloat(priceFieldAdd.getText());
            String description = descriptionFieldAdd.getText().trim();
            int categoryId = Integer.parseInt(catIdFieldAdd.getText());
            int stock = Integer.parseInt(stockFieldAdd.getText());
            String imageFilename = "";

            // Build the JSON object
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("price", price);
            jsonObject.put("description", description);
            jsonObject.put("categoryId", categoryId);
            jsonObject.put("stock", stock);
            jsonObject.put("imageFilename", imageFilename);

            // Send to server
            socketWriter.println("ADD_PRODUCT " + jsonObject.toString());

            // Receive response
            String response = socketReader.readLine();
            System.out.println("Response from server: " + response);

            if (response.startsWith("{")) {
                JSONObject responseJson = new JSONObject(response);
                if (responseJson.has("error")) {
                    showError(responseJson.getString("error"));
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Add Product");
                    alert.setHeaderText("Product Added Successfully!");
                    alert.setContentText(responseJson.toString(2));
                    alert.showAndWait();
                }
            } else {
                showError("Unexpected server response: " + response);
            }

        } catch (NumberFormatException e) {
            showError("Invalid input. Please make sure price, category ID and stock are numbers.");
        } catch (IOException e) {
            showError("Error communicating with server.");
        }
    }

    public void handleDelete() {
        try {
            int entityId = Integer.parseInt(productIdField2.getText().trim());

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("id", entityId);

            socketWriter.println("DELETE_PRODUCT " + jsonRequest.toString());

            String response = socketReader.readLine();
            JSONObject jsonResponse = new JSONObject(response);

            // Check if the deletion was successful
            if ("success".equals(jsonResponse.getString("status"))) {
                System.out.println("Product deleted successfully.");
            } else {
                String errorMessage = jsonResponse.getString("message");
                System.out.println("Error: " + errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    private void handleGetImageNames() {
        sendCommand("GET_IMAGE_NAMES");
    }

    @FXML
    private void handleDownloadImage() {
        String selectedImage = imageListView.getSelectionModel().getSelectedItem();
        if (selectedImage != null) {
            downloadImage(selectedImage);
        }
    }

    private void downloadImage(String imageName) {
        try {
            // Send request for the image
            socketWriter.println("GET_IMAGE " + imageName);

            // First read the image size from server
            String sizeResponse = socketReader.readLine();
            if (!sizeResponse.startsWith("IMAGE_SIZE:")) {
                outputArea.appendText("Invalid server response: " + sizeResponse + "\n");
                return;
            }

            long imageSize = Long.parseLong(sizeResponse.substring(11));

            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            fileChooser.setInitialFileName(imageName);
            File file = fileChooser.showSaveDialog(imageView.getScene().getWindow());

            if (file != null) {
                // Get the socket's raw input stream
                InputStream inputStream = clientSocket.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                // Read the image data in chunks
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytesRead = 0;

                while (totalBytesRead < imageSize) {
                    bytesRead = inputStream.read(buffer, 0,
                            (int) Math.min(buffer.length, imageSize - totalBytesRead));
                    if (bytesRead == -1) break;
                    fileOutputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }

                fileOutputStream.close();
                outputArea.appendText("Image downloaded successfully: " + file.getAbsolutePath() + "\n");

                // Display the image
                displayImage(file);
            }
        } catch (IOException e) {
            outputArea.appendText("Error downloading image: " + e.getMessage() + "\n");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            outputArea.appendText("Error parsing image size from server\n");
        }
    }

    private void displayImage(File imageFile) {
        try {
            Image image = new Image(imageFile.toURI().toString());
            imageView.setImage(image);
        } catch (Exception e) {
            outputArea.appendText("Error displaying image: " + e.getMessage() + "\n");
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
        } else {
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
