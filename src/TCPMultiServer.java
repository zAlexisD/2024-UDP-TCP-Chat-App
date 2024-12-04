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

    // Set time variables
    private final int millisToSec = 1000;
    private final int timeout = 60000;  // Set connection time out at 1 min (60000 ms)
    private final int interval = 10000;  // Set remind interval at 10s (10000 ms)
    private final long startTime = System.currentTimeMillis();

    public TCPMultiServer(int listeningPort) {
        this.listeningPort = listeningPort;
        this.serverState = "Closed";
    }

    public TCPMultiServer() {
        this.listeningPort = defaultPort;
        this.serverState = "Closed";
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public void launch() throws IOException {

        try(ServerSocket serverSocket = new ServerSocket(this.listeningPort)) {
            this.serverState = "Running";
            System.out.println("Server is running and listening on port " + this.listeningPort);

            // Waiting for Client connection
            System.out.println("Waiting for connection...\n");
            // Set reminder for connection time left by unblocking .accept()
            serverSocket.setSoTimeout(interval);

            while(true){
                // WIP : Manage reset timeout when a client disconnects
                // WIP : if a first client connects, stop the countdown
                // Close server if time out connection reached
                if (System.currentTimeMillis() - startTime > timeout){
                    System.out.println("Timeout reached. No connection received");
                    break;
                }

                // Check for incoming connections
                try {
                    // Accept a client's connection
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connection from client : " + clientSocket.getInetAddress());

                    // New "session" for the client
                    InputStream clientInput = clientSocket.getInputStream();
                    OutputStream clientOutput = clientSocket.getOutputStream();
                    ConnectionThread client = new ConnectionThread(clientSocket,clientInput,clientOutput);
                    client.start();

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

    @Override
    public String toString() {
        return "TCP multi server status on port " + this.listeningPort + ": " + this.serverState;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1){
            System.err.println("Usage: java TCPMultiServer <listening port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        // Instance of TCP multiserver
        TCPMultiServer servTCP = new TCPMultiServer(port);
        servTCP.launch();
        System.exit(1);     // quit the Java VM
    }
}
