/**
 * Represents a thread handling a single client connection to the server.
 *
 * <p>This thread listens for incoming messages from the client, processes them,
 * and sends responses (echo messages). It also handles client disconnection and specific commands
 * for server management.</p>
 *
 * <p>Features:</p>
 * <ul>
 *     <li>Echoes received messages back to the client.</li>
 *     <li>Manages client disconnections gracefully.</li>
 *     <li>Processes specific commands such as "exit console" and "close server".</li>
 * </ul>
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectionThread extends Thread {
    private final TCPMultiServer tcpMultiServer;
    private final Socket clientSocket;
    private final InputStream clientInput;
    private final OutputStream clientOutput;
    private final Runnable disconnectCallback;

    // Set variables for buffer size and client connection status
    private final int maxBufSize = 1024;
    protected boolean clientConnected = true;

    // Set echo message and convert to byte array for transmission
    protected final String echo = "Message received\n";
    protected final byte[] echo_buf = echo.getBytes(StandardCharsets.UTF_8);

    public ConnectionThread(TCPMultiServer tcpMultiServer, Socket clientSocket, InputStream clientInput, OutputStream clientOutput, Runnable disconnectCallback) {
        /**
         * Creates a new connection thread for a client.
         *
         * @param tcpMultiServer the TCPMultiServer instance managing this connection.
         * @param clientSocket the socket connected to the client.
         * @param clientInput the input stream from the client.
         * @param clientOutput the output stream to the client.
         * @param disconnectCallback callback to notify server when the client disconnects.
         */
        this.tcpMultiServer = tcpMultiServer;
        this.clientSocket = clientSocket;
        this.clientInput = clientInput;
        this.clientOutput = clientOutput;
        this.disconnectCallback = disconnectCallback;
    }

    /**
     * Retrieves the {@link TCPMultiServer} instance associated with this connection thread.
     *
     * This method provides access to the {@code TCPMultiServer} that manages the overall server state.
     * It is useful for testing purposes or for accessing server-related information within the context of
     * the connection thread.
     *
     * @return the {@link TCPMultiServer} instance associated with this thread.
     */
    // Getter used in the test class
    public TCPMultiServer getTcpMultiServer() {
        return this.tcpMultiServer;
    }

    /**
     * Executes the communication with the client in a separate thread.
     *
     * <p>The thread listens for messages from the client, processes them,
     * and sends back responses. It handles the following commands:</p>
     * <ul>
     *     <li>`exit console` - Terminates the connection with the client.</li>
     *     <li>`close server` - Signals a server shutdown request.</li>
     * </ul>
     *
     * <p>The thread terminates when the client disconnects or sends specific commands.</p>
     */
    @Override
    public void run() {
        try {
            while (clientConnected) {
                // WIP : manage ? and not displaying when client use a command
                // Define Client ID
                String clientID = clientSocket.getInetAddress()+ ":" +clientSocket.getPort();

                // Get the Client's message
                byte[] buf = new byte[maxBufSize];
                int bytesRead = clientInput.read(buf);
                String receivedData = new String(buf, StandardCharsets.UTF_8);
                System.out.println("Client " + clientID + " says : " + receivedData);

                // Handle connection loss (client disconnects unexpectedly)
                if (bytesRead == -1) {
                    System.out.println("Client " + clientID + " disappeared\n");
                    break;
                }
                // Handle user's disconnection
                if (receivedData.trim().equalsIgnoreCase("exit console")) {
                    System.out.println("Client " + clientID + " left the chat.\n");
                    clientConnected = false;
                }
                // Handle TCP multiserver closure commanded by client
                if (receivedData.trim().equalsIgnoreCase("close server")) {
                    System.out.println("Client " + clientID + " requested server shutdown.");
                    System.out.println("Server closing...\n");
                    tcpMultiServer.serverConnected = false;
                    clientConnected = false;
                }

                // Send an echo message back to the client
                clientOutput.write(echo_buf);
                clientOutput.flush();
                // WIP: handle different echo message if no messages received (relevant feature ?)
            }
        } catch (IOException e){
            System.err.println("Error in the client connection: "+e.getMessage());
        } finally {
            // Notify the server that the connection is closed
            if (disconnectCallback != null) {
                disconnectCallback.run();
            }
            // Resources closure
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
