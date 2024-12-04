/**
 * A TCP client that connects to a server, sends messages, and receives echoes.
 * The client supports interactive console input and handles specific commands to control the connection.
 *
 * <p>Usage: `java TCPClient <address> <port>`</p>
 *
 * <p>Supported commands:</p>
 * <ul>
 *     <li>`?` - Displays help information.</li>
 *     <li>`exit console` - Closes the client console and disconnects from the server.</li>
 *     <li>`close server` - Requests the server to shut down.</li>
 * </ul>
 */
import java.io.Console;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPClient {
    private String serverHost;
    private int serverPort;
    private final int maxBufSize = 1024;
    private final String exitConsole = "exit console";

    /**
     * Creates a TCPClient instance with the specified server host and port.
     *
     * @param serverHost the server's host address.
     * @param serverPort the server's port number.
     */
    public TCPClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    /**
     * Connects to the server, sends user input, and receives echoes from the server.
     *
     * <p>Special commands:</p>
     * <ul>
     *     <li>`?` - Displays help information.</li>
     *     <li>`exit console` - Closes the client console and disconnects from the server.</li>
     *     <li>`close server` - Requests the server to shut down.</li>
     * </ul>
     *
     * @throws Exception if there is an error in the socket connection or communication.
     */
    public void send() throws Exception {
        Socket socket = new Socket(this.serverHost,this.serverPort);
        Console console = System.console(); // get a console

        // Manage console error
        if (console == null) {
            System.err.println("No console available.");
            System.exit(1);
        }

        // Buffer for echo
        byte[] buf = new byte[maxBufSize];

        while(true){
            // retrieve the user's message
            String userInput = console.readLine("Enter a message or '?' for help : ");

            // CTRL+D corresponds to en end-of-input (EOF), console.readLine() returns null
            if (userInput==null){
                // Tell to server to do the same as with "exit console"
                byte[] exitData = exitConsole.getBytes(StandardCharsets.UTF_8);
                OutputStream exitOutput = socket.getOutputStream();
                exitOutput.write(exitData);
                exitOutput.flush();

                System.out.println("\nClosing console...\n");
                break;
            }

            // encodes strings in UTF-8
            byte[] data = userInput.getBytes(StandardCharsets.UTF_8);

            // send data to the server
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(data);
            outputStream.flush();

            // receive echo from the server
            InputStream inputStream = socket.getInputStream();
            int byteRead = inputStream.read(buf);
            String receivedEcho = new String(buf, StandardCharsets.UTF_8);
            System.out.println("server echo : " + receivedEcho);
            // WIP : handle when no response

            // Manage help panel display
            if (userInput.trim().equalsIgnoreCase("?")){
                System.out.println(">> Press CTRL+D or type 'exit console' to quit console\n" );
                System.out.println(">> type 'close server' to disconnect the server\n");
            }

            // Manage console and server closure
            if (userInput.trim().equalsIgnoreCase(exitConsole) || userInput.trim().equalsIgnoreCase("close server")){
                System.out.println("Closing console...\n");
                break;
            }
        }
        socket.close();
        System.out.println("Console closed\n");
    }

    /**
     * Main method to start the TCP client.
     *
     * <p>Usage: `java TCPClient <address> <port>`</p>
     *
     * @param args command-line arguments containing the server address and port number.
     * @throws Exception if there is an error initializing or running the client.
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 2) { // check the arguments
            System.err.println("Usage: java TCPClient <address> <port>");
            System.exit(1);
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        TCPClient clientTCP = new TCPClient(host,port);
        clientTCP.send();
    }
}
