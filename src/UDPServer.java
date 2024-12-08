/**
 * A simple UDP server that listens for incoming datagrams and processes them.
 * The server allows clients to send messages and supports special commands for closing interacting with the server.
 *
 * <p>Usage: `java UDPServer <listening port>`</p>
 *
 * <p>Supported commands from clients:</p>
 * <ul>
 *     <li>`?` - Displays a help message in the server logs.</li>
 *     <li>`exit console` - Logs that a client has left the chat.</li>
 *     <li>`close server` - Shuts down the server.</li>
 * </ul>
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPServer {
    private int listeningPort;
    private String serverState;

    // Default values and constants
    private final int defaultPort = 0;
    private final int maxBufSize = 1024;
    private final int DataOffset = 0;   // Offset for processing incoming data
    private static final int systemShutdown = 1;
    private boolean serverConnected = true;

    /**
     * Constructs a UDP server with a specific listening port.
     *
     * @param listeningPort the port number on which the server will listen.
     */
    public UDPServer(int listeningPort) {
        this.listeningPort = listeningPort;
        this.serverState = "Closed";
    }

    /**
     * Constructs a UDP server with a default listening port (0).
     */
    public UDPServer() {
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
     * Starts the UDP server, listens for incoming datagrams, and processes them.
     *
     * <p>Supported client commands:</p>
     * <ul>
     *     <li>`?` - Displays a help message in the server logs.</li>
     *     <li>`exit console` - Logs that a client has left the chat.</li>
     *     <li>`close server` - Shuts down the server.</li>
     * </ul>
     *
     * @throws IOException if there is a network or socket error.
     */
    public void launch() throws IOException {
        // Try-with-resources to ensure the socket is closed properly
        try (DatagramSocket datagramSocket = new DatagramSocket(this.listeningPort)){
            this.serverState = "Running";
            System.out.println("UDPServer is running and listening on port " + this.getListeningPort());

            // Buffer for incoming data
            byte[] buf = new byte[maxBufSize];

            // Main loop to handle incoming datagrams
            while(serverConnected){
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                // Wait for a datagram to be received
                datagramSocket.receive(packet);

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                String clientID = clientAddress + ":" + clientPort;

                // Decode received data to UTF-8 string
                String receivedData = new String(packet.getData(), DataOffset, packet.getLength(), StandardCharsets.UTF_8);

                // Display client address, port, and message content
                System.out.println("User in " + clientAddress + " says on port " + clientPort + ": " + receivedData + "\n");

                // Handle client's help request
                if(receivedData.trim().equalsIgnoreCase(("?"))){
                    System.out.println("User at "+ clientID + " is looking at the help notice");
                }
                // Handle Console closure
                if(receivedData.trim().equalsIgnoreCase(("exit console"))){
                    System.out.println("Bye Bye user "+ clientID + " ヾ(・ω・*)");
                }
                // Handle UDP server closure
                if (receivedData.trim().equalsIgnoreCase("close server")){
                    System.out.println("User at " + clientID + " requested server shutdown.");
                    System.out.println("Server closing...\n");
                    serverConnected = false;
                }
            }
        }
        // Update server state upon closure
        this.serverState = "Closed";
        System.out.println("Server closed\n");
    }

    /**
     * Provides a string representation of the server's current state and listening port.
     *
     * @return a string describing the server's status.
     */
    @Override
    public String toString() {
        return "UDP server status on port " + this.listeningPort + ": " + this.serverState;
    }

    /**
     * Main method to start the UDP server.
     *
     * <p>Usage: `java UDPServer <listening port>`</p>
     *
     * @param args command-line arguments containing the listening port.
     * @throws IOException if there is an error launching the server.
     */
    public static void main(String[] args) throws IOException {
        // Parses command-line args
        if (args.length < 1){
            System.err.println("Usage: java UDPServer <listening port>");
            System.exit(systemShutdown);
        }

        // Get port number from args and convert it in integer
        int port = Integer.parseInt(args[0]);

        // Instance of UDP server
        UDPServer servUDP = new UDPServer(port);
        servUDP.launch();
        System.exit(systemShutdown);
    }
}
