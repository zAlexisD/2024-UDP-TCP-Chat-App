import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static java.lang.Thread.sleep;


public class TCPServer {
    private int listeningPort;
    private String serverState;
    private final int defaultPort = 0;
    private final int maxBufSize = 1024;

    // Set time variables
    private final int timeout = 60000;  // Set connection time out at 1 min (60 000 ms)
    private final long startTime = System.currentTimeMillis();
    private final int interval = 1000;  // Set check interval at 1s (1000 ms)

    public TCPServer(int listeningPort) {
        this.listeningPort = listeningPort;
        this.serverState = "Closed";
    }

    public TCPServer() {
        this.listeningPort = defaultPort;
        this.serverState = "Closed";
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public void launch() throws IOException, InterruptedException {

        try(ServerSocket serverSocket = new ServerSocket(this.listeningPort)) {
            this.serverState = "Running";
            System.out.println("Server is running and listening on port " + this.listeningPort);

            // Set echo message
            String echo = "Message received";
            byte[] echo_buf = echo.getBytes(StandardCharsets.UTF_8);

            // Waiting for Client connection
            System.out.println("Waiting for connection...\n");
            while(true){
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

                        // Get the Client's message
                        byte[] buf = new byte[maxBufSize];
                        int byteRead = input.read(buf);
                        String receivedData = new String(buf, StandardCharsets.UTF_8);
                        System.out.println("Client " + clientSocket.getInetAddress() + " says : " + receivedData);

                        // Add condition to tell when the user disconnects
                        if (receivedData.trim().equalsIgnoreCase(("exit console"))) {
                            System.out.println("User at " + clientSocket.getInetAddress() + " left the chat\n");
                            System.out.println("Waiting for new connection...\n");
                            break;
                        }

                        // Add condition to close the TCP server
                        if (receivedData.trim().equalsIgnoreCase("close server")) {
                            System.out.println("Server closing...\n");
                            break;
                        }

                        // Send an echo to the client
                        output.write(echo_buf);
                        output.flush();
                    }
                }

                // Wait for a short interval before checking again
                sleep(interval);
            }
        }
        // Server closure
        this.serverState = "Closed";
        System.out.println("Server closed\n");
    }

    @Override
    public String toString() {
        return "TCP server status on port " + this.listeningPort + ": " + this.serverState;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
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
