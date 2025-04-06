import DTOs.productDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
            System.out.println("1. View All Products                <view>");
            System.out.println("2. Find Product by ID               <find id>");
            System.out.println("3. Insert New Product               <insert name price description category_id stock>");
            System.out.println("4. Update Product                   <update id name price description category_id stock>");
            System.out.println("5. Delete Product                   <delete id>");
            System.out.println("6. Search Products by Keyword       <search keyword>");
            System.out.println("7. View All Products Json           <view json id>");
            System.out.println("8. Find Product by ID Json          <find json id>");
            System.out.println("0. Exit");
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
                    String response = socketReader.readLine();
                    System.out.println("Client message: Response from server: \"" + response + "\"");
                } else if (userRequest.startsWith("find")) {
                    String response = socketReader.readLine();
                    System.out.println("Client message: Response from server: \"" + response + "\"");
                } else if (userRequest.startsWith("insert")) {
                    String response = socketReader.readLine();
                    System.out.println("Client message: Response from server: \"" + response + "\"");
                } else if (userRequest.startsWith("update")) {
                    String response = socketReader.readLine();
                    System.out.println("Client message: Response from server: \"" + response + "\"");
                } else if (userRequest.startsWith("delete")) {
                    String response = socketReader.readLine();
                    System.out.println("Client message: Response from server: \"" + response + "\"");
                } else if (userRequest.startsWith("search")) {
                    String response = socketReader.readLine();
                    System.out.println("Client message: Response from server: \"" + response + "\"");
                } else {
                    System.out.println("Command unknown. Try again.");
                }

                userInput = new Scanner(System.in);
                System.out.println("\n===== Guitar E-Commerce System =====");
                System.out.println("1. View All Products                <view>");
                System.out.println("2. Find Product by ID               <find id>");
                System.out.println("3. Insert New Product               <insert name price description category_id stock>");
                System.out.println("4. Update Product                   <update id name price description category_id stock>");
                System.out.println("5. Delete Product                   <delete id>");
                System.out.println("6. Search Products by Keyword       <search keyword>");
                System.out.println("7. View All Products Json           <view json id>");
                System.out.println("8. Find Product by ID Json          <find json id>");
                System.out.println("0. Exit");
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
}
