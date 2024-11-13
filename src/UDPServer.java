import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPServer {
    private int listeningPort;
    private String serverState;
    private final int defaultPort = 0;
    private final int maxBufSize = 1024;

    // Constructor that sets a specific listening port
    public UDPServer(int listeningPort) {
        this.listeningPort = listeningPort;
        this.serverState = "Closed";
    }

    // Default constructor, initializes server with default port
    public UDPServer() {
        this.listeningPort = defaultPort;
        this.serverState = "Closed";
    }

    public int getListeningPort() {
        return listeningPort;
    }

    // Starts the server, listens for incoming datagrams, and processes received data
    public void launch() throws IOException {
        DatagramSocket socket = null;

        try {
            this.serverState = "Running";                           // Update server state
            socket = new DatagramSocket(this.listeningPort);        // Bind socket to specified port
            System.out.println("UDPServer is running and listening on port " + this.listeningPort);

            // Buffer for incoming data
            byte[] buf = new byte[maxBufSize];

            // Loop to continually receive datagrams
            while(true){
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                // Wait for a datagram
                socket.receive(packet);

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                // Decode received data to UTF-8 string
                String receivedData = new String(packet.getData(),0, packet.getLength(), StandardCharsets.UTF_8);

                // Display client address, port, and message content
                System.out.println("User in " + clientAddress + " says on port " + clientPort + ": " + receivedData + "\n");

                // Add condition to tell when the user disconnects
                if(receivedData.trim().equalsIgnoreCase(("exit console"))){
                    System.out.println("User at "+ clientAddress + " left the chat");
                }
                // Add condition to close the UDP server
                if (receivedData.trim().equalsIgnoreCase("close server")){
                    System.out.println("Server closing...\n");
                    break;
                }
            }
        } finally {
            this.serverState = "Closed";                // Update server state
            System.out.println("Server closed\n");
            if(socket != null && !socket.isClosed()){
                socket.close();                         // Release socket resources when finished
            }
        }
    }

    // Returns a string describing the current server status
    @Override
    public String toString() {
        return "UDP server status on port " + this.listeningPort + ": " + this.serverState;
    }

    // Main method
    public static void main(String[] args) throws IOException {
        // Parses command-line args
        if (args.length < 1){
            System.err.println("Usage: java UDPServer <listening port>");
            System.exit(1);
        }

        // Get port number from args and convert it in integer
        int port = Integer.parseInt(args[0]);

        // Create and launch a server instance
        UDPServer servUDP = new UDPServer(port);
        servUDP.launch();
        System.out.println(servUDP);
    }

}
