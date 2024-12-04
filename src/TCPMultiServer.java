/**
 * The {@code TCPMultiServer} class implements a multithreaded TCP server capable of handling
 * multiple client connections simultaneously. Each client connection is managed in its own thread.
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPMultiServer {
    private int listeningPort;
    private String serverState;
    private final int defaultPort = 0;

    // Track the number of active connection on the server
    private int activeConnections = 0;
    private final int noActiveConnections = 0;

    // Set time variables
    private final int millisToSec = 1000;
    private final int timeout = 60000;  // Set connection time out at 1 min (60000 ms)
    private final int interval = 10000;  // Set remind interval at 10s (10000 ms)
    private long lastActivityTime = System.currentTimeMillis();   // Set the last activity time as start time

    /**
     * Constructs a {@code TCPMultiServer} instance with a specified listening port.
     *
     * @param listeningPort the port on which the server will listen for incoming connections.
     */
    public TCPMultiServer(int listeningPort) {
        this.listeningPort = listeningPort;
        this.serverState = "Closed";
    }

    /**
     * Constructs a {@code TCPMultiServer} instance with the default listening port.
     */
    public TCPMultiServer() {
        this.listeningPort = defaultPort;
        this.serverState = "Closed";
    }

    /**
     * Returns the listening port of the server.
     *
     * @return the listening port number.
     */
    public int getListeningPort() {
        return listeningPort;
    }

    /**
     * Launches the server, starts listening for client connections, and handles each connection in a separate thread.
     *
     * @throws IOException if an I/O error occurs while setting up the server or handling client connections.
     */
    public void launch() throws IOException {

        try(ServerSocket serverSocket = new ServerSocket(this.listeningPort)) {
            this.serverState = "Running";
            System.out.println("Server is running and listening on port " + this.listeningPort);

            // Waiting for Client connection
            System.out.println("Waiting for connection...\n");
            // Set reminder for connection time left by unblocking .accept()
            serverSocket.setSoTimeout(interval);

            while(true){
                // Close server if time out connection reached and no active connection
                if (activeConnections == noActiveConnections && System.currentTimeMillis() - lastActivityTime > timeout){
                    System.out.println("Timeout reached. No connection received");
                    break;
                }

                // Check for incoming connections
                try {
                    // Accept a client's connection
                    Socket clientSocket = serverSocket.accept();
                    String clientID = clientSocket.getInetAddress()+":"+clientSocket.getPort();
                    System.out.println("\nConnection from client : " + clientID +"\n");

                    // Increment active connections
                    activeConnections++;

                    // New session for the client
                    InputStream clientInput = clientSocket.getInputStream();
                    OutputStream clientOutput = clientSocket.getOutputStream();

                    // Pass a callback to decrement `activeConnections` when the thread ends
                    ConnectionThread client = new ConnectionThread(clientSocket, clientInput, clientOutput, () -> {
                        activeConnections--;
                        System.out.println("Client "+clientID+" disconnected. Active connections: " + activeConnections);
                        // Reset the last activity time to reset countdown
                        lastActivityTime = System.currentTimeMillis();
                    });
                    client.start();

                }catch(java.net.SocketTimeoutException e) {
                    // Remind user of the connection time left if no clients are connected
                    if (activeConnections == noActiveConnections) {
                        long countdownSec = (lastActivityTime + timeout - System.currentTimeMillis()) / millisToSec;
                        // Avoid "Connection timeout in : -4 sec"
                        if (countdownSec < 0){ countdownSec = 0;}
                        System.out.printf("Connection timeout in: %d sec\n", countdownSec);
                    }
                }
            }
        }
        // Server closure
        this.serverState = "Closed";
        System.out.println("Server closed\n");
    }

    /**
     * Provides a string representation of the server's current state and port.
     *
     * @return a string describing the server's status.
     */
    @Override
    public String toString() {
        return "TCP multi server status on port " + this.listeningPort + ": " + this.serverState;
    }

    /**
     * The main method to start the TCP multi-server.
     * Accepts a command-line argument for the listening port.
     *
     * @param args the command-line arguments (expected: one argument specifying the listening port).
     * @throws IOException if an error occurs while starting or running the server.
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 1){
            System.err.println("Usage: java TCPMultiServer <listening port>");
            System.exit(1); // shutdown
        }

        int port = Integer.parseInt(args[0]);

        // Instance of TCP multiserver
        TCPMultiServer servTCP = new TCPMultiServer(port);
        servTCP.launch();
        System.exit(1);     // shutdown
    }
}
