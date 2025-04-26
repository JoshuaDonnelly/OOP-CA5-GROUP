package org.example.oopca5jfx.Networking;

import org.example.oopca5jfx.DAOs.productDAOInterface;
import org.example.oopca5jfx.DTOs.productDTO;
import org.example.oopca5jfx.Database.DatabaseConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    final int SERVER_PORT_NUMBER = 49000;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(SERVER_PORT_NUMBER);
            System.out.println("Server has started.");

            int clientNumber = 0;

            while (true) {
                System.out.println("Server: Listening/waiting for connections on port ..." + SERVER_PORT_NUMBER);
                clientSocket = serverSocket.accept();
                clientNumber++;
                System.out.println("Server: ClientApp " + clientNumber + " has connected.");

                Thread t = new Thread(new ClientHandler(clientSocket, clientNumber));
                t.start();
                System.out.println("Server: ClientHandler started in thread " + t.getName() + " for client " + clientNumber + ". ");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        } finally {
            try {
                if (clientSocket != null)
                    clientSocket.close();
            } catch (IOException e) {
                System.out.println(e);
            }
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        System.out.println("Server: Server exiting, Goodbye!");
    }
}

class ClientHandler implements Runnable {
    BufferedReader socketReader;
    PrintWriter socketWriter;
    Socket clientSocket;
    final int clientNumber;

