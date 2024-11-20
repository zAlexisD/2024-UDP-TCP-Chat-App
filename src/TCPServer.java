import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.net.InetAddress;


public class TCPServer {
    private int listeningPort;
    private String serverState;
    private final int defaultPort = 0;
    private final int maxBufSize = 1024;

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

    public void launch() throws IOException {
        Socket clientSocket = null;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(this.listeningPort);
            this.serverState = "Running";
            System.out.println("Server is running and listening on port " + this.listeningPort);

            // Buffers for incoming data and echo
            byte[] buf = new byte[maxBufSize];
            String echo = "Message received";
            byte[] echo_buf = echo.getBytes(StandardCharsets.UTF_8);

            while(true){
                clientSocket = serverSocket.accept();
                System.out.println("Connection from client : " + clientSocket.getInetAddress());

                // Get the Client's message
                InputStream input = clientSocket.getInputStream();
                int byteRead = input.read(buf);
                String receivedData = new String(buf, StandardCharsets.UTF_8);
                System.out.println("client say : " + receivedData);

                // Send an echo to the client
                OutputStream output = clientSocket.getOutputStream();
                output.write(echo_buf);
                output.flush();

                // Add condition to tell when the user disconnects
                if(receivedData.trim().equalsIgnoreCase(("exit console"))){
                    System.out.println("User at "+ clientSocket.getInetAddress() + " left the chat");
                    clientSocket.close();
                }

                // Add condition to close the TCP server
                if (receivedData.trim().equalsIgnoreCase("close server")){
                    System.out.println("Server closing...\n");
                    break;
                }

            }
        } finally {
            this.serverState = "Closed";
            System.out.println("Server closed\n");
            if(serverSocket != null && !serverSocket.isClosed()){
                serverSocket.close();
            }
        }
        
        
    }

    @Override
    public String toString() {
        return "TCP server status on port " + this.listeningPort + ": " + this.serverState;
    }


    public static void main(String[] args) throws IOException {
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
