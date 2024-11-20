import java.io.Console;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPClient {
    private String serverHost;
    private int serverPort;

    public TCPClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void send() throws Exception {
        Socket socket = new Socket(this.serverHost,this.serverPort);
        Console console = System.console(); // get a console

        // Manage console error
        if (console == null) {
            System.err.println("No console available.");
            System.exit(1);
        }

        while(true){
            String userInput = console.readLine("Enter a message or '?' for help : "); // retrieve the user's message
            byte[] data = userInput.getBytes(StandardCharsets.UTF_8);   // encodes strings in UTF-S8

            // Send data to the server
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(data);
            outputStream.flush();

            // Add condition to display help command
            if (userInput.trim().equalsIgnoreCase("?")){
                System.out.println("Press CTRL+D or type 'exit console' to quit console\n" );
                System.out.println("type 'close server' to disconnect the server\n");
            }

            // Add conditions to close the console
            // console.readLine() returns null if end-of-stream reached <=> CTRL+D
            if (userInput == null || userInput.trim().equalsIgnoreCase("exit console")){
                System.out.println("Closing console...\n");
                break;
            }
        }
        socket.close();
    }

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
