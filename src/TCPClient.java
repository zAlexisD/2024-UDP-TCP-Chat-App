import java.io.Console;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPClient {
    private String serverHost;
    private int serverPort;
    private final int maxBufSize = 1024;

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

        // Buffer for echo
        byte[] buf = new byte[maxBufSize];

        while(true){
            // retrieve the user's message
            String userInput = console.readLine("Enter a message or '?' for help : ");

            // CTRL+D corresponds to en end-of-input (EOF), console.readLine() returns null
            if (userInput==null){
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
            System.out.println("\nserver echo : " + receivedEcho);

            // Add condition to display help command
            if (userInput.trim().equalsIgnoreCase("?")){
                System.out.println("Press CTRL+D or type 'exit console' to quit console\n" );
                System.out.println("type 'close server' to disconnect the server\n");
            }

            // Add another condition to close the console
            if (userInput.trim().equalsIgnoreCase("exit console")){
                System.out.println("Closing console...\n");
                break;
            }
        }
        socket.close();
        System.out.println("Console closed\n");
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