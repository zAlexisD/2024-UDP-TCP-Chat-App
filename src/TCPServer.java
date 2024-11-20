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
    private final int maxBufSize=1024;

    public TCPServer(int listeningPort) {
        this.listeningPort = listeningPort;
        this.serverState = "Closed";
    }

    public TCPServer() {
        int defaultPort = 0;
        this.listeningPort = defaultPort;
        this.serverState = "Closed";
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public void launch() throws IOException {
        Socket clientSocket = null;

        try {
            ServerSocket serverSocket = new ServerSocket(this.listeningPort);
            this.serverState = "Running";
            System.out.println("Server is running and listening on port " + this.listeningPort);

            while(true){
                clientSocket = serverSocket.accept();
                System.out.println("Connection from client :" + clientSocket.getInetAddress());

                InputStream input = clientSocket.getInputStream();                // InputStream to read the server response
                OutputStream output = clientSocket.getOutputStream();

                byte[] buf = new byte[maxBufSize];

                input.read(buf);
                String receivedData = new String(buf, StandardCharsets.UTF_8);
                System.out.println("client say :" + receivedData);

                String echo = "message received";
                byte[] echo_buf = echo.getBytes(StandardCharsets.UTF_8);
                output.write(echo_buf);
                output.flush();

                clientSocket.close();

                // Add condition to close the UDP server
                if (receivedData.trim().equalsIgnoreCase("close server")){
                    System.out.println("Server closing...\n");
                    break;
                }
                
            }
        } finally {
            this.serverState = "Closed";
            System.out.println("Server closed\n");
            if(clientSocket != null && !clientSocket.isClosed()){
                clientSocket.close();
            }
        }
        
        
    }

    @Override
    public String toString() {
        return "UDP server status on port " + this.listeningPort + ": " + this.serverState;
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
