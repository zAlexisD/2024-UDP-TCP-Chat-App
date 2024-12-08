/**
 * A TCP server that listens on a specified port, accepts client connections, echoes received messages,
 * and handles specific commands for managing the server and client interactions.
 *
 * <p>Usage: `java TCPServer <listening port>`</p>
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Echoes messages received from the client.</li>
 *     <li>Manages client disconnections and specific commands.</li>
 *     <li>Times out if no client connects within a specified interval (default: 1 minute).</li>
 * </ul>
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class TCPServer {
    private int listeningPort;
    private String serverState;

    // Configuration constants
    private final int defaultPort = 0;
    private final int maxBufSize = 1024;
    private static final int systemShutdown = 1;

    // Connection flags
    protected boolean serverConnected = true;
    protected boolean clientConnected = true;

    // Set time settings
    private final int millisToSec = 1000;
    private final int timeout = 60000;  // Set connection time out at 1 min (60000 ms)
    private final int interval = 10000;  // Set remind interval at 10s (10000 ms)
    private long lastActivityTime = System.currentTimeMillis();   // Set the last activity time as start time

    /**
     * Creates a TCPServer instance with a specified listening port.
     *
     * @param listeningPort the port number on which the server will listen for incoming connections.
     */
    public TCPServer(int listeningPort) {
        this.listeningPort = listeningPort;
        this.serverState = "Closed";
    }

    /**
     * Default constructor for TCPServer, which uses the default port.
     */
    public TCPServer() {
        this.listeningPort = defaultPort;
        this.serverState = "Closed";
    }

    /**
     * Gets the port number on which the server is listening.
     *
     * @return the listening port number.
     */
    public int getListeningPort() {
        return listeningPort;
    }

    /**
     * Launches the server to accept client connections, handle communication, and manage commands.
     *
     * <p>Commands handled by the server:</p>
     * <ul>
     *     <li>`exit console` - Disconnects the client from the server.</li>
     *     <li>`close server` - Shuts down the server.</li>
     * </ul>
     *
     * <p>The server times out after 1 minute if no connections are received, with periodic reminders.</p>
     *
     * @throws IOException if an I/O error occurs when opening or managing the server socket.
     */
    public void launch() throws IOException {

        try(ServerSocket serverSocket = new ServerSocket(this.listeningPort)) {
            this.serverState = "Running";
            System.out.println("Server is running and listening on port " + this.getListeningPort());

            // Predefined echo message to send back to the client
            String echo = "Message received\n";
            byte[] echo_buf = echo.getBytes(StandardCharsets.UTF_8);


            System.out.println("Waiting for connection...\n");
            // Set reminder timeout for connection attempts by unblocking .accept()
            serverSocket.setSoTimeout(interval);

            // Server's listening session loop
            while(serverConnected){
                // Close the server if the timeout for incoming connections is reached
                if (System.currentTimeMillis() - lastActivityTime > timeout){
                    System.out.println("Timeout reached. No connection received");
                    break;
                }

                // Check for incoming connections
                try (Socket clientSocket = serverSocket.accept()) {
                    // Define client ID
                    String clientID = clientSocket.getInetAddress() + ":" + clientSocket.getPort();

                    System.out.println("Connection from client : " + clientID);
                    // Reset the client connection flag
                    clientConnected = true;

                    // Client's session loop
                    while (clientConnected) {
                        InputStream input = clientSocket.getInputStream();
                        OutputStream output = clientSocket.getOutputStream();

                        // WIP : handle ? and not displaying when client use a command
                        // Buffer for incoming data
                        byte[] buf = new byte[maxBufSize];
                        int byteRead = input.read(buf);

                        // Decode the received message
                        String receivedData = new String(buf, StandardCharsets.UTF_8);
                        System.out.println("Client " + clientID + " says : " + receivedData);

                        // WIP : Handle connection loss (byteRead = -1 is an end of a stream)
                        if (byteRead == -1) {
                            System.out.println("Client " + clientID + " disappeared\n");
                            break;
                        }
                        // Handle user's disconnection
                        if (receivedData.trim().equalsIgnoreCase(("exit console"))) {
                            System.out.println("User at " + clientID + " left the chat\n");
                            System.out.println("Waiting for new connection...\n");
                            lastActivityTime = System.currentTimeMillis();
                            clientConnected = false;
                        }
                        // Handle TCP server closure commanded by client
                        if (receivedData.trim().equalsIgnoreCase("close server")) {
                            System.out.println("Client " + clientID + " requested server shutdown.");
                            System.out.println("Server closing...\n");
                            serverConnected = false;
                            clientConnected = false;
                        }

                        // WIP : handle when no message received (if it can happen), with a different echo message ?
                        // Send an echo back to the client
                        output.write(echo_buf);
                        output.flush();
                    }
                }catch(java.net.SocketTimeoutException e){
                    // Periodic reminder about the remaining timeout duration
                    long countdownSec = (lastActivityTime + timeout - System.currentTimeMillis()) / millisToSec;
                    // Avoid negative countdown values
                    if(countdownSec < 0){countdownSec = 0;}
                    System.out.printf("Connection timeout in : %d sec\n",countdownSec);
                }
            }
        }
        // Update the server state and print closure message
        this.serverState = "Closed";
        System.out.println("Server closed\n");
    }

    /**
     * Provides a string representation of the server's current state.
     *
     * @return a string describing the server's state and listening port.
     */
    @Override
    public String toString() {
        return "TCP server status on port " + this.listeningPort + ": " + this.serverState;
    }


    /**
     * The main method to start the TCP server.
     *
     * <p>Usage: `java TCPServer <listening port>`</p>
     *
     * @param args command-line arguments containing the listening port number.
     * @throws IOException if an error occurs while starting or running the server.
     */
    public static void main(String[] args) throws IOException{
        // Parses command-line args
        if (args.length < 1){
            System.err.println("Usage: java TCPServer <listening port>");
            System.exit(systemShutdown);
        }

        // Get port number from args and convert it in integer
        int port = Integer.parseInt(args[0]);

        // Instance of TCP server
        TCPServer servTCP = new TCPServer(port);
        servTCP.launch();
        System.exit(systemShutdown);
    }

}
