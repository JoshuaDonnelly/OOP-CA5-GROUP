import DTOs.productDTO;
import Utils.JsonConnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    final static int SERVER_PORT_NUMBER = 49000;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {

        try (Socket socket = new Socket("localhost", SERVER_PORT_NUMBER);
             PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Client message: The Client is running and has connected to the server");

            while (true) {
                System.out.println("\n===== Guitar E-Commerce System =====");
                System.out.println("1. View All Products            <view>");
                System.out.println("2. Find Product by ID           <find id>");
                System.out.println("3. Insert New Product           <insert name price description category_id stock>");
                System.out.println("4. Update Product               <update id name price description category_id stock>");
                System.out.println("5. Delete Product               <delete id>");
                System.out.println("6. Search Products by Keyword   <search keyword>");
                System.out.println("9. Display Product by ID        <display id>");
                System.out.println("0. Exit                         <exit>");
                System.out.print("Enter your choice or command: ");
                String userRequest = scanner.nextLine().trim();

                // Option 0: Exit
                if (userRequest.equals("0") || userRequest.equalsIgnoreCase("exit")) {
                    socketWriter.println("exit");
                    String response = socketReader.readLine();
                    System.out.println("Client message: Response from server: \"" + response + "\"");
                    break;

                    // Option 9: Display Product by ID (pretty JSON-based)
                } else if (userRequest.equals("9")) {
                    System.out.print("Enter product ID: ");
                    String id = scanner.nextLine().trim();
                    socketWriter.println("find " + id);
                    String response = socketReader.readLine();

                    if (response == null || response.isEmpty() || response.equalsIgnoreCase("null")) {
                        System.out.println("No product found with ID: " + id);
                    } else {
                        productDTO product = JsonConnector.jsonToProduct(response);
                        System.out.println("Raw JSON response from server: " + response);
                        displayProduct(product);
                    }

                    // Manual command: "display <id>" (like before)
                } else if (userRequest.toLowerCase().startsWith("display ")) {
                    handleDisplayProductById(userRequest, socketWriter, socketReader);

                    // All other commands (view, find, insert, update, delete, search)
                } else if (isRecognizedCommand(userRequest)) {
                    socketWriter.println(userRequest);
                    String response = socketReader.readLine();
                    System.out.println("Client message: Response from server: \"" + response + "\"");


                } else {
                    System.out.println("Unrecognized command. Please try again.");
                }
            }

        } catch (IOException e) {
            System.out.println("Client message: IOException: " + e.getMessage());
        }

        System.out.println("Exiting client, but server may still be running.");
    }

    private void handleDisplayProductById(String userRequest, PrintWriter socketWriter, BufferedReader socketReader) throws IOException {
        String[] parts = userRequest.split(" ");
        if (parts.length == 2) {
            String id = parts[1];
            socketWriter.println("find " + id);
            String response = socketReader.readLine();
            if (response == null || response.isEmpty() || response.equalsIgnoreCase("null")) {
                System.out.println("No product found with ID: " + id);
                return;
            }
            productDTO product = JsonConnector.jsonToProduct(response);
            System.out.println("Raw JSON response from server: " + response);
            displayProduct(product);
        } else {
            System.out.println("Invalid command format. Use: display <ID>");
        }
    }

    private void displayProduct(productDTO product) {
        if (product == null) {
            System.out.println("Unable to display product. Data was null or invalid.");
            return;
        }
        System.out.println("\n===== Product Details =====");
        System.out.println("ID: " + product.getId());
        System.out.println("Name: " + product.getName());
        System.out.println("Price: " + product.getPrice());
        System.out.println("Description: " + product.getDescription());
        System.out.println("Category ID: " + product.getCategoryId());
        System.out.println("Stock: " + product.getStock());
        System.out.println("===========================\n");
    }

    private boolean isRecognizedCommand(String command) {
        return command.startsWith("view") ||
                command.startsWith("find") ||
                command.startsWith("insert") ||
                command.startsWith("update") ||
                command.startsWith("delete") ||
                command.startsWith("display") ||
                command.startsWith("search");
    }
}
