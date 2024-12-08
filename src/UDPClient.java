/**
 * A simple UDP client that sends messages to a UDP server.
 * The client uses a console interface for user input and sends
 * messages to the server via UDP.
 *
 * <p>Usage: `java UDPClient <address> <port>`</p>
 *
 * <p>Supported commands:</p>
 * <ul>
 *     <li>`?` - Displays help information.</li>
 *     <li>`CTRL + D` or `exit console` - Closes the client console.</li>
 *     <li>`close server` - Ends input and exits the program.</li>
 * </ul>
 */
import java.io.Console;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPClient {
    private String serverHost;
    private int serverPort;

    // Constants
    private static final int systemShutdown = 1;
    private boolean clientConnected = true;

    /**
     * Constructs a UDP client to connect to a specified server.
     *
     * @param host the hostname or IP address of the server.
     * @param port the port number of the server.
     */
    public UDPClient(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }

    /**
     * Starts the client, reads user input from the console,
     * and sends it to the UDP server.
     *
     * <p>Special commands:</p>
     * <ul>
     *     <li>`?` - Displays help information.</li>
     *     <li>`CTRL + D` or `exit console` - Closes the client console.</li>
     *     <li>`close server` - Ends input and exits the program.</li>
     * </ul>
     *
     * @throws Exception if a network error occurs or if the console is unavailable.
     */
    public void send() throws Exception {
        // Open a socket for sending datagrams
        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress serverInetAddress = InetAddress.getByName(this.serverHost);

        // Get a console
        Console console = System.console();

        // Check if console is available
        if (console == null) { 
            System.err.println("No console available.");
            System.exit(systemShutdown);
        }

        // Client's session loop
        while (clientConnected) {
            // Prompt user for input
            String userInput = console.readLine("Enter a message or '?' for help : ");

            // CTRL+D corresponds to en end-of-input (EOF), console.readLine() returns null
            if (userInput==null){
                // Treat CTRL+D as "exit console"
                userInput = "exit console";
            }

            // Encodes strings in UTF-8
            byte[] data = userInput.getBytes(StandardCharsets.UTF_8);

            // Send datagram to the server
            DatagramPacket packet = new DatagramPacket(data, data.length, serverInetAddress, serverPort);
            datagramSocket.send(packet);

            // Handle help command display
            if (userInput.trim().equalsIgnoreCase("?")){
                System.out.println("\n>> CTRL + D or type 'exit console' to quit console\n" );
                System.out.println(">> 'close server' to disconnect the server\n");
            }
            // Handle console and server closure
            if (userInput.trim().equalsIgnoreCase("exit Console") || userInput.trim().equalsIgnoreCase("close server")){
                System.out.println("Closing console...\n");
                clientConnected = false;
            }
        }
        // Close the socket and print closing message
        datagramSocket.close();
        System.out.println("Console closed\n");
    }

    /**
     * The main method to start the UDP client.
     *
     * <p>Usage: `java UDPClient <address> <port>`</p>
     *
     * @param args command-line arguments: the server address and port number.
     * @throws Exception if there is an error starting the client.
     */
    public static void main(String[] args) throws Exception {
        // Parses command-line args
        if (args.length < 2) {
            System.err.println("Usage: java UDPClient <address> <port>");
            System.exit(systemShutdown);
        }

        // Get host and port number from args + convert port in integer
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        // Instance of UDP Client
        UDPClient clientUDP = new UDPClient(host, port);
        clientUDP.send();
    }
}




