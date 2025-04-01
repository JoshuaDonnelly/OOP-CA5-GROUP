import DAOs.productDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;

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

            int clientNumber = 0;  // a number sequentially allocated to each new client (for identification purposes here)

            while (true) {
                System.out.println("Server: Listening/waiting for connections on port ..." + SERVER_PORT_NUMBER);
                clientSocket = serverSocket.accept();
                clientNumber++;
                System.out.println("Server: Listening for connections on port ..." + SERVER_PORT_NUMBER);

                System.out.println("Server: Client " + clientNumber + " has connected.");
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

class ClientHandler implements Runnable   // each ClientHandler communicates with one Client
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

    /**
     * run() method is called by the Thread it is assigned to.
     * This code runs independently of all other threads.
     */
    @Override
    public void run() {
        String request;
        try {
            while ((request = socketReader.readLine()) != null) {
                System.out.println("Server: (ClientHandler): Read command from client " + clientNumber + ": " + request);

                // Implement our PROTOCOL
                // The protocol is the logic that determines the responses given based on requests received.
                //
                if (request.startsWith("time"))  // so, client wants the time !
                {
                    LocalTime time = LocalTime.now();  // get the time
                    socketWriter.println(time);  // send the time to client (as a string of characters)
                    System.out.println("Server message: time sent to client.");
                } else if (request.startsWith("echo")) {
                    String message = request.substring(5); // strip off the leading substring "echo "
                    socketWriter.println(message);   // send the received message back to the client
                    System.out.println("Server message: echo message sent to client.");
                } else if (request.startsWith("quit"))
                {
                    socketWriter.println("Sorry to see you leaving. Goodbye.");
                    System.out.println("Server message: Client has notified us that it is quitting.");
                }
                else if (request.startsWith("triple")){
                    String triple = request.substring(7);
                    socketWriter.println(Integer.parseInt(triple)*3);
                }else if (request.startsWith("add")){
                    String add = request.substring(4);
                    int x = Integer.parseInt(add.charAt(0)+"");
                    int y = Integer.parseInt(add.charAt(2)+"");;
                    socketWriter.println(x + y);
                }else if (request.startsWith("mult")){
                    String mult = request.substring(5);
                    int x = Integer.parseInt(mult.charAt(0)+"");
                    int y = Integer.parseInt(mult.charAt(2)+"");
                    socketWriter.println(x * y);
                }else if (request.startsWith("sub")){
                    String sub = request.substring(4);
                    int x = Integer.parseInt(sub.charAt(0)+"");
                    int y = Integer.parseInt(sub.charAt(2)+"");
                    socketWriter.println(x - y);
                }else if (request.startsWith("div")){
                    String div = request.substring(4);
                    int x = Integer.parseInt(div.charAt(0)+"");
                    int y = Integer.parseInt(div.charAt(0)+"");
                    socketWriter.println(x / y);
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
        System.out.println("Server: (ClientHandler): Handler for Client " + clientNumber + " is terminating .....");
    }
}