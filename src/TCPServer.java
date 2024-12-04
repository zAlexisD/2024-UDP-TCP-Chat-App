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
    private final int defaultPort = 0;
    private final int maxBufSize = 1024;

    // Set time variables
    private final int millisToSec = 1000;
    private final int timeout = 60000;  // Set connection time out at 1 min (60000 ms)
    private final int interval = 10000;  // Set remind interval at 10s (10000 ms)
    private final long startTime = System.currentTimeMillis();

    /**
     * Creates a TCPServer instance with a specified listening port.
     *
     * @param listeningPort the port number on which the server will listen for incoming connections.
     */
    public TCPServer(int listeningPort) {
        this.listeningPort = listeningPort;
        this.serverState = "Closed";
    }


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
            System.out.println("Server is running and listening on port " + this.listeningPort);

            // Set echo message
            String echo = "Message received\n";
            byte[] echo_buf = echo.getBytes(StandardCharsets.UTF_8);

            // Waiting for Client connection
            System.out.println("Waiting for connection...\n");
            // Set reminder for connection time left by unblocking .accept()
            serverSocket.setSoTimeout(interval);

            while(true){
                // WIP : Manage reset timeout when a client disconnects
                // Close server if time out connection reached
                if (System.currentTimeMillis() - startTime > timeout){
                    System.out.println("Timeout reached. No connection received");
                    break;
                }

                // Check for incoming connections
                try (Socket clientSocket = serverSocket.accept()) {
                    // Once the connection is done
                    System.out.println("Connection from client : " + clientSocket.getInetAddress());
                    while (true) {
                        InputStream input = clientSocket.getInputStream();
                        OutputStream output = clientSocket.getOutputStream();

                        // WIP : manage ? and not displaying when client use a command
                        // Get the Client's message
                        byte[] buf = new byte[maxBufSize];
                        int byteRead = input.read(buf);
                        String receivedData = new String(buf, StandardCharsets.UTF_8);
                        System.out.println("Client " + clientSocket.getInetAddress() + " says : " + receivedData);

                        // WIP : manage when client connection is lost
                        // Manage user's disconnection
                        if (receivedData.trim().equalsIgnoreCase(("exit console"))) {
                            System.out.println("User at " + clientSocket.getInetAddress() + " left the chat\n");
                            System.out.println("Waiting for new connection...\n");
                            break;
                        }

                        // Manage TCP server closure commanded by client
                        if (receivedData.trim().equalsIgnoreCase("close server")) {
                            System.out.println("Server closing...\n");
                            break;
                        }

                        // Send an echo to the client
                        output.write(echo_buf);
                        output.flush();
                    }
                }catch(java.net.SocketTimeoutException e){
                    // Remind user of the connection time left
                    long countdownSec = (startTime + timeout - System.currentTimeMillis()) / millisToSec;
                    System.out.printf("Connection timeout in : %d sec\n",countdownSec);
                }
            }
        }
        // Server closure
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
        if (args.length < 1){
            System.err.println("Usage: java TCPServer <listening port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        TCPServer servTCP = new TCPServer(port);
        servTCP.launch();
        System.out.println(servTCP);
    }

}
