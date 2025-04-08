import DTOs.productDTO;
import Utils.JsonConnector;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    final static int SERVER_PORT_NUMBER = 49000;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Client().start();
    }

    public void start() {
        try (Socket socket = new Socket("localhost", SERVER_PORT_NUMBER);
             PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Client: Connected to server at port " + SERVER_PORT_NUMBER);

            while (true) {
                displayMenu();
                String choice = scanner.nextLine().trim();

                if (choice.equals("0")) {
                    socketWriter.println("exit");
                    System.out.println("Client: " + socketReader.readLine());
                    break;
                }

                switch (choice) {
                    case "1":
                        socketWriter.println("view");
                        handleTextProductList(socketReader);
                        break;

                    case "2":
                        System.out.print("Enter product ID: ");
                        socketWriter.println("find " + scanner.nextLine().trim());
                        handleSingleProduct(socketReader);
                        break;

                    case "3":
                        System.out.println("Enter product details: name price description category_id stock");
                        socketWriter.println("insert " + scanner.nextLine().trim());
                        System.out.println("Server: " + socketReader.readLine());
                        break;

                    case "4":
                        System.out.println("Enter updated details: id name price description category_id stock");
                        socketWriter.println("update " + scanner.nextLine().trim());
                        System.out.println("Server: " + socketReader.readLine());
                        break;

                    case "5":
                        System.out.print("Enter product ID to delete: ");
                        socketWriter.println("delete " + scanner.nextLine().trim());
                        System.out.println("Server: " + socketReader.readLine());
                        break;

                    case "6":
                        System.out.print("Enter search keyword: ");
                        socketWriter.println("search " + scanner.nextLine().trim());
                        handleTextProductList(socketReader);
                        break;

                    case "7":
                        socketWriter.println("view json");
                        handleJsonProductList(socketReader);
                        break;

                    case "8":
                        System.out.print("Enter product ID: ");
                        socketWriter.println("find json " + scanner.nextLine().trim());
                        handleJsonSingleProduct(socketReader);
                        break;

                    case "9":
                        System.out.print("Enter product ID: ");
                        socketWriter.println("find " + scanner.nextLine().trim());
                        handleSingleProduct(socketReader);
                        break;

                    case "10":
                        socketWriter.println("GET_ALL_ENTITIES");
                        handleJsonProductList(socketReader);
                        break;

                    default:
                        System.out.println("Invalid choice. Choose between 0–10.");
                }
            }

        } catch (IOException e) {
            System.out.println("Client: IOException - " + e.getMessage());
        }

        System.out.println("Client: Exiting. Server may still be running.");
    }

    private void displayMenu() {
        System.out.println("\n==== Guitar E-Commerce Client ====");
        System.out.println("1. View All Products");
        System.out.println("2. Find Product by ID");
        System.out.println("3. Insert New Product");
        System.out.println("4. Update Product");
        System.out.println("5. Delete Product");
        System.out.println("6. Search Products by Keyword");
        System.out.println("7. View All Products (JSON)");
        System.out.println("8. Find Product by ID (JSON)");
        System.out.println("9. Display Product by ID");
        System.out.println("10. Display All Entities (JSON)");
        System.out.println("0. Exit");
        System.out.print("Enter your choice (0–10): ");
    }

    private void handleTextProductList(BufferedReader reader) throws IOException {
        String response = reader.readLine();
        if (response == null || response.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        String[] productStrings = response.split("}, ");
        for (int i = 0; i < productStrings.length; i++) {
            if (!productStrings[i].endsWith("}")) {
                productStrings[i] += "}";
            }
        }

        List<productDTO> products = new ArrayList<>();

        for (String str : productStrings) {
            try {
                str = str.replace("productDTO{", "").replace("}", "");
                String[] parts = str.split(", ");
                productDTO p = new productDTO();
                for (String part : parts) {
                    String[] keyValue = part.split("=");
                    if (keyValue.length == 2) {
                        switch (keyValue[0]) {
                            case "id" -> p.setId(Integer.parseInt(keyValue[1]));
                            case "name" -> p.setName(keyValue[1]);
                            case "price" -> p.setPrice(Float.parseFloat(keyValue[1]));
                            case "description" -> p.setDescription(keyValue[1]);
                            case "categoryId" -> p.setCategoryId(Integer.parseInt(keyValue[1]));
                            case "stock" -> p.setStock(Integer.parseInt(keyValue[1]));
                        }
                    }
                }
                products.add(p);
            } catch (Exception e) {
                System.out.println("Parse error: " + str);
            }
        }

        displayProductList(products);
    }

    private void handleSingleProduct(BufferedReader reader) throws IOException {
        String response = reader.readLine();
        if (response == null || response.equalsIgnoreCase("null")) {
            System.out.println("No product found.");
            return;
        }

        try {
            String[] parts = response.replace("productDTO{", "").replace("}", "").split(", ");
            productDTO product = new productDTO();
            for (String part : parts) {
                String[] kv = part.split("=");
                if (kv.length == 2) {
                    switch (kv[0]) {
                        case "id" -> product.setId(Integer.parseInt(kv[1]));
                        case "name" -> product.setName(kv[1]);
                        case "price" -> product.setPrice(Float.parseFloat(kv[1]));
                        case "description" -> product.setDescription(kv[1]);
                        case "categoryId" -> product.setCategoryId(Integer.parseInt(kv[1]));
                        case "stock" -> product.setStock(Integer.parseInt(kv[1]));
                    }
                }
            }
            displayProduct(product);
        } catch (Exception e) {
            System.out.println("Error parsing product: " + response);
        }
    }

    private void handleJsonProductList(BufferedReader reader) throws IOException {
        String response = reader.readLine();
        try {
            List<productDTO> products = JsonConnector.jsonToProductList(response);
            displayProductList(products);
        } catch (Exception e) {
            System.out.println("JSON parse error: " + e.getMessage());
        }
    }

    private void handleJsonSingleProduct(BufferedReader reader) throws IOException {
        String response = reader.readLine();
        if (response == null || response.equalsIgnoreCase("null")) {
            System.out.println("No product found.");
            return;
        }

        try {
            productDTO product = JsonConnector.jsonToProduct(response);
            displayProduct(product);
        } catch (Exception e) {
            System.out.println("JSON parse error: " + e.getMessage());
        }
    }

    private void displayProduct(productDTO product) {
        System.out.println("\n-- Product --");
        System.out.printf("ID: %d\nName: %s\nPrice: $%.2f\nDescription: %s\nCategory ID: %d\nStock: %d\n",
                product.getId(), product.getName(), product.getPrice(), product.getDescription(),
                product.getCategoryId(), product.getStock());
    }

    private void displayProductList(List<productDTO> products) {
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        System.out.println("\n-- Product List --");
        System.out.printf("%-5s %-20s %-10s %-30s %-12s %-8s\n",
                "ID", "Name", "Price", "Description", "Category ID", "Stock");
        System.out.println("----------------------------------------------------------------------------");

        for (productDTO p : products) {
            System.out.printf("%-5d %-20s $%-9.2f %-30s %-12d %-8d\n",
                    p.getId(), p.getName(), p.getPrice(), p.getDescription(),
                    p.getCategoryId(), p.getStock());
        }
        System.out.println("----------------------------------------------------------------------------");
    }
}
