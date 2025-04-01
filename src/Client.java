import DTOs.productDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Client {

    final static int SERVER_PORT_NUMBER = 49000;
    private static final Scanner scanner = new Scanner(System.in);
    private static final productDAO productDAO = new productDAO();

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public static void start() {

        try (   // Attempt to establish a connection with the server and, if successful,
                // create a Socket for communication.
                Socket socket = new Socket("localhost", SERVER_PORT_NUMBER);

                // get the socket's input and output streams, and wrap them in writer and readers
                PrintWriter socketWriter = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ) {
            System.out.println("Client message: The Client is running and has connected to the server");
            // ask user to enter a command
            Scanner userInput = new Scanner(System.in);
            System.out.println("\n===== Guitar E-Commerce System =====");
            System.out.println("1. View All Products");
            System.out.println("2. Find Product by ID");
            System.out.println("3. Insert New Product");
            System.out.println("4. Update Product");
            System.out.println("5. Delete Product");
            System.out.println("6. Search Products by Keyword");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            System.out.print("Please enter a command: ");
            String userRequest = userInput.nextLine();

            while(true) {
                // send the command to the server on the socket
                socketWriter.println(userRequest);    // write the user's request to socket along with a newline terminator (which is required) (it is included by println() )
                // out.flush();                      // flushing buffer NOT necessary as auto flush is set to true

                // based on the request sent, process the answer returned by the server
                //
                if (userRequest.startsWith("exit")) // if the user has entered the "quit" command
                {
                    String response = socketReader.readLine();   // wait for response -
                    System.out.println("Client message: Response from server: \"" + response + "\"");
                    break;  // break out of while loop, client will exit.
                } else if (userRequest.startsWith("view")) {
                    String tripleString = socketReader.readLine();
                    System.out.println("Client message: Response from server: \"" + tripleString + "\"");
                } else if (userRequest.startsWith("find")) {
                    String addString = socketReader.readLine();
                    System.out.println("Client message: Response from server: \"" + addString + "\"");
                } else if (userRequest.startsWith("insert")) {
                    String multString = socketReader.readLine();
                    System.out.println("Client message: Response from server: \"" + multString + "\"");
                } else if (userRequest.startsWith("update")) {
                    String subString = socketReader.readLine();
                    System.out.println("Client message: Response from server: \"" + subString + "\"");
                } else if (userRequest.startsWith("delete")) {
                    String divString = socketReader.readLine();
                    System.out.println("Client message: Response from server: \"" + divString + "\"");
                } else if (userRequest.startsWith("search")) {
                String divString = socketReader.readLine();
                System.out.println("Client message: Response from server: \"" + divString + "\"");
                } else {
                    System.out.println("Command unknown. Try again.");
                }

                userInput = new Scanner(System.in);
                System.out.println("\n===== Guitar E-Commerce System =====");
                System.out.println("1. View All Products");
                System.out.println("2. Find Product by ID");
                System.out.println("3. Insert New Product");
                System.out.println("4. Update Product");
                System.out.println("5. Delete Product");
                System.out.println("6. Search Products by Keyword");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");
                System.out.print("Please enter a command: ");
                userRequest = userInput.nextLine();
            }

        }
        catch (IOException e) {
            System.out.println("Client message: IOException: " + e);
        }
        // sockets and streams are closed automatically due to try-with-resources
        // so no finally block is required here.

        System.out.println("Exiting client, but server may still be running.");
    }



    private static void viewAllProducts () {
        System.out.println("\n--- All Products ---");
        List<productDTO> products = productDAO.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
        } else {
            for (productDTO product : products) {
                System.out.println(product);
            }
        }
    }

    private static void findProductById () {
        System.out.print("\nEnter Product ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        productDTO product = productDAO.getProductById(id);
        if (product != null) {
            System.out.println("Product found: " + product);
        } else {
            System.out.println("Product not found.");
        }
    }

    private static void deleteProductById () {
        System.out.print("\nEnter Product ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        boolean product = productDAO.deleteProductById(id);
        if (product) {
            System.out.println("Product found");
            System.out.println("Deleting...");
        } else {
            System.out.println("Product not found.");
        }
    }

    private static void insertNewProduct () {
        System.out.println("\n--- Insert New Product ---");

        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Product Price: ");
        while (!scanner.hasNextFloat()) {
            System.out.print("Invalid input! Enter a valid price: ");
            scanner.next();
        }
        float price = scanner.nextFloat();
        scanner.nextLine();

        System.out.print("Enter Product Description: ");
        String description = scanner.nextLine();

        System.out.print("Enter Category ID: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input! Enter a valid category ID: ");
            scanner.next();
        }
        int categoryId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter Stock Quantity: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input! Enter a valid stock quantity: ");
            scanner.next();
        }
        int stock = scanner.nextInt();
        scanner.nextLine();

        productDTO newProduct = new productDTO(0, name, price, description, categoryId, stock);
        productDTO insertedProduct = productDAO.insertProduct(newProduct);

        if (insertedProduct != null) {
            System.out.println("Product inserted successfully! Assigned ID: " + insertedProduct.getId());
        } else {
            System.out.println("Failed to insert product.");
        }
    }
}