    public ClientHandler(Socket clientSocket, int clientNumber) {
        this.clientSocket = clientSocket;
        this.clientNumber = clientNumber;
        try {
            this.socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            this.socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        String request;
        try {
            while ((request = socketReader.readLine()) != null) {
                System.out.println("Server: (ClientHandler): Read command from client " + clientNumber + ": " + request);

                if (request.startsWith("exit")) {
                    socketWriter.println("Sorry to see you leaving. Goodbye.");
                    System.out.println("Server message: ClientApp has notified us that it is quitting.");
                } else if (request.startsWith("view json")) {
                    productDAO dao = new productDAO();
                    socketWriter.println(dao.getAllProductsJson());
                } else if (request.startsWith("find json")) {
                    String id = request.substring(10);
                    productDAO dao = new productDAO();
                    socketWriter.println(dao.getProductJsonById(Integer.parseInt(id)));
                } else if (request.startsWith("view")) {
                    productDAO dao = new productDAO();
                    socketWriter.println(dao.getAllProducts());
                } else if (request.startsWith("find")) {
                    String id = request.substring(5);
                    productDAO dao = new productDAO();
                    socketWriter.println(dao.getProductById(Integer.parseInt(id)));
                } else if (request.startsWith("insert")) {
                    String product = request.substring(7);
                    productDAO dao = new productDAO();
                    socketWriter.println(dao.insertProduct(product));
                } else if (request.startsWith("update")) {
                    String product = request.substring(7);
                    productDAO dao = new productDAO();
                    boolean success = dao.updateProduct(product);
                    socketWriter.println(success ? "Product updated successfully" : "Failed to update product");
                } else if (request.startsWith("delete")) {
                    String id = request.substring(7);
                    productDAO dao = new productDAO();
                    socketWriter.println(dao.deleteProductById(Integer.parseInt(id)));
                } else if (request.startsWith("search")) {
                    String keyword = request.substring(7);
                    productDAO dao = new productDAO();
                    socketWriter.println(dao.searchProductsByKeyword(keyword));
                } else if (request.equals("GET_ALL_ENTITIES")) {
                    productDAO dao = new productDAO();
                    socketWriter.println(dao.getAllProductsJson());
                } else if (request.startsWith("GET_IMAGE_NAMES")) {
                    ImageDAO imageDao = new ImageDAO();
                    List<String> imageNames = imageDao.getAllImageNames();
                    socketWriter.println(new JSONArray(imageNames).toString());
                } else if (request.startsWith("ADD_PRODUCT")) {
                    // Extract the JSON part from the message
                    String jsonString = request.substring("ADD_PRODUCT".length()).trim();

                    try {
                        // Parse the JSON string into a JSONObject
                        JSONObject jsonObject = new JSONObject(jsonString);

                        // Extract the fields from the JSONObject
                        String name = jsonObject.getString("name");
                        float price = jsonObject.getFloat("price");
                        String description = jsonObject.getString("description");
                        int categoryId = jsonObject.getInt("categoryId");
                        int stock = jsonObject.getInt("stock");
                        String imageFileName = jsonObject.getString("imageFileName");

                        // Create a productDAO instance and add the product using the extracted values
                        productDAO dao = new productDAO();
                        dao.addProduct(name, price, description, categoryId, stock, imageFileName);

                        // Send a response back to the client
                        socketWriter.println("Product added successfully!");
                    } catch (JSONException e) {
                        socketWriter.println("Error: Invalid JSON format");
                    }
                } else if (request.startsWith("DELETE_PRODUCT")) {
                    String jsonString = request.substring("DELETE_PRODUCT".length()).trim();
                    productDAO dao = new productDAO();

                    try {
                        JSONObject jsonRequest = new JSONObject(jsonString);
                        int productId = jsonRequest.getInt("id");

                        boolean deletionSuccessful = dao.deleteProductByIdJson(productId);

                        JSONObject jsonResponse = new JSONObject();

                        if (deletionSuccessful) {
                            jsonResponse.put("status", "success");
                            jsonResponse.put("message", "Product deleted successfully.");
                        } else {
                            jsonResponse.put("status", "error");
                            jsonResponse.put("message", "Product with ID " + productId + " not found.");
                        }

                        socketWriter.println(jsonResponse.toString());

                    } catch (Exception e) {
                        JSONObject errorResponse = new JSONObject();
                        errorResponse.put("status", "error");
                        errorResponse.put("message", "Failed to process the request.");
                        socketWriter.println(errorResponse.toString());
                    }
                } else if (request.startsWith("GET_IMAGE")) {
                    String imageName = request.substring(9);
                    ImageDAO imageDao = new ImageDAO();
                    byte[] imageData = imageDao.getImageData(imageName);

                    if (imageData != null) {
                        socketWriter.println("IMAGE_DATA_START");
                        OutputStream outputStream = clientSocket.getOutputStream();
                        outputStream.write(imageData);
                        outputStream.flush();
                        socketWriter.println("IMAGE_DATA_END");
                    } else {
                        socketWriter.println("ERROR: Image not found");
                    }
                } else {
                    socketWriter.println("error I'm sorry I don't understand your request");
                    System.out.println("Server message: Invalid request from client.");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            this.socketWriter.close();
            try {
                this.socketReader.close();
                this.clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Server: (ClientHandler): Handler for ClientApp " + clientNumber + " is terminating .....");
    }
}

class productDAO implements productDAOInterface {
    private Connection conn;

    public productDAO() {
        conn = DatabaseConnection.getConnection();
    }

    @Override
    public List<productDTO> getAllProducts() {
        List<productDTO> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(new productDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getFloat("price"),
                        rs.getString("description"),
                        rs.getInt("category_id"),
                        rs.getInt("stock"),
                        rs.getString("image_filename")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public String getAllProductsJson() {
        List<productDTO> products = getAllProducts();
        JSONArray jsonArray = new JSONArray(products);
        return jsonArray.toString();
    }

    @Override
    public productDTO getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new productDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getFloat("price"),
                        rs.getString("description"),
                        rs.getInt("category_id"),
                        rs.getInt("stock"),
                        rs.getString("image_filename")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getProductJsonById(int id) {
        productDTO product = getProductById(id);
        return product != null ? new JSONObject(product).toString() : "{}";
    }

    @Override
    public boolean deleteProductById(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public productDTO insertProduct(String pString) {
        String[] pSplit = pString.split(" ", 6); // 6 parts, not 5

        if (pSplit.length < 6) {
            System.out.println("Invalid input format. Expected: name price description categoryId stock imageFilename");
            return null;
        }

        try {
            String name = pSplit[0];
            float price = Float.parseFloat(pSplit[1]);
            String description = pSplit[2];
            int categoryId = Integer.parseInt(pSplit[3]);
            int stock = Integer.parseInt(pSplit[4]);
            String imageFilename = pSplit[5];

            productDTO p = new productDTO(0, name, price, description, categoryId, stock, imageFilename);

            String sql = "INSERT INTO products (name, price, description, category_id, stock, image_filename) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, p.getName());
                pstmt.setFloat(2, p.getPrice());
                pstmt.setString(3, p.getDescription());
                pstmt.setInt(4, p.getCategoryId());
                pstmt.setInt(5, p.getStock());
                pstmt.setString(6, p.getImageFilename());

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        p.setId(generatedKeys.getInt(1));
                        return p;
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateProduct(String pString) {
        String[] pSplit = pString.split("\\|", 7); // now expecting 7 fields

        if (pSplit.length < 7) {
            System.out.println("Invalid input format. Expected: id|name|price|description|categoryId|stock|imageFilename");
            return false;
        }

        try {
            int id = Integer.parseInt(pSplit[0]);
            String name = pSplit[1];
            float price = Float.parseFloat(pSplit[2]);
            String description = pSplit[3];
            int categoryId = Integer.parseInt(pSplit[4]);
            int stock = Integer.parseInt(pSplit[5]);
            String imageFilename = pSplit[6];

            String sql = "UPDATE products SET name = ?, price = ?, description = ?, category_id = ?, stock = ?, image_filename = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setFloat(2, price);
                pstmt.setString(3, description);
                pstmt.setInt(4, categoryId);
                pstmt.setInt(5, stock);
                pstmt.setString(6, imageFilename);
                pstmt.setInt(7, id);

                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String addProduct(String name, float price, String description, int categoryId, int stock, String imageFilename) {
        // Check if any of the fields are null or empty
        if (name == null || name.isEmpty() || description == null || description.isEmpty() || imageFilename == null || imageFilename.isEmpty()) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Invalid input. All fields must be provided.");
            return errorResponse.toString();
        }

        try {
            productDTO p = new productDTO(0, name, price, description, categoryId, stock, imageFilename);

            String sql = "INSERT INTO products (name, price, description, category_id, stock, image_filename) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, p.getName());
                pstmt.setFloat(2, p.getPrice());
                pstmt.setString(3, p.getDescription());
                pstmt.setInt(4, p.getCategoryId());
                pstmt.setInt(5, p.getStock());
                pstmt.setString(6, p.getImageFilename());

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        p.setId(generatedKeys.getInt(1));

                        // Return success response with the new product details
                        JSONObject successResponse = new JSONObject();
                        successResponse.put("status", "success");
                        successResponse.put("id", p.getId());
                        successResponse.put("name", p.getName());
                        successResponse.put("price", p.getPrice());
                        successResponse.put("description", p.getDescription());
                        successResponse.put("categoryId", p.getCategoryId());
                        successResponse.put("stock", p.getStock());
                        successResponse.put("imageFilename", p.getImageFilename());

                        return successResponse.toString();
                    }
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            // Return error if an exception occurs
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Database error occurred.");
            return errorResponse.toString();
        }

        // If no rows were affected or an error occurred, return an error message
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("status", "error");
        errorResponse.put("message", "Failed to add product.");
        return errorResponse.toString();
    }

    public boolean deleteProductByIdJson(int productId) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public List<productDTO> searchProductsByKeyword(String keyword) {
        List<productDTO> results = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ? OR description LIKE ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new productDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getFloat("price"),
                        rs.getString("description"),
                        rs.getInt("category_id"),
                        rs.getInt("stock"),
                        rs.getString("image_filename")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public String getProductsJsonByKeyword(String keyword) {
        List<productDTO> products = searchProductsByKeyword(keyword);
        JSONArray jsonArray = new JSONArray(products);
        return jsonArray.toString();
    }

    @Override
    public String getProductImagePath(int productId) {
        return "";
    }

    @Override
    public List<String> getProductAdditionalImages(int productId) {
        return List.of();
    }

    @Override
    public boolean updateProductImage(int productId, String imageFilename) {
        return false;
    }

    private void displayProduct(productDTO product) {
        System.out.println("Product Details:");
        System.out.println("ID: " + product.getId());
        System.out.println("Name: " + product.getName());
        System.out.println("Price: " + product.getPrice());
        System.out.println("Description: " + product.getDescription());
        System.out.println("Category ID: " + product.getCategoryId());
        System.out.println("Stock: " + product.getStock());
        System.out.println("Image Filename: " + product.getImageFilename());
    }
}
