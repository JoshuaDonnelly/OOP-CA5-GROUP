package org.example.oopca5jfx.Networking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.json.JSONArray;

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
            Socket socket = new Socket("localhost", SERVER_PORT_NUMBER);
            socketWriter = new PrintWriter(socket.getOutputStream(), true);
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientSocket = new Socket("localhost", SERVER_PORT_NUMBER);
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
            socketWriter.println("GET_IMAGE " + imageName);

            String response = socketReader.readLine();

            if (response.equals("IMAGE_DATA_START")) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Image");
                fileChooser.setInitialFileName(imageName);
                File file = fileChooser.showSaveDialog(outputArea.getScene().getWindow());

                if (file != null) {
                    InputStream inputStream = clientSocket.getInputStream();

                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, bytesRead);

                        String content = buffer.toString("UTF-8");
                        if (content.contains("IMAGE_DATA_END")) {
                            break;
                        }
                    }

                    byte[] imageBytes = buffer.toByteArray();
                    String imageString = new String(imageBytes, "UTF-8");
                    int endIndex = imageString.indexOf("IMAGE_DATA_END");
                    if (endIndex != -1) {
                        imageBytes = imageString.substring(0, endIndex).getBytes("UTF-8");
                    }

                    Files.write(file.toPath(), imageBytes);

                    outputArea.appendText("Image downloaded successfully: " + file.getAbsolutePath() + "\n");

                    displayImage(imageBytes);
                }
            } else {
                outputArea.appendText("Server response: " + response + "\n");
            }
        } catch (IOException e) {
            outputArea.appendText("Error downloading image: " + e.getMessage() + "\n");
        }
    }

    private void displayImage(byte[] imageData) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
            Image image = new Image(bis);
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

                if (command.equals("GET_IMAGE_NAMES")) {
                    JSONArray jsonArray = new JSONArray(response);
                    ObservableList<String> imageNames = FXCollections.observableArrayList();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        imageNames.add(jsonArray.getString(i));
                    }
                    imageListView.setItems(imageNames);

                    // ⭐ NEW: Set custom cell factory to show thumbnails
                    imageListView.setCellFactory(param -> new ListCell<String>() {
                        private final ImageView imageView = new ImageView();
                        @Override
                        protected void updateItem(String imageName, boolean empty) {
                            super.updateItem(imageName, empty);
                            if (empty || imageName == null) {
                                setGraphic(null);
                            } else {
                                try {
                                    File imageFile = new File("images/" + imageName); // assumes local /images/ folder
                                    if (imageFile.exists()) {
                                        Image image = new Image(imageFile.toURI().toString(), 100, 100, true, true);
                                        imageView.setImage(image);
                                        setGraphic(imageView);
                                    } else {
                                        setText("Missing: " + imageName);
                                        setGraphic(null);
                                    }
                                } catch (Exception e) {
                                    setText("Error: " + e.getMessage());
                                    setGraphic(null);
                                }
                            }
                        }
                    });

                    outputArea.appendText("Received " + imageNames.size() + " image names\n");
                } else {
                    outputArea.appendText("Server response: \n" + response + "\n");
                }
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
