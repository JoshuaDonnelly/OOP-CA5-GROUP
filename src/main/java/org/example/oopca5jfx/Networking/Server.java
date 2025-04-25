    package org.example.oopca5jfx.Networking;

    import org.example.oopca5jfx.DAOs.productDAOInterface;
    import org.example.oopca5jfx.DTOs.productDTO;
    import org.example.oopca5jfx.Database.DatabaseConnection;
    import org.json.JSONArray;
    import org.json.JSONObject;


    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;
    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.io.PrintWriter;
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
                    System.out.println("Server: Listening for connections on port ..." + SERVER_PORT_NUMBER);

                    System.out.println("Server: ClientApp " + clientNumber + " has connected.");
                    System.out.println("Server: Port number of remote client: " + clientSocket.getPort());
                    System.out.println("Server: Port number of the socket used to talk with client " + clientSocket.getLocalPort());

                    // create a new ClientHandler for the requesting client, passing in the socket and client number,
                    // pass the handler into a new thread, and start the handler running in the thread.
                    Thread t = new Thread(new ClientHandler(clientSocket, clientNumber));
                    t.start();

                    System.out.println("Server: ClientHandler started in thread " + t.getName() + " for client " + clientNumber + ". ");
                }
            }
            catch(IOException ex) {
                System.out.println(ex);
            }
            finally{
                try {
                    if(clientSocket!=null)
                        clientSocket.close();
                } catch (IOException e) {
                    System.out.println(e);
                }
                try {
                    if(serverSocket!=null)
                        serverSocket.close();
                } catch (IOException e) {
                    System.out.println(e);
                }

            }
            System.out.println("Server: Server exiting, Goodbye!");
        }
    }

    class ClientHandler implements Runnable
    {
        BufferedReader socketReader;
        PrintWriter socketWriter;
        Socket clientSocket;
        final int clientNumber;

        // Constructor
        public ClientHandler(Socket clientSocket, int clientNumber) {
            this.clientSocket = clientSocket;  // store socket for closing later
            this.clientNumber = clientNumber;  // ID number that we are assigning to this client
            try {
                // assign to fields
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

                    if (request.startsWith("exit"))
                    {
                        socketWriter.println("Sorry to see you leaving. Goodbye.");
                        System.out.println("Server message: ClientApp has notified us that it is quitting.");
                    }
                    else if(request.startsWith("view json")){
                        productDAO dao = new productDAO();
                        socketWriter.println(dao.getAllProductsJson());
                    }
                    else if(request.startsWith("find json")){
                        String id = request.substring(10);
                        productDAO dao = new productDAO();
                        socketWriter.println(dao.getProductJsonById(Integer.parseInt(id)));
                    }
                    else if(request.startsWith("view")){
                        productDAO dao = new productDAO();
                        socketWriter.println(dao.getAllProducts());
                    }
                    else if(request.startsWith("find")){
                        String id = request.substring(5);
                        productDAO dao = new productDAO();
                        socketWriter.println(dao.getProductById(Integer.parseInt(id)));
                    }
                    else if(request.startsWith("insert")){
                        String product = request.substring(7);
                        productDAO dao = new productDAO();
                        socketWriter.println(dao.insertProduct(product));
                    }
                    else if(request.startsWith("update")){
                        String product = request.substring(7); // after "update "
                        productDAO dao = new productDAO();
                        boolean success = dao.updateProduct(product);
                        socketWriter.println(success ? "Product updated successfully" : "Failed to update product");
                    }
                    else if(request.startsWith("delete")){
                        String id = request.substring(7);
                        productDAO dao = new productDAO();
                        socketWriter.println(dao.deleteProductById(Integer.parseInt(id)));
                    }
                    else if(request.startsWith("search")){
                        String keyword = request.substring(7);
                        productDAO dao = new productDAO();
                        socketWriter.println(dao.searchProductsByKeyword(keyword));
                    }
                    else if(request.equals("GET_ALL_ENTITIES")){
                        productDAO dao = new productDAO();
                        socketWriter.println(dao.getAllProductsJson());

                    }
                    else{
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

        // Get all //

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
                            rs.getInt("stock")
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
                            rs.getInt("stock")
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
            String[] pSplit = pString.split(" ", 5); // Splitting input string into max 5 parts

            if (pSplit.length < 5) {
                System.out.println("Invalid input format. Expected: name price description categoryId stock");
                return null;
            }

            try {
                String name = pSplit[0];
                float price = Float.parseFloat(pSplit[1]);
                String description = pSplit[2];
                int categoryId = Integer.parseInt(pSplit[3]);
                int stock = Integer.parseInt(pSplit[4]);

                productDTO p = new productDTO(0, name, price, description, categoryId, stock);

                String sql = "INSERT INTO products (name, price, description, category_id, stock) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setString(1, p.getName());
                    pstmt.setFloat(2, p.getPrice());
                    pstmt.setString(3, p.getDescription());
                    pstmt.setInt(4, p.getCategoryId());
                    pstmt.setInt(5, p.getStock());

                    int affectedRows = pstmt.executeUpdate();
                    if (affectedRows > 0) {
                        ResultSet generatedKeys = pstmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            p.setId(generatedKeys.getInt(1));
                            return p;
                        }
                    }


                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("Error: Price, categoryId, and stock must be valid numbers.");
            }
            return null;
        }

        @Override
        public boolean updateProduct(String pString) {
            // Split by pipe character instead of space
            String[] pSplit = pString.split("\\|", 6);

            if (pSplit.length < 6) {
                System.out.println("Invalid input format. Expected: id|name|price|description|categoryId|stock");
                return false;
            }

            try {
                int id = Integer.parseInt(pSplit[0]);
                String name = pSplit[1];
                float price = Float.parseFloat(pSplit[2]);
                String description = pSplit[3];
                int categoryId = Integer.parseInt(pSplit[4]);
                int stock = Integer.parseInt(pSplit[5]);

                String sql = "UPDATE products SET name = ?, price = ?, description = ?, category_id = ?, stock = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, name);
                    pstmt.setFloat(2, price);
                    pstmt.setString(3, description);
                    pstmt.setInt(4, categoryId);
                    pstmt.setInt(5, stock);
                    pstmt.setInt(6, id);

                    int affectedRows = pstmt.executeUpdate();
                    return affectedRows > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number format in update data.");
                return false;
            }
        }

        // Search by Keyword //

        @Override
        public List<productDTO> searchProductsByKeyword(String keyword) {
            List<productDTO> results = new ArrayList<>();
            String sql = "SELECT * FROM products WHERE name LIKE ? OR description LIKE ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + keyword + "%");
                pstmt.setString(2, "%" + keyword + "%");

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    productDTO product = new productDTO(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getFloat("price"),
                            rs.getString("description"),
                            rs.getInt("category_id"),
                            rs.getInt("stock")
                    );
                    results.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (results.isEmpty()) {
                System.out.println("No products found.");
            } else {
                for (productDTO product : results) {
                    System.out.println(product);
                }
            }
            return results;
        }

        @Override
        public String getProductsJsonByKeyword(String keyword) {
            List<productDTO> products = searchProductsByKeyword(keyword);
            JSONArray jsonArray = new JSONArray(products);
            return jsonArray.toString();
        }
        private void displayProduct(productDTO product) {
            System.out.println("Product Details:");
            System.out.println("ID: " + product.getId());
            System.out.println("Name: " + product.getName());
            System.out.println("Price: " + product.getPrice());
            System.out.println("Description: " + product.getDescription());
            System.out.println("Category ID: " + product.getCategoryId());
            System.out.println("Stock: " + product.getStock());
        }

    }
