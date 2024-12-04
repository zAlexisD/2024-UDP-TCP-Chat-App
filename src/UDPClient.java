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
 *     <li>`exit console` - Closes the client console.</li>
 *     <li>`CTRL + D` - Ends input and exits the program.</li>
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

    /**
     * Constructor for the UDPClient class.
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
     *     <li>`exit console` - Closes the client console.</li>
     *     <li>`CTRL + D` - Ends input and exits the program.</li>
     * </ul>
     *
     * @throws Exception if a network error occurs or if the console is unavailable.
     */
    public void send() throws Exception {
        DatagramSocket datagramSocket = new DatagramSocket(); //open a socket
        InetAddress serverInetAddress = InetAddress.getByName(this.serverHost); // get the ip address of a server knowing his name
        Console console = System.console(); // get a console

        if (console == null) { 
            System.err.println("No console available.");
            System.exit(1);
        }

        while (true) {
            String userInput = console.readLine("Enter a message or '?' for help : "); // retrieve the user's message

            // CTRL+D corresponds to en end-of-input (EOF), console.readLine() returns null
            if (userInput==null){
                System.out.println("\nClosing console...\n");
                break;
            }

            // encodes strings in UTF-8
            byte[] data = userInput.getBytes(StandardCharsets.UTF_8);

            // Send datagram to the server
            DatagramPacket packet = new DatagramPacket(data, data.length, serverInetAddress, serverPort);
            datagramSocket.send(packet);

            // Add condition to display help command
            if (userInput.trim().equalsIgnoreCase("?")){
                System.out.println("CTRL + D or type 'exit console' to quit console\n" );
                System.out.println("'close server' to disconnect the server\n");
            }
            // Add another condition to close the console
            if (userInput.trim().equalsIgnoreCase("exit console")){
                System.out.println("Closing console...\n");
                break;
            }
        }
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
        if (args.length < 2) { // check the arguments
            System.err.println("Usage: java UDPClient <address> <port>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        UDPClient clientUDP = new UDPClient(host, port);
        clientUDP.send();
    }
}




